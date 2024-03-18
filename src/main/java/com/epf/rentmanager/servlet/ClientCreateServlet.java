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

@WebServlet("/users/create")
public class ClientCreateServlet extends HttpServlet {
    @Autowired
    private ClientService clientService;
    @Override
    public void init() throws ServletException {
        super.init();
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.getServletContext().getRequestDispatcher("/WEB-INF/views/users/create.jsp").forward(request, response);
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        formatter = formatter.withLocale(Locale.FRANCE);
        LocalDate naissance = LocalDate.parse(request.getParameter("birthday"), formatter);
        Client client = new Client(-1,
                request.getParameter("last_name"),
                request.getParameter("first_name"),
                request.getParameter("email"),
                naissance);
        try {
            clientService.create(client);
        } catch (ServiceException e) {
            throw new ServletException(e);
        }
        response.sendRedirect(request.getContextPath() + "/users");
    }

}
