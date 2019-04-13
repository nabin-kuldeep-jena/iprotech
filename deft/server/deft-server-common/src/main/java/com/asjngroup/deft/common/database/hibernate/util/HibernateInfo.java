package com.asjngroup.deft.common.database.hibernate.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.sql.SQLException;

import javax.sql.DataSource;

import com.asjngroup.deft.common.database.datasource.MySqlDataSource;
import com.asjngroup.deft.common.database.helper.IdGenerator;

public class HibernateInfo
{

	private final SessionFactory sessionFactory;
	private final DataSource dataSource;
	private com.asjngroup.deft.common.database.datasource.DataSource deftDataSource;
	private final IdGenerator idGenerator;
	private static Configuration configuration;

	public HibernateInfo(SessionFactory sessionFactory, DataSource dataSource, IdGenerator idGenerator )
	{
		this.sessionFactory = sessionFactory;
		this.dataSource = dataSource;
		try
		{
			this.deftDataSource=new MySqlDataSource(dataSource);
		}
		catch ( SQLException e )
		{
			e.printStackTrace();
		}
		this.idGenerator = idGenerator;
	}

	public SessionFactory getSessionFactory()
	{
		return sessionFactory;
	}

	public DataSource getDataSource()
	{
		return dataSource;
	}

	public IdGenerator getIdGenerator()
	{
		return idGenerator;
	}

	public Configuration getConfiguration()
	{
		return configuration;
	}
	
	
	//need to removed once we get better solution 
	public static void setConfiguration(Configuration conf)
	{
		configuration=conf;
	}

	public com.asjngroup.deft.common.database.datasource.DataSource getDeftDataSource()
	{
		return deftDataSource;
	}
}
