/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverttc;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * @author JVAC
 */
public class serverttc extends Application
{
    private seguridad seguridad;
    private almacenamiento almacenamiento;
    
    public static void main(String[] args) 
    {
        //comprobamos password y usuario
        login nl = new login();
        nl.setLocationRelativeTo(null);
        nl.setVisible(true);
        System.out.println("Login");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public static void abrirConsola()
    {
        //inicializamos si es correcto seguridad y arrancamos
        Consola nueva = new Consola();
        //con esta linea le decimos que queremos arrancarlo en el centro 
        nueva.setLocationRelativeTo(null);
        //hacemos visible la ventana de la consola
        nueva.setVisible(true);
        //vamos a arrancar el servicio de servidor
        System.out.println("arranque server");
    }
}