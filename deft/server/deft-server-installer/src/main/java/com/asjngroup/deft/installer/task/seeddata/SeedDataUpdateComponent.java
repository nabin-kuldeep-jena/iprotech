package com.asjngroup.deft.installer.task.seeddata;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.dom4j.Document;
import org.dom4j.Element;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;

import com.asjngroup.deft.common.database.datasource.DataSource;
import com.asjngroup.deft.common.database.hibernate.HibernateObject;
import com.asjngroup.deft.common.database.hibernate.references.ApplicationTbl;
import com.asjngroup.deft.common.database.hibernate.references.ApplicationUser;
import com.asjngroup.deft.common.database.hibernate.references.Component;
import com.asjngroup.deft.common.database.hibernate.references.PartitionTbl;
import com.asjngroup.deft.common.database.hibernate.references.PropertyInst;
import com.asjngroup.deft.common.database.hibernate.references.PropertyInstGroup;
import com.asjngroup.deft.common.database.hibernate.references.RoleRefTable;
import com.asjngroup.deft.common.database.hibernate.references.RoleScreen;
import com.asjngroup.deft.common.database.hibernate.references.RoleScreenAction;
import com.asjngroup.deft.common.database.hibernate.references.RoleScreenLabel;
import com.asjngroup.deft.common.database.hibernate.references.ScreenTbl;
import com.asjngroup.deft.common.database.hibernate.references.TableColumn;
import com.asjngroup.deft.common.database.hibernate.references.Toolbar;
import com.asjngroup.deft.common.database.hibernate.references.ToolbarItem;
import com.asjngroup.deft.common.database.hibernate.references.UiActionItem;
import com.asjngroup.deft.common.database.hibernate.references.UserRolePartition;
import com.asjngroup.deft.common.database.hibernate.references.UserTbl;
import com.asjngroup.deft.common.database.hibernate.util.HibernateSession;
import com.asjngroup.deft.common.database.hibernate.util.HibernateUtil;
import com.asjngroup.deft.common.database.schema.Schema;
import com.asjngroup.deft.common.properties.PropertyHelperException;
import com.asjngroup.deft.common.properties.PropertyInstMaintainer;
import com.asjngroup.deft.common.util.StringHelper;
import com.asjngroup.deft.common.util.collection.CollectionHelper;
import com.asjngroup.deft.database.util.StandardDatabaseUpdateComponent;
import com.asjngroup.deft.installer.exception.ConfigurationException;
import com.asjngroup.deft.installer.tasks.InstallerData;

public class SeedDataUpdateComponent extends StandardDatabaseUpdateComponent
{
	private Map<String, Integer> componentMap;
	private Map<String, Integer> tableColumnMap;
	private Map<String, Integer> streamStageMap;
	private Map<String, Integer> partitionMap;
	private Map<String, Integer> uiActionItemMap;
	private Map<String, Integer> screenMap;
	private boolean foundToolbars;
	private InstallerData installerData;
	private boolean allowUpdates;
	private List<String> updateObjectNames;
	private static final String usrNameRoot = "Root";
	private static final String rolNameRoot = "Root";
	private static final String rolPropertyKey = "RoleProperties";
	private static final String usrNameAdmin = "Admin";
	private static final String rolNameAdmin = "Admin";
	private static final String adminRolPropertyKey = "RolAdministratorRole";
	private static final String editAdminRolPropertyKey = "RolEditAdminRoleProperties";
	private static final String appProductName = "DeftAdministrator";
	private static final String commonPartition = "Common";

	public SeedDataUpdateComponent()
	{
		this.partitionMap = new HashMap();
		this.uiActionItemMap = new HashMap();

		this.foundToolbars = false;

		this.installerData = null;

		this.updateObjectNames = new ArrayList();
	}

	public void initialise( SessionFactory sessionFactory, DataSource dataSource, Schema schema, Document doc, InstallerData installerData, List<String> updateObjectNames, boolean allowUpdates ) throws ConfigurationException
	{
		HibernateSession.getIdGenerator().setAllocationSize( HibernateSession.getIdObjectKeyFromClass( RoleScreenAction.class ), 100L );

		this.installerData = installerData;
		this.updateObjectNames = updateObjectNames;
		this.allowUpdates = allowUpdates;

		super.initialise( sessionFactory, dataSource, schema, doc );
	}

