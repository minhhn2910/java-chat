package common;

public class Tags {
	public static final String OPEN_SESSION_REQ = "<session_req>";
	public static final String CLOSE_SESSION_REQ = "</session_req>";
        public static final String SESSION_NOACK = "<session_noack />";
	public static final String OPEN_SESSION_ACK = "<session_ack>";
	public static final String CLOSE_SESSION_ACK = "</session_ack>";
	public static final String OPEN_CHAT_MSG = "<chat_msg>";
	public static final String CLOSE_CHAT_MSG = "</chat_msg>";
	public static final String OPEN_FILE_REQ = "<file_req>";
	public static final String CLOSE_FILE_REQ = "</file_req>";
	public static final String FILE_REQ_ACK = "<file_req_ack />";
        public static final String FILE_REQ_NOACK = "<file_req_noack />";
	public static final String FILE_DATA_BEGIN = "<file_data_begin />";
	public static final String FILE_DATA_END = "<file_data_end />";
	public static final String OPEN_FILE_DATA = "<file_data>";
	public static final String CLOSE_FILE_DATA = "</file_data>";
	public static final String SESSION_CLOSE = "<session_close />";
	public static final int TAG_MAX_LENGTH = 50;
	public static final int MAX_VALUE_LENGTH = 1024;
	public static final boolean validateTags(String start, String end){//xac nhan tag hop le
		boolean r = false;
		String s, e;
		s = start.toLowerCase();
		e = end.toLowerCase();
		if (s.substring(1, s.length()).equals(e.substring(2, e.length())) && e.charAt(1) =='/' ){
			r = true;
		}
		return r;
	}
}
