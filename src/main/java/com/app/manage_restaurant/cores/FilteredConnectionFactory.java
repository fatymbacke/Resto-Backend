package com.app.manage_restaurant.cores;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.r2dbc.spi.Batch;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryMetadata;
import io.r2dbc.spi.ConnectionMetadata;
import io.r2dbc.spi.IsolationLevel;
import io.r2dbc.spi.Statement;
import io.r2dbc.spi.TransactionDefinition;
import io.r2dbc.spi.ValidationDepth;
import reactor.core.publisher.Mono;

/**
 * ConnectionFactory wrapper pour interceptor les requêtes SQL
 * mais sans dépendance directe au SecurityContext.
 */
public class FilteredConnectionFactory implements ConnectionFactory {

    private static final Logger log = LoggerFactory.getLogger(FilteredConnectionFactory.class);
    private final ConnectionFactory delegate;

    public FilteredConnectionFactory(ConnectionFactory delegate) {
        this.delegate = delegate;
    }

    @Override
    public ConnectionFactoryMetadata getMetadata() {
        return delegate.getMetadata();
    }

    @Override
    public Mono<? extends Connection> create() {
        // Crée la connexion normalement, sans tenter d’accéder au SecurityContext
        return Mono.from(delegate.create())
                   .map(conn -> new FilteredConnection(conn));
    }

    private static class FilteredConnection implements Connection {

        private final Connection delegate;

        FilteredConnection(Connection delegate) {
            this.delegate = delegate;
        }

        @Override
        public Statement createStatement(String sql) {
            // Pas de filtrage ici : le filtrage global se fait dans le service/repository
            return delegate.createStatement(sql);
        }

        
        // Délégation complète pour toutes les méthodes
        @Override public Batch createBatch() { return delegate.createBatch(); }
        @Override public Publisher<Void> close() { return delegate.close(); }
        @Override public Publisher<Void> beginTransaction() { return delegate.beginTransaction(); }
        @Override public Publisher<Void> beginTransaction(TransactionDefinition definition) { return delegate.beginTransaction(definition); }
        @Override public Publisher<Void> commitTransaction() { return delegate.commitTransaction(); }
        @Override public Publisher<Void> rollbackTransaction() { return delegate.rollbackTransaction(); }
        @Override public Publisher<Void> setTransactionIsolationLevel(IsolationLevel isolationLevel) { return delegate.setTransactionIsolationLevel(isolationLevel); }
        @Override public IsolationLevel getTransactionIsolationLevel() { return delegate.getTransactionIsolationLevel(); }
        @Override public Publisher<Void> setAutoCommit(boolean autoCommit) { return delegate.setAutoCommit(autoCommit); }
        @Override public boolean isAutoCommit() { return delegate.isAutoCommit(); }
        @Override public Publisher<Void> setLockWaitTimeout(java.time.Duration timeout) { return delegate.setLockWaitTimeout(timeout); }
        @Override public Publisher<Void> setStatementTimeout(java.time.Duration timeout) { return delegate.setStatementTimeout(timeout); }
        @Override public Publisher<Boolean> validate(ValidationDepth depth) { return delegate.validate(depth); }
        @Override public ConnectionMetadata getMetadata() { return delegate.getMetadata(); }
        @Override public Publisher<Void> createSavepoint(String name) { return delegate.createSavepoint(name); }
        @Override public Publisher<Void> rollbackTransactionToSavepoint(String name) { return delegate.rollbackTransactionToSavepoint(name); }
        @Override public Publisher<Void> releaseSavepoint(String name) { return delegate.releaseSavepoint(name); }
    }
}
