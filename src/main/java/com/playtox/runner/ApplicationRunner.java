package com.playtox.runner;

import com.playtox.model.Account;
import com.playtox.service.AccountService;
import com.playtox.service.TransferTask;
import com.playtox.util.IdGenerator;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ApplicationRunner {
    private final List<Account> accounts;
    private final AccountService accountService;
    private final int threadCount;

    public ApplicationRunner(int accountCount, int threadCount) {
        this.accounts = initializeAccounts(accountCount);
        this.accountService = new AccountService(accounts);
        this.threadCount = threadCount;
    }

    public void run() {
        log.info("Application started");

        BigDecimal initialTotal = calculateTotalBalance();
        log.info("Initial total balance: {}", initialTotal);

        runTransferThreads();

        BigDecimal finalTotal = calculateTotalBalance();
        logFinalResults(initialTotal, finalTotal);

        log.info("Application finished.");
    }

    private List<Account> initializeAccounts(int accountCount) {
        List<Account> accounts = new ArrayList<>();
        for (int i = 0; i < accountCount; i++) {
            accounts.add(new Account(IdGenerator.randomId(), BigDecimal.valueOf(10000)));
        }
        return accounts;
    }

    private void runTransferThreads() {
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(new TransferTask(accountService), "TransferThread-" + (i + 1));
            threads.add(thread);
            thread.start();
        }

        waitForThreadsCompletion(threads);
    }

    private void waitForThreadsCompletion(List<Thread> threads) {
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                log.error("Thread interrupted: {}", e.getMessage(), e);
                Thread.currentThread().interrupt();
            }
        }
    }

    private void logFinalResults(BigDecimal initialTotal, BigDecimal finalTotal) {
        log.info("Final total balance: {}", finalTotal);

        if (finalTotal.compareTo(initialTotal) == 0) {
            log.info("Validation successful — total balance unchanged.");
        } else {
            log.error("Validation failed! Expected {}, but got {}", initialTotal, finalTotal);
        }

        logAccountBalances();
    }

    private void logAccountBalances() {
        log.info("Final account balances:");
        accounts.forEach(acc -> log.info("{} : {}", acc.getId(), acc.getMoney()));
    }

    private BigDecimal calculateTotalBalance() {
        return accounts.stream()
                .map(Account::getMoney)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}