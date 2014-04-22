/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package serverp2pchat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author D Thanh
 */
public class ClientHandler extends Thread{
    private Socket mySock;
    private BufferedReader inFromClient;
    private DataOutputStream outToClient; 
    private Client newClient;
    private String Name;
    private String IP;
    private String Port;
    ServerManager sm;
    private int index=0;
    public ClientHandler(Socket s, ServerManager se) throws IOException{
       mySock=s;
       inFromClient =new BufferedReader(new InputStreamReader(s.getInputStream()));
       outToClient = new DataOutputStream(s.getOutputStream());
       sm=se;
    }
    public void run(){
        boolean running=true;                  
            while(running){
            try {
                String msg = inFromClient.readLine();            
                if (msg.startsWith("</login>")){
                    msg=msg.substring(8);             
                    Name=msg;
                }
                else if (msg.startsWith("</ipclient>")){
                    msg=msg.substring(11);
                    IP=msg;
                }
                else if (msg.startsWith("</portclient>")){
                    msg=msg.substring(13);
                    Port=msg;
                    newClient= new Client(Name,IP,Port);// khi da nhan du Name, IP
                    //TO DO .............................
                    for (int i=0; i<sm.MAXUSER;i++){
                        if (sm.list[i]==null) {index=i; break;}
                        else index=sm.MAXUSER;
                    }
                    if (index<sm.MAXUSER){                        
                        sm.count++;                                           
                        sm.list[index]=newClient;
                        sm.displayCount();
                        sm.writeLog("User "+Name+" | "+IP+" | "+Port+" connected");
                    }
                    else {
                        try {                            
                            outToClient.writeBytes("</full>"+"\n");
                            sm.writeLog("User "+Name+" | "+IP+" couldn't connect. Server is full");
                            running=false;
                        } catch (IOException ex) {
                            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    //-----------------------------------
                }
                else if (msg.startsWith("</list>")){
                    //TODO ....
                    for (int i=0; i<sm.MAXUSER; i++){
                        if (sm.list[i]!=null) {                            
                                outToClient.writeBytes("</name>"+sm.list[i].getName()+"\n");         
                                outToClient.writeBytes("</ip>"+sm.list[i].getIP()+"\n");
                                outToClient.writeBytes("</port>"+sm.list[i].getPort()+"\n");
                        }
                    }
                    try {
                        outToClient.writeBytes("</end>"+"\n");
                    } catch (IOException ex) {
                        Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else if (msg.startsWith("</disconect>")){
                    try {
                        sm.writeLog("User "+Name+" | "+IP+" | "+Port+" disconnected");
                        sm.list[index]=null;
                        sm.count--;
                        sm.displayCount();       
                        mySock.close();
                        running=false;
                    } catch (IOException ex) {
                        Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (sm.kickclient==index){
                    sm.writeLog("User "+Name+" | "+IP+" | "+Port+" dropped out of server");
                    sm.list[index]=null;
                    sm.count--;
                    sm.displayCount();
                    sm.kickclient=sm.MAXUSER+1;//tranh kik tiep user do
                    running=false;
                    mySock.close();
                }
            } catch (IOException ex) {
                try {
                    sm.writeLog("Error! Read or write from client");
                    running=false;
                    sm.writeLog("User "+Name+" | "+IP+" | "+Port+" disconnected");
                    sm.list[index]=null;
                    sm.count--;
                    sm.displayCount();       
                    mySock.close();
                    Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex1) {
                    Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            }
    }
}
