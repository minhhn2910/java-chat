/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package LogInServer;

/**
 *
 * @author D Thanh
 */
public class Client {
    private String Name="";
    private String IP="";
    private String Port="";
    public Client(String N, String I, String P){
        Name=N;
        IP=I;
        Port=P;
    }
    public String getName(){
        return Name;
    }
    public String getIP(){
        return IP;
    }
    public int getPort(){
        return Integer.parseInt(Port);
    }
}
