// com.example.common.dto (or inside each service)
package com.example.applicationservice.dto;

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
public class ApiResponse<T> {
    private String status; // SUCCESS, ERROR, NOT_FOUND
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public static <T> ResponseEntity<ApiResponse<T>> success(T data, String message) {
        return ResponseEntity.ok(
                ApiResponse.<T>builder()
                        .status("SUCCESS")
                        .message(message)
                        .data(data)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(
                ApiResponse.<T>builder()
                        .status(status.name())
                        .message(message)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}