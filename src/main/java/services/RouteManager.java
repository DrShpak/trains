package services;

import dao.DataService;
import io.vavr.collection.List;
import lombok.Value;
import models.Route;
import models.Station;
import models.Terminal;
import models.Train;

import java.io.FileReader;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

@Value
public class RouteManager {
    DataService dataService;
    private static Logger log;

    static {
        System.setProperty("java.util.logging.config.file",
            "src/main/resources/logging.properties");
        log = Logger.getLogger(RouteManager.class.getName());
    }


    public void addRoute(Route route) {
//        dataService.update(route);
        dataService.insert(route);
    }

    public void removeRoute(int routeId) {
        var route = dataService.getRouteById(routeId);
        if (route.isPresent())
            dataService.remove(routeId);
        else
            throw new IllegalArgumentException("Route with id = " + routeId + " doesn't exist!");
    }

    public void editTrain(int routeId, Train newTrain) throws IllegalArgumentException  {
        var oldRoute = getRouteById(routeId);
        var newRoute = oldRoute.toBuilder()
            .train(newTrain)
            .build();
//        dataService.remove(routeId);
        dataService.update(newRoute);
    }

    public void editRouteId(int oldRouteId, int newRouteId) throws IllegalArgumentException  {
        var oldRoute = dataService.getRouteById(oldRouteId)
            .orElseThrow(() -> new IllegalArgumentException("Route with id = " + oldRouteId + " doesn't exist!"));
        var newRoute = oldRoute.toBuilder()
            .routeId(newRouteId)
            .build();
//        dataService.remove(oldRouteId);
        dataService.update(newRoute);
    }

    public void addIntermediateStation(int routeId, Station station) {
        var oldRoute = getRouteById(routeId);
        oldRoute.toBuilder();
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
//        dataService.remove(routeId);
        dataService.update(newRoute);
    }

    public void editExitStation(int routeId, Terminal station) {
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
//        dataService.remove(routeId);
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
//        dataService.remove(routeId);
        dataService.update(newRoute);
    }

    public void editExitStationName(int routeId, String name) {
        var oldRoute = getRouteById(routeId);
        var oldExitStation = oldRoute.getEntryStation();
        var newExitStation = new Terminal(oldExitStation.getTime(), name);
        var newRoute = oldRoute.toBuilder()
            .entryStation(newExitStation)
            .build();
//        dataService.remove(routeId);
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
//        dataService.remove(routeId);
        dataService.update(newRoute);
    }

    public void editIntermediateStationName(int routeId, Station station, String name) {
        var oldRoute = getRouteById(routeId);
        if (oldRoute.getIntermediateStations().find(x -> x.getName().equals(name)).isEmpty())
            throw new IllegalArgumentException("Station with name {" + name + "} doesn't exist");

        var oldStation = oldRoute.getIntermediateStations()
            .find(x -> !x.getName().equals(name)).get();
        var newStation = oldStation.toBuilder().name(name).build();
        var stations = oldRoute.getIntermediateStations()
            .filter(x -> !x.getName().equals(name)).remove(oldStation).append(newStation);

        var newRoute = oldRoute.toBuilder()
            .intermediateStations(stations)
            .build();
//        dataService.remove(routeId);
        dataService.update(newRoute);
    }

    public void editRouteName(int routeId, String name) {
        var oldRoute = getRouteById(routeId);
        var newRoute = oldRoute.toBuilder()
            .name(name)
            .build();
//        dataService.remove(routeId);
        dataService.update(newRoute);
    }

    public Route getRouteById(int routeId) {
        var supposedRoute = dataService.getRouteById(routeId);
        if (supposedRoute.isPresent())
            return supposedRoute.get();
        else {
            logException("The route with id " + routeId + " doesn't exist!", new IllegalArgumentException());
            throw new IllegalArgumentException();
        }
    }

    public List<Route> getAllRoutes() {
        return dataService.selectAll();
    }

    private void logException(String msg, Exception e) {
        log.log(Level.SEVERE, msg, e);
    }
}