import dao.filedb.FileDataService;
import models.Station;
import models.Train;
import services.RouteManager;

import java.util.Date;

public class Main {
    public static void main(String[] args) {
        var mngr = new RouteManager(new FileDataService());


        //добавление промежуточных станций
        System.out.println("до добавления новой промежуточной станции:");
        System.out.println(mngr.getRouteById(1));
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
        System.out.println("после добавления промежуточной станции");
        System.out.println(mngr.getRouteById(1) + "\n\n");


        //изменение поезда
        System.out.println("до имзенения поезда маршрута");
        System.out.println(mngr.getRouteById(2));
        try {
            mngr.editTrain(2, new Train(102));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("после изменения поезда маршрута");
        System.out.println(mngr.getRouteById(2) + "\n\n");


        //изменение имени маршрута
        System.out.println("до имзенения поезда маршрута");
        System.out.println(mngr.getRouteById(1));
        try {
            mngr.editRouteName(1, "Новый супер поезд от РЖД");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("после изменения поезда маршрута");
        System.out.println(mngr.getRouteById(1) + "\n\n");
    }
}
