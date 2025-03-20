package app.user.repository;

import app.user.model.User;
import app.user.model.UserRole;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsernameOrEmail(String username, String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findByRole(UserRole role);

    @Query("SELECT u FROM User u WHERE u.role <> :role")
    List<User> findAllByRoleNot(@Param("role") UserRole role);

    List<User> findByIsActiveTrue();

    List<User> findByIsActiveFalse();
}
