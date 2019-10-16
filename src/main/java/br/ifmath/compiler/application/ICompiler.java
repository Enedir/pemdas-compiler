package br.ifmath.compiler.application;

import br.ifmath.compiler.domain.expertsystem.AnswerType;
import br.ifmath.compiler.domain.expertsystem.IAnswer;
import br.ifmath.compiler.domain.expertsystem.IExpertSystem;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.grammar.InvalidDistributiveOperationException;
import br.ifmath.compiler.domain.grammar.nonterminal.UnrecognizedStructureException;
import br.ifmath.compiler.infrastructure.compiler.UnrecognizedLexemeException;
import br.ifmath.compiler.infrastructure.input.ValueVariable;

import java.util.List;

/**
 *
 * @author alex_
 */
public interface ICompiler {

    public IAnswer analyse(IExpertSystem expertSystem, AnswerType answerType, String... expressions) throws UnrecognizedLexemeException, UnrecognizedStructureException, InvalidAlgebraicExpressionException, InvalidDistributiveOperationException;

    public IAnswer analyse(IExpertSystem expertSystem, AnswerType answerType, List<ValueVariable> variables, String... expressions) throws UnrecognizedLexemeException, UnrecognizedStructureException, InvalidAlgebraicExpressionException, InvalidDistributiveOperationException;

    public void frontEnd(String expression) throws UnrecognizedLexemeException, UnrecognizedStructureException;

    public IAnswer backEnd() throws InvalidAlgebraicExpressionException;

}