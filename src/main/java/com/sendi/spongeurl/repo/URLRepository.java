package com.sendi.spongeurl.repo;

import com.sendi.spongeurl.entity.UrlEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface URLRepository extends CrudRepository<UrlEntity, Long> {
    List<UrlEntity> findAllByFullURL(String fullUrl);
//    public static final String HASH_KEY = "UrlEntity";
//    private final RedisTemplate template;
//
//    public URLRepository(RedisTemplate template) {
//        this.template = template;
//    }
//
//    public UrlEntity save(UrlEntity urlEntity) {
//        template.opsForHash().put(HASH_KEY, urlEntity.getId(), urlEntity);
//        return urlEntity;
//    }
//
//    public UrlEntity findById(Long id) {
//        return (UrlEntity) template.opsForHash().get(HASH_KEY, id);
//    }
//
//    public List<UrlEntity> findByFullUrl(String fullUrl) {
//        return (List<UrlEntity>) template.opsForHash().get(HASH_KEY, fullUrl);
//    }
}
