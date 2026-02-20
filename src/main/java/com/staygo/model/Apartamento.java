package com.staygo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Esto genera automáticamente todos los getters, setters, toString, etc.
@NoArgsConstructor // Genera un constructor vacío, obligatorio para Spring JPA
@Entity
@Table(name = "apartamentos")
public class Apartamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_apartamento")
    private Integer idApartamento;

    private String nombre;
    private String direccion;

    @Column(name = "precio_noche")
    private Double precioNoche;

    private Integer capacidad;
    private String descripcion;

    @Column(name = "id_propietario")
    private Integer idPropietario;
}