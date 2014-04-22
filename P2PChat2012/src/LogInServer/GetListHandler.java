/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package LogInServer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import p2pchat2012.ChatList;

/**
 *
 * @author D Thanh
 */
public class GetListHandler extends Thread {
    ChatList cl;
    private BufferedReader inFromServer;
    private DataOutputStream outToServer; 
    private String name;
    public boolean running=true;
    public GetListHandler(Socket s,ChatList c, String n) throws IOException{
        cl=c;
        name=n;
        inFromServer =new BufferedReader(new InputStreamReader(s.getInputStream()));
        outToServer = new DataOutputStream(s.getOutputStream());
    }
    public void run(){
        try {             
            outToServer.writeBytes("</login>"+name+"\n");
            outToServer.writeBytes("</ipclient>"+cl.m.getIP()+"\n");
            outToServer.writeBytes("</portclient>"+cl.m.port+"\n");
            while(running){
               try {
                    GetListFromServer GL=new GetListFromServer(inFromServer,cl,this);
                    GL.start();
                    outToServer.writeBytes("</list>"+"\n");
                    sleep(2000);                    
                } catch (   InterruptedException | IOException ex) {
                    if (cl.connect==true) cl.errorDis();
                    running=false;
                    Logger.getLogger(GetListHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                           
            }
        } catch (IOException ex) {
            if (cl.connect==true) cl.errorDis();
            Logger.getLogger(GetListHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
