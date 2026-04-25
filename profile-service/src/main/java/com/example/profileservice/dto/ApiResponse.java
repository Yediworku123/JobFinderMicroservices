package com.example.profileservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> { // ✅ ADDED <T> HERE

    private String status;   // "SUCCESS", "ERROR", "NOT_FOUND"
    private String message;
    private T data;     // ✅ CHANGED FROM Object TO T
    private LocalDateTime timestamp;

    // ✅ ADDED <T> TO STATIC METHOD
    public static <T> ResponseEntity<ApiResponse<T>> success(T data, String message) {
        return ResponseEntity.ok(
                ApiResponse.<T>builder() // ✅ ADDED <T> TO BUILDER
                        .status("SUCCESS")
                        .message(message)
                        .data(data)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    // ✅ ADDED <T> TO STATIC METHOD
    public static <T> ResponseEntity<ApiResponse<T>> error(HttpStatus httpStatus, String message) {

        String customStatus = "ERROR";
        if (httpStatus == HttpStatus.NOT_FOUND) {
            customStatus = "NOT_FOUND";
        } else if (httpStatus == HttpStatus.FORBIDDEN) {
            customStatus = "FORBIDDEN";
        }

        return ResponseEntity.status(httpStatus).body(
                ApiResponse.<T>builder() // ✅ ADDED <T> TO BUILDER
                        .status(customStatus)
                        .message(message)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}