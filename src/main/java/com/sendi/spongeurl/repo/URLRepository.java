package com.sendi.spongeurl.repo;

import com.sendi.spongeurl.entity.UrlEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface URLRepository extends CrudRepository<UrlEntity, Long> {
    List<UrlEntity> findAllByFullURL(String fullUrl);
}
