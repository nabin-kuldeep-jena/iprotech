package com.asjngroup.deft.installer.task.propertydefination;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.asjngroup.deft.common.database.datasource.DataSource;
import com.asjngroup.deft.common.database.hibernate.util.HibernateSession;
import com.asjngroup.deft.common.database.schema.Schema;
import com.asjngroup.deft.common.database.schema.SchemaHelper;
import com.asjngroup.deft.common.installer.util.InstallerSessionBuilder;
import com.asjngroup.deft.installer.tasks.AbstractReferenceDataTask;
import com.asjngroup.deft.installer.tasks.InstallerData;

public class UpdatePropertyDefinitions extends AbstractReferenceDataTask
{
	private static Log log = LogFactory.getLog( UpdatePropertyDefinitions.class );

	public void run( InstallerData installerData ) throws Exception
	{
		log.info( "Updating property definitions" );

		DataSource deftDataSource = HibernateSession.getDeftDataSource();
		PropertyDfnDatabaseUpdateComponent comp = new PropertyDfnDatabaseUpdateComponent();
		comp.initialise( HibernateSession.getSessionFactory(), deftDataSource, deftDataSource.getSchema(), this.registeredSourceFiles );
		comp.setNoDeletes( this.noDeletes );

		comp.updateDatabase();

		log.info( "Property definitions updated successfully" );
	}

	public String getDisplayName()
	{
		return "Apply Property Definitions";
	}

	public static void start() throws Exception
	{
		InstallerSessionBuilder.load();
		Schema schema = SchemaHelper.buildSchema("/com/asjngroup/deft/common/database/metadata/deft_server_schema.xml","/com/asjngroup/deft/common/database/metadata/deft_mapping_schema.xml");
		DataSource deftDataSource = HibernateSession.getDeftDataSource();
		deftDataSource.attachSchema( schema );
		InstallerData installerData = new InstallerData( deftDataSource.getSchema(), null, null );
		UpdatePropertyDefinitions propertyDfnDatabaseUpdateComponent=new UpdatePropertyDefinitions();
		propertyDfnDatabaseUpdateComponent.registerSourceFile( "/com/asjngroup/deft/common/database/metadata/propertydfn/deft_property_dfns.xml" );
		propertyDfnDatabaseUpdateComponent.run( installerData );

	}
}