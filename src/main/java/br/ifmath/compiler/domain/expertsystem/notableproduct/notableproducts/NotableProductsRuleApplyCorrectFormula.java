package br.ifmath.compiler.domain.expertsystem.notableproduct.notableproducts;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.NumericValueVariable;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class NotableProductsRuleApplyCorrectFormula implements IRule {

    private ThreeAddressCode source;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return true;
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> source) {
        List<Step> steps = new ArrayList<>();
        this.source = source.get(0);

        String explanation = this.applyFormula();

        this.source.clearNonUsedQuadruples();
        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Aplicando o produto notável fazendo o " + explanation));
        this.adjustParenthesesValue();
        return steps;
    }

    private void adjustParenthesesValue() {
        for (ExpandedQuadruple expandedQuadruple : this.source.getExpandedQuadruples()) {
            if (StringUtil.match(expandedQuadruple.getOperator(), RegexPattern.VARIABLE_WITH_COEFICIENT.toString())) {
                expandedQuadruple.setArgument1(expandedQuadruple.getOperator());
                expandedQuadruple.setOperator("");
            }
        }

    }

    private String applyFormula() {
        String rule = "";
        ExpandedQuadruple root = this.source.findQuadrupleByResult(this.source.getLeft());
        if (StringUtil.match(root.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            if (root.isPotentiation()) {

                ExpandedQuadruple innerQuadruple = this.source.findQuadrupleByResult(root.getArgument1());

                if (!StringUtil.match(innerQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {

                    rule = this.twoTermsPower(root, innerQuadruple);
                }

            } else if (root.isTimes() &&
                    StringUtil.match(root.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {

                ExpandedQuadruple leftInnerQuadruple = this.source.findQuadrupleByResult(root.getArgument1());
                ExpandedQuadruple rightInnerQuadruple = this.source.findQuadrupleByResult(root.getArgument2());

                String firstArgument = leftInnerQuadruple.getArgument1();
                String secondArgument = leftInnerQuadruple.getArgument2();

                if (secondArgument.equals(rightInnerQuadruple.getArgument2())) {
                    if (isThereAMonomy(leftInnerQuadruple, true)) {
                        this.createParentheses(firstArgument, true);
                        firstArgument = root.getArgument1();
                    }
                    ExpandedQuadruple parenthesesQuadruple = null;
                    if (isThereAMonomy(leftInnerQuadruple, false)) {
                        parenthesesQuadruple = this.createParentheses(secondArgument, false);
                        parenthesesQuadruple.setArgument1(parenthesesQuadruple.getOperator());
                        parenthesesQuadruple.setOperator("MINUS");
                        String lastQuadrupleResult = this.source.getExpandedQuadruples().get(this.source.getExpandedQuadruples().size() - 1).getResult();
                        if (!root.getArgument2().equals(lastQuadrupleResult)) {
                            ExpandedQuadruple incorrectQuadruple = this.source.findQuadrupleByArgument(lastQuadrupleResult);
                            incorrectQuadruple.setArgument2("");
                            root.setArgument2(lastQuadrupleResult);
                        }
                        secondArgument = root.getArgument2();
                    }

                    rule = this.productOfSumAndDif(root, firstArgument, secondArgument);
                    if (parenthesesQuadruple != null)
                        root.setOperator("+");
                }
            }
        }
        return rule;
    }


    private String productOfSumAndDif(ExpandedQuadruple rootQuadruple, String firstTerm, String secondTerm) {
        this.setCorrectArgument(firstTerm, rootQuadruple, true);
        rootQuadruple.setOperator("-");
        this.setCorrectArgument(secondTerm, rootQuadruple, false);
        return "quadrado do primeiro termo, menos o quadrado do segundo termo.";
    }

    private void setCorrectArgument(String argument, ExpandedQuadruple rootQuadruple, boolean isArgument1) {
        if (StringUtil.match(argument, RegexPattern.TEMPORARY_VARIABLE.toString()))
            this.source.addQuadrupleToList("^", argument, "2", rootQuadruple, isArgument1);
        else {
            if (StringUtil.match(argument, RegexPattern.VARIABLE_WITH_EXPOENT.toString())) {
                ExpandedQuadruple argumentQuadruple = this.source.findQuadrupleByArgument(argument);
                argument = this.getPotentiationTerm(argumentQuadruple, "2", rootQuadruple, isArgument1);
            } else
                argument += "^2";
            if (isArgument1) {
                rootQuadruple.setArgument1(argument);
            } else {
                rootQuadruple.setArgument2(argument);
            }
        }
    }

    private String twoTermsPower(ExpandedQuadruple root, ExpandedQuadruple innerQuadruple) {
        String sign = innerQuadruple.isPlus() ? "mais" : "menos";

        String explanation = "";
        String exponent = "";
        if (root.getArgument2().equals("2")) {
            exponent = "2";
            explanation = this.twoTermsSquare(root, innerQuadruple, sign);
        } else if (root.getArgument2().equals("3")) {
            exponent = "3";
            explanation = this.twoTermsCube(root, innerQuadruple, sign);
        }
        root.setOperator(innerQuadruple.isPlus() ? "+" : "-");
        if (!isThereAMonomy(innerQuadruple, true) &&
                !StringUtil.match(innerQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()) &&
                !StringUtil.match(innerQuadruple.getArgument1(), RegexPattern.VARIABLE_WITH_EXPOENT.toString()))
            root.setArgument1(innerQuadruple.getArgument1() + "^" + exponent);
        else {
            String argument1 = this.getPotentiationTerm(innerQuadruple, exponent, root, true);
            if (argument1.isEmpty())
                this.source.addQuadrupleToList("^", innerQuadruple.getArgument1(), exponent, root, true);
            else
                root.setArgument1(argument1);
            if (isThereAMonomy(innerQuadruple, true))
                this.createParentheses(this.source.getExpandedQuadruples().get(this.source.getExpandedQuadruples().size() - 1).getArgument1(), true);
        }
        return explanation;
    }

    private String twoTermsSquare(ExpandedQuadruple rootQuadruple, ExpandedQuadruple termsQuadruple, String sign) {
        if (!this.isThereAMonomy(termsQuadruple, false)) {
            String potentiationResult = this.getPotentiationTerm(termsQuadruple, "2", rootQuadruple, false);
            this.source.addQuadrupleToList("+", termsQuadruple.getArgument2(), (potentiationResult.isEmpty()) ? termsQuadruple.getArgument2() + "^2" : potentiationResult, rootQuadruple, false);
        } else {
            this.source.addQuadrupleToList("^", termsQuadruple.getArgument2(), "2", rootQuadruple, false);
            this.createParentheses(this.source.getExpandedQuadruples().get(this.source.getExpandedQuadruples().size() - 1).getArgument1(), true);
            this.source.addQuadrupleToList("+", termsQuadruple.getArgument2(), rootQuadruple.getArgument2(), rootQuadruple, false);
        }
        this.source.addQuadrupleToList("*", termsQuadruple.getArgument1(), rootQuadruple.getArgument2(), rootQuadruple, false);
        this.source.addQuadrupleToList("*", "2", rootQuadruple.getArgument2(), rootQuadruple, false);
        return "quadrado do primeiro termo, " + sign + " o dobro do produto " +
                "do primeiro pelo segundo termo, mais o quadrado do segundo termo.";
    }

    private ExpandedQuadruple createParentheses(String argument, boolean setOnArgument1) {
        this.source.addQuadrupleToList(argument, "", "", this.source.getExpandedQuadruples().get(this.source.getExpandedQuadruples().size() - 1), setOnArgument1);
        ExpandedQuadruple lastQuadruple = this.source.getExpandedQuadruples().get(this.source.getExpandedQuadruples().size() - 1);
        lastQuadruple.setLevel(1);
        return lastQuadruple;
    }


    private String twoTermsCube(ExpandedQuadruple rootQuadruple, ExpandedQuadruple termsQuadruple, String sign) {
        String lastOperator = (termsQuadruple.isPlus()) ? "+" : "-";
        if (!this.isThereAMonomy(termsQuadruple, false)) {
            String resultArgument1 = this.getPotentiationTerm(termsQuadruple, "2", rootQuadruple, false);
            String resultArgument2 = this.getPotentiationTerm(termsQuadruple, "3", rootQuadruple, false);
            this.source.addQuadrupleToList(lastOperator,
                    (resultArgument1.isEmpty()) ? termsQuadruple.getArgument2() + "^2" : resultArgument1, (resultArgument2.isEmpty()) ? termsQuadruple.getArgument2() + "^3" : resultArgument2, rootQuadruple, false);
        } else {
            this.source.addQuadrupleToList("^", termsQuadruple.getArgument2(), "3", rootQuadruple, false);
            String parenthesesQuadrupleResult = this.createParentheses(this.source.getExpandedQuadruples().get(this.source.getExpandedQuadruples().size() - 1).getArgument1(), true).getResult();
            String powerThreeQuadrupleResult = this.source.findQuadrupleByArgument(parenthesesQuadrupleResult).getResult();
            String powerTwoQuadrupleResult = this.source.addQuadrupleToList("^", parenthesesQuadrupleResult, "2", rootQuadruple, false).getResult();
            this.source.addQuadrupleToList(lastOperator, powerTwoQuadrupleResult, powerThreeQuadrupleResult, rootQuadruple, false);
        }
        this.source.addQuadrupleToList("*", termsQuadruple.getArgument1(), rootQuadruple.getArgument2(), rootQuadruple, false);
        this.source.addQuadrupleToList("*", "3", rootQuadruple.getArgument2(), rootQuadruple, false);
        this.source.addQuadrupleToList("+", termsQuadruple.getArgument2(), rootQuadruple.getArgument2(), rootQuadruple, false);
        ExpandedQuadruple powerQuadruple;
        if (this.isThereAMonomy(termsQuadruple, true)) {
            String argument2QuadrupleResult = this.source.getExpandedQuadruples().get(this.source.getExpandedQuadruples().size() - 1).getResult();
            String argument1QuadrupleResult = this.source.addQuadrupleToList("^", termsQuadruple.getArgument1(), "2", rootQuadruple, false).getResult();
            this.createParentheses(this.source.getExpandedQuadruples().get(this.source.getExpandedQuadruples().size() - 1).getArgument1(), true);
            powerQuadruple = this.source.addQuadrupleToList("*", argument1QuadrupleResult, argument2QuadrupleResult, rootQuadruple, false);
        } else {
            String resultArgument1 = this.getPotentiationTerm(termsQuadruple, "2", rootQuadruple, true);
            powerQuadruple = this.source.addQuadrupleToList("*", (resultArgument1.isEmpty()) ? termsQuadruple.getArgument1() + "^2" : resultArgument1, rootQuadruple.getArgument2(), rootQuadruple, false);
        }
        if (StringUtil.match(termsQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()))
            this.source.addQuadrupleToList("^", termsQuadruple.getArgument1(), "2", powerQuadruple, true);
        this.source.addQuadrupleToList("*", "3", rootQuadruple.getArgument2(), rootQuadruple, false);
        return "cubo do primeiro termo, " + sign + " o triplo do produto do " +
                "quadrado do primeiro termo pelo segundo termo, mais o triplo do produto do primeiro pelo " +
                "quadrado do segundo termo, " + sign + " o cubo do segundo termo.";
    }


    private boolean isThereAMonomy(ExpandedQuadruple expandedQuadruple, boolean isArgument1) {
        String argument = expandedQuadruple.getArgument1();
        if (!isArgument1)
            argument = expandedQuadruple.getArgument2();
        return StringUtil.match(argument, RegexPattern.VARIABLE_WITH_COEFICIENT.toString());
    }

    private String getPotentiationTerm(ExpandedQuadruple valueQuadruple, String exponent, ExpandedQuadruple quadruple, boolean isArgument1) {
        String argument = (isArgument1) ? valueQuadruple.getArgument1() : valueQuadruple.getArgument2();
        if (StringUtil.match(argument, RegexPattern.VARIABLE_WITH_EXPOENT.toString())) {
            NumericValueVariable nvv = new NumericValueVariable();
            nvv.setAttributesFromString(argument);
            nvv.labelExponentSum(Integer.parseInt(exponent));
            if (nvv.getValue() != 1) {
                return this.source.addQuadrupleToList("^", nvv.toString().substring(0, nvv.toString().indexOf('^')),
                        String.valueOf(nvv.getLabelPower()), quadruple, isArgument1).getResult();
            }
            return nvv.getLabel();
        }
        return "";
    }
}
