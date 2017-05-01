package Controlador;

import Model.Model_usuari;
import Vista.VistaServidor;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Classe del controlador
 *
 * Created by Grup 6 on 30/03/2017.
 */
public class Controlador implements ActionListener{

    private VistaServidor vista;
    private Model_usuari model;

    public Controlador(VistaServidor vista, Model_usuari model){
        this.vista = vista;
        this.model = model;
    }

    public void actionPerformed(ActionEvent event){

        if (event.getSource() instanceof JMenuItem){
            System.out.println(event.getActionCommand() + " - pestaña");

            //Modificar per que nomes s'actualitzi si es selecciona la finaestra grafic
            vista.updateLoginList(model.recuperaLogins());
            vista.changePanel(event.getActionCommand());
        }
        else if (event.getSource() instanceof JButton){
            System.out.println(event.getActionCommand() + " - boto");

            if (event.getActionCommand().equals("REGISTRAR BOTO")){
                model.registraUsuari(vista.getLogin(), vista.getMail(), vista.getPassword());
            }

        }
        else if (event.getSource() instanceof JComboBox){
            System.out.println(event.getActionCommand() + " - combobox");
        }
    }
}
