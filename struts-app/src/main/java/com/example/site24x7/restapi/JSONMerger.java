package com.example.site24x7.restapi;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.*;

public class JSONMerger {
    public static JSONArray merge(JSONArray insightArray,JSONArray cassandraArray,String interval){

      

        Map<String, JSONObject> resultMap = new HashMap<>();

        for (int i = 0; i < insightArray.length(); i++) {
            JSONObject obj = insightArray.getJSONObject(i);
            String date = obj.getString("time_slot").split(" ")[0]; // Extract date

            resultMap.putIfAbsent(date, new JSONObject());
            JSONObject data = resultMap.get(date);

            data.put("date", date);

            // Merge insight data (add numerical values, keep others)
            for (String key : obj.keySet()) {
                if (!key.equals("time_slot")) {
                    if (obj.get(key) instanceof Number) {
                        data.put(key, data.optInt(key, 0) + obj.getInt(key));
                    } else {
                        data.put(key, obj.get(key)); // Keep non-numeric values
                    }
                }
            }
        }
        System.out.println(cassandraArray);
        for (int i = 0; i < cassandraArray.length(); i++) {
            JSONObject obj = cassandraArray.getJSONObject(i);
            String date;
            if(interval.equals("1d") || interval.equals("12h")) {
            	date = obj.getString("hour_slot");
            }
            else {
            	date = obj.getString("date");

            }

            resultMap.putIfAbsent(date, new JSONObject());
            JSONObject data = resultMap.get(date);

            data.put("date", date);

            // Merge cassandra data (sum matching keys, keep unique ones)
            for (String key : obj.keySet()) {
                if (!key.equals("date")) {
                    if (obj.get(key) instanceof Number) {
                        data.put(key, data.optInt(key, 0) + obj.getInt(key));
                    } else {
                        data.put(key, obj.get(key)); // Keep non-numeric values
                    }
                }
            }
        }

        // Convert result map to JSONArray
        JSONArray resultArray = new JSONArray();
        for (JSONObject value : resultMap.values()) {
            resultArray.put(value);
        }

        // Print final merged JSON
        System.out.println(resultArray.toString(4)); // Pretty print
		return resultArray;
    }
    }
