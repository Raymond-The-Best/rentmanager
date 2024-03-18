package com.epf.rentmanager.service;

import java.security.Provider;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.epf.rentmanager.dao.ReservationDao;
import com.epf.rentmanager.exception.DaoException;
import com.epf.rentmanager.exception.ServiceException;
import com.epf.rentmanager.model.Client;
import com.epf.rentmanager.model.Reservation;
import com.epf.rentmanager.model.Vehicle;
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
    @Override
    public Optional<Reservation> findById(int id) throws ServiceException {
        try {
            return reservationDao.findById(id);
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

    public int count() throws ServiceException {
        try {
            return reservationDao.count();
        } catch (DaoException e) {
            throw new ServiceException();
        }
    }
    public boolean authorizeReservation(Reservation reservation) throws ServiceException {
        List<Reservation> existingVehicleRes = findResaByVehicleId(reservation.vehicle_id());

        // Interdire les reservations qui sont comprises entre deux dates déjà reservées
        if (!validReservationPeriod(reservation, existingVehicleRes)) return false;

        // Interdire les réservations de plus de 7 jours
        if (!validReservationLength(reservation)) return false;

        // Étudier les réservations sur les 30 derniers jours
        if (!validReservationContinuity(reservation, existingVehicleRes)) return false;

        return true;

    }
    private boolean validReservationPeriod(Reservation reservation, List<Reservation> reservations){
        for (Reservation existingReservation : reservations) {
            if (reservation.debut().isBefore(existingReservation.fin()) &&
                    reservation.fin().isAfter(existingReservation.debut())) {
                return false;
            }
        }
        return true;
    }
    private boolean validReservationLength(Reservation reservation){
        return reservation.debut().until(reservation.fin(), ChronoUnit.DAYS) <= 7;
    }

    private boolean validReservationContinuity(Reservation reservation, List<Reservation> reservations){
        reservations.add(reservation);
        reservations.sort(Comparator.comparing(Reservation::debut));
        int continuousDays = 0;
        for (int i = 0; i < reservations.size() - 1; i++) {
            Reservation current = reservations.get(i);
            Reservation next = reservations.get(i + 1);

            if (current.fin().plusDays(1).isEqual(next.fin())) {
                continuousDays += current.debut().until(next.fin()).getDays();
                if (continuousDays >= 30) {
                    return false;
                }
            } else {
                continuousDays = 0;
            }
        }
        return true;
    }
}
