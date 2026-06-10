package com.example.project.service;

import com.example.project.dto.request.UpdateUserRequest;
import com.example.project.dto.response.PageResponse;
import com.example.project.dto.response.UserResponse;

public interface UserService {
    PageResponse<UserResponse> getAllUsers(int page, int size, String keyword);
    UserResponse getUserById(Long id);
    UserResponse updateUser(Long id, UpdateUserRequest request);
    void deleteUser(Long id);
}