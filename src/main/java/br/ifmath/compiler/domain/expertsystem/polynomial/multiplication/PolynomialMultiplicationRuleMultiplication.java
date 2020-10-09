package br.ifmath.compiler.domain.expertsystem.polynomial.multiplication;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.Monomial;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PolynomialMultiplicationRuleMultiplication implements IRule {

    private ThreeAddressCode source;

    //Essa variavel indica se a multiplicacao entre dois valores eh um resultado negativo ou nao
    private boolean isMinus;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        /*Sempre entrara nesse passo, ja que nao existe a multiplicacao de polinomios
        sem esse passo. Além de que o front-end garante a presenca da operacao de multiplicacao
         */
        return true;
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> source) {
        this.source = source.get(0);

        multiply();
        this.source.clearNonUsedQuadruples();
        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        List<Step> steps = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(),
                "Multiplica-se os coeficientes, considerando a regra dos sinais, " +
                        "e para as variáveis, somam-se os expoentes pela propriedade das potências."));

        return steps;
    }

    /**
     * Realiza a multiplicacao de todas as quadruplas que tenham a operacao de multiplicacao, e adiciona
     * as quadruplas da forma correta
     */
    private void multiply() {
        //usando uma nova lista para nao haver o problema de alterar a lista principal enquanto esta sendo iterada
        List<ExpandedQuadruple> iterationList = new ArrayList<>(this.source.getExpandedQuadruples());
        for (ExpandedQuadruple expandedQuadruple : iterationList) {
            if (expandedQuadruple.isTimes()) {
                this.isMinus = false;
                String argument1 = getCorrectArgument(expandedQuadruple, true);
                String argument2 = getCorrectArgument(expandedQuadruple, false);
                String result = this.multiplyQuadrupleArguments(argument1, argument2);

                //esse caso eh somente quando a resposta eh somente um valor EX.: T4 = 4x^3
                if (expandedQuadruple.getResult().equals(this.source.getLeft())) {
                    expandedQuadruple.setOperator(isMinus ? "MINUS" : "");
                    expandedQuadruple.setArgument1(result);
                    expandedQuadruple.setArgument2("");
                } else {
                    ExpandedQuadruple father = this.source.findQuadrupleByArgument(expandedQuadruple.getResult());
                    //caso a quadrupla da iteracao estaja no argument1 do seu pai
                    if (father.getArgument1().equals(expandedQuadruple.getResult())) {
                        father.setArgument1(result);
                        if (isMinus) {
                            /*se o pai em questao eh a raiz da arvore, entao faz uma quadrupla MINUS, senao muda o
                            operador para "-"
                             */
                            if (father.getResult().equals(this.source.getLeft())) {
                                this.source.addQuadrupleToList("MINUS", father.getArgument1(), "", father, true);
                            } else
                                this.source.findDirectFather(father.getResult()).setOperator("-");
                        }
                    } else {
                        //caso estiver no argument2, podo-se somente mudar o operador para "-"
                        father.setArgument2(result);
                        if (isMinus)
                            father.setOperator("-");
                    }
                }

            }
        }

    }

    /**
     * Obtem o argumento correto, alem de verificar se cada argumento eh uma variavel temporaria
     * bem como se eh uma temporaria com MINUS ou negativa
     *
     * @param expandedQuadruple {@link ExpandedQuadruple} a ser verificado e obtido o argumento
     * @param isArgument1       {@link Boolean} indicando qual argumento a ser obtido. Sendo true - argument1
     *                          e false - argument2
     * @return {@link String} do argumento correto obtido
     */
    private String getCorrectArgument(ExpandedQuadruple expandedQuadruple, boolean isArgument1) {
        String argument;
        if (isArgument1) {
            argument = expandedQuadruple.getArgument1();
            if (StringUtil.match(expandedQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                ExpandedQuadruple innerQuadruple = this.source.findQuadrupleByResult(expandedQuadruple.getArgument1());
                argument = innerQuadruple.getArgument1();
                /*isMinus se torna true, pois se o argumento eh uma variavel temporaria, significa que eh uma
                variavel com operador "MINUS".
                 */
                this.isMinus = true;
            }
        } else {
            argument = expandedQuadruple.getArgument2();
            if (StringUtil.match(expandedQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                ExpandedQuadruple innerQuadruple = this.source.findQuadrupleByResult(expandedQuadruple.getArgument2());
                argument = innerQuadruple.getArgument1();
                this.isMinus = !this.isMinus;
            }
        }
        return argument;
    }

    /**
     * Efetivamente faz a multiplicacao de acordo com as regras de multiplicacao de polinomios de mesma base.
     * Ex.: 2x * 3x^4, entao multiplica-se 2 e 3, e soma-se o expoentede  x (1), eo expoente de x^4 (4),
     * gerando o resultado 6x^5
     *
     * @param quadrupleArgument1 {@link String} que representa o primeiro fator, no caso do exemplo seria 2x
     * @param quadrupleArgument2 {@link String} que representa o segundo fator, no caso do exemplo seria 3x^4
     * @return {@link String} que representao o produto dos fatores, no caso do exemplo seria 6x^5
     */
    private String multiplyQuadrupleArguments(String quadrupleArgument1, String quadrupleArgument2) {

        Monomial argument1 = new Monomial();
        argument1.setAttributesFromString(quadrupleArgument1);

        Monomial argument2 = new Monomial();
        argument2.setAttributesFromString(quadrupleArgument2);

        Monomial product = new Monomial();

        //Essa seria a multiplicacao dos valores da base, no caso do exemplo, 2 * 3
        product.setCoefficient(argument1.getCoefficient() * argument2.getCoefficient());

        //caso seja somente a multiplicacao de numeros
        if (argument1.getLiteral().equals("") && argument2.getLiteral().equals(""))
            product.setLiteral("");
        else if (!argument1.getLiteral().equals("") && !argument2.getLiteral().equals("")) {
            //caso os dois expoentes tenham algum valor
            int power = argument1.getLiteralDegree() + argument2.getLiteralDegree();

            //obtem o valor da incognita, para ficar no formato correto
            String label = StringUtil.removeNonAlphanumericChars(StringUtil.removeNumericChars(argument1.getLiteral()));
            product.setLiteral(label + "^" + power);
        } else {
            //caso seja a multiplicacao de um numero puro, com um fator com expoente
            if (argument1.getLiteral().equals(""))
                product.setLiteral(argument2.getLiteral());
            else
                product.setLiteral(argument1.getLiteral());
        }
        /*caso haja um fator sem valor no expoente, mas tenha uma incognita, como eh o caso do fator 2x do exemplo
        que no caso seria 2x^1
         */
        if (product.getCoefficient() == 1 && !product.getLiteral().equals(""))
            return product.getLiteral();
        return product.toString();
    }

}
