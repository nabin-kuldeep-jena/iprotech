package com.asjngroup.deft.common.properties;


import javax.xml.bind.PropertyException;

import com.asjngroup.deft.common.database.hibernate.references.PropertyDfn;
import com.asjngroup.deft.common.database.hibernate.references.PropertyInst;
import com.asjngroup.deft.common.returntypes.ReturnString;

public abstract interface PropertyType
{
	public abstract Class getReturnClass();

	public abstract boolean isSatisfied( PropertyDfn paramPropertyDfn, PropertyInst paramPropertyInst ) throws PropertyException;

	public abstract boolean isValid( PropertyInst paramPropertyInst, PropertyDfn paramPropertyDfn, ReturnString paramReturnString ) throws PropertyException;

	public abstract String getDefaultValue( PropertyDfn paramPropertyDfn ) throws PropertyException;

	public abstract Object getValue( PropertyInst paramPropertyInst ) throws PropertyException;

	public abstract void fixProperty( PropertyInst paramPropertyInst ) throws PropertyException;
}