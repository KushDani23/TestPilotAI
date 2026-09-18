import axios from 'axios'

/**
 * apiService.js
 *
 * Centralizes all HTTP communication between the React frontend
 * and the Spring Boot backend.
 *
 * In production the VITE_API_BASE_URL env variable points at the
 * Render-hosted backend; in local dev the Vite proxy handles /api.
 *
 * The Groq API key is supplied by the user at runtime and forwarded
 * as the X-Groq-Api-Key request header so the server never needs
 * a hard-coded key.
 */

const BASE_URL = import.meta.env.VITE_API_BASE_URL
  ? `${import.meta.env.VITE_API_BASE_URL}/api`
  : '/api'

// Axios instance — timeout is 60 s to cover LLM latency
const apiClient = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 60000,
})

/**
 * Sends the user's API details to the backend and returns generated test cases.
 *
 * @param {Object} requestData - { method, endpoint, description, requestBody }
 * @param {string} groqApiKey  - The user's Groq API key (forwarded as a header)
 * @returns {Promise<Object>}  - { summary, positiveTests, negativeTests, validationTests, expectedResponses }
 */
export const generateTestCases = async (requestData, groqApiKey) => {
  const response = await apiClient.post('/testcases/generate', requestData, {
    headers: {
      'X-Groq-Api-Key': groqApiKey || '',
    },
  })
  return response.data
}
