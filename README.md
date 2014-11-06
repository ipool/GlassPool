IPoolClient for Google Glass via Mirror API
========

-----------

This is a small prototype to grab some information from the IPool Api and push it to Google Glass via the Mirror API. You can see the [prototype] here.
It is done 


Setup
-----------

* Create a project on [Google App Engine Console]
* Enable the Google Mirror API in the APIs and Auth section 
* APIs & Auth > Credentials > OAuth : Create new Client ID
 * Choose "Web Application
 * Type in your website and OAuth callback
  * https://your-app-id.appspot.com
  * https://your-app-id.appspot.com/oauth2callback
* Note Client ID and Client secret
* APIs & Auth > Consent Scree > Fill in your data
* Add Google APIs in your Eclipse project - I used the App Engine Plugin
* If you don`t use the war/WEB-INF/appengine-web.xml then add
 * ```<sessions-enabled>true</sessions-enabled>```
* Force https access only
 ``` 
 <!-- force https only -->
	<security-constraint>
		<web-resource-collection>
			<web-resource-name>Protected Area</web-resource-name>
			<url-pattern>/*</url-pattern>
		</web-resource-collection>
		<user-data-constraint>
			<transport-guarantee>CONFIDENTIAL</transport-guarantee>
		</user-data-constraint>
	</security-constraint> 
``` 
Define Auth Filters
```
<!-- filters -->
	<filter>
		<filter-name>authFilter</filter-name>
		<filter-class>com.google.glassware.AuthFilter</filter-class>
	</filter>
	<filter-mapping>
		<filter-name>authFilter</filter-name>
		<url-pattern>*</url-pattern>
	</filter-mapping>

	<filter>
		<filter-name>reauthFilter</filter-name>
		<filter-class>com.google.glassware.ReauthFilter</filter-class>
	</filter>
	<filter-mapping>
		<filter-name>reauthFilter</filter-name>
		<url-pattern>*</url-pattern>
	</filter-mapping>
```
Define Servlet mapping
````
	<servlet>
		<servlet-name>IpoolAPIClient</servlet-name>
		<servlet-class>de.asideas.ipoolapiclientgoogleglass.IpoolAPIClient</servlet-class>
	</servlet>
	<servlet-mapping>
		<servlet-name>IpoolAPIClient</servlet-name>
		<url-pattern>/ipoolapiclient</url-pattern>
	</servlet-mapping>
	
	<servlet>
		<servlet-name>oauth2callback</servlet-name>
		<servlet-class>com.google.glassware.AuthServlet</servlet-class>
	</servlet>
	<servlet-mapping>
		<servlet-name>oauth2callback</servlet-name>
		<url-pattern>/oauth2callback</url-pattern>
	</servlet-mapping>
	```
* In AuthSettings.java
 * Add your-client-id and your-client-secret from App Engine
* In IPoolAPIClient.java change your IPool credentials  
 ```
OAuthConsumer consumer = new DefaultOAuthConsumer("Your-IPool-ID", "<Your-IPool-Secret>");
```
[prototype]:https://speedy-district-755.appspot.com/index.html
[Google App Engine Console]:https://console.developers.google.com
