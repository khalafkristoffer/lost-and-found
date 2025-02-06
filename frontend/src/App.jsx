import  { useState } from 'react';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import './App.css';
import LandingPage from './LandingPage';

function SearchPage() {
  const [prompt, setPrompt] = useState('');

  const handleSubmit = (event) => {
    event.preventDefault();
    // Here you would typically send the prompt to the backend for processing
    console.log('Submitted prompt:', prompt);
  };

  return (
    <div className="App">
      <header className="App-header">
        <h1>Lost and Found</h1>
        <p>Enter a description of the lost item to search for it.</p>
        <form onSubmit={handleSubmit}>
          <input
            type="text"
            value={prompt}
            onChange={(e) => setPrompt(e.target.value)}
            placeholder="Describe the lost item..."
            required
          />
          <button type="submit">Search</button>
        </form>
      </header>
    </div>
  );
}

function App() {
  return (
    <Router>
      <Switch>
        <Route path="/search" component={SearchPage} />
        <Route path="/" component={LandingPage} />
      </Switch>
    </Router>
  );
}

export default App;
