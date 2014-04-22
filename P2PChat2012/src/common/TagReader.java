package common;

import java.io.IOException;
import java.io.InputStream;

public class TagReader {
	private InputStream is; 
	public TagReader (InputStream i){
		is = i;
	}
	public synchronized TagValue nextTagValue() throws IOException, TagFormatException{
		int c = is.read();//new >=0 thi co gia tri
		TagValue tv = null;
		if (c != -1){
			tv = this.getTagValue(c);
			System.out.println(tv.getTag());
		}
		return tv;
	}
	private TagValue getTagValue (int preread) throws IOException, TagFormatException{
		TagValue tv;
		int c = preread;
		while (Character.isWhitespace(c)){
			c = is.read();
		}
		String tag;
		if (c == '<'){
			tag = this.getOpenTag();
			//if this is a disconnect tag, return
			if (tag.toLowerCase().equals(Tags.FILE_DATA_BEGIN)){
				tv = new TagValue(Tags.FILE_DATA_BEGIN, null,0);
			}else if (tag.toLowerCase().equals(Tags.FILE_DATA_END)){
				tv = new TagValue(Tags.FILE_DATA_END, null,0);
			}else if (tag.toLowerCase().equals(Tags.SESSION_CLOSE)){
				tv = new TagValue(Tags.SESSION_CLOSE, null,0);
                        }else if (tag.toLowerCase().equals(Tags.SESSION_NOACK)){
                                tv = new TagValue(Tags.SESSION_NOACK, null,0);
                        }else if (tag.toLowerCase().equals(Tags.FILE_REQ_NOACK)){
                                tv = new TagValue(Tags.FILE_REQ_NOACK, null,0);
                        }else if (tag.toLowerCase().equals(Tags.FILE_REQ_ACK)){
                                tv = new TagValue(Tags.FILE_REQ_ACK, null,0);
			}else{
				// if this is not a disconnect tag
				byte [] val = this.getValue();
				String endtag = this.getCloseTag();
				if (Tags.validateTags(tag, endtag)){
					tv = new TagValue (tag, val, val.length);
				}else{
					TagFormatException tfe = new TagFormatException("End tag doesn't match start tag: " + tag + ":" + endtag);
					throw tfe;
				}
			}
		}else{
			TagFormatException tfe = new TagFormatException("An open tag is expected instead of --" + Character.toString((char)c) +"--");
			throw tfe;			
		}
		return tv;
	}
	public String getOpenTag() throws IOException {
		//inputstream has been read a head 1 byte in getTagValue()
		String tag = "<";
		int c = is.read();
		while (c != '>'){
			tag += Character.toString((char)c);
			c = is.read();
		}
		tag += ">";
		return tag.toLowerCase();
	}
	public String getCloseTag () throws IOException{
		//inputstream has been read ahead 2 bytes in getValue()
		String tag = "</";
		int c = is.read();
		while (c != '>'){
			tag += Character.toString((char)c);
			c = is.read();
		}
		tag += ">";
		return tag.toLowerCase();
	}
	public byte [] getValue () throws IOException, TagFormatException {
		byte [] val = new byte[Tags.MAX_VALUE_LENGTH];
		int c = is.read();
		int i = 0;
		boolean complete = false;
		while (!complete){
			if (i >= Tags.MAX_VALUE_LENGTH){
				if (c != '<'){
					TagFormatException tfe = new TagFormatException("Content of a tag must be smaller than " + Tags.MAX_VALUE_LENGTH);
					throw tfe;
				}
			}
			if (c != '<'){
				val[i] = (byte)c;
				i++;
				c = is.read();
			}else{
				int c2 = is.read();
//				 if c is an escape character
				if (c2 == '<'){
					val[i] = (byte)c;
					i++;
					if (i >= Tags.MAX_VALUE_LENGTH){
						TagFormatException tfe = new TagFormatException("Content of a tag must be smaller than " + Tags.MAX_VALUE_LENGTH);
						throw tfe;
					}
					val[i] = (byte)c2;
					i++;
					c = is.read();
				}else{
					if (c2 != '/'){
						TagFormatException tfe = new TagFormatException("Special character '<' should prefixed with an escape character.");
						throw tfe;
					}
					complete = true;
				}
			}
		}
		byte [] val2 = new byte[i];
		for (int j=0;j < i; j++){
			val2[j] = val[j];
		}
		return val2;
	}
	public void close() throws IOException{
		is.close();
	}
	public static void main(String [] args) throws Exception{
		TagReader tr = new TagReader(System.in);
		TagValue tv = tr.nextTagValue();
		while (!tv.getTag().equalsIgnoreCase(Tags.SESSION_CLOSE)){
			System.out.println(tv.getTag());
			System.out.println(new String(tv.getContent()));
			System.out.println("Length: " + tv.getContent().length);
			tv = tr.nextTagValue();
		}
	}
}