	public void updateDatabase() throws ConfigurationException
	{
		if ( this.installerData == null )
		{
			throw new ConfigurationException( "Installer data is not set" );
		}

		setNoDeletes( true );

		setNoUpdates( !( this.allowUpdates ) );

		initialiseComponentMap();
		initialiseTableColumnMap();
		initialiseStreamStageMap();
		initialisePartitionMap();

		standardProcessObjects();

		Session session = null;
		try
		{
			initializeUserChartProperties();
			List applicationUsers = HibernateUtil.getAllObjects( this.sessionFactory, ApplicationUser.class );

			List<Object[]> results = HibernateUtil.query( this.sessionFactory, "select app.id, usr.id from ApplicationTbl app, UserTbl usr where usr.usrName IN ( :UsrNames ) ", "UsrNames", Arrays.asList( new String[]
			{ "Root", "Admin" } ) );

			for ( Object[] row : results )
			{
				Integer appId = ( Integer ) row[0];
				Integer usrId = ( Integer ) row[1];

				if ( HibernateUtil.findInResults( this.sessionFactory, applicationUsers, CollectionHelper.buildMap( new Object[]
				{ "appId", "usrId" }, new Object[]
				{ appId, usrId } ) ).size() == 0 )
				{
					ApplicationUser applicationUser = ( ApplicationUser ) HibernateSession.createObject( ApplicationUser.class );
					applicationUser.setAppId( ( ( Integer ) row[0] ).intValue() );
					applicationUser.setUsrId( ( ( Integer ) row[1] ).intValue() );
					applicationUser.setApuAccessFl( true );
					this.transaction.save( applicationUser );
				}

			}

			List roleScreens = HibernateUtil.getAllObjects( this.sessionFactory, RoleScreen.class );

			results = HibernateUtil.query( this.sessionFactory, "select rol.id, scr.id from RoleTbl rol, ScreenTbl scr where rol.rolName = :RolName", "RolName", "Root" );

			for ( Object[] row : results )
			{
				Integer rolId = ( Integer ) row[0];
				Integer scrId = ( Integer ) row[1];

				if ( HibernateUtil.findInResults( this.sessionFactory, roleScreens, CollectionHelper.buildMap( new Object[]
				{ "rolId", "scrId" }, new Object[]
				{ rolId, scrId } ) ).size() == 0 )
				{
					RoleScreen roleScreen = ( RoleScreen ) HibernateSession.createObject( RoleScreen.class );
					roleScreen.setRolId( ( ( Integer ) row[0] ).intValue() );
					roleScreen.setScrId( ( ( Integer ) row[1] ).intValue() );
					roleScreen.setRscBrowseFl( true );
					roleScreen.setRscEditFl( true );
					roleScreen.setRscNewFl( true );
					roleScreen.setRscRemoveFl( true );
					roleScreen.setRscExportFl( true );

					this.transaction.save( roleScreen );
				}

			}

			List roleScreensAdmin = HibernateUtil.getAllObjects( this.sessionFactory, RoleScreen.class );

			results = HibernateUtil.query( this.sessionFactory, "select rol.id, scr.id from RoleTbl rol, ScreenTbl scr where rol.rolName = :RolName", "RolName", "Admin" );

			for ( Object[] row : results )
			{
				Integer rolId = ( Integer ) row[0];
				Integer scrId = ( Integer ) row[1];

				if ( HibernateUtil.findInResults( this.sessionFactory, roleScreensAdmin, CollectionHelper.buildMap( new Object[]
				{ "rolId", "scrId" }, new Object[]
				{ rolId, scrId } ) ).size() == 0 )
				{
					RoleScreen roleScreen = ( RoleScreen ) HibernateSession.createObject( RoleScreen.class );
					roleScreen.setRolId( ( ( Integer ) row[0] ).intValue() );
					roleScreen.setScrId( ( ( Integer ) row[1] ).intValue() );
					roleScreen.setRscBrowseFl( true );
					roleScreen.setRscEditFl( true );
					roleScreen.setRscNewFl( true );
					roleScreen.setRscRemoveFl( true );
					roleScreen.setRscExportFl( true );

					this.transaction.save( roleScreen );
				}

			}

			List rolRefTables = HibernateUtil.getAllObjects( this.sessionFactory, RoleRefTable.class );

			results = HibernateUtil.query( this.sessionFactory, "select rol.id, rft.id from RoleTbl rol, RefTable rft where rol.rolName = :RolName", "RolName", "Root" );

			for ( Object[] row : results )
			{
				Integer rolId = ( Integer ) row[0];
				Integer rftId = ( Integer ) row[1];

				if ( HibernateUtil.findInResults( this.sessionFactory, rolRefTables, CollectionHelper.buildMap( new Object[]
				{ "rolId", "rftId" }, new Object[]
				{ rolId, rftId } ) ).size() == 0 )
				{
					RoleRefTable roleRefTable = ( RoleRefTable ) HibernateSession.createObject( RoleRefTable.class );
					roleRefTable.setRolId( ( ( Integer ) row[0] ).intValue() );
					roleRefTable.setRftId( ( ( Integer ) row[1] ).intValue() );
					roleRefTable.setRrfReadFl( true );
					roleRefTable.setRrfEditFl( true );
					roleRefTable.setRrfNewFl( true );
					roleRefTable.setRrfRemoveFl( true );
					roleRefTable.setRrfExportFl( true );

					this.transaction.save( roleRefTable );
				}

			}

			List rolRefTablesAdmin = HibernateUtil.getAllObjects( this.sessionFactory, RoleRefTable.class );

			results = HibernateUtil.query( this.sessionFactory, "select rol.id, rft.id from RoleTbl rol, RefTable rft where rol.rolName = :RolName", "RolName", "Admin" );

			for ( Object[] row : results )
			{
				Integer rolId = ( Integer ) row[0];
				Integer rftId = ( Integer ) row[1];

				if ( HibernateUtil.findInResults( this.sessionFactory, rolRefTablesAdmin, CollectionHelper.buildMap( new Object[]
				{ "rolId", "rftId" }, new Object[]
				{ rolId, rftId } ) ).size() == 0 )
				{
					RoleRefTable roleRefTable = ( RoleRefTable ) HibernateSession.createObject( RoleRefTable.class );
					roleRefTable.setRolId( ( ( Integer ) row[0] ).intValue() );
					roleRefTable.setRftId( ( ( Integer ) row[1] ).intValue() );
					roleRefTable.setRrfReadFl( true );
					roleRefTable.setRrfEditFl( true );
					roleRefTable.setRrfNewFl( true );
					roleRefTable.setRrfRemoveFl( true );
					roleRefTable.setRrfExportFl( true );

					this.transaction.save( roleRefTable );
				}

			}

			/*List rolWorkGroups = HibernateUtil.getAllObjects( this.sessionFactory, RoleMonitorWorkGroup.class );

			results = HibernateUtil.query( this.sessionFactory, "select rol.id, mwg.id from RoleTbl rol, MonitorWorkGroup mwg where rol.RolName = :RolName", "RolName", "Root" );

			for ( Object[] row : results )
			{
				Integer rolId = ( Integer ) row[0];
				Integer mwgId = ( Integer ) row[1];

				if ( HibernateUtil.findInResults( this.sessionFactory, rolWorkGroups, CollectionHelper.buildMap( new Object[]
				{ "RolId", "SmwgId" }, new Object[]
				{ rolId, mwgId } ) ).size() == 0 )
				{
					RoleMonitorWorkGroup rolWorkGroup = ( RoleMonitorWorkGroup ) HibernateSession.createObject( RoleMonitorWorkGroup.class );
					rolWorkGroup.setRolId( ( ( Integer ) row[0] ).intValue() );
					rolWorkGroup.setSmwgId( ( ( Integer ) row[1] ).intValue() );

					this.transaction.save( rolWorkGroup );
				}

			}

			List rolWorkGroupsAdmin = HibernateUtil.getAllObjects( this.sessionFactory, RoleMonitorWorkGroup.class );

			results = HibernateUtil.query( this.sessionFactory, "select rol.id, mwg.id from RoleTbl rol, MonitorWorkGroup mwg where rol.RolName = :RolName", "RolName", "Admin" );

			for ( Object[] row : results )
			{
				Integer rolId = ( Integer ) row[0];
				Integer mwgId = ( Integer ) row[1];

				if ( HibernateUtil.findInResults( this.sessionFactory, rolWorkGroupsAdmin, CollectionHelper.buildMap( new Object[]
				{ "RolId", "SmwgId" }, new Object[]
				{ rolId, mwgId } ) ).size() == 0 )
				{
					RoleMonitorWorkGroup rolWorkGroup = ( RoleMonitorWorkGroup ) HibernateSession.createObject( RoleMonitorWorkGroup.class );
					rolWorkGroup.setRolId( ( ( Integer ) row[0] ).intValue() );
					rolWorkGroup.setSmwgId( ( ( Integer ) row[1] ).intValue() );

					this.transaction.save( rolWorkGroup );
				}

			}*/

			saveDatabaseChanges();

			Map<String,Integer> roleScreenActions = HibernateUtil.buildKeyIdMap( this.sessionFactory, RoleScreenAction.class, Arrays.asList( new String[]
			{ "rscId", "sacId" } ) );

			results = HibernateUtil.query( this.sessionFactory, "select rsc.id, sac.id from RoleScreen rsc, ScreenAction sac where rsc.ScrId = sac.screenActionGroup.scrId and rsc.roleTbl.rolName = :RolName", "RolName", "Root" );

			for ( Object[] row : results )
			{
				Integer rscId = ( Integer ) row[0];
				Integer sacId = ( Integer ) row[1];

				if ( !( roleScreenActions.containsKey( rscId + "\t" + sacId ) ) )
				{
					RoleScreenAction roleScreenAction = ( RoleScreenAction ) HibernateSession.createObject( RoleScreenAction.class );
					roleScreenAction.setRscId( ( ( Integer ) row[0] ).intValue() );
					roleScreenAction.setSacId( ( ( Integer ) row[1] ).intValue() );

					this.transaction.save( roleScreenAction );
				}

			}

			Map roleScreenActionsAdmin = HibernateUtil.buildKeyIdMap( this.sessionFactory, RoleScreenAction.class, Arrays.asList( new String[]
			{ "rscId", "sacId" } ) );

			results = HibernateUtil.query( this.sessionFactory, "select rsc.id, sac.id from RoleScreen rsc, ScreenAction sac where rsc.scrId = sac.ScreenActionGroup.scrId and rsc.roleTbl.rolName = :RolName", "RolName", "Admin" );

			for ( Object[] row : results )
			{
				Integer rscId = ( Integer ) row[0];
				Integer sacId = ( Integer ) row[1];

				if ( !( roleScreenActionsAdmin.containsKey( rscId + "\t" + sacId ) ) )
				{
					RoleScreenAction roleScreenAction = ( RoleScreenAction ) HibernateSession.createObject( RoleScreenAction.class );
					roleScreenAction.setRscId( ( ( Integer ) row[0] ).intValue() );
					roleScreenAction.setSacId( ( ( Integer ) row[1] ).intValue() );

					this.transaction.save( roleScreenAction );
				}

			}

			List userRolePartitions = HibernateUtil.getAllObjects( this.sessionFactory, UserRolePartition.class );

			results = HibernateUtil.query( this.sessionFactory, "select usr.id, rol.id, ptn.id from UserTbl usr, RoleTbl rol, PartitionTbl ptn where usr.usrName = :UsrName and rol.rolName = :RolName", new String[]
			{ "usrName", "rolName" }, new Object[]
			{ "Root", "Root" } );

			for ( Object[] row : results )
			{
				Integer usrId = ( Integer ) row[0];
				Integer rolId = ( Integer ) row[1];
				Integer urpPtnId = ( Integer ) row[2];

				if ( urpPtnId.intValue() == 0 )
					continue;
				if ( HibernateUtil.findInResults( this.sessionFactory, userRolePartitions, CollectionHelper.buildMap( new Object[]
				{ "usrId", "rolId", "ptnId" }, new Object[]
				{ usrId, rolId, urpPtnId } ) ).size() == 0 )
				{
					UserRolePartition userRolePartition = ( UserRolePartition ) HibernateSession.createObject( UserRolePartition.class );
					userRolePartition.setUsrId( ( ( Integer ) row[0] ).intValue() );
					userRolePartition.setRolId( ( ( Integer ) row[1] ).intValue() );
					userRolePartition.setUrpPtnId( ( ( Integer ) row[2] ).intValue() );

					this.transaction.save( userRolePartition );
				}

			}

			List roleScreenLabels = HibernateUtil.getAllObjects( this.sessionFactory, RoleScreenLabel.class );

			results = HibernateUtil.query( this.sessionFactory, "select rsc.id, sla.id from RoleScreen rsc, ScreenLabel sla where rsc.scrId = sla.scrId and rsc.roleTbl.rolName = :RolName", "RolName", "Root" );

			for ( Object[] row : results )
			{
				Integer rscId = ( Integer ) row[0];
				Integer slaId = ( Integer ) row[1];

				if ( HibernateUtil.findInResults( this.sessionFactory, roleScreenLabels, CollectionHelper.buildMap( new Object[]
				{ "rscId", "slaId" }, new Object[]
				{ rscId, slaId } ) ).size() == 0 )
				{
					RoleScreenLabel roleScreenLabel = ( RoleScreenLabel ) HibernateSession.createObject( RoleScreenLabel.class );

					roleScreenLabel.setRscId( rscId.intValue() );
					roleScreenLabel.setSlaId( slaId.intValue() );

					this.transaction.save( roleScreenLabel );
				}

			}

			List roleScreenLabelsAdmin = HibernateUtil.getAllObjects( this.sessionFactory, RoleScreenLabel.class );

			results = HibernateUtil.query( this.sessionFactory, "select rsc.id, sla.id from RoleScreen rsc, ScreenLabel sla where rsc.scrId = sla.scrId and rsc.roleTbl.rolName = :RolName", "RolName", "Admin" );

			for ( Object[] row : results )
			{
				Integer rscId = ( Integer ) row[0];
				Integer slaId = ( Integer ) row[1];

				if ( HibernateUtil.findInResults( this.sessionFactory, roleScreenLabelsAdmin, CollectionHelper.buildMap( new Object[]
				{ "rscId", "slaId" }, new Object[]
				{ rscId, slaId } ) ).size() == 0 )
				{
					RoleScreenLabel roleScreenLabel = ( RoleScreenLabel ) HibernateSession.createObject( RoleScreenLabel.class );

					roleScreenLabel.setRscId( rscId.intValue() );
					roleScreenLabel.setSlaId( slaId.intValue() );

					this.transaction.save( roleScreenLabel );
				}
			}
			saveDatabaseChanges();

			String query = "from PropertyInst pri where pri.propertyDfn.propertyDfnGroup.pdgKey = 'RoleProperties' and pri.PigId = ( select rol.PigId from RoleTbl rol where rol.rolName = 'Root' )";
			List<PropertyInst> propertyInsts = HibernateSession.find( query );
			for ( PropertyInst inst : propertyInsts )
			{
				inst.setPriValue( "Y" );
				this.transaction.update( inst );
			}

			List<UserTbl> userTblObjs = HibernateUtil.query( this.sessionFactory, "from UserTbl usr where usr.versionId = 1 and usr.usrName in (:UsrName1, :UsrName2)", new String[]
			{ "UsrName1", "UsrName2" }, new Object[]
			{ "Admin", "Root" } );

			for ( UserTbl user : userTblObjs )
			{
				if ( ( user.getUsrName().equals( "Root" ) ) && ( user.getVersionId() == 1 ) )
				{
					query = "from PropertyInst pri where pri.propertyDfn.prdKey in ('RolAdminRole', 'RolEditAdminRoleProperties') and pri.pigId = ( select rol.pigId from RoleTbl rol where rol.rolName = 'Admin' )";
					propertyInsts = HibernateSession.find( query );
					for ( PropertyInst inst : propertyInsts )
					{
						inst.setPriValue( "Y" );
						this.transaction.update( inst );
					}
				}
			}
			List userRolePartitionsAdmins;
			if ( userTblObjs.size() == 2 )
			{
				results = HibernateUtil.query( this.sessionFactory, "select app.id, usr.id from ApplicationTbl app, UserTbl usr where usr.usrName = :UsrName and app.appProductName = :AppProductName", new String[]
				{ "UsrName", "AppProductName" }, new Object[]
				{ "Admin", "DeftAdministrator" } );

				for ( Object[] row : results )
				{
					Integer appId = ( Integer ) row[0];
					Integer usrId = ( Integer ) row[1];

					if ( HibernateUtil.findInResults( this.sessionFactory, applicationUsers, CollectionHelper.buildMap( new Object[]
					{ "appId", "usrId" }, new Object[]
					{ appId, usrId } ) ).size() == 0 )
					{
						ApplicationUser applicationUser = ( ApplicationUser ) HibernateSession.createObject( ApplicationUser.class );
						applicationUser.setAppId( ( ( Integer ) row[0] ).intValue() );
						applicationUser.setUsrId( ( ( Integer ) row[1] ).intValue() );

						this.transaction.save( applicationUser );
					}

				}

				userRolePartitionsAdmins = HibernateUtil.getAllObjects( this.sessionFactory, UserRolePartition.class );
				results = HibernateUtil.query( this.sessionFactory, "select usr.id, rol.id, ptn.id from UserTbl usr, RoleTbl rol, PartitionTbl ptn where usr.UsrName = :UsrName and rol.RolName = :RolName and ptn.PtnName = :PtnName", new String[]
				{ "UsrName", "RrolName", "ptnName" }, new Object[]
				{ "Admin", "Administrator", "Common" } );

				for ( Object[] row : results )
				{
					Integer usrId = ( Integer ) row[0];
					Integer rolId = ( Integer ) row[1];
					Integer urpPtnId = ( Integer ) row[2];

					if ( HibernateUtil.findInResults( this.sessionFactory, userRolePartitionsAdmins, CollectionHelper.buildMap( new Object[]
					{ "usrId", "rolId", "urpPtnId" }, new Object[]
					{ usrId, rolId, urpPtnId } ) ).size() == 0 )
					{
						UserRolePartition userRolePartition = ( UserRolePartition ) HibernateSession.createObject( UserRolePartition.class );
						userRolePartition.setUsrId( ( ( Integer ) row[0] ).intValue() );
						userRolePartition.setRolId( ( ( Integer ) row[1] ).intValue() );
						userRolePartition.setUrpPtnId( ( ( Integer ) row[2] ).intValue() );

						this.transaction.save( userRolePartition );
					}

				}

			}

			this.transaction.deleteObjects( HibernateUtil.query( this.sessionFactory, "from RoleRefTable rrf where rrf.rftId not in ( select rft.rftId from RefTable rft )" ) );
			this.transaction.deleteObjects( HibernateUtil.query( this.sessionFactory, "from RoleScreen   rsc where rsc.scrId not in ( select scr.scrId from ScreenTbl scr )" ) );
			this.transaction.deleteObjects( HibernateUtil.query( this.sessionFactory, "from RoleScreenAction rsa where rsa.sacId not in ( select sac.sacId from ScreenAction sac ) or rsa.rscId not in ( select rsc.rscId from RoleScreen rsc )" ) );
			this.transaction.deleteObjects( HibernateUtil.query( this.sessionFactory, "from RoleScreenLabel  rsl where rsl.slaId not in ( select sla.slaId from ScreenLabel sla  ) or rsl.rscId not in ( select rsc.rscId from RoleScreen rsc )" ) );

			this.transaction.commit();
		}
		catch ( HibernateException e )
		{
		}
		catch ( PropertyHelperException e )
		{
			e.printStackTrace();
		}
		finally
		{
			if ( ( session != null ) && ( session.isOpen() ) )
			{
				try
				{
					session.close();
				}
				catch ( HibernateException e )
				{
					e.printStackTrace();
				}
			}
		}
	}

