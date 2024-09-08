package com.sendi.spongeurl.controller;

import com.sendi.spongeurl.dto.FullURL;
import com.sendi.spongeurl.dto.ShortURL;
import com.sendi.spongeurl.service.URLService;
import com.sendi.spongeurl.exception.InvalidURLException;
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
public class URLController {

    private final URLService urlService;

    public URLController(URLService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<Object> shortenURL(@RequestBody FullURL fullURL, HttpServletRequest request) {
        ShortURL shortURL = urlService.shortenURL(request, fullURL);
        UrlValidator urlValidator = new UrlValidator(new String[]{"ftp", "https", "http"});

        if (!urlValidator.isValid(fullURL.getValue())) {
            InvalidURLException error = new InvalidURLException("url", fullURL.getValue(), "Invalid URL");

            // returns a custom body with error message and bad request status code
            return ResponseEntity.badRequest().body(error);
        }
        return ResponseEntity.ok(shortURL);
    }

    @GetMapping("{shortURLValue}")
    public void redirectToFullURL(HttpServletResponse response, @PathVariable String shortURLValue) {
        try {
            FullURL fullUrl = urlService.getFullUrl(shortURLValue);
            response.sendRedirect(fullUrl.getValue());
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Url not found", e);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not redirect to the full url", e);
        }
    }
}
