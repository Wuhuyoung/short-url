package com.han.shorturl.service;

import com.han.shorturl.common.ApiResponse;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author han
 * @date 2023/12/6 10:47
 */
public interface ShortUrlService {
    ApiResponse<String> createShortUrl(String url);

    RedirectView redirect(String shortUrl);
}
