package com.sendi.spongeurl.log;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Log4j2
@Service
@RequiredArgsConstructor
public class LogService {

    public void logException(Throwable ex) {
        log.debug("ERROR = {} CAUSE = {}", ex.getMessage(), ex.getCause());
        log.error(Arrays.toString(ex.getStackTrace()));
    }

}
