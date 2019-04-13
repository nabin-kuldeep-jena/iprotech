package com.asjngroup.deft.email.util.sender;

import com.asjngroup.deft.common.exception.NCashException;
import com.asjngroup.deft.common.util.StringUtil;

public class EmailSendingFailedEception extends NCashException
{
	private static final long serialVersionUID = 1L;

	public EmailSendingFailedEception( String str )
	{
		super( StringUtil.create( str ) );
	}

	public EmailSendingFailedEception( Throwable t )
	{
		super( "", t );
	}

	public EmailSendingFailedEception( String str, Object... args )
	{
		super( StringUtil.create( str, args ) );
	}

	public EmailSendingFailedEception( String str, Throwable t, Object... args )
	{
		super( StringUtil.create( str, args ), t );
	}

}
