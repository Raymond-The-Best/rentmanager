package com.epf.rentmanager.ui.cli;

import com.epf.rentmanager.configuration.AppConfiguration;
import com.epf.rentmanager.exception.ServiceException;
import com.epf.rentmanager.model.Client;
import com.epf.rentmanager.model.Reservation;
import com.epf.rentmanager.model.Vehicle;
import com.epf.rentmanager.service.ClientService;
import com.epf.rentmanager.service.ReservationService;
import com.epf.rentmanager.service.ServiceTemplate;
import com.epf.rentmanager.service.VehicleService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.epf.rentmanager.utils.IOUtils.*;

public class CliRequest {
    public static void menu(){
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfiguration.class);

        List<ServiceTemplate> services = new ArrayList<>(Arrays.asList(
                context.getBean(ClientService.class),
                context.getBean(VehicleService.class),
                context.getBean(ReservationService.class)
        ));
        boolean keepMenuAlive = true;
        do{
            StringBuilder optionsMenu = new StringBuilder();
            for (int i = 0; i < services.size(); i++) {
                optionsMenu.append(i+1+". Regarding "+services.get(i).getServiceName()+"\n");
            }
            print(
                    """
                    -----CLI INTERFACE-----\n
                    Select an option below (enter number):\n
                    %s
                    0. Exit""".formatted(optionsMenu.toString())
            );
            int choice = readInt(">>");
            switch(choice){
                case 0:
                    keepMenuAlive = false;
                    break;
                case 1:
                case 2:
                case 3:
                    print("---%s---".formatted(services.get(choice-1).getServiceName().toUpperCase()));
                    subMenu(services.get(choice-1));
                    break;
                default:
                    print("Not an option");
            }
        }while (keepMenuAlive);
    }


    static private void subMenu(ServiceTemplate service){
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfiguration.class);
        ReservationService reservationService = context.getBean(ReservationService.class);

        String name = service.getServiceName();
        String extraResOptions = (name.equals("reservation"))?
                """
                5. See all %ss for a given client
                6. See all %ss for a given vehicle""".formatted(name, name)
                : "";
        print(
                """
                Select an option below (enter number):
                1. Create a %s 
                2. Delete a %s
                3. See all %ss
                4. Find %s by ID
                %s
                0. Exit""".formatted(name,name,name,name, extraResOptions)
        );
        int choice = readInt(">>");
        try {
            switch(choice){
                case 0:
                    break;
                case 1:
                    service.create(
                            switch (service.getServiceName()){
                                case "reservation" -> inputReservationInfo();
                                case "vehicle" -> inputVehicleInfo();
                                case "client" -> inputClientInfo();
                                default -> throw new IllegalStateException("Invalid service");
                            }
                    );
                    break;
                case 2:
                    int collectedId = inputId();
                    service.delete(switch (service.getServiceName()){
                        case "reservation" -> new Reservation(collectedId, -1, -1, null, null);
                        case "vehicle" -> new Vehicle(collectedId, -1, null, null);
                        case "client" -> new Client(collectedId, null, null, null, null);
                        default -> throw new IllegalStateException("Invalid service");
                    });
                    break;
                case 3:
                    service.findAll().forEach(element -> print(">"+element.toString()));
                    break;
                case 4:
                    service.findById(inputId()).ifPresentOrElse(
                            element -> print(element.toString()),
                            () -> print("No match found")
                    );
                    break;
                case 5:
                    int clientId = inputId();
                    List<Reservation> reservations = reservationService.findResaByClientById(clientId);
                    reservations.forEach(element -> print(">"+element.toString()));
                    break;
                case 6:
                    int vehicleId = inputId();
                    reservations = reservationService.findResaByVehicleId(vehicleId);
                    reservations.forEach(element -> print(">"+element.toString()));
                    break;
                default:
                    print("Not an option");
            }
        }catch(ServiceException e){
            throw new RuntimeException(e);
        }

    }
    static private Client inputClientInfo(){
        String nom = readString("Nom:", true);
        String prenom = readString("Prénom:", true);
        String email = readString("Email:", true);
        LocalDate naissance = readDate("Date de naissance:",true);
        return new Client(-1, nom, prenom, email, naissance);
    }
    static private Vehicle inputVehicleInfo(){
        int nb_places = readInt("Nombre de places");
        String constructeur = readString("Constructeur:", true);
        String modele = readString("Modèle:", true);
        return new Vehicle(-1, nb_places, constructeur, modele);
    }
    static private Reservation inputReservationInfo(){
        int vehicle_id = readInt("Vehicle ID:");
        int client_id = readInt("Client ID:");
        LocalDate debut = readDate("Début:",true);
        LocalDate fin = readDate("Fin:",true);
        return new Reservation(-1,vehicle_id, client_id, debut, fin);
    }
    static private int inputId(){
        return readInt("ID:");
    }
}
