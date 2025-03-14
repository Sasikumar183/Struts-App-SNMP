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
        "count_admin_up": 12,
        "min_out_discard": 20,
        "max_out_traffic": 0,
        "sum_out_traffic": 0,
        "count_admin_down": 0,
        "avg_in_traffic": 0,
        "max_in_discard": 0,
        "min_out_traffic": 0,
        "sum_out_error": 0,
        "count_oper_down": 12,
        "hour_slot": "2025-02-21 15:00:00",
        "min_out_error": 0,
        "min_in_traffic": 0,
        "sum_in_traffic": 0,
        "max_out_discard": 20,
        "max_out_error": 0,
        "avg_in_discard": 0,
        "avg_out_discard": 20,
        "avg_in_error": 0,
        "max_in_error": 0,
        "avg_out_traffic": 0,
        "max_in_traffic": 0,
        "interface_id": 3,
        "count_oper_up": 0,
        "min_in_error": 0,
        "sum_out_discard": 240,
        "avg_out_error": 0
    },
    {
        "sum_in_discard": 0,
        "min_in_discard": 0,
        "sum_in_error": 0,
        "count_admin_up": 191,
        "min_out_discard": 3,
        "max_out_traffic": 31910,
        "sum_out_traffic": 6094810,
        "count_admin_down": 0,
        "avg_in_traffic": 5431,
        "max_in_discard": 0,
        "min_out_traffic": 31910,
        "sum_out_error": 0,
        "count_oper_down": 0,
        "hour_slot": "2025-02-27 11:00:00",
        "min_out_error": 0,
        "min_in_traffic": 5431,
        "sum_in_traffic": 1037321,
        "max_out_discard": 3,
        "max_out_error": 0,
        "avg_in_discard": 0,
        "avg_out_discard": 3,
        "avg_in_error": 0,
        "max_in_error": 0,
        "avg_out_traffic": 31910,
        "max_in_traffic": 5431,
        "interface_id": 3,
        "count_oper_up": 191,
        "min_in_error": 0,
        "sum_out_discard": 573,
        "avg_out_error": 0
    },
    {
        "sum_in_discard": 0,
        "min_in_discard": 0,
        "sum_in_error": 0,
        "count_admin_up": 13,
        "min_out_discard": 20,
        "max_out_traffic": 0,
        "sum_out_traffic": 0,
        "count_admin_down": 0,
        "avg_in_traffic": 0,
        "max_in_discard": 0,
        "min_out_traffic": 0,
        "sum_out_error": 0,
        "count_oper_down": 13,
        "hour_slot": "2025-02-21 16:00:00",
        "min_out_error": 0,
        "min_in_traffic": 0,
        "sum_in_traffic": 0,
        "max_out_discard": 20,
        "max_out_error": 0,
        "avg_in_discard": 0,
        "avg_out_discard": 20,
        "avg_in_error": 0,
        "max_in_error": 0,
        "avg_out_traffic": 0,
        "max_in_traffic": 0,
        "interface_id": 3,
        "count_oper_up": 0,
        "min_in_error": 0,
        "sum_out_discard": 260,
        "avg_out_error": 0
    },
    {
        "sum_in_discard": 0,
        "min_in_discard": 0,
        "sum_in_error": 0,
        "count_admin_up": 24,
        "min_out_discard": 20,
        "max_out_traffic": 0,
        "sum_out_traffic": 0,
        "count_admin_down": 0,
        "avg_in_traffic": 0,
        "max_in_discard": 0,
        "min_out_traffic": 0,
        "sum_out_error": 0,
        "count_oper_down": 24,
        "hour_slot": "2025-02-26 12:00:00",
        "min_out_error": 0,
        "min_in_traffic": 0,
        "sum_in_traffic": 0,
        "max_out_discard": 20,
        "max_out_error": 0,
        "avg_in_discard": 0,
        "avg_out_discard": 20,
        "avg_in_error": 0,
        "max_in_error": 0,
        "avg_out_traffic": 0,
        "max_in_traffic": 0,
        "interface_id": 3,
        "count_oper_up": 0,
        "min_in_error": 0,
        "sum_out_discard": 480,
        "avg_out_error": 0
    },
    {
        "sum_in_discard": 0,
        "min_in_discard": 0,
        "sum_in_error": 0,
        "count_admin_up": 27,
        "min_out_discard": 20,
        "max_out_traffic": 0,
        "sum_out_traffic": 0,
        "count_admin_down": 0,
        "avg_in_traffic": 0,
        "max_in_discard": 0,
        "min_out_traffic": 0,
        "sum_out_error": 0,
        "count_oper_down": 27,
        "hour_slot": "2025-02-24 10:00:00",
        "min_out_error": 0,
        "min_in_traffic": 0,
        "sum_in_traffic": 0,
        "max_out_discard": 20,
        "max_out_error": 0,
        "avg_in_discard": 0,
        "avg_out_discard": 20,
        "avg_in_error": 0,
        "max_in_error": 0,
        "avg_out_traffic": 0,
        "max_in_traffic": 0,
        "interface_id": 3,
        "count_oper_up": 0,
        "min_in_error": 0,
        "sum_out_discard": 540,
        "avg_out_error": 0
    },
    {
        "sum_in_discard": 0,
        "min_in_discard": 0,
        "sum_in_error": 0,
        "count_admin_up": 9,
        "min_out_discard": 3,
        "max_out_traffic": 31910,
        "sum_out_traffic": 287190,
        "count_admin_down": 0,
        "avg_in_traffic": 5431,
        "max_in_discard": 0,
        "min_out_traffic": 31910,
        "sum_out_error": 0,
        "count_oper_down": 0,
        "hour_slot": "2025-02-27 12:00:00",
        "min_out_error": 0,
        "min_in_traffic": 5431,
        "sum_in_traffic": 48879,
        "max_out_discard": 3,
        "max_out_error": 0,
        "avg_in_discard": 0,
        "avg_out_discard": 3,
        "avg_in_error": 0,
        "max_in_error": 0,
        "avg_out_traffic": 31910,
        "max_in_traffic": 5431,
        "interface_id": 3,
        "count_oper_up": 9,
        "min_in_error": 0,
        "sum_out_discard": 27,
        "avg_out_error": 0
    },
    {
        "sum_in_discard": 0,
        "min_in_discard": 0,
        "sum_in_error": 0,
        "count_admin_up": 27,
        "min_out_discard": 20,
        "max_out_traffic": 0,
        "sum_out_traffic": 0,
        "count_admin_down": 0,
        "avg_in_traffic": 0,
        "max_in_discard": 0,
        "min_out_traffic": 0,
        "sum_out_error": 0,
        "count_oper_down": 27,
        "hour_slot": "2025-02-24 11:00:00",
        "min_out_error": 0,
        "min_in_traffic": 0,
        "sum_in_traffic": 0,
        "max_out_discard": 20,
        "max_out_error": 0,
        "avg_in_discard": 0,
        "avg_out_discard": 20,
        "avg_in_error": 0,
        "max_in_error": 0,
        "avg_out_traffic": 0,
        "max_in_traffic": 0,
        "interface_id": 3,
        "count_oper_up": 0,
        "min_in_error": 0,
        "sum_out_discard": 540,
        "avg_out_error": 0
    },
    {
        "sum_in_discard": 0,
        "min_in_discard": 0,
        "sum_in_error": 0,
        "count_admin_up": 5,
        "min_out_discard": 3,
        "max_out_traffic": 26730,
        "sum_out_traffic": 133650,
        "count_admin_down": 0,
        "avg_in_traffic": 4359,
        "max_in_discard": 0,
        "min_out_traffic": 26730,
        "sum_out_error": 0,
        "count_oper_down": 0,
        "hour_slot": "2025-02-28 10:00:00",
        "min_out_error": 0,
        "min_in_traffic": 4359,
        "sum_in_traffic": 21795,
        "max_out_discard": 3,
        "max_out_error": 0,
        "avg_in_discard": 0,
        "avg_out_discard": 3,
        "avg_in_error": 0,
        "max_in_error": 0,
        "avg_out_traffic": 26730,
        "max_in_traffic": 4359,
        "interface_id": 3,
        "count_oper_up": 5,
        "min_in_error": 0,
        "sum_out_discard": 15,
        "avg_out_error": 0
    }
]
        		"""; // Your JSON Data here

        JSONArray jsonArray = new JSONArray(jsonData);
        String interval = "1w";
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
            if (interval.equals("1w") || interval.equals("30d")) {
                key = date.toString(); // Group by day
            }  else {
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
        obj.put("sum_in_discard", 0);
        obj.put("min_in_discard", Integer.MAX_VALUE);
        obj.put("sum_in_error", 0);
        obj.put("count_admin_up", 0);
        obj.put("min_out_discard", Integer.MAX_VALUE);
        obj.put("max_out_traffic", 0);
        obj.put("sum_out_traffic", 0);
        obj.put("count_admin_down", 0);
        obj.put("avg_in_traffic", 0);
        obj.put("max_in_discard", 0);
        obj.put("min_out_traffic", Integer.MAX_VALUE);
        obj.put("sum_out_error", 0);
        obj.put("count_oper_down", 0);
        obj.put("min_out_error", Integer.MAX_VALUE);
        obj.put("min_in_traffic", Integer.MAX_VALUE);
        obj.put("sum_in_traffic", 0);
        obj.put("max_out_discard", 0);
        obj.put("max_out_error", 0);
        obj.put("avg_in_discard", 0);
        obj.put("avg_out_discard", 0);
        obj.put("avg_in_error", 0);
        obj.put("max_in_error", 0);
        obj.put("avg_out_traffic", 0);
        obj.put("max_in_traffic", 0);
        obj.put("interface_id", -1);
        obj.put("count_oper_up", 0);
        obj.put("min_in_error", Integer.MAX_VALUE);
        obj.put("sum_out_discard", 0);
        obj.put("avg_out_error", 0);
        return obj;
    }

    private static void aggregateValues(JSONObject aggregated, JSONObject record) {
        aggregated.put("sum_in_discard", aggregated.getInt("sum_in_discard") + record.getInt("sum_in_discard"));
        aggregated.put("min_in_discard", Math.min(aggregated.getInt("min_in_discard"), record.getInt("min_in_discard")));
        aggregated.put("sum_in_error", aggregated.getInt("sum_in_error") + record.getInt("sum_in_error"));
        aggregated.put("count_admin_up", aggregated.getInt("count_admin_up") + record.getInt("count_admin_up"));
        aggregated.put("min_out_discard", Math.min(aggregated.getInt("min_out_discard"), record.getInt("min_out_discard")));
        aggregated.put("max_out_traffic", Math.max(aggregated.getInt("max_out_traffic"), record.getInt("max_out_traffic")));
        aggregated.put("sum_out_traffic", aggregated.getInt("sum_out_traffic") + record.getInt("sum_out_traffic"));
        aggregated.put("count_admin_down", aggregated.getInt("count_admin_down") + record.getInt("count_admin_down"));
        aggregated.put("avg_in_traffic", (aggregated.getInt("sum_in_traffic") + record.getInt("sum_in_traffic")) / 2);
        aggregated.put("max_in_discard", Math.max(aggregated.getInt("max_in_discard"), record.getInt("max_in_discard")));
        aggregated.put("min_out_traffic", Math.min(aggregated.getInt("min_out_traffic"), record.getInt("min_out_traffic")));
        aggregated.put("sum_out_error", aggregated.getInt("sum_out_error") + record.getInt("sum_out_error"));
        aggregated.put("count_oper_down", aggregated.getInt("count_oper_down") + record.getInt("count_oper_down"));
        aggregated.put("min_out_error", Math.min(aggregated.getInt("min_out_error"), record.getInt("min_out_error")));
        aggregated.put("min_in_traffic", Math.min(aggregated.getInt("min_in_traffic"), record.getInt("min_in_traffic")));
        aggregated.put("sum_in_traffic", aggregated.getInt("sum_in_traffic") + record.getInt("sum_in_traffic"));
        aggregated.put("max_out_discard", Math.max(aggregated.getInt("max_out_discard"), record.getInt("max_out_discard")));
        aggregated.put("max_out_error", Math.max(aggregated.getInt("max_out_error"), record.getInt("max_out_error")));
        aggregated.put("avg_in_discard", (aggregated.getInt("sum_in_discard") + record.getInt("sum_in_discard")) / 2);
        aggregated.put("avg_out_discard", (aggregated.getInt("sum_out_discard") + record.getInt("sum_out_discard")) / 2);
        aggregated.put("avg_in_error", (aggregated.getInt("sum_in_error") + record.getInt("sum_in_error")) / 2);
        aggregated.put("max_in_error", Math.max(aggregated.getInt("max_in_error"), record.getInt("max_in_error")));
        aggregated.put("avg_out_traffic", (aggregated.getInt("sum_out_traffic") + record.getInt("sum_out_traffic")) / 2);
        aggregated.put("max_in_traffic", Math.max(aggregated.getInt("max_in_traffic"), record.getInt("max_in_traffic")));
        aggregated.put("interface_id", record.getInt("interface_id")); // Keeping the last interface_id
        aggregated.put("count_oper_up", aggregated.getInt("count_oper_up") + record.getInt("count_oper_up"));
        aggregated.put("min_in_error", Math.min(aggregated.getInt("min_in_error"), record.getInt("min_in_error")));
        aggregated.put("sum_out_discard", aggregated.getInt("sum_out_discard") + record.getInt("sum_out_discard"));
        aggregated.put("avg_out_error", (aggregated.getInt("sum_out_error") + record.getInt("sum_out_error")) / 2);
    }
}