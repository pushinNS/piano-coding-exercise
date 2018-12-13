package io.piano.demo.security;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/*
 * Temporary token storage implementation
 * Map Values are just dummy values
 * */
@Component
@Scope(value = "singleton")
public class InMemoryTokenStore {

    private static final ConcurrentMap<String, Boolean> storage = new ConcurrentHashMap<>();

    public void revokeToken(String token) {
        storage.remove(token);
    }

    public void authorizeToken(String token) {
        storage.put(token, true);
    }

    public boolean isStored(String token) {
        return storage.containsKey(token);
    }
}
