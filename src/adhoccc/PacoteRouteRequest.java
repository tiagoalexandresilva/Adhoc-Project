/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package adhoccc;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.HashSet;

/**
 *
 * @author tiago
 */
public class PacoteRouteRequest implements Serializable {
    private boolean found;
    private String origem;
    private String destino;
    private InetAddress anterior;
    private String nomeAnterior;
    private HashSet<String> visitados;
    
    public PacoteRouteRequest(String origem, String destino, InetAddress anterior)
    {
        this.nomeAnterior = new String();
        this.found = false;
        this.origem = origem;
        this.destino = destino;
        this.anterior = anterior;
        this.visitados = new HashSet<String>();
    }
    
    public void setAnterior(InetAddress anterior)
    {
        this.anterior = anterior;
    }
    
    public void setNomeAnterior(String nomeAnterior)
    {
        this.nomeAnterior = nomeAnterior;
    }
    
    public String getNomeAnterior()
    {
        return this.nomeAnterior;
    }
    
    public void setFound(boolean found)
    {
        this.found = found;
    }
    
    public boolean getFound()
    {
        return this.found;
    }
    
    public String getOrigem()
    {
        return this.origem;
    }
    
    public InetAddress getAnterior()
    {
        return this.anterior;
    }
    
    public String getDestino()
    {
        return this.destino;
    }
    
    public HashSet<String> getVisitados()
    {
        return this.visitados;
    }
    
    public boolean adicionaVisitado(String nome)
    {   
        if(this.visitados.contains(nome))
            return false;
        else{
            this.visitados.add(nome);
            return true;
        }
            
    }
    
}
