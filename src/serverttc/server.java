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
public class server {
    
    private ServerSocket ser;
    private Socket conexiones [];//array de conexiones de tipo cliente
    
    public void inicio(String args[],Consola p) throws IOException {
        conexiones = new Socket[128];
        for(int a=0; a < conexiones.length;a++)
        {
            conexiones[a]=null;
        }
        ServerSocket ss;
        System.out.print("Inicializando servidor... ");
        try {
            p.addMensaje("\nAbriendo puerto", 'k');
            ss = new ServerSocket(Integer.parseInt(args[0]));
            p.addMensaje("\nEscuchando por el puerto -> "+args[0], 'g');
            p.inhabilitarInicio();
            System.out.println("\t[OK]");
            int idSession = 0;
            ser = ss;
            while (true) {
                Socket socket;
                socket = ss.accept();
                conexiones[idSession]= socket;
                System.out.println("Nueva conexión entrante: "+socket);
                p.addMensaje("\nNuevo Cliente -> "+socket, 'b');
                ((hilo) new hilo(socket, idSession,p)).start();
                idSession++;
            }
        } catch (IOException ex) {
            /*Logger.getLogger(server.class.getName()).log(Level.SEVERE, null, ex);*/
            if(ex.getMessage().compareToIgnoreCase("socket closed") == 0)
            {
                p.habilitarInicio();
                p.addMensaje("\nServidor parado por el puerto -> "+args[0], 'r');
            }
        }
    }
    
    public void parar(Consola p)
    {
        try {
            boolean seguir=true;
            int contar=0;
            while(seguir)
            {
                if(conexiones.length <= 128 && conexiones[contar] != null)
                {
                    conexiones[contar].close();
                }
                else
                {
                    seguir = false;
                }
                contar++;
            }
            this.ser.close();
        } catch (IOException ex) {
            /*Logger.getLogger(server.class.getName()).log(Level.SEVERE, null, ex);*/
        }
    }
    
}
