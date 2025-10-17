package com.playtox.service;

import com.playtox.model.Account;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Slf4j
public class TransferTask implements Runnable {

    private static volatile int totalTransactions = 0;
    private static final int MAX_TRANSACTIONS = 30;

    private final AccountService service;
    private final Random random = new Random();

    public TransferTask(AccountService service) {
        this.service = service;
    }

    @Override
    public void run() {
        while (getTransactionCount() < MAX_TRANSACTIONS) {
            try {
                Thread.sleep(1000 + random.nextInt(1000));

                List<Account> accounts = service.getAccounts();
                if (accounts.size() < 2) continue;

                Account from = accounts.get(random.nextInt(accounts.size()));
                Account to = accounts.get(random.nextInt(accounts.size()));
                if (from == to) continue;

                BigDecimal amount = BigDecimal.valueOf(1 + random.nextInt(5000));

                service.transfer(from, to, amount);

                incrementTransactions();

            } catch (Exception e) {
                log.error("Error in thread: {}", e.getMessage(), e);
            }
        }
    }

    private static synchronized void incrementTransactions() {
        totalTransactions++;
    }

    private static synchronized int getTransactionCount() {
        return totalTransactions;
    }
}
