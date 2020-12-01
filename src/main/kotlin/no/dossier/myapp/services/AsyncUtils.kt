package no.dossier.myapp.services

import org.slf4j.MDC
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.Future

internal class DelegatingMdcContextExecutor(private val delegate: Executor) :
    Executor {
  override fun execute(command: Runnable) {
    val mdc = MDC.getCopyOfContextMap()
    delegate.execute {
      val oldMdc = MDC.getCopyOfContextMap()
      try {
        if (mdc != null) {
          MDC.setContextMap(mdc)
        } else {
          MDC.clear()
        }
        command.run()
      } finally {
        if (oldMdc != null) {
          MDC.setContextMap(oldMdc)
        } else {
          MDC.clear()
        }
      }
    }
  }
}

internal fun Executor.preservingContext(): Executor = DelegatingMdcContextExecutor(this)
//    DelegatingMdcContextExecutor(DelegatingSecurityContextExecutor(this))

val contextPreservingExecutor: Executor = Executors.newCachedThreadPool().preservingContext()

fun createContextPreservingExecutorWithMaxThreads(maxThreads: Int): Executor =
    Executors.newFixedThreadPool(maxThreads).preservingContext()

/**
 * Async with limited concurrency.
 *
 * ```
 * val asyncContext = AsyncContext(threadCount = 20)
 * // The asyncContext should usually be stored and reused, e.g. in a bean or a private val
 *
 * return asyncContext {
 *     val a = async { fetch('http://foo.bar/a') }
 *     val b = async { fetch('http://foo.bar/b') }
 *
 *     Pair(a.get(), b.get())
 * }
 * ```
 */
class AsyncContext(
    val executor: Executor = contextPreservingExecutor
) {
  constructor(threadCount: Int) : this(executor = createContextPreservingExecutorWithMaxThreads(threadCount))

  inline operator fun <T> invoke(block: AsyncContext.() -> T): T = this.block()

  // Alias async functions to use the current context:

  fun <V> async(operation: () -> V) = async(this, operation)

  fun <T, V> Iterable<T>.mapAsync(operation: (T) -> V) = this.mapAsync(this@AsyncContext, operation)

  fun <T, V> Iterable<T>.mapAsyncThenBlock(operation: (T) -> V) = this.mapAsyncThenBlock(this@AsyncContext, operation)
}

private val defaultContext = AsyncContext()


/**
 * Async with "unlimited" concurrency.
 *
 * Call .get() on the futures to wait until the result is ready. Only do this
 * in the end, or inside an async block, to prevent blocking before all tasks
 * are queued or started.
 *
 * ```
 * val a = async { fetch('http://foo.bar/a') }
 * val b = async { fetch('http://foo.bar/b') }
 *
 * return Pair(a.get(), b.get())
 * ```
 */
fun <V> async(context: AsyncContext = defaultContext, operation: () -> V): Future<V> =
    CompletableFuture.supplyAsync({ operation() }, context.executor)

fun <T, V> Iterable<T>.mapAsync(context: AsyncContext = defaultContext, operation: (T) -> V): List<Future<V>> =
// Kotlin Iterable.map is eager, so all tasks are submitted at once
    this.map { CompletableFuture.supplyAsync({ operation(it) }, context.executor) }

// Run all operations concurrently, and then block until all of them are finished
fun <T, V> Iterable<T>.mapAsyncThenBlock(context: AsyncContext = defaultContext, operation: (T) -> V): List<V> =
    this.mapAsync(context) { operation(it) }.map { it.get() }
