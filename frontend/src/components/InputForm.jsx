import React, { useState } from 'react'
import './InputForm.css'

/**
 * InputForm — Postman-inspired API input panel
 *
 * Layout:
 *   [METHOD ▾] [/endpoint          ] [Generate Test Cases →]
 *   ─────────────────────────────────────────────────────────
 *   [Description] [Body]    ← tabs
 *   ─────────────────────────────────────────────────────────
 *   <tab content>
 *
 * Props:
 *   onSubmit(formData)  called when user submits
 *   isLoading           disables the form while waiting
 */
function InputForm({ onSubmit, isLoading }) {
  const [method, setMethod]           = useState('POST')
  const [endpoint, setEndpoint]       = useState('')
  const [description, setDescription] = useState('')
  const [requestBody, setRequestBody] = useState('')
  const [activeTab, setActiveTab]     = useState('description') // 'description' | 'body'
  const [formError, setFormError]     = useState('')

  const httpMethods = ['GET', 'POST', 'PUT', 'PATCH', 'DELETE']

  const methodColors = {
    GET:    'method-get',
    POST:   'method-post',
    PUT:    'method-put',
    PATCH:  'method-patch',
    DELETE: 'method-delete',
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    setFormError('')

    if (!endpoint.trim()) {
      setFormError('Endpoint is required (e.g., /api/users)')
      return
    }
    if (!description.trim()) {
      setFormError('Description is required')
      return
    }

    onSubmit({
      method,
      endpoint:    endpoint.trim(),
      description: description.trim(),
      requestBody: requestBody.trim() || null,
    })
  }

  return (
    <div className="postman-panel">

      {/* ── URL Bar ───────────────────────────────────────────── */}
      <form onSubmit={handleSubmit} noValidate>
        <div className="url-bar">

          {/* Method dropdown */}
          <div className="method-wrapper">
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
            <span className="method-arrow" aria-hidden="true">▾</span>
          </div>

          <div className="url-divider" aria-hidden="true"></div>

          {/* Endpoint */}
          <input
            id="api-endpoint"
            type="text"
            className="endpoint-input"
            placeholder="Enter request URL  (e.g. /api/users)"
            value={endpoint}
            onChange={(e) => setEndpoint(e.target.value)}
            disabled={isLoading}
            autoComplete="off"
            spellCheck="false"
          />

          {/* Send button */}
          <button
            id="generate-btn"
            type="submit"
            className={`send-btn ${isLoading ? 'send-btn--loading' : ''}`}
            disabled={isLoading}
          >
            {isLoading ? (
              <>
                <span className="send-spinner" aria-hidden="true"></span>
                Generating…
              </>
            ) : (
              'Generate'
            )}
          </button>

        </div>

        {/* ── Tab Bar ─────────────────────────────────────────── */}
        <div className="tab-bar">
          <button
            type="button"
            className={`tab-btn ${activeTab === 'description' ? 'tab-btn--active' : ''}`}
            onClick={() => setActiveTab('description')}
          >
            Description
            {description && <span className="tab-dot" aria-hidden="true"></span>}
          </button>
          <button
            type="button"
            className={`tab-btn ${activeTab === 'body' ? 'tab-btn--active' : ''}`}
            onClick={() => setActiveTab('body')}
          >
            Body
            {requestBody && <span className="tab-dot" aria-hidden="true"></span>}
          </button>
        </div>

        {/* ── Tab Content ─────────────────────────────────────── */}
        <div className="tab-content">

          {activeTab === 'description' && (
            <div className="tab-pane">
              <label htmlFor="api-description" className="field-label">
                API Description <span className="badge-required">required</span>
              </label>
              <textarea
                id="api-description"
                className="field-textarea"
                placeholder="Describe what this API does.&#10;e.g. Creates a new user account with name and email fields."
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                disabled={isLoading}
                rows={5}
              />
            </div>
          )}

          {activeTab === 'body' && (
            <div className="tab-pane">
              <div className="body-header">
                <label htmlFor="request-body" className="field-label">
                  Request Body <span className="badge-optional">optional</span>
                </label>
                <span className="body-format-hint">JSON</span>
              </div>
              <textarea
                id="request-body"
                className="field-textarea field-textarea--mono"
                placeholder={'{\n  "name": "John Doe",\n  "email": "john@example.com"\n}'}
                value={requestBody}
                onChange={(e) => setRequestBody(e.target.value)}
                disabled={isLoading}
                rows={7}
                spellCheck="false"
              />
              <p className="field-hint">Leave empty for GET requests</p>
            </div>
          )}

        </div>

        {/* ── Validation Error ─────────────────────────────────── */}
        {formError && (
          <div className="form-error" role="alert">
            <span aria-hidden="true">⚠</span> {formError}
          </div>
        )}

      </form>
    </div>
  )
}

export default InputForm
