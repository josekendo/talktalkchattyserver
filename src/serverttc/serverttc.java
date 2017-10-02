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
    public static void main(String[] args) 
    {
        Consola nueva = new Consola();
        //con esta linea le decimos que queremos arrancarlo en el centro 
        nueva.setLocationRelativeTo(null);
        //hacemos visible la ventana de la consola
        nueva.setVisible(true);
        //vamos a arrancar el servicio de servidor
        System.out.println("arranque server");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}