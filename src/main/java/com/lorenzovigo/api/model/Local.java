package com.lorenzovigo.api.model;

import javax.persistence.*;

@Entity
public class Local {

    private @Id @GeneratedValue Long id;

    @Column(nullable=false, unique=true)
    private String direccion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

}
