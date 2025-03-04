package com.sendi.spongeurl.repo;

import com.sendi.spongeurl.entity.UserInfoEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoRepository extends CrudRepository<UserInfoEntity, Long> {
    Optional<UserInfoEntity> findByAddress(String ip);
}
