import React, { useState, useEffect, useRef } from 'react'
import './ApiKeyModal.css'

/**
 * ApiKeyModal — Groq API key entry dialog
 *
 * Props:
 *   isOpen        boolean — controls visibility
 *   currentKey    string  — pre-fills the input if a key is already set
 *   onSubmit(key) called with the trimmed key string
 *   onClose()     called when the modal is dismissed without saving
 */
function ApiKeyModal({ isOpen, currentKey, onSubmit, onClose }) {
  const [keyValue, setKeyValue] = useState(currentKey || '')
  const [showKey, setShowKey] = useState(false)
  const inputRef = useRef(null)

  // Sync input when modal opens / currentKey changes
  useEffect(() => {
    if (isOpen) {
      setKeyValue(currentKey || '')
      setShowKey(false)
      // Focus the input a tick after mount so animation completes first
      setTimeout(() => inputRef.current?.focus(), 80)
    }
  }, [isOpen, currentKey])

  // Close on Escape key
  useEffect(() => {
    if (!isOpen) return
    const onKeyDown = (e) => {
      if (e.key === 'Escape') onClose()
    }
    window.addEventListener('keydown', onKeyDown)
    return () => window.removeEventListener('keydown', onKeyDown)
  }, [isOpen, onClose])

  if (!isOpen) return null

  const handleSubmit = (e) => {
    e.preventDefault()
    const trimmed = keyValue.trim()
    if (trimmed) onSubmit(trimmed)
  }

  return (
    <div
      className="apikey-overlay"
      role="dialog"
      aria-modal="true"
      aria-labelledby="apikey-modal-title"
      onClick={(e) => { if (e.target === e.currentTarget) onClose() }}
    >
      <div className="apikey-box">

        {/* ── Header ─────────────────────────────────────────── */}
        <div className="apikey-header">
          <span className="apikey-key-icon" aria-hidden="true">🔑</span>
          <h2 id="apikey-modal-title" className="apikey-title">Groq API Key</h2>
          <button
            className="apikey-close-btn"
            onClick={onClose}
            aria-label="Close"
            type="button"
          >
            ✕
          </button>
        </div>

        {/* ── Body ───────────────────────────────────────────── */}
        <form onSubmit={handleSubmit} noValidate>
          <p className="apikey-subtitle">Enter your Groq API key below:</p>

          <div className="apikey-input-wrapper">
            <input
              ref={inputRef}
              id="groq-api-key-input"
              type={showKey ? 'text' : 'password'}
              className="apikey-input"
              placeholder="gsk_••••••••••••••••••••••••••••••••••••••••••••••••••"
              value={keyValue}
              onChange={(e) => setKeyValue(e.target.value)}
              autoComplete="off"
              spellCheck="false"
            />
            <button
              type="button"
              className="apikey-toggle-btn"
              onClick={() => setShowKey((v) => !v)}
              aria-label={showKey ? 'Hide API key' : 'Show API key'}
            >
              {showKey ? '🙈' : '👁'}
            </button>
          </div>

          <p className="apikey-hint">
            Get your free key at{' '}
            <a
              href="https://console.groq.com/keys"
              target="_blank"
              rel="noopener noreferrer"
              className="apikey-link"
            >
              console.groq.com/keys
            </a>
            . Your key is never sent anywhere except directly to Groq.
          </p>

          <button
            id="apikey-submit-btn"
            type="submit"
            className="apikey-submit-btn"
            disabled={!keyValue.trim()}
          >
            Submit
          </button>
        </form>

      </div>
    </div>
  )
}

export default ApiKeyModal
