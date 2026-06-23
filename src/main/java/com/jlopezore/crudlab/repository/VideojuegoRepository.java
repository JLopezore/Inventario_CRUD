package com.jlopezore.crudlab.repository;

import com.jlopezore.crudlab.model.Videojuego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideojuegoRepository extends JpaRepository<Videojuego, Long> {
    // Solo con extender JpaRepository, Spring Boot nos regala automáticamente
    // todos los métodos CRUD (save, findById, findAll, deleteById, etc.)
    // ¡No hay que escribir SQL manual!
}