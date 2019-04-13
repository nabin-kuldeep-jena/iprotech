package com.asjngroup.deft.framework.referencedata.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.asjngroup.deft.common.service.models.ResponseMessageModel;

public interface CountryService
{

	ResponseMessageModel getAllCountry( HttpServletRequest request, HttpServletResponse response );

}
