package com.sendi.spongeurl.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.net.MalformedURLException;
import java.net.URL;

public class URLUtil {
    private URLUtil() {
    }

    public static String getBaseUrl(String url) {
        try {
            URL reqUrl = new URL(url);
            String protocol = reqUrl.getProtocol();
            String host = reqUrl.getHost();
            int port = reqUrl.getPort();

            if (port == -1) {
                return String.format("%s://%s/", protocol, host);
            } else {
                return String.format("%s://%s:%d/", protocol, host, port);
            }
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request url is invalid", e);
        }

    }

}
