package de.asideas.ipoolapiclientgoogleglass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.mirror.Mirror;
import com.google.api.services.mirror.Mirror.Timeline;
import com.google.api.services.mirror.model.MenuItem;
import com.google.api.services.mirror.model.NotificationConfig;
import com.google.api.services.mirror.model.TimelineItem;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.glassware.AuthUtil;

/**
 * This servlet grabs the information from IPool and passing it to the Glass via the Mirror API
 * 
 * @author Peter Krauss - peter.krauss@asideas.de
 */

@SuppressWarnings("serial")
public class IpoolAPIClient extends HttpServlet {
		
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// get Access to Mirror Api
		Mirror mirror = getMirror(req);
		
		// get TimeLine
		Timeline timeline = mirror.timeline();
			
		try {
		    JSONObject json = new JSONObject(retrieveIPool());
		    JSONArray arr = json.getJSONArray("documents");
		    JSONArray media = new JSONArray();
		    
		    String title = null;
		    String content = null;
		    String URL = null;
		    
		    for (int i = 0; i < arr.length(); i++) {
		        title = arr.getJSONObject(i).getString("title");
		        // maybe for future use but for tts needs to be stripped from html tags
		        //content = arr.getJSONObject(i).getString("content");
		        media = arr.getJSONObject(i).getJSONArray("contentReferences");   
		    }
		    		    
		    if (media.length()!=0){
			    for (int i = 0; i < media.length(); i++) {
			    	URL = media.getJSONObject(i).getString("externalUrl");   
			    }
		    }
	    
		    String html = "<article class=\"photo\">\n  <img src=\""+URL+"\" width=\"100%\" height=\"100%\">\n  <div class=\"overlay-gradient-tall-dark\"/>\n  <section>\n    <p class=\"text-auto-size\">"+title+"</p>\n  </section>\n</article>";
		    
		 // create a timeline item
			TimelineItem timelineItem = new TimelineItem()
				//.setSpeakableText(content)
				.setSpeakableText(title)
				.setHtml(html)
				.setDisplayTime(new DateTime(new Date()))
				.setNotification(new NotificationConfig().setLevel("Default"));
			
			// add menu items with built-in actions
			List<MenuItem> menuItemList = new ArrayList<MenuItem>();
			menuItemList.add(new MenuItem().setAction("DELETE"));
			menuItemList.add(new MenuItem().setAction("READ_ALOUD"));
			menuItemList.add(new MenuItem().setAction("TOGGLE_PINNED"));
			timelineItem.setMenuItems(menuItemList);
			
			// insert the crad into the timeline
			timeline.insert(timelineItem).execute();
			
			//print out results on the web browser
			resp.setContentType("text/html; charset=utf-8");
			//resp.setCharacterEncoding("UTF-8");
			resp.getWriter().println(
					"<html><head><meta http-equiv=\"refresh\" content=\"3;url=/index.html\"></head> "
							+ "<body>"+html+"</body></html>");
		} catch (JSONException e) {
		    
		    StringWriter errors = new StringWriter();
		    e.printStackTrace(new PrintWriter(errors));
		    String error =  errors.toString();
		    
		    //resp.setContentType("text/html; charset=utf-8");
		    resp.setCharacterEncoding("UTF-8");
			resp.getWriter().println(
					"<html><head><meta http-equiv=\"refresh\" content=\"3;url=/index.html\"></head> "
							+ "<body>JSON Exception " +error+" </body></html>");
		}
	}

	private Mirror getMirror(HttpServletRequest req) throws IOException{
		Credential credential = AuthUtil.getCredential(req);

		// build access to Mirror API
		return new Mirror.Builder(
				new UrlFetchTransport(), new JacksonFactory(), credential)
					.setApplicationName("Hello World").build();
	}
	
	private static String retrieveIPool() {
		
		try {
			
			// create a consumer object and configure it with the access
			// token and token secret obtained from the service provider
			OAuthConsumer consumer = new DefaultOAuthConsumer("Your-IPool-ID", "<Your-IPool-Secret>");
			consumer.setTokenWithSecret("", "");

			// create an HTTP request to a protected resource
			URL url = new URL("https://ipool.s.asideas.de:443/api/v3/search?limit=1&publisher=%22www.bild.de%22");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");

			// sign the request
			consumer.sign(conn);
			conn.connect();

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuffer html = new StringBuffer();
			char[] charBuffer = new char[4096];
			int count = 0;

			do {
				count = br.read(charBuffer, 0, 4096);
				if (count >= 0)
					html.append(charBuffer, 0, count);
			} while (count > 0);
			br.close();
			conn.disconnect();
			
			String encodedOutput = java.net.URLDecoder.decode(html.toString(), "UTF-8");		
			return encodedOutput;
	
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();

		} catch (OAuthMessageSignerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
}

