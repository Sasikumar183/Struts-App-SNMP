package com.example.site24x7.restapi;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.example.action.DatabaseConfig;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.json.JSONObject;


@SuppressWarnings("deprecation")
public class DeleteInterfaceData extends ActionSupport implements ServletRequestAware{
	private static final long serialVersionUID = 1L;
    private InputStream input;
    private HttpServletRequest request;
    
    
    String query = "DELETE FROM inter_details where id = ?";
    String uidQuery="SELECT id FROM snmp.snmp_interface_traffic WHERE interface_id = ? ALLOW FILTERING;";
    String cqlDeleteQuery ="DELETE FROM snmp.snmp_interface_traffic WHERE id = ?";
    public String execute() {
    	if (!"DELETE".equalsIgnoreCase(request.getMethod())) {
            input = new ByteArrayInputStream("Invalid Request Method".getBytes(StandardCharsets.UTF_8));
            return SUCCESS;
        }
    	int id = Integer.parseInt(request.getParameter("id"));
    	String query = " DELETE FROM inter_details where id = ?";

    	try {
    		CqlSession session = DatabaseConfig.getCassandraSession();
            if (session == null || session.isClosed()) {
                 throw new IllegalStateException("Cassandra session is not initialized.");
             }
            
            ResultSet rs = session.execute(uidQuery, id);
            for (Row row : rs) {
                UUID ids = row.getUuid("id");
                session.execute(cqlDeleteQuery, ids);
            }

            Connection con = DatabaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, id);

            int rows = ps.executeUpdate();
            String message = (rows >=0) ? "Deleted Successful" : "No interface with that id";
            JSONObject res = new JSONObject();
            res.put("message",message);
            String jsonString = res.toString(4);
            input = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
            ps.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    	return SUCCESS;
    }
    public InputStream getInput() {
        return input;
    }

    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }
}
