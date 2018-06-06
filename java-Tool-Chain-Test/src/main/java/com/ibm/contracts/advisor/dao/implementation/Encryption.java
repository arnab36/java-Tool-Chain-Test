package com.ibm.contracts.advisor.dao.implementation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.sql.SQLException;
import java.util.Set;

import javax.naming.NamingException;

import org.apache.commons.codec.binary.Base64;

public class Encryption {

	public static final String DEFAULT_ENCODING = "UTF-8";

	public static String base64encode(String text) {
		try {
			byte[] tmp = Base64.encodeBase64(text.getBytes());
			// System.out.println(new String(decoded, "UTF-8") + "\n");
			String encoded = new String(tmp, "UTF-8");
			return encoded;
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}// base64encode

	public static String base64decode(String text) {
		try {
			byte[] tmp = Base64.decodeBase64(text);
			// System.out.println(new String(decoded, "UTF-8") + "\n");
			String decoded = new String(tmp);
			return decoded;
		} catch (Exception e) {
			return null;
		}
	}// base64decode
		// http://stackoverflow.com/questions/1205135/how-to-encrypt-string-in-java

	public static void main(String[] args) throws FileNotFoundException,
			IOException, NamingException, SQLException {
		String key = "dsaihfsdhfis423efw532432";

		Set<String> s = UserDAO.getAllUser();

		String txt, encoded = null;
		//File file = new File("C:/Users/IBM_ADMIN/Documents/Phase2/strategyCred.csv");
		File file = new File("C:/Users/IBM_ADMIN/Documents/Phase2/cred7.csv");
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			int objid = 50;
			while ((line = br.readLine()) != null) {
				System.out.println(" ==============================" + line);
				String[] user = line.split(",");
				encoded = base64encode(xorMessage(user[3], key));
				if (!s.contains(user[0].trim()))
					UserDAO.insertIntoCredTablePool(user[0].trim(), user[1],
							user[2], encoded, user[4]);
				else
					System.out.println("---same user name -----------" + line);
				objid++;

			}
			
			/*while ((line = br.readLine()) != null) {				
				String[] user = line.split(",");
				//encoded = base64encode(xorMessage(user[3], key));
				StrategyDAO.insertIntoStrategyTable(user[0].trim(), user[1],
							user[2], user[3]);
				UserDAO.insertIntoCredTablePool(user[0].trim(), user[1],
						user[2], encoded,user[4]);
				
			}*/
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String xorMessage(String message, String key) {
		try {
			if (message == null || key == null)
				return null;

			char[] keys = key.toCharArray();
			char[] mesg = message.toCharArray();

			int ml = mesg.length;
			int kl = keys.length;
			char[] newmsg = new char[ml];

			for (int i = 0; i < ml; i++) {
				newmsg[i] = (char) (mesg[i] ^ keys[i % kl]);
			}// for i

			return new String(newmsg);
		} catch (Exception e) {
			return null;
		}
	}// xorMessage
}
