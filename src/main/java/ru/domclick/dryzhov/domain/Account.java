package ru.domclick.dryzhov.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Table
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
public class Account {
    @Id
    @NonNull
    private String account;

    @ManyToOne(fetch = FetchType.LAZY)
    private Client client;

    private BigDecimal money = BigDecimal.ZERO;
}
