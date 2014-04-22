package server;

import common.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import p2pchat2012.ConversationClient;
import p2pchat2012.ConversationServer;
import p2pchat2012.Manager;
import p2pchat2012.font_process;


public class ServerChatHandler extends Thread {
        ConversationServer Swindow;
        Manager m;
        private TagReader reader;
	private TagWriter writer;
	public ChatState currentState;
	private String userID = "";
	private String myName;
	private Socket mySock;
	private String filename;
	private static String basedir = "C:/";
	public ServerChatHandler(Socket sock, String ID, Manager n) throws IOException, TagFormatException{
                m=n;
		mySock = sock;
		reader = new TagReader(mySock.getInputStream());
		writer = new TagWriter(mySock.getOutputStream());
                myName=ID;
		currentState = ChatState.START;
                Swindow= new ConversationServer(sock,ID,this,m);
                Swindow.setVisible(true);
           //     basedir = p2pchat2012.P2PChat2012.basedir;
	}
	public void run(){
		try{
			boolean running = true;
			TagValue tv = reader.nextTagValue();
			while (running){
				switch (currentState){
					case START:
						if (tv.isTag(Tags.OPEN_SESSION_REQ)){
							userID = new String (tv.getContent());
                                                        int an=JOptionPane.showConfirmDialog(JOptionPane.getRootFrame(),"Do you want to chat with "+userID+" ?", "Question", JOptionPane.YES_NO_OPTION);
                                                        if (an==0){
                                                            TagValue atv = new TagValue (Tags.OPEN_SESSION_ACK, myName.getBytes(), myName.getBytes().length);
                                                            writer.writeTag(atv);
                                                            currentState = ChatState.CHAT;
                                                            Swindow.setTitle("Conversation with "+userID);
                                                            m.writeLog("Connect to user: "+userID);
                                                            m.writeID_IP(userID+" | "+Swindow.getIPConect());
                                                        }
                                                        else {
                                                            TagValue atv = new TagValue (Tags.SESSION_NOACK, null, 0);
                                                            writer.writeTag(atv);
                                                            currentState = ChatState.END;
                                                            m.writeLog("Unaccept user: "+userID);
                                                            Swindow.dispose();
                                                        }
						}
						tv = reader.nextTagValue();
						break;
					case CHAT:
						if (tv.isTag(Tags.OPEN_CHAT_MSG)){
                                                     // xu ly font
                                                         String msg = new String(tv.getContent(),"UTF8");
                                                         p2pchat2012.font_process newprocess = new font_process();
                                                        ConversationServer.partner_setting = newprocess.parse_msg(ConversationServer.partner_setting , msg);
                                                     //end
                                                        Swindow.Display(userID+": "+msg,false);
							tv = reader.nextTagValue();
						}else if (tv.isTag(Tags.OPEN_FILE_REQ)){
							this.sendChatMSG("<<Received req. " + new String(tv.getContent())+">>");
                                                        Swindow.Display("\n<You're going to receiving "+new String(tv.getContent(),"UTF8")+">",true);
							filename = new String(tv.getContent());
							int an =JOptionPane.showConfirmDialog(JOptionPane.getRootFrame(),"Do you want to receive "+filename+" ?", "Question", JOptionPane.YES_NO_OPTION);
                                                        if (an==0){
                                                            TagValue atv = new TagValue (Tags.FILE_REQ_ACK, null, 0);
                                                            writer.writeTag(atv);
                                                            currentState = ChatState.FILE_REQ_ACK;                                               
                                                        } else {
                                                            TagValue atv = new TagValue (Tags.FILE_REQ_NOACK, null, 0);
                                                            writer.writeTag(atv);
                                                            this.sendChatMSG("<<Cancel req. receive " + filename+">>");
                                                            Swindow.Display("<You cancelled file "+filename+">",true);
                                                            currentState = ChatState.CHAT;
                                                        }
							tv = reader.nextTagValue();
						}else if (tv.isTag(Tags.SESSION_CLOSE)){
							running = false;
							currentState = ChatState.END;
							this.close();
						}
						break;
					case FILE_REQ_ACK:
						if (tv.isTag(Tags.OPEN_CHAT_MSG)){
                                                    // xu ly font
                                                         String msg = new String(tv.getContent(),"UTF8");
                                                         p2pchat2012.font_process newprocess = new font_process();
                                                        ConversationServer.partner_setting = newprocess.parse_msg(ConversationServer.partner_setting , msg);
                                                     //end
                                                        Swindow.Display(userID+": "+msg,false);
                                                    //     Swindow.Display(userID+": "+new String(tv.getContent(),"UTF8"));
							tv = reader.nextTagValue();
						}else if (tv.isTag(Tags.FILE_DATA_BEGIN)){
                                                    
                                                          
                                                        //begin check box
                                                         if(p2pchat2012.P2PChat2012.choosefile)
                                                        {
                                                             final JFileChooser chooser2 = new JFileChooser() {

                                                                  public void approveSelection() {
                                                                   if (getSelectedFile().isFile()) {
                                                                     return;
                                                                     } 
                                                                   else {
                                                                            super.approveSelection();
                                                                        }
                                                                    }
                                                             };                                     
                                                             chooser2.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                                                             chooser2.showDialog(p2pchat2012.ConversationClient.Display_area, "Select");
                                                             basedir = chooser2.getSelectedFile().toString() + '\\';
                 
                                                        }
                                                        else
                                                            basedir = p2pchat2012.P2PChat2012.basedir;
    
                                                        //end checkbox  
                                                  //      basedir = p2pchat2012.P2PChat2012.basedir;
							File f = new File(basedir + filename );
							f.delete();
							this.sendChatMSG("<<Start receiving file>>");
                                                        Swindow.Display("<Start receiving file>",true);
							currentState = ChatState.FILE_RECEIVED;
							tv = reader.nextTagValue();
						}else if (tv.isTag(Tags.SESSION_CLOSE)){
							running = false;
							currentState = ChatState.END;
							this.close();
						}
						break;
					case FILE_SENT:
						break;
					case FILE_REQ_SENT:
						if (tv.isTag(Tags.OPEN_CHAT_MSG)){
                                                     // xu ly font
                                                         String msg = new String(tv.getContent(),"UTF8");
                                                         p2pchat2012.font_process newprocess = new font_process();
                                                        ConversationServer.partner_setting = newprocess.parse_msg(ConversationServer.partner_setting , msg);
                                                     //end
                                                        Swindow.Display(userID+": "+msg,false);
                                                     //   Swindow.Display(userID+": "+new String(tv.getContent(),"UTF8"));
							tv = reader.nextTagValue();
						}else if (tv.isTag(Tags.FILE_REQ_ACK)){// chap nhan file
							currentState = ChatState.CHAT;
							sendFile();
							tv = reader.nextTagValue();
                                                }else if (tv.isTag(Tags.FILE_REQ_NOACK)){//ko chap nhan file
                                                        currentState = ChatState.CHAT;                                                        
							tv = reader.nextTagValue();
						}else if (tv.isTag(Tags.SESSION_CLOSE)){
							running = false;
							currentState = ChatState.END;
							this.close();
						}
						break;
					case FILE_RECEIVED:
						if (tv.isTag(Tags.OPEN_CHAT_MSG)){
                                                     // xu ly font
                                                         String msg = new String(tv.getContent(),"UTF8");
                                                         p2pchat2012.font_process newprocess = new font_process();
                                                        ConversationServer.partner_setting = newprocess.parse_msg(ConversationServer.partner_setting , msg);
                                                     //end
                                                        Swindow.Display(userID+": "+msg,false);
                                                     //   Swindow.Display(userID+": "+new String(tv.getContent(),"UTF8"));
                                                        tv = reader.nextTagValue();
						}else if (tv.isTag(Tags.OPEN_FILE_DATA)){
							FileOutputStream fos = new FileOutputStream(basedir + filename , true);
							byte [] buf = tv.getContent();
							int i = 0;
							while (i < buf.length){
								byte b = buf[i];
								if (i < (buf.length - 1) && b == '<' && buf[i + 1] == '<'){
									fos.write(b);
									i++;
								}else if (i < (buf.length - 1) && b == '>' && buf[i + 1] == '>'){
									fos.write(b);
									i++;
								}else{
									fos.write(b);
								}
								i++;
							}
							fos.close();
							tv = reader.nextTagValue();
						}else if (tv.isTag(Tags.FILE_DATA_END)){ //chu y xem no ki ki
							this.sendChatMSG("<<Finish receiving file>>");
                                                        Swindow.Display("<Finish receiving file>",true);
                                                        Swindow.Display("<This file has placed at "+basedir+">",true);
                                                        currentState = ChatState.CHAT;
                                                        //==============================
							tv = reader.nextTagValue();
						}else if (tv.isTag(Tags.SESSION_CLOSE)){
							running = false;
							currentState = ChatState.END;
							this.close();
						}
						break;
					case END:
                                            running=false;
                                            this.close();
					default:
						break;
				}
			}
                        Swindow.Discon();
		}catch(  TagFormatException | IOException tfe){
			tfe.printStackTrace();
                        Swindow.Discon();
		}
	}
	private void sendChatMSG (String s) throws IOException{
		TagValue tv = new TagValue (Tags.OPEN_CHAT_MSG, s.getBytes(), s.getBytes().length);
		writer.writeTag(tv);
	}
	private void close(){
		try{
			reader.close();
			writer.close();
			mySock.close();
		}catch(IOException ioe){
		}
	}
	private void sendFile() throws IOException{
		System.out.println("Start sending file");
		FileSender fs = new FileSender(writer, Swindow.pathName);
		fs.start();
	}
}
