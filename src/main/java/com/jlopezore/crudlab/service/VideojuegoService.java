package com.jlopezore.crudlab.service;

import com.jlopezore.crudlab.model.Videojuego;
import com.jlopezore.crudlab.repository.VideojuegoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class VideojuegoService {

    private final VideojuegoRepository repository;

    public VideojuegoService(VideojuegoRepository repository) {
        this.repository = repository;
    }

    public List<Videojuego> obtenerTodas() {
        return repository.findAll();
    }

    public Videojuego crearVideojuego(Videojuego videojuego) {
        return repository.save(videojuego);
    }

    public Videojuego actualizarVideojuego(Long id, Videojuego datos) {
        Videojuego videojuego = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        videojuego.setTitulo(datos.getTitulo());
        videojuego.setPlataforma(datos.getPlataforma());
        videojuego.setAnioLanzamiento(datos.getAnioLanzamiento());
        videojuego.setCompletado(datos.isCompletado());
        return repository.save(videojuego);
    }

    public void eliminarVideojuego(Long id) {
        repository.deleteById(id);
    }
}
