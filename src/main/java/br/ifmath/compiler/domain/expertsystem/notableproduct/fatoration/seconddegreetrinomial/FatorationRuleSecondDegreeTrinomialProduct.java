package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.seconddegreetrinomial;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.Monomial;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.MathOperatorUtil;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class FatorationRuleSecondDegreeTrinomialProduct implements IRule {

    ThreeAddressCode source;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return true;
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> source) {
        List<Step> steps = new ArrayList<>();
        this.source = source.get(0);

        this.transformToProductOfRoots();

        this.source.clearNonUsedQuadruples();
        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(),
                "Identificamos dois elementos &xscr;&apos; e &xscr;&apos;&apos; tal que &xscr;&apos; &plus; " +
                        "&xscr;&apos;&apos; &equals; &minus;(&bscr;&sol;&ascr;) e &xscr;&apos; &middot; &xscr;&apos;&apos; " +
                        "&equals; &cscr;&sol;&ascr; ou utilizando a fórmula de Bháskara e escrevemos o resultado como um produto " +
                        "&ascr; &middot; (&xscr; &minus; &xscr;&apos;) &middot; (&xscr; &minus; &xscr;&apos;&apos;)."));
        return steps;
    }

    private void transformToProductOfRoots() {
        ExpandedQuadruple root = this.source.getRootQuadruple();
        if (StringUtil.match(root.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {

            ExpandedQuadruple innerQuadruple = this.source.findQuadrupleByResult(root.getArgument2());
            int a = this.getVariable(innerQuadruple.getArgument1(), true);
            int b = this.getVariable(innerQuadruple.getArgument1(), false);
            int c = this.getVariable(innerQuadruple.getArgument2(), false);

            String variable = new Monomial(root.getArgument1()).getLiteralVariable();
            root.setArgument1(String.valueOf(a));
            root.setOperator("*");

            Bhaskara bhaskara = new Bhaskara(a, b, c);

            this.source.addQuadrupleToList(bhaskara.getX1Operator(), variable, String.valueOf(bhaskara.getFirstRoot()), innerQuadruple, true).setLevel(1);
            this.source.addQuadrupleToList(bhaskara.getX2Operator(), variable, String.valueOf(bhaskara.getSecondRoot()), innerQuadruple, false).setLevel(1);
            innerQuadruple.setOperator("*");
        }

    }

    private int getVariable(String argument, boolean isA) {
        int parenthesesIndex = (isA) ? argument.indexOf(')') : argument.indexOf('(');
        int divideBarIndex = argument.indexOf('/');
        String variable = (isA) ? argument.substring(divideBarIndex + 1, parenthesesIndex) :
                argument.substring(parenthesesIndex + 1, divideBarIndex);
        return Integer.parseInt(variable);
    }

    public boolean areRootsEqual(ThreeAddressCode source) {
        ExpandedQuadruple root = source.getRootQuadruple();
        if (StringUtil.match(root.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {

            ExpandedQuadruple innerQuadruple = source.findQuadrupleByResult(root.getArgument2());
            int a = this.getVariable(innerQuadruple.getArgument1(), true);
            int b = this.getVariable(innerQuadruple.getArgument1(), false);
            int c = this.getVariable(innerQuadruple.getArgument2(), false);

            Bhaskara bhaskara = new Bhaskara(a, b, c);
            return bhaskara.getFirstRoot().equals(bhaskara.getSecondRoot());
        }
        return false;
    }

    private static class Bhaskara {

        private String x1, x2, x1Operator, x2Operator;

        public Bhaskara(int a, int b, int c) {
            this.calculate(a, b, c);
        }

        public String getFirstRoot() {
            return x1;
        }

        public String getSecondRoot() {
            return x2;
        }

        public String getX1Operator() {
            return x1Operator;
        }

        public String getX2Operator() {
            return x2Operator;
        }

        private void calculate(int a, int b, int c) {
            int delta = (b * b) + (-4 * (a * c));
            int divisor = 2 * a;
            double dividend = -b + Math.sqrt(delta);

            float x1 = (float) (dividend / divisor);
            this.x1Operator = (x1 < 0) ? "+" : "-";

            this.x1 = (x1 % 1 == 0) ?
                    String.valueOf(Math.abs(Math.round(x1))) :
                    this.getReducedFraction(dividend, divisor);

            dividend = -b - Math.sqrt(delta);

            float x2 = (float) (dividend / divisor);
            this.x2Operator = (x2 < 0) ? "+" : "-";
            this.x2 = (x2 % 1 == 0) ?
                    String.valueOf(Math.abs(Math.round(x2))) :
                    this.getReducedFraction(dividend, divisor);

        }

        private String getReducedFraction(double a, double b) {
            double gcd = MathOperatorUtil.gcd(a, b);
            return Math.abs(Math.round(a / gcd)) + "/" + Math.abs(Math.round(b / gcd));
        }

    }
}
