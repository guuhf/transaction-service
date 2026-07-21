package com.guuh.transaction_service.infrastructure.client;


import com.guuh.transaction_service.business.dto.response.ReportResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "Notification", url = "${notification.url}")
public interface NotificationClient {
    @PostMapping("/notification")
    public ResponseEntity<Void> sendEmail(@RequestBody ReportResponseDto dto,
                                          @RequestParam String email);
}
