package com.example.site24x7.restapi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class JSONMerger{
	public static JSONArray merge(JSONArray insightArray, JSONArray cassandraArray, String interval) {
        System.out.println("-----------------------------------------");
        System.out.println("MYSQL DATA");
        System.out.println("-----------------------------------------");
        System.out.println(insightArray.toString(4));
        System.out.println("-----------------------------------------");
        System.out.println("Cassandra DATA");
        System.out.println("-----------------------------------------");
        System.out.println(cassandraArray.toString(4));

        Map<String, JSONObject> resultMap = new HashMap<>();

        // If interval is "12h" or "24h", just append both arrays and remove duplicates
        if (interval.equals("12h") || interval.equals("24h")) {
            Set<String> uniqueTimeSlots = new HashSet<>();
            JSONArray resultArray = new JSONArray();

            for (int i = 0; i < insightArray.length(); i++) {
                JSONObject obj = insightArray.getJSONObject(i);
                String timeSlot = obj.getString("time_slot");

                if (!uniqueTimeSlots.contains(timeSlot)) {
                    uniqueTimeSlots.add(timeSlot);
                    resultArray.put(obj);
                }
            }

            for (int i = 0; i < cassandraArray.length(); i++) {
                JSONObject obj = cassandraArray.getJSONObject(i);
                String timeSlot = obj.optString("time_slot", obj.optString("hour_slot", obj.optString("date", "")));

                if (!uniqueTimeSlots.contains(timeSlot)) {
                    uniqueTimeSlots.add(timeSlot);
                    resultArray.put(obj);
                }
            }

            System.out.println("-----------------------------------------");
            System.out.println("Result DATA");
            System.out.println("-----------------------------------------");
            System.out.println(resultArray.toString(4));
            return resultArray;
        }

        // Normal merging process for other intervals
        for (int i = 0; i < insightArray.length(); i++) {
            JSONObject obj = insightArray.getJSONObject(i);
            String timeSlot = obj.getString("time_slot");

            resultMap.putIfAbsent(timeSlot, new JSONObject());
            JSONObject data = resultMap.get(timeSlot);
            data.put("time_slot", timeSlot); // Use time_slot instead of date

            for (String key : obj.keySet()) {
                if (!key.equals("time_slot")) {
                    if (obj.get(key) instanceof Number) {
                        data.put(key, data.optInt(key, 0) + obj.getInt(key));
                    } else {
                        data.put(key, obj.get(key));
                    }
                }
            }
        }

        for (int i = 0; i < cassandraArray.length(); i++) {
            JSONObject obj = cassandraArray.getJSONObject(i);
            String timeSlot = obj.optString("time_slot", obj.optString("hour_slot", obj.optString("date", "")));

            if (timeSlot.isEmpty()) continue;

            resultMap.putIfAbsent(timeSlot, new JSONObject());
            JSONObject data = resultMap.get(timeSlot);
            data.put("time_slot", timeSlot); // Use time_slot instead of date

            for (String key : obj.keySet()) {
                if (!key.equals("time_slot") && !key.equals("date") && !key.equals("hour_slot")) {
                    if (obj.get(key) instanceof Number) {
                        data.put(key, data.optInt(key, 0) + obj.getInt(key));
                    } else {
                        data.put(key, obj.get(key));
                    }
                }
            }
        }

        List<JSONObject> sortedList = new ArrayList<>(resultMap.values());
        sortedList.sort(Comparator.comparing(o -> o.getString("time_slot")));

        JSONArray resultArray = new JSONArray(sortedList);
        System.out.println("-----------------------------------------");
        System.out.println("Result DATA");
        System.out.println("-----------------------------------------");
        System.out.println(resultArray.toString(4));
        return resultArray;
    }
}
