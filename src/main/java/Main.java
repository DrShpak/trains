import dao.dbDAO.PostgresDAO;
import dao.filedb.FileDataService;
import models.Station;
import models.Train;
import services.RouteManager;

import java.util.Date;

public class Main {
    public static void main(String[] args) {
//        var mngr = new RouteManager(new FileDataService());

        var mngrDb = new RouteManager(new PostgresDAO());
//        var routes = mngr.getDataService().selectAll();

//        mngrDb.addRoute(routes.get(3));
        mngrDb.getAllRoutes().forEach(System.out::println);
    }
}
