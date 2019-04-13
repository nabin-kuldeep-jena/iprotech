package com.asjngroup.deft.framework.referencedata.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.asjngroup.deft.common.service.models.ResponseMessageModel;
import com.asjngroup.deft.framework.service.util.DeftServicesConstant;

@RequestMapping( value = "/country", method = RequestMethod.GET )
public class CountryController
{
	@Autowired( required = true )
	CountryService countryService;

	@RequestMapping( value = DeftServicesConstant.COUNTRY_URL_RESOURCE, method = RequestMethod.GET )
	public Object fetchAllCountry( HttpServletRequest request, HttpServletResponse response )
	{
		ResponseMessageModel responseMessageModel = countryService.getAllCountry( request, response );
		return responseMessageModel;
	}

	public CountryService getCountryService()
	{
		return countryService;
	}

	public void setCountryService( CountryService countryService )
	{
		this.countryService = countryService;
	}

}
