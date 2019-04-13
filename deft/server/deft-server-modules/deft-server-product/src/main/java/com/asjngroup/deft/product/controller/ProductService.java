package com.asjngroup.deft.product.controller;

import org.apache.cxf.jaxrs.ext.MessageContext;

import com.asjngroup.deft.common.database.hibernate.references.ProductTbl;

public interface ProductService
{
	public ProductTbl getProduct( String storeId, String itemId, MessageContext context );
}
