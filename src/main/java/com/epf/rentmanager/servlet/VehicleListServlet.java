package com.epf.rentmanager.servlet;

import com.epf.rentmanager.exception.ServiceException;
import com.epf.rentmanager.model.Vehicle;
import com.epf.rentmanager.service.VehicleService;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@WebServlet("/cars")
public class VehicleListServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Create an ArrayList of Vehicle objects
        List<Vehicle> vehicles;
        try {
            vehicles = VehicleService.getInstance().findAll();
        } catch (ServiceException e) {
            throw new ServletException(e);
        }

        request.setAttribute("vehicles", vehicles);
        //vehicles.forEach(element -> System.out.println(element));

        this.getServletContext().getRequestDispatcher("/WEB-INF/views/vehicles/list.jsp").forward(request, response);
    }
}
