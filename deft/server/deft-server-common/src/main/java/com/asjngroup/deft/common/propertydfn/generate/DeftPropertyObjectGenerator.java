package com.asjngroup.deft.common.propertydfn.generate;
/**
 * @author nabin.jena
 * 
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.DocumentException;

import com.asjngroup.deft.common.propertydfn.generate.xmlobj.PropertyDfn;
import com.asjngroup.deft.common.propertydfn.generate.xmlobj.PropertyDfnGroup;
import com.asjngroup.deft.common.propertydfn.generate.xmlobj.PropertyDfnGroupRoot;
import com.asjngroup.deft.common.util.StringHelper;
import com.asjngroup.deft.common.xml.XMLHelper;
import com.asjngroup.deft.common.xml.XMLTreeBuilder;
import com.asjngroup.deft.common.xml.XMLTreeException;

public class DeftPropertyObjectGenerator
{
	public void doGeneration( List<File> paramList,File targetFile ) throws DeftGeneratePropertyException
	{
		Iterator localIterator = paramList.iterator();
		while ( localIterator.hasNext() )
		{
			File localFile1 = ( File ) localIterator.next();
			XMLTreeBuilder localXMLTreeBuilder = null;
			try
			{
				localXMLTreeBuilder = new XMLTreeBuilder( "com.asjngroup.deft.common.propertydfn.generate.xmlobj", XMLHelper.loadDocument( localFile1 ),true );
			}
			catch ( DocumentException localDocumentException )
			{
				throw new DeftGeneratePropertyException( localDocumentException );
			}
			PropertyDfnGroupRoot localPropertyDfnGroupRoot = null;
			try
			{
				localPropertyDfnGroupRoot = ( PropertyDfnGroupRoot ) localXMLTreeBuilder.loadTree();
			}
			catch ( XMLTreeException localXMLTreeException )
			{
				throw new DeftGeneratePropertyException( localXMLTreeException );
			}
			File localFile2 = new File( targetFile.getAbsolutePath() );
			generateServerClasses( localPropertyDfnGroupRoot, localFile2 );
		}
	}

	private void generateServerClasses( PropertyDfnGroupRoot paramPropertyDfnGroupRoot, File paramFile ) throws DeftGeneratePropertyException
	{
		HashMap localHashMap = new HashMap();
		localHashMap.put( "Flag Property", "Boolean" );
		localHashMap.put( "Component Property", "Object" );
		localHashMap.put( "Date Property", "DateTime" );
		localHashMap.put( "Decimal Property", "BigDecimal" );
		localHashMap.put( "Context Directory Property", "ContextFile" );
		localHashMap.put( "Context File Property", "ContextFile" );
		localHashMap.put( "Sql Query Property", "String" );
		localHashMap.put( "Hard Lookup Property", "String" );
		localHashMap.put( "Integer Property", "Integer" );
		localHashMap.put( "String Property", "String" );
		localHashMap.put( "Password Property", "String" );
		localHashMap.put( "Query Property", "SqlQuery" );
		localHashMap.put( "Lookback Property", "String" );
		localHashMap.put( "Chart Property", "String" );
		localHashMap.put( "Colour Property", "String" );
		localHashMap.put( "SQL Lookup Property", "String" );
		localHashMap.put( "Long Property", "Long" );
		File localFile = new File( paramFile, "common/properties/propertygroups" );
		if ( localFile.exists() )
			localFile.delete();
		localFile.mkdirs();
		Iterator localIterator1 = paramPropertyDfnGroupRoot.PropertyDfnGroups.iterator();
		while ( localIterator1.hasNext() )
		{
			PropertyDfnGroup localPropertyDfnGroup = ( PropertyDfnGroup ) localIterator1.next();
			String str1 = localPropertyDfnGroup.pdgKey;
			String str2 = str1 + ".java";
			PrintWriter localPrintWriter = null;
			try
			{
				localPrintWriter = new PrintWriter( new FileOutputStream( new File( localFile, str2 ) ) );
			}
			catch ( FileNotFoundException localFileNotFoundException )
			{
				throw new DeftGeneratePropertyException( localFileNotFoundException );
			}
			localPrintWriter.println( "package " + paramPropertyDfnGroupRoot.serverNamespace + ";" );
			localPrintWriter.println();
			localPrintWriter.println( "import java.math.BigDecimal;" );
			localPrintWriter.println( "import java.io.File;" );
			localPrintWriter.println( "import java.util.Map;" );
			localPrintWriter.println( "import org.joda.time.DateTime;" );
			localPrintWriter.println();
			localPrintWriter.println( "import com.asjngroup.deft.common.io.util.*;" );
			localPrintWriter.println( "import com.asjngroup.deft.common.properties.propertygroups.*;");
			localPrintWriter.println( "import com.asjngroup.deft.common.properties.*;" );
			localPrintWriter.println( "import com.asjngroup.deft.common.database.hibernate.references.*;" );
			if ( !( StringHelper.isEmpty( paramPropertyDfnGroupRoot.parentServerNamespace1 ) ) )
				localPrintWriter.println( "import " + paramPropertyDfnGroupRoot.parentServerNamespace1 + ".*;" );
			if ( !( StringHelper.isEmpty( paramPropertyDfnGroupRoot.parentServerNamespace2 ) ) )
				localPrintWriter.println( "import " + paramPropertyDfnGroupRoot.parentServerNamespace2 + ".*;" );
			if ( !( StringHelper.isEmpty( paramPropertyDfnGroupRoot.parentServerNamespace3 ) ) )
				localPrintWriter.println( "import " + paramPropertyDfnGroupRoot.parentServerNamespace3 + ".*;" );
			if ( !( StringHelper.isEmpty( paramPropertyDfnGroupRoot.parentServerNamespace4 ) ) )
				localPrintWriter.println( "import " + paramPropertyDfnGroupRoot.parentServerNamespace4 + ".*;" );
			if ( !( StringHelper.isEmpty( paramPropertyDfnGroupRoot.parentServerNamespace5 ) ) )
				localPrintWriter.println( "import " + paramPropertyDfnGroupRoot.parentServerNamespace5 + ".*;" );
			if ( !( StringHelper.isEmpty( paramPropertyDfnGroupRoot.serverNamespace) ) )
			{
				localPrintWriter.println( "import com.asjngroup.deft.common.properties.propertygroups.*;" );
				localPrintWriter.println( "import " + paramPropertyDfnGroupRoot.serverNamespace + ".*;" );
			}
			localPrintWriter.println();
			localPrintWriter.print( "public class " + str1 );
			if ( localPropertyDfnGroup.pdgParentPdgKey.length() == 0 )
				localPrintWriter.println( " extends AbstractPropertyGroup" );
			else
				localPrintWriter.println( " extends " + localPropertyDfnGroup.pdgParentPdgKey );
			localPrintWriter.println( "{" );
			if ( ( localPropertyDfnGroup.PropertyDfns != null ) && ( localPropertyDfnGroup.PropertyDfns.size() > 0 ) )
			{
				Iterator localIterator2 = localPropertyDfnGroup.PropertyDfns.iterator();
				while ( localIterator2.hasNext() )
				{
					PropertyDfn localObject = ( PropertyDfn ) localIterator2.next();
					localPrintWriter.println( Indent( 1 ) + "private " + getClientPropertyType( localHashMap, ( PropertyDfn ) localObject ) + " " + ( ( PropertyDfn ) localObject ).prdKey + ";" );
				}
				localPrintWriter.println();
				localPrintWriter.println( Indent( 1 ) + "public void load( int pigId ) throws PropertyHelperException" );
				localPrintWriter.println( Indent( 1 ) + "{" );
				localPrintWriter.println( Indent( 2 ) + "Map< String, Object > propertyMap = PropertyHelper.getPropertyInstValues( pigId );" );
				localPrintWriter.println();
				localPrintWriter.println( Indent( 2 ) + "load( propertyMap );" );
				localPrintWriter.println( Indent( 1 ) + "}" );
				localPrintWriter.println();
				localPrintWriter.println( Indent( 1 ) + "public void load( Map< String, Object > propertyMap ) throws PropertyHelperException" );
				localPrintWriter.println( Indent( 1 ) + "{" );
				int i = 0;
				if ( localPropertyDfnGroup.pdgParentPdgKey.length() > 0 )
					localPrintWriter.println( Indent( 2 ) + "super.load( propertyMap );" );
				Object localObject = localPropertyDfnGroup.PropertyDfns.iterator();
				PropertyDfn localPropertyDfn;
				while ( ( ( Iterator ) localObject ).hasNext() )
				{
					localPropertyDfn = ( PropertyDfn ) ( ( Iterator ) localObject ).next();
					if ( i != 0 )
						localPrintWriter.println();
					if ( localPropertyDfn.prdMandatoryFl )
						localPrintWriter.println( Indent( 2 ) + "if ( !propertyMap.containsKey( \"" + localPropertyDfn.prdKey + "\" ) ) throw new PropertyHelperException( \"Failed to load property group %1 because property dfn %2 was not present\", \"" + localPropertyDfnGroup.pdgName + "\", \"" + localPropertyDfn.prdKey + "\" );" );
					localPrintWriter.println( Indent( 2 ) + localPropertyDfn.prdKey + " = (" + getClientPropertyType( localHashMap, localPropertyDfn ) + ")propertyMap.get( \"" + localPropertyDfn.prdKey + "\" );" );
					i = 1;
				}
				localPrintWriter.println( Indent( 1 ) + "}" );
				localObject = localPropertyDfnGroup.PropertyDfns.iterator();
				while ( ( ( Iterator ) localObject ).hasNext() )
				{
					localPropertyDfn = ( PropertyDfn ) ( ( Iterator ) localObject ).next();
					if ( !( localPropertyDfn.prdReadOnlyFl ) )
					{
						localPrintWriter.println();
						localPrintWriter.println( Indent( 1 ) + "public void set" + localPropertyDfn.prdKey + "( " + getClientPropertyType( localHashMap, localPropertyDfn ) + " val )" );
						localPrintWriter.println( Indent( 1 ) + "{" );
						localPrintWriter.println( Indent( 2 ) + "this." + localPropertyDfn.prdKey + " = val;" );
						localPrintWriter.println( Indent( 1 ) + "}" );
					}
				}
				localObject = localPropertyDfnGroup.PropertyDfns.iterator();
				while ( ( ( Iterator ) localObject ).hasNext() )
				{
					localPropertyDfn = ( PropertyDfn ) ( ( Iterator ) localObject ).next();
					localPrintWriter.println();
					localPrintWriter.println( Indent( 1 ) + "public " + getClientPropertyType( localHashMap, localPropertyDfn ) + " get" + localPropertyDfn.prdKey + "()" );
					localPrintWriter.println( Indent( 1 ) + "{" );
					localPrintWriter.println( Indent( 2 ) + "return " + localPropertyDfn.prdKey + ";" );
					localPrintWriter.println( Indent( 1 ) + "}" );
				}
			}
			localPrintWriter.println( "}" );
			localPrintWriter.close();
		}
	}

	private String getClientPropertyType( Map<String, String> paramMap, PropertyDfn paramPropertyDfn )
	{
		if ( paramPropertyDfn.cmpName.equals( "Lookup Property" ) )
			return paramPropertyDfn.prdExtra1;
		return ( ( String ) paramMap.get( paramPropertyDfn.cmpName ) );
	}

	private static String Indent( int paramInt )
	{
		return StringHelper.fill( ' ', paramInt * 4 );
	}
}