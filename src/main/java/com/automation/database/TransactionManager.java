package com.automation.database;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

/**
 * Manages database transaction lifecycle: begin, commit, rollback.
 * Extracted from DatabaseTestUtils for single-responsibility.
 *
 * @author Victor Grozev
 */
@Slf4j
public class TransactionManager {

    private final Supplier<Connection> connectionSupplier;

    /**
     * @param connectionSupplier supplies the current thread's JDBC connection
     */
    public TransactionManager(Supplier<Connection> connectionSupplier) {
        this.connectionSupplier = connectionSupplier;
    }

    /**
     * Begin transaction (disable auto-commit)
     *
     * @throws SQLException if transaction start fails
     */
    public void beginTransaction() throws SQLException {
        Connection conn = connectionSupplier.get();
        conn.setAutoCommit(false);
        log.info("Transaction started");
    }

    /**
     * Commit transaction and restore auto-commit
     *
     * @throws SQLException if commit fails
     */
    public void commitTransaction() throws SQLException {
        Connection conn = connectionSupplier.get();
        if (conn != null && !conn.getAutoCommit()) {
            conn.commit();
            conn.setAutoCommit(true);
            log.info("Transaction committed");
        }
    }

    /**
     * Rollback transaction and restore auto-commit
     *
     * @throws SQLException if rollback fails
     */
    public void rollbackTransaction() throws SQLException {
        Connection conn = connectionSupplier.get();
        if (conn != null && !conn.getAutoCommit()) {
            conn.rollback();
            conn.setAutoCommit(true);
            log.info("Transaction rolled back");
        }
    }

    /**
     * Execute code within a transaction with automatic commit/rollback
     *
     * @param dbUtils    the DatabaseTestUtils instance passed to the callback
     * @param transaction Transaction code to execute
     * @throws Exception if transaction fails (rolled back automatically)
     */
    public void executeInTransaction(DatabaseTestUtils dbUtils, TransactionCallback transaction) throws Exception {
        try {
            beginTransaction();
            transaction.execute(dbUtils);
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            log.error("Transaction failed and was rolled back", e);
            throw e;
        }
    }

    /**
     * Functional interface for transaction callbacks
     */
    @FunctionalInterface
    public interface TransactionCallback {
        void execute(DatabaseTestUtils db) throws Exception;
    }
}
