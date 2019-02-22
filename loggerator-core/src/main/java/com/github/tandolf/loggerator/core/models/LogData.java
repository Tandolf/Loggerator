package com.github.tandolf.loggerator.core.models;

import java.util.Deque;

public interface LogData {

    Deque<LogData> getMethods();
}
