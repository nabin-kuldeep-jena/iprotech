package com.asjngroup.deft.common.database.hibernate;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;

import com.asjngroup.deft.common.database.hibernate.util.HibernateInfo;

public class DeftSessionFactoryBean extends LocalSessionFactoryBean
{
	
	
	@Override
	protected SessionFactory buildSessionFactory( LocalSessionFactoryBuilder sfb )
	{
		HibernateInfo.setConfiguration( getConfiguration() );
/*		Map filterParameters = new HashMap();
		filterParameters.put( "partitionIds", StandardBasicTypes.INTEGER );
		FilterDefinition filterDefinition = new FilterDefinition( "partitionFilter", "ptn_id in ( :partitionIds )", filterParameters );

	//	sfb.addFilterDefinition( filterDefinition );
		
		filterParameters = new HashMap();
		filterParameters.put( "deleteFl", YesNoType.INSTANCE );
		filterDefinition = new FilterDefinition( "deletedFilter", "delete_fl = :deleteFl", filterParameters );
*/
	//	sfb.addFilterDefinition( filterDefinition );
		return super.buildSessionFactory( sfb );
	}
	
}
