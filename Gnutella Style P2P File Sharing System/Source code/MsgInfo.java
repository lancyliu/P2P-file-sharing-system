import java.io.Serializable;

/* store all information needed as an message, this is similar to assignment 1. 
1. we should classify which kind of request of this msg.
2. TTL, ID, filename of this msg
3. As for ID, it contains seq number and peerAdd, seq number is assigned by this peer. 
the seq number of a new message is the index of that msg in the upper leverl.
*/

public class MsgInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	//indicate what this message for-- query, hitquery or download
	public String request;
	//time to live
	public int TTL;
    //where this message from, this maybe stored in messageID
	//public PeerAddress from;
	//where this message send to. this to must be one of its neighbor, do not need to store it
	//public PeerAddress to;
	public MessageID msgID;
	public String filename;
	//this message may store the result of the search.
	public PeerAddress result;

	public MsgInfo(){
		result = null;
		filename = null;
	}

	public void setQuery(MessageID msgID, String filename){
		this.request = "query";
		this.msgID = msgID;
		this.filename = filename;
	}
	public void setHitQuery(MessageID msgID, String filename, PeerAddress result){
		this.msgID = msgID;
		this.filename = filename;
		this.result = result;
		this.request = "hitquery";
	}

	public void setRequest(String input){
		this.request = input;
	}

	public void setTTL(int ttl){
		this.TTL = ttl;
	}

	public String getFileName(){
		return this.filename;
	}

	public MessageID getMsgID(){
		return this.msgID;
	}

	public int getTTL(){
		return this.TTL;
	}

}