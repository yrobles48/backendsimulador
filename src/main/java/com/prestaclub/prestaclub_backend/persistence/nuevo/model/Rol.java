package com.prestaclub.prestaclub_backend.persistence.nuevo.model;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "roles")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;

    @OneToMany(mappedBy = "rol", fetch = FetchType.EAGER)
    private Set<Usuario> usuarios = new HashSet<>();

    public Rol() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // @Override
    // public String toString() {
    //     return "Rol{" + "id=" + id + ", nombre=" + nombre + '}';
    // }

    

    
}
