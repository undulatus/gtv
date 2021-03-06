package com.pointwest.googletoken.validator.controller;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Properties;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;

@RestController
public class TokenSignInController {
	
	private String CLIENT_ID = "";
    private Boolean isValid = false;
    
	@RequestMapping(method=RequestMethod.POST, value="/tokensignin")
    public Boolean validateToken(@RequestParam("idTokenString") String idTokenString) throws GeneralSecurityException, IOException {
		try{ 
			CLIENT_ID = getClientId();
			GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
			.setAudience(Collections.singletonList(CLIENT_ID))
			 // Or, if multiple clients access the backend:
			 //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
			.build();
			
			// (Receive idTokenString by HTTPS POST)
			GoogleIdToken idToken = verifier.verify(idTokenString);
			if (idToken != null) {
			  Payload payload = idToken.getPayload();
		
			  // Print user identifier
			  System.out.println("User ID: " + payload.getUserId());
		
			  // Get profile information from payload
			  String email = payload.getEmail();
			  boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
			  String name = (String) payload.get("name");
			  String pictureUrl = (String) payload.get("picture");
			  String locale = (String) payload.get("locale");
			  String familyName = (String) payload.get("family_name");
			  String givenName = (String) payload.get("given_name");
		
			  System.out.println(email);
			  System.out.println(emailVerified);
			  System.out.println(name);
			  System.out.println(pictureUrl);
			  System.out.println(locale);
			  System.out.println(familyName);
			  System.out.println(givenName);
			  
			  // Use or store profile information
			  // ...
			  isValid = true;
			  System.out.println("Valid ID token.");
			} else {
			  System.out.println("Invalid ID token.");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		 return isValid;
    }
	
	private String getClientId() {
		Properties prop = new Properties();
		try (InputStream input = TokenSignInController.class.getClassLoader().getResourceAsStream("google.signin.properties"))
		{
			prop.load(input);
			return prop.getProperty("client_id");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
	}

}
