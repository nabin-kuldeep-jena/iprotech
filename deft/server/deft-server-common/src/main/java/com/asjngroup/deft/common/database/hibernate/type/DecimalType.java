package com.asjngroup.deft.common.database.hibernate.type;


import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.LongType;
import org.hibernate.usertype.UserType;

public class DecimalType implements UserType
{
	public static final DecimalType DECIMAL_TYPE = new DecimalType();

	private static final int[] TYPES =
	{ -5 };

	public int[] sqlTypes()
	{
		return TYPES;
	}

	public Class returnedClass()
	{
		return BigDecimal.class;
	}

	public boolean equals( Object x, Object y ) throws HibernateException
	{
		if ( x == y )
			return true;
		if ( ( x == null ) || ( y == null ) )
		{
			return false;
		}
		return x.equals( y );
	}

	public static Long convertDisplayToStorage( BigDecimal val )
	{
		return Long.valueOf( val.multiply( new BigDecimal( 1000000 ) ).longValue() );
	}

	public Object deepCopy( Object value ) throws HibernateException
	{
		if ( value == null )
		{
			return null;
		}
		BigDecimal bigDecimal = ( BigDecimal ) value;

		return new BigDecimal( bigDecimal.unscaledValue(), bigDecimal.scale() );
	}

	public boolean isMutable()
	{
		return true;
	}

	@Override
	public int hashCode( Object x ) throws HibernateException
	{
		if ( x == null )
		{
			return 0;
		}
		return x.hashCode();
	}

	@Override
	public Object nullSafeGet( ResultSet rs, String[] names, SessionImplementor session, Object owner ) throws HibernateException, SQLException
	{
		Long result = ( Long ) LongType.INSTANCE.nullSafeGet( rs, names[0] ,session);

		if ( result == null )
		{
			return null;
		}
		return BigDecimal.valueOf( result.longValue(),/* EnvironmentProperties.getSystemProperties().getSysSystemDp().intValue() */8);
	}

	@Override
	public void nullSafeSet( PreparedStatement st, Object value, int index, SessionImplementor session ) throws HibernateException, SQLException
	{
		if ( value == null )
		{
			LongType.INSTANCE.nullSafeSet( st, null, index ,session);
			return;
		}

		BigDecimal bigDecimal = ( BigDecimal ) value;

		LongType.INSTANCE.nullSafeSet( st, Long.valueOf( bigDecimal.setScale(8/* EnvironmentProperties.getSystemProperties().getSysSystemDp().intValue()*/, RoundingMode.HALF_EVEN ).unscaledValue().longValue() ), index ,session);
	}

	@Override
	public Serializable disassemble( Object value ) throws HibernateException
	{
		return ( Serializable ) value;
	}

	@Override
	public Object assemble( Serializable cached, Object owner ) throws HibernateException
	{
		return cached;
	}

	@Override
	public Object replace( Object original, Object target, Object owner ) throws HibernateException
	{
		return original;
	}
}