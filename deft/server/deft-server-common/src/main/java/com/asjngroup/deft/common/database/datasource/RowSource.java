package com.asjngroup.deft.common.database.datasource;

public interface RowSource<T>
{
    public boolean next();

    public T get();

    public void beforeFirst();
}