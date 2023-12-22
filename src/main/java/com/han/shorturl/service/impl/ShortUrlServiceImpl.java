package com.han.shorturl.service.impl;

import com.google.common.hash.Hashing;
import com.han.shorturl.common.ApiResponse;
import com.han.shorturl.domain.UrlMap;
import com.han.shorturl.service.ShortUrlService;
import com.han.shorturl.service.UrlMapService;
import com.han.shorturl.utils.BloomFilterUtil;
import com.han.shorturl.utils.DecimalToBase62Util;
import com.han.shorturl.utils.RedisClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author han
 * @date 2023/12/6 10:48
 */
@Slf4j
@Service
public class ShortUrlServiceImpl implements ShortUrlService {

    @Value("${han.url}")
    private String shortUrl;

    @Autowired
    private RedisClientUtil redisClientUtil;

    @Autowired
    private BloomFilterUtil bloomFilterUtil;

    @Autowired
    private UrlMapService urlMapService;

    private static final String SHORT_URL_KEY = "url:short:key";
    private static final String URL_CACHE_KEY = "url:cache:key:";

    @Override
    public ApiResponse<String> createShortUrl(String url) {
        // 获得 10 进制
        long hash = Hashing.murmur3_32_fixed().hashUnencodedChars(url).padToLong();
        // 10 进制转 62 进制
        String base62 = DecimalToBase62Util.decimalToBase62(hash);
        StringBuffer stringBuffer = new StringBuffer(base62);
        checkBase62(stringBuffer);
        base62 = stringBuffer.toString();

        // 存入数据库
        UrlMap urlMap = new UrlMap();
        urlMap.setShortUrl(base62);
        urlMap.setLongUrl(url);
        int insert = urlMapService.insert(urlMap);
        if (insert > 0) {
            if (!redisClientUtil.exists(SHORT_URL_KEY)) {
                bloomFilterUtil.bfreserve(SHORT_URL_KEY, 0.01, 100);
            }
            bloomFilterUtil.bfadd(SHORT_URL_KEY, base62);
            redisClientUtil.add(URL_CACHE_KEY + base62, url);
            return ApiResponse.ok(shortUrl + base62);
        }
        return ApiResponse.ok();

    }


    @Override
    public RedirectView redirect(String shortUrl) {
        // 从 Redis 中取出长链地址
        String redirectUrl = redisClientUtil.get(URL_CACHE_KEY + shortUrl);
        if (StringUtils.isBlank(redirectUrl)) {
            redirectUrl = urlMapService.getLongUrlByShortUrl(shortUrl);
        }
        // 重定向
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(redirectUrl);
        return redirectView;
    }

    /**
     * 判断短链的唯一性
     * @param base62
     */
    private void checkBase62(StringBuffer base62) {
        Boolean bfexists = bloomFilterUtil.bfexists(SHORT_URL_KEY, base62.toString());
        // 考虑到布隆过滤器存在误判问题，如果返回存在，查询数据库确定是否真的存在
        if (bfexists) {
            String longUrl = urlMapService.getLongUrlByShortUrl(base62.toString());
            if (StringUtils.isNoneBlank(longUrl)) {
                base62.append(RandomStringUtils.random(1));
                checkBase62(base62);
            }
        }
    }

}
