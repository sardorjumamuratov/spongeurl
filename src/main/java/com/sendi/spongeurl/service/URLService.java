package com.sendi.spongeurl.service;

import com.sendi.spongeurl.dto.OneFieldLongResponse;
import com.sendi.spongeurl.dto.UrlCreateRequest;
import com.sendi.spongeurl.dto.ShortURL;
import com.sendi.spongeurl.common.URLUtil;
import com.sendi.spongeurl.common.ShorteningUtil;
import com.sendi.spongeurl.entity.UrlEntity;
import com.sendi.spongeurl.repo.URLRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class URLService {
    private final URLRepository repository;
    private final StringRedisTemplate stringRedisTemplate;

    private UrlEntity get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Url not found with id " + id));
    }

    public String getFullUrl(String shortUrl) {
        UrlEntity url = repository.findByShortURL(shortUrl)
                .orElseThrow(() -> new RuntimeException("URL doesn't exist for this short url!"));

        url.setClicks(increaseClickNumber());
        repository.save(url);

        return url.getFullURL();
    }

    private void save(Long id, String fullUrl, String shortUrl) {
        UrlEntity entity = new UrlEntity();
        entity.setId(id);
        entity.setShortURL(shortUrl);
        entity.setFullURL(fullUrl);

        repository.save(entity);
    }

    @Transactional(readOnly = true)
    public ShortURL shortenURL(HttpServletRequest servletRequest, UrlCreateRequest urlCreateRequest) {
        if (repository.existsByShortURL(urlCreateRequest.getCustomShortUrl())) {
            throw new RuntimeException("This custom URL already exists!!!");
        }

        String baseURL = URLUtil.getBaseUrl(servletRequest.getRequestURL().toString());

        List<UrlEntity> savedUrls = checkUrlExistsAndGetAll(urlCreateRequest.getFullUrlValue());

        Long nextId = generateNextId();

        String shortUrlText;

        if (Objects.nonNull(savedUrls) || !savedUrls.isEmpty()) {
            shortUrlText = savedUrls.get(0).getShortURL();
        } else {
            if (Objects.isNull(urlCreateRequest.getCustomShortUrl()) || urlCreateRequest.getCustomShortUrl().isEmpty())
                shortUrlText = ShorteningUtil.idToStr(nextId);
            else
                shortUrlText = urlCreateRequest.getCustomShortUrl();

            save(nextId, urlCreateRequest.getFullUrlValue(), shortUrlText);
        }

        return new ShortURL(baseURL + shortUrlText);
    }

    @Transactional(readOnly = true)
    public List<UrlEntity> checkUrlExistsAndGetAll(String url) {
        return repository.findAllByFullURL(url);
    }

    private Long generateNextId() {
        return stringRedisTemplate.opsForValue().increment("UrlEntity");
    }

    public OneFieldLongResponse getClicks(String shortUrl) {
         UrlEntity url = repository.findByShortURL(shortUrl)
                 .orElseThrow(() -> new RuntimeException("Cannot find by short url"));

         return new OneFieldLongResponse(url.getClicks());
    }

    public Long increaseClickNumber() {
        return stringRedisTemplate.opsForValue().increment("shorturl:click:sequence");
    }
}
