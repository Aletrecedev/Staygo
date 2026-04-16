package com.staygo.repository;

import com.staygo.model.Reserva;
import com.staygo.model.Usuario;
import com.staygo.model.Apartamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;


import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    // Para que un Cliente pueda ver "Mis Viajes"
    List<Reserva> findByCliente(Usuario cliente);

    // Para que un Propietario pueda ver quién ha reservado su piso
    List<Reserva> findByApartamento(Apartamento apartamento);

    @Query("SELECT r FROM Reserva r WHERE r.apartamento = :piso " +
            "AND (r.fechaInicio <= :fechaFin AND r.fechaFin >= :fechaInicio)")
    List<Reserva> buscarOverbooking(@Param("piso") Apartamento piso,
                                    @Param("fechaInicio") LocalDate fechaInicio,
                                    @Param("fechaFin") LocalDate fechaFin);
}

