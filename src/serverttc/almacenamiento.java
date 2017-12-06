/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverttc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
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
    private int contadorIDS;
    //recupera nuestras claves si existen
    public void recuperarclaves(seguridad se,String clave)
    {
      File archivo=new File("keys.dat");
      if(archivo.exists())
      {
          String lineas[] = leer(archivo);
          if(lineas.length >= 3)
          {
              se.loadcp(lineas[0]);
              se.loadcpr(lineas[1]);
              se.crearSecreta(clave);
              se.crearSessionAes();
              //aqui tenemos todas las claves nuestras
          }
      }  
    }
    
    public void recuperarids(Consola padre)
    {
      File archivo=new File("keys.dat");
      if(archivo.exists())
      {
          String lineas[] = leer(archivo);
          if(lineas.length >= 3)
          {
              this.contadorIDS = Integer.parseInt(lineas[3]);
              padre.addMensaje("\nNumero de usuarios en el sistema -> "+Integer.toString(this.contadorIDS), 'b');
              //aqui tenemos todas las claves nuestras
          }
      }  
    }

    public void recuperarids()
    {
      File archivo=new File("keys.dat");
      if(archivo.exists())
      {
          String lineas[] = leer(archivo);
          if(lineas.length >= 3)
          {
              this.contadorIDS = Integer.parseInt(lineas[3]);
              //aqui tenemos todas las claves nuestras
          }
      }  
    }
    
    //guarda nuestras claves
    public void guardarClaves(String clavepublica,String claveprivada,String clavesecreta)
    {
        File directorio=new File("keys.dat");
        if(directorio.exists())
        {
            //sobreescribimos el archivo
            //creamos el archivo
            try{
                directorio.createNewFile();
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(directorio))) {
                    bw.write(clavepublica);//primera linea clave publica
                    bw.newLine();
                    bw.write(claveprivada);//segunda linea clave privada
                    bw.newLine();
                    bw.write(clavesecreta);//tercera linea clave secreta
                    bw.newLine();
                    bw.write(Integer.toString(this.contadorIDS));
                    bw.newLine();
                } //primera linea clave publica
            }catch(IOException ex)
            {
                System.out.println(ex);
            }            
        }
        else
        {
            //creamos el archivo
            try{
            directorio.createNewFile();
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(directorio))) {
                    bw.write(clavepublica);//primera linea clave publica
                    bw.newLine();
                    bw.write(claveprivada);//segunda linea clave privada
                    bw.newLine();
                    bw.write(clavesecreta);//tercera linea clave secreta
                    bw.newLine();
                    bw.write(Integer.toString(000));
                    bw.newLine();
                } //primera linea clave publica
            }catch(IOException ex)
            {
                System.out.println(ex);
            }
        }
    }
    
    
    //nos sirve para crear un nuevo usuario en el sistema
    public void crearNuevoUsuario(String email, String VerificacionPassword,String nombre, String foto)
    {
        //creamos una carpeta con el email 
        //File directorio=new File(email.replace("@", "arroba"));
        File directorio=new File(email);
        directorio.mkdir();   
        //creamos un archivo vef que contenga la varificacion del password
        String ruta = email+"/vef";//datos del usuario
        String ruta2 = email+"/chat";//este archivo actua como pila si le envian un mensaje se almacena y luego se procesa cuando se confirma se borra esa linea
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
                bw.write(VerificacionPassword);//password
                bw.newLine();
                bw.write(nombre);//nombre
                bw.newLine();
                bw.write(foto);//foto
                bw.newLine();
                this.recuperarids();
                bw.write(Integer.toString(this.contadorIDS++));//id
                bw.newLine();
                this.guardarids();
                bw.close();
            }catch(IOException ex)
            {
                System.out.println("fallo al crear los archivos "+ex.getLocalizedMessage());
            }
        }
    }
    
    public void guardarids()
    {
      File archivo=new File("keys.dat");
      if(archivo.exists())
      {
          String lineas[] = leer(archivo);
          if(lineas.length >= 3)
          {
              this.guardarClaves(lineas[0], lineas[1], lineas[2]);
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
    
    //con esta opcion si no existe el usuario se crea se devuelve el id de usuario
    public String registro(String email,String nombre, String password, String foto)
    {
        if(!this.existeUsuarioBD(email))
        {
            //vamos a crear las carpetas y archivo
            this.crearNuevoUsuario(email, password, nombre, foto);
            return Integer.toString(this.contadorIDS);
        }
        return "00";//este significa que no se ha producido el registro porque existe el usuario
    }
    
    //comprobamos con los datos locales y devolvemos los datos
    public void login(String email,String vefpassword,DataOutputStream canal)
    {  
        String mensaje = "login#odin@false";
        if(existeUsuarioBD(email))
        {
            File archivo=new File(email+"/vef");
            if(archivo.exists())
            {
                String InfoUser [] = this.leer(archivo);
                if(InfoUser[0].compareTo(vefpassword) == 0)
                {
                    mensaje = "login#odin@true#odin@"+InfoUser[1]+"#odin@"+InfoUser[2]+"#odin@"+InfoUser[3]+"#odin@"+email;
                }
            }
        }
        System.out.println(mensaje);
        try
        {
            //encriptar
            canal.writeUTF(mensaje);
        }catch(IOException ex)
        {
        
        }
        
    }
    //esto nos sirve para saber si existe el usuario(servidor)
    public boolean existeUsuarioBD(String email)
    {
        File directorio=new File(email);
        return directorio.exists();
    }
    
    //verificara si el login es correcto
    public boolean login(String email, String VefPass)
    {
        if(this.existeUsuarioLocal(email))
        {
             File vef=new File(email+".dat");
             if(vef.exists() && this.leer(vef).length == 1)
             {
                 //leemos el contenido
                if (VefPass.compareTo(this.leer(vef)[0]) == 0)
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
        File fichero = new File(sFichero);
        File fichero2 = new File(sFichero2);
        return fichero.exists() || fichero2.exists();
    }
    
    //lee un fichero y devuelve si un string
    public String[] leer(File archivo)
    {
        String cadena,cadena3;
        cadena3="";
        try{
        String cadena2 [];
        int contador = 0;
        FileReader f = new FileReader(archivo);
        try (BufferedReader b = new BufferedReader(f)) 
        {
            while((cadena = b.readLine())!=null)
            {
                if(contador == 0)
                {
                    cadena3=cadena3+cadena;
                }
                else
                {
                    cadena3=cadena3+"marijose"+cadena;
                }
                contador++;
            }   
        }
        if(contador == 1)
        {
            cadena2 = new String[1];
            cadena2[0] = cadena3;
        }
        else
        {
            cadena2 = cadena3.split("marijose");
        }
        return cadena2;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(almacenamiento.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(almacenamiento.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
