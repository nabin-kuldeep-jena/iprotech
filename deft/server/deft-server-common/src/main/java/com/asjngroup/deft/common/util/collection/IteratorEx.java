package com.asjngroup.deft.common.util.collection;


public interface IteratorEx<T>
{
    public boolean hasNext() throws Exception;

    public T next() throws Exception;
}
