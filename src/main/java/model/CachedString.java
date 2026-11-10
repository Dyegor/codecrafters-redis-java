package model;

import java.time.LocalDateTime;

public record CachedString(String cachedValue, LocalDateTime expiresAt) {

}
