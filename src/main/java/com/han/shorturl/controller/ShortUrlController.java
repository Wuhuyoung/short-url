package com.han.shorturl.controller;

import com.han.shorturl.common.ApiResponse;
import com.han.shorturl.service.ShortUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author han
 * @date 2023/12/6 09:39
 */
@RestController
public class ShortUrlController {

    @Autowired
    private ShortUrlService shortUrlService;

    @GetMapping("/createShortUrl")
    public ApiResponse<String> createShortUrl(String url) {
        return shortUrlService.createShortUrl(url);
    }

    @GetMapping(value="/{shortUrl}")
    public RedirectView redirect (@PathVariable String shortUrl) {
        return shortUrlService.redirect(shortUrl);
    }


}
