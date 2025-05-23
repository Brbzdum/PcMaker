package ru.compshp.service;

import ru.compshp.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);
    User updateUser(User user);
    void deleteUser(Long id);
    Optional<User> getUserById(Long id);
    Optional<User> getUserByEmail(String email);
    Optional<User> getUserByUsername(String username);
    List<User> getAllUsers();
    List<User> getActiveUsers();
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    void activateUser(String activationCode);
    void changePassword(Long userId, String newPassword);
    void addRoleToUser(Long userId, String roleName);
    void removeRoleFromUser(Long userId, String roleName);
} 