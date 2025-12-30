package com.automation.testdata;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Test Data Factory
 * Generates realistic test data using JavaFaker library
 *
 * Features:
 * - Person data (names, emails, phones)
 * - Address data
 * - Company/business data
 * - Financial data
 * - Date/time generation
 * - Custom data patterns
 * - Locale support
 *
 * @author Victor Grozev
 */
@Slf4j
public class TestDataFactory {
    private final Faker faker;
    private final Random random;

    /**
     * Create factory with default locale (English)
     */
    public TestDataFactory() {
        this(Locale.ENGLISH);
    }

    /**
     * Create factory with specific locale
     *
     * @param locale Locale for data generation
     */
    public TestDataFactory(Locale locale) {
        this.faker = new Faker(locale);
        this.random = new Random();
        log.debug("TestDataFactory initialized with locale: {}", locale);
    }

    // ========== Person Data ==========

    /**
     * Generate random first name
     *
     * @return First name
     */
    public String firstName() {
        return faker.name().firstName();
    }

    /**
     * Generate random last name
     *
     * @return Last name
     */
    public String lastName() {
        return faker.name().lastName();
    }

    /**
     * Generate random full name
     *
     * @return Full name
     */
    public String fullName() {
        return faker.name().fullName();
    }

    /**
     * Generate random username
     *
     * @return Username
     */
    public String username() {
        return faker.name().username();
    }

    /**
     * Generate random email
     *
     * @return Email address
     */
    public String email() {
        return faker.internet().emailAddress();
    }

    /**
     * Generate random email with specific domain
     *
     * @param domain Email domain
     * @return Email address
     */
    public String email(String domain) {
        String localPart = faker.name().username();
        return localPart + "@" + domain;
    }

    /**
     * Generate random phone number
     *
     * @return Phone number
     */
    public String phoneNumber() {
        return faker.phoneNumber().phoneNumber();
    }

    /**
     * Generate random cell phone number
     *
     * @return Cell phone number
     */
    public String cellPhone() {
        return faker.phoneNumber().cellPhone();
    }

    // ========== Address Data ==========

    /**
     * Generate random street address
     *
     * @return Street address
     */
    public String streetAddress() {
        return faker.address().streetAddress();
    }

    /**
     * Generate random city
     *
     * @return City name
     */
    public String city() {
        return faker.address().city();
    }

    /**
     * Generate random state
     *
     * @return State name
     */
    public String state() {
        return faker.address().state();
    }

    /**
     * Generate random state abbreviation
     *
     * @return State abbreviation
     */
    public String stateAbbr() {
        return faker.address().stateAbbr();
    }

    /**
     * Generate random zip code
     *
     * @return Zip code
     */
    public String zipCode() {
        return faker.address().zipCode();
    }

    /**
     * Generate random country
     *
     * @return Country name
     */
    public String country() {
        return faker.address().country();
    }

    /**
     * Generate complete address
     *
     * @return Map containing address components
     */
    public Map<String, String> completeAddress() {
        Map<String, String> address = new LinkedHashMap<>();
        address.put("street", streetAddress());
        address.put("city", city());
        address.put("state", state());
        address.put("zipCode", zipCode());
        address.put("country", country());
        return address;
    }

    // ========== Company/Business Data ==========

    /**
     * Generate random company name
     *
     * @return Company name
     */
    public String companyName() {
        return faker.company().name();
    }

    /**
     * Generate random industry
     *
     * @return Industry
     */
    public String industry() {
        return faker.company().industry();
    }

    /**
     * Generate random job title
     *
     * @return Job title
     */
    public String jobTitle() {
        return faker.job().title();
    }

    /**
     * Generate random department
     *
     * @return Department name
     */
    public String department() {
        String[] departments = {
                "Engineering", "Sales", "Marketing", "HR", "Finance",
                "Operations", "Customer Service", "IT", "Legal", "R&D"
        };
        return departments[random.nextInt(departments.length)];
    }

    // ========== Financial Data ==========

    /**
     * Generate random credit card number
     *
     * @return Credit card number
     */
    public String creditCardNumber() {
        return faker.business().creditCardNumber();
    }

    /**
     * Generate random credit card expiry date
     *
     * @return Credit card expiry (MM/YY format)
     */
    public String creditCardExpiry() {
        return faker.business().creditCardExpiry();
    }

    /**
     * Generate random CVV
     *
     * @return CVV (3 digits)
     */
    public String cvv() {
        return String.format("%03d", random.nextInt(1000));
    }

