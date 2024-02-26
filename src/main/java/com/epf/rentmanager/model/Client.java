package com.epf.rentmanager.model;
import java.time.LocalDate;
public record Client(int id, String nom, String prenom, String email, LocalDate naissance){
}