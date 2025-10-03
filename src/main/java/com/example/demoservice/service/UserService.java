package com.example.demoservice.service;

import com.example.demoservice.dto.User;
import com.example.demoservice.dto.UserCreateRequest;
import com.example.demoservice.dto.UserUpdateRequest;
import com.example.demoservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(UserCreateRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setEnabled(request.isEnabled());
        user.setRole(request.getRole());
        user.setDeleted(false);
        return userRepository.save(user);
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id)
                .filter(user -> !user.isDeleted());
    }

    public boolean deleteUserById(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent() && !userOpt.get().isDeleted()) {
            User user = userOpt.get();
            user.setDeleted(true);
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }

    public List<User> findAllUsers() {
        return userRepository.findAll().stream()
                .filter(user -> !user.isDeleted())
                .toList();
    }

    public Optional<User> updateUser(Long id, UserUpdateRequest request) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent() && !userOpt.get().isDeleted()) {
            User user = userOpt.get();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setPhone(request.getPhone());
            user.setAddress(request.getAddress());
            user.setEnabled(request.isEnabled());
            user.setRole(request.getRole());
            userRepository.save(user);
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }
}
