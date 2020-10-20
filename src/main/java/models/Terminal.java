package models;

import lombok.Value;

import java.util.Date;

@Value
public class Terminal {
    Date time;
    String name;

    public Terminal(Date time, String name) {
        this.time = time;
        this.name = name;
    }
}
