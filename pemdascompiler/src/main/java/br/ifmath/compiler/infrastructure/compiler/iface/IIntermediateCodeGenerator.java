package br.ifmath.compiler.infrastructure.compiler.iface;

import br.ifmath.compiler.domain.compiler.Temporary;
import br.ifmath.compiler.domain.compiler.ThreeAddressCode;

/**
 *
 * @author alex_
 */
public interface IIntermediateCodeGenerator {

    public void addNewOperation(String operator, String argument1, String argument2, String result, int position, int level);

    public void addNewOperation(String operator, String argument1, String result, int position, int level);

    public ThreeAddressCode generateCode(String left, String comparison, String right);

    public Temporary getNextTemporary();

    public void clearValues();
}
