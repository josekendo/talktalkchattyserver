/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverttc;

import java.awt.Color;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 *
 * @author JVAC
 */
public class Consola extends javax.swing.JFrame {

    /**
     * Creates new form Consola
     */
    private Tarea servi;
    private boolean startenabled = true;
    private server ser;
    private seguridad se;
    private almacenamiento al;
    
    public Consola(){
        initComponents();
        
        try
        {
            this.inicio();
        }
        catch(BadLocationException e)
        {
            
        }
    }
    public Consola(login padre){
        initComponents();
        
        try
        {
            this.inicio();
            se = padre.getse();
            padre.dispose();
            al = new almacenamiento();
            al.recuperarids(this);
            al.registro("josekendo2@hotmail.com","jose vicente", "asdf", "imagenenbinario");
            al.recuperarids(this);
            //al.login("josekendo2@hotmail.com", "asdf",null);
        }
        catch(BadLocationException e)
        {
            
        }
    }
    
    public void startM()
    {
        if(this.startenabled)
        {
            System.out.println("arrancando servidor");
            servi = new Tarea(this);
            servi.start();
        }
    }
    
    public void stopM()
    {
       SimpleAttributeSet attrs = new SimpleAttributeSet();
       StyleConstants.setForeground(attrs, Color.red);
        try {
            jTextPane1.getStyledDocument().insertString(jTextPane1.getStyledDocument().getLength(), "\n"+"PARANDO SERVICIO",attrs);
        } catch (BadLocationException ex) {
            Logger.getLogger(Consola.class.getName()).log(Level.SEVERE, null, ex);
        }
        ser.parar(this);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        start = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Servidor TalkTalkChatty");
        setPreferredSize(new java.awt.Dimension(900, 500));

        start.setText("Iniciar");
        start.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                startL(evt);
            }
        });

        jButton2.setText("Parar");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                stopL(evt);
            }
        });

        jLabel1.setText("Port:");

        jTextField1.setText("3200");
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jScrollPane1.setViewportView(jTextPane1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(start)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 182, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(start)
                    .addComponent(jButton2)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void startL(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_startL
        startM();
    }//GEN-LAST:event_startL

    private void stopL(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stopL
        stopM();
    }//GEN-LAST:event_stopL

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Consola.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Consola.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Consola.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Consola.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Consola().setVisible(true);
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JButton start;
    // End of variables declaration//GEN-END:variables
    public void inicio() throws BadLocationException
    {
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setBold(attrs, true);
        jTextPane1.getStyledDocument().insertString(jTextPane1.getStyledDocument().getLength(), "Preparado para iniciar servicio TTC" ,attrs);
    }
    
    public void addMensaje(String a, char color)
    {
       SimpleAttributeSet attrs = new SimpleAttributeSet();
       switch(color)
       {
           case 'r': StyleConstants.setForeground(attrs, Color.RED);
           break;
           case 'g': StyleConstants.setForeground(attrs, Color.GREEN);
           break;
           case 'b': StyleConstants.setForeground(attrs, Color.BLUE);
           break;
           case 'k': StyleConstants.setForeground(attrs, Color.BLACK);
       }
        try {
            jTextPane1.getStyledDocument().insertString(jTextPane1.getStyledDocument().getLength(),a,attrs);
        } catch (BadLocationException ex) {
            Logger.getLogger(Consola.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void inhabilitarInicio()
    {
        this.start.setEnabled(false);
        this.startenabled = false;
    }
    
    public boolean comprobarEMAIL(String e)
    {
        if(al.existeUsuarioBD(e))
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    
    public String recuperarPublica()
    {
        return se.getClavePublica_envio();
    }
    
    public String recuperarSession()
    {
        return se.getClaveSession_envio();
    }
    
    public seguridad recuperarSE()
    {
        return se;
    }

    public void habilitarInicio()
    {
        this.start.setEnabled(true);
        this.startenabled = true;
    }
    
    public void SentMen(String idOrigen, String idDestino, String mensaje)
    {
       
    }
    
    class Tarea extends Thread {
       private Consola padre; 
       
       public Tarea(Consola p)
       {
           padre = p;
       }
             
       @Override
       public void run()
       {
            String puertos[] = new String[1];
            puertos[0]=jTextField1.getText();
            ser = new server();
           try {
                ser.inicio(puertos,padre);
           } catch (IOException ex) {
               Logger.getLogger(Consola.class.getName()).log(Level.SEVERE, null, ex);
           }
       }
    }  
    
}