	private void initializeUserChartProperties() throws HibernateException, PropertyHelperException
	{
		/*List<UserTbl> users = HibernateUtil.query( this.sessionFactory, "from UserTbl usr where usr.UsrName = :usrAdmin or usr.UsrName = :usrRoot", new String[]
		{ "usrAdmin", "usrRoot" }, new Object[]
		{ "Administrator", "Root" } );
		if ( users.isEmpty() )
		{
			return;
		}
		for ( UserTbl user : users )
		{
			List<PropertyInstGroup> propertyInstGroupList = HibernateUtil.query( this.sessionFactory, "Select ucp.PropertyInstGroup from UserChartProperties ucp where ucp.UserTbl.UsrName = :usrName", "usrName", user.getUsrName() );

			PropertyInstMaintainer maintainer = new PropertyInstMaintainer( this.sessionFactory, this.transaction );
			Map chartDfnPropertiesTypes;
			if ( propertyInstGroupList.isEmpty() )
			{
				chartDfnPropertiesTypes = ChartPropertiesConstant.chartDfnPropertiesTypes;
				for ( String pdgKey : chartDfnPropertiesTypes.keySet() )
				{
					String pigName = ( String ) chartDfnPropertiesTypes.get( pdgKey );
					createPropertyInstGroup( pdgKey, pigName, user, maintainer );
				}
			}
			else if ( this.updateObjectNames.contains( "UserTbl" ) )
			{
				for ( PropertyInstGroup propertyInstGroup : propertyInstGroupList )
				{
					maintainer.resetToDefault( propertyInstGroup );
				}
			}
		}
		PropertyInstMaintainer maintainer;*/
	}

