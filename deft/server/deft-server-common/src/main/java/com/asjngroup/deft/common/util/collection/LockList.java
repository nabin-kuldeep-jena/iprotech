package com.asjngroup.deft.common.util.collection;

import java.util.List;
import java.util.concurrent.locks.Lock;

public interface LockList<E> extends List< E >
{
    public Lock readLock();

    public Lock writeLock();
}
