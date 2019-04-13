package com.asjngroup.deft.security.login.service;

import java.util.List;

import com.asjngroup.deft.common.service.models.ResponseMessageModel;
import com.asjngroup.deft.common.service.models.UserDetailsModel;
import com.asjngroup.deft.security.service.models.UserTblModel;

public interface AuthenticationService
{
	public boolean doLoginAction( ResponseMessageModel errorResponse, List<UserTblModel> userTbls, UserDetailsModel userDetailsModel );

}
