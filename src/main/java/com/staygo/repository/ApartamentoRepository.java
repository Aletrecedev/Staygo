package com.staygo.repository;

import com.staygo.model.Apartamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApartamentoRepository extends JpaRepository<Apartamento, Integer> {

    // "Busca una lista de apartamentos
    // donde la 'direccion' CONTENGA la palabra que te paso,
    // y además Ignora Mayúsculas y Minúsculas (IgnoreCase)".
    List<Apartamento> findByDireccionContainingIgnoreCase(String ubicacion);
}