package UPB;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.sql.rowset.serial.SerialBlob;

public class FileUpload extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String REGEX = "[a-zA-Z0-9]{1,15}[.][a-zA-Z0-9]{1,7}";
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Part filePart = request.getPart("file");
        if (filePart == null) {
            request.getSession().setAttribute("errorMessage", "No file uploaded!");
            response.sendRedirect(request.getContextPath()+"/users");
            return;
        }
        
        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        Pattern p = Pattern.compile(REGEX);
        Matcher m = p.matcher(fileName);   // get a matcher object
               
        //validate username
        if(!m.matches()){
            request.getSession().setAttribute("errorMessage", "File name must contain only letters a-z, A-Z and numbers 0-9");
            response.sendRedirect(request.getContextPath()+"/users");
            return;
        }                    
        
        String publicKey = request.getParameter("publicKey");
        String uploader = request.getSession().getAttribute("UserName").toString();
        System.out.println(uploader);
        String owner = request.getParameter("userName");
        InputStream inputStream = filePart.getInputStream();
        ByteArrayOutputStream encrypted = new ByteArrayOutputStream();
        try {
            byte[] keybyte = Base64.getDecoder().decode(publicKey);
            PublicKey pkey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keybyte));
            CryptoUtils.encrypt(pkey, inputStream, encrypted);
        } catch (Exception E){
            System.out.println("Could not encrypt file");
        }
        Connection conn = null; // connection to the database
        String message = null;  // message will be sent back to client

        try {
            // connects to the database
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            conn = DriverManager.getConnection("jdbc:mysql://url/FileDB", "example user", "example passwd");

            // constructs SQL statement
            String sql = "INSERT INTO FILES values (?, ?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setNull(1, java.sql.Types.INTEGER);
            statement.setString(2, fileName);
            if (encrypted != null) {
                // fetches input stream of the upload file for the blob column
                statement.setBlob(3, new SerialBlob(encrypted.toByteArray()));
            }
            statement.setString(4, uploader);
            statement.setString(5, owner);
            
            // sends the statement to the database server
            int row = statement.executeUpdate();
            if (row > 0) {
                request.getSession().setAttribute("message", "File successfully uploaded!");
            }
        } catch (SQLException ex) {
            message = "ERROR: " + ex.getMessage();
            ex.printStackTrace();
        } finally {
            if (conn != null) {
                // closes the database connection
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            // sets the message in request scope
            response.sendRedirect(request.getContextPath()+"/users");
        }
    }
}
