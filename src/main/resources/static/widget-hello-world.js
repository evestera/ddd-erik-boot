const selector = '.widget-erik-boot-hello-world';
if (document.querySelector(selector)) {
  import('https://unpkg.com/htm/preact/standalone.module.js').then(({html, render}) => {
    render(html`<p>Hello world!</p>`, document.querySelector(selector))
  });
}
