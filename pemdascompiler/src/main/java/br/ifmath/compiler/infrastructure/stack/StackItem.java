package br.ifmath.compiler.infrastructure.stack;

/**
 *
 * @author alex_
 */
public abstract class StackItem implements IStackable {

    private IStackable next;

    @Override
    public IStackable getNext() {
        return next;
    }

    @Override
    public void setNext(IStackable next) {
        this.next = next;
    }


}
