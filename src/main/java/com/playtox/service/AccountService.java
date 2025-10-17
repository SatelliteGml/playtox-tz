package com.playtox.service;

import com.playtox.model.Account;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Slf4j
public class AccountService {

    private final List<Account> accounts;

    public AccountService(List<Account> accounts) {
        this.accounts = accounts;
    }

    public void transfer(Account from, Account to, BigDecimal amount) {
        Account first = from.getId().compareTo(to.getId()) < 0 ? from : to;
        Account second = from == first ? to : from;

        synchronized (first) {
            synchronized (second) {
                if (from.withdraw(amount)) {
                    to.deposit(amount);
                    log.info("Transfer {} from {} to {} successful | Balances: {} = {}, {} = {}",
                            amount, from.getId(), to.getId(),
                            from.getId(), from.getMoney(), to.getId(), to.getMoney());

                } else {
                    log.warn("Not enough funds on account {} (balance = {}, requested = {})",
                            from.getId(), from.getMoney(), amount);
                }
            }
        }
    }

}
