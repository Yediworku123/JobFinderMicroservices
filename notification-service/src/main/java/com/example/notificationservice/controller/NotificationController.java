package com.example.notificationservice.controller;

import com.example.notificationservice.entity.Notification;
import com.example.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // GET /api/notifications/me
    @GetMapping("/me")
    public ResponseEntity<List<Notification>> getMyNotifications(
            @RequestHeader("X-User-Id") String userId
    ) {
        return ResponseEntity.ok(notificationService.getNotificationsByUserId(userId));
    }
}