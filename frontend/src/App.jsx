import React, { useState } from 'react'
import InputForm from './components/InputForm'
import ResultSection from './components/ResultSection'
import ApiKeyModal from './components/ApiKeyModal'
import { generateTestCases } from './services/apiService'
import './App.css'

/**
 * App — Root Component
 *
 * Manages all application state and connects InputForm → API → ResultSection.
 * Stores the full request info so ResultSection can include it in the JSON download.
 * Also manages the Groq API key entered by the user via the ApiKeyModal.
 */
function App() {
  const [isLoading, setIsLoading] = useState(false)
  const [result, setResult] = useState(null)
  const [error, setError] = useState(null)

  // Groq API key state — entered by user, never hard-coded
  const [groqApiKey, setGroqApiKey] = useState('')
  const [showApiKeyModal, setShowApiKeyModal] = useState(false)

  // Full request info saved so ResultSection can embed it in the JSON file
  const [lastRequest, setLastRequest] = useState({
    method: '',
    endpoint: '',
    description: '',
    requestBody: '',
  })

  const handleApiKeySubmit = (key) => {
    setGroqApiKey(key)
    setShowApiKeyModal(false)
  }

  const handleGenerate = async (formData) => {
    // Require the user to set their API key before generating
    if (!groqApiKey) {
      setShowApiKeyModal(true)
      return
    }

    setIsLoading(true)
    setResult(null)
    setError(null)
    setLastRequest({
      method:      formData.method,
      endpoint:    formData.endpoint,
      description: formData.description,
      requestBody: formData.requestBody || '',
    })

    try {
      const data = await generateTestCases(formData, groqApiKey)
      setResult(data)

      setTimeout(() => {
        document.getElementById('results')?.scrollIntoView({ behavior: 'smooth', block: 'start' })
      }, 100)

    } catch (err) {
      const errorMessage =
        err.response?.data?.error ||
        err.response?.data?.message ||
        (err.response?.data && typeof err.response.data === 'object'
          ? Object.values(err.response.data).join('. ')
          : null) ||
        err.message ||
        'Failed to generate test cases. Please check your connection and try again.'
      setError(errorMessage)
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="app">

      {/* ── Groq API Key Modal ───────────────────────────────── */}
      <ApiKeyModal
        isOpen={showApiKeyModal}
        currentKey={groqApiKey}
        onSubmit={handleApiKeySubmit}
        onClose={() => setShowApiKeyModal(false)}
      />

      {/* ── Header ──────────────────────────────────────────────────────── */}
      <header className="app-header">
        <div className="header-content">
          <div className="logo-group">
            <div className="logo-icon" aria-hidden="true">▶</div>
            <div className="logo-text">
              <h1 className="app-title">
                TestPilot <span className="title-accent">AI</span>
              </h1>
              <p className="app-tagline">AI-Powered REST API Test Case Generator</p>
            </div>
          </div>
          <div className="header-right">
            {/* API Key button — shows status indicator when key is set */}
            <button
              id="set-api-key-btn"
              className={`api-key-btn ${groqApiKey ? 'api-key-btn--set' : 'api-key-btn--unset'}`}
              onClick={() => setShowApiKeyModal(true)}
              title={groqApiKey ? 'Groq API Key is set — click to change' : 'Set your Groq API Key'}
            >
              <span className="api-key-btn-dot" aria-hidden="true"></span>
              {groqApiKey ? 'API Key Set' : 'Set API Key'}
            </button>
            <span className="header-version">v1.0</span>
          </div>
        </div>
      </header>

      {/* ── Main ────────────────────────────────────────────── */}
      <main className="app-main">
        <div className="container">

          {/* No API key warning banner */}
          {!groqApiKey && (
            <div className="apikey-banner" role="alert">
              <span className="apikey-banner-icon" aria-hidden="true">🔑</span>
              <span className="apikey-banner-text">
                Set your{' '}
                <button
                  className="apikey-banner-link"
                  onClick={() => setShowApiKeyModal(true)}
                >
                  Groq API Key
                </button>{' '}
                to start generating test cases.
              </span>
            </div>
          )}

          {/* Input Form */}
          <section aria-label="API Input">
            <InputForm onSubmit={handleGenerate} isLoading={isLoading} />
          </section>

          {/* Loading */}
          {isLoading && (
            <div className="loading-container" role="status" aria-live="polite">
              <div className="loading-bar">
                <div className="loading-bar-fill"></div>
              </div>
              <p className="loading-text">Generating test cases…</p>
              <p className="loading-subtext">This usually takes 5–15 seconds</p>
            </div>
          )}

          {/* Error */}
          {error && !isLoading && (
            <div className="error-container" role="alert">
              <span className="error-icon">✕</span>
              <div className="error-content">
                <strong className="error-title">Request Failed</strong>
                <p className="error-message">{error}</p>
              </div>
            </div>
          )}

          {/* Results */}
          {result && !isLoading && (
            <section id="results" aria-label="Generated Test Cases">
              <ResultSection data={result} requestInfo={lastRequest} />
            </section>
          )}

        </div>
      </main>

      {/* ── Footer ──────────────────────────────────────────── */}
      <footer className="app-footer">
        <p>TestPilot AI &nbsp;·&nbsp; Spring Boot + Groq AI &nbsp;·&nbsp; Portfolio Project</p>
      </footer>

    </div>
  )
}

export default App
