package com.ibm.contracts.advisor.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;

public class UploadMultipartHandler {
	
	
	 public static File uploadOld(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		  
		  File file = null;
		  String name = null;
		  try {
				MultipartParser parser = new MultipartParser(request,
						1024 * 1024 * 1024); /* file limit size of 1GB */
				com.oreilly.servlet.multipart.Part  aPart;
				while ((aPart = parser.readNextPart()) != null) {
					if (aPart.isFile()) {
						FilePart fPart = (FilePart) aPart; 
						System.out.println("fPart :: "+fPart);
						
						if(fPart.getFileName() != null) {
							name = fPart.getFileName();
						}else {
							System.out.println("name is null");
						}
						
						System.out.println("name :: "+ name);
						if (name != null) {
							InputStream iStream = fPart.getInputStream();
							file = convert(iStream, name);
							String absolutePath = file.getAbsolutePath();
							System.out.println("absolutePath :: "+ absolutePath);
						} 
					}
				}// end while
			} catch (Exception ioe) {
				ioe.printStackTrace();
			}
		return file;
		  
	  }
	  
	 private static File convert(InputStream iStream, String name) throws IOException {
			File file = new File(name);
			OutputStream outputStream = new FileOutputStream(file);
			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = iStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}

			if (iStream != null) {
				try {
					iStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (outputStream != null) {
				try {
					// outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
			return file;
		}
}
