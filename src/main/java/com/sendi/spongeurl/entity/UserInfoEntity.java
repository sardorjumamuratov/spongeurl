package com.sendi.spongeurl.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("UserInfoEntity")
public class UserInfoEntity {
    @Id
    private Long id;

    private String address;
}
