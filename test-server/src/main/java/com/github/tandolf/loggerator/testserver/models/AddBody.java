package com.github.tandolf.loggerator.testserver.models;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AddBody {

    private int one;
    private int two;

}
