package org.example.generator;

import org.example.model.User;

public class UserGenerate {
    public static User getRandom() {
        return new User(System.currentTimeMillis() + "_apitest@test.ru", "qwerty", "Mary");
    }

    public static User getWithoutEmail() {
        return new User(null, "qwerty", "Mary");
    }

    public static User getWithoutPassword() {
        return new User(System.currentTimeMillis() + "_apitest@test.ru", null, "Mary");
    }

    public static User getWithoutName() {
        return new User(System.currentTimeMillis() + "_apitest@test.ru", "qwerty", null);
    }
}
