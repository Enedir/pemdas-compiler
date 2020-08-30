package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.perfectsquaretrinomial;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.FatorationRuleIdentification;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.NumericValueVariable;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

//TODO Mudar nome da classe e do pacote para antender o quadrado e o cubo perfeito
public class FatorationRulePerfectSquareTrinomialExpandedFormulaConversion implements IRule {

    private ThreeAddressCode source;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return FatorationRuleIdentification.isPerfectSquareTrinomial(source.get(0)) ||
                FatorationRuleIdentification.isPerfectCube(source.get(0));
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> source) {
        List<Step> steps = new ArrayList<>();
        this.source = source.get(0);
        boolean isPerfectSquare = FatorationRuleIdentification.isPerfectSquareTrinomial(source.get(0));
        String sign = this.adjustToExpandedFormula(isPerfectSquare);
        String reason = (isPerfectSquare) ? "Escrevemos a " +
                "expressão no formato a^2 + 2 * a * b &#177; b^2, identificando os elementos que estão elevados ao " +
                "quadrado e os respectivos produtos." : "Escrevemos a " +
                "expressão no formato a^3 " + sign + " 3 * a^2 * b + 3 * a * b^2 " + sign + " b^3, identificando os " +
                "elementos que estão elevados ao cubo, ao quadrado e os respectivos produtos.";

        this.source.clearNonUsedQuadruples();

        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), reason));
        return steps;
    }

    private String adjustToExpandedFormula(boolean isPerfectSquare) {
        ExpandedQuadruple root = this.source.getRootQuadruple();
        if (StringUtil.match(root.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()))
            root = this.source.findQuadrupleByResult(root.getArgument1());
        ExpandedQuadruple lastQuadruple = this.source.getLastQuadruple(
                this.source.findQuadrupleByArgument(root.getArgument2()));
        if (isPerfectSquare)
            this.convertSquareMiddleTerm(lastQuadruple, root);
        else
            this.convertCubeMiddleTerm(lastQuadruple, root);
        lastQuadruple.setArgument2(this.convertToRaisedValue(lastQuadruple.getArgument2(), isPerfectSquare));
        root.setArgument1(this.convertToRaisedValue(this.source.getRootQuadruple().getArgument1(), isPerfectSquare));
        return root.getOperator();
    }


    private String convertToRaisedValue(String argument, boolean isPerfectSquare) {
        return reduceToRaisedValue(argument, this.source, isPerfectSquare);
    }


    public static String reduceToRaisedValue(String argument, ThreeAddressCode source, boolean isRaisedByTwo) {
        NumericValueVariable nvv = new NumericValueVariable(argument);
        int exponent = (isRaisedByTwo) ? 2 : 3;
        if (nvv.getValue() != 1)
            nvv.setValue((isRaisedByTwo) ? (int) Math.sqrt(nvv.getValue()) : (int) Math.cbrt(nvv.getValue()));
        if (nvv.getLabelPower() > 1)
            nvv.setLabelPower(nvv.getLabelPower() / exponent);
        ExpandedQuadruple parenthesesQuadruple = new ExpandedQuadruple(
                "", nvv.toString(), "", source.retrieveNextTemporary(), 0, 1);
        source.getExpandedQuadruples().add(parenthesesQuadruple);
        ExpandedQuadruple exponentQuadruple = new ExpandedQuadruple(
                "^", parenthesesQuadruple.getResult(), String.valueOf(exponent), source.retrieveNextTemporary(), 0, 0);
        source.getExpandedQuadruples().add(exponentQuadruple);
        return exponentQuadruple.getResult();
    }

    private void convertSquareMiddleTerm(ExpandedQuadruple lastQuadruple, ExpandedQuadruple firstArgument) {
        lastQuadruple.setArgument1(getConvertedTerm(this.source.findDirectSonArgument(lastQuadruple.getArgument2(), true),
                true));
        this.source.addQuadrupleToList("*",
                this.getConvertedTerm(this.source.findDirectSonArgument(firstArgument.getArgument1(), true), true),
                lastQuadruple.getResult(), firstArgument, false);
        this.source.addQuadrupleToList("*", "2", firstArgument.getArgument2(), firstArgument, false);
    }

    private String getConvertedTerm(String argument, boolean isPerfectSquare) {

        if (StringUtil.match(argument, RegexPattern.NATURAL_NUMBER.toString()))
            return String.valueOf((isPerfectSquare) ? (int) Math.sqrt(Integer.parseInt(argument)) :
                    (int) Math.cbrt(Integer.parseInt(argument)));

        NumericValueVariable nvv = new NumericValueVariable(argument);
        int exponent = (isPerfectSquare) ? 2 : 3;
        nvv.setLabelPower(nvv.getLabelPower() / exponent);
        if (nvv.getValue() != 1)
            nvv.setValue((isPerfectSquare) ? (int) Math.sqrt(nvv.getValue()) : (int) Math.cbrt(nvv.getValue()));
        return nvv.toString();
    }

    private void convertCubeMiddleTerm(ExpandedQuadruple lastQuadruple, ExpandedQuadruple firstArgument) {
        String firstArgumentValue = this.getConvertedTerm(this.source.findDirectSonArgument(firstArgument.getArgument1(), true), false);
        String secondArgumentValue = this.getConvertedTerm(this.source.findDirectSonArgument(lastQuadruple.getArgument2(), true), false);
        lastQuadruple.setArgument1("(" + secondArgumentValue + ")^2");
        this.source.addQuadrupleToList("*", firstArgumentValue, lastQuadruple.getResult(), firstArgument, false);
        this.source.addQuadrupleToList("*", "3", firstArgument.getArgument2(), firstArgument, false);
        this.source.addQuadrupleToList("+", secondArgumentValue, firstArgument.getArgument2(), firstArgument, false);
        this.source.addQuadrupleToList("*", "(" + firstArgumentValue + ")^2", firstArgument.getArgument2(), firstArgument, false);
        this.source.addQuadrupleToList("*", "3", firstArgument.getArgument2(), firstArgument, false);
    }
}
