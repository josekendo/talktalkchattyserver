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
    
 
    public void inicio(String args[],Consola p) throws IOException {
        ServerSocket ss;
        System.out.print("Inicializando servidor... ");
        try {
            p.addMensaje("\nAbriendo puerto", 'k');
            ss = new ServerSocket(Integer.parseInt(args[0]));
            p.addMensaje("\nEscuchando por el puerto -> "+args[0], 'g');
            System.out.println("\t[OK]");
            int idSession = 0;
            while (true) {
                Socket socket;
                socket = ss.accept();
                System.out.println("Nueva conexión entrante: "+socket);
                p.addMensaje("\nNuevo Cliente -> "+socket, 'b');
                ((hilo) new hilo(socket, idSession,p)).start();
                idSession++;
            }
        } catch (IOException ex) {
            Logger.getLogger(server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
