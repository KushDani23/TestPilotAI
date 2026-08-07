# TestPilot AI ✦

> **AI-Powered REST API Test Case Generator**  
> Built with Java 21 · Spring Boot · React · Google Gemini API

---

## What It Does

TestPilot AI automatically generates comprehensive REST API test cases using Google's Gemini AI.

1. Enter your API details (method, endpoint, description, request body)
2. Click **Generate Test Cases**
3. Gemini AI creates positive, negative, and validation test cases
4. Download everything as a `.txt` file

---

## Project Structure

```
SmartAPITester/
│
├── backend/                          ← Spring Boot (Java 21, Maven)
│   ├── pom.xml
│   └── src/main/java/com/testpilot/
│       ├── TestPilotApplication.java
│       ├── controller/
│       │   └── TestCaseController.java
│       ├── service/
│       │   ├── TestCaseService.java
│       │   └── GeminiService.java
│       ├── dto/
│       │   ├── ApiRequest.java
│       │   ├── TestCase.java
│       │   ├── ExpectedResponse.java
│       │   └── TestCaseResponse.java
│       ├── exception/
│       │   ├── GeminiException.java
│       │   └── GlobalExceptionHandler.java
│       └── config/
│           └── CorsConfig.java
│   └── src/main/resources/
│       └── application.properties
│
└── frontend/                         ← React (Vite)
    ├── package.json
    ├── vite.config.js
    ├── index.html
    └── src/
        ├── main.jsx
        ├── App.jsx
        ├── App.css
        ├── index.css
        ├── components/
        │   ├── InputForm.jsx
        │   ├── InputForm.css
        │   ├── ResultSection.jsx
        │   └── ResultSection.css
        └── services/
            └── apiService.js
```

---

## Prerequisites

| Tool | Version |
|------|---------|
| Java | 21+ |
| Maven | 3.8+ |
| Node.js | 18+ |
| npm | 9+ |

---

## Setup and Run

### Step 1 — Get Your Gemini API Key

1. Go to https://aistudio.google.com/app/apikey
2. Sign in with your Google account
3. Click Create API Key
4. Copy the key

### Step 2 — Configure the Backend

Open backend/src/main/resources/application.properties and replace:

  gemini.api.key=YOUR_GEMINI_API_KEY_HERE

### Step 3 — Run the Backend

  cd backend
  mvn spring-boot:run

The backend starts at: http://localhost:8080

### Step 4 — Run the Frontend

  cd frontend
  npm install
  npm run dev

The frontend starts at: http://localhost:5173

---

## API Endpoints

POST /api/testcases/generate

Request:
{
  "method": "POST",
  "endpoint": "/users",
  "description": "Creates a new user account",
  "requestBody": "{ \"name\": \"John\", \"email\": \"john@example.com\" }"
}

Response:
{
  "summary": "...",
  "positiveTests": [...],
  "negativeTests": [...],
  "validationTests": [...],
  "expectedResponses": [...]
}

---

## Tech Stack

Frontend:  React 18, Plain CSS, Axios
Backend:   Java 21, Spring Boot 3, Maven
AI:        Google Gemini 1.5 Flash
Build:     Vite (frontend), Maven (backend)

---

Built for portfolio and placement interview preparation.
