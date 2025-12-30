package com.automation.testdata;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Order Data Builder
 * Fluent builder for generating order test data
 *
 * @author Victor Grozev
 */
@Getter
public class OrderDataBuilder {
    private final TestDataFactory factory;
    private String orderNumber;
    private Long userId;
    private Double amount;
    private String currency;
    private String status;
    private String paymentMethod;
    private LocalDateTime orderDate;

    public OrderDataBuilder(TestDataFactory factory) {
        this.factory = factory;
        // Set defaults
        this.orderNumber = "ORD-" + factory.uuid().substring(0, 8).toUpperCase();
        this.amount = factory.amount(10.0, 1000.0);
        this.currency = "USD";
        this.status = "pending";
        this.paymentMethod = factory.randomElement("credit_card", "debit_card", "paypal", "bank_transfer");
        this.orderDate = LocalDateTime.now();
    }

    public OrderDataBuilder orderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
        return this;
    }

    public OrderDataBuilder userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public OrderDataBuilder amount(Double amount) {
        this.amount = amount;
        return this;
    }

    public OrderDataBuilder currency(String currency) {
        this.currency = currency;
        return this;
    }

    public OrderDataBuilder status(String status) {
        this.status = status;
        return this;
    }

    public OrderDataBuilder paymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    public OrderDataBuilder orderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
        return this;
    }

    public Map<String, Object> build() {
        Map<String, Object> order = new LinkedHashMap<>();
        order.put("orderNumber", orderNumber);
        if (userId != null) {
            order.put("userId", userId);
        }
        order.put("amount", amount);
        order.put("currency", currency);
        order.put("status", status);
        order.put("paymentMethod", paymentMethod);
        order.put("orderDate", orderDate);
        return order;
    }
}
