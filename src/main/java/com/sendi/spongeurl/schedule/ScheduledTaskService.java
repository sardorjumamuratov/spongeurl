package com.sendi.spongeurl.schedule;

import com.sendi.spongeurl.entity.UrlEntity;
import com.sendi.spongeurl.service.URLService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduledTaskService {

    private final URLService urlService;

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteExpiredCards() {
        LocalDate today = LocalDate.now();
        List<UrlEntity> expiredCards = urlService.findAllExpiredUrls(today);

        if (!expiredCards.isEmpty()) {
            urlService.clearAllExpiredUrls(expiredCards);
        }
    }
}
