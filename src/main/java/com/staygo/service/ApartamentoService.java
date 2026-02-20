package com.staygo.service;

import com.staygo.model.Apartamento;
import com.staygo.repository.ApartamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ApartamentoService {

    @Autowired
    private ApartamentoRepository apartamentoRepository;

    public List<Apartamento> obtenerTodos() {
        return apartamentoRepository.findAll();
    }

    public void guardar(Apartamento apartamento) {
        apartamentoRepository.save(apartamento);
    }

    public void borrar(Integer id) {
        apartamentoRepository.deleteById(id);
    }

    public Apartamento obtenerPorId(Integer id) {
        // Buscar apartamento por id. Si no lo encuentra, devuelve null
        return apartamentoRepository.findById(id).orElse(null);
    }
}