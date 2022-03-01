package br.ifsp.bcc.compiladores.expr.parser;

import br.ifsp.bcc.compiladores.expr.parser.ExpressoesParser.DeclContext;
import br.ifsp.bcc.compiladores.expr.parser.ExpressoesParser.TermoContext;
import java.util.List;

public class Calculador extends ExpressoesBaseVisitor<Double> {

    Escopos escoposAninhados = new Escopos();

    @Override
    public Double visitPrograma(ExpressoesParser.ProgramaContext ctx) {
        return visitExp(ctx.exp()); //apenas fazer a visita ao filho.
    }

    @Override
    public Double visitExp(ExpressoesParser.ExpContext ctx) {
        double valor = visitTermo(ctx.termo1); //pega o valor do primeiro termo e varre em busca dos possíveis outros termos
        for (TermoContext ot : ctx.outrosTermos) {
            valor += visitTermo(ot);
        }
        return valor;
    }
    //Termo tem várias opções na gramática, então deve verificar.
    @Override
    public Double visitTermo(ExpressoesParser.TermoContext ctx) {
        if (ctx.expParentesis != null) {
            return visitExp(ctx.expParentesis);
        } else if (ctx.constante != null) {
            return Double.parseDouble(ctx.constante.getText());
        } else if (ctx.variavel != null) {
            //no caso de variável, percorrer os escopos para ver se encontra a declaração dela.
            List<TabelaDeSimbolos> escopos = escoposAninhados.percorrerEscoposAninhados();
            for (TabelaDeSimbolos ts : escopos) {
                EntradaTabelaDeSimbolos etds = ts.verificar(ctx.variavel.getText());
                if (etds != null) {//se existir, retorno o valor daquela entrada
                    return etds.valor;
                }
            }
            throw new RuntimeException("Erro semântico: "
                    + ctx.variavel.getText() + " não foi declarada antes do uso");
        } else { //se tem subepressão é necessário criar um novo escopo.
            escoposAninhados.criarNovoEscopo();
            visitListaDecl(ctx.listaDecl());
            double retorno = visitExp(ctx.subexp);
            escoposAninhados.abandonarEscopo();
            return retorno;
        }
    }

    @Override
    public Double visitDecl(ExpressoesParser.DeclContext ctx) {
        TabelaDeSimbolos escopoAtual = escoposAninhados.obterEscopoAtual();
        if (escopoAtual.verificar(ctx.nome.getText()) != null) {
            throw new RuntimeException("Erro semântico: " + ctx.nome.getText()
                    + " declarada duas vezes num mesmo escopo");
        } else {
            escopoAtual.inserir(ctx.nome.getText(), visitExp(ctx.exp()));
        }

        return null; // declaração não tem valor
    }

    @Override
    public Double visitListaDecl(ExpressoesParser.ListaDeclContext ctx) {
        for (DeclContext d : ctx.decl()) {
            visitDecl(d);
        }
        return null; // declaração não tem valor
    }
}
