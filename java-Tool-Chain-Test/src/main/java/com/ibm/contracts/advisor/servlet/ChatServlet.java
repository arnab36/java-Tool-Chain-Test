/**
 * 
 */
package com.ibm.contracts.advisor.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.contracts.advisor.constants.APICredentials;
import com.ibm.contracts.advisor.constants.Constants;
import com.ibm.contracts.advisor.parser.CiscoVcapUtils;
import com.ibm.contracts.advisor.parser.VcapSetupParser;
import com.ibm.contracts.advisor.util.JsonRespond;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

/**
 * @author Atrijit
 * 
 */
public class ChatServlet extends HttpServlet {

	/**
	 * The class variables. service will be created once. we have to keep track
	 * of msgResponse and get its context
	 */
	private ConversationService service = null;
	private MessageRequest newMessage = null;
	private MessageResponse msgResponse = null;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String message = request.getParameter("param_1");
		int startConversation = Integer.parseInt(request
				.getParameter("param_2"));

		System.out.println("startConversation is :: " + startConversation);

		if (startConversation == 0) {
			initializeConversation();
			newMessage = new MessageRequest.Builder().inputText("").build();
			msgResponse = callConversationAPI(service, newMessage);
		} else {
			newMessage = new MessageRequest.Builder().inputText(message)
					.context(msgResponse.getContext()).build();
			System.out.println("Input : " + newMessage.inputText());
			msgResponse = callConversationAPI(service, newMessage);
		}

		try {
			JsonRespond.createResponseJSON(
					msgResponse.getTextConcatenated(" "), "Temp Log",
					"Temp Error", response, 200);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void initializeConversation() {

		/*service = new ConversationService(
				ConversationService.VERSION_DATE_2017_02_03);  */		
		service = null;	

		if (Constants.CISCOVCAP) {	
			service = new ConversationService(CiscoVcapUtils.ENV_CONVERSATION_VERSION);  
			service.setUsernameAndPassword(
					CiscoVcapUtils.convUsername,
					CiscoVcapUtils.convPassword);
		} else {
			if (Constants.VCAPCONVERSATION) {
				// VcapSetupParser.getConversationCredentials(VcapSetupParser.VCAPUtils());		
				service = new ConversationService(VcapSetupParser.ENV_CONVERSATION_VERSION);
				service.setUsernameAndPassword(
						VcapSetupParser.VCAPCONVERSATION_userID,
						VcapSetupParser.VCAPCONVERSATION_password);
			} else {
				service = new ConversationService(
						ConversationService.VERSION_DATE_2017_02_03); 
				service.setUsernameAndPassword(APICredentials.CHAT_CONV_USERID,
						APICredentials.CHAT_CONV_PASSWORD);
			}
		}

	}

	public MessageResponse callConversationAPI(ConversationService service,
			MessageRequest newMessage) {

		MessageResponse msgResponse = null;
		if (Constants.CISCOVCAP) {
			msgResponse = service
					.message(CiscoVcapUtils.ENV_CHAT_CLASS_WORKSPACE_ID,
							newMessage).execute();
		} else {
			if (Constants.VCAPCONVERSATION) {
				// VcapSetupParser.setEnvironmentVariable();
				msgResponse = service
						.message(VcapSetupParser.ENV_CHAT_CLASS_WORKSPACE_ID,
								newMessage).execute();
			} else {
				msgResponse = service.message(
						APICredentials.CHAT_CLASS_WORKSPACE_ID, newMessage)
						.execute();
			}

		}

		// System.out.println("The response is :: " + msgResponse );
		return msgResponse;
	}

	/*
	 * private MessageResponse GetResponse(ConversationService service, String
	 * string) { MessageRequest newMessage = new
	 * MessageRequest.Builder().inputText( string).build(); MessageResponse
	 * response = null;
	 * 
	 * for (int i = 0; i < CHATRETRIES; i++) { try { response =
	 * service.message(CHAT_CLASS_WORKSPACE_ID, newMessage) .execute(); break; }
	 * catch (Exception e) { e.getMessage(); } } return response; }
	 */

}
