package se.andolf.loggerator.resources;

import org.springframework.stereotype.Component;
import se.andolf.loggerator.models.annotations.LogThis;

import java.io.FileNotFoundException;

@Component
public class MyNextResource {

    @LogThis
    public int addAgain(int starting, int adding) {
        return starting + adding;
    }

    @LogThis
    public void throwDeeper() throws FileNotFoundException {
        throw new FileNotFoundException();
    }
}
