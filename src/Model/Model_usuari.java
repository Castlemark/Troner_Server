package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

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

    private ConectorDB conn = new ConectorDB("root", "miauMia1", "troner", 3306);

    /**
     * Constructor de la classe
     */
    public Model_usuari() {
    }

    /**
     * Aquest metode comprova que les dades omplertes per l'usuari tinguin informació i un format correcte
     *
     * @param nomUsuari
     * @param correu
     * @param contrasenya
     * @param confirmacioContra
     * @return Si alguna dada no compleix les condicions, retorna fals
     */

    public boolean comprovaDadesFormat(String nomUsuari, String correu, String contrasenya, String confirmacioContra) {
        if (nomUsuari.isEmpty()) {
            return false;
        }
        if (correu.isEmpty()) {
            return false;

        } else {
            /*comprovem el format del correu mirant lletra per lletra:
              - ha de tenir només una arroba
              - ha de tenir almenys un punt després de l'arroba i aquest no pot estar just després ni al final
              - no pot tenir espais
             */
            int arrova = 0;
            boolean hiHaPunt = false;
            for (int i = 0; i < correu.length(); i++) {
                switch (correu.charAt(i)) {
                    case ' ':
                        return false;
                    case '@':
                        if (i == 0 || arrova != 0 || i > correu.length() - 1) {
                            return false;
                        }
                        arrova = i;
                        break;
                    case '.':
                        if (arrova != 0) {
                            hiHaPunt = true;
                            if (arrova == i - 1 || i + 1 == correu.length()) {
                                return false;
                            }
                        }
                        break;
                }
            }
            if (arrova == 0 || !hiHaPunt) {
                return false;
            }
        }
        /*comprovem el format de la contrasenya mirant lletra per lletra:
            - ha de tenir almenys una minúsucla, una majúsucula i un número
            - ha de tenir mínim 6 lletres
         */
        if (contrasenya.isEmpty()) {
            return false;
        } else {
            boolean hiHaMaj = false;
            boolean hiHaMin = false;
            boolean hiHaNum = false;
            for (int i = 0; i < contrasenya.length(); i++) {

                if (Character.isLowerCase(contrasenya.charAt(i))) {
                    hiHaMin = true;
                }
                if (Character.isUpperCase(contrasenya.charAt(i))) {
                    hiHaMaj = true;
                }
                if (Character.isDigit(contrasenya.charAt(i))) {
                    hiHaNum = true;
                }
            }
            //Mirem si s'han complert les condicions de la contrasenya
            if (!(hiHaMaj && hiHaMin && hiHaNum && contrasenya.length() >= 6)) {
                return false;
            }
        }
        if (confirmacioContra.isEmpty()) {
            return false;
        }
        if (contrasenya.equals(confirmacioContra)) {
            return true;
        }
        //Si totes les dades són correctes arribem aquí i retorna cert
        return false;
    }

    /**
     * Aquest metode comprova que les dades omplertes per l'usuari no existeixin a la base de dades
     *
     * @param nomUsuari
     * @param correu
     * @param contrasenya
     * @return fals si el correu o el nom d'usuari ja existeix
     * @throws SQLException ja que ha d'accedir a la base de dades
     */

    public boolean comprovaDadesInsercio(String nomUsuari, String correu, String contrasenya) throws SQLException {

        long id_jugador;
        String us;
        String ma;

        ResultSet rs;
        rs = recuperaUsuaris();
        System.out.println("recuperat");

        while (rs.next()) {
            id_jugador = rs.getLong(1);
            us = rs.getString(2);
            ma = rs.getString(3);

            if (us.equals(nomUsuari) || ma.equals(correu)) {
                return false;
            }
        }
        return true;

    }

    /**
     * Registra un nou usuari
     *
     * @param nomUsuari
     * @param correu
     * @param contrasenya
     * @throws SQLException introdueix l'usuari nou a la base de dades
     */
    public boolean registraUsuari(String nomUsuari, String correu, String contrasenya, String contrasenya2) throws SQLException {


        conn.connect();

        if (comprovaDadesInsercio(nomUsuari, correu, contrasenya) && comprovaDadesFormat(nomUsuari, correu, contrasenya, contrasenya2)) {

            System.out.println("inserint");
            System.out.println("INSERT INTO usuari (login, mail, contrasenya,  punts, data_registre, data_ultimacces ,up , down, left, right) VALUES (" + "'" + nomUsuari + "'" + "," + "'" + correu + "'" + "," + "'" + contrasenya + "'" + "," + "0, CURDATE(), CURDATE(), 87, 83, 65, 68" + ")");
            conn.insertQuery("INSERT INTO usuari (login, mail, contrasenya,  punts, data_registre, data_ultimacces ,tecla_up , tecla_down, tecla_left, tecla_right) VALUES (" + "'" + nomUsuari + "'" + "," + "'" + correu + "'" + "," + "'" + contrasenya + "'" + "," + "0, CURDATE(), CURDATE(), 87, 83, 65, 68" + ")");


            conn.disconnect();
            return true;
        }

        conn.disconnect();
        return false;

    }

    public void actualitzaData(String login){

        conn.connect();

        System.out.println("UPDATE usuari SET data_ultimacces = " + "CURDATE() WHERE login = '" + login +"';");
        conn.updateQuery("UPDATE usuari SET data_ultimacces = " + "CURDATE() WHERE login = '" + login +"';" );

        conn.disconnect();
    }

    /**
     * Mètode que retorna un ResultSet amb els usuaris per gestionar les dades
     *
     * @return ResultSet amb els elements dela base de dades
     */
    public ResultSet recuperaUsuaris() {

        ResultSet resultats;

        resultats = conn.selectQuery("SELECT id_jugador, login, mail, contrasenya, punts, data_registre, data_ultimacces FROM usuari");

        return resultats;
    }

    public String recuperaDadesUsuari(String login) throws SQLException {

        String text = "";
        ResultSet rs;

        conn.connect();

        rs = conn.selectQuery("SELECT id_jugador, login, mail, contrasenya, punts, data_registre, data_ultimacces FROM usuari WHERE login =" + "'" + login + "'");

        while (rs.next()) {
            text = "\n";
            text += "----------------------------------------------------------------------------------------------------------------------------" + "\n";
            text += "                                       LOGIN:                                 |             " + rs.getString(2) + "\n";
            text += "----------------------------------------------------------------------------------------------------------------------------" + "\n";
            text += "                                       PUNTS:                                |             " + rs.getInt(5) + "\n";
            text += "----------------------------------------------------------------------------------------------------------------------------" + "\n";
            text += "                                       DATA REGISTRE:              |             " + rs.getDate(6) + "\n";
            text += "----------------------------------------------------------------------------------------------------------------------------" + "\n";
            text += "                                       DATA ULTIM ACCES:        |             " + rs.getDate(7) + "\n";
            text += "----------------------------------------------------------------------------------------------------------------------------" + "\n";
        }

        conn.disconnect();

        return text;
    }

    public String comprovaInicia(Inicia inicia) throws SQLException {

        ResultSet rs;
        conn.connect();

        String login;

        if (inicia.getOpcio() == 1) {

            rs = conn.selectQuery("SELECT id_jugador, login, mail, contrasenya, punts, data_registre, data_ultimacces FROM usuari WHERE login =" + "'" + inicia.getNom() + "'");
            System.out.println("SELECT id_jugador, login, mail, contrasenya, punts, data_registre, data_ultimacces FROM usuari WHERE login =" + "'" + inicia.getNom() + "'");
            if (!rs.next()) {
                return "error a Model_usuari.comprovaInicia";
            }
            login = rs.getString(2);

            if (rs.getString(2).equals(inicia.getNom()) && rs.getString(4).equals(inicia.getPassword())) {

                conn.disconnect();
                return login;
            }

            conn.disconnect();
            return "error a Model_usuari.comprovaInicia";
        } else {

            System.out.println("Hola opcio2");
            rs = conn.selectQuery("SELECT id_jugador, login, mail, contrasenya, punts, data_registre, data_ultimacces FROM usuari WHERE mail =" + "'" + inicia.getNom() + "'");
            rs.next();
            login = rs.getString(2);

            if (rs.getString(3).equals(inicia.getNom()) && rs.getString(4).equals(inicia.getPassword())) {

                conn.disconnect();
                return login;
            }

            conn.disconnect();
            return "error a Model_usuari.comprovaInicia";
        }

    }

    public ArrayList<String> recuperaLogins() throws SQLException {

        ArrayList<String> list = new ArrayList<String>();

        conn.connect();

        ResultSet rs;
        rs = recuperaUsuaris();

        while (rs.next()) {
            list.add(rs.getString(2));
        }

        conn.disconnect();

        return list;
    }

    /**
     * Aquest mètode esborra un usuari ja registrat
     *
     * @param nomUsuari
     * @throws SQLException ja que hem d'esborrar l'usuari de la base de dades
     */
    public void eliminaUsuari(String nomUsuari) throws SQLException {
        conn.connect();

        System.out.println("DELETE FROM usuari WHERE login =" + "'" + nomUsuari + "'");
        conn.deleteQuery("DELETE FROM usuari WHERE login =" + "'" + nomUsuari + "'");

        conn.disconnect();

    }

    public void updatePuntuacio(String login, int punts){

        conn.connect();

        System.out.println("UPDATE usuari SET punts = punts + " + punts + " WHERE login = '" + login + "';");
        conn.updateQuery("UPDATE usuari SET punts = punts + " + punts + " WHERE login = '" + login + "';");
        System.out.println("INSERT INTO puntuacio(id_jugador, puntuacio) VALUES ((SELECT id_jugador FROM Usuari WHERE login = '"+ login + "'), " + punts + ");");
        conn.insertQuery("INSERT INTO puntuacio(id_jugador, puntuacio) VALUES ((SELECT id_jugador FROM Usuari WHERE login = '"+ login + "'), " + punts + ");");

        conn.disconnect();
    }

    public void actualitzaControls(String login, int up, int down, int left, int right ) {
        conn.connect();
        System.out.print("UPDATE usuari SET tecla_up =" + up + ", tecla_down = " + down + ", tecla_left =" + left + ", tecla_right = " + right + " WHERE login =" + "'" + login + "'");
        conn.updateQuery("UPDATE usuari SET tecla_up =" + up + ", tecla_down = " + down + ", tecla_left =" + left + ", tecla_right = " + right + " WHERE login =" + "'" + login + "'"); //Shan d'actualitzar els controls
        conn.disconnect();

    }


    public int getPuntsUsuari (String login) throws  SQLException{

        int punts;
        ResultSet resultSet;

        conn.connect();

        resultSet = conn.selectQuery("SELECT punts FROM usuari WHERE login = '" + login + "';" );
        resultSet.next();

        punts = resultSet.getInt("punts");

        conn.disconnect();

        return punts;
    }

    public ArrayList<Integer> getHistorial(String login) throws SQLException{

        ResultSet resultSet;
        ArrayList<Integer> punts = new ArrayList<>();

        conn.connect();

        System.out.println("SELECT id_puntuacio, puntuacio FROM puntuacio WHERE puntuacio.id_jugador = (SELECT id_jugador FROM Usuari WHERE login = '" + login + "') ORDER BY id_puntuacio ASC");
        resultSet = conn.selectQuery("SELECT id_puntuacio, puntuacio FROM puntuacio WHERE puntuacio.id_jugador = (SELECT id_jugador FROM Usuari WHERE login = '" + login + "') ORDER BY id_puntuacio ASC");

        while (resultSet.next()){
            punts.add(resultSet.getInt("puntuacio"));
        }

        conn.disconnect();
        return punts;
    }

    public String getRanquing() throws SQLException{

        String ranquing = "Pos  -   Nom  -   Punts";
        ResultSet resultSet;
        int i = 1;

        conn.connect();

        resultSet = conn.selectQuery("SELECT login, punts FROM usuari ORDER BY punts DESC LIMIT 10;");

        while (resultSet.next()){
            ranquing += "\n" + i + "    -   " + resultSet.getString("login") +"  -   " + resultSet.getInt("punts");
            i++;
        }

        conn.disconnect();
        System.out.println(ranquing);
        return ranquing;
    }

    public int[] getControls (String login) throws  SQLException{

        int[] controls = new int[4];
        ResultSet resultSet;

        conn.connect();;

        resultSet = conn.selectQuery("SELECT tecla_up, tecla_down, tecla_left, tecla_right FROM usuari WHERE login = '" + login + "';");
        resultSet.next();

        controls[0]=resultSet.getInt("tecla_up");
        controls[1]=resultSet.getInt("tecla_down");
        controls[2]=resultSet.getInt("tecla_left");
        controls[3]=resultSet.getInt("tecla_right");

        conn.disconnect();

        return controls;


    }

    public Object [][] ompleRanquing () throws SQLException {
        ResultSet rs;
        int i = 0;
        int rows = 0;

        System.out.println("hola query");

        conn.connect();

        rs = conn.selectQuery("SELECT login, data_ultimacces, punts FROM Usuari ORDER BY punts DESC");

        rs.next();
        while (rs.next()) {
            System.out.println("next");
            rows++;
        }
        Object [][] usuaris = new Object[rows+1][4];
        System.out.println(rows);
        rs.first();
        usuaris [0][0] = 1;
        usuaris [0][1] = rs.getObject(1);
        usuaris [0][2] = rs.getObject(2);
        usuaris [0][3] = rs.getObject(3);
        i++;

        while (rs.next()) {
            System.out.println("print i = " + i);
            usuaris[i] = new Object[4];
            usuaris[i][0] = i + 1;
            usuaris[i][1] = rs.getObject(1);
            usuaris[i][2] = rs.getObject(2);
            usuaris[i][3] = rs.getObject(3);
            System.out.println("print objecto = " + usuaris [i][1]);
            i++;
        }
        conn.disconnect();
        return usuaris;
    }
}



