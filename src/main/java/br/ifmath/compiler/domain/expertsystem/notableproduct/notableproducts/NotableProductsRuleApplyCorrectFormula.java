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
        ExpandedQuadruple lastQuadruple = this.source.getExpandedQuadruples().get(this.source.getExpandedQuadruples().size() - 1);
        if (StringUtil.match(lastQuadruple.getOperator(), RegexPattern.VARIABLE_WITH_COEFICIENT.toString())) {
            lastQuadruple.setArgument1(lastQuadruple.getOperator());
            lastQuadruple.setOperator("");
        }
        return steps;
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

                String leftQuadrupleArgument1 = leftInnerQuadruple.getArgument1();
                String leftQuadrupleArgument2 = leftInnerQuadruple.getArgument2();

                if (leftQuadrupleArgument2.equals(rightInnerQuadruple.getArgument2()))
                    rule = this.productOfSumAndDif(root, leftQuadrupleArgument1, leftQuadrupleArgument2);
            }
        }
        return rule;
    }


    private String productOfSumAndDif(ExpandedQuadruple rootQuadruple, String firstTerm, String secondTerm) {
        rootQuadruple.setArgument1(firstTerm + "^2");
        rootQuadruple.setOperator("-");
        rootQuadruple.setArgument2(secondTerm + "^2");
        return "quadrado do primeiro termo, menos o quadrado do segundo termo.";
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
                !StringUtil.match(innerQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString()))
            root.setArgument1(innerQuadruple.getArgument1() + "^" + exponent);
        else {
            this.source.addQuadrupleToList("^", innerQuadruple.getArgument1(), exponent, root, true);
            if (isThereAMonomy(innerQuadruple, true))
                this.createParentheses(this.source.getExpandedQuadruples().get(this.source.getExpandedQuadruples().size() - 1).getArgument1());
        }
        return explanation;
    }

    private String twoTermsSquare(ExpandedQuadruple rootQuadruple, ExpandedQuadruple termsQuadruple, String sign) {
        if (!this.isThereAMonomy(termsQuadruple, false))
            this.source.addQuadrupleToList("+", termsQuadruple.getArgument2(), termsQuadruple.getArgument2() + "^2", rootQuadruple, false);
        else {
            this.source.addQuadrupleToList("^", termsQuadruple.getArgument2(), "2", rootQuadruple, false);
            this.createParentheses(this.source.getExpandedQuadruples().get(this.source.getExpandedQuadruples().size() - 1).getArgument1());
            this.source.addQuadrupleToList("+", termsQuadruple.getArgument2(), rootQuadruple.getArgument2(), rootQuadruple, false);
        }
        this.source.addQuadrupleToList("*", termsQuadruple.getArgument1(), rootQuadruple.getArgument2(), rootQuadruple, false);
        this.source.addQuadrupleToList("*", "2", rootQuadruple.getArgument2(), rootQuadruple, false);
        return "quadrado do primeiro termo, " + sign + " o dobro do produto " +
                "do primeiro pelo segundo termo, mais o quadrado do segundo termo.";
    }

    private void createParentheses(String argument) {
        this.source.addQuadrupleToList(argument, "", "", this.source.getExpandedQuadruples().get(this.source.getExpandedQuadruples().size() - 1), true);
        this.source.getExpandedQuadruples().get(this.source.getExpandedQuadruples().size() - 1).setLevel(1);
    }


    private String twoTermsCube(ExpandedQuadruple rootQuadruple, ExpandedQuadruple termsQuadruple, String sign) {
        String lastOperator = (termsQuadruple.isPlus()) ? "+" : "-";
        this.source.addQuadrupleToList(lastOperator, termsQuadruple.getArgument2() + "^2", termsQuadruple.getArgument2() + "^3", rootQuadruple, false);
        this.source.addQuadrupleToList("*", termsQuadruple.getArgument1(), rootQuadruple.getArgument2(), rootQuadruple, false);
        this.source.addQuadrupleToList("*", "3", rootQuadruple.getArgument2(), rootQuadruple, false);
        this.source.addQuadrupleToList("+", termsQuadruple.getArgument2(), rootQuadruple.getArgument2(), rootQuadruple, false);
        ExpandedQuadruple powerQuadruple = this.source.addQuadrupleToList("*", termsQuadruple.getArgument1() + "^2", rootQuadruple.getArgument2(), rootQuadruple, false);
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
