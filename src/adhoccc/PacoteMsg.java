/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package adhoccc;


import java.io.Serializable;
/**
 *
 * @author tiago
 */
public class PacoteMsg implements Serializable{
    private String destino;
    private String origem;
    private String mensagem;
    
    public PacoteMsg(String orig, String dest,String mensagem){
        this.destino = dest;
        this.origem = orig;
        this.mensagem = mensagem;
    }
    
    String getOrigem()
    { 
        return this.origem; 
    }
    
    String getDestino()
    { 
        return this.destino; 
    }
    
    String getMensagem()
    { 
        return this.mensagem; 
    }

}
