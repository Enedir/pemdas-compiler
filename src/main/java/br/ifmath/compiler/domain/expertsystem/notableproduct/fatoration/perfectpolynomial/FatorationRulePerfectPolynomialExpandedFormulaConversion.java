package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.perfectpolynomial;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.FatorationRuleIdentification;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.Monomial;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class FatorationRulePerfectPolynomialExpandedFormulaConversion implements IRule {

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
        String reason = (isPerfectSquare) ?
                "Escrevemos a expressão no formato &ascr;&sup2; " + sign + " 2 &middot; &ascr; &middot; &bscr; &plus;" +
                        " &bscr;&sup2;, identificando os elementos que estão elevados ao quadrado e os respectivos produtos." :

                "Escrevemos a expressão no formato &ascr;&sup3; " + sign + " 3 &middot; &ascr;&sup2; &middot; &bscr; " +
                        "&plus; 3 &middot; &ascr; &middot; &bscr;&sup2; " + sign + " &bscr;&sup3;," +
                        " identificando os elementos que estão elevados ao cubo, ao quadrado e os respectivos produtos.";

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

        return (root.getOperator().equals("+")) ? "&plus;" : "&minus;";
    }


    private String convertToRaisedValue(String argument, boolean isPerfectSquare) {
        return reduceToRaisedValue(argument, this.source, isPerfectSquare);
    }


    public static String reduceToRaisedValue(String argument, ThreeAddressCode source, boolean isRaisedByTwo) {
        Monomial monomial = new Monomial(argument);
        int exponent = (isRaisedByTwo) ? 2 : 3;

        if (monomial.getCoefficient() != 1)
            monomial.setCoefficient((isRaisedByTwo) ? (int) Math.sqrt(monomial.getCoefficient()) : (int) Math.cbrt(monomial.getCoefficient()));

        if (monomial.getLiteralDegree() > 1)
            monomial.setLiteralDegree(monomial.getLiteralDegree() / exponent);

        ExpandedQuadruple parenthesesQuadruple = new ExpandedQuadruple("", monomial.toString(), "", source.retrieveNextTemporary(), 0, 1);
        source.getExpandedQuadruples().add(parenthesesQuadruple);

        ExpandedQuadruple exponentQuadruple = new ExpandedQuadruple("^", parenthesesQuadruple.getResult(), String.valueOf(exponent), source.retrieveNextTemporary(), 0, 0);
        source.getExpandedQuadruples().add(exponentQuadruple);

        return exponentQuadruple.getResult();
    }

    private void convertSquareMiddleTerm(ExpandedQuadruple lastQuadruple, ExpandedQuadruple firstArgument) {
        String sonArgument = this.source.findDirectSonArgument(lastQuadruple.getArgument2(), true);
        lastQuadruple.setArgument1(this.getConvertedTerm(sonArgument, true));

        sonArgument = this.source.findDirectSonArgument(firstArgument.getArgument1(), true);
        this.source.addQuadrupleToList("*", this.getConvertedTerm(sonArgument, true),
                lastQuadruple.getResult(), firstArgument, false);

        this.source.addQuadrupleToList("*", "2", firstArgument.getArgument2(), firstArgument, false);
    }

    private String getConvertedTerm(String argument, boolean isPerfectSquare) {

        if (StringUtil.match(argument, RegexPattern.NATURAL_NUMBER.toString()))
            return String.valueOf((isPerfectSquare) ?
                    (int) Math.sqrt(Integer.parseInt(argument)) :
                    (int) Math.cbrt(Integer.parseInt(argument)));

        Monomial monomial = new Monomial(argument);
        int exponent = (isPerfectSquare) ? 2 : 3;

        monomial.setLiteralDegree(monomial.getLiteralDegree() / exponent);
        if (monomial.getCoefficient() != 1)
            monomial.setCoefficient((isPerfectSquare) ? (int) Math.sqrt(monomial.getCoefficient()) : (int) Math.cbrt(monomial.getCoefficient()));

        return monomial.toString();
    }

    private void convertCubeMiddleTerm(ExpandedQuadruple lastQuadruple, ExpandedQuadruple firstArgument) {
        String sonArgument = this.source.findDirectSonArgument(firstArgument.getArgument1(), true);
        String firstArgumentValue = this.getConvertedTerm(sonArgument, false);

        sonArgument = this.source.findDirectSonArgument(lastQuadruple.getArgument2(), true);
        String secondArgumentValue = this.getConvertedTerm(sonArgument, false);

        lastQuadruple.setArgument1("(" + secondArgumentValue + ")^2");
        this.source.addQuadrupleToList("*", firstArgumentValue, lastQuadruple.getResult(), firstArgument, false);
        this.source.addQuadrupleToList("*", "3", firstArgument.getArgument2(), firstArgument, false);
        this.source.addQuadrupleToList("+", secondArgumentValue, firstArgument.getArgument2(), firstArgument, false);
        this.source.addQuadrupleToList("*", "(" + firstArgumentValue + ")^2", firstArgument.getArgument2(), firstArgument, false);
        this.source.addQuadrupleToList("*", "3", firstArgument.getArgument2(), firstArgument, false);
    }
}
