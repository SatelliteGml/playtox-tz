package com.playtox;

import com.playtox.runner.ApplicationRunner;

public class AppStart {
    public static void main(String[] args) {
        ApplicationRunner runner = new ApplicationRunner(4, 3);
        runner.run();
    }
}
