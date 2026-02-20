package com.staygo.repository;

import com.staygo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Consulta
    Usuario findByEmailAndContrasena(String email, String contrasena);
}