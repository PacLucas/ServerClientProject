package com.servidor.database;

public class Pontos {
    private int id;
    private String nome;
    private String obs;
    public Pontos(int id, String nome, String obs) {
        this.id = id;
        this.nome = nome;
        this.obs = obs;
    }

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