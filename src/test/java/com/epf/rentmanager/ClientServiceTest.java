package com.epf.rentmanager;

import com.epf.rentmanager.dao.ClientDao;
import com.epf.rentmanager.exception.DaoException;
import com.epf.rentmanager.exception.ServiceException;
import com.epf.rentmanager.model.Client;
import com.epf.rentmanager.service.ClientService;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class ClientServiceTest {
    @InjectMocks
    private ClientService clientService;

    @Mock
    private ClientDao clientDao;


    @Test
    public void create_should_return_a_client_id() throws DaoException, ServiceException {
        Client client = new Client(-1,
                "Doe",
                "John",
                "john.doe@gmail.com",
                LocalDate.of(1999,05,07));
        // When
        when(this.clientService.create(client)).thenReturn(1);

        // Then
        assertEquals(clientService.create(client),1);
    }
    @Test
    public void findAll_should_fail_when_dao_throws_exception() throws DaoException, ServiceException {
        // When
        when(this.clientService.findAll()).thenThrow(DaoException.class);

        // Then
        assertThrows(ServiceException.class, () -> clientService.findAll());
    }

    @Test
    public void findAll_should_work_when_dao_works() throws DaoException, ServiceException {
        List<Client> listeClient = new ArrayList<Client>();
        // When
        when(this.clientService.findAll()).thenReturn(listeClient);

        // Then
        assertEquals(clientService.findAll(),listeClient);

    }


    @Test
    public void findById_should_return_client_when_exists() throws DaoException, ServiceException {
        Client client = new Client(0,
                "Doe",
                "John",
                "john.doe@gmail.com",
                LocalDate.of(1999,05,07));

        //When
        when(this.clientService.findById(client.id())).thenReturn(Optional.of(client));

        //Then
        assertEquals(clientService.findById(client.id()),Optional.of(client));

    }

    @Test
    public void findById_should_return_empty_when_doesnt_exists() throws DaoException, ServiceException {
        //When
        when(this.clientService.findById(0)).thenReturn(Optional.empty());

        //Then
        assertEquals(clientService.findById(0),Optional.empty());

    }

}

