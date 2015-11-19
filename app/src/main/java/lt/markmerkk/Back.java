package lt.markmerkk;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;
import jfxtras.scene.control.agenda.Agenda;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.TreeMap;

public class Back extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Agenda agenda = new Agenda();
        setupAgenda(agenda);
//        Parent root = FXMLLoader.load(getClass().getResource("/sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(agenda, 300, 275));

        primaryStage.show();
    }

    private void setupAgenda(Agenda agenda) {
        // setup appointment groups
        final Map<String, Agenda.AppointmentGroup> lAppointmentGroupMap = new TreeMap<String, Agenda.AppointmentGroup>();
        for (Agenda.AppointmentGroup lAppointmentGroup : agenda.appointmentGroups()) {
            lAppointmentGroupMap.put(lAppointmentGroup.getDescription(), lAppointmentGroup);
        }

        // accept new appointments
//        agenda.newAppointmentCallbackProperty().set(new Callback<Agenda.LocalDateTimeRange, Agenda.Appointment>()
//        {
//            @Override
//            public Agenda.Appointment call(Agenda.LocalDateTimeRange dateTimeRange)
//            {
//                return new Agenda.AppointmentImplLocal()
//                        .withStartLocalDateTime( dateTimeRange.getStartLocalDateTime() )
//                        .withEndLocalDateTime( dateTimeRange.getEndLocalDateTime() )
//                        .withSummary("new")
//                        .withDescription("new")
//                        .withAppointmentGroup(lAppointmentGroupMap.get("group01"));
//            }
//        });

        // initial set
        LocalDate lTodayLocalDate = LocalDate.now();
        LocalDate lTomorrowLocalDate = LocalDate.now().plusDays(1);
        int idx = 0;
        final String lIpsum = "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Vestibulum tortor quam, feugiat vitae, ultricies eget, tempor sit amet, ante. Donec eu libero sit amet quam egestas semper. Aenean ultricies mi vitae est. Mauris placerat eleifend leo. Quisque sit amet est et sapien ullamcorper pharetra. Vestibulum erat wisi, condimentum sed, commodo vitae, ornare sit amet, wisi. Aenean fermentum, elit eget tincidunt condimentum, eros ipsum rutrum orci, sagittis tempus lacus enim ac dui. Donec non enim in turpis pulvinar facilisis. Ut felis. Praesent dapibus, neque id cursus faucibus, tortor neque egestas augue, eu vulputate magna eros eu erat. Aliquam erat volutpat. Nam dui mi, tincidunt quis, accumsan porttitor, facilisis luctus, metus";
        LocalDateTime lMultipleDaySpannerStartDateTime = lTodayLocalDate.atStartOfDay().plusHours(5);
        lMultipleDaySpannerStartDateTime = lMultipleDaySpannerStartDateTime.minusDays(lMultipleDaySpannerStartDateTime.getDayOfWeek().getValue() > 3 && lMultipleDaySpannerStartDateTime.getDayOfWeek().getValue() < 7 ? 3 : -1);
        LocalDateTime lMultipleDaySpannerEndDateTime = lMultipleDaySpannerStartDateTime.plusDays(2);

        Agenda.Appointment[] lTestAppointments = new Agenda.Appointment[]{
                new Agenda.AppointmentImplLocal()
                        .withStartLocalDateTime(LocalDateTime.of(lTodayLocalDate, LocalTime.of(8, 00)))
                        .withEndLocalDateTime(LocalDateTime.of(lTodayLocalDate, LocalTime.of(11, 30)))
                        .withSummary("A")
                        .withDescription("A much longer test description")
                        .withAppointmentGroup(lAppointmentGroupMap.get("group07"))
        };
        agenda.appointments().addAll(lTestAppointments);

        agenda.setActionCallback(new Callback<Agenda.Appointment, Void>() {
            @Override
            public Void call(Agenda.Appointment param) {
                System.out.println("Appointment: "+param);
                return null;
            }
        });
        // action
//        agenda.setActionCallback( (appointment) -> {
//            System.out.println("AGenda click");
//            showPopup(agenda, "Action on " + appointment);
//            return null;
//        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
