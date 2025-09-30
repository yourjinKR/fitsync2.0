package com.fitsync.domain.user.service;

import com.fitsync.domain.user.repository.UserRepository;
import com.fitsync.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true) // 데이터를 조회만 하는 경우 성능 최적화를 위해 readOnly=true 옵션을 사용합니다.
    public List<User> findAllUsers() {

        return userRepository.findAll();
    }

    @Transactional // 데이터에 변경이 일어나는 작업에는 Transactional을 붙여 데이터 일관성을 보장합니다.
    public User saveUser(User user) {

        return userRepository.save(user);
    }

    // 자신의 id 가져오기
    public User findByEmail(String email) {

        User user = userRepository.findByEmail(email).
                orElseThrow(() -> new IllegalArgumentException("해당 이메일과 일치하는 유저 못찾음 : " + email));

        return user;
    }
}
