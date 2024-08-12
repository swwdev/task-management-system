package com.example.taskmanagment.services;


import com.example.taskmanagment.dto.user.CustomUserDetails;
import com.example.taskmanagment.models.User;
import com.example.taskmanagment.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.taskmanagment.utils.Constants.USER;
import static com.example.taskmanagment.utils.ResponseUtils.NOT_FOUND;
import static com.example.taskmanagment.utils.ResponseUtils.getMessage;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(getMessage(NOT_FOUND, USER)));
        return new CustomUserDetails(user);
    }
}
