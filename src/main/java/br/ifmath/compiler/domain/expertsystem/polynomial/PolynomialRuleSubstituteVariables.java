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
import java.util.List;

public class PolynomialRuleSubstituteVariables implements IRule {
    private List<NumericValueVariable> userInput = new ArrayList<>();
    private String globalVariable = "";


    private List<ExpandedQuadruple> expandedQuadruples;

    public PolynomialRuleSubstituteVariables() {
        this.expandedQuadruples = new ArrayList<>();
        // Valor para teste switch_variable_y_to_3_scenery_one_with_success
        // this.userInput.add(new NumericValueVariable("y", 3));

        // Valor para teste sum_numbers_scenery_one_with_success
        this.userInput.add(new NumericValueVariable("y", 4));
    }


    @Override
    public boolean match(List<ThreeAddressCode> sources) {
        return this.isThereVariablesToSubstitute(sources.get(0).getOperationsFromRight()); // pegando operacoes da direita pois nao haverá sinal de igualdade. EX: x=3y^2
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> sources) throws InvalidAlgebraicExpressionException {
        expandedQuadruples.clear();

        List<Step> steps = new ArrayList<>();

        switchVariables(sources.get(0).getExpandedQuadruples().get(0));


        String right = generateParameter(sources.get(0), 1);

        ThreeAddressCode step = new ThreeAddressCode("x", sources.get(0).getComparison(), right, expandedQuadruples);
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation(), step.toMathNotation(), "Substituindo os valores nas variáveis correspondentes."));

        return steps;
    }

    private String generateParameter(ThreeAddressCode value, int position) {
        String parameter = "";

        if (StringUtil.match(value.getRight(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple expandedQuadruple = value.findQuadrupleByResult(value.getRight());
            parameter +=  expandedQuadruple.getArgument1() + expandedQuadruple.getOperator() + expandedQuadruple.getArgument2();

        }
        return parameter;
    }

    private void switchVariables(ExpandedQuadruple threeAddressCode) {
        //TODO For each variable in the right part of the threeaddresscode

        if (!this.userInput.isEmpty()) {
            if (threeAddressCode.getArgument1().contains(this.userInput.get(0).getLabel())) {
                threeAddressCode.setArgument1(this.userInput.get(0).getValue().toString());
                this.userInput.remove(0);
            }
        }
    }

    private boolean isThereVariablesToSubstitute(List<ExpandedQuadruple> expandedQuadruples) {
        //contador para verificar se existe uma variável a ser substituida
        int countEqualVariable = 0;
      //  List<ExpandedQuadruple> sonExpandedQuadruples = new ArrayList<>();

        //Verifica na lista de quadruplas se existe uma variavel a ser substituida
        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
            //verifica para cada temporário aninhado, se há uma variavel a ser trocada
//            if (StringUtil.match(expandedQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())){
//                sonExpandedQuadruples.add(new ThreeAddressCode().findQuadrupleByArgument(expandedQuadruple.getArgument1()));
//                this.isThereVariablesToSubstitute(sonExpandedQuadruples);
//            }
//
//            if( StringUtil.match(expandedQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())){
//                sonExpandedQuadruples.add(new ThreeAddressCode().findQuadrupleByArgument(expandedQuadruple.getArgument2()));
//                this.isThereVariablesToSubstitute(sonExpandedQuadruples);
//            }

            if (!userInput.isEmpty() && expandedQuadruple.getArgument1().contains(userInput.get(0).getLabel()) || expandedQuadruple.getArgument2().contains(userInput.get(0).getLabel())) {
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

        return (countEqualVariable > 0);
    }

    private String findVariable(ThreeAddressCode threeAddressCode, String param) {
        String variable = null;
        if (StringUtil.match(param, RegexPattern.TEMPORARY_VARIABLE.toString())) {
            ExpandedQuadruple expandedQuadruple = threeAddressCode.findQuadrupleByResult(param);

            variable = findVariable(threeAddressCode, expandedQuadruple.getArgument1());

            if (StringUtil.isEmpty(variable)) {
                variable = findVariable(threeAddressCode, expandedQuadruple.getArgument2());
            }
        } else if (isVariable(param)) {
            variable = StringUtil.removeNumericChars(param);
        }

        return variable;
    }

    private String findNumber(ThreeAddressCode threeAddressCode, String param) {
        ExpandedQuadruple expandedQuadruple = threeAddressCode.findQuadrupleByArgument(param);


        if (StringUtil.match(param, RegexPattern.TEMPORARY_VARIABLE.toString())) {
            expandedQuadruple = threeAddressCode.findQuadrupleByResult(param);
            globalVariable += findNumber(threeAddressCode, expandedQuadruple.getArgument1());

            globalVariable += findNumber(threeAddressCode, expandedQuadruple.getArgument2());

        } else if (NumberUtil.isInteger(Integer.parseInt(param))) {
            globalVariable = NumberUtil.removeVariable(param);

        }
        if (!globalVariable.equals("")) {

            globalVariable += expandedQuadruple.getOperator();
            globalVariable = NumberUtil.removeVariable(param);
        }
        return globalVariable;
    }

    private boolean isVariable(String param) {
        return StringUtil.matchAny(param, RegexPattern.VARIABLE.toString(), RegexPattern.VARIABLE_WITH_COEFICIENT.toString())
                && !StringUtil.match(param, RegexPattern.TEMPORARY_VARIABLE.toString());
    }
}
