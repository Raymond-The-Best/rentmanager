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
import java.util.Optional;

import com.epf.rentmanager.exception.DaoException;
import com.epf.rentmanager.model.Client;
import com.epf.rentmanager.persistence.ConnectionManager;
import org.springframework.stereotype.Repository;

@Repository
public class ClientDao {
	private ClientDao() {}
	private static final String CREATE_CLIENT_QUERY = "INSERT INTO Client(nom, prenom, email, naissance) VALUES(?, ?, ?, ?);";
	private static final String DELETE_CLIENT_QUERY = "DELETE FROM Client WHERE id=?;";
	private static final String FIND_CLIENT_QUERY = "SELECT nom, prenom, email, naissance FROM Client WHERE id=?;";
	private static final String FIND_CLIENTS_QUERY = "SELECT id, nom, prenom, email, naissance FROM Client;";
	private static final String FIND_NB_CLIENTS_QUERY = "SELECT COUNT(*) AS client_count FROM Client;";
	private static final String FIND_ALL_EMAILS = "SELECT email FROM Client;";
	
	public int create(Client client) throws DaoException {
		// Créer un client dans la BDD à partir d'un objet Client
		// Renvoie l'ID de l'instance créée
        try (Connection connexion = ConnectionManager.getConnection();
			 PreparedStatement statement = connexion.prepareStatement(CREATE_CLIENT_QUERY, PreparedStatement.RETURN_GENERATED_KEYS);){
			statement.setString(1, client.nom());
			statement.setString(2, client.prenom());
			statement.setString(3, client.email());
			statement.setDate(4, Date.valueOf(client.naissance()));
			statement.executeUpdate();
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
	
	public int delete(Client client) throws DaoException {
        try (Connection connexion = ConnectionManager.getConnection();
			 PreparedStatement statement = connexion.prepareStatement(DELETE_CLIENT_QUERY);){
			statement.setInt(1, client.id());
			statement.executeUpdate();
			return 1;

        } catch (SQLException e) {
            throw new DaoException(e.toString());
        }
	}

	public Optional<Client> findById(int id) throws DaoException {
		try (Connection connexion = ConnectionManager.getConnection();
			 PreparedStatement statement = connexion.prepareStatement(FIND_CLIENT_QUERY);){
			statement.setInt(1, id);
			statement.execute();
			ResultSet resultat = statement.getResultSet();
			while(resultat.next()){
				Client client = new Client(id,
						resultat.getString("nom"),
						resultat.getString("prenom"),
						resultat.getString("email"),
						resultat.getDate("naissance").toLocalDate());
				return Optional.of(client);
			}

		} catch (SQLException e) {
			throw new DaoException(e.toString());
		}
		return Optional.empty();
	}

	public List<Client> findAll() throws DaoException {
		List<Client> listeDeClients = new ArrayList<>();
        try (Connection connexion = ConnectionManager.getConnection();
			 PreparedStatement statement = connexion.prepareStatement(FIND_CLIENTS_QUERY);){
			statement.execute();
			ResultSet resultat = statement.getResultSet();
					Client client;
			while(resultat.next()){
				client = new Client(resultat.getInt("id"),
						resultat.getString("nom"),
						resultat.getString("prenom"),
						resultat.getString("email"),
						resultat.getDate("naissance").toLocalDate());
				listeDeClients.add(client);
			}
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

		return listeDeClients;
	}

	public int count() throws DaoException{
		int count = 0;
		try (Connection connexion = ConnectionManager.getConnection();
			 PreparedStatement statement = connexion.prepareStatement(FIND_NB_CLIENTS_QUERY);){
			statement.execute();
			ResultSet resultat = statement.getResultSet();
			while(resultat.next()){
				count = resultat.getInt("client_count");
			}
		} catch (SQLException e) {
			throw new DaoException(e.toString());
		}
		return count;
	}

	public List<String> getAllEmail() throws DaoException{
		List<String> emailAddresses = new ArrayList<>();
		try (Connection connexion = ConnectionManager.getConnection();
			 PreparedStatement statement = connexion.prepareStatement(FIND_ALL_EMAILS);){
			statement.execute();
			ResultSet resultat = statement.getResultSet();
			while(resultat.next()){
				emailAddresses.add(resultat.getString("email"));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return emailAddresses;
	}
}
