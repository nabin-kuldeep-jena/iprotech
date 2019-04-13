package com.asjngroup.deft.email.util.sender.util;

import com.asjngroup.deft.email.util.EmailTemplateParam;
import com.asjngroup.deft.email.util.sender.EmailSender;
import com.asjngroup.deft.email.util.sender.EmailSendingFailedEception;

public final class NCashEmailUtil
{
	private static EmailSender NCashEmailSender;

	public static void setComponent( EmailSender NCashEmailSender )
	{
		NCashEmailUtil.NCashEmailSender = NCashEmailSender;
	}

	public static void sendPlainEmailMessage( String from, String to, String subject, String emailMessage ) throws EmailSendingFailedEception
	{
		//NCashEmailSender.sendPlainEmailMessage( from, to, subject, emailMessage );
	}

	public static void sendHtmlTemplateEmailMessage( EmailTemplateParam emailTemplateParam ) throws EmailSendingFailedEception
	{
		NCashEmailSender.sendHtmlTemplateEmailMessage( emailTemplateParam );
	}

}
