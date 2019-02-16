package se.andolf.loggerator.models;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class LogData {

    private String name;
    private Object[] args;

    @Singular
    private List<LogData> methods;
}
