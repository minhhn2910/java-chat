/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package LogInServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import p2pchat2012.ChatList;

/**
 *
 * @author D Thanh
 */
public class GetListFromServer extends Thread {
    
    private ChatList cl;
    private BufferedReader inFromServer;
    private String name;
    private String ip;
    private String port;
    GetListHandler glh;
    
    public GetListFromServer(BufferedReader in, ChatList c, GetListHandler x) throws IOException{
        cl=c;
        inFromServer =in;
        glh=x;
    }
    
    public void run(){
        boolean running=true;        
        int count=0;
        for (int i=0; i<cl.MAXUSER; i++){
            cl.list[i]=null;
        }
        while(running){
            try {
                String msg = inFromServer.readLine();            
                if (msg.startsWith("</full>")){
                    cl.errorFull();                    
                    running=false;
                    glh.running=false;
                }
                else if (msg.startsWith("</name>")){
                    msg=msg.substring(7);
                    name=msg;
                }
                else if (msg.startsWith("</ip>")){
                    msg=msg.substring(5);
                    ip=msg;
                    //cl.list[count++]=new Client(name,ip);
                }
                else if (msg.startsWith("</port>")){
                    msg=msg.substring(7);
                    port=msg;
                    cl.list[count++]=new Client(name,ip,port);
                }
                else if (msg.startsWith("</end>")) {
                    Client temp;
                    for (int i=0;i<count-1;i++){
                        for (int j=i; j<count-1; j++){
                            if (cl.list[j].getName().compareTo(cl.list[j+1].getName())>0){
                                temp=cl.list[j];
                                cl.list[j]=cl.list[j+1];
                                cl.list[j+1]=temp;
                            }
                        }
                    }
                    cl.displayList();
                    running=false;
                }
            } catch (IOException ex) {
                if (cl.connect==true) cl.m.writeLog("Error! Communication with server");
                running=false;
                Logger.getLogger(GetListFromServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }    
}
