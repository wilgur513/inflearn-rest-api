package com.wilgur513.inflearnrestapi.configs;

import com.wilgur513.inflearnrestapi.accounts.Account;
import com.wilgur513.inflearnrestapi.accounts.AccountRole;
import com.wilgur513.inflearnrestapi.accounts.AccountService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class AppConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public ApplicationRunner runner(AccountService accountService) {
        return (args) -> {
            Account account = Account.builder()
                    .email("wilgur513@email.com")
                    .password("wilgur513")
                    .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                    .build();
            accountService.saveAccount(account);
        };
    }
}
