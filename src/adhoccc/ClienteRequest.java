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
import java.util.Scanner;

/**
 *
 * @author Tiago
 */
public class ClienteRequest extends Thread {
        private InetAddress group;
        private MulticastSocket s;
        private int port;
        private Informacao info;
        
        public ClienteRequest(InetAddress group, MulticastSocket s, int port, Informacao info){
            this.group = group;
            this.s = s;
            this.port = port;
            this.info = info;
        }
        
        @Override
        public void run(){
            PacoteRouteRequest pacote;
            PacoteMsg msg;
            DatagramPacket p;
            byte[] aEnviar = new byte[2048];
            Scanner scan = new Scanner(System.in);
            
            while(true)
            {
                String[] res = scan.nextLine().split(",");

                if(res[0].equals("rr"))
                {
                    info.getPendentes().put(info.getNome()+res[1],null);
                    pacote = new PacoteRouteRequest(info.getNome(), res[1], null);
                    pacote.adicionaVisitado(info.getNome());
                    
                    try {
                        aEnviar = serializePacote(pacote);
                    } catch (IOException ex) {
                        System.out.println("ClienteRequest: Serialize error.");
                    }
                    
                    p = new DatagramPacket(aEnviar, aEnviar.length, group, port);

                    try {
                         s.send(p);
                    } catch (IOException ex) {
                        System.out.println("ClienteRequest: Send error.");
                    }
                }
                if(res[0].equals("msg")){
                    if(info.getVizinhosDirectos().containsKey(res[1]))
                    {
                        msg = new PacoteMsg(info.getNome(),res[1],res[2]);
                        
                        try {
                        aEnviar = serializePacote(msg);
                        } catch (IOException ex) {
                            System.out.println("ClienteRequest: Serialize error.");
                        }

                        p = new DatagramPacket(aEnviar, aEnviar.length, info.getVizinhosDirectos().get(res[1]).getIP(), port);

                        try {
                             s.send(p);
                        } catch (IOException ex) {
                            System.out.println("ClienteRequest: Send error.");
                        }
                    }
                    else
                    {
                        if(info.getRoute().containsKey(res[1]))
                        {
                            msg = new PacoteMsg(info.getNome(),res[1],res[2]);
                        
                            try {
                            aEnviar = serializePacote(msg);
                            } catch (IOException ex) {
                                System.out.println("ClienteRequest: Serialize error.");
                            }

                            p = new DatagramPacket(aEnviar, aEnviar.length, info.getRoute().get(res[1]).getIP(), port);

                            try {
                                 s.send(p);
                            } catch (IOException ex) {
                                System.out.println("ClienteRequest: Send error.");
                            }
                        }
                        else{
                         //se nao tiver na tabela de vizinhos directos ou na da route, preciso de fazer um routerequest para ficar a conhecer uma rota para enviar
                            info.getPendentes().put(info.getNome()+res[1],null);
                            pacote = new PacoteRouteRequest(info.getNome(), res[1], null);
                            pacote.adicionaVisitado(info.getNome());

                            try {
                                aEnviar = serializePacote(pacote);
                            } catch (IOException ex) {
                                System.out.println("ClienteRequest: Serialize error.");
                            }

                            p = new DatagramPacket(aEnviar, aEnviar.length, group, port);

                            try {
                                 s.send(p);
                            } catch (IOException ex) {
                                System.out.println("ClienteRequest: Send error.");
                            }
                            
                            try {
                                Thread.sleep(4000); //time interval
                            } catch (InterruptedException ex) {
                                 System.out.println("ClienteRequest: Thread sleep error.");
                            }
                            //ao fim de 4 segundos, vou verificar novamente se ja tem, se sim mando a mensagem
                            if(info.getRoute().containsKey(res[1]))
                            {
                                msg = new PacoteMsg(info.getNome(),res[1],res[2]);

                                try {
                                    aEnviar = serializePacote(msg);
                                } catch (IOException ex) {
                                    System.out.println("ClienteRequest: Serialize error.");
                                }

                                p = new DatagramPacket(aEnviar, aEnviar.length, info.getRoute().get(res[1]).getIP(), port);

                                try {
                                     s.send(p);
                                } catch (IOException ex) {
                                    System.out.println("ClienteRequest: Send error.");
                                }
                            }
                            else{ System.out.println("Impossivel enviar mensagem!"); }
                        }
                    }
                }
            }
            
        }
        
        private byte[] serializePacote(Object p) throws IOException {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            ObjectOutputStream o = new ObjectOutputStream(b);
            o.writeObject(p);
            o.flush();
            o.close();
            return b.toByteArray();
        }
}
