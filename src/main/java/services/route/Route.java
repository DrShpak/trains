package services.route;

import io.vavr.collection.List;
import lombok.Builder;
import lombok.Value;

import java.util.Date;

@Value
@Builder(toBuilder = true)
public class Route {
    int routeId;
    String name;
    Terminal exitStation;
    Terminal entryStation;
    Train train;
//    Date leaveTime;
//    Date arrivalTime;
    List<Station> intermediateStations;

    public Route(
        int routeId, String name, Terminal exitStation,
        Terminal entryStation, Train train,
        List<Station> intermediateStations
    ) {
        this.routeId = routeId;
        this.name = name;
        this.exitStation = exitStation;
        this.entryStation = entryStation;
        this.train = train;
//        this.leaveTime = leaveTime;
//        this.arrivalTime = arrivalTime;
        this.intermediateStations = intermediateStations.sortBy(Station::getArrivalTime);
    }
}