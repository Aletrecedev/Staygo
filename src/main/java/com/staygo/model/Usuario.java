package com.staygo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    private String nombre;
    private String apellidos;
    private String telefono;
    private String email;
    private String contrasena;

    @Column(name = "nombre_usuario")
    private String nombreUsuario;

    private String rol = "CLIENTE";
}