package ru.domclick.dryzhov.domain;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Table
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
public class Client {
    @Id
    @NonNull
    private String username;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "client")
    private List<Account> accounts = new ArrayList<>();

    public void addAccount(Account account) {
        account.setClient(this);
        accounts.add(account);
    }
}
