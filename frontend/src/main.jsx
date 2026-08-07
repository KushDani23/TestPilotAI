import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
import './index.css'

// This is the entry point for the React application.
// ReactDOM.createRoot mounts the React app to the <div id="root"> in index.html
// React.StrictMode helps catch potential problems during development
ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
)
