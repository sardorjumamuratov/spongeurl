package com.sendi.spongeurl.entity.mapper;

import com.sendi.spongeurl.dto.UrlCreateRequest;
import com.sendi.spongeurl.entity.UrlEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UrlMapper {

}
