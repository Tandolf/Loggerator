package se.andolf.loggerator.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.andolf.loggerator.models.annotations.LogThis;

@Component
public class MyResource {

    @Autowired
    private MyNextResource myNextResource;

    @LogThis
    public int add(int starting, int adding) {
        return myNextResource.addAgain(starting, adding);
    }
}
