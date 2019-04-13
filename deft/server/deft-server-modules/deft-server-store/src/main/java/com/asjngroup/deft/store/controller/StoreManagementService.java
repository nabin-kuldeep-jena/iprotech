package com.asjngroup.deft.store.controller;

import java.util.List;

import org.apache.cxf.jaxrs.ext.MessageContext;

import com.asjngroup.deft.common.database.hibernate.references.StoreBranch;

public interface StoreManagementService
{

	public List<StoreBranch> getAllPreferedStore( String city, MessageContext context );

}
