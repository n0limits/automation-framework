package com.automation.testdata;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Address Data Builder
 * Fluent builder for generating address test data
 *
 * @author Victor Grozev
 */
@Getter
public class AddressDataBuilder {
    private final TestDataFactory factory;
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;

    public AddressDataBuilder(TestDataFactory factory) {
        this.factory = factory;
        // Set defaults
        this.street = factory.streetAddress();
        this.city = factory.city();
        this.state = factory.state();
        this.zipCode = factory.zipCode();
        this.country = factory.country();
    }

    public AddressDataBuilder street(String street) {
        this.street = street;
        return this;
    }

    public AddressDataBuilder city(String city) {
        this.city = city;
        return this;
    }

    public AddressDataBuilder state(String state) {
        this.state = state;
        return this;
    }

    public AddressDataBuilder zipCode(String zipCode) {
        this.zipCode = zipCode;
        return this;
    }

    public AddressDataBuilder country(String country) {
        this.country = country;
        return this;
    }

    public Map<String, Object> build() {
        Map<String, Object> address = new LinkedHashMap<>();
        address.put("street", street);
        address.put("city", city);
        address.put("state", state);
        address.put("zipCode", zipCode);
        address.put("country", country);
        return address;
    }
}
