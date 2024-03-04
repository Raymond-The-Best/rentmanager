package com.epf.rentmanager.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.epf.rentmanager.dao.ClientDao;
import com.epf.rentmanager.exception.DaoException;
import com.epf.rentmanager.exception.ServiceException;
import com.epf.rentmanager.model.Client;
import com.epf.rentmanager.utils.IOUtils;
import org.springframework.stereotype.Service;

@Service
public class ClientService implements ServiceTemplate<Client>{
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
			return clientDao.delete(client);
		} catch (DaoException e){
			throw new ServiceException();
		}
	}
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
}
