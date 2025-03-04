package com.sendi.spongeurl.repo;

import com.sendi.spongeurl.entity.UrlEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface URLRepository extends CrudRepository<UrlEntity, Long> {
    List<UrlEntity> findAllByFullURL(String fullUrl);

    Optional<UrlEntity> findByShortURL(String shortUrl);

    boolean existsByShortURL(String shortURL);

    List<UrlEntity> findByExpiryDateBefore(LocalDate expiryDateBefore);
}
