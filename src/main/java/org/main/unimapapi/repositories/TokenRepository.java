package org.main.unimapapi.repositories;

import org.main.unimapapi.entities.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, Long> {
    Optional<TokenEntity> findByRefreshToken(String refreshToken);


    @Query("""
        select t from TokenEntity t 
        where t.user.id = :userId 
        and t.revoked = false 
        and t.expiryDate > :now
        """)
    List<TokenEntity> findAllValidTokensByUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);



    @Modifying
    @Query("""
        update TokenEntity t 
        set t.revoked = true 
        where t.user.id = :userId 
        and t.revoked = false
        """)
    void revokeAllUserTokens(@Param("userId") Long userId);


    @Modifying
    @Query("""
    delete from TokenEntity t 
    where t.expiryDate < :cutoffDate 
    and t.revoked = true
    """)
    int deleteExpiredTokens(@Param("cutoffDate") LocalDateTime cutoffDate);






}