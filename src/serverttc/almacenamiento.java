/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverttc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JVAC
 */
public class almacenamiento {
    //todo en esta clase se guarda en formato texto la encriptacion se hace con la clave aes secreta personal e intransferible 
    
    //recupera nuestras claves si existen
    public void recuperarclaves(seguridad seg)
    {
      File directorio=new File("keys");
      if(directorio.exists())
      {
          
      }  
    }
    
    //guarda nuestras claves
    public void guardarClaves(String clavepublica,String claveprivada,String clavesecreta)
    {
        File directorio=new File("keys");
        if(directorio.exists())
        {
            //sobreescribimos el archivo
            
        }
        else
        {
            //creamos el archivo
            try{
            directorio.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(directorio));
            bw.write(clavepublica);//primera linea clave publica
            bw.newLine();
            bw.write(claveprivada);//segunda linea clave privada
            bw.newLine();
            bw.write(clavesecreta);//tercera linea clave secreta
            bw.close();
            }catch(Exception ex)
            {
                System.out.println(ex);
            }
        }
    }
    
    //nos sirve para crear un nuevo usuario en el sistema
    public void crearNuevoUsuario(String email, String VerificacionPassword)
    {
        //creamos una carpeta con el email 
        //File directorio=new File(email.replace("@", "arroba"));
        File directorio=new File(email);
        directorio.mkdir();   
        //creamos un archivo vef que contenga la varificacion del password
        String ruta = "/"+email+"/vef";
        String ruta2 = "/"+email+"/chat";//este archivo actua como pila si le envian un mensaje se almacena y luego se procesa cuando se confirma se borra esa linea
        File archivo2 = new File(ruta2);
        File archivo = new File(ruta);
        BufferedWriter bw;
        if(!archivo.exists()) 
        {
            try
            {
                archivo.createNewFile();
                archivo2.createNewFile();
                bw = new BufferedWriter(new FileWriter(archivo));
                bw.write(VerificacionPassword);
                bw.close();
            }catch(IOException ex)
            {
                System.out.println("fallo al crear los archivos "+ex.getLocalizedMessage());
            }
        }
    }
    //nos sirve para crear un nuevo usuario en el sistema
    public void crearNuevoUsuarioLocal(String nickname, String VerificacionPassword)
    {
        //creamos una carpeta con el email 
        //File directorio=new File(email.replace("@", "arroba"));
        //creamos un archivo vef que contenga la varificacion del password
        String ruta = nickname+".dat";
        String ruta2 = "exis";
        File archivo = new File(ruta);
        File archivo2 = new File(ruta2);
        BufferedWriter bw,bw2;
        if(!archivo.exists()) 
        {
            try
            {
                archivo.createNewFile();
                archivo2.createNewFile();
                bw = new BufferedWriter(new FileWriter(archivo));
                bw2 = new BufferedWriter(new FileWriter(archivo2));
                bw.write(VerificacionPassword);
                bw2.write(VerificacionPassword);
                bw.close();
                bw2.close();
            }catch(IOException ex)
            {
                System.out.println("fallo al crear los archivos"+ex.getLocalizedMessage());
            }
        }
    }
    
    //agregamos la clave publica a la carpeta de ese usuario
    public void crearClavePublica(String email, String clave)
    {
        //creamos clavePublica en su carpeta
        
    }
    
    //esto nos sirve para saber si existe el usuario(servidor)
    public boolean existeUsuarioBD(String email)
    {
        File directorio=new File(email);
        return directorio.exists();
    }
    
    //verificara si el login es correcto
    public boolean login(String email, byte[] VefPass)
    {
        if(this.existeUsuarioLocal(email))
        {
             File vef=new File(email+".dat");
             if(vef.exists())
             {
                 String pass = new String(VefPass);
                 //leemos el contenido
                if (pass.compareTo(this.leer(vef)) == 0)
                {
                    return true;
                }
             }
        }
        return false;
    }
    
    //fichero principal para saber si en el pc existe ese usuario(servidor y cliente) este archivo esta en raiz del programa
    public boolean existeUsuarioLocal(String idUser)
    {
        String sFichero = idUser+".dat";
        String sFichero2 = "exis";
        System.out.println(sFichero);
        File fichero = new File(sFichero);
        File fichero2 = new File(sFichero2);
        if(fichero.exists() || fichero2.exists())
        {
            return true;
        }
        return false;
    }
    
    //lee un fichero y devuelve si un string
    public String leer(File archivo)
    {
        String cadena;
        try{
        FileReader f = new FileReader(archivo);
        BufferedReader b = new BufferedReader(f);
        while((cadena = b.readLine())!=null)
        {
            System.out.println(cadena);
        }
        b.close();
        return cadena;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(almacenamiento.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(almacenamiento.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
