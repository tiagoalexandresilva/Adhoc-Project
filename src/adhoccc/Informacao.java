/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package adhoccc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.TreeMap;

/**
 *
 * @author Tiago
 */
public class Informacao {
    private String nome;
    private TreeMap<String,DadosVizinho> vizinhosdirectos;
    private TreeMap<String,DadosVizinho> route;
    private TreeMap<String,DadosVizinho> pendentes;
    
    public Informacao(){
        try {
            this.nome = InetAddress.getLocalHost().getHostName();
            this.vizinhosdirectos = new TreeMap<String,DadosVizinho>();
            this.route = new TreeMap<String,DadosVizinho>();
            this.pendentes = new TreeMap<String,DadosVizinho>();
        } catch (UnknownHostException ex) {
            System.out.println("Informação: Hostname error.");
        }
    }
    
    public String getNome()
    {
        return this.nome;
    }
    
    public TreeMap<String,DadosVizinho> getPendentes()
    {
        return this.pendentes;
    }
    
    public TreeMap<String,DadosVizinho> getVizinhosDirectos()
    {
        return this.vizinhosdirectos;
    }
    
    public TreeMap<String,DadosVizinho> getRoute()
    {
        return this.route;
    }
        
    public void actualizaVizinhosDirectos(PacoteHello pacote, InetAddress ipaddress)
    {
        String nomenodo = pacote.getNomeNodo();
        if(!nomenodo.equals(this.nome))
        {
            if(!this.vizinhosdirectos.containsKey(nomenodo)){
                DadosVizinho dados = new DadosVizinho(nomenodo,ipaddress);
                this.vizinhosdirectos.put(nomenodo,dados);
            }
            else{

                if(!nomenodo.equals(this.vizinhosdirectos.get(nomenodo).getNomeVizinho()) && this.vizinhosdirectos.containsKey(nomenodo)){
                    DadosVizinho dados = new DadosVizinho(nomenodo,ipaddress);
                    this.vizinhosdirectos.put(nomenodo,dados);
                }
                else{
                    this.vizinhosdirectos.get(nomenodo).setTempo(System.currentTimeMillis());
                    this.vizinhosdirectos.get(nomenodo).setActivo(true);
                }
            }
            //ver quais os vizinhos que vem no pacote e que estao na tabela e actualizar o tempo ou adiciona-los   
            for(String str : pacote.getVizinhos())
            {
                if(!str.equals(this.nome))
                {

                    if(!this.vizinhosdirectos.containsKey(str) || !this.vizinhosdirectos.get(str).getActivo()){
                        DadosVizinho dados = new DadosVizinho(nomenodo,ipaddress);
                        this.vizinhosdirectos.put(str,dados);
                    }
                    else{
                        this.vizinhosdirectos.get(str).setTempo(System.currentTimeMillis());
                        this.vizinhosdirectos.get(str).setActivo(true);
                    }

                }
            }

    
        }
        verificaTempo();       
    }
    
    public HashSet<String> filtraTabelaGlobalVizinhos()
    {
        HashSet<String> res = new HashSet<String>();
        DadosVizinho dados;
        

        for(String str : this.vizinhosdirectos.keySet())
        {
            dados = this.vizinhosdirectos.get(str).clone();
            if(dados.getNomeVizinho().equals(str) && dados.getActivo()) {
                res.add(str);              
            }
        }
        return res;
    }
    
    private void verificaTempo(){
        DadosVizinho dados;
        long t = System.currentTimeMillis();
        
        for(String str : this.vizinhosdirectos.keySet())
        {
            dados = this.vizinhosdirectos.get(str);
            if(( t - dados.getTempo()) >= 2500)
                dados.setActivo(false);
            else
                dados.setActivo(true);
        } 
        
        for(String str : this.route.keySet())
        {
            dados = this.route.get(str);
            if(( t - dados.getTempo()) >= 2500)
                dados.setActivo(false);
            else
                dados.setActivo(true);
        } 
    }
    
    
    public void actualizaRouteRequest(PacoteRouteRequest pacote,InetAddress ipanterior, InetAddress group, MulticastSocket s, int port)
    {
        if(pacote.getFound())
        {
            //Se chegou a origem

            if(this.pendentes.containsKey(pacote.getOrigem()+pacote.getDestino()) && pacote.getOrigem().equals(this.nome)){
                this.pendentes.remove(pacote.getOrigem()+pacote.getDestino());
                this.route.put(pacote.getDestino(),new DadosVizinho(pacote.getNomeAnterior(),ipanterior));
            }
            else{
                //Se está a voltar para trás quer dizer que já encontrou o destino
                if(this.pendentes.containsKey(pacote.getOrigem()+pacote.getDestino())){

                    this.route.put(pacote.getDestino(),new DadosVizinho(pacote.getNomeAnterior(),ipanterior));
                    pacote.setNomeAnterior(this.nome);
                    enviaPacote(pacote,this.pendentes.get(pacote.getOrigem()+pacote.getDestino()).getIP(),s,port);
                    this.pendentes.remove(pacote.getOrigem()+pacote.getDestino());
                }
            }
            
        }
        else{
            //se ainda ainda não estava na lista de route
            if(pacote.adicionaVisitado(this.nome))
            {

                this.pendentes.put(pacote.getOrigem()+pacote.getDestino(), new DadosVizinho("",ipanterior));
                pacote.setNomeAnterior(this.nome);
                //se tem na tabela de vizinhos directos
                   
                if(this.vizinhosdirectos.containsKey(pacote.getDestino()))
                {
                    pacote.setFound(true);
                    enviaPacote(pacote,ipanterior,s,port);
                    this.pendentes.remove(pacote.getOrigem()+pacote.getDestino());
                }

                else
                {
                    enviaPacote(pacote,group,s,port);
                    //se nao o tem vai propragar para todos os vizinhos
                }      
            }
        }
        verificaTempo(); 
    }   
    
    private void enviaPacote(PacoteRouteRequest pacote, InetAddress ipaddress, MulticastSocket s, int port)
    {
        DatagramPacket p;
        byte[] aEnviar = new byte[2048];
        
        try {
                aEnviar = serializePacote(pacote);
                p = new DatagramPacket(aEnviar, aEnviar.length, ipaddress, port);

                s.send(p);
            } catch (IOException ex) {
                System.out.println("Informacao: Send error." + ex.toString());
            }
    }
    
     public byte[] serializePacote(Object p) throws IOException {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            ObjectOutputStream o = new ObjectOutputStream(b);
            o.writeObject(p);
            o.flush();
            o.close();
            return b.toByteArray();
        }

}
