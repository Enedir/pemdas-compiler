package br.ifmath.compiler.application;

import br.ifmath.compiler.domain.expertsystem.AnswerType;
import br.ifmath.compiler.domain.expertsystem.IAnswer;
import br.ifmath.compiler.domain.expertsystem.IExpertSystem;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.expertsystem.polynomial.classes.Monomial;
import br.ifmath.compiler.domain.grammar.nonterminal.UnrecognizedStructureException;
import br.ifmath.compiler.infrastructure.compiler.UnrecognizedLexemeException;

import java.util.List;

/**
 * @author alex_
 */
public interface ICompiler {

    public IAnswer analyse(IExpertSystem expertSystem, AnswerType answerType, String... expressions) throws UnrecognizedLexemeException, UnrecognizedStructureException, InvalidAlgebraicExpressionException;

    public IAnswer analyseNumeric(IExpertSystem expertSystem, AnswerType answerType, List<Monomial> variables, String expressions) throws UnrecognizedLexemeException, UnrecognizedStructureException, InvalidAlgebraicExpressionException;

    public void frontEnd(String expression) throws UnrecognizedLexemeException, UnrecognizedStructureException;

    public IAnswer backEnd() throws InvalidAlgebraicExpressionException;

}