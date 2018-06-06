package com.ibm.contracts.advisor.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.contracts.advisor.constants.Constants;

public class SSLServlet extends HttpServlet implements Constants{

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if(request.getScheme().equals("http")) { 
			//redirect to https 
			//response.sendRedirect(REDIRECTPATH);
			response.sendRedirect("https://"+request.getServerName()+FORWARDPATH);
			
		}
		else
		{
		        //Collect the data from the database and send it to JSP
		      
		        RequestDispatcher rd = request.getRequestDispatcher(FORWARDPATH);
		    rd.forward(request, response);
		}
	}

}
