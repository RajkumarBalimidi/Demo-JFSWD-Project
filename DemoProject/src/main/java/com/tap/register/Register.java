package com.tap.register;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@MultipartConfig(
	    fileSizeThreshold = 1024 * 1024 * 1,  // 1 MB
	    maxFileSize = 1024 * 1024 * 10,       // 10 MB
	    maxRequestSize = 1024 * 1024 * 15     // 15 MB
	)
public class Register extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        Part filePart = request.getPart("image"); // Retrieves the file part from the form

        InputStream imageStream = filePart.getInputStream(); // Get the input stream of the image file
      
        // Database credentials
        String dbURL = "jdbc:mysql://localhost:3306/demoproject";
        String dbUser = "root";
        String dbPass = "MySQL";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load MySQL JDBC driver
            Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);

            String sql = "INSERT INTO demoproject (name, email, image) VALUES (?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, name);
            statement.setString(2, email);

           
            statement.setBlob(3, imageStream); // Set the binary stream of the image as BLOB
            
            


            int row = statement.executeUpdate();
            if (row > 0) {
                response.sendRedirect("success.jsp");
            }

            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            response.getWriter().println("Error: " + ex.getMessage());
        }
        finally {
            if (imageStream != null) {
                imageStream.close();
            }
            filePart.delete();  // Delete the file part explicitly
        }
    }
}
