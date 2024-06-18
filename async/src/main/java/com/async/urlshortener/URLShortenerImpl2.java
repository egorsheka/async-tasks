package com.async.urlshortener;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

public class URLShortenerImpl2 implements URLShortener {

    // todo get from config
    private final String BASE_URL = "https://short.url/";
    private final String CHARACTERS = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM0123456789";
    private final int BASE = CHARACTERS.length();
    private long counter = 1;

    private final Map<String, String> baseUrlToShortUrlMap;
    private final Map<String, String> shortUrlToBaseUrlMap;

    private final Object lock = new Object();

    @Override
    public Optional<String> shorten(String baseUrl) {
        if (baseUrl == null || baseUrl.isEmpty()){
            throw new IllegalArgumentException("base url is null or empty");
        }

        if (baseUrlToShortUrlMap.containsKey(baseUrl)){
            return Optional.of(baseUrl);
        }

        final String shortUrl;
        synchronized (lock){
            // double check
            if (baseUrlToShortUrlMap.containsKey(baseUrl)){
                return Optional.of(baseUrl);
            }

            shortUrl = encode(counter);
            counter++;

            baseUrlToShortUrlMap.put(baseUrl, shortUrl);
            shortUrlToBaseUrlMap.put(shortUrl, baseUrl);
        }
        return Optional.of(shortUrl);
    }



    @Override
    public Optional<String> giveBaseUrl(String shortenedUrl) {
        return Optional.empty();
    }


    private String encode(long id) {
        StringBuilder sb = new StringBuilder();
        while (id > 0){
            sb.append(CHARACTERS.charAt((int)id % BASE));
            id = id / BASE;
        }
        return sb.toString();
    }
}
