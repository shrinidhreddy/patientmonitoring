package com.websocket;

import java.util.Collections;

import javax.websocket.server.ServerEndpoint;
import javax.websocket.*;

import java.io.StringWriter;
import java.util.*;
import javax.json.*;

@ServerEndpoint(value="/VitalCheckEndPoint",configurator=VitalCheckConfigurator.class)
public class VitalCheckEndPoint {
	static Set<Session> subscribers=Collections.synchronizedSet(new HashSet<Session>());
	
	@OnOpen
	public void handleOpen(EndpointConfig endpointconfig,Session usersession) {
		usersession.getUserProperties().put("username",endpointconfig.getUserProperties().get("username"));
		subscribers.add(usersession);
	}
	
	@OnMessage
	public void handleMessage(String message,Session usersession) {
		String username=usersession.getUserProperties().get("username").toString();
		//String phone=usersession.getUserProperties().get("phone").toString();
		if(username!=null && !username.equals("doctor")) {
			String messages[]=message.split(",");
			String vital=messages[0];
			String phone=messages[1];
			String temp=messages[2];
			String bp=messages[3];
			String pulse=messages[4];
			subscribers.stream().forEach(x->{
				try {
					
					if(x.getUserProperties().get("username").equals("doctor")) {
						x.getBasicRemote().sendText(buildJSON(username,vital+","+temp+","+pulse+","+bp));
					}
				}
				catch(Exception e) {
					e.getStackTrace();
				}
			});
		}
		else if(username!=null && username.equals("doctor")) {
			String messages[]=message.split(",");
			String patient=messages[0];
			String subject=messages[1];
			String phone=messages[2];
			
			subscribers.stream().forEach(x->{
				try {
					if(subject.equals("ambulance")) {
						if(x.getUserProperties().get("username").equals(patient)) {
							x.getBasicRemote().sendText(buildJSON("Doctor","Has summoned an ambulance"));
						}
						else if(x.getUserProperties().get("username").equals("ambulance")) {
							x.getBasicRemote().sendText(buildJSON(patient+","+phone,"Requires an ambulance"));
						}
					}
					else if(subject.equals("medication")) {
						if(x.getUserProperties().get("username").equals(patient)) {
							x.getBasicRemote().sendText(buildJSON("Doctor",messages[2]+","+messages[3]+","+" "));
						}
					}
				}
				catch(Exception e) {
					e.getStackTrace();
				}
				
			});
		}
	}
	
	@OnClose
	public void handleClose(Session usersession) {
		subscribers.remove(usersession);
	}
	@OnError
	public void handleError(Throwable t) {
		
	}

	private String buildJSON(String username, String message) {
		// TODO Auto-generated method stub
		
		JsonObject jsonobject=Json.createObjectBuilder().add("message",username+","+message+",").build();
		
		StringWriter stringwriter=new StringWriter();
		try (JsonWriter jsonwriter=Json.createWriter(stringwriter)){
			jsonwriter.write(jsonobject);
			
		}
		return stringwriter.toString();
	}

	
}
