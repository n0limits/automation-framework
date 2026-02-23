package com.automation.database;

import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class QueryBuilder {
    private final StringBuilder query;
    private final List<String> conditions;
    private String orderByClause;
    private String limitClause;
    private boolean hasFrom;

    private QueryBuilder() {
        this.query = new StringBuilder();
        this.conditions = new ArrayList<>();
        this.hasFrom = false;
    }

    public static QueryBuilder select(String... columns) {
        QueryBuilder builder = new QueryBuilder();
        if (columns.length == 0) {
            builder.query.append("SELECT * ");
        } else {
            builder.query.append("SELECT ").append(String.join(", ", columns)).append(" ");
        }
        return builder;
    }

    public QueryBuilder from(String table) {
        this.query.append("FROM ").append(table).append(" ");
        this.hasFrom = true;
        return this;
    }

    public QueryBuilder where(String condition) {
        conditions.add(condition);
        return this;
    }

    public QueryBuilder and(String condition) {
        if (conditions.isEmpty()) {
            throw new IllegalStateException("Cannot use and() without a preceding where() clause");
        }
        conditions.add("AND " + condition);
        return this;
    }

    public QueryBuilder or(String condition) {
        if (conditions.isEmpty()) {
            throw new IllegalStateException("Cannot use or() without a preceding where() clause");
        }
        conditions.add("OR " + condition);
        return this;
    }

    public QueryBuilder orderBy(String column, String direction) {
        this.orderByClause = "ORDER BY " + column + " " + direction;
        return this;
    }

    public QueryBuilder limit(int limit) {
        this.limitClause = "LIMIT " + limit;
        return this;
    }

    public String build() {
        if (!hasFrom) {
            throw new IllegalStateException("Cannot build query without a from() clause");
        }

        StringBuilder result = new StringBuilder(query.toString().trim());

        if (!conditions.isEmpty()) {
            result.append(" WHERE ");
            result.append(String.join(" ", conditions));
        }

        if (orderByClause != null) {
            result.append(" ").append(orderByClause);
        }

        if (limitClause != null) {
            result.append(" ").append(limitClause);
        }

        String finalQuery = result.toString().trim();
        log.debug("Built query: {}", finalQuery);
        return finalQuery;
    }

    public static String insert(String table, String... columns) {
        StringBuilder insertQuery = new StringBuilder("INSERT INTO ")
                .append(table)
                .append(" (")
                .append(String.join(", ", columns))
                .append(") VALUES (");

        for (int i = 0; i < columns.length; i++) {
            insertQuery.append("?");
            if (i < columns.length - 1) {
                insertQuery.append(", ");
            }
        }
        insertQuery.append(")");
        return insertQuery.toString();
    }

    public static String update(String table) {
        return "UPDATE " + table + " SET ";
    }

    public static String delete(String table) {
        return "DELETE FROM " + table + " ";
    }
}
