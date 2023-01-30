package com.gl.alarm;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

import com.gl.alarm.configuration.ConnectionConfiguration;
import com.gl.alarm.configuration.PropertiesReader;


@EnableAsync
@SpringBootConfiguration
@EnableAutoConfiguration
@SpringBootApplication(scanBasePackages = {"com.gl.alarm"})
public class AlarmApplication {
	private static final Logger logger = Logger.getLogger(AlarmApplication.class);
    static PropertiesReader propertiesReader = null;
	static ConnectionConfiguration connectionConfiguration = null;
	  
	public static void main(String[] args) {
		String alertCode=args[0];
		String alertMessage=args[1];
		String alertFrom=args[2];
		ApplicationContext context = SpringApplication.run(AlarmApplication.class, args);
		propertiesReader = (PropertiesReader) context.getBean("propertiesReader");
		connectionConfiguration = (ConnectionConfiguration) context.getBean("connectionConfiguration");
		Map<String, String> placeholderMapForAlert = new HashMap<>();
	    placeholderMapForAlert.put("<e>",alertMessage);
	    placeholderMapForAlert.put("<process_name>", alertFrom);
	    raiseAlert(alertCode, placeholderMapForAlert,0);
	    logger.info("Alert "+alertCode+" is raised. So, doing nothing.");
	    System.exit(0);
	}
	
	public static String getAlertbyId(String alertId) {
		  Connection conn = null;
		  Statement stmt = null;
		   String description="";
		   try{
			  conn = connectionConfiguration.getConnection();
		      stmt = conn.createStatement();
		      
		      String sql="select description from alert_db where alert_id='"+alertId+"'";
		      
		      logger.info("Fetching alert message by alert id from alertDb");
		      ResultSet rs = stmt.executeQuery(sql);
		      while(rs.next()) {
		    	  description = rs.getString("description");
		      }
		      
		   }catch(SQLException e){
			      //Handle errors for JDBC
				Map<String, String> placeholderMapForAlert=new HashMap<String, String>();
				placeholderMapForAlert.put("<e>",e.toString());
				placeholderMapForAlert.put("<process_name>","Alarm.jar");
				raiseAlert("alert006", placeholderMapForAlert,0);
				logger.info("Alert [ALERT_006] is raised. So, doing nothing.");
				System.exit(0);
			   }catch(Exception e){
			      //Handle errors for Class.forName
					Map<String, String> placeholderMapForAlert=new HashMap<String, String>();
					placeholderMapForAlert.put("<e>",e.toString());
					placeholderMapForAlert.put("<process_name>","Alarm.jar");
					raiseAlert("alert006", placeholderMapForAlert,0);
					logger.info("Alert [ALERT_006] is raised. So, doing nothing.");
					System.exit(0);
			   }finally{
			      //finally block used to close resources
			      try{
			         if(stmt!=null)
			            conn.close();
			      }catch(SQLException e){
						Map<String, String> placeholderMapForAlert=new HashMap<String, String>();
						placeholderMapForAlert.put("<e>",e.toString());
						placeholderMapForAlert.put("<process_name>","Alarm.jar");
						raiseAlert("alert006", placeholderMapForAlert,0);
						logger.info("Alert [ALERT_006] is raised. So, doing nothing.");
						System.exit(0);
			      }// do nothing
			      try{
			         if(conn!=null)
			            conn.close();
			      }catch(SQLException e){
						Map<String, String> placeholderMapForAlert=new HashMap<String, String>();
						placeholderMapForAlert.put("<e>",e.toString());
						placeholderMapForAlert.put("<process_name>","Alarm.jar");
						raiseAlert("alert006", placeholderMapForAlert,0);
						logger.info("Alert [ALERT_006] is raised. So, doing nothing.");
						System.exit(0);
			      }//end finally try
			   }//end try
		   return description;
	}
	
	public static void raiseAlert(String alertId, Map<String, String> bodyPlaceHolderMap,Integer userId) {
		   Connection conn = null;
		   Statement stmt = null;
		try {
			 conn = connectionConfiguration.getConnection();
		     stmt = conn.createStatement();
			String alertDescription = getAlertbyId(alertId);

			if(Objects.nonNull(bodyPlaceHolderMap)) {
				for (Map.Entry<String, String> entry : bodyPlaceHolderMap.entrySet()) {
					logger.info("Placeholder key : " + entry.getKey() + " value : " + entry.getValue());
					alertDescription=alertDescription.replaceAll(entry.getKey(), entry.getValue());
				}
			}
			logger.info("alert message: "+alertDescription);
			String sql="Insert into running_alert_db(alert_id,created_on,modified_on,description,status,user_id)"
					+ "values('"+alertId+"',"+defaultNowDate()+","+defaultNowDate()+",'"+alertDescription+"',0,"+userId+")";
			logger.info("Inserting alert into running alert db");
			
			stmt.executeQuery(sql);
		}
		catch(SQLException e){
		      //Handle errors for JDBC
			Map<String, String> placeholderMapForAlert=new HashMap<String, String>();
			placeholderMapForAlert.put("<e>",e.toString());
			placeholderMapForAlert.put("<process_name>","Alarm.jar");
			raiseAlert("alert006", placeholderMapForAlert,0);
			logger.info("Alert [ALERT_006] is raised. So, doing nothing.");
			System.exit(0);
		   }catch(Exception e){
		      //Handle errors for Class.forName
				Map<String, String> placeholderMapForAlert=new HashMap<String, String>();
				placeholderMapForAlert.put("<e>",e.toString());
				placeholderMapForAlert.put("<process_name>","Alarm.jar");
				raiseAlert("alert006", placeholderMapForAlert,0);
				logger.info("Alert [ALERT_006] is raised. So, doing nothing.");
				System.exit(0);
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            conn.close();
		      }catch(SQLException e){
					Map<String, String> placeholderMapForAlert=new HashMap<String, String>();
					placeholderMapForAlert.put("<e>",e.toString());
					placeholderMapForAlert.put("<process_name>","Alarm.jar");
					raiseAlert("alert006", placeholderMapForAlert,0);
					logger.info("Alert [ALERT_006] is raised. So, doing nothing.");
					System.exit(0);
		      }// do nothing
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException e){
					Map<String, String> placeholderMapForAlert=new HashMap<String, String>();
					placeholderMapForAlert.put("<e>",e.toString());
					placeholderMapForAlert.put("<process_name>","Alarm.jar");
					raiseAlert("alert006", placeholderMapForAlert,0);
					logger.info("Alert [ALERT_006] is raised. So, doing nothing.");
					System.exit(0);
		      }//end finally try
		   }//end try
	}
	
	public static String defaultNowDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssSSSSS");
	    String val = sdf.format(new Date());
	    String date = "to_timestamp('" + val + "','YYYY-MM-DD HH24:MI:SSFF5')";
	    return date;
	}
}
