package models;

import lombok.Builder;
import lombok.Value;

import java.util.Date;

@Value
@Builder(toBuilder = true)
public class Station {
    String name;
    Date arrivalTime;
    Date leaveTime;

    public Station(String name, Date arrivalTime, Date leaveTime) {
        this.name = name;
        if (arrivalTime.after(leaveTime))
            throw new IllegalArgumentException("Arrival time is after than leave time.");
        else {
            this.arrivalTime = arrivalTime;
            this.leaveTime = leaveTime;
        }
    }

    public boolean isWithinRange(Date date) {
        return !(date.before(leaveTime) || date.after(leaveTime));
    }
}