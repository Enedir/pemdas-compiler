package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.seconddegreetrinomial;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.Monomial;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
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
            int x1 = Math.round(bhaskara.getFirstRoot());
            int x2 = Math.round(bhaskara.getSecondRoot());

            String operatorX1 = (x1 < 0) ? "+" : "-";
            String operatorX2 = (x2 < 0) ? "+" : "-";

            this.source.addQuadrupleToList(operatorX1, variable, String.valueOf(Math.abs(x1)), innerQuadruple, true).setLevel(1);
            this.source.addQuadrupleToList(operatorX2, variable, String.valueOf(Math.abs(x2)), innerQuadruple, false).setLevel(1);
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
            int x1 = Math.round(bhaskara.getFirstRoot());
            int x2 = Math.round(bhaskara.getSecondRoot());
            return x1 == x2;
        }
        return false;
    }

    private static class Bhaskara {
        private float x1, x2;

        public Bhaskara(int a, int b, int c) {
            this.calculate(a, b, c);
        }

        private void calculate(int a, int b, int c) {
            int delta = (b * b) + (-4 * (a * c));
            this.x1 = (float) ((-b + Math.sqrt(delta)) / (2 * a));
            this.x2 = (float) ((-b - Math.sqrt(delta)) / (2 * a));
        }

        public float getFirstRoot() {
            return x1;
        }

        public float getSecondRoot() {
            return x2;
        }
    }
}
