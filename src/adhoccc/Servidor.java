/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package adhoccc;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 *
 * @author Tiago
 */
public class Servidor extends Thread{
    private int port;
    private Informacao info;
    
    public Servidor(Informacao info){
        this.port = 9999;
        this.info = info;
    }
    
    @Override
    public void run(){
        MulticastSocket s = null;
        InetAddress group = null;
        
        try {
            group = InetAddress.getByName("FF05::C");
        } catch (UnknownHostException ex) {
            System.out.println("Servidor: group error.");
        }
          
        try {
            s = new MulticastSocket(port);
        } catch (IOException ex) {
            System.out.println("Servidor: MultiSocket error.");
        }
        
        try {
            s.joinGroup(group);
            s.setTimeToLive(1);
        } catch (IOException ex) {
            System.out.println("Servidor: joinGroup error.");
        }
        
        new Receiver(group,s,port,info).start();
        new ClienteRequest(group,s,port,info).start();
    }
    
}
