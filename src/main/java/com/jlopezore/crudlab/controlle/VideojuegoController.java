package com.jlopezore.crudlab.controlle;

import com.jlopezore.crudlab.model.Videojuego;
import com.jlopezore.crudlab.service.VideojuegoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
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

    @PutMapping("/{id}")
    public Videojuego actualizar(@PathVariable Long id, @RequestBody Videojuego videojuego) {
        return service.actualizarVideojuego(id, videojuego);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminarVideojuego(id);
        return ResponseEntity.noContent().build();
    }
}