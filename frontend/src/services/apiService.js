import axios from 'axios'

/**
 * apiService.js
 *
 * This module centralizes all HTTP communication between the React frontend
 * and the Spring Boot backend.
 *
 * We use Axios instead of the native fetch() because:
 * - Axios automatically parses JSON responses
 * - Axios has better error handling
 * - Axios makes it easy to set base URLs and headers
 *
 * Since we configured a proxy in vite.config.js, all /api/... calls
 * are automatically forwarded to http://localhost:8080 during development.
 */

// Create an Axios instance with default configuration
const apiClient = axios.create({
  baseURL: '/api',            // All requests will be prefixed with /api
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 60000,             // 60-second timeout (Gemini can take a few seconds)
})

/**
 * Sends the user's API details to the backend and returns generated test cases.
 *
 * @param {Object} requestData - { method, endpoint, description, requestBody }
 * @returns {Promise<Object>} - { summary, positiveTests, negativeTests, validationTests, expectedResponses }
 */
export const generateTestCases = async (requestData) => {
  const response = await apiClient.post('/testcases/generate', requestData)
  return response.data
}
