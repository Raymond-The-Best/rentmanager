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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@WebServlet("/rents/update")
public class ReservationUpdateServlet extends HttpServlet {
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
        String selectedReservIdString = request.getParameter("id");
        List<Client> clients;
        List<Vehicle> vehicles;
        try {
            clients = clientService.findAll();
            vehicles = vehicleService.findAll();
        } catch (ServiceException e) {
            throw new ServletException(e);
        }
        request.setAttribute("clients", clients);
        request.setAttribute("vehicles", vehicles);
        if(selectedReservIdString != null) {
            Optional<Reservation> reservation;
            int selectedReservId = Integer.parseInt(selectedReservIdString);
            try {
                reservation = reservationService.findById(selectedReservId);
            } catch (ServiceException e) {
                throw new ServletException(e);
            }
            if(reservation.isPresent()){
                request.setAttribute("reservation", reservation.get());
            }
        }
        this.getServletContext().getRequestDispatcher("/WEB-INF/views/rents/update.jsp").forward(request, response);
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String selectedReservIdString = request.getParameter("id");
        int selectedReservId = -1;
        Reservation originalReserv = new Reservation(-1, -1, -1, null, null);
        if(selectedReservIdString != null) {
            Optional<Reservation> originalOptionalReserv;
            selectedReservId = Integer.parseInt(selectedReservIdString);
            try {
                originalOptionalReserv = reservationService.findById(selectedReservId);
            } catch (ServiceException e) {
                throw new ServletException(e);
            }
            if (originalOptionalReserv.isPresent()) originalReserv = originalOptionalReserv.get();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        formatter = formatter.withLocale(Locale.FRANCE);
        LocalDate debut = LocalDate.parse(request.getParameter("begin"), formatter);
        LocalDate fin = LocalDate.parse(request.getParameter("end"), formatter);

        Reservation reservation = new Reservation(selectedReservId,
                Integer.parseInt(request.getParameter("car")),
                Integer.parseInt(request.getParameter("client")),
                debut,
                fin
        );
        try {
            reservationService.update(reservation);
        } catch (ServiceException e) {
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + "/rents");
    }

}
