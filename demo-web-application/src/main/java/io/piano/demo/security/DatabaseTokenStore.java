package io.piano.demo.security;

import io.piano.demo.model.StoredToken;
import io.piano.demo.repository.StoredTokenRepository;
import org.springframework.stereotype.Component;

/**
 * Token store is intended to manage token lifecycle using logout endpoint
 */
@Component
public class DatabaseTokenStore {

    private final StoredTokenRepository storage;

    public DatabaseTokenStore(StoredTokenRepository storage) {
        this.storage = storage;
    }

    public void revokeToken(String token) {
        storage.removeByToken(token);
    }

    public void storeToken(String token) {
        final StoredToken tokenToCreate = new StoredToken(token);
        storage.save(tokenToCreate);
    }

    public boolean isStored(String token) {
        return storage.existsByToken(token);
    }

}
