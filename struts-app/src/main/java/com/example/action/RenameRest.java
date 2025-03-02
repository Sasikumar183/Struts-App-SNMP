package com.example.action;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.ServletRequestAware;

import javax.servlet.http.HttpServletRequest;

@SuppressWarnings("deprecation")
public class RenameRest extends ActionSupport implements ServletRequestAware {

    private static final long serialVersionUID = 1L;
    private InputStream input;
    private HttpServletRequest request;

    public String execute() {
        String nameParam = request.getParameter("name");
        String nameFromDB = "Not Found"; // Default value if no result is found
        int id = -1; // Default invalid ID

        // Safe parsing of ID
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            input = new ByteArrayInputStream("Invalid ID format".getBytes(StandardCharsets.UTF_8));
            return SUCCESS;
        }

        // Database query using inline connection
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT username FROM user WHERE user_id=" + id)) {

            if (rs.next()) {
                nameFromDB = rs.getString("username");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            input = new ByteArrayInputStream("Database error".getBytes(StandardCharsets.UTF_8));
            return SUCCESS;
        }

        // Response message
        String message = (nameParam != null)
                ? "Interface name is renamed to: " + nameParam + " (DB name: " + nameFromDB + ")"
                : "No interface name provided (DB name: " + nameFromDB + ")";

        input = new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8));
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
