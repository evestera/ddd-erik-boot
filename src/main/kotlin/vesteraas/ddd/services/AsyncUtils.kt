package vesteraas.ddd.services

import org.slf4j.MDC
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.function.Supplier

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

val contextPreservingExecutor: Executor = Executors.newCachedThreadPool().preservingContext()

fun createContextPreservingExecutorWithMaxThreads(maxThreads: Int): Executor =
    Executors.newFixedThreadPool(maxThreads).preservingContext()

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

fun <V> async(context: AsyncContext = defaultContext, operation: () -> V): Future<V> =
    CompletableFuture.supplyAsync(Supplier { operation() }, context.executor)

fun <T, V> Iterable<T>.mapAsync(context: AsyncContext = defaultContext, operation: (T) -> V): List<Future<V>> =
// Kotlin Iterable.map is eager, so all tasks are submitted at once
    this.map { CompletableFuture.supplyAsync({ operation(it) }, context.executor) }

// Run all operations concurrently, and then block until all of them are finished
fun <T, V> Iterable<T>.mapAsyncThenBlock(context: AsyncContext = defaultContext, operation: (T) -> V): List<V> =
    this.mapAsync(context) { operation(it) }.map { it.get() }
