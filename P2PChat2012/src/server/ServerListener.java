package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import p2pchat2012.Manager;

public class ServerListener extends Thread{
	private ServerSocket ssocket;
	private int serverPort;
        private String ID_Name;
        Manager m;
	public ServerListener(int port,String ID, Manager n){
		serverPort = port;
                ID_Name=ID;
                m=n;
	}
	public void run(){
		try {
			ssocket = new ServerSocket(serverPort);
			while(true){
				Socket s = ssocket.accept();
				ClientSocketHandler csh = new ClientSocketHandler(s,ID_Name,m);
				csh.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
