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
import java.sql.SQLException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class Register extends HttpServlet {

    private static final String REGEX = "[a-zA-Z0-9.]{1,15}";
    
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
       
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String userName = request.getParameter("userName");
        String pass = request.getParameter("pass");
        
        Pattern p = Pattern.compile(REGEX);
        Matcher m = p.matcher(userName);   // get a matcher object
               
        //validate username
        if(!m.matches()){
            request.getSession().setAttribute("errorMessage", "User name must contain only letters a-z, A-Z and numbers 0-9");
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

                PreparedStatement ps = con.prepareStatement("insert into USERS values(?,?,?,?,?)"); // generates sql query

                byte[] generatedSalt = Security.getSalt();
                String hashedPasswd = Security.hash(pass, generatedSalt);

                System.out.println("generatedSalt " + Base64.getEncoder().encodeToString(generatedSalt));
                System.out.println("hashedPasswd " + hashedPasswd);

                ps.setString(1, userName);
                ps.setString(2, hashedPasswd);
                ps.setString(3, Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
                ps.setString(4, Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
                ps.setBytes(5, generatedSalt);

                if (ps.executeUpdate() != 0) {
                    HttpSession s = request.getSession();
                    s.setAttribute("UserName", userName);
                    s.setAttribute("PrivateKey", Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
                    s.setAttribute("message", "Succesfully registered and logged in");
                } else {
                    request.getSession().setAttribute("errorMessage", "Failed to register. Username probably exists");
                }
                ps.close();
                con.close();
            } catch (NoSuchAlgorithmException | ClassNotFoundException | SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                request.getSession().setAttribute("errorMessage", "Failed to register. Username probably exists");
            } finally {
                response.sendRedirect(request.getContextPath());
            }
        }
    }
}
