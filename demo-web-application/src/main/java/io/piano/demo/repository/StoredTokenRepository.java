package io.piano.demo.repository;

import io.piano.demo.model.StoredToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface StoredTokenRepository extends JpaRepository<StoredToken, Long> {

    boolean existsByToken(String token);

    @Transactional
    void removeByToken(String token);
}
