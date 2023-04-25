package com.geulkkoli.application.security;

import com.geulkkoli.application.user.AuthUser;
import com.geulkkoli.domain.user.User;
import com.geulkkoli.domain.user.service.UserService;
import com.geulkkoli.web.user.JoinFormDto;
import com.geulkkoli.web.user.edit.PasswordEditDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserSecurityServiceTest {

    @Autowired
    UserSecurityService userSecurityService;
    @Autowired
    UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;



    @Test
    void join() {
        JoinFormDto joinForm = new JoinFormDto();
        joinForm.setEmail("tako11@naver.com");
        joinForm.setUserName("tako11@naver.com");
        joinForm.setNickName("바나나11");
        joinForm.setPhoneNo("01012345631");
        joinForm.setGender("male");
        joinForm.setPassword("123qwe!@#");
        User user = userSecurityService.join(joinForm);

        assertThat(user.getRole()).isEqualTo(RoleEntity.of(Role.USER));
    }

    @Test
    @DisplayName("기존 비밀번호와 일치하는지 확인 테스트")
    void isPasswordVerification() {
        JoinFormDto joinForm = new JoinFormDto();
        joinForm.setEmail("tako1@naver.com");
        joinForm.setUserName("tako1@naver.com");
        joinForm.setNickName("바나나1");
        joinForm.setPhoneNo("01012345671");
        joinForm.setGender("male");
        joinForm.setPassword("123qwe!@#");
        User saveUser = userSecurityService.join(joinForm);


        PasswordEditDto passwordEditDto = new PasswordEditDto();
        passwordEditDto.setOldPassword("123qwe!@#");
        passwordEditDto.setNewPassword("abc123!@#");
        passwordEditDto.setVerifyPassword("abc123!@#");

        assertThat(userSecurityService.isPasswordVerification(saveUser, passwordEditDto)).isTrue();
    }

    @Test
    @DisplayName("비밀번호 업데이트 테스트")
    void updatePassword() {

        //given
        JoinFormDto joinForm = new JoinFormDto();
        joinForm.setEmail("tako1@naver.com");
        joinForm.setUserName("tako1@naver.com");
        joinForm.setNickName("바나나1");
        joinForm.setPhoneNo("01012345671");
        joinForm.setGender("male");
        joinForm.setPassword("123qwe!@#");
        userSecurityService.join(joinForm);


        PasswordEditDto passwordEditDto = new PasswordEditDto();
        passwordEditDto.setOldPassword("123qwe!@#");
        passwordEditDto.setNewPassword("abc123!@#");
        passwordEditDto.setVerifyPassword("abc123!@#");

        //when
        userSecurityService.updatePassword(1L, passwordEditDto);

        //then
        User updatePasswordUser = userService.findById(1L);

        assertThat(passwordEncoder.matches("abc123!@#", updatePasswordUser.getPassword())).isTrue();
    }

    @Test
    @DisplayName("회원 권한을 가진 유저가 올바르게 로그인 했을 때 회원 권한 인증 정보를 반환한다.")
    void Returns_membership_credentials_when_a_user_with_membership_privileges_is_correctly_logged_in() {
        JoinFormDto joinForm = new JoinFormDto();
        joinForm.setEmail("tako1@naver.com");
        joinForm.setUserName("tako1@naver.com");
        joinForm.setNickName("바나나1");
        joinForm.setPhoneNo("01012345671");
        joinForm.setGender("male");
        joinForm.setPassword("123qwe!@#");
        User saveUser = userSecurityService.join(joinForm);

        //when
        UserDetails user = userSecurityService.loadUserByUsername("tako1@naver.com");
        AuthUser authUser = (AuthUser) user;
        //then

        assertAll(() -> assertThat(authUser.getAuthorities()).hasSize(1),
                () -> assertThat(Objects.requireNonNull(authUser.getAuthorities()).iterator().next().getAuthority()).isEqualTo("ROLE_USER"),
                () -> assertThat(authUser.getUsername()).isEqualTo("tako1@naver.com"),
                () -> assertThat(authUser.getPassword()).isEqualTo(saveUser.getPassword()),
                () -> assertThat(authUser.isAccountNonExpired()).isTrue(),
                () -> assertThat(authUser.isAccountNonLocked()).isTrue(),
                () -> assertThat(authUser.isCredentialsNonExpired()).isTrue(),
                () -> assertThat(authUser.isEnabled()).isTrue());
    }

    @Test
    @DisplayName("존재하지 않는 이메일일 경우 usernameNotFoundException 예외가 발생한다.")
    void exception_thrown_for_non_existent_emails() {

        assertThrows(UsernameNotFoundException.class, () -> userSecurityService.loadUserByUsername("tako11@naver.com"));

    }

    @Test
    @DisplayName("이메일은 존재하지만 권한 정보가 존재하지 않을 경우 AuthenticationException 예외가 발생한다.")
    void exception_thrown_for_non_existent_permission() {
        JoinFormDto joinForm = new JoinFormDto();
        joinForm.setEmail("tako1@naver.com");
        joinForm.setUserName("tako1@naver.com");
        joinForm.setNickName("바나나1");
        joinForm.setPhoneNo("01012345671");
        joinForm.setGender("male");
        joinForm.setPassword("123qwe!@#");
        User saveUser = userSecurityService.join(joinForm);

        assertThrows(AuthenticationException.class, () -> userSecurityService.loadUserByUsername("tako1@naver.com"));
    }
}