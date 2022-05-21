package com.zlin.idempotent.controller;

import com.zlin.idempotent.enums.CacheKey;
import com.zlin.idempotent.model.Result;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author zlin
 * @date 20220521
 */
@RestController
@RequestMapping("token")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenController {

    private final StringRedisTemplate redisTemplate;
    private final RedissonClient redisson;

    @GetMapping
    public Result getIdempotentToken(HttpSession session) {
        String token = UUID.randomUUID().toString();
        // 这里的key看需求的粒度，若只防止用户在同一个浏览器上重复提交，在读个浏览器上算正常业务时，可使用sessionId
        redisTemplate.opsForValue().set(getTokenCacheKey(session.getId()),
                token, 600, TimeUnit.SECONDS);
        return Result.ok(token);
    }

    public void checkIdempotentToken(HttpServletRequest request) {
        String sessionId = request.getSession().getId();
        String token = request.getHeader("_idempotentToken");
        RLock lock = redisson.getLock(CacheKey.ORDER_TOKEN_LOCK.append(sessionId));
        lock.lock(5, TimeUnit.SECONDS);
        try {
            String orderTokenKey = getTokenCacheKey(sessionId);
            String cacheToken = redisTemplate.opsForValue().get(orderTokenKey);
            if (StringUtils.isBlank(cacheToken) || !cacheToken.equals(token)) {
                throw new RuntimeException("token不存在");
            }
            redisTemplate.delete(orderTokenKey);
        } finally {
            lock.unlock();
        }
    }

    private String getTokenCacheKey(String sessionId) {
        return CacheKey.ORDER_TOKEN.append(sessionId);
    }

}
