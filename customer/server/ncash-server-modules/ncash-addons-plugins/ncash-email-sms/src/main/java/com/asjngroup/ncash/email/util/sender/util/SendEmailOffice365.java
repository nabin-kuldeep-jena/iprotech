package com.asjngroup.ncash.email.util.sender.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.asjngroup.ncash.email.util.NCashEmailException;
import com.asjngroup.ncash.email.util.sender.impl.NCashEmailHelper;

public class SendEmailOffice365
{

	public static void main( String[] args ) throws NCashEmailException
	{

		List<String> toAddresses = new ArrayList<String>();
		toAddresses.add( "asjn91@gmail.com" );

		Properties properties = new Properties();

		// turn on authentication and set the user and password properties
		properties.setProperty( "mail.smtp.auth", "true" );
		properties.setProperty( "mail.smtp.user", "nabin.jena@subex.com" );

		properties.setProperty( "mail.smtp.password", "subex@12345" );
		// get and set the SMTP properties from our *server* property list
		properties.setProperty( "mail.smtp.from",  "nabin.jena@subex.com"  );
		properties.setProperty( "mail.smtp.host", "smtp.office365.com");
		properties.setProperty( "mail.smtp.port", "587" );
		properties.setProperty( "mail.smtp.starttls.enable", "false" );

		// turn on *send partial* so invalid addresses don't prevent others from being delivered
		properties.setProperty( "mail.smtp.sendpartial", "true" );

		// return the SMTP properties list
		NCashEmailHelper.sendMail( properties, toAddresses, null, null, "TestEmail", "Hi.....", null );
	}

}