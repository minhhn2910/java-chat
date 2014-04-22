/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package serverp2pchat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author D Thanh
 */
public class ServerListener extends Thread{
    private ServerSocket ssocket;
    private int serverPort;
    ServerManager sm;
    
    public ServerListener(int port,ServerManager s){
        serverPort=port;
        sm=s;
    }
    public void run(){

        try {
            ssocket = new ServerSocket(serverPort);
        } catch (IOException ex) {
            Logger.getLogger(ServerListener.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
	while(true){
            try {
                Socket s = ssocket.accept();           
                ClientHandler csh = new ClientHandler(s,sm);            
                csh.start();
            } catch (IOException ex) {
                sm.writeLog("Cannot accept with this socket");
                Logger.getLogger(ServerListener.class.getName()).log(Level.SEVERE, null, ex);
            }
	}
    }
}
