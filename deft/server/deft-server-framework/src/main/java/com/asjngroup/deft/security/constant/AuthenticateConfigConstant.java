package com.asjngroup.deft.security.constant;

public class AuthenticateConfigConstant
{
	public static final String ORIENTATIN_LTR = "ltr";
	public static final String PAGE_CONFIG_PROP_FILE_NAME = "/authenticatePageConfig.properties";
	public static final String APPLICATION_KEY = "deft.application";
	public static final String ORIENTATION_KEY = "deft.orientation";
	public static final String USER_NAME_KEY = "deft.username";
	public static final String PASS_WORD_KEY = "deft.password";
	public static final String LOGIN_KEY = "deft.login";
	public static final String LOCALE_INFO_KEY = "deft.locale";
	public static final String COPYRIGHT_MESS_KEY = "deft.copyright_message";
	public static final String REMEMBER_PASSWORD = "deft.remember_password";
	public static final String SPARK_MESSAGE = "deft.message";
	public static final String GROUP_SEPERATOR = "deft.groupSeperator";
	public static final String DECIMAL_SEPERATOR = "deft.decimalSeperator";
	public static final String WINDOW_TITLE = "deft.windowTitle";
	public static final String USERNAME_REQUIRED = "deft.uName";
	public static final Integer INVALID_USERNAME_OR_PASSWORD = Integer.valueOf( 1 );
	public static final Integer PASSWORD_EXPIRED = Integer.valueOf( 2 );
	public static final Integer INVALID_PASSWORD = Integer.valueOf( 3 );
	public static final Integer LOGIN_FAILED = Integer.valueOf( 4 );
	public static final Integer USER_DISABLED = Integer.valueOf( 5 );
	public static final Integer NO_OF_ATTEMPTS = Integer.valueOf( 6 );
	public static final Integer INACTIVE = Integer.valueOf( 7 );
	public static final Integer NO_PRIVLEGE_TO_APPLICATION = Integer.valueOf( 8 );
	public static final Integer PASSWORD_WILL_EXPIRE = Integer.valueOf( 9 );
	public static final Integer USER_ALREADY_LOGGED_IN = Integer.valueOf( 10 );
	public static final Integer NO_ROLE_ACCESS = Integer.valueOf( 11 );
	public static final Integer USER_EXPIRED = Integer.valueOf( 12 );
	public static final Integer MACHINE_BOUND = Integer.valueOf( 13 );
}
