package com.asjngroup.deft.installer.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;

import com.asjngroup.deft.common.database.schema.Schema;

public class InstallerData
{
	public static final String SYSTEM_PROPERTIES_PDG_KEY = "SystemProperties";
	public static final String CLIENT_PROPERTIES_PDG_KEY = "ClientProperties";
	public static final String SERVER_PROPERTIES_PDG_KEY = "ServerProperties";
	public static final String SYS_LICENSE_KEY_PRD_KEY = "SysLicenseKey";
	public static final String SYS_OPERATOR_NAME = "SysOperatorName";
	public static final String SYSTEM_PROPERTIES_PDG_NAME = "System Properties";
	public static final String CLIENT_PROPERTIES_PDG_NAME = "Client Properties";
	public static final String SERVER_PROPERTIES_PDG_NAME = "Server Properties";
	private String rootDataDir = "";

	private String sysLicenseKey;
	private String sysOperatorName;
	private boolean newInstall = false;

	private List<String> createdTableObjects = new ArrayList();
	private Schema referenceSchema;
	private Schema usageSchema;
	private Map<String, Schema> additionalSchemas;
	private boolean isLicenseSaved = false;
	private boolean updateNeeded = true;

	public InstallerData(Schema referenceSchema, Schema usageSchema, Map<String, Schema> additionalSchemas )
	{
		this.referenceSchema = referenceSchema;
		this.usageSchema = usageSchema;
		this.additionalSchemas = additionalSchemas;
	}

	public String getRootDataDir()
	{
		return this.rootDataDir;
	}

	public void setRootDataDir( String rootDataDir )
	{
		this.rootDataDir = rootDataDir;
	}

	public boolean getNewInstall()
	{
		return this.newInstall;
	}

	public boolean getLicenseSaved()
	{
		return this.isLicenseSaved;
	}

	public void setNewInstall( boolean newInstall )
	{
		this.newInstall = newInstall;
	}

	public boolean getUpdateNeeded()
	{
		return this.updateNeeded;
	}

	public void setUpdateNeeded( boolean updateNeeded )
	{
		this.updateNeeded = updateNeeded;
	}

	public void setLicenseSaved( boolean isLicenseSaved )
	{
		this.isLicenseSaved = isLicenseSaved;
	}

	public String getSysLicenseKey()
	{
		return this.sysLicenseKey;
	}

	public void setSysLicenseKey( String sysLicenseKey )
	{
		this.sysLicenseKey = sysLicenseKey;
	}

	public String getSysOperatorName()
	{
		return this.sysOperatorName;
	}

	public void setSysOperatorName( String sysOperatorName )
	{
		this.sysOperatorName = sysOperatorName;
	}

	public void addCreatedTableObject( String str )
	{
		if ( this.createdTableObjects.contains( str ) )
			return;
		this.createdTableObjects.add( str );
	}

	public boolean isCreatedTableObject( String objectName )
	{
		return this.createdTableObjects.contains( objectName );
	}

	public Map<String, String> getPropertyTokenMap()
	{
		Map tokenMap = new HashMap();

		tokenMap.put( "%DataDir%", this.rootDataDir );

		return tokenMap;
	}

	public void initialise( SessionFactory sessionFactory )
	{
	}

	public List<String> getCreatedTableObjects()
	{
		return this.createdTableObjects;
	}

	public Schema getReferenceSchema()
	{
		return this.referenceSchema;
	}

	public Schema getUsageSchema()
	{
		return this.usageSchema;
	}

	public Map<String, Schema> getAdditionalSchemas()
	{
		return this.additionalSchemas;
	}

/*	public LicenseContent getLicenseContent()
	{
		return this.licenseContent;
	}

	public void setLicenseContent( LicenseContent licenseContent )
	{
		this.licenseContent = licenseContent;
	}

	public boolean isDICEEnabled()
	{
		if ( this.licenseContent instanceof LicenseKeyContentsV02 )
		{
			return ( ( LicenseKeyContentsV02 ) this.licenseContent ).isDICEEnabled();
		}
		return false;
	}

	public boolean isRatingEnabled()
	{
		if ( this.licenseContent instanceof LicenseKeyContentsV02 )
		{
			return ( ( LicenseKeyContentsV02 ) this.licenseContent ).isRatingEnabled();
		}
		return false;
	}

	public boolean isZenEnabled()
	{
		if ( this.licenseContent instanceof LicenseKeyContentsV02 )
		{
			return ( ( LicenseKeyContentsV02 ) this.licenseContent ).isZenEnabled();
		}
		return false;
	}
*/
}
