import React, { useState } from 'react'
import InputForm from './components/InputForm'
import ResultSection from './components/ResultSection'
import { generateTestCases } from './services/apiService'
import './App.css'

/**
 * App Component — Root Component
 *
 * This is the top-level component that manages application state and
 * orchestrates communication between child components.
 *
 * State:
 * - isLoading: boolean — true while waiting for the API response
 * - result: TestCaseResponse object — the AI-generated test cases
 * - error: string — error message to display if the request fails
 * - lastRequest: { method, endpoint } — used in the download file header
 *
 * The component renders:
 * 1. A header with branding
 * 2. The InputForm component
 * 3. Either a loading indicator, an error message, or the ResultSection
 */
function App() {
  const [isLoading, setIsLoading] = useState(false)
  const [result, setResult] = useState(null)
  const [error, setError] = useState(null)
  const [lastRequest, setLastRequest] = useState({ method: '', endpoint: '' })

  /**
   * Called by InputForm when the user submits the form.
   * Sends the API details to Spring Boot and updates the result state.
   *
   * @param {Object} formData - { method, endpoint, description, requestBody }
   */
  const handleGenerate = async (formData) => {
    // Reset previous results and errors
    setIsLoading(true)
    setResult(null)
    setError(null)
    setLastRequest({ method: formData.method, endpoint: formData.endpoint })

    try {
      const data = await generateTestCases(formData)
      setResult(data)

      // Smooth scroll to the results after they load
      setTimeout(() => {
        document.getElementById('results')?.scrollIntoView({ behavior: 'smooth', block: 'start' })
      }, 100)

    } catch (err) {
      // Extract a meaningful error message from the Axios error
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

      {/* ── Header ──────────────────────────────────────────────────── */}
      <header className="app-header">
        <div className="header-content">
          <div className="logo-group">
            <div className="logo-icon" aria-hidden="true">
              <span>✦</span>
            </div>
            <div className="logo-text">
              <h1 className="app-title">TestPilot <span className="title-ai">AI</span></h1>
              <p className="app-tagline">AI-Powered REST API Test Case Generator</p>
            </div>
          </div>
          <div className="header-badge">
            <span className="powered-by">Powered by</span>
            <span className="gemini-badge">Gemini</span>
          </div>
        </div>
      </header>

      {/* ── Main Content ─────────────────────────────────────────────── */}
      <main className="app-main">
        <div className="container">

          {/* Instruction Bar */}
          <div className="instruction-bar">
            <span className="instruction-step active">1</span>
            <span className="instruction-label">Enter API Details</span>
            <span className="instruction-arrow">→</span>
            <span className={`instruction-step ${result ? 'active' : ''}`}>2</span>
            <span className="instruction-label">Generate Test Cases</span>
            <span className="instruction-arrow">→</span>
            <span className={`instruction-step ${result ? 'active' : ''}`}>3</span>
            <span className="instruction-label">Download Results</span>
          </div>

          {/* Input Form */}
          <section className="section" aria-label="API Input Form">
            <div className="section-header">
              <span className="section-tag">REQUEST</span>
            </div>
            <InputForm onSubmit={handleGenerate} isLoading={isLoading} />
          </section>

          {/* Loading State */}
          {isLoading && (
            <div className="loading-container" role="status" aria-live="polite">
              <div className="loading-pulse">
                <div className="pulse-ring"></div>
                <div className="pulse-ring pulse-ring--delay"></div>
                <span className="pulse-icon" aria-hidden="true">✦</span>
              </div>
              <p className="loading-text">Analyzing your API with Gemini AI...</p>
              <p className="loading-subtext">This usually takes 5–15 seconds</p>
            </div>
          )}

          {/* Error State */}
          {error && !isLoading && (
            <div className="error-container" role="alert">
              <div className="error-icon-large" aria-hidden="true">⚠</div>
              <div className="error-content">
                <h3 className="error-title">Something went wrong</h3>
                <p className="error-message">{error}</p>
              </div>
            </div>
          )}

          {/* Results */}
          {result && !isLoading && (
            <section className="section" aria-label="Generated Test Cases">
              <div className="section-header">
                <span className="section-tag section-tag--success">RESPONSE</span>
              </div>
              <ResultSection data={result} requestInfo={lastRequest} />
            </section>
          )}

        </div>
      </main>

      {/* ── Footer ───────────────────────────────────────────────────── */}
      <footer className="app-footer">
        <p>
          TestPilot AI &nbsp;·&nbsp; Built with Spring Boot &amp; Google Gemini
          &nbsp;·&nbsp; <span className="footer-note">For learning &amp; portfolio use</span>
        </p>
      </footer>

    </div>
  )
}

export default App
