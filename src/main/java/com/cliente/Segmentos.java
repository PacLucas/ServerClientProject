package com.cliente;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class Segmentos {
    @JsonProperty("id")
    private int id;
    @JsonProperty("ponto_origem")
    private JsonNode ponto_origem;
    @JsonProperty("ponto_destino")
    private JsonNode ponto_destino;
    @JsonProperty("direcao")
    private String direcao;
    @JsonProperty("distancia")
    private String distancia;
    @JsonProperty("obs")
    private String obs;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public JsonNode getPonto_origem() {
        return ponto_origem;
    }

    public void setPonto_origem(JsonNode ponto_origem) {
        this.ponto_origem = ponto_origem;
    }

    public JsonNode getPonto_destino() {
        return ponto_destino;
    }

    public void setPonto_destino(JsonNode ponto_destino) {
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
}
