struct Device {
    state: String,
}

impl Device {
    fn handle_event(&mut self, event: Event) {
        match self.state.as_str() {
            "idle" => {
                if event == Event::PowerOn {
                    self.state = "booting".to_string();
                    // 20 lines of booting logic
                }
            }
            "booting" => {
                if event == Event::BootComplete {
                    self.state = "ready".to_string();
                    // 30 lines of ready logic
                }
            }
            "ready" => {
                // More nested conditions...
            }
            _ => {}
        }
    }
}
