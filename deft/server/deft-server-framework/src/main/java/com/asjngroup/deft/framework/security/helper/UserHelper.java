package com.asjngroup.deft.framework.security.helper;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.asjngroup.deft.common.database.hibernate.references.UserTbl;
import com.asjngroup.deft.common.models.UserSession;
import com.asjngroup.deft.common.util.NCashConstant;

public class UserHelper
{

	public static UserTbl getCurrentUser( HttpServletRequest servletRequest )
	{
		return getCurrentUserSession( servletRequest.getSession() ).getUserTbl();
	}

	public static UserSession getCurrentUserSession( HttpServletRequest httpServletRequest )
	{
		return getCurrentUserSession( httpServletRequest.getSession() );
	}

	public static UserSession getCurrentUserSession( HttpSession httpSession )
	{
		UserSession userSession = ( UserSession ) httpSession.getAttribute( NCashConstant.USER_SESSION );
		return userSession;
	}

	public static List<Integer> getPartitionIdsForCurrentUser( HttpServletRequest request )
	{
		/*UserSession userSession = getCurrentUserSession( context );
		return userSession.getUserPartitionIDs();*/

		List<Integer> partitionIds = new ArrayList<>();
		partitionIds.add( 1 );
		return partitionIds;
	}

}
