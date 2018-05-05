/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testecarrinhoautonomo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author Bruno
 */
public class Testecarrinhoautonomo {

    private boolean vertical = false;
    private static final Posicao CAMPO = new Posicao(4, 4);//Tamanho da arena
    static Carrinho verde = new Carrinho("Verde", new Posicao(0, 0));
    static Carrinho vermeio = new Carrinho("Vermeio", new Posicao(4, 4));
    static ArrayList<Carrinho> carrinhos = new ArrayList<>();
    static Set<Posicao> obstaculos = new HashSet<Posicao>();
    static Set<Posicao> destinos = new HashSet<Posicao>();
    static int iteracoes = 0;
    static int diferenca = 0;

    public static void main(String[] args) {
        //Adiciona os participantes
        carrinhos.add(verde);
        carrinhos.add(vermeio);

        geraDestino(carrinhos);
        geraObstaculos(obstaculos);

        while (!isTerminou(carrinhos)) {
            for (Carrinho c : carrinhos) {
                if (!c.chegou) {
                    geraProximaPosicao(c);
                }
                System.out.println(c.toString());
                iteracoes++;
            }
        }
        System.out.println("ITERAÇÕES: " + iteracoes);
    }

    static void geraDestino(ArrayList<Carrinho> xd) {
        int x = 0;
        int y = 0;
        for (Carrinho c : xd) {
            x += c.getOrigem().getX();
            if (c.getOrigem().getX() == 0) {
                y += 0;
            } else {
                y += c.getOrigem().getX() == 0 ? 0 : c.getOrigem().getY();
            }
        }
        //Encontra o centro entre todos os participantes para reduzir o custo
        Posicao destino = new Posicao((int) x / xd.size(), (int) y / xd.size());
        for (Carrinho c : xd) {
            c.setDestino(destino);
            destinos.add(destino);
        }
    }

    static boolean isTerminou(ArrayList<Carrinho> xd) {
        boolean x = true;
        for (Carrinho c : xd) {
            if ((c.getDestino().equals(c.getAtual()))) {
                c.setChegou(true);
            } else {
                x = false;
            }
        }
        return x;
    }

    static void geraProximaPosicao(Carrinho c) {
        ArrayList<Posicao> posicoesValidas = new ArrayList<Posicao>();
        c.setAtual(c.getProximo());
        Posicao pos = c.getAtual();
        posicoesValidas.add(pos);
        diferenca = Integer.MAX_VALUE;
        if (isPosicaoValida(pos = new Posicao(c.getAtual().getX() - 1, c.getAtual().getY()))) {
            verificaDiferenca(posicoesValidas, pos, c.getDestino());
        }
        if (isPosicaoValida(pos = new Posicao(c.getAtual().getX(), c.getAtual().getY() + 1))) {
            verificaDiferenca(posicoesValidas, pos, c.getDestino());
        }
        if (isPosicaoValida(pos = new Posicao(c.getAtual().getX() + 1, c.getAtual().getY()))) {
            verificaDiferenca(posicoesValidas, pos, c.getDestino());
        }
        if (isPosicaoValida(pos = new Posicao(c.getAtual().getX(), c.getAtual().getY() - 1))) {
            verificaDiferenca(posicoesValidas, pos, c.getDestino());
        }
        c.setProximo(posicoesValidas.get(new Random().nextInt(posicoesValidas.size())));
    }

    static boolean isPosicaoValida(Posicao p) {
        return (p.getX() >= 0 && p.getX() <= CAMPO.getX()) && (p.getY() >= 0 && p.getY() <= CAMPO.getY());
    }

    static boolean isObstaculoValido(Posicao p) {
        return isPosicaoValida(p) && !destinos.contains(p);
    }

    static void verificaDiferenca(ArrayList<Posicao> posicoes, Posicao novaPos, Posicao destino) {
        //TO DO ignorar o obstaculo
        if (diferenca > Math.abs(destino.getX() - novaPos.getX()) + Math.abs(destino.getY() - novaPos.getY())) {
            diferenca = Math.abs(destino.getX() - novaPos.getX()) + Math.abs(destino.getY() - novaPos.getY());
            posicoes.set(0, novaPos);
        }
    }

    static void geraObstaculos(Set<Posicao> lista) {
        while (lista.size() < 4) {
            Posicao pos = new Posicao(new Random().nextInt(CAMPO.getX()), new Random().nextInt(CAMPO.getY()));
            if (isObstaculoValido(pos)) {
                lista.add(pos);
            }
        }
    }
}
