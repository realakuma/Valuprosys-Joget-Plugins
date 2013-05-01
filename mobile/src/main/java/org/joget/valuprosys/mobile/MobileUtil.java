/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joget.valuprosys.mobile;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author akuma
 */
public class MobileUtil {
    
       /**
     * Read form's Approve INFO from request parameters and populate into a Map
     * @param request,prefix
     * @return
     */
    public static Map<String, String> retrieveApproveINFOFromRequest(HttpServletRequest request,String prefix) {
        Map<String, String> variables = new HashMap<String, String>();

        if (request != null) {
            Enumeration<String> enumeration = request.getParameterNames();
            //loop through all parameters to get the workflow variables
            while (enumeration.hasMoreElements()) {
                String paramName = enumeration.nextElement();
                if (paramName.startsWith(prefix)) {
                    variables.put(paramName, request.getParameter(paramName));
                }
            }
        }
        return variables;
    }
    
}
