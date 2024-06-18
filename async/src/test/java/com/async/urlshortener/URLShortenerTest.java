package com.async.urlshortener;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class URLShortenerTest {

    URLShortener urlShortener = new URLShortenerImpl(new HashMap<>(), new HashMap<>());


    //1. What are the main functions the service should support? Just shortening and retrieving the original URL?
    //2. Should the data be stored only in memory?
    //3. Is concurrent access expected on the service?
    //4. Are there any requirements for the algorithm used in service (incremental identifiers, UUID, hash-based) ?
    //5. Are there any specific constraints or limitations to consider, such as the maximum length of the original URL?
    //6. Is there any URL validation?
    //7. How should the service handle errors, such as null or empty strings?

    // shorten_withValidInput_shouldReturnShortString
    // shorten_withNullInput_shouldThrowIllegalArgumentException
    // shorten_withEmptyInput_shouldThrowIllegalArgumentException
    // shorten_withRepeatedUrl_shouldReturnTheSameShortUrl

    // giveBaseUrl_withValidInput_shouldReturnBaseString
    // giveBaseUrl_withNullInput_shouldThrowIllegalArgumentException
    // giveBaseUrl_withEmptyInput_shouldThrowIllegalArgumentException
    // giveBaseUrl_withNotExistUrl_shouldReturnEmptyOptional


    @Test
    public void shorten_withValidInput_shouldReturnShortString() {
        final Optional<String> shorten = urlShortener.shorten("http://google.com");
        assertTrue(shorten.isPresent());
        assertEquals(8, shorten.get().length());
    }

    @Test
    public void shorten_withNullInput_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, ()-> urlShortener.shorten(null));
    }

    @Test
    public void shorten_withEmptyInput_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, ()-> urlShortener.shorten(""));
    }

    @Test
    public void shorten_withRepeatedUrl_shouldReturnTheSameShortUrl() {
        final String url = "http://google.com/some-vary-big-link-and-a-lot-of-information";
        final Optional<String> shorten = urlShortener.shorten(url);
        final Optional<String> repeatedShorten = urlShortener.shorten(url);

        assertTrue(shorten.isPresent());
        assertEquals(8, shorten.get().length());
        assertTrue(repeatedShorten.isPresent());
        assertEquals(shorten.get(), repeatedShorten.get());
    }

    @Test
    public void giveBaseUrl_withValidInput_shouldReturnBaseString() {
        final String baseUrl = "http://google.com/some-link";
        final Optional<String> shorten = urlShortener.shorten(baseUrl);

        final Optional<String> givenBaseUrl = urlShortener.giveBaseUrl(shorten.get());

        assertTrue(givenBaseUrl.isPresent());
        assertEquals(baseUrl, givenBaseUrl.get());
    }

    @Test
    public void giveBaseUrl_withNullInput_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, ()-> urlShortener.giveBaseUrl(null));
    }

    @Test
    public void giveBaseUrl_withEmptyInput_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, ()-> urlShortener.giveBaseUrl(""));
    }

    @Test
    public void giveBaseUrl_withNotExistUrl_shouldReturnEmptyOptional() {
        final Optional<String> givenBaseUrl = urlShortener.giveBaseUrl("someLink");
        assertTrue(givenBaseUrl.isEmpty());
    }

}