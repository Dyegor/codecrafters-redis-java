package model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CachedString {

    private final String cachedValue;

    private final LocalDateTime expiresAt;

    public CachedString(String cachedValue, LocalDateTime expiresAt) {
        this.cachedValue = cachedValue;
        this.expiresAt = expiresAt;
    }

    public String getCachedValue() {
        return cachedValue;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
}
