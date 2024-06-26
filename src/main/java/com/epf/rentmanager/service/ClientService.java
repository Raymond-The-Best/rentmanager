package com.epf.rentmanager.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.epf.rentmanager.dao.ClientDao;
import com.epf.rentmanager.exception.DaoException;
import com.epf.rentmanager.exception.ServiceException;
import com.epf.rentmanager.model.Client;
import com.epf.rentmanager.model.Reservation;
import com.epf.rentmanager.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService implements ServiceTemplate<Client>{
	@Autowired
	private ReservationService reservationService;
	private ClientDao clientDao;
	private String serviceName = "client";
	private ClientService(ClientDao clientDao) {
		this.clientDao = clientDao;
	}
	@Override
	public int create(Client client) throws ServiceException {
		if(IOUtils.isUndefined(client.prenom()) || IOUtils.isUndefined(client.nom())) throw new ServiceException();
		Client upperCasedClient = new Client(client.id(),
				client.nom().toUpperCase(),
				client.prenom(),
				client.email(),
				client.naissance());
        try {return clientDao.create(upperCasedClient);}
		catch (DaoException e) {throw new ServiceException();}
	}
	@Override
	public int delete(Client client) throws ServiceException{
		try {
			List<Reservation> reservationsASupprimer = reservationService.findResaByClientById(client.id());
			for(Reservation reservation: reservationsASupprimer) reservationService.delete(reservation);
			return clientDao.delete(client);
		} catch (DaoException e){
			throw new ServiceException();
		}
	}
	@Override
	public Optional<Client> findById(int id) throws ServiceException {
        try {
            return clientDao.findById(id);
        } catch (DaoException e) {
            throw new ServiceException();
        }
    }

	@Override
	public String getServiceName() {
		return serviceName;
	}

	@Override
	public List<Client> findAll() throws ServiceException {
        try {
            return clientDao.findAll();
        } catch (DaoException e) {
            throw new ServiceException();
        }

    }
	public List<String> getAllEmail() throws ServiceException {
		try{
			return clientDao.getAllEmail();
		} catch (DaoException e){
			throw new ServiceException();
		}
	}
	public boolean update(Client client) throws ServiceException {
		try {
			return clientDao.update(client);
		} catch (DaoException e){
			throw new ServiceException();
		}
	}

	public int count() throws ServiceException {
		try {
			return clientDao.count();
		} catch (DaoException e) {
			throw new ServiceException();
		}
	}
	public boolean authorizeClientCreation(Client client){
		if(!isOver18YearsOld(client)) return false;
		if(!isNewEmailAddress(client)) return false;
		return true;
	}
	public boolean authorizeClientUpdate(Client originalClient, Client newClient){
		if(!isOver18YearsOld(newClient)) return false;
		if(!isNewEmailAddress(newClient) && !originalClient.email().equals(newClient.email())) return false;
		return true;
	}
	private boolean isOver18YearsOld(Client client){
		return ChronoUnit.YEARS.between(client.naissance(), LocalDate.now()) >= 18;
	}
	private boolean isNewEmailAddress(Client client){
        try {
            return !clientDao.getAllEmail().contains(client.email());
        } catch (DaoException e) {
            throw new RuntimeException(e);
        }
    }
}
