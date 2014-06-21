/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package adhoccc;

import java.io.Serializable;
import java.util.HashSet;

/**
 *
 * @author tiago
 */
public class PacoteHello implements Serializable{
    private String nodo;
    private HashSet<String> vizinhos;
    
    public PacoteHello(String nodo, HashSet<String> vizinhos)
    {
        this.nodo = nodo;
        this.vizinhos = vizinhos;
    }
    
    public String getNomeNodo()
    {
        return this.nodo;
    }
    
    public HashSet<String> getVizinhos(){
        return this.vizinhos;
    }
    
}
