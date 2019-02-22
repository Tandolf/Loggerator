package com.github.tandolf.loggerator.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import com.github.tandolf.loggerator.models.AddBody;
import com.github.tandolf.loggerator.models.annotations.LogThis;
import com.github.tandolf.loggerator.resources.MyResource;

@RestController
public class MyController {

    private final MyResource myResource;

    @Autowired
    public MyController(MyResource myResource) {
        this.myResource = myResource;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/add", produces = MediaType.TEXT_HTML_VALUE)
    @LogThis
    public String addEndpoint(@RequestBody AddBody numbers) {
        return String.valueOf(myResource.add(numbers.getOne(), numbers.getTwo()));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/exception")
    @LogThis
    public void throwException() {
        myResource.throwException();
    }
}
