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

    // NUEVO CAMPO: Estado de la reserva (Vital para la lógica de cancelación)
    @Column(nullable = false)
    private String estado = "CONFIRMADA"; // Valor por defecto al crear una reserva

    // NOTA SENIOR: Si más adelante añades valoraciones (como en tu PDF inicial),
    // irían aquí:
    // private String valoracionHuesped;
    // private String valoracionPropietario;

    // LÓGICA DE NEGOCIO: ¿Se puede cancelar esta reserva?
    public boolean isCancelable() {
        // 1. Si ya está cancelada, obvio no se puede volver a cancelar
        if ("CANCELADA".equals(this.estado)) {
            return false;
        }

        // 2. Si faltan 2 días o menos, tampoco se puede
        long diasHastaCheckin = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), this.fechaInicio);
        return diasHastaCheckin > 2;
    }
}