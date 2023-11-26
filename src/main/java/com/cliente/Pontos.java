package com.cliente;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Pontos {
    @JsonProperty("id")
    private int id;
    @JsonProperty("name")
    private String nome;
    @JsonProperty("obs")
    private String obs;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }
}
