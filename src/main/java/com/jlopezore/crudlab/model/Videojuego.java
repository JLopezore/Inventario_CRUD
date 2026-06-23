package com.jlopezore.crudlab.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "videojuegos")
@Data // Lombok: genera getters, setters, constructores y toString automáticamente
public class Videojuego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;
    private String plataforma;
    private Integer anioLanzamiento;
    private boolean completado;

}