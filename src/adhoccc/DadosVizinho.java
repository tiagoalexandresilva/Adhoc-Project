/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package adhoccc;

import java.net.InetAddress;

/**
 *
 * @author tiago
 */
public class DadosVizinho {
    private String nomevizinho;
    private InetAddress ipaddress;
    private boolean activo;
    private long tempo;
    
    public DadosVizinho(String nomevizinho, InetAddress ipaddress)
    {
        this.nomevizinho = nomevizinho;
        this.ipaddress = ipaddress;
        this.activo = true;
        this.tempo = System.currentTimeMillis();
    }
    
    public DadosVizinho(DadosVizinho dados)
    {
        this.nomevizinho = dados.getNomeVizinho();
        this.ipaddress = dados.getIP();
        this.activo = dados.getActivo();
        this.tempo = dados.getTempo();
    }
    
    public void setActivo(boolean activo)
    {
        this.activo = activo;
    }
    
    public InetAddress getIP()
    {
        return this.ipaddress;
    }
    
    public String getNomeVizinho()
    {
        return this.nomevizinho;
    }
    
    public long getTempo()
    {
        return this.tempo;
    }
    
    public boolean getActivo()
    {
        return this.activo;
    }
    
    public void setTempo(long tempo)
    {
        this.tempo = tempo;
    }
    
    public void setNomeVizinho(String nomevizinho)
    {
        this.nomevizinho = nomevizinho;
    }
    
    @Override
    public DadosVizinho clone()
    {
        return new DadosVizinho(this);
    }
}
