package br.ifmath.compiler.application;

import br.ifmath.compiler.domain.compiler.ThreeAddressCode;
import br.ifmath.compiler.domain.compiler.Token;
import br.ifmath.compiler.domain.expertsystem.AnswerType;
import br.ifmath.compiler.domain.expertsystem.IAnswer;
import br.ifmath.compiler.domain.expertsystem.IExpertSystem;
import br.ifmath.compiler.domain.expertsystem.InvalidAlgebraicExpressionException;
import br.ifmath.compiler.domain.grammar.GrammarSymbol;
import br.ifmath.compiler.domain.grammar.nonterminal.E;
import br.ifmath.compiler.domain.grammar.nonterminal.UnrecognizedStructureException;
import br.ifmath.compiler.domain.grammar.terminal.Success;
import br.ifmath.compiler.infrastructure.compiler.*;
import br.ifmath.compiler.infrastructure.compiler.iface.IIntermediateCodeGenerator;
import br.ifmath.compiler.infrastructure.compiler.iface.ILexicalAnalyzer;
import br.ifmath.compiler.infrastructure.compiler.iface.ISymbolTable;
import br.ifmath.compiler.infrastructure.compiler.iface.ISyntacticAnalyzer;
import br.ifmath.compiler.infrastructure.input.ValueVariable;
import br.ifmath.compiler.infrastructure.stack.Stack;
import br.ifmath.compiler.infrastructure.stack.exception.StackAddNullItemException;
import br.ifmath.compiler.infrastructure.util.MathOperatorUtil;
import br.ifmath.compiler.infrastructure.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author alex_
 */
public class Compiler implements ICompiler {

    private final Stack<GrammarSymbol> stack;
    private final ISymbolTable symbolTable;
    private final ILexicalAnalyzer lexicalAnalyzer;
    private final ISyntacticAnalyzer syntacticAnalyzer;
    private final IIntermediateCodeGenerator intermediateCodeGenerator;

    private E e;
    private AnswerType answerType;
    private List<ThreeAddressCode> intermediateCodes;
    private IExpertSystem expertSystem;

    public Compiler() {
        this.stack = new Stack<>();
        this.symbolTable = new SymbolTable();
        this.intermediateCodeGenerator = new IntermediateCodeGenerator();
        this.lexicalAnalyzer = new LexicalAnalyzer(this.symbolTable);
        this.syntacticAnalyzer = new SyntacticAnalyzer(this.stack, this.symbolTable, this.intermediateCodeGenerator);
    }

    private void setUp() {
        symbolTable.cleanSymbolTable();
        intermediateCodeGenerator.clearValues();
        stack.clear();

        this.e = new E();

        try {
            stack.push(new Success());
            stack.push(this.e);
        } catch (StackAddNullItemException ex) {
            ex.printStackTrace();
        }
    }

    private void printThreeAddressCode(ThreeAddressCode threeAddressCode) {
        System.out.println("E.param1 = " + threeAddressCode.getLeft());
        System.out.println("E.comparison = " + threeAddressCode.getComparison());
        System.out.println("E.param2 = " + threeAddressCode.getRight());
        System.out.println(threeAddressCode.toString());
    }

    @Override
    public IAnswer analyse(IExpertSystem expertSystem, AnswerType answerType, String... expressions) throws UnrecognizedLexemeException, UnrecognizedStructureException, InvalidAlgebraicExpressionException {
        this.expertSystem = expertSystem;
        this.answerType = answerType;
        this.intermediateCodes = new ArrayList<>();


        for (String expression : expressions) {
            expression = MathOperatorUtil.replaceReducedDistributive(expression);
            setUp();
            frontEnd(expression);
            this.intermediateCodes.add(intermediateCodeGenerator.generateCode(e.getParameter1(), e.getComparison(), e.getParameter2()));
        }

        return backEnd();
    }

    @Override
    public IAnswer analyse(IExpertSystem expertSystem, AnswerType answerType, List<ValueVariable> variables, String... expressions) throws UnrecognizedLexemeException, UnrecognizedStructureException, InvalidAlgebraicExpressionException {
        this.expertSystem = expertSystem;
        this.answerType = answerType;
        this.intermediateCodes = new ArrayList<>();
        this.expertSystem.setVariables(variables);

        for (String expression : expressions) {

            expression = MathOperatorUtil.replaceReducedDistributive(expression);
            setUp();
            frontEnd(expression);
            this.intermediateCodes.add(intermediateCodeGenerator.generateCode(e.getParameter1(), e.getComparison(), e.getParameter2()));
        }

        return backEnd();
    }

    @Override
    public void frontEnd(String expression) throws UnrecognizedLexemeException, UnrecognizedStructureException {
        Token token = null;
        while (StringUtil.isNotEmpty(expression) || !stack.isEmpty()) {
            if (StringUtil.isEmpty(expression)) {
                token = new Token(new Success());
            } else {
                expression = lexicalAnalyzer.identifyNextToken(expression);
                token = lexicalAnalyzer.getNextToken();
            }

            syntacticAnalyzer.analyzeSyntactically(token);
        }

        if (!stack.isEmpty())
            throw new UnrecognizedStructureException(stack.peek(), token);
    }

    @Override
    public IAnswer backEnd() throws InvalidAlgebraicExpressionException {
        switch (this.answerType) {
            case BEST:
                return this.expertSystem.findBestAnswer(intermediateCodes);
            case OPTIONS:
                return this.expertSystem.findPossibleHandles(intermediateCodes);
        }

        return null;//TODO throw exception
    }

}

