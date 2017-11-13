/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverttc;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author JVAC
 */
public class seguridad {
    
    private PublicKey clavePublica;//clave publica (para compartir servidor y demas usuarios)
    private PrivateKey clavePrivada;//clave privada(solo nosotros)
    private Key claveSecreta;//clave secreta es utilizada para los archivos internos (solo nosotros)
    private Key claveSession;//clave de session se crea cada vez que se abre el programa (para compartir solo con usuarios)
    
    //inicializamos a null;
    public void seguridad()
    {
        this.clavePrivada = null;
        this.clavePublica = null;
        this.claveSecreta = null;
        this.claveSession = null;
    }
    
    //genera un par de claves nuevas(solo ejecutar si no se encuentra archivo de inicio)
    public boolean crearrsa()
    {
        KeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            clavePublica = keyPair.getPublic();//se genera la clave publica
            clavePrivada = keyPair.getPrivate();//se genera la clave privada
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(seguridad.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    //genera una clave de session nueva
    public void crearSessionAes()
    {
        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            this.claveSession = keyGenerator.generateKey();//creada
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(seguridad.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //genera una clave de aes secreta nueva (solo se crea al entrar por primera vez el servidor o cliente)
    public void crearSecreta(String clave)
    {
      try{
      KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
      keyGenerator.init(128);
      this.claveSecreta = new SecretKeySpec(clave.getBytes(),0, 16, "AES");
      }catch(NoSuchAlgorithmException ex)
      {
          System.out.println(ex);
      }
    }
    
    //encripta con la clave publica que le pasemos
    public byte[] encriptarPublica(PublicKey clave,String mensaje)
    {
        try {
            Cipher rsa;
            rsa = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            rsa.init(Cipher.ENCRYPT_MODE, clave);
            byte[] encriptado = rsa.doFinal(mensaje.getBytes());
            return encriptado;
        } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException ex) {
            Logger.getLogger(seguridad.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    //desencripta con nuestra clave privada
    public String desencriptarPrivada(String mensaje)
    {
        try {
        Cipher rsa;
        rsa = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsa.init(Cipher.DECRYPT_MODE, this.clavePrivada);//se desencripta siempre con nuestra clave privada
        byte[] bytesDesencriptados = rsa.doFinal(mensaje.getBytes());
        String textoDesencripado = new String(bytesDesencriptados);
        return textoDesencripado;
        }catch(InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException ex)
        {
           System.out.println(ex);
        }
        return null;
    }
    
    //crea la hash del password 
    public String sha512(String password)
    {
        try{
            MessageDigest md= MessageDigest.getInstance("SHA-512");
            md.update(password.getBytes());
            byte[] mb = md.digest();
            //Arrays.toString(mb) comprobante
            byte[] encoded = Base64.encodeBase64(mb);
            System.out.println(new String(encoded));
            return new String(encoded,"UTF-8");
        }catch(UnsupportedEncodingException | NoSuchAlgorithmException ex)
        {
            System.out.println(ex);
        }
        return null;
    }
    
    //guarda clave aes secreta, clave aes publica y clave aes privada
    //las claves de session de cada usuario se guardan en un archivo individual desde almacenamiento
    public void guardarMisClaves()
    {
        if(this.clavePrivada != null && this.clavePublica != null && this.claveSecreta != null)
        {
            byte[] ncp = this.clavePublica.getEncoded();
            byte[] ncpr = this.clavePrivada.getEncoded();
            byte[] ncs = this.claveSecreta.getEncoded();
            almacenamiento al = new almacenamiento();
            al.guardarClaves(new String(Base64.encodeBase64(ncp)),new String(Base64.encodeBase64(ncpr)),new String(Base64.encodeBase64(ncs)));
            System.out.println("cp-"+new String(Base64.encodeBase64(ncp)));
        }
    }
    
    public PublicKey getClavePublica()
    {
        return this.clavePublica;//devolvemos nuestra clave publica
    }

    public Key getClaveSession()
    {
        return this.claveSession;//devolvemos nuestra clave publica
    }
    
    public boolean loadcpr(String clave)
    {
        byte[] claveB = clave.getBytes();
        try
        {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            KeySpec keySpec = new PKCS8EncodedKeySpec(claveB);
            PrivateKey keyFromBytes = keyFactory.generatePrivate(keySpec);
            this.clavePrivada = keyFromBytes;
            return true;
        }catch(NoSuchAlgorithmException | InvalidKeySpecException ex)
        {
            System.out.println(ex);
        }
        return false;
    }
    
    public boolean loadcp(String clave)
    {
        byte[] claveB = clave.getBytes();
        try
        {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            KeySpec keySpec = new X509EncodedKeySpec(claveB);
            PublicKey keyFromBytes = keyFactory.generatePublic(keySpec);
            this.clavePublica = keyFromBytes;
            byte[] ncp = this.clavePublica.getEncoded();
            System.out.println("cp-"+new String(Base64.encodeBase64(ncp)));
            return true;
        }catch(NoSuchAlgorithmException | InvalidKeySpecException ex)
        {
            System.out.println(ex);
        }
        return false;
    }
    
}
