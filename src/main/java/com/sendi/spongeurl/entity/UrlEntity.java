package com.sendi.spongeurl.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("UrlEntity")
public class UrlEntity {
    @Id
    private Long id;

    private String shortURL;

    private String fullURL;

    private Long clicks;

    private Boolean isOneTime;

    private LocalDate expiryDate;
}

