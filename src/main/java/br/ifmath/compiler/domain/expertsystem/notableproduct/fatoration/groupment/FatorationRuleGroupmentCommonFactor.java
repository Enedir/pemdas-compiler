package br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.groupment;

import br.ifmath.compiler.domain.compiler.ExpandedQuadruple;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.expertsystem.IRule;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.notableproduct.fatoration.FatorationRuleIdentification;
import br.ifmath.compiler.infrastructure.props.RegexPattern;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class FatorationRuleGroupmentCommonFactor implements IRule {

    ThreeAddressCode source;

    @Override
    public boolean match(List<ThreeAddressCode> source) {
        try {
            return FatorationRuleIdentification.isGroupment(source.get(0));
        } catch (InvalidAlgebraicExpressionException e) {
            return false;
        }
    }

    @Override
    public List<Step> handle(List<ThreeAddressCode> source) throws InvalidAlgebraicExpressionException {
        List<Step> steps = new ArrayList<>();
        this.source = source.get(0);

        this.generateDoubleCommonFactor();

        this.source.clearNonUsedQuadruples();
        ThreeAddressCode step = new ThreeAddressCode(this.source.getLeft(), this.source.getExpandedQuadruples());
        List<ThreeAddressCode> codes = new ArrayList<>();
        codes.add(step);
        steps.add(new Step(codes, step.toLaTeXNotation().trim(), step.toMathNotation().trim(),
                "Colocamos em evidência o elemento que temos em comum nos primeiros termos e somamos ao elemento " +
                        "que possuímos em comum nos últimos termos."));
        return steps;
    }

    /**
     * Gera duas estruturas parecida com o fator em comum.
     *
     * @throws InvalidAlgebraicExpressionException Erro caso não seja possível gerar algum dos dois fatores em comum.
     */
    private void generateDoubleCommonFactor() throws InvalidAlgebraicExpressionException {

        //obtém o número total de argumentos nas quádruplas para poder gerar o casal
        int argumentsCount = FatorationRuleIdentification.argumentsCount(source.getRootQuadruple(), source);

        //retora uma lista no qual a posição 0 estará o primeiro conjuge, e na posição 1 o segundo.
        List<ThreeAddressCode> coupleSources = FatorationRuleIdentification.generateCouple(source, argumentsCount);

        ThreeAddressCode firstSpouse = coupleSources.get(0);
        ThreeAddressCode secondSpouse = coupleSources.get(1);

        String secondSpouseOperation = this.source.findQuadrupleByArgument(secondSpouse.getLeft()).getOperator();

        Couple couple = new Couple(firstSpouse, secondSpouse, secondSpouseOperation);

        //a partir do casal, altera as quadruplas para gerarem a estrutura com os dois fatores em comum
        this.changeQuadruples(couple);

    }

    private void changeQuadruples(Couple couple) {
        //pega a primeira quádrupla e altera para o primeiro fator em comum
        ExpandedQuadruple root = this.source.getRootQuadruple();
        root.setArgument1(couple.getFirstSpouseFactor());
        root.setOperator("*");
        root.setLevel(0);

        //adiciona as quadruplas do primeiro conjuge para o ThreeAddressCode principal
        List<ExpandedQuadruple> firstCouple = couple.getFirstSpouseMultiplier();

        //ajusta caso a primeira quádrupla for "MINUS"
        this.adjustMinusQuadruplePosition(firstCouple);
        ExpandedQuadruple innerQuadruple = this.source.findQuadrupleByResult(root.getArgument2());

        //inverte para garantir que as quádruplas vão apontar corretamente uma para a outra
        Collections.reverse(firstCouple);

        this.addSpouseQuadruplesToSource(firstCouple, innerQuadruple, true);

        //ajusta o operador do segundo conjuge
        innerQuadruple.setOperator(couple.getSecondSpouseOperator());

        innerQuadruple = this.source.findQuadrupleByResult(innerQuadruple.getArgument2());


        //mesma preparação feita antes, agora para o segundo fator em comum
        innerQuadruple.setArgument1(couple.getSecondSpouseFactor());
        innerQuadruple.setOperator("*");
        innerQuadruple.setLevel(0);

        List<ExpandedQuadruple> secondQuadruple = couple.getSecondSpouseMultiplier();
        Collections.reverse(secondQuadruple);

        this.addSpouseQuadruplesToSource(secondQuadruple, innerQuadruple, false);
    }

    /**
     * Ajusta a posição de uma quádrupla com operador "MINUS".
     *
     * @param expandedQuadruples {@link List} de {@link ExpandedQuadruple} a ser verificada para ajustar a quádrupla.
     */
    private void adjustMinusQuadruplePosition(List<ExpandedQuadruple> expandedQuadruples) {
        //encontra a quádrupla com "MINUS"
        ExpandedQuadruple minusQuadruple = null;
        for (ExpandedQuadruple expandedQuadruple : expandedQuadruples) {
            if (expandedQuadruple.isNegative()) {
                minusQuadruple = expandedQuadruple;
                break;
            }
        }

        //remove a quádrupla e insere novamente na lista, somente para deixar ela na última posição
        if (minusQuadruple != null) {
            expandedQuadruples.remove(minusQuadruple);
            expandedQuadruples.add(minusQuadruple);
        }
    }

    /**
     * Adiciona as qúadruplas presentes no {@code spouse} para o {@link ThreeAddressCode} principal.
     *
     * @param spouse           {@link List} de {@link ExpandedQuadruple} que serão inseridas no source.
     * @param innerQuadruple   {@link ExpandedQuadruple} onde será inserida a primeira quadrupla do {@code spouse}.
     * @param isSetOnArgument1 booleano que indica qual o argumento da {@code innerQuadruple}.
     */
    private void addSpouseQuadruplesToSource(List<ExpandedQuadruple> spouse, ExpandedQuadruple innerQuadruple,
                                             boolean isSetOnArgument1) {

        //variável que controla a última quadrupla inserida no source
        String lastInsertQuadruple = null;

        //variável que controla o result da quádrupla que é "MINUS" e que será inserida no source
        String minusQuadrupleResult = null;

        for (ExpandedQuadruple expandedQuadruple : spouse) {

            //Adiciona quádrupla atual no source
            ExpandedQuadruple insertQuadruple = this.source.addQuadrupleToList(expandedQuadruple.getOperator(), expandedQuadruple.getArgument1(),
                    expandedQuadruple.getArgument2(), innerQuadruple, isSetOnArgument1);

            //adiciona a quádrupla de minus para a variável de controle
            if (insertQuadruple.isNegative())
                minusQuadrupleResult = insertQuadruple.getResult();

            //coloca entre parênteses
            insertQuadruple.setLevel(1);

            //ajusta para apontar para a quádrupla MINUS que acabou de ser inserida na lista
            if (StringUtil.match(insertQuadruple.getArgument1(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                insertQuadruple.setArgument1(minusQuadrupleResult);
            }

            //ajusta para apontar para a quádrupla que acabou de ser inserida na lista
            if (StringUtil.match(insertQuadruple.getArgument2(), RegexPattern.TEMPORARY_VARIABLE.toString())) {
                insertQuadruple.setArgument2(lastInsertQuadruple);
            }

            lastInsertQuadruple = insertQuadruple.getResult();
        }


    }
}
