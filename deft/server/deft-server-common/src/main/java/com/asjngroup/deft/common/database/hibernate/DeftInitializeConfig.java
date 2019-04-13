package com.asjngroup.deft.common.database.hibernate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import com.asjngroup.deft.common.database.helper.IdGenerator;
import com.asjngroup.deft.common.database.helper.MysqlIdGenerator;
import com.asjngroup.deft.common.database.hibernate.exception.HibernateUtilException;
import com.asjngroup.deft.common.database.hibernate.util.HibernateInfo;
import com.asjngroup.deft.common.database.hibernate.util.HibernateSession;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DeftInitializeConfig
{

	@Bean( destroyMethod = "close" )
	public DataSource dataSource() throws SQLException
	{
		DeftHikariConfig config = new DeftHikariConfig( "/deft-datasource.properties" );
		HikariDataSource dataSource = new HikariDataSource( config );
		return dataSource;
	}

	@Autowired
	@Bean
	public LocalSessionFactoryBean sessionFactory( DataSource dataSource )
	{
		DeftSessionFactoryBean deftSessionFactoryBean = new DeftSessionFactoryBean();
		deftSessionFactoryBean.setDataSource( dataSource );
		Properties props = new Properties();
		final File propFile = new File( "/deft-hibernate.properties" );
		try (final InputStream is = propFile.isFile() ? new FileInputStream( propFile ) : this.getClass().getResourceAsStream( "/deft-hibernate.properties" ))
		{
			if ( is != null )
			{
				props.load( is );
				deftSessionFactoryBean.setHibernateProperties( props );
				deftSessionFactoryBean.setPackagesToScan( props.getProperty( "deft.packageToScan" ).split( ";" ) );
				deftSessionFactoryBean.afterPropertiesSet();
			}
			else
			{
				throw new IllegalArgumentException( "Cannot find property file: deft-hibernate.properties" );
			}
		}
		catch ( IOException io )
		{
			throw new RuntimeException( "Failed to read property file", io );
		}
		return deftSessionFactoryBean;
	}

	@Autowired
	@Bean
	public IdGenerator idGenerator()
	{
		return new MysqlIdGenerator();
	}

	@Autowired
	@Bean
	public HibernateInfo hibernateInfo( SessionFactory sessionFactory, DataSource dataSource, IdGenerator idGenerator )
	{
		return new HibernateInfo( sessionFactory, dataSource, idGenerator );
	}

	@Autowired
	@Bean
	public HibernateSession hibernateSession( HibernateInfo hibInfo ) throws HibernateUtilException
	{
		HibernateSession.initialise( hibInfo );
		return null;
	}

}
