package com.automation.testdata;

import lombok.Getter;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Person Data Builder
 * Fluent builder for generating person test data
 *
 * @author Victor Grozev
 */
@Getter
public class PersonDataBuilder {
    private final TestDataFactory factory;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private String gender;
    private String ssn;

    public PersonDataBuilder(TestDataFactory factory) {
        this.factory = factory;
        // Set defaults
        this.firstName = factory.firstName();
        this.lastName = factory.lastName();
        this.email = factory.email();
        this.phone = factory.phoneNumber();
        this.dateOfBirth = factory.pastDate(365 * 50); // Random DOB up to 50 years ago
        this.gender = factory.randomElement("Male", "Female", "Other");
    }

    public PersonDataBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public PersonDataBuilder lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public PersonDataBuilder email(String email) {
        this.email = email;
        return this;
    }

    public PersonDataBuilder phone(String phone) {
        this.phone = phone;
        return this;
    }

    public PersonDataBuilder dateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public PersonDataBuilder gender(String gender) {
        this.gender = gender;
        return this;
    }

    public PersonDataBuilder ssn(String ssn) {
        this.ssn = ssn;
        return this;
    }

    public String fullName() {
        return firstName + " " + lastName;
    }

    public Map<String, Object> build() {
        Map<String, Object> person = new LinkedHashMap<>();
        person.put("firstName", firstName);
        person.put("lastName", lastName);
        person.put("fullName", fullName());
        person.put("email", email);
        person.put("phone", phone);
        person.put("dateOfBirth", dateOfBirth);
        person.put("gender", gender);
        if (ssn != null) {
            person.put("ssn", ssn);
        }
        return person;
    }
}
