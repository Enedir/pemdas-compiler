package br.ifmath.compiler.domain.expertsystem.notableproduct.notableproducts;

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

public class NotableProductsRuleMultiplication implements IRule {

    private ThreeAddressCode source;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return isThereAMultiplication(source.get(0));
    }


    @Override
    public List<Step> handle(List<ThreeAddressCode> source) {
        this.source = source.get(0);


        while (isThereAMultiplication(this.source)) {
            List<String> multiplicationBorder = new ArrayList<>();
            this.retrieveBorderAndValue(multiplicationBorder, this.source.findQuadrupleByResult(this.source.getLeft()), true);
            if (!multiplicationBorder.isEmpty()) {
                int startBorder = 0, endBorder = multiplicationBorder.size() - 1;
                this.multiplyBetweenBorders(multiplicationBorder.get(startBorder), multiplicationBorder.get(endBorder), multiplicationBorder.subList(startBorder + 1, endBorder));
            }
            this.source.clearNonUsedQuadruples();
        }
        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        List<Step> steps = new ArrayList<>();
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(), "Resolvendo as multiplicações."));
        return steps;
    }

    /**
     * Obtém os "limites" para ser feita a multiplicação, ou seja, de que quádrupla até qual quádrupla será realizada
     * a multiplicação
     *
     * @param resultsList        {@link List} de {@link String} que representa os limites e os valores a serem multiplicados,
     *                           na seguinte estrutura:
     *                           <ul>
     *                              <li>Posição 0: Primeiro Limite, onde irá começar a multiplicar;</li>
     *                              <li>Posição 1,2,3,etc: Valores a serem multiplicados;</li>
     *                              <li> Última Posição: Segundo Limite, onde irá parar as multiplicações;</li>
     *                           </ul>
     *                           Ex.:
     *                                       <ul>
     *                                       <li>Posição 0: T7</li>
     *                                       <li>Posição 1: 2</li>
     *                                       <li>Posição 2: x</li>
     *                                       <li>Posição 3: 3</li>
     *                                       <li>Posição 4: T5</li>
     *                                       </ul>
     *                           Assim irá multiplicar os valores 2,x e 3 como se fosse 2 * x * 3, começando da
     *                           quádrupla T7 até a quadrupla T5.
     * @param iterationQuadruple {@link ExpandedQuadruple} quádrupla da iteração atual.
     * @param isStart            {@link Boolean} que indica se é a primeira iteração.
     */
    private void retrieveBorderAndValue(List<String> resultsList, ExpandedQuadruple iterationQuadruple, boolean isStart) {
        /*Essa verificação é necessária para poder identificar quando ele vai inserir a quádrupla na lista (limite), e
         * quando vai inserir os valores entre os limites */

        if (isStart) {
            if (iterationQuadruple.isTimes()) {
                resultsList.add(iterationQuadruple.getResult());
                resultsList.add(iterationQuadruple.getArgument1());
                isStart = false;
            }
            this.retrieveBorderAndValue(resultsList, this.source.findQuadrupleByResult(iterationQuadruple.getArgument2()), isStart);
        } else {

            if (!iterationQuadruple.isTimes()) {
                //coloca os valores entre os limites na lista
                resultsList.add(iterationQuadruple.getArgument1());
                resultsList.add(iterationQuadruple.getResult());
            } else {
                resultsList.add(iterationQuadruple.getArgument1());
                this.retrieveBorderAndValue(resultsList, this.source.findQuadrupleByResult(iterationQuadruple.getArgument2()), false);
            }
        }
    }

    /**
     * Realiza a multiplicação entre os limites definidos.
     *
     * @param startBorder {@link String} limite de onde será começado a multiplicar.
     * @param endBorder   {@link String} limite de onde terminará de multiplicar.
     * @param values      {@link List} de {@link String} contendo os valores a serem multiplicados.
     */
    private void multiplyBetweenBorders(String startBorder, String endBorder, List<String> values) {
        Monomial monomial = new Monomial();
        for (String value : values) {
            boolean isMinus = false;
            if (StringUtil.match(value, RegexPattern.TEMPORARY_VARIABLE.toString())) {
                ExpandedQuadruple innerQuadruple = source.findQuadrupleByResult(value);
                if (innerQuadruple != null) {
                    //Se for uma quádrupla de MINUS com somente um número
                    if (StringUtil.match(innerQuadruple.getArgument1(), RegexPattern.NATURAL_NUMBER.toString()))
                        value = (innerQuadruple.isNegative()) ? "-" + innerQuadruple.getArgument1() : innerQuadruple.getArgument1();

                    //Se for uma quádrupla de MINUS com um monomio
                    if (StringUtil.isVariable(innerQuadruple.getArgument1())) {
                        value = innerQuadruple.getArgument1();
                        isMinus = true;
                    }
                }
            }

            Monomial valueMonomial = new Monomial();
            //Se for um monômio, pega o coeficiente dele para o value
            if (StringUtil.isVariable(value)) {
                valueMonomial.setAttributesFromString(value);
                value = valueMonomial.getCoefficient().toString();
            }

            //Multiplicação entre os coeficientes numéricos
            if (StringUtil.match(value, RegexPattern.INTEGER_NUMBER.toString()))
                monomial.setCoefficient((monomial.getCoefficient() == null) ? Integer.parseInt(value) : monomial.getCoefficient() * Integer.parseInt(value));

            //Ajusta o sinal caso for um valor negativo
            if (isMinus)
                monomial.setCoefficient(monomial.getCoefficient() * -1);

            if (valueMonomial.getLiteral() != null)
                value = valueMonomial.getLiteral();

            //Multiplicação entre os literais
            if (StringUtil.isVariable(value))
                if (monomial.getLiteral() == null)
                    monomial.setLiteral(value);
                else
                    //compareTo é para colocar os literais em ordem alfabética
                    monomial.setLiteral((monomial.getLiteral().compareTo(value) < 0) ? monomial.getLiteral() + value : value + monomial.getLiteral());
        }
        //Ajusta as quádruplas para os produtos das multiplicações

        ExpandedQuadruple startQuadrupleFather = this.source.findQuadrupleByArgument(startBorder);
        if (startQuadrupleFather.getArgument1().equals(startBorder))
            startQuadrupleFather.setArgument1(endBorder);
        else
            startQuadrupleFather.setArgument2(endBorder);


        ExpandedQuadruple endBorderQuadruple = this.source.findQuadrupleByResult(endBorder);

        //Se for um valor negativo
        if (monomial.getCoefficient() < 0) {
            monomial.setCoefficient(monomial.getCoefficient() * -1);

            startQuadrupleFather.setOperator(MathOperatorUtil.signalRule(startQuadrupleFather.getOperator(), "-"));
        }
        endBorderQuadruple.setArgument1(monomial.toString());

    }

    /**
     * Verifica se há alguma operação de multiplicação em alguma quádrupla.
     *
     * @param source {@link ThreeAddressCode} que contém as quádruplas.
     * @return {@code true} caso haja uma operação de multiplicação e {@code false} caso contrário.
     */
    private boolean isThereAMultiplication(ThreeAddressCode source) {
        for (ExpandedQuadruple expandedQuadruple : source.getExpandedQuadruples()) {
            if (expandedQuadruple.isTimes())
                return true;
        }
        return false;
    }


}
