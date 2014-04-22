package common;

public class TagValue {
	private String tag;
	private byte[] content;
	private int length = 0;
	public TagValue(){
		tag = "";
		content = null;
	}
	public TagValue(String t, byte [] c, int l){
		tag =t;
		length = l;
		content = c;
	}
	public void set (String t, byte [] c, int l){
		tag =t;
		length = l;
		content = c;
	}
	public String getTag(){
		return tag;
	}
	public boolean isTag(String t){//kiem tra tag xem co giong ko
		return tag.equalsIgnoreCase(t.trim());
	}
	public byte [] getContent(){
		return content;
	}
	public int getContentLength(){
		return length;
	}
	public String valueToString(){
		return new String(content);
	}
}
