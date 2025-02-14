package com.sendi.spongeurl.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UrlCreateRequest {
    private String fullUrlValue;
    private String customShortUrl;
}
