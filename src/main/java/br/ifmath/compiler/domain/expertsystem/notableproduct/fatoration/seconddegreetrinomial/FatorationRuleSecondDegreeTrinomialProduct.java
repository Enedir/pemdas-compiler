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

    /**
     * Ajusta as quádruplas para ficarem na fórmula de produto das raízes: a * (x - x') * (x - x'').
     */
    private void transformToProductOfRoots() {
        ExpandedQuadruple root = this.source.getRootQuadruple();
        if (StringUtil.match(root.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {

            //acha quádrupla que contém os valores de a, b e c
            ExpandedQuadruple innerQuadruple = this.source.findQuadrupleByResult(root.getArgument2());

            //pega os valores respectivos
            int a = this.getVariable(innerQuadruple.getArgument1(), true);
            int b = this.getVariable(innerQuadruple.getArgument1(), false);
            int c = this.getVariable(innerQuadruple.getArgument2(), false);

            //cria a parte da formula que é a *
            String variable = new Monomial(root.getArgument1()).getLiteralVariable();
            root.setArgument1(String.valueOf(a));
            root.setOperator("*");

            /*é necessário sempre fazer bhaskara, pois soma e produto só é viável de forma manual e ambos
             * dão o mesmo resultados*/
            Bhaskara bhaskara = new Bhaskara(a, b, c);

            //cria a parte da formula que é a (x - x')
            this.source.addQuadrupleToList(bhaskara.getX1Operator(), variable, String.valueOf(bhaskara.getFirstRoot()), innerQuadruple, true)
                    .setLevel(1);

            //cria a parte da formula que é a (x - x'')
            this.source.addQuadrupleToList(bhaskara.getX2Operator(), variable, String.valueOf(bhaskara.getSecondRoot()), innerQuadruple, false)
                    .setLevel(1);

            //coloca a operação de * entre as duas partes acima
            innerQuadruple.setOperator("*");
        }

    }

    /**
     * Encontra os valores de a, b e c.
     *
     * @param argument {@link String} que contém o valor a ser encontrado.
     * @param isA      booleano que vai indicar se é o valor a, para diferenciar quando o {@code argument} se refere
     *                 ao argument1 da quadrupla.
     * @return inteiro que representa o valor de a, b ou c.
     */
    private int getVariable(String argument, boolean isA) {
        //os valores serão encontrados através do parênteses, baseado na fórmula da regra anterior x^2 + (b/a)x + (c/a).

        /*A partir da fórmula é possível ver que o valor de a sempre está diretamente antes de um ")", e os valores
         * de b e c estão diretamente depois de um "(" */
        int parenthesesIndex = (isA) ? argument.indexOf(')') : argument.indexOf('(');


        int divideBarIndex = argument.indexOf('/');

        /*A lógica abaixo é: se for valor a, é obtido o valor que está DEPOIS do "/" e ANTES do ")";
         *  se for b ou c, é obtido o valor que está ANTES do "/" e DEPOIS do "(";
         * */
        String variable = (isA) ? argument.substring(divideBarIndex + 1, parenthesesIndex) :
                argument.substring(parenthesesIndex + 1, divideBarIndex);

        return Integer.parseInt(variable);
    }

    /**
     * Verificação que incida se os valores de x' e x'' são iguais. Isso simboliza que na verdade a regra que
     * deve ser aplicada não é a de trinômio do segundo grau, mas sim a de trinômio quadrado perfeito.
     *
     * @param source {@link ThreeAddressCode} que contém todas as quádruplas.
     * @return true caso as raízes (x' e x'') sejam iguais, simbolizando que deve ser aplicada a regra de trinômio
     * quadrado perfeito. Caso contrário, será retornado false, indicando que o processamento está correto através da
     * regra de trinômio do segundo grau.
     */
    public boolean areRootsEqual(ThreeAddressCode source) {
        ExpandedQuadruple root = source.getRootQuadruple();
        if (StringUtil.match(root.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {

            //obtém os valores de a, b e c
            ExpandedQuadruple innerQuadruple = source.findQuadrupleByResult(root.getArgument2());
            int a = this.getVariable(innerQuadruple.getArgument1(), true);
            int b = this.getVariable(innerQuadruple.getArgument1(), false);
            int c = this.getVariable(innerQuadruple.getArgument2(), false);

            //verifica se as duas raizes são iguais
            Bhaskara bhaskara = new Bhaskara(a, b, c);
            return bhaskara.getFirstRoot().equals(bhaskara.getSecondRoot());
        }
        return false;
    }

    /**
     * Classe que representa a fórmula de bhaskara com seus valores de raízes (x' e x'') com seus respectivos operadores.
     */
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

        /**
         * Realiza o cálculo da fórmula de bhaskara em uma função do segundo grau
         *
         * @param a inteiro que representa o valor de a.
         * @param b inteiro que representa o valor de b.
         * @param c inteiro que representa o valor de c.
         */
        private void calculate(int a, int b, int c) {
            int delta = (b * b) + (-4 * (a * c));
            double dividend = -b + Math.sqrt(delta);
            int divisor = 2 * a;

            //pegando a primeira raiz x'
            float x1 = (float) (dividend / divisor);
            this.x1Operator = (x1 < 0) ? "+" : "-";

            //se for uma raiz exata retornará ela mesma, caso contrário será mantido na forma de fração
            this.x1 = (x1 % 1 == 0) ?
                    String.valueOf(Math.abs(Math.round(x1))) :
                    this.getReducedFraction(dividend, divisor);

            dividend = -b - Math.sqrt(delta);

            //pegando a segunda raiz x''
            float x2 = (float) (dividend / divisor);
            this.x2Operator = (x2 < 0) ? "+" : "-";

            //se for uma raiz exata retornará ela mesma, caso contrário será mantido na forma de fração
            this.x2 = (x2 % 1 == 0) ?
                    String.valueOf(Math.abs(Math.round(x2))) :
                    this.getReducedFraction(dividend, divisor);

        }

        /**
         * Obtém a forma de fração de dois valores.
         *
         * @param numerator   valor que representa o numerador.
         * @param denominator valor que representa o denominador.
         * @return {@link String} que contém a fração na seguinta forma: numerator/denominator
         */
        private String getReducedFraction(double numerator, double denominator) {
            //obtém o maior divisor comum dos dois valores
            double gcd = MathOperatorUtil.gcd(numerator, denominator);

            //retorna a fração já reduzida ao máximo
            return Math.abs(Math.round(numerator / gcd)) + "/" + Math.abs(Math.round(denominator / gcd));
        }

    }
}
