package com.sendi.spongeurl.repo;

import com.sendi.spongeurl.entity.UrlEntity;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class URLRepository {
    public static final String HASH_KEY = "UrlEntity";
    private RedisTemplate template;

    public UrlEntity save(UrlEntity urlEntity) {
        template.opsForHash().put(HASH_KEY, urlEntity.getId(), urlEntity);
        return urlEntity;
    }

    public UrlEntity findById(Long id) {
        return (UrlEntity) template.opsForHash().get(HASH_KEY, id);
    }

    public List<UrlEntity> findByFullUrl(String fullUrl) {
        return (List<UrlEntity>) template.opsForHash().get(HASH_KEY, fullUrl);
    }



}
