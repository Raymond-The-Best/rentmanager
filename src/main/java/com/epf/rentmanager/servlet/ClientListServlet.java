package com.epf.rentmanager.servlet;

import com.epf.rentmanager.exception.ServiceException;
import com.epf.rentmanager.model.Client;
import com.epf.rentmanager.model.Reservation;
import com.epf.rentmanager.service.ClientService;
import com.epf.rentmanager.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.naming.spi.ResolveResult;
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

@WebServlet("/users")
public class ClientListServlet extends HttpServlet {
    @Autowired
    private ClientService clientService;
    @Autowired
    private ReservationService reservationService;
    @Override
    public void init() throws ServletException {
        super.init();
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String selectedClientIdString = request.getParameter("id");
        if(selectedClientIdString != null){
            int selectedClientId = Integer.parseInt(selectedClientIdString);
            boolean delete = request.getParameter("delete").equals("terminate");
            if(delete){
                try {
                    System.out.println("Attempting to delete client "+selectedClientId);
                    clientService.delete(new Client(selectedClientId, null, null, null, null));
                } catch (ServiceException e) {
                    throw new ServletException(e);
                }
            }
        }


        List<Client> clients;
        try {
            clients = clientService.findAll();
        } catch (ServiceException e) {
            throw new ServletException(e);
        }

        request.setAttribute("clients", clients);

        this.getServletContext().getRequestDispatcher("/WEB-INF/views/users/list.jsp").forward(request, response);
    }
}