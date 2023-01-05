package com.yoojin.restapiwithspring.account;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccountServiceTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    AccountService accountService;
    @Autowired
    AccountRepository accountRepository;
    @Test
    public void findByUserName() {
        //given
        String email = "yoojinlee.dev@gmail.com";
        String password = "yoojin";
        Account account = Account.builder()
                .email(email)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();

        this.accountRepository.save(account);

        //when
        UserDetailsService userDetailsService = accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        //then
        assertThat(userDetails.getPassword()).isEqualTo(account.getPassword());
    }

    @Test
    public void findByUserName_error() {
        //given
        String email = "wrongMail@gmail.com";
        expectedException.expect(UsernameNotFoundException.class);
        expectedException.expectMessage(Matchers.containsString(email));
        //when
        UserDetails userDetails = accountService.loadUserByUsername(email);
        //then
    }
}