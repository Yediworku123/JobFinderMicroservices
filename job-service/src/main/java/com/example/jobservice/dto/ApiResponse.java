package com.example.jobservice.dto;

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

    private String status;
    private String message;
    private T data; // ✅ NOW 'T' IS RECOGNIZED
    private LocalDateTime timestamp;

    public static <T> ResponseEntity<ApiResponse<T>> success(T data, String message) { // ✅ ADDED <T> HERE
        return ResponseEntity.ok(
                ApiResponse.<T>builder() // ✅ ADDED <T> HERE
                        .status("SUCCESS")
                        .message(message)
                        .data(data)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(HttpStatus httpStatus, String message) { // ✅ ADDED <T> HERE

        String customStatus = "ERROR";
        if (httpStatus == HttpStatus.NOT_FOUND) {
            customStatus = "NOT_FOUND";
        } else if (httpStatus == HttpStatus.FORBIDDEN) {
            customStatus = "FORBIDDEN";
        }

        return ResponseEntity.status(httpStatus).body(
                ApiResponse.<T>builder() // ✅ ADDED <T> HERE
                        .status(customStatus)
                        .message(message)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}