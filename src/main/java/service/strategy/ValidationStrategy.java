package service.strategy;

import model.Event;

public interface ValidationStrategy {
    void validate(Event event);
}