package com.staygo.repository;

import com.staygo.model.Apartamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApartamentoRepository extends JpaRepository<Apartamento, Integer> {
}