package org.auscope.portal.core.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;

/**
 * Utility methods for text/string operations.
 * 
 * @author Richard Goh
 */
public class TextUtil {

    /**
     * Test whether a string is null or empty
     * @param s
     *          The string to test
     * @return true if null or empty, false otherwise
     */
    public static boolean isNullOrEmpty(String s) {
        return s == null || s.length() == 0;
    }

    /**
     * Test whether any of the input strings is null or empty
     * @param strings
     *                  The strings to be tested
     * @return true is at lease one of the strings is null or empty
     */
    public static boolean isAnyNullOrEmpty(String... strings) {
        for (String string : strings) {
            if (isNullOrEmpty(string))
                return true;
        }
        return false;
    }

    /**
     * Test whether all of the input strings are null or empty
     * @param strings
     *                  The strings to be tested
     * @return true all of the strings is null or empty
     */
    public static boolean isAllNullOrEmpty(String... strings) {
        for (String string : strings) {
            if (!isNullOrEmpty(string))
                return false;
        }
        return true;
    }

    /**
     * Get the last N lines of multi-line text. Each line will be separated by platform dependant line separator.
     * 
     * @param text
     *            The multi-line text.
     * @param maxLines
     *            The last N lines.
     * @return a string which contains the last N lines of a given multi-line text.
     */
    public static String tail(String text, int maxLines) {
        StringBuffer sb = new StringBuffer(maxLines);

        try {
            List<String> lines = IOUtils.readLines(new StringReader(text));
            int fromIndex = lines.size() - maxLines;
            int toIndex = lines.size();
            for (String line : lines.subList(fromIndex, toIndex)) {
                sb.append(line + System.getProperty("line.separator"));
            }
        } catch (Exception ex) {
            // if failed, return empty string.
        }

        return sb.toString();
    }
    
    /**
     * for some annoying reason, angular param adds double quote to its string parameter. use this to clean it up
     * @param param
     * @return a clean param
     */
    public static String cleanQueryParameter(String param){
    	return param.replace("\"", "");    	
    }

	public static String getStringFromDocument(Document doc)
	{
		try
	    {
	       DOMSource domSource = new DOMSource(doc);
	       StringWriter writer = new StringWriter();
	       StreamResult result = new StreamResult(writer);
	       TransformerFactory tf = TransformerFactory.newInstance();
	       Transformer transformer = tf.newTransformer();
	       transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	       transformer.transform(domSource, result);
	       return writer.toString();
	    }
	    catch(TransformerException ex)
	    {
	       ex.printStackTrace();
	       return null;
	    }		
	}
}