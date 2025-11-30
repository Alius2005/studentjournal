package org.example.studentjournal.services;

import org.example.studentjournal.DbManager;
import org.example.studentjournal.POJO.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private DbManager dbManager;

    public List<User> getAllUsers() {
        try {
            return dbManager.getUsers();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching users", e);
        }
    }

    public Optional<User> getUserById(int id) {
        return getAllUsers().stream().filter(u -> u.getId() == id).findFirst();
    }

    public void saveUser(User user) {
        try {
            if (user.getId() == 0) {
                dbManager.insertUser(user.getFirstName(), user.getLastName(), user.getMiddleName(),
                        user.getPasswordHash(), user.getRole(), user.getGroupId());
            } else {
                dbManager.updateUser(user.getId(), user.getFirstName(), user.getLastName(), user.getMiddleName(),
                        user.getPasswordHash(), user.getRole(), user.getGroupId());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error saving user", e);
        }
    }

    public void deleteUser(int id) {
        try {
            dbManager.deleteUser(id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting user", e);
        }
    }

    public Optional<User> findByUsername(String username) {
        try {
            User user = dbManager.findUserByUsername(username);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            throw new RuntimeException("Error finding user by username", e);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getFirstName() + " " + user.getLastName())
                .password(user.getPasswordHash())
                .roles(user.getRole())
                .build();
    }
}
