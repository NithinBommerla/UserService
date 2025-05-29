package dev.nithin.userservice.repository;

import dev.nithin.userservice.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByTokenValueAndIsDeletedAndExpiresAtGreaterThan(String tokenValue, boolean isDeleted, Date date);

    Optional<Token> findByTokenValueAndIsDeleted(String tokenValue, boolean isDeleted);

}
