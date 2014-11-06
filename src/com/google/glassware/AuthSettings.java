package com.google.glassware;

import java.util.Arrays;
import java.util.List;

public class AuthSettings {
	public static String CLIENT_ID = "571093447971-all1qtpe17v0u5epl7uo49ca06lqomip.apps.googleusercontent.com";
	public static String CLIENT_SECRET = "CvIsQeM7KmPqnyVAxp9ev4V3";
	
	public static final List<String> GLASS_SCOPE = 
			  Arrays.asList("https://www.googleapis.com/auth/glass.timeline",
					  	"https://www.googleapis.com/auth/glass.location",
					  	"https://www.googleapis.com/auth/userinfo.profile");
}


