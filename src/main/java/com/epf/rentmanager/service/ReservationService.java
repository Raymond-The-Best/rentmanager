package com.epf.rentmanager.service;

import java.security.Provider;
import java.util.ArrayList;
import java.util.List;

import com.epf.rentmanager.dao.ReservationDao;
import com.epf.rentmanager.exception.DaoException;
import com.epf.rentmanager.exception.ServiceException;
import com.epf.rentmanager.model.Client;
import com.epf.rentmanager.model.Reservation;
import com.epf.rentmanager.utils.IOUtils;
import org.springframework.stereotype.Service;

@Service
public class ReservationService implements ServiceTemplate<Reservation> {

    private ReservationDao reservationDao;
    public static ReservationService instance;
    private final String serviceName = "reservation";

    private ReservationService(ReservationDao reservationDao) {
        this.reservationDao = reservationDao;
    }
    @Override
    public int create(Reservation reservation) throws ServiceException {
        try {
            return reservationDao.create(reservation);
        } catch (DaoException e) {
            throw new ServiceException();
        }
    }
    @Override
    public int delete(Reservation reservation) throws ServiceException {
        try {
            return reservationDao.delete(reservation);
        } catch (DaoException e) {
            throw new ServiceException();
        }
    }
    public List<Reservation> findResaByClientById(int clientId) throws ServiceException{
        try {
            return reservationDao.findResaByClientId(clientId);
        } catch (DaoException e) {
            throw new ServiceException();
        }
    }
    public List<Reservation> findResaByVehicleId(int vehicleId) throws ServiceException{
        try {
            return reservationDao.findResaByVehicleId(vehicleId);
        } catch (DaoException e) {
            throw new ServiceException();
        }
    }

    @Override
    public List<Reservation> findAll() throws ServiceException {
        try {
            return reservationDao.findAll();
        } catch (DaoException e) {
            throw new ServiceException();
        }
    }

    public String getServiceName() {
        return serviceName;
    }
}
