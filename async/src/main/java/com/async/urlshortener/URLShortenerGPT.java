package com.async.urlshortener;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class URLShortenerGPT {
    private static final String BASE_URL = "http://short.url/";
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int BASE = CHARACTERS.length();
    private static final int MIN_LENGTH = 4;
    private static final int MAX_LENGTH = 8;

    private ConcurrentHashMap<String, String> keyToUrlMap;
    private ConcurrentHashMap<String, String> urlToKeyMap;
    private AtomicLong counter;

    public URLShortenerGPT() {
        keyToUrlMap = new ConcurrentHashMap<>();
        urlToKeyMap = new ConcurrentHashMap<>();
        counter = new AtomicLong(0);
    }

    public synchronized String shortenURL(String longURL) {
        if (urlToKeyMap.containsKey(longURL)) {
            return BASE_URL + urlToKeyMap.get(longURL);
        }

        String key;
        do {
            long id = counter.getAndIncrement();
            key = encode(id);
        } while (key.length() < MIN_LENGTH || key.length() > MAX_LENGTH);

        keyToUrlMap.put(key, longURL);
        urlToKeyMap.put(longURL, key);

        return BASE_URL + key;
    }

    public String expandURL(String shortURL) {
        String key = shortURL.replace(BASE_URL, "");
        return keyToUrlMap.get(key);
    }

    private String encode(long id) {
        StringBuilder sb = new StringBuilder();
        while (id > 0) {
            sb.append(CHARACTERS.charAt((int) (id % BASE)));
            id /= BASE;
        }
        return sb.reverse().toString();
    }

    public static void main(String[] args) {
        URLShortenerGPT urlShortener = new URLShortenerGPT();

        // Пример использования
        String shortURL = urlShortener.shortenURL("https://www.example.com");
        System.out.println("Short URL: " + shortURL);

        String longURL = urlShortener.expandURL(shortURL);
        System.out.println("Long URL: " + longURL);
    }
}
