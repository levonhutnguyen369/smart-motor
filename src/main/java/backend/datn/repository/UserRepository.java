package backend.datn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import backend.datn.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}