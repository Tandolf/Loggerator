package se.andolf.loggerator.models;

public interface LogBuilder {

    Object build();

    Object start(long start);

    Object end(long end);

    Object push(MethodData methodData);

    Object returnValue(Object returnValue);

    Object returnStatus(boolean b);

    Object name(String name);
}
