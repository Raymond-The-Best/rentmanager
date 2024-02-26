package com.epf.rentmanager.model;

import java.time.LocalDate;

public record Reservation(int id, int vehicle_id, int client_id, LocalDate debut, LocalDate fin){}
