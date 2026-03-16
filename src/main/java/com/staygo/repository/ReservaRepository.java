package com.staygo.repository;

import com.staygo.model.Reserva;
import com.staygo.model.Usuario;
import com.staygo.model.Apartamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    // Para que un Cliente pueda ver "Mis Viajes"
    List<Reserva> findByCliente(Usuario cliente);

    // Para que un Propietario pueda ver quién ha reservado su piso
    List<Reserva> findByApartamento(Apartamento apartamento);
}