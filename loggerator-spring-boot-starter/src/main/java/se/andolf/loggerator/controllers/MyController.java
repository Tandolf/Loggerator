package se.andolf.loggerator.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import se.andolf.loggerator.models.AddBody;
import se.andolf.loggerator.models.annotations.LogThis;
import se.andolf.loggerator.resources.MyResource;

@RestController
public class MyController {

    private final MyResource myResource;

    @Autowired
    public MyController(MyResource myResource) {
        this.myResource = myResource;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/one", produces = MediaType.TEXT_HTML_VALUE)
    @LogThis
    public String addEndpoint(@RequestBody AddBody numbers) {
        return String.valueOf(myResource.add(numbers.getAddendOne(), numbers.getAddendTwo()));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/two", produces = MediaType.TEXT_HTML_VALUE)
    @LogThis
    public String getTwo() {
        ThreadLocal<Integer> testValue = new ThreadLocal<>();
        testValue.set(2);

        return String.valueOf(testValue.get());
    }
}
