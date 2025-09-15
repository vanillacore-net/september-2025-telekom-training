class NetworkMonitor {
private:
    Dashboard* dashboard;
    AlertSystem* alertSystem;
    Logger* logger;
    Analytics* analytics;
    
public:
    void onStatusChange(Status status) {
        dashboard->update(status);
        alertSystem->check(status);
        logger->log(status);
        analytics->analyze(status);
        // Mehr hard-coded dependencies...
    }
    
    void addNewSystem(System* sys) {
        // Requires code change!
    }
};
