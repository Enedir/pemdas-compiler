package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.commonfactor;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.FatorationRuleIdentification;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.Monomial;
import br.ifmath.compiler.domain.grammar.nonterminal.M;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.MathOperatorUtil;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class FatorationRuleCommonFactor implements IRule {

    private ThreeAddressCode source;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return FatorationRuleIdentification.isCommonFactor(source.get(0).getRootQuadruple(), source.get(0));
    }


    @Override
    public List<Step> handle(List<ThreeAddressCode> source) {
        List<Step> steps = new ArrayList<>();
        this.source = source.get(0);
        String commonFactor = this.getCommonFactor();
        if (!commonFactor.isEmpty())
            this.groupCommonFactor(commonFactor);

        this.source.clearNonUsedQuadruples();
        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Verificamos o elemento que temos em comum e colocamos em evidência."));
        return steps;
    }

    //<editor-fold desc="getCommonFactor">
    private String getCommonFactor() {
        String rootArgument1 = this.getFirstArgument(this.source.getRootQuadruple());
        Monomial initialMonomial = new Monomial(rootArgument1);
        int coefficient = this.getCommonCoefficient(this.source.getRootQuadruple(), initialMonomial.getCoefficient());
        String literal = this.getCommonLiteral(this.source.getRootQuadruple(), new Monomial(initialMonomial.getLiteral()));
        return new Monomial(literal, coefficient).toString();
    }

    private String getFirstArgument(ExpandedQuadruple iterationQuadruple) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return this.getFirstArgument(source.findQuadrupleByResult(iterationQuadruple.getArgument1()));
        }
        return iterationQuadruple.getArgument1();
    }

    private int getCommonCoefficient(ExpandedQuadruple iterationQuadruple, int coefficient) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return this.getCommonCoefficient(source.findQuadrupleByResult(iterationQuadruple.getArgument1()), coefficient);
        }

        Monomial monomial = new Monomial(iterationQuadruple.getArgument1());

        coefficient = (int) MathOperatorUtil.gcd(coefficient, monomial.getCoefficient());
        if (iterationQuadruple.isNegative())
            iterationQuadruple = source.findQuadrupleByArgument(iterationQuadruple.getResult());

        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return this.getCommonCoefficient(source.findQuadrupleByResult(iterationQuadruple.getArgument2()), coefficient);
        }

        monomial.setAttributesFromString(iterationQuadruple.getArgument2());

        coefficient = (int) MathOperatorUtil.gcd(coefficient, monomial.getCoefficient());
        return coefficient;
    }

    private String getCommonLiteral(ExpandedQuadruple iterationQuadruple, Monomial literal) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return this.getCommonLiteral(source.findQuadrupleByResult(iterationQuadruple.getArgument1()), literal);
        }

        Monomial monomial = new Monomial(iterationQuadruple.getArgument1());

        literal = this.getLowestDegreeLiteral(monomial, literal);
        if (literal.toString().isEmpty())
            return literal.toString();

        if (iterationQuadruple.isNegative())
            iterationQuadruple = source.findQuadrupleByArgument(iterationQuadruple.getResult());

        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return this.getCommonLiteral(source.findQuadrupleByResult(iterationQuadruple.getArgument2()), literal);
        }

        monomial = new Monomial(iterationQuadruple.getArgument2());

        literal = this.getLowestDegreeLiteral(monomial, literal);

        return literal.toString();
    }

    private Monomial getLowestDegreeLiteral(Monomial firstLiteral, Monomial secondLiteral) {

        if (firstLiteral.getLiteral().isEmpty() || secondLiteral.getLiteral().isEmpty() ||
                !firstLiteral.getLiteralVariable().equals(secondLiteral.getLiteralVariable()))
            return new Monomial();

        return (firstLiteral.getLiteralDegree() < secondLiteral.getLiteralDegree()) ?
                new Monomial(firstLiteral.getLiteral()) :
                new Monomial(secondLiteral.getLiteral());
    }
    //</editor-fold>>

    //<editor-fold desc="isEqualPattern">
    private boolean isEqualPattern(ExpandedQuadruple iterationQuadruple, Monomial pattern) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return this.isEqualPattern(source.findQuadrupleByResult(iterationQuadruple.getArgument1()), pattern);
        }

        if (this.isDifferentPattern(iterationQuadruple.getArgument1(), pattern))
            return false;

        if (iterationQuadruple.isNegative())
            iterationQuadruple = source.findQuadrupleByArgument(iterationQuadruple.getResult());

        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            return this.isEqualPattern(source.findQuadrupleByResult(iterationQuadruple.getArgument2()), pattern);
        }

        return !this.isDifferentPattern(iterationQuadruple.getArgument2(), pattern);

    }

    private boolean isDifferentPattern(String argument, Monomial pattern) {
        if (StringUtil.match(argument, RegexPattern.TEMPORARY_VARIABLE.toString()))
            argument = this.source.findQuadrupleByResult(argument).getArgument1();
        Monomial monomial = new Monomial(argument);
        if (pattern.getLiteral().isEmpty() && pattern.getCoefficient() == null) {
            return true;
        } else if (pattern.getLiteral().isEmpty()) {
            return monomial.getCoefficient() % pattern.getCoefficient() != 0;

        } else if (pattern.getCoefficient() == null) {
            if (!StringUtil.match(monomial.toString(), RegexPattern.VARIABLE_WITH_EXPONENT.toString()))
                return !monomial.getLiteral().contains(pattern.getLiteral());
            return itDoesntMatchMonomy(pattern.getLiteralDegree(), monomial.getLiteralDegree(), monomial.getLiteralVariable(), pattern.getLiteralVariable(), false);
        }
        if (pattern.getCoefficient() == 1)
            return !monomial.getLiteral().contains(pattern.getLiteralVariable());
        return itDoesntMatchMonomy(pattern.getCoefficient(), monomial.getCoefficient(), monomial.getLiteralVariable(), pattern.getLiteralVariable(), true);
    }

    private boolean itDoesntMatchMonomy(int numeralPart1, int numeralPart2, String literalPart1, String literalPart2,
                                        boolean isValue) {
        return !((this.isMultiple(numeralPart1, numeralPart2, isValue))
                && literalPart1.contains(literalPart2));
    }

    private boolean isMultiple(int n1, int n2, boolean isLabel) {
        return (isLabel) ? (n1 % n2 == 0 || n2 % n1 == 0) && !(n1 == 1 || n2 == 1) :
                (n1 % n2 == 0 || n2 % n1 == 0) || ((n1 == 1 && n2 != 1) || (n2 == 1) && n1 != 1);
    }
    //</editor-fold>>

    //<editor-fold desc="groupCommonFactor">
    private void groupCommonFactor(String commonFactor) {
        this.removeCommonFactor(this.source.getRootQuadruple(), commonFactor);
        this.surroundQuadruplesWithParentheses(this.source.getLeft());
        ExpandedQuadruple newRoot = new ExpandedQuadruple("*", commonFactor, this.source.getLeft(), this.source.retrieveNextTemporary(), 0, 0);
        this.source.getExpandedQuadruples().add(newRoot);
        this.source.setLeft(newRoot.getResult());
    }

    private void removeCommonFactor(ExpandedQuadruple iterationQuadruple, String commonFactor) {
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            this.removeCommonFactor(source.findQuadrupleByResult(iterationQuadruple.getArgument1()), commonFactor);
            return;
        }

        this.adjustTermsByFactor(iterationQuadruple, commonFactor, true);

        if (iterationQuadruple.isNegative())
            iterationQuadruple = source.findQuadrupleByArgument(iterationQuadruple.getResult());


        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            this.removeCommonFactor(source.findQuadrupleByResult(iterationQuadruple.getArgument2()), commonFactor);
            return;
        }

        this.adjustTermsByFactor(iterationQuadruple, commonFactor, false);
    }

    private void adjustTermsByFactor(ExpandedQuadruple argumentQuadruple, String commonFactor, boolean isArgument1) {
        String argument = (isArgument1) ? argumentQuadruple.getArgument1() : argumentQuadruple.getArgument2();
        if (StringUtil.match(argument, RegexPattern.TEMPORARY_VARIABLE.toString()))
            argument = this.source.findQuadrupleByResult(argument).getArgument1();
        String newArgument;
        if (StringUtil.match(commonFactor, RegexPattern.VARIABLE.toString()) && argument.contains(commonFactor)) {
            if (argument.equals(commonFactor))
                newArgument = "1";
            else if (StringUtil.match(argument, RegexPattern.VARIABLE_WITH_COEFFICIENT.toString()))
                newArgument = argument.replace(commonFactor, "");
            else {
                Monomial monomial = new Monomial(argument);
                monomial.setLiteralDegree(monomial.getLiteralDegree() - 1);
                newArgument = monomial.toString();
            }
        } else if (StringUtil.match(commonFactor, RegexPattern.NATURAL_NUMBER.toString())) {
            int commonFactorValue = Integer.parseInt(commonFactor);
            Monomial monomial = new Monomial(argument);
            int iterationValue = monomial.getCoefficient();
            if (iterationValue % commonFactorValue == 0)
                monomial.setCoefficient(iterationValue / commonFactorValue);
            newArgument = monomial.toString();

        } else {
            Monomial argumentMonomial = new Monomial(argument);
            Monomial monomialFactor = new Monomial(commonFactor);

            if (argumentMonomial.getLiteral().equals(monomialFactor.getLiteral()))
                argumentMonomial.setLiteral("");
            else
                argumentMonomial.setLiteralDegree(argumentMonomial.getLiteralDegree() - monomialFactor.getLiteralDegree());

            argumentMonomial.setCoefficient(argumentMonomial.getCoefficient() / monomialFactor.getCoefficient());

            newArgument = argumentMonomial.toString();
        }

        if (isArgument1)
            argumentQuadruple.setArgument1(newArgument);
        else
            argumentQuadruple.setArgument2(newArgument);
    }

    private void surroundQuadruplesWithParentheses(String companionResult) {
        ExpandedQuadruple iterationQuadruple = this.source.findQuadrupleByResult(companionResult);
        if (StringUtil.match(iterationQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            this.surroundQuadruplesWithParentheses(source.findQuadrupleByResult(iterationQuadruple.getArgument1()).getResult());
        }

        iterationQuadruple.setLevel(1);

        if (StringUtil.match(iterationQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            this.surroundQuadruplesWithParentheses(source.findQuadrupleByResult(iterationQuadruple.getArgument2()).getResult());
        }
    }
    //</editor-fold>>

}
