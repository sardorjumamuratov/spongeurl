package com.sendi.spongeurl.service;

import com.sendi.spongeurl.dto.OneFieldLongResponse;
import com.sendi.spongeurl.dto.UrlCreateRequest;
import com.sendi.spongeurl.dto.ShortURL;
import com.sendi.spongeurl.common.URLUtil;
import com.sendi.spongeurl.common.ShorteningUtil;
import com.sendi.spongeurl.entity.UrlEntity;
import com.sendi.spongeurl.entity.UserInfoEntity;
import com.sendi.spongeurl.repo.URLRepository;
import com.sendi.spongeurl.repo.UserInfoRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua_parser.Client;
import ua_parser.Parser;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class URLService {
    private final URLRepository repository;
    private final UserInfoRepository userInfoRepository;
    private final StringRedisTemplate stringRedisTemplate;

    private UrlEntity get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Url not found with id " + id));
    }

    @Transactional
    public String getFullUrl(String shortUrl, HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (Objects.isNull(ipAddress) || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        UserInfoEntity userInfoEntity = new UserInfoEntity();
        userInfoEntity.setAddress(ipAddress);
        userInfoRepository.save(userInfoEntity);

        saveDeviceDetails(userInfoEntity.getAddress(), request.getHeader("user-agent"));

        UrlEntity url = repository.findByShortURL(shortUrl)
                .orElseThrow(() -> new RuntimeException("URL doesn't exist for this short url!"));

        url.setClicks(increaseClickNumber());
        repository.save(url);

        return url.getFullURL();
    }

    private void save(Long id, String fullUrl, String shortUrl, LocalDate expiryDate) {
        UrlEntity entity = new UrlEntity();
        entity.setId(id);
        entity.setShortURL(shortUrl);
        entity.setFullURL(fullUrl);

        if (Objects.isNull(expiryDate))
            expiryDate = LocalDate.now().plusDays(3);

        entity.setExpiryDate(expiryDate);

        repository.save(entity);
    }

    @Transactional
    public ShortURL shortenURL(HttpServletRequest servletRequest, UrlCreateRequest urlCreateRequest) {
        String ipAddress = servletRequest.getHeader("X-Forwarded-For");
        if (Objects.isNull(ipAddress) || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = servletRequest.getRemoteAddr();
        }

        UserInfoEntity userInfoEntity = new UserInfoEntity();
        userInfoEntity.setAddress(ipAddress);
        userInfoRepository.save(userInfoEntity);

        saveDeviceDetails(userInfoEntity.getAddress(), servletRequest.getHeader("user-agent"));

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

            save(nextId, urlCreateRequest.getFullUrlValue(), shortUrlText, urlCreateRequest.getExpiryDate());
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

    @Transactional
    public OneFieldLongResponse getClicks(String shortUrl, HttpServletRequest servletRequest) {
        String ipAddress = servletRequest.getHeader("X-Forwarded-For");
        if (Objects.isNull(ipAddress) || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = servletRequest.getRemoteAddr();
        }

        UserInfoEntity userInfoEntity = new UserInfoEntity();
        userInfoEntity.setAddress(ipAddress);
        userInfoRepository.save(userInfoEntity);

        saveDeviceDetails(userInfoEntity.getAddress(), servletRequest.getHeader("user-agent"));

         UrlEntity url = repository.findByShortURL(shortUrl)
                 .orElseThrow(() -> new RuntimeException("Cannot find by short url"));

         return new OneFieldLongResponse(url.getClicks());
    }

    public Long increaseClickNumber() {
        return stringRedisTemplate.opsForValue().increment("shorturl:click:sequence");
    }

    @Transactional
    public void saveDeviceDetails(String IP, String userAgent) {
        UserInfoEntity userInfoEntity = userInfoRepository.findByAddress(IP)
                .orElseThrow();

        String deviceDetails = "UNKNOWN";

        Client client = new Parser().parse(userAgent);
        if (Objects.nonNull(client)) {
            deviceDetails = client.userAgent.family
                    + " " + client.userAgent.major + "."
                    + client.userAgent.minor + " - "
                    + client.os.family + " " + client.os.major
                    + "." + client.os.minor;
        }

        List<String> existingDeviceDetails = userInfoEntity.getDeviceDetails();
        existingDeviceDetails.add(deviceDetails);
    }

    public List<UrlEntity> findAllExpiredUrls(LocalDate today) {
        return repository.findByExpiryDateBefore(today);
    }

    public void clearAllExpiredUrls(List<UrlEntity> urls) {
        repository.deleteAll(urls);
    }
}
