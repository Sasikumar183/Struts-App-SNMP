package com.example.site24x7.restapi;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.example.action.DatabaseConfig;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

import org.checkerframework.checker.units.qual.C;
import org.json.JSONArray;
import org.json.JSONObject;

public class CassandraSpecificInterface {
	
	public static JSONArray getCassandraData(int id,String interval) {
		Instant now = Instant.now();
		Instant inter;
		if (interval.equals("12h")) {
		    inter = now.minusSeconds(12 * 3600);  
		} 
		else if (interval.equals("24h")) {
		    inter = now.minusSeconds(24 * 3600);  
		} 
		else if (interval.equals("1w")) {
		    inter = now.minusSeconds(7 * 24 * 3600); 
		} 
		else if (interval.equals("30d")) {
		    inter = now.minusSeconds(30 * 24 * 3600);
		}
		else {
			return new JSONArray();
		}
		
		String query = "SELECT interface_id, hour_slot, avg_in_discard, avg_in_error, avg_in_traffic, " +
                "avg_out_discard, avg_out_error, avg_out_traffic, count_admin_down, count_admin_up, " +
                "count_oper_down, count_oper_up,  max_in_discard, max_in_error, " +
                "max_in_traffic, max_out_discard, max_out_error, max_out_traffic, min_in_discard, " +
                "min_in_error, min_in_traffic, min_out_discard, min_out_error, min_out_traffic, " +
                "sum_in_discard, sum_in_error, sum_in_traffic, sum_out_discard, sum_out_error, sum_out_traffic " +
                "FROM snmp_interface_traffic WHERE hour_slot >= ? AND interface_id = ? ALLOW FILTERING;";

        // Execute query
        String timestampStr = inter.toString(); // Convert Instant to String
        
        CqlSession session = DatabaseConfig.getCassandraSession();
        
        ResultSet resultSet = session.execute(session.prepare(query).bind(timestampStr, 1));

        JSONArray jsonArray = new JSONArray();
        for (Row row : resultSet) {
            JSONObject jsonObject = new JSONObject();
            
            jsonObject.put("interface_id", row.getInt("interface_id"));
            jsonObject.put("hour_slot", row.getString("hour_slot"));
            jsonObject.put("avg_in_discard", row.getDouble("avg_in_discard"));
            jsonObject.put("avg_in_error", row.getDouble("avg_in_error"));
            jsonObject.put("avg_in_traffic", row.getDouble("avg_in_traffic"));

            jsonObject.put("avg_out_discard", row.getDouble("avg_out_discard"));
            jsonObject.put("avg_out_error", row.getDouble("avg_out_error"));
            jsonObject.put("avg_out_traffic", row.getDouble("avg_out_traffic"));

            jsonObject.put("count_admin_down", row.getInt("count_admin_down"));
            jsonObject.put("count_admin_up", row.getInt("count_admin_up"));
            jsonObject.put("count_oper_down", row.getInt("count_oper_down"));
            jsonObject.put("count_oper_up", row.getInt("count_oper_up"));

            jsonObject.put("max_in_discard", row.getInt("max_in_discard"));
            jsonObject.put("max_in_error", row.getInt("max_in_error"));
            jsonObject.put("max_in_traffic", row.getDouble("max_in_traffic"));

            jsonObject.put("max_out_discard", row.getInt("max_out_discard"));
            jsonObject.put("max_out_error", row.getInt("max_out_error"));
            jsonObject.put("max_out_traffic", row.getDouble("max_out_traffic"));

            jsonObject.put("min_in_discard", row.getInt("min_in_discard"));
            jsonObject.put("min_in_error", row.getInt("min_in_error"));
            jsonObject.put("min_in_traffic", row.getDouble("min_in_traffic"));

            jsonObject.put("min_out_discard", row.getInt("min_out_discard"));
            jsonObject.put("min_out_error", row.getInt("min_out_error"));
            jsonObject.put("min_out_traffic", row.getDouble("min_out_traffic"));

            jsonObject.put("sum_in_discard", row.getInt("sum_in_discard"));
            jsonObject.put("sum_in_error", row.getInt("sum_in_error"));
            jsonObject.put("sum_in_traffic", row.getDouble("sum_in_traffic"));

            jsonObject.put("sum_out_discard", row.getInt("sum_out_discard"));
            jsonObject.put("sum_out_error", row.getInt("sum_out_error"));
            jsonObject.put("sum_out_traffic", row.getDouble("sum_out_traffic"));

            jsonArray.put(jsonObject);
        }
        
		return jsonArray;
	}
	
	
   
}
