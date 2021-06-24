package com.websocket;
import javax.servlet.http.*;
import javax.websocket.server.*;
import javax.websocket.HandshakeResponse;

public class VitalCheckConfigurator extends ServerEndpointConfig.Configurator{
	
	public void modifyHandshake(ServerEndpointConfig sec,HandshakeRequest request,HandshakeResponse response) {
		sec.getUserProperties().put("username", ((HttpSession)request.getHttpSession()).getAttribute("username").toString());
	}

}
