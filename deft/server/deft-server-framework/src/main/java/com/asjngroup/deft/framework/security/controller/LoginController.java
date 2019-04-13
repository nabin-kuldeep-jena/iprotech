package com.asjngroup.deft.framework.security.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.asjngroup.deft.common.service.models.UserCredentialModel;
import com.asjngroup.deft.framework.service.util.DeftServicesConstant;

public interface LoginController
{
	@RequestMapping( value = DeftServicesConstant.LOGIN_URL_RESOURCE, method = RequestMethod.GET )
	public Object authenticateUser( UserCredentialModel userCredentialModel, HttpServletRequest request, HttpServletResponse response );
}
