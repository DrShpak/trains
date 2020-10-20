package dao.filedb;

import dao.DataService;
import io.vavr.collection.List;
import models.Route;
import models.Station;
import models.Terminal;
import models.Train;

import java.util.Date;
import java.util.Optional;

public class FileDataService implements DataService {
    private List<Route> routes; // только для 1 аттестации

    public FileDataService() {
        this.routes = fillTestData();
    }

    public void update(int routeId) {

    }

    public List<Route> selectAll() {
        return routes;
    }

    @Override
    public void insert(Route route) {
        routes = routes.append(route);
    }

    public void remove(int routeId) {
        routes = routes.removeFirst(x -> x.getRouteId() == routeId);
    }

    @Override
    public void update(Route route) {
        routes = routes.append(route);
    }

    @Override
    public Optional<Route> getRouteById(int routeId) {
        return routes.asJava().stream().filter(x -> x.getRouteId() == routeId).findAny();
    }

    private List<Route> fillTestData() {
        var idCount = 1;
        var trainCount = 1;
        return List.of
            (
                Route.builder()
                    .name("route 1")
                    .train(new Train(trainCount++))
                    .routeId(idCount++)
                    .exitStation(new Terminal(new Date(1000000000000L), "kekovka"))
                    .entryStation(new Terminal(new Date(1200000000000L), "lolovka"))
                    .intermediateStations(List.of
                        (
                            Station.builder()
                                .name("промежуточная 1")
                                .arrivalTime(new Date(1000000001000L))
                                .leaveTime(new Date(1000000003000L))
                                .build(),
                            Station.builder()
                                .name("промежуточная 2")
                                .arrivalTime(new Date(1000000071000L))
                                .leaveTime(new Date(  1000000881000L))
                                .build(),
                            Station.builder()
                                .name("промежуточная 3")
                                .arrivalTime(new Date(1000071000100L))
                                .leaveTime(new Date(  1090022030000L))
                                .build()
                        ))
                    .build(),
                Route.builder()
                    .name("route 2")
                    .train(new Train(trainCount++))
                    .routeId(idCount++)
                    .exitStation(new Terminal(new Date( 10000000000L), "Машмет"))
                    .entryStation(new Terminal(new Date(30000000000L), "Россошь"))
                    .intermediateStations(List.of
                        (
                            Station.builder()
                                .name("промежуточная 1")
                                .arrivalTime(new Date(10000002000L))
                                .leaveTime(new Date(  100000088000L))
                                .build(),
                            Station.builder()
                                .name("промежуточная 2")
                                .arrivalTime(new Date(10005000000L))
                                .leaveTime(new Date(10009000000L))
                                .build(),
                            Station.builder()
                                .name("промежуточная 3")
                                .arrivalTime(new Date(11000000000L))
                                .leaveTime(new Date(12000000000L))
                                .build()
                        ))
                    .build(),
                Route.builder()
                    .name("route 3")
                    .train(new Train(trainCount++))
                    .routeId(idCount++)
                    .exitStation(new Terminal(new Date(1000000L), "Москва"))
                    .entryStation(new Terminal(new Date(5000000L), "Питер"))
                    .intermediateStations(List.of
                        (
                            Station.builder()
                                .name("промежуточная 1")
                                .arrivalTime(new Date(1000700L))
                                .leaveTime(new Date(1000900L))
                                .build(),
                            Station.builder()
                                .name("промежуточная 2")
                                .arrivalTime(new Date(1100000L))
                                .leaveTime(new Date(1700000L))
                                .build(),
                            Station.builder()
                                .name("промежуточная 3")
                                .arrivalTime(new Date(3000000L))
                                .leaveTime(new Date(4400000L))
                                .build()
                        ))
                    .build(),
                Route.builder()
                    .name("route 4")
                    .train(new Train(trainCount++))
                    .routeId(idCount++)
                    .exitStation(new Terminal(new Date(60000000L), "Сочи"))
                    .entryStation(new Terminal(new Date(100000000L), "Воронеж"))
                    .intermediateStations(List.of
                        (
                            Station.builder()
                                .name("промежуточная 1")
                                .arrivalTime(new Date(70000000L))
                                .leaveTime(new Date(80000000L))
                                .build(),
                            Station.builder()
                                .name("промежуточная 2")
                                .arrivalTime(new Date(89000000L))
                                .leaveTime(new Date(95000000L))
                                .build()
                        ))
                    .build()
            );
    }
}
