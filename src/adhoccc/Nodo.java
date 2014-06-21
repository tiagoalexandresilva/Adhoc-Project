/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package adhoccc;
/** 
 *
 * @author Tiago
 */

public class Nodo {

    public static void main(String[] args) {
            Informacao info = new Informacao();
            new Servidor(info).start();
            new Cliente(info).start();
    }
}
