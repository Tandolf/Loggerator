package com.github.tandolf.loggerator.testserver.controllers;


import com.github.tandolf.loggerator.core.models.annotations.LogThis;
import com.github.tandolf.loggerator.testserver.models.AddBody;
import com.github.tandolf.loggerator.testserver.resources.MyResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class MyController {

    private final MyResource myResource;

    @Autowired
    public MyController(MyResource myResource) {
        this.myResource = myResource;
    }

    @LogThis
    @PostMapping(path = "/add", produces = MediaType.TEXT_HTML_VALUE)
    public String addEndpoint(@RequestBody AddBody numbers) {
        return String.valueOf(myResource.add(numbers.getOne(), numbers.getTwo()));
    }

    @LogThis
    @GetMapping(path = "/exception")
    public void throwException() {
        myResource.throwException();
    }
}
