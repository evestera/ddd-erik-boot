{
  const selector = '.widget-erik-boot-own-nodes';
  if (document.querySelector(selector)) {
    import('https://unpkg.com/htm/preact/standalone.module.js').then(({html, useState, useEffect, render}) => {
      const Widget = () => {
        const [nodes, setNodes] = useState(null);
        useEffect(() => {
          fetch("/nodes").then(res => res.json()).then(res => {
            setNodes(res);
          });
        }, []);

        if (!nodes) {
          return html`<p>Loading nodes...</p>`;
        }

        if (nodes.length === 0) {
          return html`<p>It seems like the application does not know of any nodes...</p>`
        }

        return html`
            <ul>
                ${nodes.map(url => html`<li><a href="${url}">${url}</a></li>`)}
            </ul>
        `;
      }
      render(html`<${Widget} />`, document.querySelector(selector))
    });
  }
}
