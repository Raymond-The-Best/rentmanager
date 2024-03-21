package com.epf.rentmanager.servlet;

import com.epf.rentmanager.exception.ServiceException;
import com.epf.rentmanager.model.Client;
import com.epf.rentmanager.model.Reservation;
import com.epf.rentmanager.model.Vehicle;
import com.epf.rentmanager.service.ClientService;
import com.epf.rentmanager.service.ReservationService;
import com.epf.rentmanager.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@WebServlet("/rents")
public class ReservationListServlet extends HttpServlet {
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private VehicleService vehicleService;
    @Override
    public void init() throws ServletException {
        super.init();
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Reservation> reservations;
        List<String> clients = new ArrayList<>();
        List<String> vehicles = new ArrayList<>();
        try {
            reservations = reservationService.findAll();
            for(Reservation reservation: reservations){
                Optional<Client> client = clientService.findById(reservation.client_id());
                Optional<Vehicle> vehicle = vehicleService.findById(reservation.vehicle_id());
                clients.add(
                        (client.isPresent())?
                                client.get().prenom()+" "+client.get().nom():
                                "CLIENT MISSING"
                );
                vehicles.add(
                        (vehicle.isPresent())?
                                vehicle.get().constructeur()+" "+vehicle.get().modele():
                                "VEHICLE MISSING"
                );
            }
        } catch (ServiceException e) {
            throw new ServletException(e);
        }
        request.setAttribute("reservations", reservations);
        request.setAttribute("vehicles_names", vehicles);
        request.setAttribute("clients_names", clients);

        this.getServletContext().getRequestDispatcher("/WEB-INF/views/rents/list.jsp").forward(request, response);
    }
}
