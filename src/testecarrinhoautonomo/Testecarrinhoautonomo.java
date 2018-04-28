/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testecarrinhoautonomo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author Bruno
 */
public class Testecarrinhoautonomo {

    private boolean vertical = false;
    private static final int CAMPO[][] = new int[4][4]; //Tamanho da arena
    static Carrinho verde = new Carrinho("Verde", new int[0][0]);
    static Carrinho vermeio = new Carrinho("Vermeio", new int[4][4]);
    static ArrayList<Carrinho> carrinhos = new ArrayList<>();

    public static void main(String[] args) {
        //Adiciona os participantes
        carrinhos.add(verde);
        carrinhos.add(vermeio);

        geraDestino(carrinhos);
        while (!isTerminou(carrinhos)) {
            for (Carrinho c : carrinhos) {
                if (!c.chegou) {
                    geraProximaPosicao(c);
                }
                System.out.println(c.toString());
            }
        }
    }

    static void geraDestino(ArrayList<Carrinho> xd) {
        int x = 0;
        int y = 0;
        for (Carrinho c : xd) {
            x += c.getOrigem().length;
            if (c.getOrigem().length == 0) {
                y += 0;
            } else {
                y += c.getOrigem().length == 0 ? 0 : c.getOrigem()[0].length;
            }
        }
        //Encontra o centro entre todos os participantes para reduzir o custo
        int[][] destino = new int[(int) x / 2][(int) y / 2];
        for (Carrinho c : xd) {
            c.setDestino(destino);
        }
    }

    static boolean isTerminou(ArrayList<Carrinho> xd) {
        for (Carrinho c : xd) {
            if (!Arrays.equals(c.getDestino(), c.getOrigem())) {
                c.setChegou(true);
                return false;
            }
        }
        return true;
    }

    static void geraProximaPosicao(Carrinho c) {
        ArrayList<int[][]> posicoesValidas = new ArrayList<>();

        if (isPosicaoValida(c.getAnterior().length - 1, c.getAnterior().length == 0 ? 0 : c.getAnterior()[0].length - 1)) {
            posicoesValidas.add(new int[c.getAnterior().length - 1][c.getAnterior().length == 0 ? 0 : c.getAnterior()[0].length - 1]);
        } else if (isPosicaoValida(c.getAnterior().length - 1, c.getAnterior().length == 0 ? 0 : c.getAnterior()[0].length + 1)) {
            posicoesValidas.add(new int[c.getAnterior().length - 1][c.getAnterior().length == 0 ? 0 : c.getAnterior()[0].length + 1]);
        } else if (isPosicaoValida(c.getAnterior().length + 1, c.getAnterior().length == 0 ? 0 : c.getAnterior()[0].length + 1)) {
            posicoesValidas.add(new int[c.getAnterior().length + 1][c.getAnterior().length == 0 ? 0 : c.getAnterior()[0].length + 1]);
        } else if (isPosicaoValida(c.getAnterior().length + 1, c.getAnterior().length == 0 ? 0 : c.getAnterior()[0].length - 1)) {
            posicoesValidas.add(new int[c.getAnterior().length + 1][c.getAnterior().length == 0 ? 0 : c.getAnterior()[0].length - 1]);
        }
        c.setAnterior(new int[c.getProximo().length][c.getProximo().length == 0 ? 0 : c.getProximo()[0].length]);
        int[][] x = posicoesValidas.get(new Random().nextInt(posicoesValidas.size()));
        c.setProximo(new int[x.length][x.length == 0? 0: x[0].length]);
    }

    static boolean isPosicaoValida(int x, int y) {
        try {
            if (new int[x][y] != null) {
                return true;
            }
        } catch (Exception e) {

        }
        return false;
    }
}
