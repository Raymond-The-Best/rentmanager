package com.epf.rentmanager.servlet;

import com.epf.rentmanager.exception.ServiceException;
import com.epf.rentmanager.model.Client;
import com.epf.rentmanager.service.ClientService;
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

@WebServlet("/users/update")
public class ClientUpdateServlet extends HttpServlet {
    @Autowired
    private ClientService clientService;
    @Override
    public void init() throws ServletException {
        super.init();
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String selectedClientIdString = request.getParameter("id");
        if(selectedClientIdString != null) {
            Optional<Client> client;
            int selectedClientId = Integer.parseInt(selectedClientIdString);
            try {
                client = clientService.findById(selectedClientId);
            } catch (ServiceException e) {
                throw new ServletException(e);
            }
            if(client.isPresent()){
                request.setAttribute("client", client.get());
            }
        }
        this.getServletContext().getRequestDispatcher("/WEB-INF/views/users/update.jsp").forward(request, response);
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String selectedClientIdString = request.getParameter("id");
        int selectedClientId = -1;
        Client originalClient = new Client(-1, null, null, null, null);
        if(selectedClientIdString != null) {
            Optional<Client> originalOptionalClient;
            selectedClientId = Integer.parseInt(selectedClientIdString);
            try {
                originalOptionalClient = clientService.findById(selectedClientId);
            } catch (ServiceException e) {
                throw new ServletException(e);
            }
            if (originalOptionalClient.isPresent()) originalClient = originalOptionalClient.get();
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        formatter = formatter.withLocale(Locale.FRANCE);
        LocalDate naissance = LocalDate.parse(request.getParameter("birthday"), formatter);
        Client client = new Client(selectedClientId,
                request.getParameter("last_name"),
                request.getParameter("first_name"),
                request.getParameter("email"),
                naissance);
        boolean validated = false;
        try {
            validated = clientService.authorizeClientUpdate(originalClient, client);
            if (validated) clientService.update(client);
        } catch (ServiceException e) {
            throw new ServletException(e);
        }
        if (validated) response.sendRedirect(request.getContextPath() + "/users");
        else{
            request.setAttribute("clientError", "Client update is not possible. Please choose new email or set age over 18.");
            doGet(request, response);
        }
    }

}
