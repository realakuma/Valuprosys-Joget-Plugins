/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joget.valuprosys.mobile;

/**
 *
 * @author realakuma
 */
public class StringUtils {

	public static String defaultIfEmpty(String str, String defaultString) {
		if (str == null) {
			return defaultString;
		}
		if (str.trim().length() == 0) {
			return "";
		}
		if (str.trim().equals("null")){
			return "";
			
		}

		return str;
	}

	public static String defaultIfEmpty(String str) {
		return defaultIfEmpty(str,"");
	}
}