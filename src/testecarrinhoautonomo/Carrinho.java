/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testecarrinhoautonomo;

import java.util.Objects;

/**
 *
 * @author Bruno
 */
public class Carrinho {

    String nome;
    Posicao destino;
    Posicao proximo;
    Posicao origem;
    Posicao atual;
    boolean chegou;

    public Carrinho() {
        destino = new Posicao();
        proximo = new Posicao();
        origem = new Posicao();
        atual = new Posicao();
        nome = "";
    }

    public Carrinho(String nome, Posicao origem) {
        this.nome = nome;
        this.origem = origem;
        destino = new Posicao();
        proximo = origem;
        atual = origem;
    }

    
    @Override
    public String toString() {
        return "Carrinho{" + "nome=" + nome + ", destino=" + destino + ", proximo=" + proximo + ", origem=" + origem + ", atual=" + atual + ", chegou=" + chegou + '}';
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Posicao getDestino() {
        return destino;
    }

    public void setDestino(Posicao destino) {
        this.destino = destino;
    }

    public Posicao getProximo() {
        return proximo;
    }

    public void setProximo(Posicao proximo) {
        this.proximo = proximo;
    }

    public Posicao getOrigem() {
        return origem;
    }

    public void setOrigem(Posicao origem) {
        this.origem = origem;
    }

    public Posicao getAtual() {
        return atual;
    }

    public void setAtual(Posicao atual) {
        this.atual = atual;
    }

    public boolean isChegou() {
        return chegou;
    }

    public void setChegou(boolean chegou) {
        this.chegou = chegou;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.destino);
        hash = 23 * hash + Objects.hashCode(this.proximo);
        hash = 23 * hash + Objects.hashCode(this.origem);
        hash = 23 * hash + Objects.hashCode(this.atual);
        hash = 23 * hash + (this.chegou ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Carrinho other = (Carrinho) obj;
        if (!Objects.equals(this.nome, other.nome)) {
            return false;
        }
        if (!Objects.equals(this.destino, other.destino)) {
            return false;
        }
        if (!Objects.equals(this.proximo, other.proximo)) {
            return false;
        }
        if (!Objects.equals(this.origem, other.origem)) {
            return false;
        }
        if (!Objects.equals(this.atual, other.atual)) {
            return false;
        }
        if (this.chegou != other.chegou) {
            return false;
        }
        return true;
    }

}
