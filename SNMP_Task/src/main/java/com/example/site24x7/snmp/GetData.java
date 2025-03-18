package com.example.site24x7.snmp;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;
import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

@WebServlet("/getData")
public class GetData extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static String SNMP_HOST;
    private static String SNMP_COMMUNITY;
    private static int SNMP_VERSION;
    private static JSONArray snmpData = new JSONArray();

    static {
        try {
            InputStream input = GetData.class.getClassLoader().getResourceAsStream("hostdetails.properties");
            if (input == null) {
                throw new RuntimeException("Properties file not found!");
            }
            Properties properties = new Properties();
            properties.load(input);

            SNMP_HOST = properties.getProperty("SNMP_HOST");
            SNMP_COMMUNITY = properties.getProperty("SNMP_COMMUNITY");
            SNMP_VERSION = Integer.parseInt(properties.getProperty("SNMP_VERSION"));
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        SNMPData();
        JSONObject responseObj = new JSONObject();
        responseObj.put("data", snmpData);

        out.print(responseObj.toString());
        out.flush();
    }

    public static void SNMPData() throws IOException {
        Snmp snmp = new Snmp(new DefaultUdpTransportMapping());
        snmp.listen();

        String totalOID = "1.3.6.1.2.1.2.1.0";
        String totalInterfacesStr = getSnmpValue(snmp, totalOID);

        if (totalInterfacesStr.equals("N/A")) {
            System.out.println("Failed to fetch total interfaces.");
            snmp.close();
            return;
        }

        int totalInterfaces = Integer.parseInt(totalInterfacesStr)-1;
        List<String> oids = Arrays.asList(
        	    "1.3.6.1.2.1.2.2.1.1",  // Interface ID (ifIndex)
        	    "1.3.6.1.2.1.2.2.1.2",  // Interface Name (ifDescr)
        	    "1.3.6.1.2.1.2.2.1.10", // Bytes In
        	    "1.3.6.1.2.1.2.2.1.16", // Bytes Out
        	    "1.3.6.1.2.1.2.2.1.14", // In Errors
        	    "1.3.6.1.2.1.2.2.1.20", // Out Errors
        	    "1.3.6.1.2.1.2.2.1.7",  // Admin Status
        	    "1.3.6.1.2.1.2.2.1.8",  // Oper Status
        	    "1.3.6.1.2.1.2.2.1.13", // Inbound Discards
        	    "1.3.6.1.2.1.2.2.1.19"  // Outbound Discards
        	);



        JSONArray data = new JSONArray();
        for (int i = 1; i <= totalInterfaces-1; i++) {
            String interfaceId = getSnmpValue(snmp, oids.get(0) + "." + i);  // Get Interface ID
            String interfaceName = getSnmpValue(snmp, oids.get(1) + "." + i);
            String inBytes = getSnmpValue(snmp, oids.get(2) + "." + i);
            String outBytes = getSnmpValue(snmp, oids.get(3) + "." + i);
            String inErrors = getSnmpValue(snmp, oids.get(4) + "." + i);
            String outErrors = getSnmpValue(snmp, oids.get(5) + "." + i);
            String adminStatus = getSnmpValue(snmp, oids.get(6) + "." + i);
            String operStatus = getSnmpValue(snmp, oids.get(7) + "." + i);
            String inDiscards = getSnmpValue(snmp, oids.get(8) + "." + i);
            String outDiscards = getSnmpValue(snmp, oids.get(9) + "." + i);

            JSONObject dataobj = new JSONObject();
            dataobj.put("Interface ID", interfaceId);
            dataobj.put("IP", SNMP_HOST);
            dataobj.put("Interface Name", interfaceName);
            dataobj.put("inBytes", inBytes);
            dataobj.put("outBytes", outBytes);
            dataobj.put("inErrors", inErrors);
            dataobj.put("outErrors", outErrors);
            dataobj.put("adminStatus", adminStatus);
            dataobj.put("operationStatus", operStatus);
            dataobj.put("inDiscards", inDiscards);
            dataobj.put("outDiscards", outDiscards);

            data.put(dataobj);
        }

        snmpData=data;
        System.out.println(data.toString(4));
        snmp.close();
    }

    public static String getSnmpValue(Snmp snmp, String oid) {
        Address targetAddress = GenericAddress.parse("udp:" + SNMP_HOST + "/161");
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(SNMP_COMMUNITY));
        target.setAddress(targetAddress);
        target.setRetries(2);
        target.setTimeout(1500);
        target.setVersion(SNMP_VERSION);

        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(oid)));
        pdu.setType(PDU.GET);

        try {
            ResponseEvent response = snmp.send(pdu, target);
            if (response == null || response.getResponse() == null) {
                return "N/A";
            }
            return response.getResponse().get(0).getVariable().toString();
        } catch (IOException e) {
            return "N/A";
        }
    }
}
