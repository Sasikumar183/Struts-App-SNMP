package com.example.site24x7.snmp;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalTime;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;

public class ArchiveData implements ServletContextListener {
    private CqlSession session;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            session = DatabaseConfig.getCassandraSession();
            if (session == null || session.isClosed()) {
                throw new IllegalStateException("Cassandra session is not initialized.");
            }
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	         scheduler.scheduleAtFixedRate(() -> insertDummyData(), 
	             calculateDelayUntil(10,34), 
	             24 * 60 * 60, 
	             TimeUnit.SECONDS);
	
	         scheduler.scheduleAtFixedRate(() -> {
	             try {
	                 removeData();
	             } catch (SQLException e) {
	                 e.printStackTrace();
	             }
	         }, calculateDelayUntil(16, 0), 24 * 60 * 60, TimeUnit.SECONDS);
	
	         scheduler.scheduleAtFixedRate(StoreData::fetchData, 0, 1, TimeUnit.MINUTES);

            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
		private static long calculateDelayUntil(int targetHour, int targetMinute) {
		    LocalTime now = LocalTime.now();
		    LocalTime targetTime = LocalTime.of(targetHour, targetMinute);
		
		    if (now.isAfter(targetTime)) {
		        return 24 * 60 * 60; 
		    }
		    return java.time.Duration.between(now, targetTime).getSeconds();
		} 
    private void insertDummyData() {	
        String sqlQuery = "SELECT " +
                "inter_details.id, " +
                "DATE_FORMAT(collected_time, '%Y-%m-%d %H:00:00') AS hour_slot, " +
                "interface.IP, " +
                "MAX(in_traffic) AS max_in_traffic, " +
                "MIN(in_traffic) AS min_in_traffic, " +
                "AVG(in_traffic) AS avg_in_traffic, " +
                "SUM(in_traffic) AS sum_in_traffic, " +
                "COUNT(in_traffic) AS count_in_traffic, " +
                "MAX(out_traffic) AS max_out_traffic, " +
                "MIN(out_traffic) AS min_out_traffic, " +
                "AVG(out_traffic) AS avg_out_traffic, " +
                "SUM(out_traffic) AS sum_out_traffic, " +
                "MAX(in_error) AS max_in_error, " +
                "MIN(in_error) AS min_in_error, " +
                "AVG(in_error) AS avg_in_error, " +
                "SUM(in_error) AS sum_in_error, " +
                "MAX(out_error) AS max_out_error, " +
                "MIN(out_error) AS min_out_error, " +
                "AVG(out_error) AS avg_out_error, " +
                "SUM(out_error) AS sum_out_error, " +
                "MAX(in_discard) AS max_in_discard, " +
                "MIN(in_discard) AS min_in_discard, " +
                "AVG(in_discard) AS avg_in_discard, " +
                "SUM(in_discard) AS sum_in_discard, " +
                "MAX(out_discard) AS max_out_discard, " +
                "MIN(out_discard) AS min_out_discard, " +
                "AVG(out_discard) AS avg_out_discard, " +
                "SUM(out_discard) AS sum_out_discard, " +
                "COUNT(CASE WHEN admin_status = 1 THEN 1 END) AS admin_up, " +
                "COUNT(CASE WHEN admin_status = 2 THEN 1 END) AS admin_down, " +
                "COUNT(CASE WHEN oper_status = 1 THEN 1 END) AS oper_up, " +
                "COUNT(CASE WHEN oper_status = 2 THEN 1 END) AS oper_down " +
                "FROM inter_details " +
                "JOIN interface ON inter_details.id = interface.id " +
                "GROUP BY inter_details.id, hour_slot, interface.IP " +
                "ORDER BY inter_details.id, hour_slot;";
        
        String insertQuery = "INSERT INTO snmp.snmp_interface_traffic (" +
                "id, interface_id, hour_slot, interface_ip, " +
                "count_admin_down, count_admin_up, avg_in_discard, avg_in_error, avg_in_traffic, " +
                "avg_out_discard, avg_out_error, avg_out_traffic, max_in_discard, max_in_error, " +
                "max_in_traffic, max_out_discard, max_out_error, max_out_traffic, min_in_discard, " +
                "min_in_error, min_in_traffic, min_out_discard, min_out_error, min_out_traffic, " +
                "count_oper_down, count_oper_up, sum_in_discard, sum_in_error, sum_in_traffic, " +
                "sum_out_discard, sum_out_error, sum_out_traffic) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlQuery)) {

            if (session == null || session.isClosed()) {
                throw new IllegalStateException("Cassandra session is not initialized.");
            }

            com.datastax.oss.driver.api.core.cql.PreparedStatement statement = session.prepare(insertQuery);

            while (rs.next()) {
            	BoundStatement boundStatement = statement.bind(
            		    UUID.randomUUID(), 
            		    rs.getInt("id"),  
            		    rs.getString("hour_slot"), 
            		    rs.getString("IP"),
            		    rs.getInt("admin_down"), rs.getInt("admin_up"),
            		    rs.getDouble("avg_in_discard"), rs.getDouble("avg_in_error"), rs.getDouble("avg_in_traffic"),
            		    rs.getDouble("avg_out_discard"), rs.getDouble("avg_out_error"), rs.getDouble("avg_out_traffic"),
            		    
            		    rs.getInt("max_in_discard"), rs.getInt("max_in_error"), rs.getDouble("max_in_traffic"),
            		    rs.getInt("max_out_discard"), rs.getInt("max_out_error"), rs.getDouble("max_out_traffic"),

            		    rs.getInt("min_in_discard"), rs.getInt("min_in_error"), rs.getDouble("min_in_traffic"),
            		    rs.getInt("min_out_discard"), rs.getInt("min_out_error"), rs.getDouble("min_out_traffic"),
            		    rs.getInt("oper_down"), rs.getInt("oper_up"),


            		    rs.getInt("sum_in_discard"), rs.getInt("sum_in_error"), rs.getDouble("sum_in_traffic"),
            		    rs.getInt("sum_out_discard"), rs.getInt("sum_out_error"), rs.getDouble("sum_out_traffic")
            		);




                session.execute(boundStatement);
            }
        	System.out.println("Data is inserted");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void removeData() throws SQLException {
        String query = "DELETE FROM inter_details WHERE collected_time >= CONCAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), ' 10:00:00') AND collected_time < CONCAT(CURDATE(), ' 10:00:00');";
        
        Connection conn = DatabaseConfig.getConnection();
        Statement stmt = conn.createStatement();
        
        int rowsAffected = stmt.executeUpdate(query);
        System.out.println(rowsAffected + " rows deleted.");
        
        stmt.close();
        conn.close();
    }


    @Override
    public void contextDestroyed(ServletContextEvent event) {
        System.out.println("ServletContextListener destroyed. Closing Cassandra session.");
        if (session != null && !session.isClosed()) {
            session.close();
        }
    }
}
