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

import static com.epf.rentmanager.utils.IOUtils.print;

@WebServlet("/rents/create")
public class ReservationCreateServlet extends HttpServlet {
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

        this.getServletContext().getRequestDispatcher("/WEB-INF/views/rents/create.jsp").forward(request, response);
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        formatter = formatter.withLocale(Locale.FRANCE);
        LocalDate debut = LocalDate.parse(request.getParameter("begin"), formatter);
        LocalDate fin = LocalDate.parse(request.getParameter("end"), formatter);

        print("Selected vehicle id:"+request.getParameter(("car")));
        print("Selected client id:"+request.getParameter(("client")));

        Reservation reservation = new Reservation(-1,
                Integer.parseInt(request.getParameter("car")),
                Integer.parseInt(request.getParameter("client")),
                debut,
                fin
        );
        try {
            reservationService.create(reservation);
        } catch (ServiceException e) {
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + "/rents");
    }

}
