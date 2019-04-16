package UPB;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils {

    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    public static void encrypt(PublicKey key, InputStream inputStream, OutputStream outputStream) throws Exception {
        // vygenerovanie inicializacneho vektora
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[16];
        secureRandom.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        doCrypto(Cipher.ENCRYPT_MODE, key, inputStream, outputStream, ivParameterSpec);

    }

    public static void decrypt(PrivateKey key, InputStream inputStream, OutputStream outputStream) throws Exception {
        // nacitanie IV
        byte[] IvContent = new byte[20]; // iv + length iv
        inputStream.read(IvContent, 0, 20);
        ByteBuffer byteBuffer = ByteBuffer.wrap(IvContent);
        int ivLength = byteBuffer.getInt();
        System.out.println(ivLength);
        if (ivLength < 12 || ivLength > 16) { // check input parameter
                throw new IllegalArgumentException("invalid iv length");
        }
        byte[] IVbyte = new byte[ivLength];
        byteBuffer.get(IVbyte);

        String iv2STR = new String(IVbyte, StandardCharsets.UTF_8);
        System.out.println(iv2STR);

        IvParameterSpec ivParameterSpec = new IvParameterSpec(IVbyte);

        doCrypto(Cipher.DECRYPT_MODE, key, inputStream, outputStream, ivParameterSpec);
    }

    private static void doCrypto(int cipherMode, Key secretKey, InputStream inputStream, OutputStream outputStream, IvParameterSpec ivParameterSpec) throws Exception {
        try {
            SecretKey symetricKey;
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            if (cipherMode == Cipher.ENCRYPT_MODE) {
                byte[] iv = ivParameterSpec.getIV();
                ByteBuffer byteBuffer = ByteBuffer.allocate(4 + iv.length);
                System.out.println("ivlength " + iv.length);
                byteBuffer.putInt(iv.length);
                byteBuffer.put(iv);
                byte[] ivByte = byteBuffer.array();
                System.out.println("Length of ivByte" + ivByte.length);

                outputStream.write(ivByte);
                //write key into file
                symetricKey = generateKey();
                outputStream.write(encryptRSA(secretKey,symetricKey.getEncoded()));

            } else {
                //remove initialization vector
                //nputStream.read(new byte[20], 0, 20);
                //read key from file
                byte[] b = new byte[64];
                inputStream.read(b, 0, 64);
                b = decryptRSA(secretKey,b);
                symetricKey = new SecretKeySpec(b, 0, b.length, "AES");
            }

            cipher.init(cipherMode, symetricKey, ivParameterSpec);
            int chunk = 16 * 1000;//kusok po ktorom sifrujem/desifrujem
            byte[] decBytes = new byte[chunk];
            int n = inputStream.read(decBytes, 0, decBytes.length);

            while (n > 0) {
                //samotne sifrovanie/desifrovanie			
                byte[] out = cipher.update(decBytes, 0, n);
                outputStream.write(out);
                n = inputStream.read(decBytes, 0, decBytes.length);
            }
            byte[] outputBytes = cipher.doFinal();
            outputStream.write(outputBytes);

            inputStream.close();
            outputStream.close();

            System.out.println("De/Encryption Successfully");
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | IOException ex) {
            System.out.println("Error encrypting/decrypting file " + ex.getMessage());
            throw new Exception("Error encrypting/decrypting file " + ex.getMessage());

        }
    }

    public static KeyPair buildKeyPair() throws NoSuchAlgorithmException {
            final int keySize = 512;
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(keySize);      
            return keyPairGenerator.genKeyPair();
    }

    public static byte[] encryptRSA(Key publicKey, byte[] message) throws Exception {
            Cipher cipher = Cipher.getInstance("RSA");  
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);  

            return cipher.doFinal(message);  
    }

    public static byte[] decryptRSA(Key privateKey, byte [] encrypted) throws Exception {
            Cipher cipher = Cipher.getInstance("RSA");  
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            return cipher.doFinal(encrypted);
    }

    public static SecretKey generateKey() throws NoSuchAlgorithmException, IOException {
            // Generovanie kluca
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128); // 128 default; 192 and 256 also possible
            SecretKey key = keyGenerator.generateKey();

            return key;
    }

    public static String displayErrorForWeb(Throwable t) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            String stackTrace = sw.toString();
            return stackTrace.replace(System.getProperty("line.separator"), "<br/>\n");
    }
}
