package com.epf.rentmanager.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.epf.rentmanager.exception.DaoException;
import com.epf.rentmanager.model.Client;
import com.epf.rentmanager.model.Reservation;
import com.epf.rentmanager.persistence.ConnectionManager;

public class ReservationDao {

	private static ReservationDao instance = null;
	private ReservationDao() {}
	public static ReservationDao getInstance() {
		if(instance == null) {
			instance = new ReservationDao();
		}
		return instance;
	}
	
	private static final String CREATE_RESERVATION_QUERY = "INSERT INTO Reservation(client_id, vehicle_id, debut, fin) VALUES(?, ?, ?, ?);";
	private static final String DELETE_RESERVATION_QUERY = "DELETE FROM Reservation WHERE id=?;";
	private static final String FIND_RESERVATIONS_BY_CLIENT_QUERY = "SELECT id, vehicle_id, debut, fin FROM Reservation WHERE client_id=?;";
	private static final String FIND_RESERVATIONS_BY_VEHICLE_QUERY = "SELECT id, client_id, debut, fin FROM Reservation WHERE vehicle_id=?;";
	private static final String FIND_RESERVATIONS_QUERY = "SELECT id, client_id, vehicle_id, debut, fin FROM Reservation;";
		
	public int create(Reservation reservation) throws DaoException {
		// Créer une réservation dans la BDD à partir d'un objet Reservation
		// Renvoie l'ID de l'instance créée
		try (Connection connexion = ConnectionManager.getConnection();
			 PreparedStatement statement = connexion.prepareStatement(CREATE_RESERVATION_QUERY, PreparedStatement.RETURN_GENERATED_KEYS);){
			statement.setInt(1, reservation.client_id());
			statement.setInt(2, reservation.vehicle_id());
			statement.setDate(3, Date.valueOf(reservation.debut()));
			statement.setDate(4, Date.valueOf(reservation.fin()));
			statement.executeUpdate();
			connexion.commit();
			ResultSet resultat = statement.getGeneratedKeys();
			int key=-1;
			if (resultat.next()) {
				key = resultat.getInt(1);
			}
			connexion.close();
			return key;


		} catch (SQLException e) {
			throw new DaoException(e.toString());
		}
	}
	
	public int delete(Reservation reservation) throws DaoException {
			try (Connection connexion = ConnectionManager.getConnection();
				 PreparedStatement statement = connexion.prepareStatement(DELETE_RESERVATION_QUERY);){
				statement.setInt(1, reservation.id());
				return statement.executeUpdate();
			} catch (SQLException e) {
				throw new DaoException(e.toString());
			}
	}
	public List<Reservation> findResaByClientId(int clientId) throws DaoException {
		List<Reservation> listeDeReservations = new ArrayList<>();
		try (Connection connexion = ConnectionManager.getConnection();
			 PreparedStatement statement = connexion.prepareStatement(FIND_RESERVATIONS_BY_CLIENT_QUERY);){
			statement.setInt(1, clientId);
			statement.execute();
			ResultSet resultat = statement.getResultSet();
			Reservation reservation;
			while(resultat.next()){
				reservation = new Reservation(resultat.getInt("id"),
						resultat.getInt("vehicle_id"),
						clientId,
						resultat.getDate("debut").toLocalDate(),
						resultat.getDate("fin").toLocalDate()
						);
				listeDeReservations.add(reservation);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return listeDeReservations;
	}
	
	public List<Reservation> findResaByVehicleId(int vehicleId) throws DaoException {
		List<Reservation> listeDeReservations = new ArrayList<>();
		try (Connection connexion = ConnectionManager.getConnection();
			 PreparedStatement statement = connexion.prepareStatement(FIND_RESERVATIONS_BY_VEHICLE_QUERY);){
			statement.setInt(1, vehicleId);
			statement.execute();
			ResultSet resultat = statement.getResultSet();
			Reservation reservation;
			while(resultat.next()){
				reservation=new Reservation(resultat.getInt("id"),
						vehicleId,
						resultat.getInt("client_id"),
						resultat.getDate("debut").toLocalDate(),
						resultat.getDate("fin").toLocalDate());
				listeDeReservations.add(reservation);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return listeDeReservations;
	}

	public List<Reservation> findAll() throws DaoException {
		List<Reservation> listeDeReservations = new ArrayList<>();
		try (Connection connexion = ConnectionManager.getConnection();
			 PreparedStatement statement = connexion.prepareStatement(FIND_RESERVATIONS_QUERY);){
			statement.execute();
			ResultSet resultat = statement.getResultSet();
			Reservation reservation;
			while(resultat.next()){
				reservation=new Reservation(resultat.getInt("id"),
						resultat.getInt("client_id"),
						resultat.getInt("vehicle_id"),
						resultat.getDate("debut").toLocalDate(),
						resultat.getDate("fin").toLocalDate());
				listeDeReservations.add(reservation);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return listeDeReservations;
	}
}
