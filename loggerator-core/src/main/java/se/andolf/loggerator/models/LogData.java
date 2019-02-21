package se.andolf.loggerator.models;

import java.util.Deque;

public interface LogData {

    Deque<LogData> getMethods();
}
