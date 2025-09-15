class Observer {
public:
    virtual void update(const Status& status) = 0;
};

class Subject {
private:
    std::vector<Observer*> observers;
    
public:
    void attach(Observer* obs) {
        observers.push_back(obs);
    }
    
    void notify(const Status& status) {
        for(auto obs : observers) {
            obs->update(status);
        }
    }
};

class Dashboard : public Observer {
    void update(const Status& status) override {
        // Dashboard specific handling
    }
};
