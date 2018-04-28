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
    int[][] destino;
    int[][] proximo;
    int[][] origem;
    int[][] anterior;
    boolean chegou;

    public Carrinho(String nome, int[][] origem) {
        this.nome = nome;
        this.origem = origem;
        this.anterior = origem;
        this.destino = new int[0][0];
        this.proximo = new int[0][0];
    }
    public Carrinho(Carrinho c){
        this.nome = c.getNome();
        this.origem = c.getOrigem();
        this.anterior = c.getOrigem();
        this.destino = new int[0][0];
        this.proximo = new int[0][0];
    }

    public int[][] getAnterior() {
        return anterior;
    }

    public void setAnterior(int[][] anterior) {
        this.anterior = anterior;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isChegou() {
        return chegou;
    }

    public void setChegou(boolean chegou) {
        this.chegou = chegou;
    }

    public int[][] getDestino() {
        return destino;
    }

    public void setDestino(int[][] destino) {
        this.destino = destino;
    }

    public int[][] getProximo() {
        return proximo;
    }

    public void setProximo(int[][] proximo) {
        this.proximo = proximo;
    }

    public int[][] getOrigem() {
        return origem;
    }

    public void setOrigem(int[][] origem) {
        this.origem = origem;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.nome);
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
        return true;
    }

    private String formataArray(int[][] xd) {
        return "[" + xd.length + "," + (xd.length == 0 ? 0 : xd[0].length) + "]";
    }

    @Override
    public String toString() {
        return "Carrinho{" + "nome=" + nome + ", destino=" + formataArray(destino) + ", anterior=" + formataArray(anterior) + "proximo=" + formataArray(proximo)
                + ", origem=" + formataArray(origem) + '}';
    }

}
