package com.asjngroup.deft.common.io.util;

public abstract interface InputHandler<T, U>
{
	public abstract void process( T paramT, U paramU ) throws Exception;
}