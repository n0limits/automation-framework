package com.automation.testdata;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User Data Builder
 * Fluent builder for generating user test data
 *
 * @author Victor Grozev
 */
@Getter
public class UserDataBuilder {
    private final TestDataFactory factory;
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    private String status;
    private String role;
    private LocalDateTime createdAt;

    public UserDataBuilder(TestDataFactory factory) {
        this.factory = factory;
        // Set defaults
        this.username = factory.username();
        this.email = factory.email();
        this.password = factory.password();
        this.firstName = factory.firstName();
        this.lastName = factory.lastName();
        this.phone = factory.phoneNumber();
        this.status = "active";
        this.role = "user";
        this.createdAt = LocalDateTime.now();
    }

    public UserDataBuilder username(String username) {
        this.username = username;
        return this;
    }

    public UserDataBuilder email(String email) {
        this.email = email;
        return this;
    }

    public UserDataBuilder password(String password) {
        this.password = password;
        return this;
    }

    public UserDataBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserDataBuilder lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public UserDataBuilder phone(String phone) {
        this.phone = phone;
        return this;
    }

    public UserDataBuilder status(String status) {
        this.status = status;
        return this;
    }

    public UserDataBuilder role(String role) {
        this.role = role;
        return this;
    }

    public UserDataBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Map<String, Object> build() {
        Map<String, Object> user = new LinkedHashMap<>();
        user.put("username", username);
        user.put("email", email);
        user.put("password", password);
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("phone", phone);
        user.put("status", status);
        user.put("role", role);
        user.put("createdAt", createdAt);
        return user;
    }
}
