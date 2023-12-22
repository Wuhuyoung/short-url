package com.han.shorturl.service.impl;

import com.han.shorturl.domain.UrlMap;
import com.han.shorturl.mapper.UrlMapMapper;
import com.han.shorturl.service.UrlMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

/**
 * @author han
 * @date 2023/12/7 08:58
 */
@Service
public class UrlMapServiceImpl implements UrlMapService {

    @Autowired
    private UrlMapMapper urlMapMapper;


    @Override
    public int insert(UrlMap urlMap) {
        return urlMapMapper.insertSelective(urlMap);
    }

    @Override
    public String getLongUrlByShortUrl(String shortUrl) {
        Example example = new Example(UrlMap.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("short_rul", shortUrl);
        UrlMap urlMap = urlMapMapper.selectOneByExample(example);
        if (urlMap != null) {
            return urlMap.getLongUrl();
        }
        return "";
    }
}
