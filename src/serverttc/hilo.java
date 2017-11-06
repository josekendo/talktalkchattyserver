/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverttc;
import java.io.*;
import java.net.*;
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
            dos.writeUTF("Hola soy el servidor\n");
            boolean seguir = true;
            while (seguir)
            {
                accion = dis.readUTF();
                if(accion.compareTo("") != 0)
                {
                    if(accion.compareTo("hola") == 0)
                    {
                        System.out.println("El cliente con idSesion "+this.idSessio+" saluda");
                        padre.addMensaje("\nEl cliente con idSesion "+this.idSessio+" saluda",'k');
                        dos.writeUTF("adios");
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
