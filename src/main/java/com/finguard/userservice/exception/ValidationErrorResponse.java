package com.finguard.userservice.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


/**
 * A structured response class for validation errors.
 *
 * <p>This class ensures:
 * <ul>
 *   <li>Consistent 400 Bad Request responses for invalid payloads.</li>
 *   <li>User-friendly and structured error messages for clients.</li>
 *   <li>Captures the timestamp, HTTP status, and a list of validation errors.</li>
 * </ul>
 *
 * <p>Typical JSON example:
 * <pre>{@code
 * {
 *   "timestamp": "2025-07-15T16:15:23",
 *   "status": 400,
 *   "errors": [
 *     "email must be a well-formed email address",
 *     "password must be at least 8 characters"
 *   ]
 * }
 * }</pre>
 */
@Getter
@Setter
@NoArgsConstructor
public class ValidationErrorResponse {
    /**
     * The timestamp when the error occurred.
     */
    private LocalDateTime timestamp;
    /**
     * The HTTP status code (e.g. 400).
     */
    private int status;
    /**
     * A list of validation error messages.
     */
    private List<String> errors;

    /**
     * Constructs the error response with all fields.
     *
     * @param timestamp the time of error occurrence.
     * @param status HTTP status code.
     * @param errors list of error messages.
     */
    public ValidationErrorResponse(LocalDateTime timestamp, int status, List<String> errors) {
        this.timestamp = timestamp;
        this.status = status;
        this.errors = errors;
    }

}
