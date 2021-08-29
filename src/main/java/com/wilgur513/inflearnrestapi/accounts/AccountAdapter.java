package com.wilgur513.inflearnrestapi.accounts;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class AccountAdapter extends User {
    private final Account account;

    public AccountAdapter(Account account) {
        super(account.getEmail(), account.getPassword(), getAuthorities(account));
        this.account = account;
    }

    private static Set<SimpleGrantedAuthority> getAuthorities(Account account) {
        return account.getRoles().stream().map(r -> "ROLE_" + r.name()).map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }

    public Account getAccount() {
        return account;
    }
}
