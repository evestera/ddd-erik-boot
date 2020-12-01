{
  const selector = '.widget-erik-boot-describe-self';
  if (document.querySelector(selector)) {
    import('https://unpkg.com/htm/preact/standalone.module.js').then(({html, useState, useEffect, render}) => {
      const Widget = () => {
        const [metadata, setMetadata] = useState(null);
        useEffect(() => {
          fetch("/metadata").then(res => res.json()).then(res => {
            setMetadata(res);
          }).catch(err => ({ error: err }));
        }, []);

        if (!metadata) {
          return html`<p>Loading metadata...</p>`;
        }

        if (metadata.error) {
          return html`<p>Error loading metadata: ${metadata.error}</p>`;
        }

        return html`
            <div>
                <dl>
                    <dt>Name</dt>
                    <dd>${metadata.name}</dd>

                    <dt>Owner</dt>
                    <dd>${metadata.owner || "No owner defined"}</dd>

                    <dt>Description</dt>
                    <dd>${metadata.description || "No description defined"}</dd>

                    <dt>Services</dt>
                    <dd>
                        <ul>
                            ${(metadata.services || []).map(path => html`
                                <li><a href="${path}">${path}</a></li>
                            `)}
                        </ul>
                    </dd>
                </dl>
            </div>
        `;
      }
      render(html`<${Widget} />`, document.querySelector(selector))
    });
  }
}
