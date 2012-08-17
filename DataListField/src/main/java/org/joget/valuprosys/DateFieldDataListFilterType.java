package org.joget.valuprosys;

import java.util.HashMap;
import java.util.Map;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.datalist.model.DataList;
import org.joget.apps.datalist.model.DataListFilterQueryObject;
import org.joget.apps.datalist.model.DataListFilterTypeDefault;
import org.joget.plugin.base.PluginManager;
import org.joget.workflow.util.WorkflowUtil;

public class DateFieldDataListFilterType extends DataListFilterTypeDefault {

    public String getName() {
        return "Date Field Data List Filter Type";
    }

    public String getVersion() {
        return "3.0.0";
    }

    public String getDescription() {
        return "Data List Filter Type - Date Field";
    }

    public String getLabel() {
        return "Date Field";
    }

    public String getClassName() {
        return this.getClass().getName();
    }

    public String getPropertyOptions() {
        return "";
    }

    public String getTemplate(DataList datalist, String name, String label) {
        PluginManager pluginManager = (PluginManager) AppUtil.getApplicationContext().getBean("pluginManager");
        Map dataModel = new HashMap();
        dataModel.put("name", datalist.getDataListEncodedParamName(DataList.PARAMETER_FILTER_PREFIX+name));
        dataModel.put("label", label);
        dataModel.put("value", getValue(datalist, name));
        dataModel.put("contextPath", WorkflowUtil.getHttpServletRequest().getContextPath());
        return pluginManager.getPluginFreeMarkerTemplate(dataModel, getClassName(), "/templates/dateFieldDataListFilterType.ftl", null);
    }

    public DataListFilterQueryObject getQueryObject(DataList datalist, String name) {
        DataListFilterQueryObject queryObject = new DataListFilterQueryObject();
        if (datalist != null && datalist.getBinder() != null && getValue(datalist, name) != null && !getValue(datalist, name).isEmpty()) {
            String date_from=getValue(datalist,name);
            String [] str_date=date_from.split(";");
            queryObject=getQuery(datalist.getBinder().getColumnName(name),str_date);
            return queryObject;
        }
        return null;
    }

    private DataListFilterQueryObject getQuery(String column_name,String[] condition )
    {
        DataListFilterQueryObject queryObject = new DataListFilterQueryObject();
        
       String querystr="";
       
       if (condition.length==2){
           if (!condition[0].trim().equals("")&& !condition[1].trim().equals(""))
               {
                querystr=column_name + " >= ? and "+   column_name + " <= ?";
                queryObject.setQuery(querystr);
                queryObject.setValues(new String[]{condition[0].trim(),condition[1].trim()});
                 return queryObject;
                }
       }
            if (!condition[0].trim().equals(""))
            {
                 querystr=column_name + " >= ?";
                 queryObject.setQuery(querystr);
                 queryObject.setValues(new String[]{condition[0].trim()});
                 return queryObject;
            }
             if (!condition[1].trim().equals(""))
            {
                 querystr=column_name + " <= ?";
                queryObject.setQuery(querystr);
                 queryObject.setValues(new String[]{condition[1].trim()});
                 return queryObject;
                
            }
       return queryObject;
    }
}
