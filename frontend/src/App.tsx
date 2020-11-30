import React, {useEffect, useState} from 'react';
import './App.css';

function App() {
  return (
      <>
        <header>
          <h1>Eriks Spring Boot node</h1>
        </header>
        <main>
          <section>
            <header>
              <h2>Widgets</h2>
            </header>
            <Widgets/>
          </section>
        </main>
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
        {widgets.map(widget => <WidgetPreview widget={widget}/>)}
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
  }, []);

  return (
      <aside>
        <h3>{widget.name || widget.selector}</h3>
        <p>{widget.description || "No description"}</p>
        <p>Selector: <code>{widget.selector}</code></p>
        <p>Script URL: <code>{widget.script}</code></p>
        <h4>Preview:</h4>
        <div className={widget.selector.replace(".", "")}/>
      </aside>
  );
}


export default App;
