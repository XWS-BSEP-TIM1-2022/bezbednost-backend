package xwsbsep.bezbednostbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xwsbsep.bezbednostbackend.model.User;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
