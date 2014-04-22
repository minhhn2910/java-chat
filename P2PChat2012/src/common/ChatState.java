package common;

public enum ChatState {
	START,
	CHAT_REQ,// client side only
	CHAT, 
	FILE_REQ_SENT, // file transfer initiator only
	FILE_REQ_RECEIVED,
	FILE_REQ_ACK,
	FILE_SENT, //file transfer initiator only
	FILE_RECEIVED, // file receiver
	END
}
