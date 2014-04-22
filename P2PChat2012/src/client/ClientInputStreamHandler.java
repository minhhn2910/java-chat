package client;

import common.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import p2pchat2012.ConversationClient;
import p2pchat2012.Manager;
import p2pchat2012.font_process;

public class ClientInputStreamHandler extends Thread {
        ConversationClient Cwindow;   
	private TagReader reader;
	public TagWriter writer;
	public ChatState currentState;
	private Socket mySock;
	private String userID;
        private String sender;
	private String receivedFile;
	private String basedir = "C:/";	
        Manager m;
	
	public ClientInputStreamHandler(Socket sock, ConversationClient Cwin, String ID, Manager n) throws IOException{
		mySock = sock;
                m=n;
		currentState = ChatState.START;
		reader = new TagReader(sock.getInputStream());
		writer = new TagWriter(sock.getOutputStream());
                Cwindow=Cwin;
                userID=ID;
	}
	public void run(){
		try{
                    if (!Cwindow.isVisible()) this.close(); //hgdjfhkfFITYUVGHZVKZCVHKZCVHGJZV BZNMB
			TagValue tv = null;
			boolean running = true;
			while (running){ 
				switch (currentState){
					case START:
						TagValue atv = new TagValue(Tags.OPEN_SESSION_REQ,userID.getBytes(), userID.getBytes().length);
						writer.writeTag(atv);                                               
						currentState = ChatState.CHAT_REQ;
						tv = reader.nextTagValue();
						break;
					case CHAT_REQ:
						if (tv.isTag(Tags.OPEN_SESSION_ACK)){
							currentState = ChatState.CHAT;                                               
                                                        sender=new String (tv.getContent());
                                                        Cwindow.setTitle("Conversation with "+sender);
                                                        m.writeLog("Conect to ip: "+Cwindow.getIPConect());
                                                        m.writeID_IP(sender+" | "+Cwindow.getIPConect());
							tv = reader.nextTagValue();							
						}//----------------------------
                                                else {//moi them vao
                                                    currentState = ChatState.END;
                                                    m.writeLog("IP: "+Cwindow.getIPConect()+" unaccepted");
                                                    Cwindow.Display("<This user unaccepted to chat with you>",true);
                                                }
                                                //-----------------------------
						break;
					case CHAT:
						if (tv.isTag(Tags.OPEN_CHAT_MSG)){
                                                     // xu ly font
                                                        String msg = new String(tv.getContent(),"UTF8");
                                                       p2pchat2012.font_process newprocess = new font_process();
                                                        ConversationClient.partner_setting = newprocess.parse_msg(ConversationClient.partner_setting , msg);
                                                     //end
                                                        Cwindow.Display(sender+": "+msg,false);
							tv = reader.nextTagValue();							
						}else if (tv.isTag(Tags.OPEN_FILE_REQ)){
							this.sendChatMSG("<<Received req. " + new String(tv.getContent())+">>");
                                                        Cwindow.Display("\n<You're going to receiving "+new String(tv.getContent(),"UTF8")+">",true);
                                                        //xem cho nay lai---------------------
                                                        receivedFile = new String(tv.getContent());
                                                        int an =JOptionPane.showConfirmDialog(JOptionPane.getRootFrame(),"Do you want to receive "+receivedFile+" ?", "Question", JOptionPane.YES_NO_OPTION);
                                                        if (an==0){
                                                            TagValue atvv = new TagValue (Tags.FILE_REQ_ACK, null, 0);
                                                            writer.writeTag(atvv);
                                                            currentState = ChatState.FILE_REQ_ACK; 
                                                        }else{
                                                            TagValue atvv = new TagValue (Tags.FILE_REQ_NOACK, null, 0);
                                                            writer.writeTag(atvv);
                                                            this.sendChatMSG("<<Cancel req. receive " + receivedFile+">>");
                                                            Cwindow.Display("<You cancelled file "+receivedFile+">",true);
                                                            currentState = ChatState.CHAT; 
                                                        }
                                                        //===================================
							tv = reader.nextTagValue();
						}else if (tv.isTag(Tags.SESSION_CLOSE)){
							running = false;
							currentState = ChatState.END;
							this.close();
						}
						break;
					case FILE_REQ_SENT:
						if (tv.isTag(Tags.OPEN_CHAT_MSG)){
                                                    // xu ly font
                                                        String msg = new String(tv.getContent(),"UTF8");
                                                       p2pchat2012.font_process newprocess = new font_process();
                                                        ConversationClient.partner_setting = newprocess.parse_msg(ConversationClient.partner_setting , msg);
                                                     //end
                                                         Cwindow.Display(sender+": "+msg,false);
							tv = reader.nextTagValue();							
						}else if (tv.isTag(Tags.FILE_REQ_ACK)){//chap nhan file
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
					case FILE_REQ_RECEIVED:
						break;
					case FILE_REQ_ACK:
						if (tv.isTag(Tags.OPEN_CHAT_MSG)){							
                                                         // xu ly font
                                                        String msg = new String(tv.getContent(),"UTF8");
                                                       p2pchat2012.font_process newprocess = new font_process();
                                                        ConversationClient.partner_setting = newprocess.parse_msg(ConversationClient.partner_setting , msg);
                                                     //end
                                                         Cwindow.Display(sender+": "+msg,false);						
							tv = reader.nextTagValue();							
						}else if (tv.isTag(Tags.FILE_DATA_BEGIN)){
							this.sendChatMSG("<<Start receiving file>>");
                                                        Cwindow.Display("<Start receiving file>",true);
                                                        
                                                        //begin check box
                                                         if(p2pchat2012.P2PChat2012.choosefile)
                                                        {
                                                             final JFileChooser chooser1 = new JFileChooser() {

                                                                  public void approveSelection() {
                                                                   if (getSelectedFile().isFile()) {
                                                                     return;
                                                                     } 
                                                                   else {
                                                                            super.approveSelection();
                                                                        }
                                                                    }
                                                             };                                     
                                                             chooser1.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                                                             chooser1.showDialog(p2pchat2012.ConversationClient.Display_area, "Select");
                                                             basedir = chooser1.getSelectedFile().toString() + '\\';
                 
                                                        }
                                                        else
                                                            basedir = p2pchat2012.P2PChat2012.basedir;
    
                                                        //end checkbox  
                                                         basedir = p2pchat2012.P2PChat2012.basedir;
							File f = new File(basedir + receivedFile );
							f.delete();
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
					case FILE_RECEIVED:
						if (tv.isTag(Tags.OPEN_CHAT_MSG)){
                                                         // xu ly font
                                                        String msg = new String(tv.getContent(),"UTF8");
                                                       p2pchat2012.font_process newprocess = new font_process();
                                                        ConversationClient.partner_setting = newprocess.parse_msg(ConversationClient.partner_setting , msg);
                                                     //end
                                                         Cwindow.Display(sender+": "+msg,false);
							tv = reader.nextTagValue();							
						}else if (tv.isTag(Tags.OPEN_FILE_DATA)){
							FileOutputStream fos = new FileOutputStream(basedir + receivedFile , true);
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
						}else if (tv.isTag(Tags.FILE_DATA_END)){
							this.sendChatMSG("<<Finish receiving file>>");
                                                        Cwindow.Display("<Finish receiving fil>",true);
                                                        Cwindow.Display("<This file has placed at "+basedir+">",true);
							currentState = ChatState.CHAT;
							tv = reader.nextTagValue();
						}else if (tv.isTag(Tags.SESSION_CLOSE)){
							currentState = ChatState.END;
							running = false;
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
                        Cwindow.Discon();
		}catch(IOException ioe){
			System.out.println("Error reading InputStream");
			ioe.printStackTrace();
                        Cwindow.Discon();
		}catch(TagFormatException tfe){
			tfe.printStackTrace();
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
			ioe.printStackTrace();
		}
	}
	private void sendFile() throws IOException{
		FileSender fs = new FileSender(writer, Cwindow.pathName);
		fs.start();
	}

}
