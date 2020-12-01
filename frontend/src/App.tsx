import React, {useEffect, useState} from 'react';
import './App.css';

function App() {
  return (
      <>
        <header>
          <h1>Eriks Spring Boot node</h1>
        </header>
        <main>
          <article>
            <p>
              This app provides some useful services for creating minimal nodes:
            </p>
            <ul>
              <li><a href="#widgets">Widgets</a>, if you don't want to bother with a frontend yourself.</li>
              <li><a href="#persistence">Persistence-of-nodes-as-a-service</a> if you want persistence, but don't want to set up a database.</li>
            </ul>
          </article>
          <hr/>
          <article>
            <header>
              <h2 id="widgets">Widgets</h2>
            </header>
            <Widgets/>
          </article>
          <article>
            <h2 id="persistence">Persistence of nodes (as a service)</h2>
            <p>
              Make a secure <code>POST</code> request to <code>/secure/persist/nodes/sync</code> when your app
              starts and this app will let call <code>POST /nodes</code> for any nodes your app has forgotten.
              It will automatically check your app every 10 minutes to see if your app has learned of any new
              nodes.
            </p>

            <p>
              Make a secure <code>POST</code> request to <code>/secure/persist/nodes/unregister</code> to stop
              this syncing.
            </p>
          </article>
        </main>
        <hr />
        <footer>
          <p><small>This is a footer. I guess I should have some stuff here?</small></p>
        </footer>
      </>
  );
}

type Widget = {
  selector: string // CSS selector
  script: string // URL to JS-file
  name?: string
  description?: string
};

function Widgets(): React.ReactElement {
  const [widgets, setWidgets] = useState<Widget[] | undefined>();
  useEffect(() => {
    fetch("/widgets").then(res => res.json()).then((res: Widget[]) => {
      setWidgets(res);
    })
  }, []);

  if (!widgets) {
    return <p>Loading widgets...</p>;
  }

  if (widgets.length === 0) {
    return <p>No widgets found</p>
  }

  return (
      <>
        {widgets.map(widget => <WidgetPreview widget={widget} key={widget.script}/>)}
      </>
  );
}

function WidgetPreview({widget}: { widget: Widget }): React.ReactElement {
  useEffect(() => {
    const script = document.createElement('script');

    script.src = widget.script;
    script.async = true;

    document.body.appendChild(script);

    return () => {
      document.body.removeChild(script);
    }
  }, [widget.script]);

  return (
      <>
        <article>
          <h3>{widget.name || widget.selector}</h3>
          <p>{widget.description || "No description"}</p>
          <p>Selector: <code>{widget.selector}</code></p>
          <p>Script URL: <code>{widget.script}</code></p>
          <h4>Preview:</h4>
          <div className={widget.selector.replace(".", "")}/>
        </article>
        <hr/>
      </>
  );
}


export default App;
