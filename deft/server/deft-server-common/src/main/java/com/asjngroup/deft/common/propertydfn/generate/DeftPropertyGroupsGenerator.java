package com.asjngroup.deft.common.propertydfn.generate;

import java.io.File;
import java.util.ArrayList;

public class DeftPropertyGroupsGenerator
{
	public static void main( String[] paramArrayOfString ) throws Exception
	{
		DeftPropertyObjectGenerator localPropertyGenerator = new DeftPropertyObjectGenerator();
		ArrayList localArrayList = new ArrayList();
		for ( int i=1;i<paramArrayOfString.length;i++ )
		{
			File localFile = new File( paramArrayOfString[i] );
			if ( !( localFile.exists() ) )
				throw new DeftGeneratePropertyException( "File '%1' does not exist", new Object[]
				{ localFile.getAbsolutePath() } );
			if ( !( localFile.isFile() ) )
				throw new DeftGeneratePropertyException( "File '%1' is not a file", new Object[]
				{ localFile.getAbsolutePath() } );
			localArrayList.add( localFile );
		}
		localPropertyGenerator.doGeneration( localArrayList, new File( paramArrayOfString[0] ) );
	}
}