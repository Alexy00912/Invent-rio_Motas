package com.starterkit.springboot.mota;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "motas")
public class Mota {

    @Id
    @GeneratedValue(generator = "mota-id-gen")
    @GenericGenerator(name = "mota-id-gen", strategy = "increment")
    private Long id;

    @Column(nullable = false, length = 120)
    private String marca;

    @Column(length = 1000)
    private String modelo;

    @Column(length = 1000)
    private String cilindrada;

    @Column(length = 1000)
    private String ano;

    @Column(length = 1000)
    private String kilometragem;

    @Column(length = 1000)
    private String cor;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Boolean garantia;

    @Column(nullable = false)
    private Boolean seguro;

    @Column(length = 100)
    private String cost;

    @Column(nullable = false)
    private Boolean done = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "codigo_unico", unique = true, length = 36, updatable = false)
    private String codigoUnico;

    @Column(length = 500)
    private String imagemPath;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;

        if (codigoUnico == null || codigoUnico.trim().isEmpty()) {
            codigoUnico = UUID.randomUUID().toString();
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // GETTERS E SETTERS

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getCilindrada() {
        return cilindrada;
    }

    public void setCilindrada(String cilindrada) {
        this.cilindrada = cilindrada;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getKilometragem() {
        return kilometragem;
    }

    public void setKilometragem(String kilometragem) {
        this.kilometragem = kilometragem;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getGarantia() {
        return garantia;
    }

    public void setGarantia(Boolean garantia) {
        this.garantia = garantia;
    }

    public Boolean getSeguro() {
        return seguro;
    }    

    public void setSeguro(Boolean seguro) {
        this.seguro = seguro;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCodigoUnico() {
        return codigoUnico;
    }

    public void setCodigoUnico(String codigoUnico) {
        this.codigoUnico = codigoUnico;
    }

    public String getImagemPath() {
        return imagemPath;
    }

    public void setImagemPath(String imagemPath) {
        this.imagemPath = imagemPath;
    }
}