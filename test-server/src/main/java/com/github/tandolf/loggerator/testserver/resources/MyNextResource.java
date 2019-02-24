package com.github.tandolf.loggerator.testserver.resources;

import com.github.tandolf.loggerator.core.models.annotations.LogThis;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;

@Component
public class MyNextResource {

    @LogThis
    public int addAgain(int starting, int adding) {
        return starting + adding;
    }

    @LogThis(timed = false)
    public void throwDeeper() throws FileNotFoundException {
        throw new FileNotFoundException();
    }
}
