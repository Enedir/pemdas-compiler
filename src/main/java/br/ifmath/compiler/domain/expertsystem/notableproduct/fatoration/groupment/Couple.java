package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.groupment;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.commonfactor.FatorationRuleCommonFactor;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.MathOperatorUtil;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe que representa o "casal" de {@link ThreeAddressCode} de cada lado de um agrupamento. Por exemplo, na
 * expressão x * (x^2 - 2x + 3) - 1 * (x^2 - 2x + 3), o primeiro conjuge seria x * (x^2 - 2x + 3), e o segundo
 * -1 * (x^2 - 2x + 3).
 */
public class Couple {
    private String firstSpouseFactor, secondSpouseFactor, firstMultiplierNotation, secondMultiplierNotation;
    private final String secondSpouseOperator;
    private final List<ExpandedQuadruple> firstSpouseMultiplier, secondSpouseMultiplier;

    public Couple(ThreeAddressCode firstSpouse, ThreeAddressCode secondSpouse, String secondSpouseOperator) throws InvalidAlgebraicExpressionException {
        firstSpouseMultiplier = new ArrayList<>();
        secondSpouseMultiplier = new ArrayList<>();
        this.secondSpouseOperator = secondSpouseOperator;
        setSpouse(firstSpouse, true);
        if (secondSpouseOperator.equals("-"))
            setMinusToFirstQuadruple(secondSpouse);
        setSpouse(secondSpouse, false);
    }

    public String getFirstSpouseFactor() {
        return firstSpouseFactor;
    }

    public String getSecondSpouseFactor() {
        return secondSpouseFactor;
    }

    public List<ExpandedQuadruple> getFirstSpouseMultiplier() {
        return firstSpouseMultiplier;
    }

    public List<ExpandedQuadruple> getSecondSpouseMultiplier() {
        return secondSpouseMultiplier;
    }

    public String getSecondSpouseOperator() {
        return secondSpouseOperator;
    }

    /**
     * Indica se algum dos conjuges estão vazios (nulos).
     *
     * @return true caso haja algum dado nulo de algum conjuge e false caso contrário.
     */
    public boolean areEmpty() {
        return firstSpouseFactor == null || firstSpouseMultiplier == null
                || secondSpouseFactor == null || secondSpouseMultiplier == null;
    }

    /**
     * Atribui o fator e o multiplicador ao conjuge correto. No exemplo x * (2 + 3), o fator seria x, e o multiplicador
     * seria (2 + 3).
     *
     * @param spouse        {@link ThreeAddressCode} que contém o conjuge a ser obtido dos valores.
     * @param isFirstSpouse booleano que indica se é o primeiro ou o segundo conjuge a ser atribuído.
     * @throws InvalidAlgebraicExpressionException Erro caso não consigo obter o fator em comum do conjuge.
     */
    private void setSpouse(ThreeAddressCode spouse, boolean isFirstSpouse) throws InvalidAlgebraicExpressionException {
        boolean isMinus = this.isFirstQuadrupleMinus(spouse.getRootQuadruple(), spouse);


        //usa a regra de fator em comum para obter o fator do conjuge

        FatorationRuleCommonFactor commonFactor = new FatorationRuleCommonFactor();
        ThreeAddressCode source = getResultSource(commonFactor, spouse);

        //atribui o fator ao conjuge correto
        if (isFirstSpouse)
            this.firstSpouseFactor = source.getRootQuadruple().getArgument1();
        else {
            this.secondSpouseFactor = source.getRootQuadruple().getArgument1();

            //caso específico de o fator do segundo conjuge ser -1
            if (secondSpouseFactor.equals("1") && isMinus)
                spouse.changeAllOperations();
        }


        //para obter a notação dos multiplicadores (representação em uma string só das quádruplas do multiplicador)
        String latexNotation = spouse.toLaTeXNotation().trim();
        String multiplierNotation = latexNotation.substring(latexNotation.indexOf('*') + 1);

        //atribui a notacao ao conjuge correto
        if (isFirstSpouse)
            this.firstMultiplierNotation = multiplierNotation;
        else
            this.secondMultiplierNotation = multiplierNotation;


        //obtendo os multiplicadores dos conjuges
        ExpandedQuadruple firstSourceRoot = spouse.getRootQuadruple();
        if (StringUtil.match(firstSourceRoot.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            for (ExpandedQuadruple expandedQuadruple : spouse.getExpandedQuadruples()) {

                //pega todas as quadruplas e coloca para o conjuge, com exceção a primeira, já que essa está com o fator
                if (!expandedQuadruple.equals(spouse.getRootQuadruple())) {
                    if (isFirstSpouse)
                        this.firstSpouseMultiplier.add(expandedQuadruple);
                    else
                        this.secondSpouseMultiplier.add(expandedQuadruple);
                }
            }
        }
    }



    /**
     * Verifica se a primeira quádrupla tem operador "MINUS".
     *
     * @param iterationQuadruple {@link ExpandedQuadruple} que representa qual a quádrupla inicial a procurar
     *                           pelo argumento.
     * @param source             {@link ThreeAddressCode} que contém todas as quádruplas a serem verificadas.
     * @return true caso a primeira quádrupla válida tenha operador "MINUS" e false caso contrário.
     */
    private boolean isFirstQuadrupleMinus(ExpandedQuadruple iterationQuadruple, ThreeAddressCode source) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return this.isFirstQuadrupleMinus(source.findQuadrupleByResult(iterationQuadruple.getArgument1()), source);
        }
        return iterationQuadruple.isNegative();
    }

    /**
     * Obtém o resultado da regra de fator em comum {@link FatorationRuleCommonFactor}.
     *
     * @param rule       {@link IRule} que é a classe da regra a ser obtido o resultado.
     * @param ruleSource {@link ThreeAddressCode} que vai ser utilizado pela {@code rule}.
     * @return novo {@link ThreeAddressCode} com o fator em comum.
     * @throws InvalidAlgebraicExpressionException Erro caso não encontre um fator em comum.
     */
    private ThreeAddressCode getResultSource(IRule rule, ThreeAddressCode ruleSource) throws InvalidAlgebraicExpressionException {
        return rule.handle(Collections.singletonList(ruleSource)).get(0).getSource().get(0);
    }


    /**
     * Indica se o multiplicador do primeiro conjuge é igual ao do segundo conjuge
     *
     * @return true caso sejam iguais e false caso contrário.
     */
    public boolean isFirstSpouseMultiplierEqualsSecond() {
        if (firstSpouseMultiplier.size() != secondSpouseMultiplier.size())
            if (listDoesntContainsMinus(firstSpouseMultiplier))
                return false;

        return firstMultiplierNotation.equals(secondMultiplierNotation);
    }

    /**
     * Verifica se em uma {@link List} de {@link ExpandedQuadruple} contém um item com operador "MINUS".
     *
     * @param expandedQuadruples {@link List} de {@link ExpandedQuadruple} a ser verificada.
     * @return true caso haja um item com operador "MINUS" e false caso contrário.
     */
    private boolean listDoesntContainsMinus(List<ExpandedQuadruple> expandedQuadruples) {
        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
            if (expandedQuadruple.isNegative())
                return false;
        }
        return true;
    }


    /**
     * Troca o operador da primeira quádrupla para "MINUS".
     *
     * @param source {@link ThreeAddressCode} que contém a quádrupla a ser alterada.
     */
    private void setMinusToFirstQuadruple(ThreeAddressCode source) {
        ExpandedQuadruple root = source.getRootQuadruple();
        source.addQuadrupleToList("MINUS", root.getArgument1(), "", root, true);
    }
}
