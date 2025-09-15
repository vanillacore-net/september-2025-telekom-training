class ConfigManager:
    def execute_action(self, action, params):
        if action == "set_bandwidth":
            old = self.bandwidth
            self.bandwidth = params['value']
            # No undo possible!
        elif action == "set_timeout":
            old = self.timeout
            self.timeout = params['value']
            # No undo possible!
        elif action == "enable_feature":
            self.features[params['name']] = True
            # How to rollback?
        elif action == "modify_route":
            # Complex routing change
            # No way to undo!
            pass
