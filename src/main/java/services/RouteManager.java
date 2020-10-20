package services;

import dao.DataService;
import io.vavr.collection.List;
import lombok.Value;
import models.Route;
import models.Station;
import models.Terminal;
import models.Train;

import java.util.Random;

@Value
public class RouteManager {
    DataService dataService;

    public void addRoute(Route route) {
        dataService.update(route);
    }

    public void removeRoute(int routeId) {
        var route = dataService.getRouteById(routeId);
        if (route.isPresent())
            dataService.remove(routeId);
    }

    public void editTrain(int routeId, Train newTrain) throws IllegalArgumentException  {
        var oldRoute = getRouteById(routeId);
        var newRoute = oldRoute.toBuilder()
            .train(newTrain)
            .build();
        dataService.remove(routeId);
        dataService.update(newRoute);
    }

    public void editRouteId(int oldRouteId, int newRouteId) throws IllegalArgumentException  {
        var oldRoute = dataService.getRouteById(oldRouteId)
            .orElseThrow(() -> new IllegalArgumentException("Route with id = " + oldRouteId + " doesn't exist!"));
        var newRoute = oldRoute.toBuilder()
            .routeId(newRouteId)
            .build();
        dataService.remove(oldRouteId);
        dataService.update(newRoute);
    }

    public void addIntermediateStation(int routeId, Station station) throws Exception {
        var oldRoute = getRouteById(routeId);
        if (station.getArrivalTime().before(oldRoute.getExitStation().getTime()))
            throw new IllegalArgumentException("Exit station has date after than arrival time " + station.getArrivalTime());
        if (station.getLeaveTime().after(oldRoute.getEntryStation().getTime()))
            throw new IllegalArgumentException("Entry station has date before than leave time time " + station.getLeaveTime());
        if (oldRoute.getIntermediateStations()
            .toJavaStream()
            .anyMatch(x -> x.isWithinRange(station.getArrivalTime())
                || x.isWithinRange(station.getLeaveTime()))) {
            throw new IllegalArgumentException("These date interval is incorrect");
        }
        if (oldRoute.getIntermediateStations().find(x -> x.getName().equals(station.getName())).isDefined())
            throw new IllegalArgumentException("Station with the same name already exists " + station.getName());
        var stations = oldRoute.getIntermediateStations().append(station);
        var newRoute = oldRoute.toBuilder()
            .intermediateStations(stations)
            .build();
        dataService.remove(routeId);
        dataService.update(newRoute);
    }

    public void editExitStation(int routeId, Terminal station) throws IllegalArgumentException {
        var oldRoute = getRouteById(routeId);

        /*
        отфильтровали станции, которые стоят посля терминала
        все станции ДО и старая терминальная станция сюда не попадут
         */
        var stations = oldRoute.getIntermediateStations()
            .filter(x -> x.getArrivalTime().after(station.getTime()));
        var newRoute = oldRoute.toBuilder()
            .intermediateStations(stations)
            .exitStation(station)
            .build();
        dataService.remove(routeId);
        dataService.update(newRoute);
    }

    public void editEntryStation(int routeId, Terminal entryStation) {
        var oldRoute = getRouteById(routeId);

        /*
        отфильтровали станции, которые стоят передм терминалом
        все станции после и старая терминальная станция сюда не попадут
         */
        var stations = oldRoute.getIntermediateStations()
            .filter(x -> x.getArrivalTime().before(entryStation.getTime()));
        var newRoute = oldRoute.toBuilder()
            .intermediateStations(stations)
            .entryStation(entryStation)
            .build();
        dataService.remove(routeId);
        dataService.update(newRoute);
    }

    public Route createRoute(int routeId, String name, Terminal exitStation, Terminal entryStation,
                             Train train,
                             List<Station> intermediateStations) {
        return Route.builder()
            .routeId(routeId)
            .name(name)
            .exitStation(exitStation)
            .intermediateStations(intermediateStations)
            .entryStation(entryStation)
            .train(train)
            .build();
    }

    public Route createRoute(String name, Train train) {
        return Route.builder()
            .routeId(new Random().nextInt())
            .name(name)
            .train(train)
            .build();
    }

    public void removeIntermediateStation(int routeId, String name) throws IllegalArgumentException {
        var oldRoute = getRouteById(routeId);
        if (oldRoute.getIntermediateStations().find(x -> x.getName().equals(name)).isEmpty())
            throw new IllegalArgumentException("Station with name {" + name + "} doesn't exist");
        var stations = oldRoute.getIntermediateStations()
            .filter(x -> !x.getName().equals(name));

        var newRoute = oldRoute.toBuilder()
            .intermediateStations(stations)
            .build();
        dataService.remove(routeId);
        dataService.update(newRoute);
    }

    public void editRouteName(int routeId, String name) {
        var oldRoute = getRouteById(routeId);
        var newRoute = oldRoute.toBuilder()
            .name(name)
            .build();
        dataService.remove(routeId);
        dataService.update(newRoute);
    }

    public Route getRouteById(int routeId) {
        return dataService.getRouteById(routeId)
            .orElseThrow(() -> new IllegalArgumentException("Route with id = " + routeId + " doesn't exist!"));
    }
}