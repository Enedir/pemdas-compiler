package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.twobinomialproduct;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.NumericValueVariable;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class FatorationRuleTwoBinomialProduct implements IRule {

    ThreeAddressCode source;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return true;
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> source) throws InvalidAlgebraicExpressionException {
        List<Step> steps = new ArrayList<>();
        this.source = source.get(0);

        this.transformToProductOfRoots();

        this.source.clearNonUsedQuadruples();
        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(),
                "Identificamos dois elementos x' e x'' tal que x' + x'' = -(b/a) e x' * x'' = c/a ou " +
                        "utilizando a fórmula de Bháskara e escrevemos o resultado como um produto " +
                        "a * (x - x') * (x - x'')."));
        return steps;
    }

    //TODO testar
    private void transformToProductOfRoots() throws InvalidAlgebraicExpressionException {
        ExpandedQuadruple root = this.source.getRootQuadruple();
        String variable = new NumericValueVariable(root.getArgument1()).getLabelVariable();
        int a = this.getVariable(root.getArgument1(), true);
        int b = this.getVariable(root.getArgument1(), false);
        int c = this.getVariable(root.getArgument2(), false);
        root.setArgument1(String.valueOf(a));
        root.setOperator("*");
        if (StringUtil.match(root.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {

            Bhaskara bhaskara = new Bhaskara(a, b, c);
            String x1 = String.valueOf(Math.round(bhaskara.getFirstRoot()));
            String x2 = String.valueOf(Math.round(bhaskara.getSecondRoot()));

            ExpandedQuadruple innerQuadruple = this.source.findQuadrupleByResult(root.getArgument2());
            innerQuadruple.setOperator("*");
            this.source.addQuadrupleToList("-", variable, x1, innerQuadruple, true);
            this.source.addQuadrupleToList("-", variable, x2, innerQuadruple, false);

        }

    }

    private int getVariable(String argument, boolean isA) {
        int parenthesesIndex = (isA) ? argument.indexOf(')') : argument.indexOf('(');
        int divideBarIndex = argument.indexOf('/');
        String variable = (isA) ? argument.substring(divideBarIndex, parenthesesIndex) :
                argument.substring(parenthesesIndex, divideBarIndex);
        return Integer.parseInt(variable);
    }

    private static class Bhaskara {
        private double x1, x2;

        public Bhaskara(int a, int b, int c) throws InvalidAlgebraicExpressionException {
            this.calculate(a, b, c);
        }

        private void calculate(int a, int b, int c) throws InvalidAlgebraicExpressionException {
            int delta = (b * b) + (-4 * (a * c));
            if (delta >= 0) {
                this.x1 = (-b + Math.sqrt(delta)) / 2 * a;
                this.x2 = (-b - Math.sqrt(delta)) / 2 * a;
                return;
            }
            throw new InvalidAlgebraicExpressionException("Delta não possui raiz positiva");
        }

        public double getFirstRoot() {
            return x1;
        }

        public double getSecondRoot() {
            return x2;
        }
    }
}
