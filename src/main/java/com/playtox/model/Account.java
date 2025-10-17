package com.playtox.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    private String id;
    private BigDecimal money;

    public synchronized boolean withdraw(BigDecimal amount) {
        if (money.compareTo(amount) >= 0) {
            money = money.subtract(amount);
            return true;
        }
        return false;
    }

    public synchronized void deposit(BigDecimal amount) {
        money = money.add(amount);
    }
}
