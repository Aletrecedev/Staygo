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
}