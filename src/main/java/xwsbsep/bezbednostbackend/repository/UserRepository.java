package xwsbsep.bezbednostbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import xwsbsep.bezbednostbackend.model.User;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    User findByUsername(String username);
    @Query("select u from User u where u.id = ?1")
    User getUserById(UUID id);
}
