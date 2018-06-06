package com.ibm.contracts.advisor.util;

import java.util.StringTokenizer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class SessionData {
	
	
	public void setSessionData(Cookie cookie, HttpSession session, String fileName) {
		// TODO Auto-generated method stub
		String data = cookie.getValue();
	//	System.out.println("Cookie Data is :: "+ data);
		String [] items = data.split("\\|");
		
		session.setAttribute("userId", items[1]);
		session.setAttribute("firstName", items[2]);
		session.setAttribute("lastName", items[3]);
		session.setAttribute("fileName", fileName);
	}

	public void setSessionData(Cookie cookie, HttpSession session) {
		// TODO Auto-generated method stub
		String data = cookie.getValue();
		//System.out.println("Cookie Data is :: "+ data);
		String [] items = data.split("\\|");
		
		session.setAttribute("userId", items[1]);
		session.setAttribute("firstName", items[2]);
		session.setAttribute("lastName", items[3]);
		session.setAttribute("fileName", items[4]);
	}
	
	
	
	
	public void setCookie(String Name, Cookie cookie, String value, HttpServletResponse response){
		cookie = new Cookie(Name,value);
		cookie.setMaxAge(-1);
		response.addCookie(cookie);	
		//System.out.println("Cookie Name :: "+Name);
		//System.out.println("Cookie value :: "+value);
	}
}