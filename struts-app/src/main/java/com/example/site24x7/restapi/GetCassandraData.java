package com.example.site24x7.restapi;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.example.action.DatabaseConfig;

import org.json.JSONArray;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class GetCassandraData {
	
	public static JSONObject getData(String interval) {
        JSONObject res = new JSONObject();
        CqlSession session = DatabaseConfig.getCassandraSession();
		try  {
       	 String Cquery = """
                    SELECT primary_id, avg_in_traffic, avg_out_traffic, avg_in_error, avg_out_error, 
                           avg_in_discard,avg_out_discard
                    FROM snmp_interface_traffic where hour_slot >= ?
                    ALLOW FILTERING;
                    """;

       	 	

            String inter = getTimestamp(interval);
            
            PreparedStatement preparedStatement = session.prepare(Cquery);
            ResultSet resultSet = session.execute(preparedStatement.bind(inter));
            Map<Integer, JSONObject> dataMap = new HashMap<>();

            for (Row row : resultSet) {
                int interfaceId = row.getInt("primary_id");
                double avgInTraffic = row.getDouble("avg_in_traffic");
                double avgOutTraffic = row.getDouble("avg_out_traffic");
                double avgInError = row.getDouble("avg_in_error");
                double avgOutError = row.getDouble("avg_out_error");
                double avgInDiscard = row.getDouble("avg_in_discard");
                double avgOutDiscard = row.getDouble("avg_out_discard");
               

                JSONObject jsonObj = dataMap.getOrDefault(interfaceId, new JSONObject()
                        .put("interface_id", interfaceId)
                        .put("sum_in_traffic", 0.0)
                        .put("sum_out_traffic", 0.0)
                        .put("sum_in_error", 0.0)
                        .put("sum_out_error", 0.0)
                        .put("sum_in_discard", 0.0)
                        .put("sum_out_discard", 0.0)
                        .put("count", 0));

                jsonObj.put("sum_in_traffic", jsonObj.getDouble("sum_in_traffic") + avgInTraffic);
                jsonObj.put("sum_out_traffic", jsonObj.getDouble("sum_out_traffic") + avgOutTraffic);
                jsonObj.put("sum_in_error", jsonObj.getDouble("sum_in_error") + avgInError);
                jsonObj.put("sum_out_error", jsonObj.getDouble("sum_out_error") + avgOutError);
                jsonObj.put("sum_in_discard", jsonObj.getDouble("sum_in_discard") + avgInDiscard);
                jsonObj.put("sum_out_discard", jsonObj.getDouble("sum_out_discard") + avgOutDiscard);
                jsonObj.put("count", jsonObj.getInt("count") + 1);
                dataMap.put(interfaceId, jsonObj);
            }

            JSONArray resultArray = new JSONArray();
            for (JSONObject jsonObj : dataMap.values()) {
                int count = jsonObj.getInt("count");

                JSONObject avgObj = new JSONObject();
                avgObj.put("interface_id", jsonObj.getInt("interface_id"));
                avgObj.put("avg_in_traffic", jsonObj.getDouble("sum_in_traffic") / count);
                avgObj.put("avg_out_traffic", jsonObj.getDouble("sum_out_traffic") / count);
                avgObj.put("avg_in_error", jsonObj.getDouble("sum_in_error") / count);
                avgObj.put("avg_out_error", jsonObj.getDouble("sum_out_error") / count);
                avgObj.put("avg_in_discard", jsonObj.getDouble("sum_in_discard") / count);
                avgObj.put("avg_out_discard", jsonObj.getDouble("sum_out_discard") / count);

                resultArray.put(avgObj);
            }
            res.put("data", resultArray);
		}
		catch (Exception e) {
            e.printStackTrace();
            res.put("error", "db error");
        }
        return res;

	}
	
	public static String getTimestamp(String time) {
		
    	LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        LocalDateTime resultTime = time.equals("12h") ? now.minusHours(12) :
                                   time.equals("1d")  ? now.minusDays(1) :
                                   time.equals("1w")  ? now.minusDays(7) :
                                   time.equals("30d") ? now.minusDays(30) :
                                   now;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return resultTime.format(formatter);
    }	
   
}