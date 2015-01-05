package com.google.glassware;

import java.util.Arrays;
import java.util.List;

public class AuthSettings {
	public static String CLIENT_ID = "Your-Client-ID from App Engine";
	public static String CLIENT_SECRET = "Your-Client-Secret from App Engine";
	
	public static final List<String> GLASS_SCOPE = 
			  Arrays.asList("https://www.googleapis.com/auth/glass.timeline",
					  	"https://www.googleapis.com/auth/glass.location",
					  	"https://www.googleapis.com/auth/userinfo.profile");
}


