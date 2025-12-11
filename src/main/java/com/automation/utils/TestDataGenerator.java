package com.automation.utils;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import java.util.Random;

@Slf4j
public class TestDataGenerator {
    private static final Faker faker = new Faker();
    private static final Random random = new Random();

    public static String generateRandomEmail() {
        return faker.internet().emailAddress();
    }

    public static String generateRandomName() {
        return faker.name().fullName();
    }

    public static String generateRandomPhone() {
        return faker.phoneNumber().phoneNumber();
    }

    public static String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }

    public static int generateRandomNumber(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    public static String generateRandomString(int length) {
        return faker.lorem().characters(length);
    }
}
