package com.epf.rentmanager.servlet;

import com.epf.rentmanager.exception.ServiceException;
import com.epf.rentmanager.model.Client;
import com.epf.rentmanager.model.Reservation;
import com.epf.rentmanager.model.Vehicle;
import com.epf.rentmanager.service.ReservationService;
import com.epf.rentmanager.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@WebServlet("/cars")
public class VehicleListServlet extends HttpServlet {
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private ReservationService reservationService;
    @Override
    public void init() throws ServletException {
        super.init();
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String selectedVehicleIdString = request.getParameter("id");
        if(selectedVehicleIdString != null){
            int selectedVehicleId = Integer.parseInt(selectedVehicleIdString);
            boolean delete = request.getParameter("delete").equals("terminate");
            if(delete){
                try {
                    System.out.println("Attempting to delete vehicle "+selectedVehicleId);
                    vehicleService.delete(new Vehicle(selectedVehicleId, -1, null, null));
                } catch (ServiceException e) {
                    throw new ServletException(e);
                }
            }
        }

        List<Vehicle> vehicles;
        try {
            vehicles = vehicleService.findAll();
        } catch (ServiceException e) {
            throw new ServletException(e);
        }

        request.setAttribute("vehicles", vehicles);

        this.getServletContext().getRequestDispatcher("/WEB-INF/views/vehicles/list.jsp").forward(request, response);
    }
}
