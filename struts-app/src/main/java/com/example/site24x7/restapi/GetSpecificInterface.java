package com.example.site24x7.restapi;

import java.sql.Connection;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.example.action.DatabaseConfig;
import com.fasterxml.jackson.annotation.JsonMerge;

import java.sql.*;
import org.json.JSONObject;

public class GetSpecificInterface {

    public static JSONObject getGeneralDetails(int id) throws SQLException {
        String query = "SELECT * FROM interface WHERE id = ?";
        JSONObject json = new JSONObject();
        
        Connection con = DatabaseConfig.getConnection();
        java.sql.PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
        	json.put("id", id);
        	json.put("name", rs.getString("interface_name"));
        	json.put("ip", rs.getString("IP"));
        }
        else {
        	json.put("error","Id not there");
        }
        System.out.println(json); 
        con.close();
        ps.close();
        rs.close();
        
        return json;
    }


	public static JSONObject getInsights(int id,String interval) throws SQLException {
		String query="";
		
		int seconds = 0,hour=0;
		if (interval.equals("1h")) {
		    seconds = 600; 
		    hour = 1;
		} else if (interval.equals("6h")) {
		    seconds = 1800;  
		    hour = 6;
		} else if (interval.equals("12h")) {
		    seconds = 3600; 
		    hour = 12;
		} else if (interval.equals("1d")) { 
		    seconds = 3600;
		    hour = 24;
		} else if (interval.equals("1w")) { 
		    seconds = 86400; 
		    hour = 168; 
		} else if (interval.equals("30d")) { 
		    seconds = 172800; 
		    hour = 720;  
		}
		
		
			query = """
					SELECT 
				    id,
				    FROM_UNIXTIME(FLOOR(UNIX_TIMESTAMP(collected_time) / ?) * ?) AS time_slot,
				    
				    -- Traffic Statistics
				    AVG(in_traffic) AS avg_in_traffic,
				    MAX(in_traffic) AS max_in_traffic,
				    MIN(in_traffic) AS min_in_traffic,
				    
				    AVG(out_traffic) AS avg_out_traffic,
				    MAX(out_traffic) AS max_out_traffic,
				    MIN(out_traffic) AS min_out_traffic,
				
				    -- Error Statistics
				    AVG(in_error) AS avg_in_error,
				    MAX(in_error) AS max_in_error,
				    MIN(in_error) AS min_in_error,
				    COUNT(CASE WHEN in_error > 0 THEN 1 END) AS count_in_error,  
				
				    AVG(out_error) AS avg_out_error,
				    MAX(out_error) AS max_out_error,
				    MIN(out_error) AS min_out_error,
				    COUNT(CASE WHEN out_error > 0 THEN 1 END) AS count_out_error, 
				
				    -- Discard Statistics
				    AVG(in_discard) AS avg_in_discard,
				    MAX(in_discard) AS max_in_discard,
				    MIN(in_discard) AS min_in_discard,
				    COUNT(CASE WHEN in_discard > 0 THEN 1 END) AS count_in_discard,  
				
				    AVG(out_discard) AS avg_out_discard,
				    MAX(out_discard) AS max_out_discard,
				    MIN(out_discard) AS min_out_discard,
				    COUNT(CASE WHEN out_discard > 0 THEN 1 END) AS count_out_discard
				
				FROM inter_details
				WHERE collected_time >= NOW() - INTERVAL ? HOUR 
				AND id = ?
				GROUP BY id, time_slot
				ORDER BY id, time_slot;

										""";
		
		JSONObject json = new JSONObject();
        
        Connection con = DatabaseConfig.getConnection();
        java.sql.PreparedStatement ps = con.prepareStatement(query);
        
        ps.setInt(1, seconds);
        ps.setInt(2, seconds);
        ps.setInt(3, hour);
        ps.setInt(4, id);
        ResultSet rs = ps.executeQuery();
        JSONArray jsonArr = new JSONArray();
        
        while (rs.next()) {
            JSONObject data = new JSONObject();
            data.put("time_slot", rs.getString("time_slot"));
            data.put("avg_in_traffic", rs.getDouble("avg_in_traffic"));
            data.put("max_in_traffic", rs.getDouble("max_in_traffic"));
            data.put("min_in_traffic", rs.getDouble("min_in_traffic"));
            data.put("avg_out_traffic", rs.getDouble("avg_out_traffic"));
            data.put("max_out_traffic", rs.getDouble("max_out_traffic"));
            data.put("min_out_traffic", rs.getDouble("min_out_traffic"));
            data.put("avg_in_error", rs.getDouble("avg_in_error"));
            data.put("max_in_error", rs.getDouble("max_in_error"));
            data.put("min_in_error", rs.getDouble("min_in_error"));
            data.put("count_in_error", rs.getInt("count_in_error"));
            data.put("avg_out_error", rs.getDouble("avg_out_error"));
            data.put("max_out_error", rs.getDouble("max_out_error"));
            data.put("min_out_error", rs.getDouble("min_out_error"));
            data.put("count_out_error", rs.getInt("count_out_error"));
            data.put("avg_in_discard", rs.getDouble("avg_in_discard"));
            data.put("max_in_discard", rs.getDouble("max_in_discard"));
            data.put("min_in_discard", rs.getDouble("min_in_discard"));
            data.put("count_in_discard", rs.getInt("count_in_discard"));
            data.put("avg_out_discard", rs.getDouble("avg_out_discard"));
            data.put("max_out_discard", rs.getDouble("max_out_discard"));
            data.put("min_out_discard", rs.getDouble("min_out_discard"));
            data.put("count_out_discard", rs.getInt("count_out_discard"));

            jsonArr.put(data);
        }
        if (interval.equals("1h") || interval.equals("6h")) {
        	json.put("data", jsonArr);

        }
        else if(interval.equals("1w") || interval.equals("30d") || interval.equals("12h") || interval.equals("1d")) {
            json.put("data", JSONMerger.merge(jsonArr,CassandraSpecificInterface.getCassandraData(id, interval) ,interval));

        }
        
//        json.put("insight", jsonArr);
//        json.put("cassandra", CassandraSpecificInterface.getCassandraData(id, interval));
        con.close();
        ps.close();
        rs.close();
        
        return json;
		
		
	}

	public static JSONObject getCurrentStatus(int id) throws SQLException {
		String query = """
                SELECT id, oper_status, admin_status
                FROM (
                    SELECT id, oper_status, admin_status, 
                           ROW_NUMBER() OVER (PARTITION BY id ORDER BY collected_time DESC) AS rn
                    FROM inter_details
                ) t
                WHERE rn = 1 AND id = ?;
            """;
		JSONObject json = new JSONObject();
        
        Connection con = DatabaseConfig.getConnection();
        java.sql.PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        
        if(rs.next()) {
        	json.put("operation_status", rs.getInt("oper_status"));
        	json.put("admin_status", rs.getInt("admin_status"));
        }
        else {
        	json.put("error","Id not there");
        }
        System.out.println(json); 
        con.close();
        ps.close();
        rs.close();
        
        return json;
		
	}

}
