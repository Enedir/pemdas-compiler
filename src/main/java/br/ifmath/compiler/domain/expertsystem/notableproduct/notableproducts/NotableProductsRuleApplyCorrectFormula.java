package br.ifmath.compiler.domain.expertsystem.notableproduct.notableproducts;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
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
                        this.createParentheses(firstArgument, this.source.getExpandedQuadruples().get(this.source.getExpandedQuadruples().size() - 1), true);
                        firstArgument = root.getArgument1();
                    }
                    if (isThereAMonomy(leftInnerQuadruple, false)) {
                        this.createParentheses(secondArgument, this.source.getExpandedQuadruples().get(this.source.getExpandedQuadruples().size() - 1), false);
                        String lastQuadrupleResult = this.source.getExpandedQuadruples().get(this.source.getExpandedQuadruples().size() - 1).getResult();
                        if (!root.getArgument2().equals(lastQuadrupleResult)) {
                            ExpandedQuadruple incorrectQuadruple = this.source.findQuadrupleByArgument(lastQuadrupleResult);
                            incorrectQuadruple.setArgument2("");
                            root.setArgument2(lastQuadrupleResult);
                        }
                        secondArgument = root.getArgument2();
                    }

                    rule = this.productOfSumAndDif(root, firstArgument, secondArgument);
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
            argument = this.createParentheses(argument, rootQuadruple, isArgument1).getResult();
            argument = this.source.addQuadrupleToList("^", argument, "2", rootQuadruple, isArgument1).getResult();
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

        this.source.addQuadrupleToList("^", innerQuadruple.getArgument1(), exponent, root, true);
        ExpandedQuadruple lastQuadruple = this.source.getExpandedQuadruples().get(this.source.getExpandedQuadruples().size() - 1);
        if (!StringUtil.match(lastQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()))
            this.createParentheses(lastQuadruple.getArgument1(), lastQuadruple, true);

        return explanation;
    }

    private String twoTermsSquare(ExpandedQuadruple rootQuadruple, ExpandedQuadruple termsQuadruple, String sign) {
        this.source.addQuadrupleToList("^", termsQuadruple.getArgument2(), "2", rootQuadruple, false);
        ExpandedQuadruple lastQuadruple = this.source.getExpandedQuadruples().get(this.source.getExpandedQuadruples().size() - 1);
        this.createParentheses(lastQuadruple.getArgument1(), lastQuadruple, true);
        this.source.addQuadrupleToList("+", termsQuadruple.getArgument2(), rootQuadruple.getArgument2(), rootQuadruple, false);
        this.source.addQuadrupleToList("*", termsQuadruple.getArgument1(), rootQuadruple.getArgument2(), rootQuadruple, false);
        this.source.addQuadrupleToList("*", "2", rootQuadruple.getArgument2(), rootQuadruple, false);
        return "quadrado do primeiro termo, " + sign + " o dobro do produto " +
                "do primeiro pelo segundo termo, mais o quadrado do segundo termo.";
    }

    private ExpandedQuadruple createParentheses(String argument, ExpandedQuadruple quadruple, boolean setOnArgument1) {
        this.source.addQuadrupleToList(argument, "", "", quadruple, setOnArgument1);
        ExpandedQuadruple lastQuadruple = this.source.getExpandedQuadruples().get(this.source.getExpandedQuadruples().size() - 1);
        lastQuadruple.setLevel(1);
        return lastQuadruple;
    }


    private String twoTermsCube(ExpandedQuadruple rootQuadruple, ExpandedQuadruple termsQuadruple, String sign) {
        String lastOperator = (termsQuadruple.isPlus()) ? "+" : "-";
        this.source.addQuadrupleToList("^", termsQuadruple.getArgument2(), "3", rootQuadruple, false);
        ExpandedQuadruple lastQuadruple = this.source.getExpandedQuadruples().get(this.source.getExpandedQuadruples().size() - 1);
        String parenthesesQuadrupleResult = this.createParentheses(lastQuadruple.getArgument1(), lastQuadruple, true).getResult();
        String powerThreeQuadrupleResult = this.source.findQuadrupleByArgument(parenthesesQuadrupleResult).getResult();
        String powerTwoQuadrupleResult = this.source.addQuadrupleToList("^", parenthesesQuadrupleResult, "2", rootQuadruple, false).getResult();
        this.source.addQuadrupleToList(lastOperator, powerTwoQuadrupleResult, powerThreeQuadrupleResult, rootQuadruple, false);
        this.source.addQuadrupleToList("*", termsQuadruple.getArgument1(), rootQuadruple.getArgument2(), rootQuadruple, false);
        this.source.addQuadrupleToList("*", "3", rootQuadruple.getArgument2(), rootQuadruple, false);
        this.source.addQuadrupleToList("+", termsQuadruple.getArgument2(), rootQuadruple.getArgument2(), rootQuadruple, false);
        ExpandedQuadruple powerQuadruple;
        String argument2QuadrupleResult = this.source.getExpandedQuadruples().get(this.source.getExpandedQuadruples().size() - 1).getResult();
        String argument1QuadrupleResult = this.source.addQuadrupleToList("^", termsQuadruple.getArgument1(), "2", rootQuadruple, false).getResult();
        lastQuadruple = this.source.getExpandedQuadruples().get(this.source.getExpandedQuadruples().size() - 1);
        if (!StringUtil.match(lastQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()))
            this.createParentheses(lastQuadruple.getArgument1(), lastQuadruple, true);
        powerQuadruple = this.source.addQuadrupleToList("*", argument1QuadrupleResult, argument2QuadrupleResult, rootQuadruple, false);
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
}
