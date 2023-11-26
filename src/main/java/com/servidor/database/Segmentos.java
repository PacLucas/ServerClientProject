package com.servidor.database;

public class Segmentos {
    private int id;
    private Integer ponto_origem;
    private Integer ponto_destino;
    private String direcao;
    private String distancia;
    private String obs;

    public Segmentos(int id, Integer ponto_origem, Integer ponto_destino, String direcao, String distancia, String obs) {
        this.id = id;
        this.ponto_origem = ponto_origem;
        this.ponto_destino = ponto_destino;
        this.direcao = direcao;
        this.distancia = distancia;
        this.obs = obs;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getPonto_origem() {
        return ponto_origem;
    }

    public void setPonto_origem(Integer ponto_origem) {
        this.ponto_origem = ponto_origem;
    }

    public Integer getPonto_destino() {
        return ponto_destino;
    }

    public void setPonto_destino(Integer ponto_destino) {
        this.ponto_destino = ponto_destino;
    }

    public String getDirecao() {
        return direcao;
    }

    public void setDirecao(String direcao) {
        this.direcao = direcao;
    }

    public String getDistancia() {
        return distancia;
    }

    public void setDistancia(String distancia) {
        this.distancia = distancia;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }
}
