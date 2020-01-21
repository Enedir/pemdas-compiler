package br.ifmath.compiler.domain.expertsystem.polynomial.numericvalue;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PolynomialRuleParenthesesOperations implements IRule {
    private List<ExpandedQuadruple> expandedQuadruples;
    private List<Step> steps = new ArrayList<>();


    @Override
    public boolean match(List<ThreeAddressCode> source) {
        return isThereOperationsBetweenParentheses(source.get(0).getExpandedQuadruples());
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> source) throws InvalidAlgebraicExpressionException {
        expandedQuadruples = new ArrayList<>();
        expandedQuadruples.addAll(source.get(0).getExpandedQuadruples());

        resolveInnerOperations(source);
        clearNonUsedQuadruples();

        return steps;
    }

    /**
     * Retira as quadruplas que nao estao sendo mais utilizadas
     */
    private void clearNonUsedQuadruples() {
        expandedQuadruples.removeIf(expandedQuadruple -> expandedQuadruple.getLevel() != 0);
    }

    /**
     * Para cada uma das quadruplas, verifica qual esta envolta em parenteses (nivel diferente de 0), e aplica a
     * operacao correta daquela quadrupla, seja essa uma soma, multiplicacao ou potenciacao.
     *
     * @param source lista de {@link ThreeAddressCode} que contem todas as quadruplas.
     */
    private void resolveInnerOperations(List<ThreeAddressCode> source) throws InvalidAlgebraicExpressionException {
        for (int i = 0; i < expandedQuadruples.size() - 1; i++) {
            ExpandedQuadruple expandedQuadruple = expandedQuadruples.get(i);
            if (expandedQuadruple.getLevel() != 0) {
                i += selectCorrectOperation(expandedQuadruple, source);
            }
        }
    }

    /**
     * Verifica qual a operacao de uma certa quadrupla, e realiza a regra de acordo com essa operacao. Além disso,
     * resulta no passo que sera dado pela estrutura de repeticao que esse metodo esta envolto.
     * Ex.: Caso haja (3 + (2 - 1)), serao feitas duas quadruplas: uma para (2 - 1), e outra para (3 + (2 - 1)), entao
     * o passo sera 2. Assim, quando passar a proxima iteracao da repeticao, nao sera analisado novamente uma dessas
     * quadruplas.
     *
     * @param expandedQuadruple {@link ExpandedQuadruple} sendo analisada.
     * @param sources           lista de {@link ThreeAddressCode} contendo todas as quadruplas.
     * @return passo de quantas quadruplas serao puladas pela estrutura de repeticao.
     */
    private int selectCorrectOperation(ExpandedQuadruple expandedQuadruple, List<ThreeAddressCode> sources) throws InvalidAlgebraicExpressionException {
        IRule operation = this.associateRule(expandedQuadruple);

        List<ExpandedQuadruple> sameLevelQuadruples = getQuadruplesInSameLevel(expandedQuadruple, sources);
        String lastQuadResult = sameLevelQuadruples.get(sameLevelQuadruples.size() - 1).getResult();

        List<ThreeAddressCode> operationsSource = new ArrayList<>();
        operationsSource.add(new ThreeAddressCode(lastQuadResult, sameLevelQuadruples));
        List<Step> parenthesesOperation = operation.handle(operationsSource);

        String result = this.getFinalResult(parenthesesOperation);

        /*
         * Adiciona o resultado de operacoes dentro de um parenteses, na posicao correta (argumento 1 ou 2) de sua
         * respectiva quadrupla
         */
        ExpandedQuadruple aux = sources.get(0).findQuadrupleByArgument1(lastQuadResult);

        if (aux == null) {

            aux = sources.get(0).findQuadrupleByArgument2(lastQuadResult);

            if (aux == null) {
                aux = sources.get(0).findQuadrupleByResult(lastQuadResult);
            } else {
                aux.setArgument2(result);
            }

        } else {
            aux.setArgument1(result);
        }

        sources.get(0).getExpandedQuadruples().removeAll(sameLevelQuadruples);

        if (sources.get(0).getExpandedQuadruples().isEmpty()) {
            sources.get(0).getExpandedQuadruples().add(aux);
        }

        this.addStep(parenthesesOperation.get(parenthesesOperation.size() - 1), sources);

        return sameLevelQuadruples.size() - 1;
    }

    /**
     * Dada uma quadrupla, identifica qual a operacao que ela contem (soma, multiplicacao ou potenciacao).
     *
     * @param expandedQuadruple {@link ExpandedQuadruple} a ser analisada.
     * @return {@link IRule} referente a operacao presente na quadrupla.
     */
    private IRule associateRule(ExpandedQuadruple expandedQuadruple) {
        if (expandedQuadruple.isPotentiation()) {
            return new PolynomialRuleNumbersPotentiation();
        } else if (expandedQuadruple.isTimes()) {
            return new PolynomialRuleMultiplyNumbers();
        } else {
            return new PolynomialRuleSumNumbers();
        }
    }

    /**
     * Obtem o resultado numerico de uma lista de {@link Step} que representa o resultado dentro de um parenteses.
     *
     * @param parenthesesOperation lista de {@link Step} a ser analisada.
     * @return {@link String} que representa o valor numerico da operacao.
     */
    private String getFinalResult(List<Step> parenthesesOperation) {
        if (StringUtil.match(parenthesesOperation.get(0).getSource().get(0).getLeft(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
            List<ExpandedQuadruple> innerQuadruples = parenthesesOperation.get(0).getSource().get(0).getExpandedQuadruples();
            return innerQuadruples.get(innerQuadruples.size() - 1).getArgument1();
        }
        return parenthesesOperation.get(0).getMathExpression();
    }

    /**
     * Adiciona um {@link Step} em uma lista de {@link ThreeAddressCode}.
     *
     * @param lastStep {@link Step} a ser adicionado a lista.
     * @param sources  lista de {@link ThreeAddressCode} que contem todos as quadruplas
     */
    private void addStep(Step lastStep, List<ThreeAddressCode> sources) {
        lastStep.setSource(sources);
        lastStep.setLatexExpression(sources.get(0).toLaTeXNotation().replace("*", ".").trim());
        lastStep.setMathExpression(sources.get(0).toMathNotation().replace("*", ".").trim());
        lastStep.setReason(lastStep.getReason().replace(".", " dentro dos parênteses."));
        steps.add(lastStep);
    }

    /**
     * A partir de uma quadrupla, obtem as quadruplas que estao no mesmo nivel e que tem as mesmas operacoes que essa.
     *
     * @param expandedQuadruple {@link ExpandedQuadruple} que sera analisado os niveis a partir dela .
     * @param sources           lista de {@link ThreeAddressCode} que contem todas as  quadruplas.
     * @return lista de {@link ExpandedQuadruple} que contem as quadruplas de mesmo nivel.
     */
    private List<ExpandedQuadruple> getQuadruplesInSameLevel(ExpandedQuadruple expandedQuadruple, List<ThreeAddressCode> sources) {
        List<ExpandedQuadruple> expandedQuadruples = new ArrayList<>();
        for (ExpandedQuadruple sourceQuadruple : sources.get(0).getExpandedQuadruples()) {
            if ((sourceQuadruple.getLevel() == expandedQuadruple.getLevel()) && (sourceQuadruple.getOperator().equals(expandedQuadruple.getOperator()) || (sourceQuadruple.isPlusOrMinus() && expandedQuadruple.isPlusOrMinus()))) {
                expandedQuadruples.add(sourceQuadruple);
            }
        }
        expandedQuadruples.forEach(expandedQuadruple1 -> expandedQuadruple1.setLevel(expandedQuadruple1.getLevel() - 1));
        return expandedQuadruples;
    }


    /**
     * Verifica se há alguma quadrupla envolta em parenteses.
     *
     * @param expandedQuadruples lista de {@link ExpandedQuadruple} que contém todas as quadruplas.
     * @return true caso haja algum parenteses, e false caso contrario.
     */
    private boolean isThereOperationsBetweenParentheses(List<ExpandedQuadruple> expandedQuadruples) {
        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
            if (expandedQuadruple.getLevel() != 0) {
                return true;
            }
        }
        return false;
    }
}
