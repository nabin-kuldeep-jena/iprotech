package com.asjngroup.deft.common.database.hibernate.util;


import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class HibernateQueryCaseConverter
{
	private List<String> aliases = new ArrayList();
	private boolean lowerCaseFl;

	public HibernateQueryCaseConverter( boolean lowerCaseFl )
	{
		this.lowerCaseFl = lowerCaseFl;
	}

	public String convertIntialCharCase( String query )
	{
		parseQueryAlias( query );
		query = updateQuery( query );
		return query;
	}

	private String updateQuery( String query )
	{
		return convertCase( query );
	}

	private void parseQueryAlias( String query )
	{
		if ( query.length() == 0 )
		{
			return;
		}
		String subQuery = query.substring( 0, query.length() );
		if ( subQuery.length() == -1 )
			return;
		int firstIndex = subQuery.indexOf( "from" );
		if ( firstIndex <= 0 )
			return;
		int whereIndex = subQuery.indexOf( "where" );
		whereIndex = ( whereIndex == -1 ) ? subQuery.length() : whereIndex + 5;
		findAndPopulateAlias( subQuery.substring( firstIndex, whereIndex ) );
		parseQueryAlias( subQuery.substring( whereIndex, subQuery.length() ) );
	}

	private void findAndPopulateAlias( String subquery )
	{
		String newStr = subquery.substring( 4, subquery.length() );
		String[] tokens = newStr.split( "," );
		for ( String item : tokens )
		{
			item = item.trim();
			String[] tempArray = item.split( " " );

			if ( tempArray.length < 2 )
			{
				continue;
			}
			String alias = tempArray[1];

			if ( alias.indexOf( 41 ) > 0 )
			{
				alias = alias.replace( ")", "" );
			}
			this.aliases.add( alias );
		}
	}

	public String convertCase( String str )
	{
		StringBuffer sb = new StringBuffer( str.length() );
		boolean doNotConvertFl = false;
		boolean quoteConvertFl = false;

		String[] tokens = str.split( " " );

		for ( int index = 0; index < tokens.length; ++index )
		{
			if ( tokens[index].length() <= 0 )
			{
				continue;
			}

			StringBuffer token = new StringBuffer( tokens[index] );

			if ( tokens[index].equalsIgnoreCase( "from" ) )
			{
				doNotConvertFl = true;
			}
			if ( ( ( doNotConvertFl ) && ( tokens[index].equalsIgnoreCase( "where" ) ) ) || ( ( tokens[index].equalsIgnoreCase( "group" ) ) && ( tokens[( index + 1 )].equalsIgnoreCase( "by" ) ) ) || ( ( tokens[index].equalsIgnoreCase( "order" ) ) && ( tokens[( index + 1 )].equalsIgnoreCase( "by" ) ) ) )
			{
				doNotConvertFl = false;
				if ( !( tokens[index].equalsIgnoreCase( "where" ) ) )
				{
					token = new StringBuffer( tokens[index] + " " + tokens[( index + 1 )] );
					++index;
				}
			}

			int quoteIndex = tokens[index].indexOf( 39 );
			if ( ( quoteConvertFl ) && ( quoteIndex > -1 ) )
			{
				quoteConvertFl = false;

				if ( ( quoteIndex + 1 < tokens[index].length() ) && ( tokens[index].charAt( quoteIndex + 1 ) == '\'' ) )
				{
					quoteConvertFl = true;
				}
				sb.append( token );
				sb.append( " " );
			}
			else
			{
				if ( ( !( doNotConvertFl ) ) && ( !( quoteConvertFl ) ) )
				{
					int indexOfQuote = tokens[index].indexOf( 39 );
					if ( indexOfQuote > -1 )
					{
						String nonQuoteString = tokens[index].substring( 0, indexOfQuote );
						String quotedString = tokens[index].substring( indexOfQuote );
						quoteConvertFl = true;

						int count = 0;
						for ( int ind = 0; ind < quotedString.length(); ++ind )
						{
							if ( quotedString.charAt( ind ) == '\'' )
							{
								++count;
							}
						}
						if ( count % 2 == 0 )
						{
							sb.append( convertCase( nonQuoteString ) );
							sb.append( quotedString );
							sb.append( " " );
							quoteConvertFl = false;
							continue;
						}

					}

					if ( tokens[index].indexOf( 46 ) > 0 )
					{
						token = convertDottedString( tokens[index] );
					}
					else
					{
						token.setCharAt( 0, Character.toLowerCase( token.charAt( 0 ) ) );
					}
				}
				sb.append( token );
				sb.append( " " );
			}
		}
		return sb.toString().trim();
	}

	private StringBuffer convertDottedString( String str )
	{
		StringBuffer sb = new StringBuffer();

		StringTokenizer st = new StringTokenizer( str, "." );

		while ( st.hasMoreTokens() )
		{
			StringBuffer wordBuffer = new StringBuffer( st.nextToken() );

			if ( ( !( this.aliases.contains( wordBuffer.toString() ) ) ) && ( !( wordBuffer.toString().equalsIgnoreCase( "id" ) ) ) )
			{
				if ( this.lowerCaseFl )
					wordBuffer.setCharAt( 0, Character.toLowerCase( wordBuffer.charAt( 0 ) ) );
				else
				{
					wordBuffer.setCharAt( 0, Character.toUpperCase( wordBuffer.charAt( 0 ) ) );
				}
			}
			sb.append( wordBuffer );
			sb.append( "." );
		}

		sb.deleteCharAt( sb.length() - 1 );

		return sb;
	}
}