package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Model.utils.ConectorDB;

/**
 * Classe que contorla i gestiona les dades de la base de dades
 *
 * Created by Grup 6 on 05/04/2017.
 */
public class Model_usuari {
    private String login;
    private String mail;
    private String password;

    ConectorDB conn = new ConectorDB("root", "12069554eE", "troner", 3306);

    /**
     * Constructor de la classe
     */
    public Model_usuari (){
    }

    /**
     * Aquest metode comprova que les dades omplertes per l'usuari tinguin informació i un format correcte
     *
     * @param nomUsuari
     * @param correu
     * @param contrasenya
     * @param confirmacioContra
     * @return  Si alguna dada no compleix les condicions, retorna fals
     */

    public boolean comprovaDadesFormat(String nomUsuari, String correu, String contrasenya, String confirmacioContra){
        if(nomUsuari.isEmpty()){
            return false;
        }
        if(correu.isEmpty()){
            return false;

        }else{
            /*comprovem el format del correu mirant lletra per lletra:
              - ha de tenir només una arrova
              - ha de tenir almenys un punt després de l'arrova i aquest no pot estar just després ni al final
              - no pot tenir espais
             */
            int arrova = 0;
            boolean hiHaPunt = false;
            for (int i = 0; i < correu.length(); i++){
                switch(correu.charAt(i)){
                    case ' ':
                        return false;
                    case '@':
                        if(i == 0 || arrova != 0 || i > correu.length() - 1) {return false;}
                        arrova = i;
                        break;
                    case '.':
                        if(arrova != 0){
                            hiHaPunt = true;
                            if(arrova == i - 1 || i + 1 == correu.length()){return false;}
                        }
                        break;
                }
            }
            if (arrova == 0 || !hiHaPunt){return false;}
        }
        /*comprovem el format de la contrasenya mirant lletra per lletra:
            - ha de tenir almenys una minúsucla, una majúsucula i un número
            - ha de tenir mínim 6 lletres
         */
        if(contrasenya.isEmpty()){
            return false;
        }else {
            boolean hiHaMaj = false;
            boolean hiHaMin = false;
            boolean hiHaNum = false;
            for(int i = 0; i < contrasenya.length(); i++){

                if(Character.isLowerCase(contrasenya.charAt(i))){
                    hiHaMin = true;
                }
                if(Character.isUpperCase(contrasenya.charAt(i))){
                    hiHaMaj = true;
                }
                if(Character.isDigit(contrasenya.charAt(i))) {
                    hiHaNum = true;
                }
            }
            //Mirem si s'han complert les condicions de la contrasenya
            if(!(hiHaMaj && hiHaMin && hiHaNum && contrasenya.length() >= 6)){return false;}
        }
        if(confirmacioContra.isEmpty()) {return false;}
        if(contrasenya.equals(confirmacioContra)) {return true;}
        //Si totes les dades són correctes arribem aquí i retorna cert
        return false;
    }

    /**
     * Aquest metode comprova que les dades omplertes per l'usuari no existeixin a la base de dades
     * @param nomUsuari
     * @param correu
     * @param contrasenya
     * @return fals si el correu o el nom d'usuari ja existeix
     * @throws SQLException ja que ha d'accedir a la base de dades
     */

    public boolean comprovaDadesInsercio(String nomUsuari, String correu, String contrasenya) throws SQLException{

        long id_jugador;
        String us;
        String ma;

        ResultSet rs;
        rs = recuperaUsuaris();
        System.out.println("recuperat");

        while (rs.next()){
            id_jugador = rs.getLong(1);
            us = rs.getString(2);
            ma = rs.getString(3);

            if (us.equals(nomUsuari) || ma.equals(correu)){
                System.out.println("fals");
                return false;
            }
        }
        System.out.println("cert");
        return true;

    }

    /**
     * Registra un nou usuari
     * @param nomUsuari
     * @param correu
     * @param contrasenya
     * @throws SQLException introdueix l'usuari nou a la base de dades
     */
    public void registraUsuari(String nomUsuari, String correu, String contrasenya) throws SQLException{


        conn.connect();

        if (comprovaDadesInsercio(nomUsuari, correu, contrasenya)){

            System.out.println("inserint");
            System.out.println("INSERT INTO usuari (login, mail, contrasenya) VALUES (" + "'" +nomUsuari + "'" + "," + "'" + correu + "'" + "," + "'" + contrasenya + "'" +")");
            conn.insertQuery("INSERT INTO usuari (login, mail, contrasenya, data_registre) VALUES (" + "'" +nomUsuari + "'" + "," + "'" + correu + "'" + "," + "'" + contrasenya + "'" + "," + "CURDATE()" +")");
        }
        conn.disconnect();

    }

    /**
     * Mètode que retorna un ResultSet amb els usuaris per gestionar les dades
     * @return ResultSet amb els elements dela base de dades
     */
    public ResultSet recuperaUsuaris(){

        ResultSet resultats;

        resultats = conn.selectQuery("SELECT id_jugador, login, mail, contrasenya, punts, data_registre, data_ultimacces FROM usuari");

        return  resultats;
    }

    public Object[][] rcuperaLlistaUsuaris() throws SQLException{
        conn.connect();

        ResultSet rs;
        rs = recuperaUsuaris();


        int i = 0;
        int j = 0;
        Object[] inserir = new Object[5];

        while (rs.next()){
            i++;
        }
        rs.beforeFirst();

        Object[][] data = new Object[i][5];

        while (rs.next()){
            inserir[0] = rs.getString(2);
            inserir[1] = rs.getInt(5);
            inserir[2] = rs.getDate(6);
            inserir[3] = rs.getDate(6);
            inserir[4] = "Delete";

            data [j] = inserir;
            System.out.println(data[j][0]);
            j++;
        }


        conn.disconnect();

        return data;
    }

    public ArrayList<String> recuperaLogins() throws SQLException{

        ArrayList<String> list = new ArrayList<String>();

        conn.connect();

        ResultSet rs;
        rs = recuperaUsuaris();

        while (rs.next()){
            list.add(rs.getString(2));
        }

        conn.disconnect();

        return list;
    }

    /**
     * Aquest mètode esborra un usuari ja registrat
     * @param nomUsuari
     * @throws SQLException ja que hem d'esborrar l'usuari de la base de dades
     */
    public void eliminaUsuari(String nomUsuari) throws SQLException{
        conn.connect();

        System.out.println("DELETE FROM usuari WHERE login =" + "'" + nomUsuari + "'");
        conn.deleteQuery("DELETE FROM usuari WHERE login =" + "'" + nomUsuari + "'");

        conn.disconnect();

    }
}



