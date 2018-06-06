package com.ibm.contracts.advisor.servlet;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SessionHelper {

	public SessionHelper() {
		// TODO Auto-generated constructor stub
	}
	
	public static HttpSession sessionHelp(HttpServletRequest request, ServletContext sc, HttpServletResponse response){
		HttpSession session = request.getSession(false);
		if(session == null)
		 {
			session = request.getSession(true);
			Cookie[] cookies = request.getCookies();				
			if(cookies != null){
				for(Cookie cookie : cookies){
					if(cookie.getName().equals("SESSION_ID")) {
						cookie.setValue(session.getId());
					}
					if(cookie.getName().equals("USER_ID")) {
						session.setAttribute("userId", cookie.getValue());
					}
					if(cookie.getName().equals("FIRST_NAME")){
						session.setAttribute("firstName", cookie.getValue());
					}
					if(cookie.getName().equals("LAST_NAME")){
						session.setAttribute("lastName", cookie.getValue());
					}
					if(cookie.getName().equals("FILE_NAME")){
						session.setAttribute("fileName", cookie.getValue());
					}
					
				}
			}else {
				try {
					sc.getRequestDispatcher("/index.html").forward(request, response);
				} catch (ServletException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return session;
	}

}
