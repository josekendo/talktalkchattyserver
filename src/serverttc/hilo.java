/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverttc;
import java.io.*;
import java.net.*;
import java.security.Key;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.*;
/**
 *
 * @author JVAC
 */
public class hilo extends Thread {

    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;
    private int idSessio;
    private Consola padre;
    private Key clave_session;//todas las conexiones se cifraran con esta 
    private PublicKey clave_publica;//el intercambio de claves sera con esta
    private String idusuario;
    private long fechaultimaconexion;
    public hilo(Socket socket, int id, Consola p) {
        this.socket = socket;
        this.idSessio = id;
        this.padre = p;
        this.idusuario = null;
        Date date = new Date();
        this.fechaultimaconexion = date.getTime();
        try {
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(hilo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void desconnectar() {
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(hilo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public void run() {
        String accion;
        try {
            dos.writeUTF("Sbienvenida#odin@"+padre.recuperarPublica());//esto pasa una vez en la conexion por lo que si pierde los paquetes deberia volver a establecer conexion
            boolean seguir = true;
            while (seguir)
            {
                dos.writeUTF("vivo");
                accion = dis.readUTF();
                if(accion.compareTo("") != 0 && !(accion.contains("vivo")))
                {
                    if(accion.split("#odin@").length >= 2 && accion.split("#odin@")[0].compareToIgnoreCase("Cbienvenida") == 0)//recibimos la bienvenida del cliente con su clave publica
                    {
                        String partes [] = accion.split("#odin@");
                        this.clave_publica = padre.recuperarSE().loadcp_externa(partes[1]);
                        String mensaje = "CVok#odin@"+padre.recuperarSE().getClaveSession_envio();
                        mensaje = padre.recuperarSE().encriptarPublica(clave_publica,mensaje);//encriptamos nuestra clave de session con su clave publica y se la enviamos
                        dos.writeUTF(mensaje);//ahora le informamos que tenemos la clave publica que nos envie la de session
                    }
                    else if(padre.recuperarSE().desencriptarPrivada(accion) != null && padre.recuperarSE().desencriptarPrivada(accion).split("#odin@").length >= 2 && padre.recuperarSE().desencriptarPrivada(accion).split("#odin@")[0].compareToIgnoreCase("CSok") == 0)
                    {
                        //aqui nos viene encriptado con nuestra clave publica(2)
                        String partes [] = padre.recuperarSE().desencriptarPrivada(accion).split("#odin@");
                        this.clave_session = padre.recuperarSE().loadcs_externa(partes[1]);
                        //ya tenemos la clave publica y de session de este cliente
                        this.padre.addMensaje("\nConfirmacion de enlace seguro con conexion -> "+this.idSessio,'g');
                        dos.writeUTF(padre.recuperarSE().encriptarMiSessionSuSession("CONFISECURITY#odin@true", clave_session));
                    }
                    else if(accion.contains("#hela@") == false && padre.recuperarSE().desencriptarMiSessionSuSession(accion, clave_session).split("#odin@").length >= 2 && padre.recuperarSE().desencriptarMiSessionSuSession(accion, clave_session).split("#odin@")[0].compareToIgnoreCase("existEmail") == 0)
                    {
                        String partes [] = padre.recuperarSE().desencriptarMiSessionSuSession(accion, clave_session).split("#odin@");
                        String mensaje = "existEmail#odin@"+padre.comprobarEMAIL(partes[1])+"#odin@"+partes[1];
                        dos.writeUTF(padre.recuperarSE().encriptarMiSessionSuSession(mensaje, clave_session));
                    }
                    else if(accion.contains("#hela@") == true &&padre.recuperarSE().desencriptarMiSessionSuSession(accion.split("#hela@")[0], clave_session).split("#odin@").length >= 2 && padre.recuperarSE().desencriptarMiSessionSuSession(accion.split("#hela@")[0], clave_session).split("#odin@")[0].compareToIgnoreCase("RegistroUser") == 0)
                    {
                         String partes [] = padre.recuperarSE().desencriptarMiSessionSuSession(accion.split("#hela@")[0], clave_session).split("#odin@");
                         String imagen = accion.split("#hela@")[1];
                         almacenamiento al = new almacenamiento();
                         //primero email, nombre, password, foto
                         String respuesta = al.registro(partes[1],partes[2],partes[3],imagen);
                         String mensaje = "contestRegistro#odin@"+padre.comprobarEMAIL(partes[1])+"#odin@"+respuesta;
                         dos.writeUTF(padre.recuperarSE().encriptarMiSessionSuSession(mensaje, clave_session));
                         
                    }
                    else if(accion.contains("#hela@") == true &&padre.recuperarSE().desencriptarMiSessionSuSession(accion.split("#hela@")[0], clave_session).split("#odin@").length >= 2 && padre.recuperarSE().desencriptarMiSessionSuSession(accion.split("#hela@")[0], clave_session).split("#odin@")[0].compareToIgnoreCase("CambioImagen") == 0)
                    {
                         String partes [] = padre.recuperarSE().desencriptarMiSessionSuSession(accion.split("#hela@")[0], clave_session).split("#odin@");
                         String imagen = accion.split("#hela@")[1];
                         almacenamiento al = new almacenamiento();
                         //primero email, nombre, password, foto
                         if(this.idusuario.compareToIgnoreCase(partes[1]) == 0)
                         {
                            al.cambiarImagenPerfil(partes[1],imagen);
                         }
                         
                    }
                    else if(accion.contains("#hela@") == true &&padre.recuperarSE().desencriptarMiSessionSuSession(accion.split("#hela@")[0], clave_session).split("#odin@").length >= 2 && padre.recuperarSE().desencriptarMiSessionSuSession(accion.split("#hela@")[0], clave_session).split("#odin@")[0].compareToIgnoreCase("creGrupo") == 0)
                    {
                         String partes [] = padre.recuperarSE().desencriptarMiSessionSuSession(accion.split("#hela@")[0], clave_session).split("#odin@");
                         String imagen = accion.split("#hela@")[1];
                         almacenamiento al = new almacenamiento();
                         //primero email, nombre, password, foto
                         al.crearGrupo(partes[1], imagen, partes[2], partes[3]);
                         
                    }
                    else if(padre.recuperarSE().desencriptarMiSessionSuSession(accion, clave_session).split("#odin@").length >= 2 && padre.recuperarSE().desencriptarMiSessionSuSession(accion, clave_session).split("#odin@")[0].compareToIgnoreCase("LoginUser") == 0)
                    {
                        String partes [] = padre.recuperarSE().desencriptarMiSessionSuSession(accion, clave_session).split("#odin@");
                        //segundo valor es el email, tercero la verificacion del password 
                        almacenamiento al = new almacenamiento();
                        String devolucion [] = al.loginUser(partes[1], partes[2]);
                        String partes2[] = devolucion[0].split("#odin@");
                        System.out.println("Usuario "+partes2[1]+"-"+partes2[2]+" hace login -> "+devolucion[0]+"-"+devolucion[1]);
                        if(!(partes2[1].compareToIgnoreCase("00") == 0))
                        {
                            this.idusuario = partes2[1];
                        }
                        dos.writeUTF(padre.recuperarSE().encriptarMiSessionSuSession(devolucion[0], clave_session)+"#hela@"+devolucion[1]);
                    }
                    else if(padre.recuperarSE().desencriptarMiSessionSuSession(accion, clave_session).split("#odin@").length >= 2 && padre.recuperarSE().desencriptarMiSessionSuSession(accion, clave_session).split("#odin@")[0].compareToIgnoreCase("searchUser") == 0)
                    {
                        String partes [] = padre.recuperarSE().desencriptarMiSessionSuSession(accion, clave_session).split("#odin@");
                        almacenamiento al = new almacenamiento();
                        System.out.println("busqueda de usuario "+partes[1]);
                        String us []= al.buscarUsuarioID(partes[1]);
                        String mensaje;
                        if(us != null)
                        {
                            mensaje = "searchUser#odin@"+us[0]+"#odin@"+us[1]+"#odin@true";
                            dos.writeUTF(padre.recuperarSE().encriptarMiSessionSuSession(mensaje, clave_session)+"#hela@"+us[2]);
                        }
                        else
                        {
                            mensaje = "searchUser#odin@"+partes[1]+"#odin@nada#odin@false";
                            dos.writeUTF(padre.recuperarSE().encriptarMiSessionSuSession(mensaje, clave_session)+"#hela@"+"nada");
                        }
                        //segundo valor es el id del destinatario, tercero mensaje
                    }
                    else if(padre.recuperarSE().desencriptarMiSessionSuSession(accion, clave_session).split("#odin@").length >= 2 && padre.recuperarSE().desencriptarMiSessionSuSession(accion, clave_session).split("#odin@")[0].compareToIgnoreCase("SentMen") == 0)
                    {
                        String partes [] = padre.recuperarSE().desencriptarMiSessionSuSession(accion, clave_session).split("#odin@");
                        //segundo valor es el id de destino, el tercero es el destinatario, tercero mensaje(ya formateado)
                        almacenamiento al = new almacenamiento();
                        al.addmensaje(partes[1], partes[2], partes[3]);
                    }
                    else if(padre.recuperarSE().desencriptarMiSessionSuSession(accion, clave_session).split("#odin@").length >= 2 && padre.recuperarSE().desencriptarMiSessionSuSession(accion, clave_session).split("#odin@")[0].compareToIgnoreCase("OKMEN") == 0)
                    {
                        String partes [] = padre.recuperarSE().desencriptarMiSessionSuSession(accion, clave_session).split("#odin@");
                        //segundo valor es el id de destino, el tercero es el destinatario, tercero mensaje(ya formateado)
                        almacenamiento al = new almacenamiento();
                        if(partes.length >= 2)
                        al.eliminarMensaje(this.idusuario,partes[1]);
                    }
                    else
                    {
                       System.out.println("mensaje no reconocido -> "+accion);
                    }
                }
                
                if(accion.contains("vivo"))
                {
                    Date date = new Date();
                    this.fechaultimaconexion = date.getTime();
                }
                
                if(this.idusuario != null && new almacenamiento().hayMensajes(this.idusuario))
                {
                    System.out.println("La conexion con idsuario -> "+this.idusuario+"tiene mensajes por recibir");
                    String [] mens = new almacenamiento().obtenerConversacion(this.idusuario);
                    for(String men:mens)
                    {
                        String mensaje = "SentMen#odin@"+men;
                        dos.writeUTF(padre.recuperarSE().encriptarMiSessionSuSession(mensaje, clave_session));
                    }
                }
            }
        } catch (IOException ex) {
            /*Logger.getLogger(hilo.class.getName()).log(Level.SEVERE, null, ex);*/
           /*detectar cierre de sesion*/
        }
        desconnectar();
    }
    
}
