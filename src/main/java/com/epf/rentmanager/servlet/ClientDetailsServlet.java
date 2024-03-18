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

@WebServlet("/users/details")
public class ClientDetailsServlet extends HttpServlet {
    @Autowired
    private ClientService clientService;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private VehicleService vehicleService;
    @Override
    public void init() throws ServletException {
        super.init();
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int selectedClientId = Integer.parseInt(request.getParameter("id"));
        Client client;
        List<Reservation> reservations;
        List<Vehicle> vehicles = new ArrayList<>();
        List<String> vehicles_names = new ArrayList<>();
        try {
            client = clientService.findById(selectedClientId).get();
            reservations = reservationService.findResaByClientById(selectedClientId);
            for(Reservation reservation: reservations){
                Optional<Vehicle> vehicle = vehicleService.findById(reservation.vehicle_id());
                if (vehicle.isPresent()) {
                    Vehicle foundVehicle = vehicle.get();
                    vehicles_names.add(foundVehicle.constructeur()+" "+foundVehicle.modele());
                    if(!vehicles.contains(foundVehicle)) vehicles.add(foundVehicle);
                }
            }

        } catch (ServiceException e) {
            throw new ServletException(e);
        }

        request.setAttribute("client", client);
        request.setAttribute("reservations", reservations);
        request.setAttribute("vehicles", vehicles);
        request.setAttribute("vehicles_names", vehicles_names);

        this.getServletContext().getRequestDispatcher("/WEB-INF/views/users/details.jsp").forward(request, response);
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.sendRedirect(request.getContextPath() + "/users");
    }
}
