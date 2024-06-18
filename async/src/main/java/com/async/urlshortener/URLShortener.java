package com.async.urlshortener;

import java.util.Optional;

public interface URLShortener {

    Optional<String> shorten(String baseUrl);
    Optional<String> giveBaseUrl(String shortenedUrl);

}
