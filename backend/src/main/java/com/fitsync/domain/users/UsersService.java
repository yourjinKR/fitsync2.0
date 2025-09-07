package com.fitsync.domain.users;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어 의존성을 주입합니다.
public class UsersService {

    private final UsersRepository usersRepository;

    @Transactional(readOnly = true) // 데이터를 조회만 하는 경우 성능 최적화를 위해 readOnly=true 옵션을 사용합니다.
    public List<Users> findAllUsers() {
        return usersRepository.findAll();
    }

    @Transactional // 데이터에 변경이 일어나는 작업에는 Transactional을 붙여 데이터 일관성을 보장합니다.
    public Users saveUser(Users user) {
        return usersRepository.save(user);
    }
}