	private void createPropertyInstGroup( String pdgKey, String pigName, UserTbl admin, PropertyInstMaintainer maintainer ) throws PropertyHelperException, HibernateException
	{
		PropertyInstGroup instGroup = maintainer.createPropertyInstGroupObject( pdgKey, pigName );
		this.transaction.save( instGroup );
		//createUserChartProperties( instGroup, admin );
	}

	/*private void createUserChartProperties( PropertyInstGroup instGroup, UserTbl admin ) throws HibernateException
	{
		UserChartProperties userChartProperties = ( UserChartProperties ) HibernateSession.createObject( UserChartPropertiesImpl.class, true );
		userChartProperties.setPigId( instGroup.getId() );
		userChartProperties.setUsrId( admin.getUsrId() );
		userChartProperties.setPartitionId( admin.getPartitionId() );
		this.transaction.save( userChartProperties );
	}*/

	public void extractFromDatabase() throws ConfigurationException
	{
	}

	protected boolean onCopyAttributeToProperty( HibernateObject toObject, Element element, ClassMetadata metadata, Map<String, String> elementAttributes, String attributeName, String attributeValue, Stack<StandardDatabaseUpdateComponent.StackObject> objectIdStack ) throws ConfigurationException
	{
		if ( super.onCopyAttributeToProperty( toObject, element, metadata, elementAttributes, attributeName, attributeValue, objectIdStack ) )
		{
			return true;
		}

		if ( ( attributeName.equals( "scrName" ) ) && ( toObject instanceof ToolbarItem ) )
		{
			Integer appId = null;
			for ( StandardDatabaseUpdateComponent.StackObject stackObject : objectIdStack )
			{
				if ( stackObject.getObjectName().equals( "applicationTbl" ) )
				{
					appId = stackObject.getKeyValue();
					break;
				}
			}

			if ( appId == null )
			{
				throw new ConfigurationException( "Application id is not in the object id stack for looking up Screen id from toolbar" );
			}

			Integer scrId = ( Integer ) this.screenMap.get( attributeValue );
			try
			{
				metadata.setPropertyValue( toObject, "scrId", scrId );
			}
			catch ( HibernateException e )
			{
				throw new ConfigurationException( "Error setting screen id from name %1 found in object type %2", e, new Object[]
				{ attributeValue, toObject.getClass().getName() } );
			}

			return true;
		}
		/*if ( ( toObject instanceof ExtraArgsDfn ) && ( attributeName.equals( "EadType" ) ) )
		{
			return copyParameterDfnType( toObject, attributeName, attributeValue, metadata );
		}
		if ( ( toObject instanceof InterfaceTbl ) && ( attributeName.startsWith( "IfeArg" ) ) && ( attributeName.endsWith( "Extra" ) ) )
		{
			return copyParameterDfnType( toObject, attributeName, attributeValue, metadata );
		}*/
		/*if ( toObject instanceof ActionPinConnection )
		{
			String apcFromTrpName = ( String ) elementAttributes.get( "ApcFromTrpName" );
			String apcFromAcnName = ( String ) elementAttributes.get( "ApcFromAcnName" );
			String apcFromAnpName = ( String ) elementAttributes.get( "ApcFromAnpName" );
			String apcToAnpName = ( String ) elementAttributes.get( "ApcToAnpName" );

			if ( ( apcFromTrpName == null ) && ( apcFromAnpName == null ) )
			{
				throw new ConfigurationException( "Found ActionPinConnection that does not specify ApcFromTrpName or ApcFromAnpName" );
			}

			if ( ( apcFromTrpName != null ) && ( apcFromAnpName != null ) )
			{
				throw new ConfigurationException( "Found ActionPinConnection that specifies both of ApcFromTrpName or ApcFromAnpName" );
			}

			if ( ( apcFromAnpName != null ) && ( apcFromAcnName == null ) )
			{
				throw new ConfigurationException( "Found ActionPinConnection that specifies ApcFromAnpName but does not specify ApcFromAcnName" );
			}

			if ( apcToAnpName == null )
			{
				throw new ConfigurationException( "Found ActionPinConnection that does not specify ApcToAnpName" );
			}

			Integer triggerId = null;
			Integer actionId = null;

			for ( StandardDatabaseUpdateComponent.StackObject stackObject : objectIdStack )
			{
				if ( stackObject.getObjectName().equals( "Action" ) )
				{
					actionId = stackObject.getKeyValue();
				}
				else if ( stackObject.getObjectName().equals( "TriggerTbl" ) )
				{
					triggerId = stackObject.getKeyValue();
				}

			}

			try
			{
				metadata.setPropertyValue( toObject, "ApcToAcnId", actionId );

				Integer toActionPinId = ( Integer ) HibernateUtil.queryExpectOneRow( this.sessionFactory, "select anp.AnpId from ActionPin anp, Action acn where anp.AnpName = :anpName and acn.ActionType.ActId = anp.ActId and acn.AcnId = :acnId", new String[]
				{ "anpName", "acnId" }, new Object[]
				{ apcToAnpName, actionId } );

				if ( toActionPinId == null )
				{
					throw new ConfigurationException( "Could not find ApcToAnpId matching ApcToAnpName " + apcToAnpName );
				}

				metadata.setPropertyValue( toObject, "ApcToAnpId", toActionPinId );

				if ( apcFromAnpName != null )
				{
					Integer fromActionId = ( Integer ) HibernateUtil.queryExpectOneRow( this.sessionFactory, "select acn.AcnId from Action acn where acn.AcnName = :acnName and acn.TrgId = :trgId", new String[]
					{ "acnName", "trgId" }, new Object[]
					{ apcFromAcnName, triggerId } );

					if ( fromActionId == null )
					{
						throw new ConfigurationException( "Could not find ApcFromAcnId matching ApcFromAcnName " + apcFromAcnName );
					}

					metadata.setPropertyValue( toObject, "ApcFromAcnId", fromActionId );

					Integer fromActionPinId = ( Integer ) HibernateUtil.queryExpectOneRow( this.sessionFactory, "select anp.AnpId from ActionPin anp, Action acn where anp.AnpName = :anpName and acn.ActionType.ActId = anp.ActId and acn.AcnId = :acnId", new String[]
					{ "anpName", "acnId" }, new Object[]
					{ apcFromAnpName, fromActionId } );

					if ( fromActionPinId == null )
					{
						throw new ConfigurationException( "Could not find ApcFromApnId matching ApcFromApnName " + apcFromAnpName );
					}

					metadata.setPropertyValue( toObject, "ApcFromAnpId", fromActionPinId );
				}
				else
				{
					Integer fromTriggerPinId = ( Integer ) HibernateUtil.queryExpectOneRow( this.sessionFactory, "select trp.TrpId from TriggerPin trp, TriggerTbl trg where trp.TrpName = :trpName and trg.TriggerType.TrtId = trp.TrtId and trg.TrgId = :trgId", new String[]
					{ "trpName", "trgId" }, new Object[]
					{ apcFromTrpName, triggerId } );

					if ( fromTriggerPinId == null )
					{
						throw new ConfigurationException( "Could not find ApcFromTrpId matching ApcFromTrpName " + apcFromTrpName );
					}

					metadata.setPropertyValue( toObject, "ApcFromTrpId", fromTriggerPinId );

					metadata.setPropertyValue( toObject, "ApcFromTrgId", triggerId );
				}

			}
			catch ( HibernateException e )
			{
				throw new ConfigurationException( e );
			}

			elementAttributes.clear();

			return true;
		}*/
		if ( ( !( toObject instanceof TableColumn ) ) && ( attributeName.endsWith( "tclName" ) ) )
		{
			String prefix = StringHelper.removeCamelCaseSuffix( attributeName, 2 );

			Object tableColumnId = null;
			if ( attributeValue.length() > 0 )
			{
				if ( prefix.equals( "partition" ) )
				{
					tableColumnId = this.tableColumnMap.get( ( ( String ) elementAttributes.get( "tbdName" ) ) + "\t" + attributeValue );
				}
				else
				{
					if ( !( elementAttributes.containsKey( prefix + "tbdName" ) ) )
					{
						throw new ConfigurationException( prefix + "tbdName not specified for table column name %1 in object type %2", new Object[]
						{ attributeValue, toObject.getClass().getName() } );
					}

					tableColumnId = this.tableColumnMap.get( ( ( String ) elementAttributes.get( new StringBuilder().append( prefix ).append( "tbdName" ).toString() ) ) + "\t" + attributeValue );
				}

				if ( tableColumnId == null )
				{
					throw new ConfigurationException( "Unknown component name %1 found in attribute %2 of object type %3", new Object[]
					{ attributeValue, attributeName, toObject.getClass().getName() } );
				}

			}

			try
			{
				metadata.setPropertyValue( toObject, prefix + "tclId", tableColumnId );
			}
			catch ( HibernateException e )
			{
				throw new ConfigurationException( "Error setting " + prefix + " table column id from name %1 found in object type %2", e, new Object[]
				{ attributeValue, toObject.getClass().getName() } );
			}

			if ( !( prefix.equals( "partition" ) ) )
			{
				elementAttributes.remove( prefix + "tbdName" );
			}

			return true;
		}
		/*if ( ( !( toObject instanceof StreamStage ) ) && ( attributeName.endsWith( "StsName" ) ) )
		{
			String prefix = StringHelper.removeCamelCaseSuffix( attributeName, 2 );

			Object streamStageId = null;
			if ( attributeValue.length() > 0 )
			{
				if ( !( elementAttributes.containsKey( prefix + "StmName" ) ) )
				{
					throw new ConfigurationException( prefix + "StmName not specified for stream stage name %1 in object type %2", new Object[]
					{ attributeValue, toObject.getClass().getName() } );
				}

				streamStageId = this.streamStageMap.get( ( ( String ) elementAttributes.get( new StringBuilder().append( prefix ).append( "StmName" ).toString() ) ) + "\t" + attributeValue );

				if ( streamStageId == null )
				{
					throw new ConfigurationException( "Unknown component name %1 found in attribute %2 of object type %3", new Object[]
					{ attributeValue, attributeName, toObject.getClass().getName() } );
				}

			}

			try
			{
				metadata.setPropertyValue( toObject, prefix + "StsId", streamStageId );
			}
			catch ( HibernateException e )
			{
				throw new ConfigurationException( "Error setting " + prefix + " stream stage id from name %1 found in object type %2", e, new Object[]
				{ attributeValue, toObject.getClass().getName() } );
			}

			elementAttributes.remove( prefix + "StmName" );

			return true;
		}*/
		if ( ( !( toObject instanceof PartitionTbl ) ) && ( attributeName.endsWith( "ptnName" ) ) )
		{
			toObject.setPartitionId( ( ( Integer ) this.partitionMap.get( attributeValue ) ).intValue() );

			return true;
		}
		if ( toObject instanceof UiActionItem )
		{
			return handleUiActionItem( toObject, attributeName, attributeValue );
		}
		/*if ( ( toObject instanceof DataSourcePerspective ) && ( attributeName.endsWith( "DprPdgKey" ) ) )
		{
			( ( DataSourcePerspectiveImpl ) toObject ).setDprPdgKey( attributeValue );

			return true;
		}
		if ( ( toObject instanceof ChartSubTypeProp ) && ( attributeName.endsWith( "CspPdgKey" ) ) )
		{
			( ( ChartSubTypePropImpl ) toObject ).setCspPdgKey( attributeValue );

			return true;
		}*/

		return false;
	}

