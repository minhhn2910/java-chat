package server;

import common.TagFormatException;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import p2pchat2012.Manager;

public class ClientSocketHandler extends Thread {
	private Socket csocket;
        private String ID_Name;
        Manager m;
	public ClientSocketHandler(Socket s, String ID, Manager n){
		csocket = s;
                ID_Name=ID;
                m=n;
	}
    @Override
	public void run(){        
        try {
            ServerChatHandler ish = new ServerChatHandler(csocket,ID_Name,m);
            ish.start();
        } catch (IOException | TagFormatException ex) {
            Logger.getLogger(ClientSocketHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
	
    }
}
