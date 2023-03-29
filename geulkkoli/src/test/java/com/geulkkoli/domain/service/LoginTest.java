package com.geulkkoli.domain.service;

import com.geulkkoli.domain.User;
import com.geulkkoli.infrastructure.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class LoginTest {

    @Autowired
    LoginService loginService;
    @Autowired
    UserRepository userRepository;

    @PostConstruct
    void init(){
        userRepository.save(User.builder()
                .userId("kkk")
                .userName("김")
                .nickName("바나나")
                .password("1234")
                .build());
    }

    @Test
    @DisplayName("로그인테스트")
    void loginTest(){
        //given

        //when
        User loginUser = loginService.login("kkk", "1234");

        //then
        assertThat(loginUser.getUserId()).isEqualTo("kkk");
    }

    @Test
    @DisplayName("로그인실패시_실패예외를_던진다")
    void throwErrorWhenLoginFailedTest(){
        //given

        //when

        //then
        assertThatThrownBy(() ->loginService.login("kkk1", "1234")).isInstanceOf(LoginFailureException.class);
    }

}
