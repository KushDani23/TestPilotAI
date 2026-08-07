import React, { useState } from 'react'
import './InputForm.css'

/**
 * InputForm Component
 *
 * This component renders the input section of the application.
 * It collects the user's API details through a Postman-inspired form.
 *
 * Props:
 * - onSubmit(formData): called when user clicks "Generate Test Cases"
 * - isLoading: boolean — disables the form while the API call is in progress
 */
function InputForm({ onSubmit, isLoading }) {
  // Local state for each form field
  const [method, setMethod] = useState('POST')
  const [endpoint, setEndpoint] = useState('')
  const [description, setDescription] = useState('')
  const [requestBody, setRequestBody] = useState('')
  const [formError, setFormError] = useState('')

  // HTTP methods available in the dropdown
  const httpMethods = ['GET', 'POST', 'PUT', 'PATCH', 'DELETE']

  // Color coding for HTTP method badges — standard REST convention
  const methodColors = {
    GET: 'method-get',
    POST: 'method-post',
    PUT: 'method-put',
    PATCH: 'method-patch',
    DELETE: 'method-delete',
  }

  /**
   * Handles form submission.
   * Validates required fields before calling the parent's onSubmit.
   */
  const handleSubmit = (e) => {
    e.preventDefault()
    setFormError('')

    // Client-side validation
    if (!endpoint.trim()) {
      setFormError('Please enter an API endpoint (e.g., /users)')
      return
    }
    if (!description.trim()) {
      setFormError('Please enter a description for your API')
      return
    }

    // Pass the form data up to the parent component (App.jsx)
    onSubmit({
      method,
      endpoint: endpoint.trim(),
      description: description.trim(),
      requestBody: requestBody.trim() || null,
    })
  }

  return (
    <div className="input-form-container">
      <form onSubmit={handleSubmit} noValidate>

        {/* ── URL Bar (Method + Endpoint) ───────────────────────────────── */}
        <div className="url-bar">
          <div className="method-selector-wrapper">
            <select
              id="http-method"
              className={`method-select ${methodColors[method]}`}
              value={method}
              onChange={(e) => setMethod(e.target.value)}
              disabled={isLoading}
            >
              {httpMethods.map((m) => (
                <option key={m} value={m}>{m}</option>
              ))}
            </select>
          </div>

          <input
            id="api-endpoint"
            type="text"
            className="endpoint-input"
            placeholder="/api/users"
            value={endpoint}
            onChange={(e) => setEndpoint(e.target.value)}
            disabled={isLoading}
            autoComplete="off"
            spellCheck="false"
          />
        </div>

        {/* ── API Description ───────────────────────────────────────────── */}
        <div className="form-group">
          <label htmlFor="api-description" className="form-label">
            API Description
            <span className="required-badge">required</span>
          </label>
          <textarea
            id="api-description"
            className="form-textarea"
            placeholder="Describe what this API does. E.g., Creates a new user account with name and email."
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            disabled={isLoading}
            rows={3}
          />
        </div>

        {/* ── Request Body ──────────────────────────────────────────────── */}
        <div className="form-group">
          <label htmlFor="request-body" className="form-label">
            Request Body
            <span className="optional-badge">optional</span>
          </label>
          <textarea
            id="request-body"
            className="form-textarea form-textarea--mono"
            placeholder={`{\n  "name": "John Doe",\n  "email": "john@example.com"\n}`}
            value={requestBody}
            onChange={(e) => setRequestBody(e.target.value)}
            disabled={isLoading}
            rows={6}
            spellCheck="false"
          />
          <p className="field-hint">Paste your JSON request body here. Leave empty for GET requests.</p>
        </div>

        {/* ── Validation Error ──────────────────────────────────────────── */}
        {formError && (
          <div className="form-error" role="alert">
            <span className="error-icon">⚠</span>
            {formError}
          </div>
        )}

        {/* ── Submit Button ─────────────────────────────────────────────── */}
        <button
          id="generate-btn"
          type="submit"
          className={`generate-btn ${isLoading ? 'generate-btn--loading' : ''}`}
          disabled={isLoading}
        >
          {isLoading ? (
            <>
              <span className="spinner" aria-hidden="true"></span>
              Generating Test Cases...
            </>
          ) : (
            <>
              <span className="btn-icon" aria-hidden="true">✦</span>
              Generate Test Cases
            </>
          )}
        </button>

      </form>
    </div>
  )
}

export default InputForm
