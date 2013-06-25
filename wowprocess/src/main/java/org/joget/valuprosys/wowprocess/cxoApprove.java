/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joget.valuprosys.wowprocess;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.log4j.Level;
import org.joget.apps.app.service.AppUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.plugin.base.PluginManager;
import org.joget.workflow.model.service.WorkflowManager;
import java.sql.Timestamp;
import org.joget.workflow.model.WorkflowAssignment;

/**
 *
 * @author realakuma
 */
public class cxoApprove extends DefaultApplicationPlugin {

    Connection con = null;

    @Override
    public Object execute(Map props) {
        PluginManager pluginManager = (PluginManager) props.get("pluginManager");
        WorkflowManager wm = (WorkflowManager) pluginManager.getBean("workflowManager");
        WorkflowAssignment wfAssignment = (WorkflowAssignment) props.get("workflowAssignment");
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try {
            // retrieve connection from the default datasource
            DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
            con = ds.getConnection();
            // execute SQL query
            if (!con.isClosed()) {
                String applyunit = "";
                String username ="";
                String lastPermissionPerson = "";
                String lastPermissionStatus = "";
                String lastPermissionDate = "";
                String lastPermissionComment = "";
                String nextPerson = "director";
                String sql = "select * from app_fd_wowprime_leavefrom where id ='" + wfAssignment.getProcessId() + "'";
                
                stmt = con.prepareStatement(sql);
                rs = stmt.executeQuery();

/*
                if (rs.next()) {
                    applyunit = rs.getString("c_applyunit");
                    lastPermissionPerson = rs.getString("c_wowprime_leave_manager_approver").split("\\(")[0];
                    lastPermissionStatus = rs.getString("c_wowprime_leave_manager_managerStatus");
                    lastPermissionDate = rs.getString("c_wowprime_leave_manager_time");
                    lastPermissionComment = rs.getString("c_wowprime_leave_manager_comment");
                }
                isAgent(lastPermissionPerson, "请假", "manager");
                username = changeUsernameToAgent(nextPermissionPerson(changePermissionToChinese(nextPerson), "请假", applyunit), "请假", nextPerson);
                if (username.equals(lastPermissionPerson)) {
                    saveNextPermissionData(username, lastPermissionDate, lastPermissionStatus, lastPermissionComment);
                    wm.activityVariable(wfAssignment.getActivityId(), "isPass", "yes");
                } else {
                    wm.activityVariable(wfAssignment.getActivityId(), "isPass", "no");
                }*/
                 wm.activityVariable(wfAssignment.getActivityId(), "isPass", "yes");
                wm.activityVariable(wfAssignment.getActivityId(), "username", "admin");
            }
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
        } finally {
            try {
                if (!con.isClosed()) {
                    rs.close();
                    stmt.close();
                    con.close();

                }
            } catch (SQLException ex) {
                Logger.getLogger(cxoApprove.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }

        }


        return null;

    }

    public static String md5(String str) {
        String s = str;
        if (s == null) {
            return "";
        } else {
            String value = null;
            MessageDigest md5 = null;
            try {
                md5 = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException ex) {
                /*Logger.getLogger(ToolFunction.class.getName()).log(Level.WARN,
                null, ex);
                 * 
                 */
            }
            sun.misc.BASE64Encoder baseEncoder = new sun.misc.BASE64Encoder();
            try {
                value = baseEncoder.encode(md5.digest(s.getBytes("utf-8")));
            } catch (Exception ex) {
            }
            return value;
        }
    }

    public String changePermissionToChinese(String job) {
        if (job.equals("manager")) {
            return "经理";
        } else if (job.equals("director")) {
            return "总监";
        } else if (job.equals("vice-generalManager")) {
            return "副总经理";
        } else if (job.equals("generalManager")) {
            return "总经理";
        } else if (job.equals("excutivechairman")) {
            return "执行长";
        } else if (job.equals("chairman")) {
            return "主席";
        } else {
            return "经理";
        }
    }

    public String nextPermissionPerson(String job, String type, String applyDepartment) throws SQLException {
        String sql = "select c_username,c_dayPermission,c_addPermission,c_productPermission,c_expensePermission from app_fd_wowprime_job_mapping_departm where  c_department='" + applyDepartment + "' and c_job in (select id from app_fd_wowprime_approval_permission where c_job='" + job + "')";
        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        String username = "";
        if (type.equals("请假")) {
            while (rs.next()) {
                if (rs.getString("c_dayPermission").equals("true")) {
                    username += rs.getString("c_username") + ";";
                }
            }
        } else if (type.equals("加班")) {
            while (rs.next()) {
                if (rs.getString("c_addPermission").equals("true")) {
                    username += rs.getString("c_username") + ";";
                }
            }
        } else if (type.equals("费用")) {
            while (rs.next()) {
                if (rs.getString("c_expensePermission").equals("true")) {
                    username += rs.getString("c_username") + ";";
                }
            }
        } else if (type.equals("采购")) {
            while (rs.next()) {
                if (rs.getString("c_productPermission").equals("true")) {
                    username += rs.getString("c_username") + ";";
                }
            }
        }
        return username;
    }

    public Boolean isInDate(String startDate, String endDate, String date) {
        String[] startDateData = startDate.split("-");
        String[] endDateData = endDate.split("-");
        String[] dateData = date.split("-");
        int[] startDataInt = new int[3];
        int[] endDateInt = new int[3];
        int[] dateInt = new int[3];
        for (int i = 0; i < 3; i++) {
            startDataInt[i] = Integer.parseInt(startDateData[i]);
            endDateInt[i] = Integer.parseInt(endDateData[i]);
            dateInt[i] = Integer.parseInt(dateData[i]);
        }

        if (startDataInt[0] <= dateInt[0] && endDateInt[0] >= dateInt[0]) {
            if (startDataInt[1] <= dateInt[1] && endDateInt[1] >= dateInt[1]) {
                if (startDataInt[2] <= dateInt[2] && endDateInt[2] >= dateInt[2]) {
                    return true;
                }
            }
        }
        return false;
    }

    public void saveAgent(String username, String type, String job) {
        try {
            Date now = new Date();
            SimpleDateFormat format = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            Timestamp timestampCreated = new Timestamp(now.getTime());
            Timestamp timestampModified = new Timestamp(now.getTime());
            String time = format.format(now);
            String id = "ttpc-" + md5(time + Math.random());
            //String id = "ttpc-" + md5(time + Math.random() + i);
            String sql = "insert app_fd_wowprime_save_agent (`id`, `dateCreated`, `dateModified`, `c_type`, `c_agent`, `c_job`, `c_isUse`, `c_processid`) VALUES (?,?,?,?,?,?,?,?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, id);
            stmt.setString(2, timestampCreated.toString());
            stmt.setString(3, timestampModified.toString());
            stmt.setString(4, type);
            stmt.setString(5, username);
            stmt.setString(6, job);
            stmt.setString(7, "false");
            stmt.setString(8, "#assignment.processId#");
            stmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
        }
    }

    public String changeUsernameToAgent(String username, String type, String job) throws SQLException {
        String currentDate = "#date.yyyy-MM-dd#";
        String[] usernames = username.split(";");
        for (int i = 0; i < usernames.length; i++) {
            String sql = "select c_startDate,c_newUsername,c_endDate from app_fd_wowprime_agent_permission where  c_oldUsername='" + usernames[i] + "'";
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                if (isInDate(rs.getString("c_startDate"), rs.getString("c_endDate"), currentDate)) {
                    usernames[i] = rs.getString("c_newUsername");
                    saveAgent(usernames[i], type, job);
                }
            }
        }
        username = "";
        for (int i = 0; i < usernames.length; i++) {
            username += usernames[i];
            if (i != usernames.length - 1) {
                username += ";";
            }
        }
        return username;
    }

