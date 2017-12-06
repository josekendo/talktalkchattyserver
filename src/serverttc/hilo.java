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
    private Key clave_session;
    private PublicKey clave_publica;
    
    public hilo(Socket socket, int id, Consola p) {
        this.socket = socket;
        this.idSessio = id;
        this.padre = p;
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
                accion = dis.readUTF();
                if(accion.compareTo("") != 0)
                {
                    if(padre.recuperarSE().desencriptarPrivada(accion).split("#odin@").length >= 2 && padre.recuperarSE().desencriptarPrivada(accion).split("#odin@")[0].compareToIgnoreCase("Cbienvenida") == 0)//recibimos la bienvenida del cliente con su clave publica
                    {
                        String partes [] = padre.recuperarSE().desencriptarPrivada(accion).split("#odin@");
                        this.clave_publica = padre.recuperarSE().loadcp_externa(partes[1]);
                        dos.writeUTF(padre.recuperarSE().encriptarPublica(clave_publica, "CVok@odin#"+padre.recuperarSE().getClaveSession_envio()));//ahora le informamos que tenemos la clave publica que nos envie la de session
                    }
                    else if(padre.recuperarSE().desencriptarPrivada(padre.recuperarSE().desencriptarAes(clave_session, accion)).split("#odin@").length >= 2 && padre.recuperarSE().desencriptarPrivada(padre.recuperarSE().desencriptarAes(clave_session, accion)).split("#odin@")[0].compareToIgnoreCase("CSok") == 0)
                    {
                        //aqui nos viene encriptado con nuestra clave publica(2) y con nuestra clave de session(1) se podria en las siguiente agregar tambien la suya propia pero para este chat no es necesario
                        String partes [] = padre.recuperarSE().desencriptarPrivada(padre.recuperarSE().desencriptarAes(clave_session, accion)).split("#odin@");
                        this.clave_session = padre.recuperarSE().loadcs_externa(partes[1]);
                        //ya tenemos la clave publica y de session de este cliente
                        this.padre.addMensaje("Confirmacion de enlace seguro con conexion -> "+this.idSessio,'g');
                    }
                    else if(padre.recuperarSE().desencriptarPrivada(padre.recuperarSE().desencriptarAes(clave_session, accion)).split("#odin@").length >= 2 && padre.recuperarSE().desencriptarPrivada(padre.recuperarSE().desencriptarAes(clave_session, accion)).split("#odin@")[0].compareToIgnoreCase("existEmail") == 0)
                    {
                        String partes [] = padre.recuperarSE().desencriptarPrivada(padre.recuperarSE().desencriptarAes(clave_session, accion)).split("#odin@");
                        padre.addMensaje("Se esta comprobando verificacion del usuario",'k');
                        padre.comprobarEMAIL(partes[1],dos);
                    }
                    else if(padre.recuperarSE().desencriptarPrivada(padre.recuperarSE().desencriptarAes(clave_session, accion)).split("#odin@").length >= 2 && padre.recuperarSE().desencriptarPrivada(padre.recuperarSE().desencriptarAes(clave_session, accion)).split("#odin@")[0].compareToIgnoreCase("RegistroUser") == 0)
                    {
                         String partes [] = padre.recuperarSE().desencriptarPrivada(padre.recuperarSE().desencriptarAes(clave_session, accion)).split("#odin@");
                         almacenamiento al = new almacenamiento();
                         al.registro(partes[1],partes[2],partes[3],partes[4]);
                    }
                    else if(padre.recuperarSE().desencriptarPrivada(padre.recuperarSE().desencriptarAes(clave_session, accion)).split("#odin@").length >= 2 && padre.recuperarSE().desencriptarPrivada(padre.recuperarSE().desencriptarAes(clave_session, accion)).split("#odin@")[0].compareToIgnoreCase("LoginUser") == 0)
                    {
                        String partes [] = padre.recuperarSE().desencriptarPrivada(padre.recuperarSE().desencriptarAes(clave_session, accion)).split("#odin@");
                        //segundo valor es el email, tercero la verificacion del password 
                        almacenamiento al = new almacenamiento();
                        al.login(partes[1], partes[2], dos);
                    }
                    else if(padre.recuperarSE().desencriptarPrivada(padre.recuperarSE().desencriptarAes(clave_session, accion)).split("#odin@").length >= 2 && padre.recuperarSE().desencriptarPrivada(padre.recuperarSE().desencriptarAes(clave_session, accion)).split("#odin@")[0].compareToIgnoreCase("SentMen") == 0)
                    {
                        String partes [] = padre.recuperarSE().desencriptarPrivada(padre.recuperarSE().desencriptarAes(clave_session, accion)).split("#odin@");
                        //segundo valor es el id del destinatario, tercero mensaje
                    }
                    else
                    {
                       System.out.println("mensaje no reconocido -> "+accion);
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
