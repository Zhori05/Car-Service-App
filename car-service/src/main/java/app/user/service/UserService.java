package app.user.service;

import app.Exception.DomainException;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.repository.UserRepository;
import app.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public User register(RegisterRequest registerRequest) {

        Optional<User> optionUser = userRepository.findByUsernameOrEmail(registerRequest.getUsername(),registerRequest.getEmail());
        if (optionUser.isPresent()) {
            throw new DomainException("Username with this email or username already exist.");
        }

        User user = userRepository.save(initializeUser(registerRequest));

        log.info("Successfully create new user account for username [%s] and id [%s]".formatted(user.getUsername(), user.getId()));

        return user;
    }

    private User initializeUser(RegisterRequest registerRequest) {

        return User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .phoneNumber(registerRequest.getPhoneNumber())
                .role(UserRole.USER)
                .isActive(true)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
    }
    public User getById(UUID id) {

        return userRepository.findById(id).orElseThrow(() -> new DomainException("User with id [%s] does not exist.".formatted(id)));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username).orElseThrow(() -> new DomainException("Потребител с това име не съществува."));

        return new AuthenticationMetadata(user.getId(), username, user.getPassword(), user.getRole(), user.isActive());
    }

    @Cacheable("users")
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }
    @Cacheable("users")
    public List<User> getActiveUsers() {

        return userRepository.findByIsActiveTrue();
    }

    @Cacheable("users")
    public List<User> getAllMechanics() {
        return userRepository.findByRole(UserRole.MECHANIC); // Връща само потребители с роля MECHANIC
    }
    @Cacheable("users")
    public List<User> getAllBasicUsers() {
        return userRepository.findByRole(UserRole.USER); // Връща само потребители с роля USER
    }
    @Cacheable("users")
    public List<User> getAllNonMechanics() {
        return userRepository.findAllByRoleNot(UserRole.MECHANIC);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void switchRole(UUID userId) {

        User user = getById(userId);

        if (user.getRole() == UserRole.USER) {
            user.setRole(UserRole.ADMIN);
        }else if (user.getRole() == UserRole.MECHANIC){
            user.setRole(UserRole.ADMIN);
        }else {
            user.setRole(UserRole.USER);
        }

        userRepository.save(user);
    }

    @CacheEvict(value = "addMechanic", allEntries = true)
    public void switchMechanic(UUID userId) {

        User mechanicUser = getById(userId);

        if (mechanicUser.getRole() == UserRole.USER) {
            mechanicUser.setRole(UserRole.MECHANIC);
        } else {
            mechanicUser.setRole(UserRole.USER);
        }

        userRepository.save(mechanicUser);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void switchStatus(UUID userId) {

        User user = getById(userId);

        // НАЧИН 1:
//        if (user.isActive()){
//            user.setActive(false);
//        } else {
//            user.setActive(true);
//        }

        // false -> true
        // true -> false
        user.setActive(!user.isActive());
        userRepository.save(user);
    }



    @Cacheable("users")
    public List<User> getNotActiveUsers() {

        return userRepository.findByIsActiveFalse();
    }
}