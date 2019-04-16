/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UPB;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 *
 * @author Rikpat
 */
public class NewComment extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        response.setContentType("text/html");
        String commenter = request.getSession().getAttribute("UserName").toString();
        String comment = request.getParameter("comment");
        Integer fileID = Integer.parseInt(request.getParameter("fileID"));
        
               
        // validate given input
        if (commenter.isEmpty() || comment.isEmpty() || (comment.length()> 100)) {
            request.getSession().setAttribute("errorMessage", "Please fill in all the fields! Length of comments is less than 100 letters");
            response.sendRedirect(request.getContextPath());
        } else {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://url/FileDB", "example user", "example passwd");
                KeyPair keyPair = CryptoUtils.buildKeyPair();

                PreparedStatement ps = con.prepareStatement("insert into COMMENTS values(?,?,?,?)"); // generates sql query

                ps.setNull(1, java.sql.Types.INTEGER);
                ps.setInt(2, fileID);
                ps.setString(3, commenter);
                ps.setString(4, comment);
                
                if (ps.executeUpdate() == 0) {
                    request.getSession().setAttribute("errorMessage", "Failed to submit comment.");
                }
                ps.close();
                con.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                request.getSession().setAttribute("errorMessage", "Failed to submit comment.");
            } finally {
                response.sendRedirect(request.getContextPath());
            }
        }
    }
}
