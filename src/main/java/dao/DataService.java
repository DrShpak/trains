package dao;

import io.vavr.collection.List;
import services.route.Route;

import java.util.Optional;

public interface DataService {
    void update(int routeId);

    void update(Route route);

//    Route select(int routeId);

    List<Route> selectAll();

    void remove(int routeId);

    Optional<Route> getRouteById(int routeId);
}
