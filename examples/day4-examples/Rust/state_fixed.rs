trait DeviceState {
    fn handle_event(self: Box<Self>, event: Event) 
        -> Box<dyn DeviceState>;
}

struct IdleState;
struct BootingState;
struct ReadyState;

impl DeviceState for IdleState {
    fn handle_event(self: Box<Self>, event: Event) 
        -> Box<dyn DeviceState> {
        match event {
            Event::PowerOn => Box::new(BootingState),
            _ => self,
        }
    }
}

struct Device {
    state: Box<dyn DeviceState>,
}

impl Device {
    fn handle_event(&mut self, event: Event) {
        self.state = self.state.handle_event(event);
    }
}
