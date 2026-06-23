package com.jlopezore.crudlab.service;

import com.jlopezore.crudlab.model.Videojuego;
import com.jlopezore.crudlab.repository.VideojuegoRepository;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class VideojuegoService {

    private final VideojuegoRepository repository;

    // Inyección de dependencias por constructor (Buena práctica en Spring moderno)
    public VideojuegoService(VideojuegoRepository repository) {
        this.repository = repository;
    }

    public List<Videojuego> obtenerTodas() {
        return repository.findAll();
    }

    public Videojuego crearVideojuego(Videojuego videojuego) {
        return repository.save(videojuego);
    }
}
