package com.wilgur513.inflearnrestapi.accounts;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter @Setter @EqualsAndHashCode(of={"id"})
@Builder @NoArgsConstructor @AllArgsConstructor
public class Account {
    @Id @GeneratedValue @Column(unique = true)
    private Integer id;
    private String email;
    private String password;
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<AccountRole> roles;
}
