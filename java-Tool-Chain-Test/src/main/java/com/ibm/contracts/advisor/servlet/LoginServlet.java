package com.ibm.contracts.advisor.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.dao.implementation.UserDAO;
import com.ibm.contracts.advisor.util.JsonRespond;
import com.ibm.contracts.advisor.util.SOP;
import com.ibm.contracts.advisor.util.SessionData;

;

@WebServlet(urlPatterns = { "/Login", "/Logout" })
public class LoginServlet extends HttpServlet implements Constants {
	private static final long serialVersionUID = 1L;
	PrintWriter out;

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		//String strAction = request.getParameter("action");
		ServletContext sc = this.getServletContext();
		HttpSession session = request.getSession(false);
		//System.out.println(strAction);
		// doPost(request, response);
		// System.out.println("=========== Inside login servlet do get =================");
		//if ("Logout".equals(strAction)) {
			if (session != null) {
				session.invalidate();
			}
			request.logout();
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					SOP.printSOPSmall("Cookies Name :: " + cookie.getName());
					cookie.setValue("");
					cookie.setMaxAge(0);
					// response.addCookie(cookie);
				}
			}

			sc.getRequestDispatcher("/index.html").forward(request, response);
		//}
		/*
		 * else if(session != null && session.getAttribute("userId") != null) {
		 * sc.getRequestDispatcher("/upload.html").forward(request, response); }
		 */
		/*else {
			sc.getRequestDispatcher("/index.html").forward(request, response);
		}*/
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String userid = request.getParameter("email");
		String password = request.getParameter("password");
		System.out.println("from request userid is "+userid);
		System.out.println("from request password is "+password);

		JSONObject jsonObject = new JSONObject();
		JSONObject strategyInfoHierarchy = new JSONObject();
		Cookie sId = null;
		Cookie uId = null;
		Cookie fName = null;
		Cookie lName = null;

		HttpSession session = request.getSession(false);

		String[] actualPassword = null;
		try {
			//actualPassword = (new UserDAO().getPasswordById(userid));
			actualPassword = UserDAO.getPasswordById(userid);
			try {
				System.out.println("Password1 is "+actualPassword[0]);
				System.out.println("Password2 is "+actualPassword[1]);
				System.out.println("Given Password2 is "+password);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		boolean success = false;
		ServletContext sc = this.getServletContext();
		SessionData sd = new SessionData();

		if (actualPassword[0] != null) {
			if (actualPassword[0].equals(password)) {
				success = true;
				session = request.getSession(true);
				session.setAttribute("userId", userid);
				session.setAttribute("firstName", actualPassword[1]);
				session.setAttribute("lastName", actualPassword[2]);
				session.setMaxInactiveInterval(30 * 60);

				sd.setCookie("SESSION_ID", sId, session.getId(), response);
				sd.setCookie("USER_ID", uId, userid, response);
				sd.setCookie("FIRST_NAME", fName, actualPassword[1], response);
				sd.setCookie("LAST_NAME", lName, actualPassword[2], response);
				
				System.out.println("1. success is "+success);
				System.out.println("session is "+session);
				System.out.println("session.getAttribute(userId) is "+session.getAttribute(userId));
				System.out.println("SESSION_ID in coolie is "+sId);

			} else {
				success = false;
				System.out.println("2. success is "+success);
				if(session!=null){
					session.invalidate();
				}
				
			}
		} else {
			success = false;
			System.out.println("3. success is "+success);
		}

		if (!success) {
			try {
				System.out.println("failed");
				JsonRespond.respond("Failure", response, 400);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			
			try {
				jsonObject.put("firstName", actualPassword[1]);
				jsonObject.put("lastName", actualPassword[2]);
				jsonObject.put("role", actualPassword[3]);
				jsonObject.put("status", 200);
			
				/*JsonRespond.createLoginResponseJSON(jsonObject.toJSONString(),
						strategyInfoHierarchy.toJSONString(), response, 200);*/
				
				JsonRespond.createLoginResponseJSON(jsonObject.toJSONString(),
						response, 200);

				//JsonRespond.respond(jsonObject.toJSONString(), response, 200);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}