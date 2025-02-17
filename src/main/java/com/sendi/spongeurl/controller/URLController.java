package com.sendi.spongeurl.controller;

import com.sendi.spongeurl.dto.OneFieldLongResponse;
import com.sendi.spongeurl.dto.UrlCreateRequest;
import com.sendi.spongeurl.dto.ShortURL;
import com.sendi.spongeurl.service.URLService;
import com.sendi.spongeurl.exception.custom.InvalidURLException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/url")
public class URLController {

    private final URLService urlService;

    public URLController(URLService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<Object> shortenURL(@RequestBody UrlCreateRequest urlCreateRequest, HttpServletRequest request) {

        UrlValidator urlValidator = new UrlValidator(new String[]{"ftp", "https", "http"});

        if (!urlValidator.isValid(urlCreateRequest.getFullUrlValue())) {
            throw new InvalidURLException("Invalid URL entered");
        }

        ShortURL shortURL = urlService.shortenURL(request, urlCreateRequest);

        return ResponseEntity.ok(shortURL);
    }

    @GetMapping("{shortURLValue}")
    public void redirectToFullURL(HttpServletRequest request, HttpServletResponse response, @PathVariable String shortURLValue) {
        try {
            String fullUrl = urlService.getFullUrl(shortURLValue, request);
            response.sendRedirect(fullUrl);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Url not found", e);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not redirect to the full url", e);
        }
    }

    @GetMapping("/clicks/{shortURLValue}")
    public ResponseEntity<OneFieldLongResponse> getNumberOfClicks(HttpServletRequest request, @PathVariable String shortURLValue) {
        return ResponseEntity.ok(urlService.getClicks(shortURLValue, request));
    }
}
