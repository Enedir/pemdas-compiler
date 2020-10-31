package br.ifmath.compiler.domain.expertsystem.polynomial.classes;

import br.ifmath.compiler.infrastructure.input.Polynomial;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

public class Monomial extends Polynomial {

    public Monomial() {
    }

    public Monomial(String literal, Integer coefficient) {
        this.literal = literal;
        this.coefficient = coefficient;
    }

    /**
     * Instancia um {@link Monomial} obtendo os atributos através de uma {@link String}.
     *
     * @param argument {@link String} de onde serão extraídos os atributos.
     */
    public Monomial(String argument) {
        this.setAttributesFromString(argument);
    }

    /**
     * Extrai o coeficiente e a parte literal do monômio através de uma {@link String}.
     *
     * @param argument {@link String} de onde será obtido os valores.
     */
    public void setAttributesFromString(String argument) {

        //caso seja uma variável com expoente, independente se for positivo ou negativo
        if (StringUtil.match(argument.replace("-", ""), RegexPattern.VARIABLE_WITH_EXPONENT.toString())) {

            this.coefficient = this.getCoefficientFromString(argument.substring(0, argument.indexOf("^") - 1));
            this.literal = argument.substring(argument.indexOf("^") - 1);
        } else {
            String coefficient = StringUtil.removeNonNumericChars(argument);
            String literal = StringUtil.removeNumericChars(argument);

            //valor negativo
            if (argument.startsWith("-")) {
                coefficient = "-" + coefficient;
                literal = literal.replace("-", "");
            }
            this.coefficient = this.getCoefficientFromString(coefficient);
            this.literal = literal;
        }
    }

    /**
     * Obtém o coeficiente de uma {@link String}, ou seja, o valor numérico.
     *
     * @param param {@link String} a ser obtido o coeficiente.
     * @return valor do coeficiente obtido. Casos especiais:
     * <ul>
     *     <li>Caso não haja um coeficiente: será considerado 1;</li>
     *     <li>Caso não haja um coeficiente, e seja um valor negativo: será considerado -1.</li>
     * </ul>
     */
    private int getCoefficientFromString(String param) {
        if (param.equals(""))
            return 1;
        if (param.equals("-"))
            return -1;
        return Integer.parseInt(param);
    }

    /**
     * Atribui o valor do grau associado a parte literal do monômio.
     *
     * @param newPower valor inteiro que será atribuído ao grau do monômio.
     */
    public void setLiteralDegree(int newPower) {

        //verifica se o literal tem expoente 1 (somente a varíavel x, por exemplo) ou diferente (x^2, por exemplo)
        String alphabeticLiteral = (this.getLiteralDegree() == 1) ? literal : this.literal.substring(0, this.literal.indexOf('^'));

        if (newPower == 0) {
            this.literal = "";
        } else if (newPower == 1) {
            this.literal = alphabeticLiteral;
        } else {
            this.literal = alphabeticLiteral + "^" + newPower;
        }
    }

    /**
     * Obtém o grau (expoente) da parte literal do monômio.
     *
     * @return valor inteiro que representa o grau da parte literal. Caso não haver uma parte literal, será retornado 0.
     */
    public int getLiteralDegree() {
        if (this.literal == null || this.literal.equals("")) {
            return 0;
        }

        String coefficient = StringUtil.removeNonNumericChars(this.literal);
        if (coefficient.equals("")) {
            return 1;
        }
        return Integer.parseInt(coefficient);
    }

    /**
     * Obtém a letra alfabética que representa a parte literal do monômio.
     *
     * @return {@link String} que é a letra do alfabeto da parte literal.
     */
    public String getLiteralVariable() {
        if (literal.equals(""))
            return "";
        return (literal.length() == 1) ? literal : literal.substring(0, literal.indexOf('^'));
    }

    @Override
    public String toString() {
        if ((this.literal == null || this.literal.equals("")) && this.coefficient == null)

            return "";

        else if (this.literal == null || this.literal.equals(""))

            return String.valueOf(this.coefficient);

        else if (this.coefficient == null)

            return this.literal;

        else {
            if (coefficient == 1)
                return literal;
            if (coefficient == -1)
                return "-" + literal;
            return coefficient + literal;
        }
    }
}
