package com.testpilot.controller;

import com.testpilot.dto.ApiRequest;
import com.testpilot.dto.TestCaseResponse;
import com.testpilot.service.TestCaseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * TestCaseController
 *
 * This is the REST API layer — the entry point for all HTTP requests from the frontend.
 *
 * It exposes two endpoints:
 * 1. POST /api/testcases/generate — Accepts API details, returns generated test cases
 * 2. GET  /api/testcases/download — Returns the test cases as a downloadable .txt file
 *
 * The Controller does NOT contain business logic. It:
 * - Receives the request
 * - Validates it (via @Valid)
 * - Delegates to TestCaseService
 * - Returns the response
 *
 * @RestController = @Controller + @ResponseBody (auto-converts return values to JSON)
 * @RequestMapping sets the base URL prefix for all endpoints in this class
 * @CrossOrigin is NOT used here — CORS is handled globally in CorsConfig
 */
@RestController
@RequestMapping("/api/testcases")
public class TestCaseController {

    private final TestCaseService testCaseService;

    // Constructor injection — the recommended way to inject dependencies in Spring Boot
    public TestCaseController(TestCaseService testCaseService) {
        this.testCaseService = testCaseService;
    }

    /**
     * POST /api/testcases/generate
     *
     * Accepts the user's API details from React and returns AI-generated test cases.
     *
     * @Valid triggers bean validation on the ApiRequest DTO.
     * If validation fails, GlobalExceptionHandler handles it automatically.
     *
     * @RequestBody tells Spring to deserialize the incoming JSON body into ApiRequest
     * ResponseEntity<TestCaseResponse> gives us full control over the HTTP response
     */
    @PostMapping("/generate")
    public ResponseEntity<TestCaseResponse> generateTestCases(@Valid @RequestBody ApiRequest request) {
        TestCaseResponse response = testCaseService.generateTestCases(request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/testcases/download
     *
     * Returns the last generated test cases as a plain text (.txt) file download.
     *
     * The Content-Disposition header with "attachment" tells the browser to download
     * the file instead of displaying it in the browser tab.
     *
     * Note: Since we have no database, the frontend sends the content as a query
     * parameter and we format it as plain text for download.
     * In a real production app, you'd store this in a session or database.
     *
     * For this beginner project, the download is handled entirely on the frontend
     * using a Blob + anchor tag trick, so this endpoint is optional.
     * We include it here to demonstrate the backend download pattern.
     */
    @GetMapping("/download")
    public ResponseEntity<String> downloadTestCases(@RequestParam String content) {
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=TestCases.txt")
            .header("Content-Type", "text/plain")
            .body(content);
    }
}
