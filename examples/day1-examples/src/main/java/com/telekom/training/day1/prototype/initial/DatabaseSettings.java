package com.telekom.training.day1.prototype.initial;

public class DatabaseSettings {
    private final String connectionPool;
    private final int maxConnections;
    private final int timeoutSeconds;
    
    public DatabaseSettings(String connectionPool, int maxConnections, int timeoutSeconds) {
        this.connectionPool = connectionPool;
        this.maxConnections = maxConnections;
        this.timeoutSeconds = timeoutSeconds;
    }
    
    public String getConnectionPool() { return connectionPool; }
    public int getMaxConnections() { return maxConnections; }
    public int getTimeoutSeconds() { return timeoutSeconds; }
    
    @Override
    public String toString() {
        return String.format("DatabaseSettings{pool='%s', maxConn=%d, timeout=%ds}", 
            connectionPool, maxConnections, timeoutSeconds);
    }
}