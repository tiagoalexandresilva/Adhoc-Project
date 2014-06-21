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
public class Cliente extends Thread{
    private int port;
    private Informacao info;
    
    public Cliente(Informacao info){
        this.port = 9999;
        this.info = info;
    }
    
    @Override
    public void run(){
        InetAddress group = null;
        MulticastSocket s = null;
            
        try {
            group = InetAddress.getByName("FF05::C");
        } catch (UnknownHostException ex) {
            System.out.println("Cliente: group error.");
        }
        
        try {
               s = new MulticastSocket(port);
        } catch (IOException ex) {
              System.out.println("Cliente: MultiSocket error.");
        }

        try {
             s.joinGroup(group);
        } catch (IOException ex) {
             System.out.printf("Cliente: joinGroup error.");
        }
  
        new HelloSender(group,s,port,info).start();
    } 
}
