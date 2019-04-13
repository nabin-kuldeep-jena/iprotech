package com.asjngroup.deft.common.database.hibernate;


import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.internal.SessionFactoryImpl;

public class DeftHibernateConfiguration extends Configuration
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog( DeftHibernateConfiguration.class );

	public boolean isCallFromHibernateConfigurationClass = false;

	public DeftHibernateConfiguration()
	{
		HibernatePropertiesHelper.setDecryptedPasswordToProperties( getProperties() );
	}

	public boolean reinitializeProperties()
	{
		Properties props = HibernatePropertiesHelper.getHibernateProperties();

		if ( props == null )
		{
			return false;
		}
		HibernatePropertiesHelper.setDecryptedPasswordToProperties( props );

		getProperties().putAll( props );

		return true;
	}

	public SessionFactory buildSessionFactory() throws HibernateException
	{
		SessionFactory sessionFactory = super.buildSessionFactory();
		if ( this.isCallFromHibernateConfigurationClass )
		{
			return sessionFactory;
		}
		return null/*new SessionFactoryImpl( sessionFactory )*/;
	}
}