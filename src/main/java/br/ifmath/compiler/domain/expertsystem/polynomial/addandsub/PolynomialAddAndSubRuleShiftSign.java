package br.ifmath.compiler.domain.expertsystem.polynomial.addandsub;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.MathOperatorUtil;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.*;

public class PolynomialAddAndSubRuleShiftSign implements IRule {
    private ThreeAddressCode source;

    public PolynomialAddAndSubRuleShiftSign() {
    }

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        //Simboliza que sempre irá entrar nesta regra, visto que na verdade essa é a junção da regra de troca de sinais
        // e de remoção dos parentes, e esse último sempre deverá ser executado
        return true;
    }


    @Override
    public List<Step> handle(List<ThreeAddressCode> sources) {
        String reason = "Removendo os parênteses dos polinômios";
        // A variável source é utilizada para previnir a utilização de parâmetros desnecessários em diversos métodos
        this.source = sources.get(0);

        ExpandedQuadruple root = this.source.findQuadrupleByResult(this.source.getLeft());
        //sideQuadruple é a variável utilizada para identificar qual dos lados da equação (argument1 ou 2 do root)
        // está sendo processada
        ExpandedQuadruple sideQuadruple = this.getQuadrupleWithMinus(this.source.findQuadrupleByResult(root.getArgument1()));
        for (int i = 0; i < 2; i++) {
            //Verifica se é necessario fazer alguma troca de sinais
            if (sideQuadruple != null) {
                this.changeSign(sideQuadruple, true);
                reason = "Aplicando a regra de troca de sinais em operações prioritárias, em duplas negações ou " +
                        "em somas de números negativos. E, removendo os parênteses dos polinômios.";
            }
            sideQuadruple = this.getQuadrupleWithMinus(root);
        }

        this.handleParentheses();

        this.source.clearNonUsedQuadruples();

        List<Step> steps = new ArrayList<>();
        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), "", "", this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), reason));

        return steps;
    }

    /**
     * Verifica todas as quadruplas e faz as trocas de sinais necessários.
     *
     * @param iterationQuadruple Inicialmente, a primeira quadrupla a ser analisada. E por se tratar
     *                           de um metodo recursivo, esse parametro sera a quadrupla atual a ser
     *                           analisada
     * @param itHasToChange      Controla se é necessario fazer a troca de sinais na {@code iterationQuadruple}
     */
    private void changeSign(ExpandedQuadruple iterationQuadruple, boolean itHasToChange) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            this.changeSign(this.source.findQuadrupleByResult(iterationQuadruple.getArgument1()), itHasToChange);
        }

        /*A logica dessas verificacoes, eh que toda vez que ha um operador de "-", ira mudar o itHasToChange
          para true, simbolizando que eh necessario trocar todos os sinais que encontrar. Mas se o valor de
          itHasToChange ja for true, e encontrar outro operador "-", simboliza uma dupla negacao, o que
          mudara o valor de itHasToChange para false, simbolizando que nenhum dos proximos sinais sera alterado.
         */
        if (iterationQuadruple.isPlus() && itHasToChange) {
            iterationQuadruple.setOperator("-");
        } else if (iterationQuadruple.isMinus() && itHasToChange) {
            if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                int nextQuadrupleLevel = this.source.findQuadrupleByResult(iterationQuadruple.getArgument2()).getLevel();
                if (nextQuadrupleLevel > iterationQuadruple.getLevel())
                    itHasToChange = false;
            }
            iterationQuadruple.setOperator("+");
        } else if (iterationQuadruple.isMinus() && !itHasToChange)
            itHasToChange = true;

        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            this.changeSign(this.source.findQuadrupleByResult(iterationQuadruple.getArgument2()), itHasToChange);
        }


    }

    /**
     * Faz as verificacoes em relacao aos parenteses, como um operador negativo a frente de um parenteses, e depois
     * disso, eh retirado todos os parenteses.
     */
    private void handleParentheses() {
        for (ExpandedQuadruple expandedQuadruple : this.source.getExpandedQuadruples()) {

            // o nivel -1 simboliza que o metodo ja fez alguma alteracao na quadrupla em questao
            if (expandedQuadruple.getLevel() != -1) {
                if (expandedQuadruple.isNegative())
                    this.handleMinusParentheses(expandedQuadruple);

                if (expandedQuadruple.isMinus() && StringUtil.match(expandedQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                    this.handleSubParentheses(expandedQuadruple);
                }
            }
        }

        //retira os parenteses
        this.source.removeQuadruplesParentheses();
    }

    /**
     * Faz as alteracoes de sinais e de argumentos em casos de uma quadrupla com MINUS.
     *
     * @param expandedQuadruple {@link ExpandedQuadruple} que contém o operador MINUS
     */
    private void handleMinusParentheses(ExpandedQuadruple expandedQuadruple) {
        ExpandedQuadruple grandfather = this.source.findDirectFather(expandedQuadruple.getResult());
        if (grandfather != null) {
            grandfather.setOperator(MathOperatorUtil.signalRule(grandfather.getOperator(), "-"));
            this.source.replaceFatherArgumentForSons(expandedQuadruple, 1);

            //simboliza que estas quadruplas ja foram alteradas
            grandfather.setLevel(-1);
        }
        expandedQuadruple.setLevel(-1);
    }


    /**
     * Faz as alteracoes de sinais e de argumentos em casos de operador "-" precedido da {@code expandedQuadruple},
     * que esta contido em um parenteses.
     *
     * @param expandedQuadruple {@link ExpandedQuadruple} que é precedido de operador "-"
     */
    private void handleSubParentheses(ExpandedQuadruple expandedQuadruple) {
        ExpandedQuadruple son = this.source.findQuadrupleByResult(expandedQuadruple.getArgument2());
        son = findDirectSon(son);
        if (son.isNegative()) {
            expandedQuadruple.setOperator(MathOperatorUtil.signalRule(expandedQuadruple.getOperator(), "-"));
            this.source.replaceFatherArgumentForSons(son, 1);

            //simboliza que estas quadruplas ja foram alteradas
            son.setLevel(-1);
            expandedQuadruple.setLevel(-1);
        }

    }

    /**
     * Encontra o filho diretamente depois do operador "-" da {@code iterationQuadruple}
     *
     * @param iterationQuadruple {@link ExpandedQuadruple} de onde sera encontrado o filho
     * @return {@link ExpandedQuadruple} posterior ao operador "-" da {@code iterationQuadruple}
     */
    private ExpandedQuadruple findDirectSon(ExpandedQuadruple iterationQuadruple) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple son = this.source.findQuadrupleByResult(iterationQuadruple.getArgument1());
            return this.findDirectSon(son);
        }
        return iterationQuadruple;
    }

    /**
     * Encontra uma quadrupla com operador "-", dentro das presentes em uma determinada arvore de quadruplas. E retorna
     * {@code null} caso nao encontrar.
     *
     * @param iterationQuadruple {@link ExpandedQuadruple} que sera a raiz da arvore a ser pesquisada
     * @return {@link ExpandedQuadruple} que contem um operador "-"
     */
    private ExpandedQuadruple getQuadrupleWithMinus(ExpandedQuadruple iterationQuadruple) {

        if (iterationQuadruple.isMinus() && StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple son = this.source.findQuadrupleByResult(iterationQuadruple.getArgument2());
            if (son.getLevel() > iterationQuadruple.getLevel())
                return this.source.findQuadrupleByResult(iterationQuadruple.getArgument2());
        }

        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return getQuadrupleWithMinus(this.source.findQuadrupleByResult(iterationQuadruple.getArgument1()));
        }

        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return getQuadrupleWithMinus(this.source.findQuadrupleByResult(iterationQuadruple.getArgument2()));
        }

        return null;
    }

}

