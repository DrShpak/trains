import dao.filedb.FileDataService;
import io.vavr.collection.List;
import services.RouteManager;
import services.route.Route;
import services.route.Station;
import services.route.Terminal;
import services.route.Train;

import java.util.Date;

public class Main {
    public static void main(String[] args) {
        var route = Route.builder()
            .train(new Train(1))
            .routeId(1)
            .exitStation(new Terminal(new Date(1000000000000L), "kekovka"))
            .entryStation(new Terminal(new Date(1200000000000L), "lolovka"))
            .intermediateStations(List.empty())
            .build();

        var kek = route.toBuilder().routeId(100).build();
//        System.out.println(kek);
//        System.out.println(route);
        var mngr = new RouteManager(new FileDataService());

        try {
            mngr.addIntermediateStation(1,
                Station.builder()
                    .name("gorlovka")
                    .arrivalTime(new Date(1100000000000L))
                    .leaveTime(new Date(  1150000000000L))
                    .build());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
//        mngr.changeTrain(101, new Train(101));
//        mngr.changeRouteId(101, 2);
        System.out.println(mngr.getRouteById(1));
    }
}
