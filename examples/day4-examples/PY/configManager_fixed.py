from abc import ABC, abstractmethod

class Command(ABC):
    @abstractmethod
    def execute(self):
        pass
    
    @abstractmethod
    def undo(self):
        pass

class SetBandwidthCommand(Command):
    def __init__(self, manager, new_value):
        self.manager = manager
        self.new_value = new_value
        self.old_value = None
    
    def execute(self):
        self.old_value = self.manager.bandwidth
        self.manager.bandwidth = self.new_value
    
    def undo(self):
        self.manager.bandwidth = self.old_value

class CommandInvoker:
    def __init__(self):
        self.history = []
    
    def execute(self, command):
        command.execute()
        self.history.append(command)
