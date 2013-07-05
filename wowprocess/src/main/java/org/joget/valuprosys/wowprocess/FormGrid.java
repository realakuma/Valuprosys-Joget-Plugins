package org.joget.valuprosys.wowprocess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.lang.StringEscapeUtils;
import org.joget.apps.app.dao.FormDefinitionDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.FormDefinition;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.lib.Grid;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.Form;
import org.joget.apps.form.model.FormBuilderPaletteElement;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.apps.form.service.FormService;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.util.StringUtil;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.service.WorkflowManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

public class FormGrid extends Element
  implements FormBuilderPaletteElement
{
  private FormRowSet cachedRowSet;

  public String getName()
  {
    return "Form Grid";
  }

  public String getVersion()
  {
    return "3.0.0";
  }

  public String getDescription()
  {
    return "Form Grid Element";
  }

  public String getClassName()
  {
    return getClass().getName();
  }

  public String getLabel()
  {
    return "Form Grid";
  }

  public String getPropertyOptions()
  {
    String formDefField = null;
    AppDefinition appDef = AppUtil.getCurrentAppDefinition();
    if (appDef != null) {
      String formJsonUrl = "[CONTEXT_PATH]/web/json/console/app/" + appDef.getId() + "/" + appDef.getVersion() + "/forms/options";
      formDefField = "{name:'formDefId',label:'@@form.formgrid.formId@@',type:'selectbox',options_ajax:'" + formJsonUrl + "',required : 'True'}";
    } else {
      formDefField = "{name:'formDefId',label:'@@form.formgrid.formId@@',type:'textfield',required : 'True'}";
    }
    Object[] arguments = { formDefField };
    return AppUtil.readPluginResource(getClass().getName(), "/properties/form/formGrid.json", arguments, true, "message/form/FormGrid");
  }

  public String getFormBuilderCategory()
  {
    return "Enterprise";
  }

  public int getFormBuilderPosition()
  {
    return 1200;
  }

  public String getFormBuilderIcon()
  {
    return "/plugin/org.joget.apps.form.lib.Grid/images/grid_icon.gif";
  }

  public String getFormBuilderTemplate()
  {
    return "<label class='label'>Grid</label><table cellspacing='0'><tr><th>Header</th><th>Header</th></tr><tr><td>Cell</td><td>Cell</td></tr></table>";
  }

  public String renderTemplate(FormData formData, Map dataModel)
  {
    String template = "formGrid.ftl";

    String decoration = FormUtil.getElementValidatorDecoration(this, formData);
    dataModel.put("decoration", decoration);

    Map headers = getHeaderMap(formData);
    dataModel.put("headers", headers);

    FormRowSet rows = getRows(formData);
/*
    if ((getPropertyString("enableSorting") != null) && (getPropertyString("enableSorting").equals("true")) && (getPropertyString("sortField") != null) && (!getPropertyString("sortField").isEmpty()))
    {
      final String sortField = getPropertyString("sortField");
      Collections.sort(rows, new Comparator() {
        public int compare(FormRow row1, FormRow row2) {
          String number1 = row1.getProperty(sortField);
          String number2 = row2.getProperty(sortField);

          if ((number1 != null) && (number2 != null))
            try {
              return Integer.parseInt(number1) - Integer.parseInt(number2);
            }
            catch (Exception e)
            {
            }
          return 0;
        }
      });
    }
*/
    dataModel.put("rows", rows);

    if ("true".equals(getPropertyString("readonly")))
      dataModel.put("buttonLabel", getPropertyString("submit-label-readonly"));
    else {
      dataModel.put("buttonLabel", getPropertyString("submit-label-normal"));
    }

    dataModel.put("json", StringEscapeUtils.escapeHtml(getSelectedFormJson(formData)));

    dataModel.put("customDecorator", getDecorator());

    AppDefinition appDef = AppUtil.getCurrentAppDefinition();
    dataModel.put("appId", appDef.getAppId());
    dataModel.put("appVersion", appDef.getVersion());

    String html = FormUtil.generateElementHtml(this, formData, template, dataModel);
    return html;
  }

  public FormRowSet formatData(FormData formData)
  {
    FormRowSet rowSet = getRows(formData);
    rowSet.setMultiRow(true);
    try
    {
      rowSet = convertJsonToFormRowSet(rowSet);
    } catch (JSONException ex) {
      Logger.getLogger(Grid.class.getName()).log(Level.SEVERE, null, ex);
    }

    return rowSet;
  }

  protected Map<String, Map> getHeaderMap(FormData formData)
  {
    Map headerMap = new ListOrderedMap();
    Object optionProperty = getProperty("options");
    Iterator i$;
    if ((optionProperty != null) && ((optionProperty instanceof Collection))) {
      for (i$ = ((ArrayList)optionProperty).iterator(); i$.hasNext(); ) { Object opt = i$.next();
        Map optMap = (Map)opt;
        Object value = optMap.get("value");
        if (value != null) {
          headerMap.put(value.toString(), optMap);
        }
      }
    }
    return headerMap;
  }

  protected FormRowSet getRows(FormData formData)
  {
    if (this.cachedRowSet == null) {
      String id = getPropertyString("id");
      String param = FormUtil.getElementParameterName(this);
      FormRowSet rowSet = new FormRowSet();

      String json = getPropertyString("value");
      if ((json != null) && (!json.isEmpty())) {
        try {
          rowSet = parseFormRowSetFromJson(json);
        } catch (Exception ex) {
          Logger.getLogger(Grid.class.getName()).log(Level.SEVERE, "Error parsing grid JSON", ex);
        }

      }

      boolean continueLoop = true;
      int i = 0;
      while (continueLoop) {
        FormRow row = new FormRow();
        String paramName = param + "_jsonrow_" + i;
        String paramValue = formData.getRequestParameter(paramName);
        if (paramValue != null) {
          row.setProperty("jsonrow", paramValue);
          try {
            row.putAll(convertJsonToFormRow(paramValue));
          } catch (Exception ex) {
            Logger.getLogger(Grid.class.getName()).log(Level.SEVERE, "Error parsing grid JSON", ex);
          }
        }
        if (!row.isEmpty()) {
          if (i == 0)
          {
            rowSet = new FormRowSet();
          }
          rowSet.add(row);
        }
        else {
          continueLoop = false;
        }
        i++;
      }

      if (!FormUtil.isFormSubmitted(this, formData))
      {
        FormRowSet binderRowSet = formData.getLoadBinderData(this);
        if (binderRowSet != null) {
          if (!binderRowSet.isMultiRow())
          {
            if (!binderRowSet.isEmpty()) {
              FormRow row = (FormRow)binderRowSet.get(0);
              String jsonValue = row.getProperty(id);
              try {
                rowSet = parseFormRowSetFromJson(jsonValue);
              } catch (Exception ex) {
                Logger.getLogger(Grid.class.getName()).log(Level.SEVERE, "Error parsing grid JSON", ex);
              }
            }
          }
          else {
            try {
              rowSet = convertFormRowToJson(binderRowSet);
            } catch (Exception ex) {
              Logger.getLogger(Grid.class.getName()).log(Level.SEVERE, "Error convert grid form row to JSON", ex);
            }
          }
        }

      }

      this.cachedRowSet = rowSet;
    }

    return this.cachedRowSet;
  }

  protected FormRowSet parseFormRowSetFromJson(String json)
    throws JSONException
  {
    FormRowSet rowSet = new FormRowSet();
    rowSet.setMultiRow(true);

    if ((json != null) && (json.trim().length() > 0))
    {
      JSONArray jsonArray = new JSONArray(json);
      for (int i = 0; i < jsonArray.length(); i++) {
        JSONObject jsonRow = (JSONObject)jsonArray.get(i);

        FormRow row = new FormRow();
        JSONArray fields = jsonRow.names();
        if ((fields != null) && (fields.length() > 0)) {
          for (int k = 0; k < fields.length(); k++) {
            String fieldName = fields.getString(k);
            String value = jsonRow.getString(fieldName);
            row.setProperty(fieldName, value);
          }
        }
        row.setProperty("jsonrow", jsonRow.toString());
        rowSet.add(row);
      }
    }
    return rowSet;
  }

  protected FormRowSet convertFormRowToJson(FormRowSet oriRowSet) throws JSONException {
    FormRowSet rowSet = new FormRowSet();
    rowSet.setMultiRow(true);

    for (FormRow row : oriRowSet) {
      JSONObject jsonObject = new JSONObject();
      FormRow newRow = new FormRow();
      for (Map.Entry entry : row.entrySet()) {
        String key = (String)entry.getKey();
        String value = entry.getValue().toString();
        jsonObject.put(key, value);
        newRow.setProperty(key, value);
      }
      newRow.setProperty("jsonrow", jsonObject.toString());
      rowSet.add(newRow);
    }
    return rowSet;
  }

  protected FormRowSet convertJsonToFormRowSet(FormRowSet oriRowSet) throws JSONException {
    FormRowSet rowSet = new FormRowSet();
    rowSet.setMultiRow(true);

    int i = 0;
    for (FormRow row : oriRowSet) {
      FormRow newRow = convertJsonToFormRow(row.get("jsonrow").toString());

      if ((getPropertyString("enableSorting") != null) && (getPropertyString("enableSorting").equals("true")) && (getPropertyString("sortField") != null) && (!getPropertyString("sortField").isEmpty())) {
        newRow.put(getPropertyString("sortField"), Integer.toString(i));
      }

      rowSet.add(newRow);
      i++;
    }
    return rowSet;
  }

  protected FormRow convertJsonToFormRow(String json) throws JSONException {
    JSONObject jsonObject = new JSONObject(json);
    FormRow newRow = new FormRow();
    JSONArray fields = jsonObject.names();
    if ((fields != null) && (fields.length() > 0)) {
      for (int k = 0; k < fields.length(); k++) {
        String fieldName = fields.getString(k);
        if (fieldName.equals("_tempFilePathMap")) {
          JSONObject tempFilePathMap = jsonObject.getJSONObject("_tempFilePathMap");
          JSONArray tempFilePaths = tempFilePathMap.names();
          if ((tempFilePaths != null) && (tempFilePaths.length() > 0))
            for (int l = 0; l < tempFilePaths.length(); l++) {
              String tempFilePathFieldId = tempFilePaths.getString(l);
              String tempFilePath = tempFilePathMap.getString(tempFilePathFieldId);
              newRow.putTempFilePath(tempFilePathFieldId, tempFilePath);
            }
        }
        else {
          String value = jsonObject.getString(fieldName);
          newRow.setProperty(fieldName, value);
        }
      }
    }
    return newRow;
  }

  protected String getSelectedFormJson(FormData parentFormData) {
    String formDefId = getPropertyString("formDefId");
    Form form = null;
    AppDefinition appDef = AppUtil.getCurrentAppDefinition();
    if ((appDef != null) && (formDefId != null)) {
      FormDefinitionDao formDefinitionDao = (FormDefinitionDao)AppUtil.getApplicationContext().getBean("formDefinitionDao");
      FormService formService = (FormService)AppUtil.getApplicationContext().getBean("formService");
      FormDefinition formDef = formDefinitionDao.loadById(formDefId, appDef);

      FormData formData = new FormData();
      String json = formDef.getJson();

      if ((parentFormData.getProcessId() != null) && (!parentFormData.getProcessId().isEmpty())) {
        formData.setProcessId(parentFormData.getProcessId());
        WorkflowManager wm = (WorkflowManager)AppUtil.getApplicationContext().getBean("workflowManager");
        WorkflowAssignment wfAssignment = wm.getAssignmentByProcess(parentFormData.getProcessId());
        json = AppUtil.processHashVariable(json, wfAssignment, StringUtil.TYPE_JSON, null);
      }

      form = formService.loadFormFromJson(json, formData);
      form.setStoreBinder(new JsonFormBinder());
      form.setLoadBinder(new JsonFormBinder());

      Boolean readonly = Boolean.valueOf("true".equalsIgnoreCase(getPropertyString("readonly")));
      Boolean readonlyLabel = Boolean.valueOf("true".equalsIgnoreCase(getPropertyString("readonlyLabel")));
      if ((readonly.booleanValue()) || (readonlyLabel.booleanValue())) {
        FormUtil.setReadOnlyProperty(form, readonly, readonlyLabel);
      }

      return formService.generateElementJson(form);
    }
    return "";
  }

  public Boolean selfValidate(FormData formData)
  {
    Boolean valid = Boolean.valueOf(true);

    FormRowSet rowSet = getRows(formData);
    String id = FormUtil.getElementParameterName(this);
    String errorMsg = getPropertyString("errorMessage");

    String min = getPropertyString("validateMinRow");
    if ((min != null) && (!min.isEmpty()))
      try {
        int minNumber = Integer.parseInt(min);
        if (rowSet.size() < minNumber)
          valid = Boolean.valueOf(false);
      }
      catch (Exception e)
      {
      }
    String max = getPropertyString("validateMaxRow");
    if ((max != null) && (!max.isEmpty()))
      try {
        int maxNumber = Integer.parseInt(max);
        if (rowSet.size() > maxNumber)
          valid = Boolean.valueOf(false);
      }
      catch (Exception e)
      {
      }
    if (!valid.booleanValue()) {
      formData.addFormError(id, errorMsg);
    }

    return valid;
  }

  protected String getDecorator() {
    String decorator = "";
    try
    {
      String min = getPropertyString("validateMinRow");
      String max = getPropertyString("validateMaxRow");

      if (((min != null) && (!min.isEmpty())) || ((max != null) && (!max.isEmpty())))
        decorator = "*";
    }
    catch (Exception e) {
    }
    return decorator;
  }
  

}