    /**
     * Generate random amount
     *
     * @param min Minimum amount
     * @param max Maximum amount
     * @return Random amount
     */
    public double amount(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

    /**
     * Generate random currency code
     *
     * @return Currency code (e.g., USD, EUR, GBP)
     */
    public String currency() {
        return faker.currency().code();
    }

    // ========== Date/Time Data ==========

    /**
     * Generate random past date
     *
     * @param maxDaysBack Maximum days in the past
     * @return Past date
     */
    public LocalDate pastDate(int maxDaysBack) {
        Date date = faker.date().past(maxDaysBack, TimeUnit.DAYS);
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Generate random future date
     *
     * @param maxDaysAhead Maximum days in the future
     * @return Future date
     */
    public LocalDate futureDate(int maxDaysAhead) {
        Date date = faker.date().future(maxDaysAhead, TimeUnit.DAYS);
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Generate random past date time
     *
     * @param maxHoursBack Maximum hours in the past
     * @return Past date time
     */
    public LocalDateTime pastDateTime(int maxHoursBack) {
        Date date = faker.date().past(maxHoursBack, TimeUnit.HOURS);
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * Generate random future date time
     *
     * @param maxHoursAhead Maximum hours in the future
     * @return Future date time
     */
    public LocalDateTime futureDateTime(int maxHoursAhead) {
        Date date = faker.date().future(maxHoursAhead, TimeUnit.HOURS);
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * Generate date between two dates
     *
     * @param startDate Start date
     * @param endDate End date
     * @return Random date between start and end
     */
    public LocalDate dateBetween(LocalDate startDate, LocalDate endDate) {
        Date start = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date between = faker.date().between(start, end);
        return between.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    // ========== Lorem Ipsum/Text Data ==========

    /**
     * Generate random sentence
     *
     * @return Sentence
     */
    public String sentence() {
        return faker.lorem().sentence();
    }

    /**
     * Generate random sentence with word count
     *
     * @param wordCount Number of words
     * @return Sentence
     */
    public String sentence(int wordCount) {
        return faker.lorem().sentence(wordCount);
    }

    /**
     * Generate random paragraph
     *
     * @return Paragraph
     */
    public String paragraph() {
        return faker.lorem().paragraph();
    }

    /**
     * Generate random paragraphs
     *
     * @param count Number of paragraphs
     * @return Paragraphs
     */
    public String paragraphs(int count) {
        return faker.lorem().paragraphs(count).toString();
    }

    // ========== Internet Data ==========

    /**
     * Generate random password
     *
     * @param minLength Minimum length
     * @param maxLength Maximum length
     * @param includeUppercase Include uppercase
     * @param includeSpecial Include special characters
     * @param includeDigit Include digits
     * @return Password
     */
    public String password(int minLength, int maxLength, boolean includeUppercase,
                           boolean includeSpecial, boolean includeDigit) {
        return faker.internet().password(minLength, maxLength, includeUppercase,
                includeSpecial, includeDigit);
    }

    /**
     * Generate random password with defaults (8-16 chars, all character types)
     *
     * @return Password
     */
    public String password() {
        return faker.internet().password(8, 16, true, true, true);
    }

    /**
     * Generate random URL
     *
     * @return URL
     */
    public String url() {
        return faker.internet().url();
    }

    /**
     * Generate random domain name
     *
     * @return Domain name
     */
    public String domainName() {
        return faker.internet().domainName();
    }

    /**
     * Generate random IP address
     *
     * @return IP address
     */
    public String ipAddress() {
        return faker.internet().ipV4Address();
    }

    // ========== Custom Data ==========

    /**
     * Generate random number between min and max
     *
     * @param min Minimum value
     * @param max Maximum value
     * @return Random number
     */
    public int numberBetween(int min, int max) {
        return faker.number().numberBetween(min, max);
    }

    /**
     * Generate random boolean
     *
     * @return Random boolean
     */
    public boolean bool() {
        return random.nextBoolean();
    }

    /**
     * Generate random element from array
     *
     * @param elements Array of elements
     * @param <T> Element type
     * @return Random element
     */
    @SafeVarargs
    public final <T> T randomElement(T... elements) {
        return elements[random.nextInt(elements.length)];
    }

    /**
     * Generate random element from list
     *
     * @param elements List of elements
     * @param <T> Element type
     * @return Random element
     */
    public <T> T randomElement(List<T> elements) {
        return elements.get(random.nextInt(elements.size()));
    }

    /**
     * Generate UUID
     *
     * @return UUID string
     */
    public String uuid() {
        return UUID.randomUUID().toString();
    }

    // ========== Builder Pattern for Complex Objects ==========

    /**
     * Create person data builder
     *
     * @return PersonDataBuilder
     */
    public PersonDataBuilder person() {
        return new PersonDataBuilder(this);
    }

    /**
     * Create address data builder
     *
     * @return AddressDataBuilder
     */
    public AddressDataBuilder address() {
        return new AddressDataBuilder(this);
    }

    /**
     * Create user data builder
     *
     * @return UserDataBuilder
     */
    public UserDataBuilder user() {
        return new UserDataBuilder(this);
    }

    /**
     * Create order data builder
     *
     * @return OrderDataBuilder
     */
    public OrderDataBuilder order() {
        return new OrderDataBuilder(this);
    }

    // ========== Get underlying Faker instance ==========

    /**
     * Get underlying Faker instance for custom data generation
     *
     * @return Faker instance
     */
    public Faker getFaker() {
        return faker;
    }
}
