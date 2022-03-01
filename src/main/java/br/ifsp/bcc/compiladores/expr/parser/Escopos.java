/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ifsp.bcc.compiladores.expr.parser;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author zscrollock
 */
public class Escopos {
        private LinkedList<TabelaDeSimbolos> pilhaDeTabelas;

    public Escopos() {
        pilhaDeTabelas = new LinkedList<>();
        criarNovoEscopo(); //escopo global. Sempre está presente.
    }

    public void criarNovoEscopo() {
        pilhaDeTabelas.push(new TabelaDeSimbolos());
    }

    public TabelaDeSimbolos obterEscopoAtual() {
        return pilhaDeTabelas.peek(); //olha o topo da pilha sem remover.
    }

    public List<TabelaDeSimbolos> percorrerEscoposAninhados() {
        return pilhaDeTabelas;
    }

    public void abandonarEscopo() {
        pilhaDeTabelas.pop(); //retira o topo da pilha. Por isso não tem remover na tabela de símbolos, pois sempre remove a tabela inteira.
    }
}
