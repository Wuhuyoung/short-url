package com.han.shorturl.service;

import com.han.shorturl.domain.UrlMap;

/**
 * @author han
 * @date 2023/12/7 08:57
 */
public interface UrlMapService {

    int insert(UrlMap urlMap);

    String getLongUrlByShortUrl(String shortUrl);

}
