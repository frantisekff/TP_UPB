/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UPB;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.passay.PasswordData;

/**
 *
 * @author Rikpat
 */
public class Login extends HttpServlet {
    private static final String REGEX = "[a-zA-Z0-9.]{1,15}";
  
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String userName = request.getParameter("userName");
        String pass = request.getParameter("pass");
        if ((request.getSession().getAttribute("timeout") != null) && (Long.parseLong(request.getSession().getAttribute("timeout").toString()) + TimeUnit.SECONDS.toMillis(30) > System.currentTimeMillis())) {
            request.getSession().setAttribute("errorMessage", "Please wait for 30 seconds before logging in again!");
            response.sendRedirect(request.getContextPath());
            return;
        }
        
        Pattern p = Pattern.compile(REGEX);
        Matcher m = p.matcher(userName);   // get a matcher object
   
        //validate username
        if(!m.matches()){
            request.getSession().setAttribute("errorMessage", "User name must contain only letters a-z, A-Z, numbers 0-9 and dot");
            response.sendRedirect(request.getContextPath());
        }   
        // validate given input
        else if (userName.isEmpty() || pass.isEmpty()) {
            request.getSession().setAttribute("errorMessage", "Please fill in all the fields!");
            response.sendRedirect(request.getContextPath());
        } else if (!Security.isPasswordSecure(new PasswordData(userName,pass), getServletContext())) {
            request.getSession().setAttribute("errorMessage", "<p>A password is said to be strong if it satisfies the following criteria:\n <br>"
                    + "  <li>It contains at least one lowercase English character.</li>"
                    + "  <li>It contains at least one uppercase English character.</li>"
                    + "  <li>It contains at least one special character. The special characters are: .!@#$%^&*()-+</li>"
                    + "  <li>Its length is at least 8</li>"
                    + "  <li>It contains at least one digit.</li>"
                    + " <li>It doesn't contain dictonary words.</li>"
                    + "</ul>");
            response.sendRedirect(request.getContextPath());
        } else {
            // inserting data into mysql database 
            // create a test database and student table before running this to create table
            //create table student(name varchar(100), userName varchar(100), pass varchar(100), addr varchar(100), age int, qual varchar(100), percent varchar(100), year varchar(100));
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://url/FileDB", "example user", "example passwd");
                KeyPair keyPair = CryptoUtils.buildKeyPair();

                PreparedStatement ps = con.prepareStatement("select * from USERS where NAME = ?"); // generates sql query

                ps.setString(1, userName);
                ResultSet rs = ps.executeQuery();
                rs.first();
                
                try {
                    byte[] salt = rs.getBytes("SALT");
                    String hashedPassword = rs.getString("PASSWORD");
                    HttpSession s = request.getSession();
                    if (hashedPassword.equals(Security.hash(pass, salt))) {
                        s.setAttribute("UserName", rs.getString("NAME"));
                        s.setAttribute("PrivateKey", rs.getString("PRIVATEKEY"));
                    } else {
                        s.setAttribute("errorMessage", "Incorrect Username or Password!");
                        s.setAttribute("timeout",System.currentTimeMillis());
                    }

                } catch (Exception e) {
                    request.getSession().setAttribute("errorMessage", "Incorrect Username or Password!");
                }
                ps.close();
                con.close();
                response.sendRedirect(request.getContextPath());
            } catch (NoSuchAlgorithmException | ClassNotFoundException | SQLException e) {
                // TODO Auto-generated catch block
                response.getWriter().println(CryptoUtils.displayErrorForWeb(e));
            }
        }
    }
}
