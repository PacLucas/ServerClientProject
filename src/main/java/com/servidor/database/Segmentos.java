package com.servidor.database;

public class Segmentos {
    private int id;
    private Pontos ponto_origem;
    private Pontos ponto_destino;
    private String direcao;
    private String distancia;
    private String obs;

    public Segmentos(int id, Pontos ponto_origem, Pontos ponto_destino, String direcao, String distancia, String obs) {
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

    public Pontos getPonto_origem() {
        return ponto_origem;
    }

    public void setPonto_origem(Pontos ponto_origem) {
        this.ponto_origem = ponto_origem;
    }

    public Pontos getPonto_destino() {
        return ponto_destino;
    }

    public void setPonto_destino(Pontos ponto_destino) {
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