	private boolean handleUiActionItem( HibernateObject toObject, String attributeName, String attributeValue ) throws ConfigurationException
	{
		if ( attributeName.equalsIgnoreCase( "uaiCode" ) )
		{
			this.uiActionItemMap.put( attributeValue, Integer.valueOf( toObject.getId() ) );
			return false;
		}
		if ( attributeName.endsWith( "parentActionUaiCode" ) )
		{
			Integer id = ( Integer ) this.uiActionItemMap.get( attributeValue );
			if ( id == null )
				throw new ConfigurationException( "No matching UiActionItem found for the UaiCode", new Object[]
				{ attributeValue } );
			( ( UiActionItem ) toObject ).setUaiParentActionUaiId( id );
			return true;
		}
		if ( attributeName.endsWith( "uaiParentUaiCode" ) )
		{
			Integer id = ( Integer ) this.uiActionItemMap.get( attributeValue );
			if ( id == null )
				throw new ConfigurationException( "No matching UiActionItem found for the UaiCode", new Object[]
				{ attributeValue } );
			( ( UiActionItem ) toObject ).setUaiParentUaiId( ( Integer ) this.uiActionItemMap.get( attributeValue ) );
			return true;
		}
		return false;
	}

	private boolean copyParameterDfnType( Object toObject, String attributeName, String attributeValue, ClassMetadata metadata ) throws ConfigurationException
	{/*
		if ( ( attributeValue.equals( "string" ) ) || ( attributeValue.equals( "int" ) ) || ( attributeValue.equals( "long" ) ) || ( attributeValue.equals( "bool" ) ) || ( attributeValue.equals( "date" ) ) || ( attributeValue.equals( "datetime" ) ) || ( attributeValue.equals( "decimal" ) ) || ( attributeValue.equals( "password" ) ) )
		{
			return false;
		}

		try
		{
			List results = HibernateSession.query( " from ParameterDfn pmd where pmd.PmdName = :PmdName ", "PmdName", attributeValue );

			if ( results.size() != 1 )
			{
				throw new ConfigurationException( "ParameterDfn lookup for %1 returned %2 rows. 1 expected.", new Object[]
				{ attributeValue, Integer.valueOf( results.size() ) } );
			}
			ParameterDfn pmdObj = ( ParameterDfn ) results.get( 0 );

			metadata.setPropertyValue( toObject, attributeName, Integer.toString( pmdObj.getPmdId() ) );
		}
		catch ( HibernateException e )
		{
			throw new ConfigurationException( e );
		}

*/		return true;
	}

