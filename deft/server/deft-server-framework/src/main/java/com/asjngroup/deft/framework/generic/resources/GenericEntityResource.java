package com.asjngroup.deft.framework.generic.resources;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.asjngroup.deft.common.database.hibernate.util.HibernateSession;
import com.asjngroup.deft.framework.generic.custom.component.CustomizeDataMapper;
import com.asjngroup.deft.framework.generic.custom.component.CustomizeDataMapperFactory;
import com.asjngroup.deft.framework.generic.models.EntityModel;
import com.asjngroup.deft.framework.generic.models.EntityRef;
import com.asjngroup.deft.framework.generic.services.EntityService;
import com.asjngroup.deft.framework.generic.services.IEntityService;
import com.asjngroup.deft.framework.security.helper.UserHelper;

@RestController
@RequestMapping( "/genEntity" )
public class GenericEntityResource
{
	// FIXME: It is recommended that this be autowired, but since the service is created by cxf, there is some problem with wiring
	private IEntityService service = new EntityService();
	public static final String PARTITION_ID = "partitionId";
	
	@Autowired
	private CustomizeDataMapperFactory customizeDataMapperFactory;

	@RequestMapping( value = "/{entityName}", method = RequestMethod.GET, consumes =
	{ "application/json", "application/xml" }, produces =
	{ "application/json", "application/xml" } )
	public List<EntityModel> getEntities( @PathVariable( "entityName" ) String entityName, HttpServletRequest request, HttpServletResponse response )
	{
		boolean isLinkRequired = false;
		isLinkRequired = Boolean.TRUE.equals( request.getParameterMap().get( "isLinkRequired" ) != null ? request.getParameterMap().get( "isLinkRequired" )[0] : false );

		String[] filters = request.getParameterMap().get( "filter" );
		String condition = filters != null ? filters[0] : null;
		Integer pageNum = null;
		Integer pageSize = null;

		if ( request.getParameterMap().containsKey( "pageNum" ) && request.getParameterMap().containsKey( "pageSize" ) )
		{
			pageNum = Integer.parseInt( request.getParameterMap().get( "pageNum" )[0] );
			pageSize = Integer.parseInt( request.getParameterMap().get( "pageSize" )[0] );
		}

		Session session = HibernateSession.openSession();
		List<EntityModel> models = new ArrayList<>();
		try
		{
			List<Integer> partitions = UserHelper.getPartitionIdsForCurrentUser( request );
			CustomizeDataMapper customizeDataMapper = customizeDataMapperFactory.getCustomDataMapper( String.valueOf( request.getParameterMap().get( "cmp" ) ) );
			boolean isLink = Boolean.valueOf( String.valueOf( request.getParameterMap().get( "link" ) ) );
			models = service.getEntities( entityName, condition, partitions, pageNum, pageSize, session, customizeDataMapper, isLink );
			if ( isLinkRequired )
				updateLinks( models, getBaseUri( request.getRequestURI() ) );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			return null;//ResponseBuilder.sendErrorResponse( "Internal Server Error", Status.INTERNAL_SERVER_ERROR, null );
		}
		finally
		{
			HibernateSession.closeSession( session );
		}

		return models;
	}

	@RequestMapping( value = "/{relationEntiy}/{entityName}", method = RequestMethod.GET, consumes =
	{ "application/json", "application/xml" }, produces =
	{ "application/json", "application/xml" } )
	public List<EntityModel> getEntitiesByRelation( @PathVariable( "relationEntiy" ) String relationEntiy, @PathVariable( "entityName" ) String entityName, HttpServletRequest request, HttpServletResponse response )
	{
		String[] filters = request.getParameterMap().get( "filter" );
		String condition = filters != null ? filters[0] : null;

		Integer pageNum = null;
		Integer pageSize = null;

		if ( request.getParameterMap().containsKey( "pageNum" ) && request.getParameterMap().containsKey( "pageSize" ) )
		{
			pageNum = Integer.parseInt( request.getParameterMap().get( "pageNum" )[0] );
			pageSize = Integer.parseInt( request.getParameterMap().get( "pageSize" )[0] );
		}

		Session session = HibernateSession.openSession();
		List<EntityModel> models = new ArrayList<>();
		try
		{
			List<Integer> partitions = UserHelper.getPartitionIdsForCurrentUser( request );
			boolean isLink = Boolean.valueOf( String.valueOf( request.getParameterMap().get( "link" ) ) );
			models = service.getEntities( entityName, relationEntiy, condition, partitions, pageNum, pageSize, session, isLink );
			updateLinks( models, getBaseUri( request.getRequestURI().toString() ) );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			return null;//ResponseBuilder.sendErrorResponse( "Internal Server Error", Status.INTERNAL_SERVER_ERROR, null );
		}
		finally
		{
			HibernateSession.closeSession( session );
		}

		return models;
	}

