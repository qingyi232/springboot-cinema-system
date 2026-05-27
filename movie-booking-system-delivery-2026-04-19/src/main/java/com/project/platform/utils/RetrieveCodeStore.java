package com.project.platform.utils;

import com.project.platform.exception.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class RetrieveCodeStore {

    private final Map<String, CodeRecord> codeStore = new ConcurrentHashMap<>();

    @Value("${security.retrieve-code.expire-seconds:300}")
    private long expireSeconds;

    public String generateCode(String type, String tel) {
        validateBaseParams(type, tel);
        clearExpired();
        String code = String.format("%06d", ThreadLocalRandom.current().nextInt(100000, 1000000));
        codeStore.put(buildKey(type, tel), new CodeRecord(code, Instant.now().plusSeconds(expireSeconds).toEpochMilli()));
        return code;
    }

    public void verifyCode(String type, String tel, String code) {
        validateBaseParams(type, tel);
        if (code == null || code.isBlank()) {
            throw new CustomException("验证码不能为空");
        }
        CodeRecord record = codeStore.get(buildKey(type, tel));
        if (record == null) {
            throw new CustomException("请先发送验证码");
        }
        if (record.expireAt < System.currentTimeMillis()) {
            codeStore.remove(buildKey(type, tel));
            throw new CustomException("验证码已过期，请重新发送");
        }
        if (!record.code.equals(code.trim())) {
            throw new CustomException("验证码错误");
        }
        codeStore.remove(buildKey(type, tel));
    }

    private void validateBaseParams(String type, String tel) {
        if (type == null || type.isBlank()) {
            throw new CustomException("用户类型不能为空");
        }
        if (tel == null || tel.isBlank()) {
            throw new CustomException("手机号不能为空");
        }
    }

    private String buildKey(String type, String tel) {
        return type.trim() + ":" + tel.trim();
    }

    private void clearExpired() {
        long now = System.currentTimeMillis();
        codeStore.entrySet().removeIf(entry -> entry.getValue().expireAt < now);
    }

    private record CodeRecord(String code, long expireAt) {
    }
}
