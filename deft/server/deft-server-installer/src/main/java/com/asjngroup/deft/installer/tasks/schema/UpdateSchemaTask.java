
package com.asjngroup.deft.installer.tasks.schema;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.asjngroup.deft.common.database.datasource.DataSource;
import com.asjngroup.deft.common.database.datasource.DataSourceException;
import com.asjngroup.deft.common.database.datasource.DataSourceHelper;
import com.asjngroup.deft.common.database.datasource.MySqlDataSource;
import com.asjngroup.deft.common.database.datasource.SyncDatabaseListener;
import com.asjngroup.deft.common.database.hibernate.util.HibernateSession;
import com.asjngroup.deft.common.database.schema.Entity;
import com.asjngroup.deft.common.database.schema.Index;
import com.asjngroup.deft.common.database.schema.Schema;
import com.asjngroup.deft.common.database.schema.SchemaHelper;
import com.asjngroup.deft.common.database.schema.SchemaParseException;
import com.asjngroup.deft.common.database.schema.Table;
import com.asjngroup.deft.common.installer.util.InstallerSessionBuilder;
import com.asjngroup.deft.installer.tasks.InstallerData;
import com.asjngroup.deft.installer.tasks.InstallerTask;
import com.asjngroup.deft.installer.tasks.InstallerTaskException;

public class UpdateSchemaTask implements InstallerTask
{
	private static Log log = LogFactory.getLog( UpdateSchemaTask.class );

	private boolean isSilentInstallation = false;

	private InstallerData installerData;

	private DataSource dataSource;

	public UpdateSchemaTask( InstallerData installerData, DataSource dataSource )
	{
		this.installerData = installerData;
		this.dataSource = dataSource;
	}

	public void execute() throws SchemaTaskException
	{
		log.info( "Updating schema" );
		try
		{
			List<String> tableCreated = new ArrayList<String>();
			// Now we can sychronise each data source with the database
			Schema schema = SchemaHelper.buildSchema( installerData.getReferenceSchema().schemaName,true );
			dataSource.attachSchema( schema );
			syncDatabaseToSchema( schema, dataSource, tableCreated );
		}
		catch ( SchemaParseException e )
		{
			throw new SchemaTaskException( e );
		}
		finally
		{
			dataSource.closeAll();
		}
	}

	private void syncDatabaseToSchema( final Schema schema, final DataSource dataSource, final List<String> tableCreated ) throws SchemaTaskException
	{
		try
		{
			// sync to the database, with callback for confirming table drops
			DataSourceHelper.syncDatabaseToSchema( schema, dataSource, "", "", new SyncDatabaseListener()
			{
				public boolean createTable( Table table )
				{
					Entity objectMapping = schema.findEntityFromTableName( table.database.DatabaseName, table.TableName );

					if ( objectMapping != null )
					{
						tableCreated.add( objectMapping.EntityName );
					}

					return true;
				}

				public boolean updateTable( Table fromTable, Table toTable )
				{
					// Always update tables (even if they are in the ignore list)
					return true;
				}

				public boolean updateIndex( Index fromIndex, Index toIndex )
				{
					return true;
				}

				public boolean dropTable( Table table )
				{
					// Never drop tables in the installer
					return false;
				}
			} );
			dataSource.updateAllNextNumber();
		}
		catch ( DataSourceException e )
		{
			throw new SchemaTaskException( e );
		}
	}

	public void setData( String taskName, InstallerData installerData )
	{

	}

	public void initialise() throws InstallerTaskException
	{

	}

	public boolean isSilentInstallation()
	{
		return isSilentInstallation;
	}

	public void setSilentInstallation( boolean isSilent )
	{
		this.isSilentInstallation = isSilent;

	}

	public void postInstallationTaskInitialise() throws InstallerTaskException
	{

	}

	public void setData( InstallerData istallerData )
	{
		// TODO Auto-generated method stub

	}

	public static void start()
	{
		try
		{
		/*	File file= new File(UpdateSchemaTask.class.getResource("/hibernate.properties").toURI());
			Properties hikariProperties=new Properties();
			hikariProperties.load( new InputStreamReader( new FileInputStream( file ) ) );
			HikariConfig hikariConfig = new HikariConfig( hikariProperties );
			javax.sql.DataSource dataSource = new HikariDataSource( hikariConfig );
			LocalSessionFactoryBean sessionFactory=new NCashSessionFactoryBean();
			sessionFactory.setDataSource( dataSource );
			sessionFactory.setAnnotatedPackages( "com.asjngroup.ncash.common.database.hibernate" );
			HibernateInfo hibernateInfo=new HibernateInfo( (SessionFactory)sessionFactory, dataSource, new MysqlIdGenerator() );
			HibernateSession.initialise(hibernateInfo );*/
			InstallerSessionBuilder.load();
			InstallerData installerData = new InstallerData(new Schema(),null,null);
			installerData.getReferenceSchema().schemaName="/com/asjngroup/deft/common/database/metadata/deft_server_schema.xml";
			new UpdateSchemaTask( installerData, new MySqlDataSource( HibernateSession.getDataSource() ) ).execute();
		}
		catch ( SchemaTaskException | SQLException e )
		{
			e.printStackTrace();
		}
	}
};