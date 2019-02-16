package se.andolf.loggerator.models;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AddBody {

    private int addendOne;
    private int addendTwo;

}
