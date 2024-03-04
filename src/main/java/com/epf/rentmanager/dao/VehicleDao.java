package com.epf.rentmanager.dao;

import com.epf.rentmanager.exception.DaoException;
import com.epf.rentmanager.model.Client;
import com.epf.rentmanager.model.Vehicle;
import com.epf.rentmanager.persistence.ConnectionManager;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class VehicleDao {
	private VehicleDao() {}
	private static final String CREATE_VEHICLE_QUERY = "INSERT INTO Vehicle(constructeur, modele, nb_places) VALUES(?, ?, ?);";
	private static final String DELETE_VEHICLE_QUERY = "DELETE FROM Vehicle WHERE id=?;";
	private static final String FIND_VEHICLE_QUERY = "SELECT id, constructeur, modele, nb_places FROM Vehicle WHERE id=?;";
	private static final String FIND_VEHICLES_QUERY = "SELECT id, constructeur, modele, nb_places FROM Vehicle;";
	private static final String FIND_NB_VEHICLES_QUERY = "SELECT COUNT(*) AS vehicle_count FROM Vehicle;";
	
	public int create(Vehicle vehicle) throws DaoException {
		// Créer un véhicule dans la BDD à partir d'un objet Vehicle
		// Renvoie l'ID de l'instance créée
		try (Connection connexion = ConnectionManager.getConnection();
			 PreparedStatement statement = connexion.prepareStatement(CREATE_VEHICLE_QUERY, PreparedStatement.RETURN_GENERATED_KEYS);){
			statement.setString(1, vehicle.constructeur());
			statement.setString(2, vehicle.modele());
			statement.setInt(3, vehicle.nb_places());
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

	public int delete(Vehicle vehicle) throws DaoException {
		try {
			Connection connexion = ConnectionManager.getConnection();
			PreparedStatement statement = connexion.prepareStatement(DELETE_VEHICLE_QUERY);
			statement.setInt(1, vehicle.id());
			statement.execute();
			return 1;
		} catch (SQLException e) {
			throw new DaoException(e.toString());
		}
	}

	public Optional<Vehicle> findById(int id) throws DaoException {
		try (Connection connexion = ConnectionManager.getConnection();
			 PreparedStatement statement = connexion.prepareStatement(FIND_VEHICLE_QUERY);){
			statement.setInt(1, id);
			statement.executeUpdate();
			ResultSet resultat = statement.getResultSet();
			while(resultat.next()){
				Vehicle vehicle = new Vehicle(id,
						resultat.getInt("nb_places"),
						resultat.getString("constructeur"),
						resultat.getString("modele"));
				return Optional.of(vehicle);
			}
		} catch (SQLException e) {
			throw new DaoException(e.toString());
		}
		return Optional.empty();
	}

	public List<Vehicle> findAll() throws DaoException {
		List<Vehicle> listeDeVehicles = new ArrayList<>();
		try (Connection connexion = ConnectionManager.getConnection();
			 PreparedStatement statement = connexion.prepareStatement(FIND_VEHICLES_QUERY);){
			statement.execute();
			ResultSet resultat = statement.getResultSet();
			Vehicle vehicle;
			while(resultat.next()){
				vehicle = new Vehicle(resultat.getInt("id"),
						resultat.getInt("nb_places"),
						resultat.getString("constructeur"),
						resultat.getString("modele"));
				listeDeVehicles.add(vehicle);
			}
		} catch (SQLException e) {
			throw new DaoException();
		}
		return listeDeVehicles;
	}

	public int count() throws DaoException{
		// Solution rapide à mettre en place, mais lourd en ressources et peu efficace : return this.findAll().size();
		// Solution "propre"
		int count = 0;
		try (Connection connexion = ConnectionManager.getConnection();
			 PreparedStatement statement = connexion.prepareStatement(FIND_NB_VEHICLES_QUERY);){
			statement.execute();
			ResultSet resultat = statement.getResultSet();
			while(resultat.next()){
				count = resultat.getInt("vehicle_count");
			}
		} catch (SQLException e) {
			throw new DaoException(e.toString());
		}
		return count;
	}
}
