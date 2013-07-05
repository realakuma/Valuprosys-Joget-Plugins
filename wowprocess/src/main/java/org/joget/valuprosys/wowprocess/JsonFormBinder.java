package org.joget.valuprosys.wowprocess;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormBinder;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormLoadBinder;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.apps.form.model.FormStoreBinder;
import org.joget.plugin.property.service.PropertyUtil;
import org.joget.workflow.util.WorkflowUtil;

public class JsonFormBinder extends FormBinder
  implements FormLoadBinder, FormStoreBinder
{
  public String getName()
  {
    return "Json Form Binder";
  }

  public String getVersion()
  {
    return "3.0.0";
  }

  public String getDescription()
  {
    return "Json Form Binder";
  }

  public String getClassName()
  {
    return getClass().getName();
  }

  public String getLabel()
  {
    return "JSON Form Binder";
  }

  public String getPropertyOptions()
  {
    return "";
  }

  public FormRowSet load(Element element, String primaryKey, FormData formData)
  {
    FormRowSet results = null;

    HttpServletRequest request = WorkflowUtil.getHttpServletRequest();
    String jsonFormData = request.getParameter("_jsonFormData");

    if ((jsonFormData != null) && (!jsonFormData.isEmpty())) {
      results = new FormRowSet();
      results.setMultiRow(false);

      FormRow row = new FormRow();
      Map rowData = PropertyUtil.getPropertiesValueFromJson(jsonFormData);
      rowData = convertTempFilePathToValue(rowData);
      row.setCustomProperties(rowData);

      if (((formData.getPrimaryKeyValue() == null) || (formData.getPrimaryKeyValue().isEmpty())) && (row.getId() != null) && (!row.getId().isEmpty())) {
        formData.setPrimaryKeyValue(row.getId());
      }

      results.add(row);
    }

    return results;
  }

  private Map convertTempFilePathToValue(Map rowData) {
    if (rowData.containsKey("_tempFilePathMap")) {
      Map tempFilePathMap = (Map)rowData.get("_tempFilePathMap");
      rowData.putAll(tempFilePathMap);
      rowData.remove("_tempFilePathMap");
    }
    return rowData;
  }

  public FormRowSet store(Element element, FormRowSet rows, FormData formData)
  {
    return rows;
  }
}