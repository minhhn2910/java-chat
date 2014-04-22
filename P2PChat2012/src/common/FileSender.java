package common;

import java.io.FileInputStream;
import java.io.IOException;

public class FileSender extends Thread {
	TagWriter writer;
	String file2send;
	public FileSender(TagWriter tw, String filename){
		writer = tw;
		file2send = filename;
	}
	public void run(){
		byte [] sendbuf = new byte[Tags.MAX_VALUE_LENGTH];
		try{
			TagValue atv = new TagValue(Tags.FILE_DATA_BEGIN, null, 0);
			writer.writeTag(atv);
			FileInputStream fis = new FileInputStream(file2send);
			int c = fis.read();
			int i = 0;
			while (c!= -1){
				if (i == Tags.MAX_VALUE_LENGTH){
					TagValue tv = new TagValue(Tags.OPEN_FILE_DATA, sendbuf, Tags.MAX_VALUE_LENGTH);
					writer.writeTag(tv);
//					System.out.println("tag size: " + tv.getContentLength());
					i = 0;
					sendbuf = new byte[Tags.MAX_VALUE_LENGTH];
				}
				if ((c == '<')||(c == '>')){
                                    if (i>=Tags.MAX_VALUE_LENGTH-1) {
                                        byte [] val2 = new byte[i];
					for (int j=0;j < i; j++){
						val2[j] = sendbuf[j];
					}
					TagValue tv = new TagValue(Tags.OPEN_FILE_DATA, val2, i);
					writer.writeTag(tv);
                                        i=0;
                                        sendbuf = new byte[Tags.MAX_VALUE_LENGTH];
                                    }
                                    sendbuf[i] = (byte) c;
                                    i++;                                   
                                }
				if (i == Tags.MAX_VALUE_LENGTH){
					TagValue tv = new TagValue(Tags.OPEN_FILE_DATA, sendbuf, Tags.MAX_VALUE_LENGTH);
					writer.writeTag(tv);
//					System.out.println(new String(sendbuf));
//					System.out.println("tag size: " + tv.getContentLength());
					i = 0;
					sendbuf = new byte[Tags.MAX_VALUE_LENGTH];
				}
				sendbuf[i] = (byte) c;
//				System.out.print(c);
				i++;
				c = fis.read();
			}
			if (i > 0){
				if (i == Tags.MAX_VALUE_LENGTH){
					TagValue tv = new TagValue(Tags.OPEN_FILE_DATA, sendbuf, Tags.MAX_VALUE_LENGTH);
					writer.writeTag(tv);
//					System.out.println(new String(sendbuf));
//					System.out.println("tag size: " + tv.getContentLength());
				}else{
					byte [] val2 = new byte[i];
					for (int j=0;j < i; j++){
						val2[j] = sendbuf[j];
					}
					TagValue tv = new TagValue(Tags.OPEN_FILE_DATA, val2, i);
					writer.writeTag(tv);
//					System.out.println("tag size: " + tv.getContentLength());
//					System.out.println(new String(val2));
				}
			}                      
			fis.close();
			atv = new TagValue(Tags.FILE_DATA_END, null, 0);
			writer.writeTag(atv);
		}catch(IOException ioe){
		}
	}
}
