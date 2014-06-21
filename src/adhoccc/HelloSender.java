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

/**
 *
 * @author tiago
 */
public class HelloSender extends Thread{
        private InetAddress group;
        private MulticastSocket s;
        private int port;
        private Informacao info;
        
        public HelloSender(InetAddress group, MulticastSocket s, int port, Informacao info){
            this.group = group;
            this.s = s;
            this.port = port;
            this.info = info;
        }
        
        @Override
        public void run(){
            byte[] aEnviar = new byte[2048];
            DatagramPacket p;
    
            while(true)
            {
                try {
                   aEnviar = serializePacoteHello(new PacoteHello(info.getNome(),info.filtraTabelaGlobalVizinhos()));
                } catch (IOException ex) {
                    System.out.println("HelloSender: Serialize error.");
                }

                p = new DatagramPacket(aEnviar, aEnviar.length, group, port);

                try {
                     s.send(p);
                } catch (IOException ex) {
                    System.out.println("HelloSender: Send error.");
                }

                try {
                     Thread.sleep(2000); //time interval
                } catch (InterruptedException ex) {
                      System.out.println("HelloSender: Thread sleep error.");
                }
            }
    
        }
        
        private byte[] serializePacoteHello(Object p) throws IOException {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            ObjectOutputStream o = new ObjectOutputStream(b);
            o.writeObject(p);
            o.flush();
            o.close();
            return b.toByteArray();
        }
    
}
