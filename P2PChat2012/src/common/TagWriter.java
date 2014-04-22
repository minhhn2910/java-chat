package common;

import java.io.IOException;
import java.io.OutputStream;

public class TagWriter {
	private OutputStream myOS;
	public TagWriter (OutputStream os){
		myOS = os;
	}
	public synchronized void writeTag(TagValue tv) throws IOException{
		if (tv.isTag(Tags.OPEN_SESSION_REQ)){
			myOS.write(Tags.OPEN_SESSION_REQ.getBytes());
			myOS.write(tv.getContent());
			myOS.write(Tags.CLOSE_SESSION_REQ.getBytes());
		}else if (tv.isTag(Tags.OPEN_SESSION_ACK)){
			myOS.write(Tags.OPEN_SESSION_ACK.getBytes());
			myOS.write(tv.getContent());
			myOS.write(Tags.CLOSE_SESSION_ACK.getBytes());
		}else if (tv.isTag(Tags.OPEN_CHAT_MSG)){
			myOS.write(Tags.OPEN_CHAT_MSG.getBytes());
			myOS.write(tv.getContent());
			myOS.write(Tags.CLOSE_CHAT_MSG.getBytes());	
		}else if (tv.isTag(Tags.OPEN_FILE_DATA)){
			myOS.write(Tags.OPEN_FILE_DATA.getBytes());
			myOS.write(tv.getContent());
			myOS.write(Tags.CLOSE_FILE_DATA.getBytes());
		}else if (tv.isTag(Tags.OPEN_FILE_REQ)){
			myOS.write(Tags.OPEN_FILE_REQ.getBytes());
			myOS.write(tv.getContent());
			myOS.write(Tags.CLOSE_FILE_REQ.getBytes());
		}else if (tv.isTag(Tags.FILE_REQ_ACK)){
			myOS.write(Tags.FILE_REQ_ACK.getBytes());
                }else if (tv.isTag(Tags.FILE_REQ_NOACK)){
			myOS.write(Tags.FILE_REQ_NOACK.getBytes());
                }else if (tv.isTag(Tags.SESSION_NOACK)){
			myOS.write(Tags.SESSION_NOACK.getBytes());
		}else if (tv.isTag(Tags.FILE_DATA_BEGIN)){
			myOS.write(Tags.FILE_DATA_BEGIN.getBytes());
		}else if (tv.isTag(Tags.FILE_DATA_END)){
			myOS.write(Tags.FILE_DATA_END.getBytes());
		}else if (tv.isTag(Tags.SESSION_CLOSE)){
			myOS.write(Tags.SESSION_CLOSE.getBytes());
		}else{
			System.out.println("Tag wasn't correctly defined");
		}		
	}
	public void close() throws IOException {
		myOS.flush();
		myOS.close();
	}
}
