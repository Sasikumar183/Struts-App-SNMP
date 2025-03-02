package com.example.site24x7.restapi;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CassandraDataAggregator {
	
	
	public static JSONArray getAggregated(JSONArray jsonArray,String interval) {
		return processAggregation(jsonArray, interval);
	}
    public static void main(String[] args) {
        // Sample JSON Array (Replace with your actual Cassandra JSON)
        String jsonData = """
        		[
            {
                "sum_in_discard": 0,
                "min_in_discard": 0,
                "sum_in_error": 0,
                "count_admin_up": 5,
                "min_out_discard": 0,
                "max_out_traffic": 1130077,
                "sum_out_traffic": 3887638,
                "count_admin_down": 0,
                "avg_in_traffic": 777527.6,
                "max_in_discard": 0,
                "min_out_traffic": 427587,
                "sum_out_error": 0,
                "count_oper_down": 0,
                "hour_slot": "2025-02-28 10:00:00",
                "min_out_error": 0,
                "min_in_traffic": 427587,
                "sum_in_traffic": 3887638,
                "max_out_discard": 0,
                "max_out_error": 0,
                "avg_in_discard": 0,
                "avg_out_discard": 0,
                "avg_in_error": 0,
                "max_in_error": 0,
                "avg_out_traffic": 777527.6,
                "max_in_traffic": 1130077,
                "interface_id": 1,
                "count_oper_up": 5,
                "min_in_error": 0,
                "sum_out_discard": 0,
                "avg_out_error": 0
            },
            {
                "sum_in_discard": 0,
                "min_in_discard": 0,
                "sum_in_error": 0,
                "count_admin_up": 191,
                "min_out_discard": 0,
                "max_out_traffic": 9114583,
                "sum_out_traffic": 1.292291174E9,
                "count_admin_down": 0,
                "avg_in_traffic": 6765922.377,
                "max_in_discard": 0,
                "min_out_traffic": 3528406,
                "sum_out_error": 0,
                "count_oper_down": 0,
                "hour_slot": "2025-02-27 11:00:00",
                "min_out_error": 0,
                "min_in_traffic": 3528406,
                "sum_in_traffic": 1.292291174E9,
                "max_out_discard": 0,
                "max_out_error": 0,
                "avg_in_discard": 0,
                "avg_out_discard": 0,
                "avg_in_error": 0,
                "max_in_error": 0,
                "avg_out_traffic": 6765922.377,
                "max_in_traffic": 9114583,
                "interface_id": 1,
                "count_oper_up": 191,
                "min_in_error": 0,
                "sum_out_discard": 0,
                "avg_out_error": 0
            },
            {
                "sum_in_discard": 0,
                "min_in_discard": 0,
                "sum_in_error": 0,
                "count_admin_up": 9,
                "min_out_discard": 0,
                "max_out_traffic": 9750711,
                "sum_out_traffic": 8.3713791E7,
                "count_admin_down": 0,
                "avg_in_traffic": 9301532.3333,
                "max_in_discard": 0,
                "min_out_traffic": 9138204,
                "sum_out_error": 0,
                "count_oper_down": 0,
                "hour_slot": "2025-02-27 12:00:00",
                "min_out_error": 0,
                "min_in_traffic": 9138204,
                "sum_in_traffic": 8.3713791E7,
                "max_out_discard": 0,
                "max_out_error": 0,
                "avg_in_discard": 0,
                "avg_out_discard": 0,
                "avg_in_error": 0,
                "max_in_error": 0,
                "avg_out_traffic": 9301532.3333,
                "max_in_traffic": 9750711,
                "interface_id": 1,
                "count_oper_up": 9,
                "min_in_error": 0,
                "sum_out_discard": 0,
                "avg_out_error": 0
            }
        ]
        		"""; // Your JSON Data here

        JSONArray jsonArray = new JSONArray(jsonData);
        String interval = "1w"; // Change to "2d" for two-day aggregation

        JSONArray result = processAggregation(jsonArray, interval);
        System.out.println(result.toString(2)); // Pretty print JSON
    }

    public static JSONArray processAggregation(JSONArray jsonArray, String interval) {
        Map<String, JSONObject> aggregatedData = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject record = jsonArray.getJSONObject(i);
            String timestamp = record.getString("hour_slot");
            LocalDate date = LocalDate.parse(timestamp.split(" ")[0]); // Extract date part

            String key;
            if (interval.equals("1w")) {
                key = date.toString(); // Group by day
            } else if (interval.equals("2d")) {
                key = "2-day-total"; // Aggregate into one object
            } else {
                continue; // Skip invalid intervals
            }

            aggregatedData.putIfAbsent(key, createEmptyRecord(key));

            JSONObject aggregated = aggregatedData.get(key);
            aggregateValues(aggregated, record);
        }

        return new JSONArray(aggregatedData.values());
    }

    private static JSONObject createEmptyRecord(String key) {
        JSONObject obj = new JSONObject();
        obj.put("date", key);
        obj.put("sum_in_traffic", 0);
        obj.put("sum_out_traffic", 0);
        obj.put("count_admin_up", 0);
        obj.put("count_oper_up", 0);
        obj.put("max_in_traffic", 0);
        obj.put("max_out_traffic", 0);
        return obj;
    }

    private static void aggregateValues(JSONObject aggregated, JSONObject record) {
        aggregated.put("sum_in_traffic", aggregated.getDouble("sum_in_traffic") + record.getDouble("sum_in_traffic"));
        aggregated.put("sum_out_traffic", aggregated.getDouble("sum_out_traffic") + record.getDouble("sum_out_traffic"));
        aggregated.put("count_admin_up", aggregated.getInt("count_admin_up") + record.getInt("count_admin_up"));
        aggregated.put("count_oper_up", aggregated.getInt("count_oper_up") + record.getInt("count_oper_up"));
        aggregated.put("max_in_traffic", Math.max(aggregated.getDouble("max_in_traffic"), record.getDouble("max_in_traffic")));
        aggregated.put("max_out_traffic", Math.max(aggregated.getDouble("max_out_traffic"), record.getDouble("max_out_traffic")));
    }
}
