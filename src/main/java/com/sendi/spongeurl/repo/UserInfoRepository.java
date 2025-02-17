package com.sendi.spongeurl.repo;

import com.sendi.spongeurl.entity.UserInfoEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRepository extends CrudRepository<UserInfoEntity, Long> {
}
