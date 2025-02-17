package com.sendi.spongeurl.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UrlCreateRequest {
    private String fullUrlValue;
    private String customShortUrl;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;
}
