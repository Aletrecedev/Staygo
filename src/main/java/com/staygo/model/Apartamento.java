package com.staygo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "apartamentos")
public class Apartamento {

    // 1. EL ID ÚNICO DEL APARTAMENTO
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_apartamento")
    private Integer idApartamento;

    // 2. LA RELACIÓN CON EL DUEÑO
    @ManyToOne
    @JoinColumn(name = "id_propietario", nullable = false)
    private Usuario propietario;

    // 3. DATOS
    private String nombre;
    private String direccion;

    @Column(name = "precio_noche")
    private Double precioNoche;

    private Integer capacidad;
    private String descripcion;

}