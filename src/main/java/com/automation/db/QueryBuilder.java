package com.automation.db;

import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class QueryBuilder {
    private final StringBuilder query;
    private final List<String> conditions;

    private QueryBuilder() {
        this.query = new StringBuilder();
        this.conditions = new ArrayList<>();
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
        return this;
    }

    public QueryBuilder where(String condition) {
        conditions.add(condition);
        return this;
    }

    public QueryBuilder and(String condition) {
        if (!conditions.isEmpty()) {
            conditions.add("AND " + condition);
        }
        return this;
    }

    public QueryBuilder or(String condition) {
        if (!conditions.isEmpty()) {
            conditions.add("OR " + condition);
        }
        return this;
    }

    public QueryBuilder orderBy(String column, String direction) {
        query.append("ORDER BY ").append(column).append(" ").append(direction).append(" ");
        return this;
    }

    public QueryBuilder limit(int limit) {
        query.append("LIMIT ").append(limit).append(" ");
        return this;
    }

    public String build() {
        if (!conditions.isEmpty()) {
            query.append("WHERE ");
            query.append(String.join(" ", conditions));
        }
        String finalQuery = query.toString().trim();
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