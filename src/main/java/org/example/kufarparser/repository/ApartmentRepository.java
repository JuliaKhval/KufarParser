package org.example.kufarparser.repository;

import org.example.kufarparser.model.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApartmentRepository extends JpaRepository<Apartment, String> {
    List<Apartment> findAll();
}