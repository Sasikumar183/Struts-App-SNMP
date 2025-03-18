package com.example.site24x7.snmp;

import java.sql.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class StoreData {
    private static final Map<Integer, Long> previousInTraffic = new HashMap<>();
    private static final Map<Integer, Long> previousOutTraffic = new HashMap<>();

    static void fetchData() {
        String apiURL = "http://localhost:8080/SNMP_Task/GetData";
        StringBuilder data = new StringBuilder();

        try {
            URL url = new URL(apiURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                System.err.println("Failed to fetch data: HTTP " + conn.getResponseCode());
                return;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    data.append(line);
                }
            }

            if (data.length() == 0) {
                System.err.println("Error: API response is empty.");
                return;
            }

            JSONObject jsonobj = new JSONObject(data.toString());
            JSONArray dataArray = jsonobj.getJSONArray("data");

            try (Connection con = DatabaseConfig.getConnection()) {
                if (con == null || con.isClosed()) {
                    System.err.println("Error: Database connection is null or closed.");
                    return;
                }

                String interfaceQuery = "INSERT INTO interface (id, interface_name, IP) VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE interface_name = ?";

                String detailsQuery = "INSERT INTO inter_details (id, in_traffic, out_traffic, in_error, out_error, in_discard, out_discard, admin_status, oper_status, collected_time) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";

                try (PreparedStatement interfaceStmt = con.prepareStatement(interfaceQuery);
                     PreparedStatement detailsStmt = con.prepareStatement(detailsQuery)) {

                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject obj = dataArray.getJSONObject(i);

                        int interfaceId = obj.optInt("Interface ID", -1);
                        String interfaceName = obj.optString("Interface Name", "Unknown");
                        String IP = obj.optString("IP", "0.0.0.0");
                        long inBytes = obj.optLong("inBytes", 0);
                        long outBytes = obj.optLong("outBytes", 0);
                        int inErrors = obj.optInt("inErrors", 0);
                        int outErrors = obj.optInt("outErrors", 0);
                        int inDiscards = obj.optInt("inDiscards", 0);
                        int outDiscards = obj.optInt("outDiscards", 0);
                        int adminStatus = obj.optInt("adminStatus", 0);
                        int operationStatus = obj.optInt("operationStatus", 0);

                        long prevInBytes = previousInTraffic.getOrDefault(interfaceId, 0L);
                        long prevOutBytes = previousOutTraffic.getOrDefault(interfaceId, 0L);

                        long deltaIn = Math.max(inBytes - prevInBytes, 0);
                        long deltaOut = Math.max(outBytes - prevOutBytes, 0);

                        interfaceStmt.setInt(1, interfaceId);
                        interfaceStmt.setString(2, interfaceName);
                        interfaceStmt.setString(3, IP);
                        interfaceStmt.setString(4, interfaceName);
                        interfaceStmt.executeUpdate();

                        detailsStmt.setInt(1, interfaceId);
                        detailsStmt.setLong(2, deltaIn);
                        detailsStmt.setLong(3, deltaOut);
                        detailsStmt.setInt(4, inErrors);
                        detailsStmt.setInt(5, outErrors);
                        detailsStmt.setInt(6, inDiscards);
                        detailsStmt.setInt(7, outDiscards);
                        detailsStmt.setInt(8, adminStatus);
                        detailsStmt.setInt(9, operationStatus);
                        detailsStmt.executeUpdate();

                        previousInTraffic.put(interfaceId, inBytes);
                        previousOutTraffic.put(interfaceId, outBytes);
                    }
                }
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.err.println("Error fetching API data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}