package com.asjngroup.deft.security.login.service;

import org.apache.cxf.jaxrs.ext.MessageContext;

import com.asjngroup.deft.common.service.models.ResponseMessageModel;
import com.asjngroup.deft.security.service.models.ChangePassowrdModel;

public interface ChangePasswordService
{
	ResponseMessageModel updatePassword( ChangePassowrdModel changePasswordModel, MessageContext context );
}
