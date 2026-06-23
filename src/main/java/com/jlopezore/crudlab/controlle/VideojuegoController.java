package com.jlopezore.crudlab.controlle;

import com.jlopezore.crudlab.model.Videojuego;
import com.jlopezore.crudlab.service.VideojuegoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/videojuego")
public class VideojuegoController {

    private final VideojuegoService service;

    public VideojuegoController(VideojuegoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Videojuego> listar() {
        return service.obtenerTodas();
    }

    @PostMapping
    public Videojuego crear(@RequestBody Videojuego videojuego) {
        return service.crearVideojuego(videojuego);
    }
}