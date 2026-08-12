package com.guuh.transaction_service.business;

import com.guuh.transaction_service.business.dto.response.ReportResponseDto;
import com.guuh.transaction_service.infrastructure.client.NotificationClient;
import com.guuh.transaction_service.infrastructure.entity.User;
import com.guuh.transaction_service.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CronService {
    private final ReportService reportService;
    private final NotificationClient client;
    private final UserRepository userRepository;

    @Scheduled(cron = "${cron.expression}")
    public void sendEmail(){
        int page = 0;
        Page<User> users;
        do {
        users = userRepository.findAll(PageRequest.of(page, 100));
        for (User user: users){
            ReportResponseDto dto = reportService.generateMonthlyReport(user.getId());
            client.sendEmail(dto, user.getEmail());
        }

        page++;
        } while (users.hasNext());
    }


}
