package com.staygo.repository;

import com.staygo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Consulta
    Usuario findByEmail(String email);

    // El escudo del Registro:
    boolean existsByEmail(String email);
}