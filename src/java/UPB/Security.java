//////////////////////////////////////////////////////////////////////////
// TODO:                                                                //
// Uloha1: Vytvorit funkciu na bezpecne generovanie saltu.              //
// Uloha2: Vytvorit funkciu na hashovanie.                              //
// Je vhodne vytvorit aj dalsie pomocne funkcie napr. na porovnavanie   //
// hesla ulozeneho v databaze so zadanym heslom.                        //
//////////////////////////////////////////////////////////////////////////
package UPB;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Random;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.servlet.ServletContext;
import org.passay.*;
import org.passay.dictionary.FileWordList;
import org.passay.dictionary.WordListDictionary;


public class Security {
    
    protected static String hash(String password, byte[] salt) {  
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 100000, 512);
            SecretKey key = skf.generateSecret(spec);
            byte[] res = key.getEncoded();
            return Base64.getEncoder().encodeToString(res);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
    
    protected static byte[] getSalt() {
        Random random = new Random();
        byte[] saltBytes = new byte[32];
        random.nextBytes(saltBytes);
        return saltBytes;
    }
    
    protected static boolean isPasswordSecure(PasswordData password, ServletContext context) {
        try {
            PasswordValidator validator = new PasswordValidator(
                new LengthRule(8, 25),
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Special, 1),
                new WhitespaceRule(),
                new UsernameRule(),
                //new DictionaryRule(new WordListDictionary(new FileWordList(new RandomAccessFile(context.getRealPath("/resources/sk-50k.txt"),"r")))),
                new DictionaryRule(new WordListDictionary(new FileWordList(new RandomAccessFile(context.getRealPath("/resources/top10000.txt"),"r")))));
            RuleResult rr = validator.validate(password);
            System.out.println(rr.getDetails());
            return rr.isValid();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}