	protected boolean checkProcessBlock( String blockName, boolean isRoot ) throws ConfigurationException
	{
		if ( ( isRoot ) && ( this.updateObjectNames.contains( blockName ) ) )
		{
			return true;
		}

		if ( !( isRoot ) )
		{
			return ( !( blockName.equals( "propertyInstGroups" ) ) );
		}

		return false;
	}

	protected void onEndTopLevelElement( Element element ) throws ConfigurationException
	{
		if ( element.getName().equals( "tableDfns" ) )
		{
			initialiseTableColumnMap();
		}

		if ( element.getName().equals( "streams" ) )
		{
			initialiseStreamStageMap();
		}

		if ( element.getName().equals( "partitionTbls" ) )
		{
			initialisePartitionMap();
		}

		if ( !( element.getName().equals( "uiActionItems" ) ) )
			return;
	}

	protected void onBeginElement( Class clazz, Element element ) throws ConfigurationException
	{
		if ( ApplicationTbl.class.isAssignableFrom( clazz ) )
		{
			this.foundToolbars = false;
		}
		else if ( ( Toolbar.class.isAssignableFrom( clazz ) ) && ( !( this.foundToolbars ) ) )
		{
			this.foundToolbars = true;

			saveDatabaseChanges();
			try
			{
				this.screenMap = HibernateUtil.buildKeyIdMap( this.sessionFactory, ScreenTbl.class, "scrName" );
			}
			catch ( HibernateException e )
			{
				throw new ConfigurationException( e );
			}
		}
		else
		{
			/*if ( !( ActionPinConnection.class.isAssignableFrom( clazz ) ) )
			{
				return;
			}*/
			saveDatabaseChanges();
		}
	}

