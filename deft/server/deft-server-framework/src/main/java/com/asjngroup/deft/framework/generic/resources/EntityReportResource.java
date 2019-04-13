package com.asjngroup.deft.framework.generic.resources;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.asjngroup.deft.framework.generic.models.EntityModel;
import com.asjngroup.deft.framework.generic.services.EntityService;
import com.asjngroup.deft.framework.generic.services.IEntityService;
import com.asjngroup.deft.framework.security.helper.UserHelper;

@RestController
@RequestMapping( "/genReportEntity" )
public class EntityReportResource
{

	private IEntityService service = new EntityService();

	@RequestMapping( value = "/entityName/{id}", method = RequestMethod.GET, consumes =
	{ "application/json", "application/xml" }, produces =
	{ "application/json", "application/xml" } )
	public List<EntityModel> genericReport( @PathVariable( "entityName" ) String entityName, @RequestParam( "fields" ) String fields, @RequestParam( "aggrFun" ) String aggrFun, HttpServletRequest request, HttpServletResponse response )
	{
		List<Integer> partitions = UserHelper.getPartitionIdsForCurrentUser( request );

		String[] filters = request.getParameterMap().get( "filter" );
		String condition = filters != null ? filters[0] : null;
		Integer pageNum = null;
		Integer pageSize = null;
		Integer paccId = null;

		if ( request.getParameterMap().containsKey( "pageNum" ) && request.getParameterMap().containsKey( "pageSize" ) )
		{
			pageNum = Integer.parseInt( request.getParameterMap().get( "pageNum" )[0] );
			pageSize = Integer.parseInt( request.getParameterMap().get( "pageSize" )[0] );
		}
		if ( request.getParameterMap().containsKey( "paccId" ) )
		{
			paccId = Integer.parseInt( request.getParameterMap().get( "paccId" )[0] );
		}
		List<EntityModel> models = service.queryEntities( entityName, fields, aggrFun, condition, partitions, pageNum, pageSize );

		return models;
	}

}