    public boolean isAgent(String username, String type, String job) throws SQLException {
        String sql = "select id from app_fd_wowprime_save_agent where  c_processid=? and c_agent=? and c_type=? and c_job=?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setString(1, "#assignment.processId#");
        stmt.setString(2, username);
        stmt.setString(3, type);
        stmt.setString(4, job);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            sql = "update app_fd_wowprime_leavefrom set c_wowprime_leave_manager_approver=? where id ='#assignment.processId#'";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, username + "(代理人)");
            stmt.executeUpdate();

            sql = "update app_fd_wowprime_save_agent set c_isUse='true' where id =?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, rs.getString("id"));
            stmt.executeUpdate();
            return true;
        }
        return false;
    }

    public boolean saveNextPermissionData(String username, String date, String status, String comment) {
        try {
            String sql = "update app_fd_wowprime_leavefrom set c_wowprime_leave_director_approver=?,c_wowprime_leave_director_directorStatus=?,c_wowprime_leave_director_comment=?,c_wowprime_leave_director_time=? where id ='#assignment.processId#'";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, username + "(代理人)");
            stmt.setString(2, status);
            stmt.setString(3, comment);
            stmt.setString(4, date);
            stmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
            return false;
        }
        return true;
    }

    public String getName() {
        return "cxo approvment Tool";
    }

    public String getVersion() {
        return "3.1.0";
    }

    public String getDescription() {
        return "cxo approvment Tool";
    }

    public String getLabel() {
        return "cxo approvment Tool";
    }

    public String getClassName() {
        return getClass().getName();
    }

    public String getPropertyOptions() {
        return AppUtil.readPluginResource(getClass().getName(), "", null, true, "");
    }
}