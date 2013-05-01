/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joget.valuprosys.mobile;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormBinder;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.apps.form.model.FormValidator;
import org.joget.apps.form.service.FormUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author akuma
 */
public class JsonUtil {

    /**
     * Generates JSON representing an element.
     *
     * @param element
     * @return
     */
    public static String generateElementJson(Element element) throws Exception {
        JSONObject jsonObject = JsonUtil.generateElementJsonObject(element);
        String json = jsonObject.toString();
        return json;
    }

    /**
     * Generates a JSONObject to represent an element
     *
     * @param element
     * @return
     * @throws Exception
     */
    public static JSONObject generateElementJsonObject(Element element) throws Exception {
        JSONObject jsonObj = new JSONObject();

        // set class name
        jsonObj.put(FormUtil.PROPERTY_CLASS_NAME, element.getClassName());

        // set properties
        JSONObject jsonProps = JsonUtil.generatePropertyJsonObject(element.getProperties());
        jsonObj.put(FormUtil.PROPERTY_PROPERTIES, jsonProps);

        // set validator
        FormValidator validator = element.getValidator();
        if (validator != null) {
            JSONObject jsonValidatorProps = JsonUtil.generatePropertyJsonObject(validator.getProperties());
            JSONObject jsonValidator = new JSONObject();
            jsonValidator.put(FormUtil.PROPERTY_CLASS_NAME, validator.getClassName());
            jsonValidator.put(FormUtil.PROPERTY_PROPERTIES, jsonValidatorProps);
            jsonProps.put(FormUtil.PROPERTY_VALIDATOR, jsonValidator);
        }

        // set load binder
        FormBinder loadBinder = (FormBinder) element.getLoadBinder();
        if (loadBinder != null) {
             JSONObject jsonLoadBinderProps = JsonUtil.generatePropertyJsonObject(loadBinder.getProperties());
             JSONObject jsonLoadBinder = new JSONObject();
             jsonLoadBinder.put(FormUtil.PROPERTY_CLASS_NAME, loadBinder.getClassName());
             jsonLoadBinder.put(FormUtil.PROPERTY_PROPERTIES, jsonLoadBinderProps);
             jsonProps.put(FormBinder.FORM_LOAD_BINDER, jsonLoadBinder);
             

        }

        // set store binder
        FormBinder storeBinder = (FormBinder) element.getStoreBinder();
        if (storeBinder != null) {
             JSONObject jsonStoreBinderProps = JsonUtil.generatePropertyJsonObject(storeBinder.getProperties());
             JSONObject jsonStoreBinder = new JSONObject();
             jsonStoreBinder.put(FormUtil.PROPERTY_CLASS_NAME, storeBinder.getClassName());
             jsonStoreBinder.put(FormUtil.PROPERTY_PROPERTIES, jsonStoreBinderProps);
             jsonProps.put(FormBinder.FORM_STORE_BINDER, jsonStoreBinder);
             

        }

        // set options binder
        FormBinder optionsBinder = (FormBinder) element.getOptionsBinder();
        if (optionsBinder != null) {
             JSONObject jsonOptionsBinderProps = JsonUtil.generatePropertyJsonObject(optionsBinder.getProperties());
             JSONObject jsonOptionsBinder = new JSONObject();
             jsonOptionsBinder.put(FormUtil.PROPERTY_CLASS_NAME, optionsBinder.getClassName());
             jsonOptionsBinder.put(FormUtil.PROPERTY_PROPERTIES, jsonOptionsBinderProps);
             jsonProps.put(FormBinder.FORM_OPTIONS_BINDER, jsonOptionsBinder);
            

        }


        // set child elements
        JSONArray jsonChildren = new JSONArray();
        Collection<Element> children = element.getChildren();
        if (children != null) {
            for (Element child : children) {
                JSONObject childJson = JsonUtil.generateElementJsonObject(child);
               // if (!child.getClassName().toString().equals("org.joget.apps.form.model.Column")) {
                    jsonChildren.put(childJson);
                //}
            }
        }
        jsonObj.put(FormUtil.PROPERTY_ELEMENTS, jsonChildren);

        return jsonObj;
    }

    /**
     * Generates a JSONObject to represent the properties of an element
     *
     * @return
     */
    public static JSONObject generatePropertyJsonObject(Map<String, Object> properties) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        if (properties != null) {
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                String propertyName = entry.getKey();
                Object objValue = entry.getValue();
                if (objValue != null && objValue instanceof FormRowSet) {
                    JSONArray jsonArray = new JSONArray();
                    for (FormRow row : (FormRowSet) objValue) {
                        Set<String> props = row.stringPropertyNames();
                        JSONObject jo = new JSONObject();
                        for (String key : props) {
                            String val = row.getProperty(key);
                            jo.accumulate(key, val);
                        }
                        jsonArray.put(jo);
                    }
                    jsonObject.put(propertyName, jsonArray);
                } else if (objValue != null && objValue instanceof Object[]) {
                    Object[] mapArray = (Object[]) objValue;
                    JSONArray jsonArray = new JSONArray();
                    for (Object row : mapArray) {
                        Map m = (Map) row;
                        JSONObject jo = new JSONObject(m);
                        jsonArray.put(jo);
                    }
                    jsonObject.put(propertyName, jsonArray);
                } else if (objValue != null && objValue instanceof Map) {
                    jsonObject.put(propertyName, (Map) objValue);
                } else {
                    String value = (objValue != null) ? objValue.toString() : "";
                    jsonObject.accumulate(propertyName, value);
                }
            }
        }
        return jsonObject;
    }
}
