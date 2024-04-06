package com.epf.rentmanager.servlet;

import com.epf.rentmanager.exception.ServiceException;
import com.epf.rentmanager.model.Client;
import com.epf.rentmanager.model.Vehicle;
import com.epf.rentmanager.service.ClientService;
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
import java.util.Locale;
import java.util.Optional;

@WebServlet("/cars/update")
public class VehicleUpdateServlet extends HttpServlet {
    @Autowired
    private VehicleService vehicleService;
    @Override
    public void init() throws ServletException {
        super.init();
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String selectedVehicleIdString = request.getParameter("id");
        if(selectedVehicleIdString != null) {
            Optional<Vehicle> vehicle;
            int selectedVehicleId = Integer.parseInt(selectedVehicleIdString);
            try {
                vehicle = vehicleService.findById(selectedVehicleId);
            } catch (ServiceException e) {
                throw new ServletException(e);
            }
            if(vehicle.isPresent()){
                request.setAttribute("vehicle", vehicle.get());
            }
        }
        this.getServletContext().getRequestDispatcher("/WEB-INF/views/vehicles/update.jsp").forward(request, response);
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String selectedVehicleIdString = request.getParameter("id");
        int selectedVehicleId = -1;
        Vehicle originalVehicle = new Vehicle(-1, -1, null, null);
        if(selectedVehicleIdString != null) {
            Optional<Vehicle> originalOptionalVehicle;
            selectedVehicleId = Integer.parseInt(selectedVehicleIdString);
            try {
                originalOptionalVehicle = vehicleService.findById(selectedVehicleId);
            } catch (ServiceException e) {
                throw new ServletException(e);
            }
            if (originalOptionalVehicle.isPresent()) originalVehicle = originalOptionalVehicle.get();
        }

        Vehicle vehicle = new Vehicle(selectedVehicleId,
                Integer.parseInt(request.getParameter("seats")),
                request.getParameter("manufacturer"),
                request.getParameter("modele"));
        boolean validated = false;
        try {
            validated = vehicleService.authorizeVehicleUpdate(vehicle);
            if (validated) vehicleService.update(vehicle);
        } catch (ServiceException e) {
            throw new ServletException(e);
        }
        if (validated) response.sendRedirect(request.getContextPath() + "/cars");
        else{
            request.setAttribute("clientError", "Client update is not possible. Please choose new email or set age over 18.");
            doGet(request, response);
        }
    }

}
