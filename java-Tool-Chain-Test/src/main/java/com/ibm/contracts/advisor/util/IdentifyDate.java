package com.ibm.contracts.advisor.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class IdentifyDate {
	
	public static String formatDetect(Elements elements){
		String format="NA";
		String pattern1 = "\\d{2,4}[\\-:./]\\d{2}[\\-:./]\\d{2,4}";
		for(Element e:elements){
			String d=getSpecialString(e.toString(),pattern1);
			if(d!=null){
				String[] all = d.split("[\\-:./]");
				if(Integer.parseInt(all[0])>12 && format.equals("NA")){
					format="IND";
				}
				else if(Integer.parseInt(all[0])>12 && format.equals("US")){
					format="MUL";
				}
				
					
				
				if(Integer.parseInt(all[1])>12 && format.equals("NA")){
					format="US";
				}
				if(Integer.parseInt(all[1])>12 && format.equals("IND")){
					format="MUL";
				}
			}
		}
		return format;
	}
	

	public static String extractDateWOyear(String test) {
		String aDate = null;// new DateTime(year, month, 1, 0, 0, 0);
		System.out.println("coming to extract date wo year::"+test);
		String standard = "us";
		String pattern1 = "\\d{2,4}[\\-:./]\\d{2}[\\-:./]\\d{2,4}";
		String mdate1 = getSpecialString(test, pattern1);

		if (mdate1 != null) {
			aDate = getAllDig(mdate1, standard);
			return aDate;
		} else {

			String pattern2 = "([a-zA-Z]+\\s+[0-9][0-9]?(st|nd|rd|th)?\\s*)";// aug
																				// 1st

			String pattern3 = "([0-9][0-9]?(st|nd|rd|th)?\\s+[a-zA-Z]+\\s*)";// 1st
																				// aug
																				// 2002
			String pattern4 = "([0-9][0-9]?[\\-\\s+][a-zA-Z]{3,10})";// 11-jun-17
			// System.out.println( );

			String extracted1 = getSpecialString(test, pattern3);
			String extracted2 = getSpecialString(test, pattern2);
			String extracted3 = getSpecialString(test, pattern4);
			// System.out.println(extracted3);
			
					if(extracted1!=null){
							aDate=getAllWOyear(extracted1);
							//System.out.println("1st got::"+aDate);
						}

						 if(extracted2!=null && aDate==null){
							aDate=getAllWOyear(extracted2);
							//System.out.println("2nd got::"+aDate);
						}

						 if(extracted3!=null  && aDate==null)
							aDate=getAllWOyear(extracted3); 

						 if(aDate!=null)
								System.out.println("got date wo year::"+aDate);
			return aDate;
		}
	}

	
	
	
	public static String extractDate(String test) {
		String aDate = null;// new DateTime(year, month, 1, 0, 0, 0);
		 System.out.println("coming to extract date::"+test);
		// String test2="2012.10.21 this was the date";
		String standard = "us";
		String pattern1 = "\\d{1,4}[\\-:./]\\d{1,2}[\\-:./]\\d{2,4}";
		String mdate1 = getSpecialString(test, pattern1);

		if (mdate1 != null) {
			aDate = getAllDig(mdate1, standard);
			System.out.println("got date::"+aDate);
			return aDate;
		} else {

			String pattern2 = "([a-zA-Z]+[\\s\\-]+[0-9][0-9]?(st|nd|rd|th)?\\s*[\\-,]?\\s*(20)?[0-9]{2})";// aug
																								// 1st
																								// 2002

			String pattern3 = "([0-9][0-9]?(st|nd|rd|th)?\\s+(of\\s+)?[a-zA-Z]+\\s*\\,?\\s*(20)?[0-9]{2})";// 1st
																									// aug
																									// 2002
			String pattern4 = "([0-9][0-9]?[\\s]?[\\–\\-][\\s]?[a-zA-Z]+[\\-\\s+](20)?[0-9]{2})";// 11-jun-17
			
			//System.out.println(getSpecialString(test, "([0-9][0-9]?[\\s]?[\\–\\-][\\s?][a-zA-Z]+[\\-\\s+](20)?[0-9]{2})") );
			

			String extracted1 = getSpecialString(test, pattern3);
			String extracted2 = getSpecialString(test, pattern2);
			String extracted3 = getSpecialString(test, pattern4);
			/*System.out.println("pattern3::"+extracted1);
			 System.out.println("pattern2::"+extracted2);
			 System.out.println("pattern4::"+extracted3);*/
			if (extracted1 != null){
				try{
					aDate = getAll(extracted1);
				}catch(Exception e){
					System.out.println(e.toString());
					aDate=null;
				}
				
			}
				
			 if (extracted2 != null && aDate==null){
				try{
					aDate = getAll(extracted2);
				}catch(Exception e){
					System.out.println(e.toString());
					aDate=null;
				}
				
			}
				
			 if (extracted3 != null && aDate==null){
				try{
					aDate = getAll(extracted3);
				}catch(Exception e){
					System.out.println(e.toString());
					aDate=null;
				}
				
			}
				
			if(aDate!=null)
			System.out.println("got date::"+aDate);
			return aDate;
		}
	}
	
	public static void main(String[] args) {
		String test = "this sow shall begin on 15-1-18 and remain in effect until ";
		System.out.println(test);
		System.out.println(extractDate(test));
		
	}

	private static String getAllDig(String mdate1, String standard) {
		String dateStr = null;
		String[] all = mdate1.split("[\\-:./]");
		if (standard.equalsIgnoreCase("us")) {
			// System.out.println("day is ::"+all[1]);
			// System.out.println("month is ::"+all[0]);
			if (all[2].length() == 2)
				dateStr = Integer.parseInt(all[1]) + "|"
						+ Integer.parseInt(all[0]) + "|20" + all[2];
			else
				// System.out.println("year is ::"+all[2]);

				dateStr = Integer.parseInt(all[1]) + "|"
						+ Integer.parseInt(all[0]) + "|" + all[2];

		} else {
			// System.out.println("day is ::"+all[0]);
			// System.out.println("month is ::"+all[1]);
			// System.out.println("year is ::"+all[2]);

			dateStr = all[0] + "|" + all[1] + "|" + all[2];
		}
		return dateStr;
	}

	private static String getAllWOyear(String myDate) {
		int month = getMonth(myDate);
		// System.out.println("month ::"+month);
		String dateStr = null;
		myDate = myDate.replaceAll("[a-zA-Z]{3,11}", "");

		int day = getDay(myDate);
		DateTime d = new DateTime();
		// System.out.println("year is ::"+d.getYear());
		if (day == 0 || month == 0)
			return null;
		dateStr = day + "|" + month + "|" + d.getYear();
		return dateStr;
	}

	private static String getAll(String myDate) {
		//System.out.println("fetch date::"+myDate);
		int month = getMonth(myDate);
		if (month == 0) {
			return null;
		}

		//System.out.println("month ::" + month);
		String dateStr = null;
		myDate = myDate.replaceAll("[a-zA-Z]{3,11}", "");

		// //System.out.println("==="+myDate);
		int year = getYear(myDate);
		//System.out.println(myDate + "  year is ::" + year);
		myDate = myDate.replaceFirst(Integer.toString(year), "");
		//System.out.println("got ::"+myDate);
		int day = getDay(myDate);
		//System.out.println("day is ::" + day);
		if (year < 100)
			year = 2000 + year;
		dateStr = day + "|" + month + "|" + year;
		return dateStr;
	}

	private static int getYear(String myDate) {
		// TODO Auto-generated method stub
		String year = getSpecialString(myDate, "(20)?\\d{2}");
		return Integer.parseInt(year);
	}

	private static int getDay(String myDate) {
		// TODO Auto-generated method stub

		// System.out.println("get day from :"+myDate);
		String day = getSpecialString(myDate, "[123]?[0-9]");
		return Integer.parseInt(day);
	}

	private static int getMonth(String date) {
		if (getSpecialString(date, "jan(uary)?") != null)
			return 1;
		if (getSpecialString(date, "feb(ruary)?") != null)
			return 2;
		if (getSpecialString(date, "mar(ch)?") != null)
			return 3;

		if (getSpecialString(date, "apr(il)?") != null)
			return 4;
		if (getSpecialString(date, "may") != null)
			return 5;
		if (getSpecialString(date, "jun(e)?") != null)
			return 6;

		if (getSpecialString(date, "jul(y)?") != null)
			return 7;
		if (getSpecialString(date, "aug(ust)?") != null)
			return 8;
		if (getSpecialString(date, "sep(t(ember)?)?") != null)
			return 9;
		if (getSpecialString(date, "oct(ober)?") != null)
			return 10;
		if (getSpecialString(date, "nov(ember)?") != null)
			return 11;
		if (getSpecialString(date, "dec(ember)?") != null)
			return 12;

		return 0;
	}

	

	public static String getSpecialString(String text, String regex) {
		Pattern p = Pattern.compile(regex);

		Matcher m = p.matcher(text.toLowerCase());
		ArrayList<String> tmp = new ArrayList<String>();
		while (m.find()) {
			String group = m.group();
			tmp.add(group);
		}
		if (tmp.size() == 0)
			return null;
		else
			return tmp.get(tmp.size() - 1);
	}

	public static List<String> getAllSpecialString(String text, String regex) {
		Pattern p = Pattern.compile(regex);

		Matcher m = p.matcher(text.toLowerCase());
		ArrayList<String> tmp = new ArrayList<String>();
		while (m.find()) {
			String group = m.group();
			tmp.add(group);
		}
		return tmp;
	}

}
