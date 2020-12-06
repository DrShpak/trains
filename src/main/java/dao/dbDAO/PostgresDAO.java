package dao.dbDAO;

import dao.DataService;
import io.vavr.collection.List;
import models.Route;
import models.Station;
import models.Terminal;
import models.Train;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;

public class PostgresDAO implements DataService {
    private final static String PATH_TO_QUERIES = "src/main/resources/postgresQueries.properties";
    private final static Properties properties = new Properties();
    static {
        try {
            properties.load(new FileInputStream(PATH_TO_QUERIES));
        } catch (IOException e) {
            //todo log it
        }
    }

    private final ConnectionBuilder builder = new PostgresConnectionBuilder();

    @Override
    public void update(int routeId) {
        //nothing
    }

    //todo баг: не обновляются промежуточные станции
    @Override
    public void update(Route route) {
        try (var con = builder.getConnection();
             var stm = con.prepareStatement(properties.getProperty("UPDATE"))) {
            fillStatement(stm, route);
            stm.setInt(7, route.getRouteId());

            stm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(Route route) {
        try (var con = builder.getConnection();
             var stm = con.prepareStatement(properties.getProperty("INSERT_ROUTE"), new String[]{"route_id"})) {
            fillStatement(stm, route);
            stm.executeUpdate();

            try {
                con.setAutoCommit(false);
                var gk = stm.getGeneratedKeys();
                var routeId = -1;
                while (gk.next()) {
                    routeId = gk.getInt("ROUTE_ID");
                }
                insertIntermediateStations(con, route, routeId);
                con.commit();
            } catch (SQLException ex) {
                con.rollback();
            }
        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    private void insertIntermediateStations(Connection con, Route route, int routeId) {
        try (var stm = con.prepareStatement(properties.getProperty("INSERT_INTERMEDIATE_STATIONS"))) {
            for (Station station : route.getIntermediateStations()) {
                stm.setString(1, station.getName());
                stm.setString(2, String.valueOf(station.getArrivalTime()));
                stm.setString(3, String.valueOf(station.getLeaveTime()));
                stm.setInt(4, routeId);

                stm.addBatch();
            }

            stm.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(int routeId) {
        try (var con = builder.getConnection();
             var stm = con.prepareStatement(properties.getProperty("DELETE"))) {
            stm.setInt(1, routeId);
            stm.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public Optional<Route> getRouteById(int routeId) {
        Route route = null;
        try (var con = builder.getConnection();
             var routeStm = con.prepareStatement(properties.getProperty("SELECT_BY_ROUTE_ID"))) {
            routeStm.setInt(1, routeId);
            var routesSet = routeStm.executeQuery();
            while (routesSet.next()) {
                var stationsStm = con.prepareStatement(properties.getProperty("SELECT_INTERMEDIATE_STATIONS"));
                stationsStm.setInt(1, routesSet.getInt("route_id"));
                var intermediateStationsSet = stationsStm.executeQuery();
                route = parseRoute(routesSet, intermediateStationsSet);
            }
        } catch (SQLException | ParseException throwables) {
            throwables.printStackTrace();
        }

        return route != null ? Optional.of(route) : Optional.empty();
    }

    @Override
    public List<Route> selectAll() {
        List<Route> routes = List.empty();
        try (var con = builder.getConnection();
             var sqlRoutesSet = con.createStatement().executeQuery(properties.getProperty("SELECT_ROUTES"))) {
            while (sqlRoutesSet.next()) {
                var stationsStm = con.prepareStatement(properties.getProperty("SELECT_INTERMEDIATE_STATIONS"));
                stationsStm.setInt(1, sqlRoutesSet.getInt("route_id"));
                var sqlIntermediateStationsSet = stationsStm.executeQuery();
                routes = routes.append(parseRoute(sqlRoutesSet, sqlIntermediateStationsSet));
            }
        } catch (SQLException | ParseException e) {
            //todo log info
        }
        return routes;
    }

    private Route parseRoute(ResultSet routesSet, ResultSet intermediateStationsSet) throws SQLException, ParseException {
        return Route.builder()
            .routeId(routesSet.getInt("route_id"))
            .name(routesSet.getString("route_name"))
            .exitStation(new Terminal(
                parseDate(routesSet.getString("leave_time")),
                routesSet.getString("exit_station")
            ))
            .entryStation(new Terminal(
                parseDate(routesSet.getString("arrival_time")),
                routesSet.getString("entry_station")
            ))
            .train(new Train(routesSet.getInt("train")))
            .intermediateStations(parseStations(intermediateStationsSet))
            .build();
    }

    private Date parseDate(String date) throws ParseException {
        //Mon Nov 09 23:27:08 MSK 2020
        SimpleDateFormat format1 = new SimpleDateFormat("MMM dd HH:mm:ss yyyy");
        try {
            return format1.parse(date);
        } catch (ParseException e) {
            // если дата записана в дефолтном формате java.util.Date
            var temp = date.split(" ");
            date = temp[1] + " " + temp[2] + " " + temp[3] + " " + temp[5];
            return format1.parse(date);
        }
    }

    private List<Station> parseStations(ResultSet stationsSet) throws SQLException, ParseException {
        List<Station> stations = List.empty();
        while (stationsSet.next()) {
            stations = stations.append(Station.builder()
                .name(stationsSet.getString("station_name"))
                .arrivalTime(parseDate(stationsSet.getString("arrival_date")))
                .leaveTime(parseDate(stationsSet.getString("leave_date")))
                .build());
        }
        return stations;
    }

    private void fillStatement(PreparedStatement stm, Route route) throws SQLException {
        stm.setString(1, route.getName());
        stm.setString(2, route.getExitStation().getName());
        stm.setString(3, String.valueOf(route.getExitStation().getTime()));
        stm.setString(4, route.getEntryStation().getName());
        stm.setString(5, String.valueOf(route.getEntryStation().getTime()));
        stm.setInt(6, route.getTrain().getNumber());
    }
}