	@RequestMapping( value = "/{entityName}/{entityId}", method = RequestMethod.GET )
	public List<EntityModel> getEntity( @PathVariable( "entityName" ) String entityName, @PathVariable( "entityId" ) String entityId, HttpServletRequest request )
	{
		Session session = HibernateSession.openSession();
		List<EntityModel> models = new ArrayList<>();
		try
		{
			List<Integer> partitions = UserHelper.getPartitionIdsForCurrentUser( request );
			CustomizeDataMapper customizeDataMapper = customizeDataMapperFactory.getCustomDataMapper( String.valueOf( request.getParameterMap().get( "cmp" ) ) );
			boolean isLink = Boolean.valueOf( String.valueOf( request.getParameterMap().get( "link" ) ) );
			models = service.getEntity( entityName, entityId, partitions, session, customizeDataMapper, isLink );
			updateLinks( models, getBaseUri( request.getRequestURI().toString() ) );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			return null;//ResponseBuilder.sendErrorResponse( "Internal Server Error", Status.INTERNAL_SERVER_ERROR, null );
		}
		finally
		{
			HibernateSession.closeSession( session );
		}

		return models;
	}

	@RequestMapping( value = "/{entityName}", method = RequestMethod.POST )
	public EntityRef createEntity( EntityModel model, @PathVariable( "entityName" ) String entityName, HttpServletRequest request )
	{
		doPartitionChecks( model, request );

		EntityRef ref = service.createEntity( model );
		return ref;
	}

	@RequestMapping( value = "/{entityName}/{entityId}", method = RequestMethod.PUT )
	public EntityRef updateEntity( EntityModel model, @PathVariable( "entityName" ) String entityName, @PathVariable( "entityId" ) String entityId, HttpServletRequest request )
	{
		//FIXME: not sure if this is needed
		model.setId( entityId );

		doPartitionChecks( model, request );
		EntityRef ref = this.service.updateEntity( model );
		return ref;
	}

	@RequestMapping( value = "/{entityName}", method = RequestMethod.PUT )
	public List<EntityRef> updateEntities( EntityModel model, @PathVariable( "entityName" ) String entityName, HttpServletRequest request )
	{
		doPartitionChecks( model, request );
		String[] filters = request.getParameterMap().get( "filter" );
		String condition = filters != null ? filters[0] : null;

		List<EntityRef> refs = this.service.updateEntities( model, condition );
		return refs;
	}

	@RequestMapping( value = "/{entityName}", method = RequestMethod.DELETE )
	public List<EntityRef> deleteEntities( @PathVariable( "entityName" ) String entityName, HttpServletRequest request )
	{
		List<Integer> partitions = UserHelper.getPartitionIdsForCurrentUser( request );

		String[] filters = request.getParameterMap().get( "filter" );
		String condition = filters != null ? filters[0] : null;
		List<EntityRef> refs = this.service.deleteEntities( entityName, condition, partitions );
		return refs;
	}

	@RequestMapping( value = "/{entityName}/{entityId}", method = RequestMethod.DELETE )
	public List<EntityRef> deleteEntity( @PathVariable( "entityName" ) String entityName, @PathVariable( "entityId" ) String entityId, HttpServletRequest request )
	{
		List<Integer> partitions = UserHelper.getPartitionIdsForCurrentUser( request );
		List<EntityRef> refs = this.service.deleteEntity( entityName, entityId, partitions );
		return refs;
	}

	private void doPartitionChecks( EntityModel model, HttpServletRequest request )
	{
		List<Integer> partitions = UserHelper.getPartitionIdsForCurrentUser( request );

		// partition checks
		Integer partition = null;
		if ( model.getProperties().containsKey( PARTITION_ID ) )
		{
			partition = Integer.parseInt( String.valueOf( model.getProperties().get( PARTITION_ID ) ) );
			if ( !partitions.contains( partition ) )
			{
				throw new RuntimeException( "Partition " + partition + " does not belong to user" );
			}
		}
		else
		{
			model.getProperties().put( PARTITION_ID, partitions.get( 0 ) );
		}

		/*if ( partitions.size() > 1 && partition == null )
		{
			//if the user has more than one partition and there is no partition provided in the data, then it is an error.
			throw new RuntimeException( "User has more than one partitions, provide a partition Id" );
		}*/
	}

	private void updateLinks( List<EntityModel> models, String baseUri )
	{
		for ( EntityModel entityModel : models )
		{
			entityModel.prependLink( baseUri );
		}
	}

	/**
	 * 
	 * @param uri
	 * @return
	 */
	public String getBaseUri( String uri )
	{
		return uri.substring( 0, uri.lastIndexOf( "/" ) );
	}

	public IEntityService getService()
	{
		return service;
	}

	public void setService( IEntityService service )
	{
		this.service = service;
	}

	public void setCustomizeDataMapperFactory( CustomizeDataMapperFactory customizeDataMapperFactory )
	{
		this.customizeDataMapperFactory = customizeDataMapperFactory;
	}

}
