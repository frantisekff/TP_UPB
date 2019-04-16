package UPB;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class FileDownload extends HttpServlet {
    private final int ARBITARY_SIZE = 1048;
    //private static final String UPLOAD_DIRECTORY = "C:/apache-tomcat-9.0.12-windows-x64";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        HttpSession sess = req.getSession();
        resp.setContentType("application/octet-stream");
        if(sess.getAttribute("UserName") == null) {
            sess.setAttribute("errorMessage", "You have to be logged in to download!");
            resp.sendRedirect(req.getContextPath());
            return;
        }
        String privateKey = sess.getAttribute("PrivateKey").toString();
        try {
            //Database
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://url/FileDB", ""example user"", "password"); 
            PreparedStatement ps = con.prepareStatement("select * from FILES where ID = ?"); // generates sql query
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            //Reading data
            rs.first();
            BufferedInputStream is = new BufferedInputStream(rs.getBlob("file").getBinaryStream());
            
            resp.setHeader("Content-disposition", "attachment; filename=" + rs.getString("filename"));
            byte[] keybyte = Base64.getDecoder().decode(privateKey);
            PrivateKey pkey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(keybyte));
            OutputStream out = resp.getOutputStream(); 
            CryptoUtils.decrypt(pkey, is, out);
            ps.close();
            con.close();
        } catch (Exception ex) {
            System.out.println("Download failed " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}