package com.graduation.platform.service;

import com.graduation.platform.model.entity.User;
import com.graduation.platform.model.dto.UserDTO;

import java.util.List;

public interface UserService {
    User register(UserDTO userDTO);
    User login(String username, String password);
    User getUserById(Long id);
    List<User> getAllUsers();
    void updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
}