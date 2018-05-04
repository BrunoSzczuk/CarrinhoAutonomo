/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testecarrinhoautonomo;

import java.util.ArrayList;
import java.util.Random;

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
            x += c.getOrigem().getX();
            if (c.getOrigem().getX() == 0) {
                y += 0;
            } else {
                y += c.getOrigem().getX() == 0 ? 0 : c.getOrigem().getY();
            }
        }
        //Encontra o centro entre todos os participantes para reduzir o custo
        Posicao destino = new Posicao((int) x / 2, (int) y / 2);
        for (Carrinho c : xd) {
            c.setDestino(destino);
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

        if (isPosicaoValida(new Posicao(c.getAtual().getX() - 1, c.getAtual().getY()))) {
            posicoesValidas.add(new Posicao(c.getAtual().getX() - 1, c.getAtual().getY()));
        }  if (isPosicaoValida(new Posicao(c.getAtual().getX(), c.getAtual().getY() + 1))) {
            posicoesValidas.add(new Posicao(c.getAtual().getX(), c.getAtual().getY() + 1));
        }  if (isPosicaoValida(new Posicao(c.getAtual().getX() + 1, c.getAtual().getY()))) {
            posicoesValidas.add(new Posicao(c.getAtual().getX() + 1, c.getAtual().getY()));
        }  if (isPosicaoValida(new Posicao(c.getAtual().getX(), c.getAtual().getY() - 1))) {
            posicoesValidas.add(new Posicao(c.getAtual().getX(), c.getAtual().getY() - 1));
        }
        c.setProximo(posicoesValidas.get(new Random().nextInt(posicoesValidas.size())));
        //System.out.println("Posição gerada: " + c);
    }

    static boolean isPosicaoValida(Posicao p) {
        return (p.getX() >= 0 && p.getX() <= CAMPO.getX()) && (p.getY() >= 0 && p.getY() <= CAMPO.getY());
    }
}
