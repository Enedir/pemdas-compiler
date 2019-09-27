package br.ifmath.compiler.domain.expertsystem.polynomial;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.NumericValueVariable;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.NumberUtil;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PolynomialRuleSubstituteVariables implements IRule {
    private List<NumericValueVariable> userInput = new ArrayList<>();


    private List<ExpandedQuadruple> expandedQuadruples;

    public PolynomialRuleSubstituteVariables() {
        this.expandedQuadruples = new ArrayList<>();
        /** Valores para testes
         */
        this.userInput.add(new NumericValueVariable("a", 777));
        this.userInput.add(new NumericValueVariable("y", 3));
        this.userInput.add(new NumericValueVariable("z", 4));
    }


    @Override
    public boolean match(List<ThreeAddressCode> sources) {
        /**
         * pegando operacoes da direita pois nao havera sinal de igualdade. EX: x=3y^2
         */
        return this.isThereVariablesToSubstitute(sources.get(0).getOperationsFromRight());
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> sources) throws InvalidAlgebraicExpressionException {
        expandedQuadruples.clear();

        List<Step> steps = new ArrayList<>();

        switchVariables(sources.get(0).getExpandedQuadruples());

        String right = generateParameter(sources.get(0));

        expandedQuadruples = sources.get(0).getExpandedQuadruples();

        //FIXME: primeiro parâmetro hardcoded devido a restrição do compilador
        ThreeAddressCode step = new ThreeAddressCode("x", sources.get(0).getComparison(), right, expandedQuadruples);
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation(), step.toMathNotation().trim(), "Substituindo os valores nas variáveis correspondentes."));

        return steps;
    }

//FIXME Verificação da entrada do usuario ser negativa, para isso deve ser criada uma quádrupla

//    private void checkNegativeInput(List<ExpandedQuadruple> source) {
//        List<ExpandedQuadruple> negativeInputs = new ArrayList<>();
//        for (NumericValueVariable input : userInput) {
//            if (input.getValue().toString().startsWith("-")) {
//                String cleanInput = input.getValue().toString().replace("-", "").trim();
//                ExpandedQuadruple negativeQuadruple = new ExpandedQuadruple("MINUS", cleanInput, "T" + (source.size() + 1), 0, 0);
//                negativeInputs.add(negativeQuadruple);
//                for (ExpandedQuadruple expandedQuadruple : source) {
//                    if (input.getLabel().equals(expandedQuadruple.getArgument1())) {
//                        expandedQuadruple.setArgument1(negativeQuadruple.getResult());
//                    }
//                    if (input.getLabel().equals(expandedQuadruple.getArgument2())) {
//                        expandedQuadruple.setArgument2(negativeQuadruple.getResult());
//                    }
//                }
//            }
//        }
//
//    }

    /**
     * Transforma o {@link ThreeAddressCode} em uma {@link String}
     *
     * @param value codigo de tres endereços a ser transformado
     * @return {@link String} que representa o {@link ThreeAddressCode}
     */
    private String generateParameter(ThreeAddressCode value) {
        List<ExpandedQuadruple> quadruples = value.getExpandedQuadruples();
        return quadruples.get(quadruples.size() - 1).getResult();
    }

    /**
     * Altera todas as variaveis presentes no polinomio para seus valores
     * respectivos.
     *
     * @param expandedQuadruples Lista de quadruplas expandidadas para substituicao
     */
    private void switchVariables(List<ExpandedQuadruple> expandedQuadruples) {
        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
            replaceTemporaryVariables(expandedQuadruple);
        }

        userInput.remove(0);

        if (!userInput.isEmpty()) {
            switchVariables(expandedQuadruples);
        }
    }

    /**
     * Efetivamente substitui as variaveis contidas no polinomio
     *
     * @param expandedQuadruple uma {@link ExpandedQuadruple} expecifica a ser analisada
     */
    private void replaceTemporaryVariables(ExpandedQuadruple expandedQuadruple) {

        //FIXME: deve ser alterado quando forem utilizados valores reais do frontend
        if (!this.userInput.isEmpty()) {

            if (!StringUtil.match(expandedQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                if (expandedQuadruple.getArgument1().contains(this.userInput.get(0).getLabel())) {
                    expandedQuadruple.setArgument1(this.userInput.get(0).getValue().toString());
                }
            }

            if (!StringUtil.match(expandedQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                if (!expandedQuadruple.getOperator().equals("MINUS")) {
                    if (expandedQuadruple.getArgument2().contains(this.userInput.get(0).getLabel())) {
                        expandedQuadruple.setArgument2(this.userInput.get(0).getValue().toString());
                    }
                }
            }
        }
    }

    /**
     * Verifica se ha alguma variavel a ser substituida dentro do polinomio
     *
     * @param expandedQuadruples lista de quadruplas que representam o polinomio
     * @return true caso haja pelo menos uma variavel a ser substituida
     */
    private boolean isThereVariablesToSubstitute(List<ExpandedQuadruple> expandedQuadruples) {
        int countEqualVariable = 0;

        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {

            if (!expandedQuadruple.getOperator().equals("MINUS")) {
                if (!userInput.isEmpty() && (expandedQuadruple.getArgument1().contains(userInput.get(0).getLabel()) || expandedQuadruple.getArgument2().contains(userInput.get(0).getLabel()))) {
                    countEqualVariable++;
                }

                if (userInput.size() > 1) {
                    if (expandedQuadruple.getArgument1().contains(userInput.get(1).getLabel()) || expandedQuadruple.getArgument2().contains(userInput.get(1).getLabel())) {
                        countEqualVariable++;
                    }
                }

                if (userInput.size() > 2) {
                    if (expandedQuadruple.getArgument1().contains(userInput.get(2).getLabel()) || expandedQuadruple.getArgument2().contains(userInput.get(2).getLabel())) {
                        countEqualVariable++;
                    }
                }

                if (userInput.size() > 3) {
                    if (expandedQuadruple.getArgument1().contains(userInput.get(3).getLabel()) || expandedQuadruple.getArgument2().contains(userInput.get(3).getLabel())) {
                        countEqualVariable++;
                    }
                }

                if (userInput.size() > 4) {
                    if (expandedQuadruple.getArgument1().contains(userInput.get(4).getLabel()) || expandedQuadruple.getArgument2().contains(userInput.get(4).getLabel())) {
                        countEqualVariable++;
                    }
                }
            }

        }

        return (countEqualVariable > 0);
    }

}
