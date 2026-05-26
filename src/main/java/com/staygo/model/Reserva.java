package com.staygo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data // Lombok: Nos crea todos los Getters, Setters, toString, etc. automáticamente
@NoArgsConstructor // Lombok: Nos crea el constructor vacío obligatorio para JPA
@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idReserva;

    // Relación: Una reserva pertenece a UN apartamento
    @ManyToOne
    @JoinColumn(name = "id_apartamento", nullable = false)
    private Apartamento apartamento;

    // Relación: Una reserva pertenece a UN cliente (Usuario)
    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Usuario cliente;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    @Column(name = "importe_total")
    private Double precioTotal;

    // Estado base en la base de datos
    @Column(nullable = false)
    private String estado = "CONFIRMADA";

    // Nota: Si más adelante se añaden valoraciones, irían aquí.


    // Lógica de Negocio Y Métodos Inteligentes

    /**
     * Calcula el estado real de la reserva basándose en la fecha de hoy.
     * Si la fecha de fin ya pasó, la reserva se considera COMPLETADA automáticamente.
     */
    public String getEstadoReal() {
        // 1. Si ya se canceló de forma manual, se queda cancelada
        if ("CANCELADA".equals(this.estado)) {
            return "CANCELADA";
        }

        // 2. Si la fecha de salida ya es anterior al día de hoy -> Completada
        if (this.fechaFin != null && this.fechaFin.isBefore(LocalDate.now())) {
            return "COMPLETADA";
        }

        // 3. Si aún no ha pasado la fecha de salida, sigue confirmada
        return "CONFIRMADA";
    }

    /**
     * Comprueba si la reserva aún se puede cancelar (Faltan más de 2 días)
     */
    public boolean isCancelable() {
        if ("CANCELADA".equals(this.estado)) {
            return false;
        }

        long diasHastaCheckin = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), this.fechaInicio);
        return diasHastaCheckin > 2;
    }
}