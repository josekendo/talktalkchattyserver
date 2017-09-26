/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverttc;

/**
 *
 * @author JVAC
 */
public class serverttc
{
    public static void main(String [] args)
    {
        Consola nueva = new Consola();
        //con esta linea le decimos que queremos arrancarlo en el centro 
        nueva.setLocationRelativeTo(null);
        //hacemos visible la ventana de la consola
        nueva.setVisible(true);
    }
}