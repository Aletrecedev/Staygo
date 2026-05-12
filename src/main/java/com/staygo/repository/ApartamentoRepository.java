package com.staygo.repository;

import com.staygo.model.Apartamento;
import com.staygo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ApartamentoRepository extends JpaRepository<Apartamento, Integer> {

    // "Busca una lista de apartamentos
    // donde la 'direccion' CONTENGA la palabra que te paso,
    // y además Ignora Mayúsculas y Minúsculas (IgnoreCase)".
    List<Apartamento> findByDireccionContainingIgnoreCase(String ubicacion);

    // Devuelve los apartamentos que pertenecen a un propietario específico
    List<Apartamento> findByPropietario(Usuario propietario);

    // CONSULTA DE BÚSQUEDA (Disponibilidad, Capacidad y Ubicación)
    @Query("SELECT a FROM Apartamento a WHERE a.activo = true " + // <--- Filtro de Soft Delete
            "AND (:ubicacion IS NULL OR a.direccion LIKE %:ubicacion%) " +
            "AND (:huespedes IS NULL OR a.capacidad >= :huespedes) " +
            "AND NOT EXISTS (SELECT r FROM Reserva r WHERE r.apartamento = a " +
            "AND r.estado = 'CONFIRMADA' " +
            "AND (r.fechaInicio < :fechaFin AND r.fechaFin > :fechaInicio))")
    List<Apartamento> buscarDisponibles(
            @Param("ubicacion") String ubicacion,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            @Param("huespedes") Integer huespedes
    );

    // Para la página principal de "Explorar": Solo mostramos lo que NO está borrado
    List<Apartamento> findByActivoTrue();

    // Para el panel del propietario: Mostramos sus pisos activos
    List<Apartamento> findByPropietarioAndActivoTrue(Usuario propietario);
}