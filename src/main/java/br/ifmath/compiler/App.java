package br.ifmath.compiler;


import br.ifmath.compiler.application.Compiler;
import br.ifmath.compiler.application.ICompiler;
import br.ifmath.compiler.domain.expertsystem.AnswerType;
import br.ifmath.compiler.domain.expertsystem.IAnswer;
import br.ifmath.compiler.domain.expertsystem.IExpertSystem;
import br.ifmath.compiler.domain.expertsystem.Step;
import br.ifmath.compiler.domain.expertsystem.linearequation.AnswerLinearEquation;
import br.ifmath.compiler.domain.expertsystem.linearequation.LinearEquationExpertSystem;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) {
        String[] expressions = new String[] {
            //"y+2=2y-4",
            "x+3*(-10)=-x",
            //"x+3*(-10x)=-1",
        };
        
        ICompiler compiler = new Compiler();
        IExpertSystem expertSystem = new LinearEquationExpertSystem();
        for (String expression : expressions) {
            System.out.println("===========================================================================================");
            System.out.println(String.format("\nExpressão: %s", expression));
            
            try {
                IAnswer answer = compiler.analyse(expertSystem, AnswerType.BEST, expression);
                
                for (Step step : answer.getSteps()) {
                    System.out.println("NOTAÇÃO MATEMÁTICA......: " + step.getMathExpression());
                    System.out.println("NOTAÇÃO LaTeX...........: " + step.getLatexExpression());
                    System.out.println("JUSTIFICATIVA...........: " + step.getReason());
                    System.out.println();
                }
                
                System.out.println();
                System.out.println("A.....: " + ((AnswerLinearEquation) answer).getA());
                System.out.println("B.....: " + ((AnswerLinearEquation) answer).getB());
                System.out.println("X.....: " + ((AnswerLinearEquation) answer).getX());
                
                System.out.println(String.format("A expressão %s é válida!", expression));
            } catch (Exception e) {
                System.out.println(String.format("A expressão %s é inválida! Mensagem de erro: %s", expression, e.getMessage()));
                e.printStackTrace();
            }
        }
    }
}
