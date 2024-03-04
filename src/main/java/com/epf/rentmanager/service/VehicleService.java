package com.epf.rentmanager.service;

import java.util.List;
import java.util.Optional;

import com.epf.rentmanager.exception.DaoException;
import com.epf.rentmanager.exception.ServiceException;
import com.epf.rentmanager.model.Vehicle;
import com.epf.rentmanager.dao.VehicleDao;
import com.epf.rentmanager.utils.IOUtils;
import org.springframework.stereotype.Service;

@Service
public class VehicleService implements ServiceTemplate<Vehicle>{

	private VehicleDao vehicleDao;
    private String serviceName = "vehicle";
	
	private VehicleService(VehicleDao vehicleDao) {
		this.vehicleDao = vehicleDao;
	}
	
	@Override
	public int create(Vehicle vehicle) throws ServiceException {
        if(IOUtils.isUndefined(vehicle.constructeur())) throw new ServiceException();
        try {
            return vehicleDao.create(vehicle);
        } catch (DaoException e) {
            throw new ServiceException();
        }
    }

	public Optional<Vehicle> findById(int id) throws ServiceException {
        try {
            return vehicleDao.findById(id);
        } catch (DaoException e) {
            throw new ServiceException();
        }
    }
    @Override
    public int delete(Vehicle vehicle) throws ServiceException{
        try {
            return vehicleDao.delete(vehicle);
        } catch (DaoException e) {
            throw new ServiceException();
        }
    }
    @Override
	public List<Vehicle> findAll() throws ServiceException {
        try {
            return vehicleDao.findAll();
        } catch (DaoException e) {
            throw new ServiceException();
        }
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    public int count() throws ServiceException{
        try {
            return vehicleDao.count();
        } catch (DaoException e) {
            throw new ServiceException();
        }
    }
}
