package com.async.urlshortener;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

public class URLShortenerImpl implements URLShortener {

    private final Map<String, String> baseUrlToShortUrlMap;
    private final Map<String, String> shortUrlToBaseUrlMap;

    private final Logger logger = Logger.getLogger(URLShortenerImpl.class.getName());

    private final Object lock = new Object();


    public URLShortenerImpl(Map<String, String> baseUrlToShortUrlMap, Map<String, String> shortUrlToBaseUrlMap) {
        this.baseUrlToShortUrlMap = baseUrlToShortUrlMap;
        this.shortUrlToBaseUrlMap = shortUrlToBaseUrlMap;
    }


    @Override
    public Optional<String> shorten(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            String warning = String.format("Base url %s is null ot empty", baseUrl);
            throw new IllegalArgumentException(warning);
        }

        String shortUrl = generateShortUrl(baseUrl);
        synchronized (lock) {
            if (baseUrlToShortUrlMap.containsKey(baseUrl)) {
                return Optional.of(baseUrlToShortUrlMap.get(baseUrl));
            }
            baseUrlToShortUrlMap.put(baseUrl, shortUrl);
            shortUrlToBaseUrlMap.put(shortUrl, baseUrl);
        }
        return Optional.of(shortUrl);
    }


    @Override
    public Optional<String> giveBaseUrl(String shortenedUrl) {
        if (shortenedUrl == null || shortenedUrl.isBlank()) {
            throw new IllegalArgumentException("shortenedUrl url is null or empty");
        }
        final String result;
        synchronized (lock) {
            result = shortUrlToBaseUrlMap.get(shortenedUrl);
        }
        return Optional.ofNullable(result);
    }

    private String generateShortUrl(String baseUrl) {
        return Base64.getUrlEncoder()
                .encodeToString(baseUrl.getBytes(StandardCharsets.UTF_8))
                .substring(0, 8);
    }
}
