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
import java.util.ArrayList;
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
    private ArrayList<Key> clave_session;
    private ArrayList<String> nombre;
    private ArrayList<PublicKey> claves_publicas;
    
    //inicializamos a null;
    public void seguridad()
    {
        this.clavePrivada = null;
        this.clavePublica = null;
        this.claveSecreta = null;
        this.claveSession = null;
        this.clave_session = new ArrayList();
        this.nombre = new ArrayList();
        this.claves_publicas = new ArrayList();
    }
    
    //genera un par de claves nuevas(solo ejecutar si no se encuentra archivo de inicio)
    public boolean crearrsa()
    {
        KeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(4096);
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
    public String encriptarPublica(PublicKey clave,String mensaje)
    {
        try {
            Cipher rsa;
            rsa = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            rsa.init(Cipher.ENCRYPT_MODE, clave);
            byte[] encriptado = rsa.doFinal(mensaje.getBytes());
            String men = "";
            int con = 0;
            for (byte b : encriptado) {
                if(con == 0)
                {
                   men = men+(Integer.toHexString(0xFF & b)); 
                }
                else
                {
                   men = men+"@loki#"+(Integer.toHexString(0xFF & b));
                }
                con++;
            }
            return men;
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
            if(mensaje.contains("@loki#"))
            {
                String [] men = mensaje.split("@loki#");
                byte[] mensaje_en_bytes = new byte[men.length];
                int con = 0;
                for(String s : men)
                {
                    mensaje_en_bytes[con] = (byte)Integer.parseInt(s,16);//Integer.parseInt(s,16)
                    con++;
                }
                byte[] desencriptado = rsa.doFinal(mensaje_en_bytes);
                String des = new String(desencriptado);
                return des;
            }
        }catch(InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException ex)
        {
           System.out.println(ex);
        }
        return null;
    }
    
    //encriptamos con la clave de session 
    public String encriptarSession(Key session,String m)
    {
          // Se obtiene un cifrador AES
          try
          {
            Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
            aes.init(Cipher.ENCRYPT_MODE, session);
            byte[] encriptado = aes.doFinal(m.getBytes());
            String men = "";
            int con = 0;
            for (byte b : encriptado) {
                if(con == 0)
                {
                   men = men+(Integer.toHexString(0xFF & b)); 
                }
                else
                {
                   men = men+"@thor#"+(Integer.toHexString(0xFF & b));
                }
                con++;
            }
            return men;
          }
          catch(InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e)
          {
              System.out.println(e);
          }
          return null;
    }
    //desencriptamos con la clave aes que nos pasen -- sera utilizada para desencriptar los archivos al iniciar el programa
    public String desencriptarAes(Key clave, String m)
    {
        try
        {
            Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
            aes.init(Cipher.DECRYPT_MODE, clave);
            String [] men = m.split("@thor#");
            byte[] mensaje_en_bytes = new byte[men.length];
            int con = 0;
            for(String s : men)
            {
                mensaje_en_bytes[con] = (byte)Integer.parseInt(s,16);//Integer.parseInt(s,16)
                con++;
            }
            byte[] desencriptado = aes.doFinal(mensaje_en_bytes);
            String des = new String(desencriptado);
            return des;
        }catch(InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e)
        {
            System.out.println(e);
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
        }
    }
    
    //devolvemos nuestra clave publica generada la primera ejecucion
    public PublicKey getClavePublica()
    {
        return this.clavePublica;//devolvemos nuestra clave publica
    }

    //devolvemos nuestra clave publica generada la primera ejecucion para enviarla a un cliente o servidor
    public String getClavePublica_envio()
    {
        byte[] ncp = this.clavePublica.getEncoded();
        String clave = new String(Base64.encodeBase64(ncp));
        return clave;//devolvemos nuestra clave publica
    }
    
    //recuperamos nuestra clave de session generada al arrancar el cliente o el servidor
    public Key getClaveSession()
    {
        return this.claveSession;//devolvemos nuestra clave publica
    }
    
    //devolvemos nuestra clave publica generada la primera ejecucion para enviarla a un cliente o servidor
    public String getClaveSession_envio()
    {
        byte[] ncp = this.claveSession.getEncoded();
        String clave = new String(Base64.encodeBase64(ncp));
        return clave;//devolvemos nuestra clave publica
    }
    //recuperamos la clave privada
    public boolean loadcpr(String clave)
    {
        byte[] clavepreB = Base64.decodeBase64(clave);
        byte[] claveB = clavepreB;
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
    //recuperamos la clave secreta o session
    public boolean loadcs(String clave)
    {
        byte[] clavepreB = Base64.decodeBase64(clave);
        byte[] claveB = clavepreB;
        Key key = new SecretKeySpec(claveB, "AES");
        this.claveSecreta = key;
        return true;
    }
    //recuperamos la clave secreta o session
    public Key loadcs_externa(String clave)
    {
        byte[] clavepreB = Base64.decodeBase64(clave);
        byte[] claveB = clavepreB;
        Key key = new SecretKeySpec(claveB, "AES");
        return key;
    }
    //recuperamos la clave publica
    public boolean loadcp(String clave)
    {
        byte[] clavepreB = Base64.decodeBase64(clave);
        byte[] claveB = clavepreB;
        try
        {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            KeySpec keySpec = new X509EncodedKeySpec(claveB);
            PublicKey keyFromBytes = keyFactory.generatePublic(keySpec);
            this.clavePublica = keyFromBytes;
            return true;
        }catch(NoSuchAlgorithmException | InvalidKeySpecException ex)
        {
            System.out.println(ex);
        }
        return false;
    }
    //recuperamos la clave publica
    public PublicKey loadcp_externa(String clave)
    {
        byte[] clavepreB = Base64.decodeBase64(clave);
        byte[] claveB = clavepreB;
        try
        {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            KeySpec keySpec = new X509EncodedKeySpec(claveB);
            PublicKey keyFromBytes = keyFactory.generatePublic(keySpec);
            return keyFromBytes;
        }catch(NoSuchAlgorithmException | InvalidKeySpecException ex)
        {
            System.out.println(ex);
        }
        return null;
    }
    //le pasas(nombre,email o id) segun como se inicialice, te devuelve la clave publica de ese usuario para encriptar los mensajes que le vas a enviar
    public PublicKey devolver_publica(String n)
    {
        if(!this.nombre.isEmpty())
        {
            if(!this.claves_publicas.isEmpty())
            {
                for(int a=0;a < nombre.size();a++)
                {
                    if(nombre.get(a) != null && n.compareToIgnoreCase(nombre.get(a)) == 0)
                    {
                        if(a < claves_publicas.size())
                        {
                            return claves_publicas.get(a);
                        }
                        else
                        {
                            return null;
                        }
                    }
                }
            }   
        }
        return null;
    }
    //le pasas el nombre o id o email segun como se inicialice y te devuelve la clave de session para desencriptar o encriptar los mensajes y archivos que le vas a enviar  
    public Key devolver_session(String n)
    {
        if(!this.nombre.isEmpty())
        {
            if(!this.clave_session.isEmpty())
            {
                for(int a=0;a < nombre.size();a++)
                {
                    if(nombre.get(a) != null && n.compareToIgnoreCase(nombre.get(a)) == 0)
                    {
                        if(a < this.clave_session.size())
                        {
                            return clave_session.get(a);
                        }
                        else
                        {
                            return null;
                        }
                    }
                }                
            }               
        }
        return null;
    }
    //creamos un usuario(id) y su clave publica y privada
    public void crearUser(String n,Key session, PublicKey publica)
    {
        this.nombre.add(n);
        this.clave_session.add(session);//esto se agrega cuando tenemos su id del server
        this.claves_publicas.add(publica);//esto se agrega cuando tenemos su id del server
    }
    //funcion para encriptar con nuestra clave aes y su session 
    public String encriptarMiSessionSuSession(String mensaje,Key sessionSuya)//id del usuario al que se va a enviar
    {
        return this.encriptarSession(sessionSuya,this.encriptarSession(this.claveSession, mensaje));
    }
    // se encripta primero con mi session luego con su session se busca la session en la bd de datos de sesiones activas
    public String encriptarMiSessionSuSession(String id,String mensaje)//id del usuario al que se va enviar
    {
        return this.encriptarSession(this.devolver_session(id),this.encriptarSession(this.claveSession, mensaje));
    }
    //desencripta 
    public String desencriptarMiSessionSuSession(String mensaje,Key sessionSuya)//id del usuario al que se va a enviar
    {
        return this.desencriptarAes(sessionSuya,this.desencriptarAes(this.claveSession, mensaje));
    }
    //desencripta buscando el ultimo en la base de datos
    public String desencriptarMiSessionSuSession(String id,String mensaje,Key sessionSuya)//id del usuario al que se va a enviar
    {
        return this.desencriptarAes(this.devolver_session(id),this.desencriptarAes(this.claveSession, mensaje));
    }
    
}
