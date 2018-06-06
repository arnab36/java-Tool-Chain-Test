package com.ibm.contracts.advisor.util;



import java.io.IOException;
import java.io.UnsupportedEncodingException;


import org.apache.commons.codec.binary.Base64;


public class Encryption {

	public static final String DEFAULT_ENCODING = "UTF-8"; 
	
	

    public static String base64encode(String text) {
        try {
        	byte[] tmp = Base64.encodeBase64(text.getBytes());
        	//System.out.println(new String(decoded, "UTF-8") + "\n");
        	String encoded=new String(tmp, "UTF-8");
            return encoded;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }//base64encode

    public static String base64decode(String text) {
        try {
        	byte[] tmp = Base64.decodeBase64(text);
        	//System.out.println(new String(decoded, "UTF-8") + "\n");
        	String decoded=new String(tmp);
            return decoded;
        } catch (Exception e) {
            return null;
        }
    }//base64decode
//http://stackoverflow.com/questions/1205135/how-to-encrypt-string-in-java
    public static void main(String[] args) {
    	String key = "dsaihfsdhfis423efw532432";
    	for(int i=1;i<=7;i++){
    		String txt="password"+Integer.toString(i);
    		String encoded= base64encode(xorMessage(txt, key));
    		//System.out.println(i+","+"user"+i+","+"ayan,das,"+encoded);
    	}
        String txt = "password1";
        
       // System.out.println(txt + " XOR-ed to: " + (txt = xorMessage(txt, key)));

        String encoded = base64encode(txt);       
       // System.out.println(" is encoded to: " + encoded + " and that is decoding to: " + (txt = base64decode(encoded)));
       // System.out.print("XOR-ing back to original: " + xorMessage(txt, key));
    }

    public static String xorMessage(String message, String key) {
        try {
            if (message == null || key == null) return null;

            char[] keys = key.toCharArray();
            char[] mesg = message.toCharArray();

            int ml = mesg.length;
            int kl = keys.length;
            char[] newmsg = new char[ml];

            for (int i = 0; i < ml; i++) {
                newmsg[i] = (char)(mesg[i] ^ keys[i % kl]);
            }//for i

            return new String(newmsg);
        } catch (Exception e) {
            return null;
        }
    }//xorMessage
}