	protected Object onGetSearchKeyValue( Element element, String searchKey, Stack<StandardDatabaseUpdateComponent.StackObject> objectIdStack ) throws ConfigurationException
	{
		if ( ( element.getName().equals( "frequencyGroupComponent" ) ) && ( searchKey.equals( "cmpId" ) ) )
		{
			String cptTypeCd = element.attribute( "cptTypeCd" ).getValue();
			String cmpName = element.attribute( "cmpName" ).getValue();
			return this.componentMap.get( cptTypeCd + "\t" + cmpName );
		}
		if ( ( element.getName().equals( "eventProcessor" ) ) && ( searchKey.equals( "cmpId" ) ) )
		{
			String cptTypeCd = element.attribute( "cptTypeCd" ).getValue();
			String cmpName = element.attribute( "cmpName" ).getValue();
			return this.componentMap.get( cptTypeCd + "\t" + cmpName );
		}
		if ( ( element.getName().equals( "aggregationProcessor" ) ) && ( searchKey.equals( "cmpId" ) ) )
		{
			String cptTypeCd = element.attribute( "cptTypeCd" ).getValue();
			String cmpName = element.attribute( "cmpName" ).getValue();
			return this.componentMap.get( cptTypeCd + "\t" + cmpName );
		}
		if ( ( element.getName().equals( "actionPinConnection" ) ) && ( searchKey.equals( "apcToAnpId" ) ) )
		{
			String apcToAnpName = element.attributeValue( "apcToAnpName" );

			if ( apcToAnpName == null )
			{
				throw new ConfigurationException( "Found ActionPinConnection that does not specify ApcToAnpName" );
			}

			Integer actionId = null;

			for ( StandardDatabaseUpdateComponent.StackObject stackObject : objectIdStack )
			{
				if ( stackObject.getObjectName().equals( "Action" ) )
				{
					actionId = stackObject.getKeyValue();
				}
			}

			String actName = element.getParent().getParent().attributeValue( "actName" );

			Integer toActionPinId = null;
			try
			{
				toActionPinId = ( Integer ) HibernateUtil.queryExpectOneRow( this.sessionFactory, "select anp.AnpId from ActionPin anp where anp.AnpName = :anpName and anp.ActionType.ActName = :actName", new String[]
				{ "anpName", "actName" }, new Object[]
				{ apcToAnpName, actName } );
			}
			catch ( HibernateException e )
			{
				throw new ConfigurationException( e );
			}

			if ( toActionPinId == null )
			{
				throw new ConfigurationException( "Could not find ApcToAnpId matching ApcToAnpName " + apcToAnpName );
			}

			return toActionPinId;
		}
		if ( ( element.getName().equals( "defaultTableGridColumn" ) ) && ( searchKey.equals( "tclId" ) ) )
		{
			String tbdName = element.attribute( "tbdName" ).getValue();
			String tclName = element.attribute( "tclName" ).getValue();
			return this.tableColumnMap.get( tbdName + "\t" + tclName );
		}
		return null;
	}

