package br.ifmath.compiler.infrastructure.stack;

import br.ifmath.compiler.infrastructure.stack.exception.StackAddNullItemException;
import br.ifmath.compiler.infrastructure.stack.exception.StackNullClassException;

/**
 *
 * @author alex_
 *
 * @param <E> generic class to be stacked
 */
public class Stack<E extends IStackable> {

    private E top;

    public Stack() {

    }

    public void push(E item) throws StackAddNullItemException {
        if (item == null) {
            throw new StackAddNullItemException();
        }

        if (top != null) {
            item.setNext(top);
        }

        top = item;
    }

    public void pushAll(E[] items) throws StackAddNullItemException {
        if (items == null) {
            throw new StackAddNullItemException();
        }

        for (E item : items) {
            this.push(item);
        }
    }

    public E peek() {
        return top;
    }

    public E peekFirst(Class<?> clazz) throws StackNullClassException {
        if (clazz == null) {
            throw new StackNullClassException();
        }

        E current = top;

        while(current != null) {
            if (clazz.isInstance(current)) {
                return current;
            }

            current = (E) current.getNext();
        }

        return null;
    }

    public E pop() {
        E currentTop = top;

        if (top != null) {
            top = (E) top.getNext();
            currentTop.setNext(null);
        }

        return currentTop;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public void clear() {
        top = null;
    }

}
