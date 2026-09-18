package com.testpilot.controller;

import com.testpilot.dto.ApiRequest;
import com.testpilot.dto.TestCaseResponse;
import com.testpilot.exception.LLMException;
import com.testpilot.service.TestCaseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/testcases")
public class TestCaseController {

    private final TestCaseService testCaseService;

    public TestCaseController(TestCaseService testCaseService) {
        this.testCaseService = testCaseService;
    }

    @PostMapping("/generate")
    public ResponseEntity<TestCaseResponse> generateTestCases(
            @Valid @RequestBody ApiRequest request,
            @RequestHeader(value = "X-Groq-Api-Key", required = false) String groqApiKey) {

        // Reject the request immediately if no API key is provided
        if (groqApiKey == null || groqApiKey.isBlank()) {
            throw new LLMException(
                "No Groq API key provided. Please set your API key using the 'Set API Key' button.");
        }

        TestCaseResponse response = testCaseService.generateTestCases(request, groqApiKey);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/download")
    public ResponseEntity<String> downloadTestCases(@RequestParam String content) {
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=TestCases.txt")
            .header("Content-Type", "text/plain")
            .body(content);
    }
}
