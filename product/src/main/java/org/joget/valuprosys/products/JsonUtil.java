package org.joget.valuprosys.products;

import java.sql.ResultSet;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
        
        
   
public class JsonUtil {         
        
       
     public static String extractJSONArray(ResultSet rs) throws SQLException,JSONException {  
     ResultSetMetaData md = rs.getMetaData();  
     int num = md.getColumnCount();  
     JSONArray array = new JSONArray();  
     while (rs.next()) {  
     JSONObject mapOfColValues = new JSONObject();  
     for (int i = 1; i <= num; i++) {  
     mapOfColValues.put(md.getColumnName(i), rs.getObject(i));  
     }  
     
     array.put(mapOfColValues);  
     }  
     return array.toString();  
     }
     
}