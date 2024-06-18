package p2psystem;

import com.google.gson.Gson;

public class MessageUtils {
	private static final Gson gson = new Gson ( ) ; 
	
	public static String serialize ( Message message ) {
		return gson.toJson(message) ; 
	}
	
	public static Message deserialize ( String json ) {
		return gson.fromJson(json, Message.class) ; 
	}
}
