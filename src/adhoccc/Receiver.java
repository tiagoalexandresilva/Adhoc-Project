/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package adhoccc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 *
 * @author tiago
 */
public class Receiver extends Thread{
        private InetAddress group;
        private MulticastSocket s;
        private int port;
        private Informacao info;
        
        public Receiver(InetAddress group, MulticastSocket s, int port, Informacao info){
            this.group = group;
            this.s = s;
            this.port = port;
            this.info = info;
        }
        
        @Override
        public void run(){
            Object pacote = null;
            PacoteHello hello = null;
            PacoteRouteRequest route = null;
            PacoteMsg msg = null;
            DatagramPacket pedido;
            byte[] aReceber = new byte[2048];
            DatagramPacket p;
            byte[] aEnviar = new byte[2048];
            
            while(true){
                pedido = new DatagramPacket(aReceber, aReceber.length);

                try {
                    s.receive(pedido);                 
                } catch (IOException ex) {
                    System.out.println("Servidor: Receive error.");
                }

                try {
                    pacote = deserializePacote(pedido.getData());

                    if(pacote instanceof PacoteHello){
                        hello = (PacoteHello) pacote;
                        info.actualizaVizinhosDirectos(hello,pedido.getAddress());                
                    } else if(pacote instanceof PacoteRouteRequest){
                        
                        route = (PacoteRouteRequest) pacote;
                        info.actualizaRouteRequest(route,pedido.getAddress(),group,s,port);
                        
                    } else if(pacote instanceof PacoteMsg){
                        msg = (PacoteMsg) pacote;
                        if(info.getNome().equals(msg.getDestino()))
                            System.out.println("A mensagem foi: "+msg.getMensagem());
                        else
                        {
                            if(info.getVizinhosDirectos().containsKey(msg.getDestino()))
                            {
                                try {
                                    aEnviar = info.serializePacote(msg);
                                } catch (IOException ex) {
                                    System.out.println("ClienteRequest: Serialize error.");
                                }

                                p = new DatagramPacket(aEnviar, aEnviar.length, info.getVizinhosDirectos().get(msg.getDestino()).getIP(), port);

                                try {
                                     s.send(p);
                                } catch (IOException ex) {
                                    System.out.println("ClienteRequest: Send error.");
                                }
                            }
                            else{
                                if(info.getRoute().containsKey(msg.getDestino()))
                                {
                                    try {
                                        aEnviar = info.serializePacote(msg);
                                    } catch (IOException ex) {
                                        System.out.println("ClienteRequest: Serialize error.");
                                    }

                                    p = new DatagramPacket(aEnviar, aEnviar.length, info.getRoute().get(msg.getDestino()).getIP(), port);

                                    try {
                                         s.send(p);
                                    } catch (IOException ex) {
                                        System.out.println("ClienteRequest: Send error.");
                                    }
                                }
                               
                            }
                        }
                    }
                    else 
                        System.out.println("Pacote desconhecido...");
                    
                } catch (IOException | ClassNotFoundException ex) {
                    System.out.println("Servidor: Deserialize error.");
                }

                
                
            }
        }
    
        private Object deserializePacote(byte[] bytes) throws IOException, ClassNotFoundException {
            ByteArrayInputStream b = new ByteArrayInputStream(bytes);
            ObjectInputStream o = new ObjectInputStream(b);
            return o.readObject();
        }
}
