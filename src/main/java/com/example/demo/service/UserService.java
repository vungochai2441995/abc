package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.mapper.UserMapper;
import com.example.demo.model.request.CreateUserRequest;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public UserDto  createUser(CreateUserRequest createUserRequest) {
        return null;
    }

    public UserDto updateUser(CreateUserRequest createUserRequest, int id) {
        return null;
    }

    public UserDto deleteUser(int id) {
        return null;
    }

    public UserDto getUserById(int id) {
        Optional<User> user = userRepository.findById(id);
        return UserMapper.toUserDto(user.get());
    }

    public List<UserDto> getListUser(String keyword, int page) {
        return null;
    }
}
