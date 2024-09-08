package com.sendi.spongeurl.service;

import com.sendi.spongeurl.dto.FullURL;
import com.sendi.spongeurl.dto.ShortURL;
import com.sendi.spongeurl.common.URLUtil;
import com.sendi.spongeurl.common.ShorteningUtil;
import com.sendi.spongeurl.entity.UrlEntity;
import com.sendi.spongeurl.repo.URLRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class URLService {
    private final URLRepository urlRepository;

    public URLService(URLRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    private UrlEntity get(Long id) {
        UrlEntity urlEntity = urlRepository.findById(id);
        return urlEntity;
    }

    public FullURL getFullUrl(String shortenString) {
        Long id = ShorteningUtil.strToId(shortenString);
        return new FullURL(this.get(id).getFullURL());
    }

    private UrlEntity save(FullURL fullUrl) {
        return urlRepository.save(new UrlEntity(fullUrl.getValue()));
    }

    public ShortURL shortenURL(HttpServletRequest request, FullURL fullURL) {
        String baseURL = URLUtil.getBaseUrl(request.getRequestURL().toString());

        List<UrlEntity> savedUrls = checkFullURLExists(fullURL);

        UrlEntity savedUrl = null;

        if (savedUrls.isEmpty()) {
            savedUrl = save(fullURL);
        } else {
            savedUrl = savedUrls.get(0);
        }

        String shortUrlText = ShorteningUtil.idToStr(savedUrl.getId());

        return new ShortURL(baseURL + shortUrlText);
    }

    private List<UrlEntity> checkFullURLExists(FullURL fullUrl) {
        return urlRepository.findByFullUrl(fullUrl.getValue());
    }
}