	private void initialiseComponentMap() throws ConfigurationException
	{
		try
		{
			this.componentMap = HibernateUtil.buildKeyIdMap( this.sessionFactory, Component.class, Arrays.asList( new String[]
			{ "componentType.cptTypeCd", "cmpName" } ) );
		}
		catch ( HibernateException e )
		{
			throw new ConfigurationException( "Error building component map", e, new Object[0] );
		}
	}

	private void initialiseTableColumnMap() throws ConfigurationException
	{
		try
		{
			this.tableColumnMap = HibernateUtil.buildKeyIdMap( this.sessionFactory, TableColumn.class, Arrays.asList( new String[]
			{ "tableDfn.tbdName", "tclName" } ) );
		}
		catch ( HibernateException e )
		{
			throw new ConfigurationException( "Error building table column map", e, new Object[0] );
		}
	}

	private void initialiseStreamStageMap() throws ConfigurationException
	{
		try
		{
			this.streamStageMap = null;/*HibernateUtil.buildKeyIdMap( this.sessionFactory, StreamStage.class, Arrays.asList( new String[]
			{ "Stream.StmName", "StsName" } ) );*/
		}
		catch ( HibernateException e )
		{
			throw new ConfigurationException( "Error building stream stage map", e, new Object[0] );
		}
	}

	private void initialisePartitionMap() throws ConfigurationException
	{
		try
		{
			this.partitionMap = HibernateUtil.buildKeyIdMap( this.sessionFactory, PartitionTbl.class, Arrays.asList( new String[]
			{ "ptnName" } ) );
		}
		catch ( HibernateException e )
		{
			throw new ConfigurationException( "Error building stream stage map", e, new Object[0] );
		}
	}

	private void initialiseUiActionItemMap() throws ConfigurationException
	{
		try
		{
			this.uiActionItemMap = HibernateUtil.buildKeyIdMap( this.sessionFactory, UiActionItem.class, Arrays.asList( new String[]
			{ "uaiCode" } ) );
		}
		catch ( HibernateException e )
		{
			throw new ConfigurationException( "Error building stream stage map", e, new Object[0] );
		}
	}
}