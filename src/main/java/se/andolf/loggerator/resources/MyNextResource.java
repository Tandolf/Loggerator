package se.andolf.loggerator.resources;

import org.springframework.stereotype.Component;
import se.andolf.loggerator.model.LogThis;

@Component
public class MyNextResource {

    @LogThis
    public int addAgain(int starting, int adding) {
        return starting + adding;
    }
}
