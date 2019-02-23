package com.github.tandolf.loggerator.core.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Deque;

@JsonDeserialize(as = MethodData.class)
public interface LogData {

    Deque<LogData> getMethods();
}
