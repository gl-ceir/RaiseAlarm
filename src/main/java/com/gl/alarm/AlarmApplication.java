package com.gl.alarm;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

import com.gl.alarm.configuration.ConnectionConfiguration;
// import com.gl.alarm.configuration.PropertiesReader;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@EnableAsync
@SpringBootConfiguration
@EnableAutoConfiguration
@SpringBootApplication(scanBasePackages = {"com.gl.alarm"})
@EnableEncryptableProperties
public class AlarmApplication {

    static Logger logger = LogManager.getLogger(AlarmApplication.class);
    static String appdbName = "app";

    //  static PropertiesReader propertiesReader = null;
    static ConnectionConfiguration connectionConfiguration = null;
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(AlarmApplication.class, args);
        String alertCode = args[0];
        String alertMessage = args[1];
        String alertProcess = args[2];
        String userId = args[3];
        logger.info("Alert " + alertCode + ",Alert msg=" + alertMessage + ",Process name = " + alertProcess + ", via Id =" + userId);
        //  propertiesReader = (PropertiesReader) context.getBean("propertiesReader");
        //  appdbName = propertiesReader.appdbName;
        connectionConfiguration = (ConnectionConfiguration) context.getBean("connectionConfiguration");
        Map<String, String> placeholderMapForAlert = new HashMap<>();
        placeholderMapForAlert.put("<e>", alertMessage);
        placeholderMapForAlert.put("<process_name>", alertProcess);
        raiseAlert(alertCode, placeholderMapForAlert, userId);
        logger.error("Alert " + alertCode + " is raised. So, doing nothing.");
        System.exit(0);
    }


    public static void raiseAlert(String alertId, Map<String, String> bodyPlaceHolderMap, String userId) {
        try (Connection conn = connectionConfiguration.getConnection(); Statement stmt = conn.createStatement();) {
            String alertDescription = getAlertbyId(alertId);
            if (Objects.nonNull(bodyPlaceHolderMap)) {
                for (Map.Entry<String, String> entry
                        : bodyPlaceHolderMap.entrySet()) {
                    logger.info("Placeholder key : " + entry.getKey() + " value : " + entry.getValue());
                    alertDescription = alertDescription.replaceAll(entry.getKey(), entry.getValue());
                }
            }
            logger.info("alert message: " + alertDescription);
            String sql = "Insert into " + appdbName + ".sys_generated_alert (alert_id,created_on,modified_on,description,status,user_id)"
                    + "values('" + alertId + "',now(), now() ,'" + alertDescription
                    + "',0," + userId + ")";
            logger.info("Inserting alert  [" + sql + "]");
            stmt.executeUpdate(sql);

        } catch (Exception e) {
            //	raiseAlert("alert006", Map.of("<e>", e.toString(), "<process_name>", "Alarm.jar"), 0);
            logger.error("Not able to update  " + e.toString() + " i.e. " + e.getLocalizedMessage());
        } finally {
            System.exit(0);
        }
    }

    public static String getAlertbyId(String alertId) {
        String description = "";
        try (Connection conn = connectionConfiguration.getConnection(); Statement stmt = conn.createStatement();) {
            String sql = "select description from " + appdbName + ".cfg_feature_alert where alert_id ='" + alertId + "'";
            logger.info("Fetching alert via[" + sql + "]");
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                description = rs.getString("description");
            }
            if (description == null || description.equals("")) {
                logger.info("No description found");
            }
        } catch (Exception e) {
            raiseAlert("alert006", Map.of("<e>", e.toString(), "<process_name>", "Alarm.jar"), "0");
            logger.error("No able to update  " + e.toString() + " i.e. " + e.getLocalizedMessage());
        } finally {
            return description;
        }
    }

    public static void raiseAlertnew(String alertId, String alertMessage, String alertProcess, int userId) {
        //ConnectionConfiguration is used to create jdbc connection. Dev can change this as per code
        String appdbName = "app";
        try (Connection conn = (Connection) connectionConfiguration.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("select description from " + appdbName + ".cfg_feature_alert where alert_id ='" + alertId + "'");) {
            String alertDescription = null;
            while (rs.next()) {
                alertDescription = rs.getString("description")
                        .replaceAll("<e>", alertMessage)
                        .replaceAll("<process_name>", alertProcess);
            }
            String sql = "Insert into " + appdbName + ".sys_generated_alert (alert_id,created_on,modified_on,description,user_id)"
                    + "values('" + alertId + "',now(), now() ,'" + alertDescription + "'," + userId + ")";
            logger.info("Inserting alert  [" + sql + "]");
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            logger.error("Not able to update  " + e.toString() + " i.e. " + e.getLocalizedMessage());
        }
    }
}
