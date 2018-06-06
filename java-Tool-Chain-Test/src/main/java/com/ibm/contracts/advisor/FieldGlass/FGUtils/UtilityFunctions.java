package com.ibm.contracts.advisor.FieldGlass.FGUtils;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ibm.contracts.advisor.vo.IndexPairVO;

public class UtilityFunctions {

	/**
	 * The following function takes 3 inputs,
	 * 
	 * content -> the document content in String format pattern -> The tag we
	 * are searching for range -> The number of characters i.e the length of
	 * substring we are searching with
	 * 
	 * if the start index is '0' then we shall start from beginning of the file.
	 * 
	 */

	public static IndexPairVO getOutputIndex(String content, String pattern,
			int range, int startIndex) {
		IndexPairVO output = new IndexPairVO();
		int start = startIndex;
		int end = 0;
		try {
			for (int i = start;; i++) {
				if (content.substring(i, i + range).equals(pattern)) {
					end = i;
					break;
				}
			}

			output.setStartIndex(startIndex);
			output.setEndIndex(end);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return output;
	}

	/**
	 * The following function will take 4 inputs.
	 * 
	 * 1) arg1 : content as String 2) arg2 : opening tag pattern as String, eg "
	 * <table>
	 * " 3) arg3 : closing tag pattern as String, eg "
	 * </table>
	 * " 4) arg4 : title as String denoting the content of the tag.
	 * 
	 * Output: A pair of indices defining the beginning and end of the tag.
	 * 
	 * */
	public static IndexPairVO GetCurrentTagIndex(String content,
			String patternTagOpen, String patternTagClose, String title) {
		IndexPairVO output = new IndexPairVO();
		int startIndex = 0;
		int endIndex = 0;
		int keyIndex = 0;
		int range = 0;

		boolean lowerFlag = true;

		keyIndex = content.indexOf(title);
		if (keyIndex == -1) {
			keyIndex = checkLowerCase(content, title);
			if (keyIndex == -1) {
				lowerFlag = false;
				System.out.println("Ecception is lower case also");
			}
		}

		System.out.println(keyIndex);
		System.out.println(lowerFlag);

		if (lowerFlag) {
			try {
				// Going backward
				range = patternTagOpen.length();
				for (int i = keyIndex;; i--) {
					if (content.substring(i, i + range).equals(patternTagOpen)) {
						startIndex = i;
						break;
					}
				}

				// going forward
				range = patternTagClose.length();
				for (int i = keyIndex;; i++) {
					if (content.substring(i, i + range).equals(patternTagClose)) {
						endIndex = i;
						break;
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Exception for class :: " + title);
			}
		}

		output.setStartIndex(startIndex);
		output.setEndIndex(endIndex);
		return output;
	}

	/**
	 * The following function takes two arguments, -
	 * 
	 * arg1 : content as String arg2: title as String
	 * 
	 * O/P : - It returns the index of the title in the content as Integer.
	 * 
	 * If that title is not found, it returns -1.
	 * 
	 * */
	private static int checkLowerCase(String content, String title) {
		// TODO Auto-generated method stub
		content = content.toLowerCase();
		title = title.toLowerCase();
		System.out.println("Inside checkLower case ");
		int keyIndex = 0;
		keyIndex = content.indexOf(title);
		return keyIndex;
	}

	/**
	 * 
	 * The following function will take,- arg 1) :: pair of indices arg 2) ::
	 * the content of the doc as String It will return the text under the next
	 * <td></td> tag.
	 * 
	 * Inside the <td></td> tag we may get some
	 * <p>
	 * ,
	 * </p>
	 * tags which we have to return
	 * 
	 * */

	public static String getNextTDText(IndexPairVO titleIndex, String doc) {
		// TODO Auto-generated method stub
		String content = "";

		System.out.println("Char is :: "
				+ doc.charAt(titleIndex.getEndIndex() + 8));
		int tdStartIndex = 0, tdEndIndex = 0;

		try {

			for (int i = titleIndex.getEndIndex();; i++) {
				if (doc.substring(i, i + 4).equals("<td>")) {
					tdStartIndex = i + 4;
					break;
				}
			}

			for (int i = tdStartIndex;; i++) {
				if (doc.substring(i, i + 5).equals("</td>")) {
					tdEndIndex = i;
					break;
				}
			}

			content = doc.substring(tdStartIndex, tdEndIndex);
			content = content.replaceAll("<p>|</p>|&lt;br&gt;|&lt;|&gt", "");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Content We got :: ");
		System.out.println(content);
		return content;
	}

	/**
	 * 
	 * The following function will take,- arg 1) :: pair of indices arg 2) ::
	 * the content of the doc as String
	 * 
	 * We just return the substring between start and end index
	 * */

	public static String ProcessUnstructured(IndexPairVO titleIndex,
			String doc, String title) {
		// TODO Auto-generated method stub
		String content = doc.substring(titleIndex.getStartIndex(),
				titleIndex.getEndIndex());

		content = content.replace(title, "");
		content = content.replaceAll("<p>|</p>", "");

		boolean nextTag = false;
		nextTag = checkNextTag(doc, titleIndex.getEndIndex() + 1, "<p>");

		String nextTdText = "", nextPText = "";

		if (nextTag) {
			nextPText = getNextPText(doc, titleIndex.getEndIndex());
		}

		nextTdText = getNextTableFirstColumnText(doc, titleIndex.getEndIndex());

		content = content + nextPText + nextTdText;

		return content;
	}

	
	/**
	 * Just a Tag Checking Function The following function takes two arguments,-
	 * 1) The content of the doc as String 2) The index to start parsing
	 * 
	 * The following function will check for whether the next tag is a
	 * <table>
	 * tag or
	 * <p>
	 * tag. If it is a
	 * <p>
	 * tag return True, False, otherwise.
	 * 
	 * */
	private static boolean checkNextTag(String doc, int startIndex,
			String pattern) {
		// TODO Auto-generated method stub
		boolean flag = false;
		int endIndex = 0;
		try {
			for (int i = startIndex;; i++) {
				if (doc.substring(i, i + 1).equals("<")) {
					endIndex = i;
					break;
				}
			}
			System.out.println("Found next P :: "
					+ doc.substring(endIndex, endIndex + 3));
			if ((pattern).equals(doc.substring(endIndex,
					endIndex + pattern.length()))) {
				flag = true;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Returning flag :: " + flag);
		return flag;
	}

	
	/**
	 * The following function takes two arguments,- 1) The content of the doc as
	 * String 2) The index to start parsing
	 * 
	 * O/P :- String as text
	 * 
	 * The algorithm works as follows,-
	 * 
	 * Starting from the end index of the the </p> tag it will search till the
	 * next
	 * <table>
	 * tag. From There it will trace back to last </p> tag. And it will return
	 * everything between the start index (</p> tag index) and this final index.
	 * 
	 * */
	private static String getNextPText(String doc, int startIndex) {

		// TODO Auto-generated method stub
		String pattern1 = "<table";
		int range = pattern1.length();
		int endIndex = 0, lastClosedPTagIndex = 0;
		String content = "";

		try {
			for (int i = startIndex;; i++) {
				if (doc.substring(i, i + range).equals(pattern1)) {
					endIndex = i;
					break;
				}
			}

			String pattern2 = "</p>";
			range = pattern2.length();
			for (int i = endIndex;; i--) {
				if (doc.substring(i, i + range).equals(pattern2)) {
					lastClosedPTagIndex = i;
					break;
				}
			}

			content = doc.substring(startIndex, lastClosedPTagIndex);
			content = content.replaceAll("<p>|</p>", "");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return content;
	}

	
	
	/**
	 * The following function takes two arguments,- 1) The content of the doc as
	 * String 2) The index to start parsing
	 * 
	 * O/P :- It returns output as a String.
	 * 
	 * The algorithm works as follows,-
	 * 
	 * It will search till the next
	 * <table>
	 * tag. Once found it will search the first column of the first row. If that
	 * is empty then it will take the text of the corresponding second column
	 * and return it as text.
	 * 
	 * */

	private static String getNextTableFirstColumnText(String doc, int startIndex) {
		// TODO Auto-generated method stub
		int endIndex = 0;
		String content = "";

		IndexPairVO temp = new IndexPairVO();
		temp.setStartIndex(0);

		try {
			endIndex = getNextIndexOf(doc, startIndex, "<table");
			endIndex = getNextIndexOf(doc, endIndex, "<tbody>");
			endIndex = getNextIndexOf(doc, endIndex, "<tr>");
			temp.setEndIndex(endIndex);
			content = getNextTDText(temp, doc);
			if (("").equalsIgnoreCase(content)) {
				endIndex = getNextIndexOf(doc, endIndex, "</td>");
				temp.setEndIndex(endIndex);
				content = getNextTDText(temp, doc);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content;
	}

	
	
	/**
	 * The following function will take two inputs,-
	 * 
	 * 1) Content of document as String 2) index to start searching from as
	 * integer 3) pattern to search as String
	 * */
	private static int getNextIndexOf(String doc, int startIndex, String pattern) {
		// TODO Auto-generated method stub
		int range = pattern.length();
		int endIndex = 0;
		try {
			for (int i = startIndex;; i++) {
				if (doc.substring(i, i + range).equals(pattern)) {
					endIndex = i;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return endIndex;
	}

	/**
	 * 
	 * The following function will take,- arg 1) :: pair of indices arg 2) ::
	 * the content of the doc as String.
	 * 
	 * O/P : It will return the text under the next
	 * <p>
	 * </p>
	 * tag.
	 * 
	 * */

	public static String GetPTagToTableTagContent(IndexPairVO titleIndex,
			String doc) {
		// TODO Auto-generated method stub

		String content = "";
		
		int pStartIndex = 0, pEndIndex = 0;

		try {

			for (int i = titleIndex.getEndIndex();; i++) {
				if (doc.substring(i, i + 3).equals("<p>")) {
					pStartIndex = i + 3;   // changed from pStartIndex = i + 4;
					break;
				}
			}

			for (int i = pStartIndex;; i++) {
				if (doc.substring(i, i + 4).equals("</p>")) {
					pEndIndex = i;
					break;
				}
			}

			
			content = doc.substring(pStartIndex, pEndIndex);
			content = content.replaceAll("<p>|</p>|&lt;br&gt;|&lt;|&gt", "");
			
			System.out.println("*** "+content);

			boolean nextTag = false;

			while (true) {
				nextTag = checkNextTag(doc, pEndIndex + 1, "<p>");
				String nextPText = "";

				if (nextTag) {
					nextPText = getNextPText(doc, pEndIndex+1);   // nextPText = getNextPText(doc, pEndIndex + 1);
					content = content + nextPText;
				} else {
					nextPText = getNextTableFirstColumnText(doc, pEndIndex);
					content = content + nextPText;
					break;
				}

				IndexPairVO output = getOutputIndex(doc, "<p>", "<p>".length(),
						pEndIndex);
				pEndIndex = output.getEndIndex();

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Content We got :: ");
		System.out.println(content);
		return content;
	}

	
	
	/**
	 * The following function will take two inputs. 1) arg1: A pair of indices
	 * 2) arg2 : content of the document
	 * 
	 * O/P: - It will return the tag name P or TD depending on whether the pair
	 * of indices is just a
	 * <p>
	 * </p>
	 * or it is inside some <td></p> tag
	 * 
	 * */
	public static String checkTagTypeOfTittletext(IndexPairVO titleIndex,
			String doc) {
		// TODO Auto-generated method stub
		int firstIndex = 0, secondIndex = 0;
		String tag = "P";

		System.out.println("==================================");
		System.out.println(doc.substring(titleIndex.getStartIndex(),
				titleIndex.getEndIndex() + 3));

		try {

			for (int i = titleIndex.getEndIndex() + 3;; i++) {
				if (doc.charAt(i) == ('<')) {
					firstIndex = i;
					break;
				}
			}

			for (int i = firstIndex;; i++) {
				if (doc.charAt(i) == ('>')) {
					secondIndex = i;
					break;
				}
			}

			String tagType = doc.substring(firstIndex + 1, secondIndex);
			System.out.println("tagType :: " + tagType);

			if (tagType.equalsIgnoreCase("td")
					|| (tagType.equalsIgnoreCase("/td"))) {
				tag = "TD";
			}

			if (tagType.equalsIgnoreCase("p")
					|| (tagType.equalsIgnoreCase("/p"))) {
				tag = "P";
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tag;
	}

	
	
	/**
	 * The following function will return the nth index of occurrence of a
	 * string(token) in a given content.
	 */
	public static int nthIndexOf(String content, String token, int occurrence) {
		int j = 0;

		try {
			for (int i = 0; i < occurrence; i++) {
				j = content.indexOf(token, j + 1);
				if (j == -1)
					break;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return j;
	}
	
	

	/**
	 * TODO Commenting
	 * This is the only custom code specially designed for PaymentMethod class
	 * 
	 * @param content
	 * @param title
	 * 
	 * */
	public static String CheckPaymentMethodTable(String title, String content) {

		String tableContent = "", paymentTable = "";
		int endIndex = 0;
		int keyIndex = content.indexOf(title);
		

		boolean nextTagFlag = false;

		if (keyIndex != -1) {

			System.out.println(content.charAt(keyIndex));
			System.out.println(content.subSequence(keyIndex,keyIndex + title.length()));
			nextTagFlag = checkNextTag(content, keyIndex + title.length()
					+ "</p>".length(), "<p>");
			
			System.out.println("Next tag flag is :: " + nextTagFlag);

			// If it is a <p> Tag
			if (nextTagFlag) {

				List<String> paymentMethodDictionary = new ArrayList<String>();
				
				// FG MS template words
				paymentMethodDictionary.add("Type");
				paymentMethodDictionary.add("Name");
				paymentMethodDictionary.add("Internal Use Only");
				paymentMethodDictionary.add("Frequency");
				paymentMethodDictionary.add("Date Range");
				paymentMethodDictionary.add("Capitalized?");
				paymentMethodDictionary.add("Amount?");
				
				// FG MP template words
				paymentMethodDictionary.add("Amount?");
				paymentMethodDictionary.add("Status");
				paymentMethodDictionary.add("Owner");
				paymentMethodDictionary.add("Description");
				paymentMethodDictionary.add("Requested Amount");
				paymentMethodDictionary.add("Final Amount");
				paymentMethodDictionary.add("Due Date");
				
				IndexPairVO titleIndex = new IndexPairVO();
				titleIndex.setStartIndex(0);
				titleIndex.setEndIndex(keyIndex + title.length()+ "</p>".length());
				String tempContent =  GetPTagToTableTagContent(titleIndex,
							 content);
				
				System.out.println("tempContent is :: \n "+ tempContent);
				
				for(int  i  = 0; i < paymentMethodDictionary.size(); i++){
					if(tempContent.contains(paymentMethodDictionary.get(i))){
						tableContent = tableContent + paymentMethodDictionary.get(i) + ". ";
					}
				}
				
				System.out.println("tableContent is :: \n "+ tableContent);
				
			} else {
				// if it is a <table> Tag
				for (int i = keyIndex;; i++) {
					if (content.substring(i, i + "</p>".length())
							.equals("</p>")) {
						endIndex = i;
						break;
					}
				}
				// Parsing the entire </p> Tag
				endIndex = endIndex + 4;
				
				int tableEnd = 0;
				for (int i = endIndex;; i++) {
					if (content.substring(i, i + "</table>".length()).equals(
							"</table>")) {
						tableEnd = i;
						break;
					}
				}

				paymentTable = content.substring(endIndex, tableEnd);
				Document doc = Jsoup.parse(paymentTable);

				Element table = doc.select("table").get(0); // select the first
															// table.
				Elements rows = table.select("tr");

				// Parsing only two rows
				for (int i = 0; i < 2; i++) { // first row is the col names so
												// skip it.
					Element row = rows.get(i);
					Elements cols = row.select("td");

					for (int j = 0; j < cols.size(); j++) {
						System.out.println(cols.get(j).text());
						// TODO check for empty field and discard them
						tableContent = tableContent + cols.get(j).text() + ". ";
					}

				}
			}

		}

		return tableContent;
	}

	
	// Demo main
	public static void main(String[] ab) {
		char a = '<';
		String str = "<P> sda sfd sadsa safdsada</p>";

		for (int i = 0;; i++) {
			if (str.charAt(i) == ('>')) {
				System.out.println(i);
				break;
			}
		}

	}

}
