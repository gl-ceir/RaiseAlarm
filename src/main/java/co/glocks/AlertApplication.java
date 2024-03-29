package co.glocks;

//import com.gl.alarm.configuration.MySQLConnection;
import com.gl.alarm.configuration.MySQLConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author maverick
 */
public class AlertApplication {

    static final Logger logger = LogManager.getLogger(AlertApplication.class);

    public static boolean raiseAlert(String alertId, String alertMessage, String alertProcess, int userId) {
        logger.info("Alert " + alertId + ",Alert msg=" + alertMessage + ", Process name = " + alertProcess + ", via Id =" + userId);
        String appdbName = "app";

        try {
            Connection conn = new MySQLConnection().getConnection();
            logger.info("Connection " + conn);
            Statement stmt = conn.createStatement();
            String query = "select description from " + appdbName + ".cfg_feature_alert where alert_id ='" + alertId + "'";
            logger.info("Qyuery " + query);
            ResultSet rs = stmt.executeQuery(query);
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
            return true;
        } catch (Exception e) {
            logger.error("Not able to update  " + e.toString() + " i.e. " + e.getLocalizedMessage());
            return false;
        }
    }
}
