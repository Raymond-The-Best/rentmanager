package com.epf.rentmanager.service;

import java.util.List;
import java.util.Optional;

import com.epf.rentmanager.exception.DaoException;
import com.epf.rentmanager.exception.ServiceException;
import com.epf.rentmanager.model.Reservation;
import com.epf.rentmanager.model.Vehicle;
import com.epf.rentmanager.dao.VehicleDao;
import com.epf.rentmanager.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehicleService implements ServiceTemplate<Vehicle>{

	@Autowired
    private ReservationService reservationService;
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

    @Override
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
            List<Reservation> reservationsASupprimer = reservationService.findResaByVehicleId(vehicle.id());
            for(Reservation reservation: reservationsASupprimer) reservationService.delete(reservation);
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

    public boolean authorizeVehicleUpdate(Vehicle vehicle) {
        return correctSeatNumber(vehicle);

    }
    private boolean correctSeatNumber(Vehicle vehicle){
        return (vehicle.nb_places()>=2 && vehicle.nb_places()<=9);
    }

    public boolean update(Vehicle vehicle) throws ServiceException {
        try {
            vehicleDao.update(vehicle);
        }catch (DaoException e){
            throw new ServiceException();
        }
        return true;
    }
}
