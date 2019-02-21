package se.andolf.loggerator.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.andolf.loggerator.models.annotations.LogThis;

import java.io.FileNotFoundException;

@Component
public class MyResource {

    private final MyNextResource myNextResource;

    @Autowired
    public MyResource(MyNextResource myNextResource) {
        this.myNextResource = myNextResource;
    }

    @LogThis
    public int add(int starting, int adding) {
        return myNextResource.addAgain(starting, adding);
    }

    @LogThis
    public String throwException() {
        try {
            myNextResource.throwDeeper();
        } catch (FileNotFoundException e) {

        }
        return "I Got this";
    }
}
