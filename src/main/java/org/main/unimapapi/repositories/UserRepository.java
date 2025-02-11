package org.main.unimapapi.repositories;

import org.main.unimapapi.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByLogin(String login);
    Optional<User> findByUsername(String username);

    Optional<User> getIdByEmail(String email);


}
