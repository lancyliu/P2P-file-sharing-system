import java.io.Serializable;

public class MessageID implements Serializable{
	private static final long serialVersionUID = 1L;
	//the sequence number of this message
	public int seqNum;
	//the peer address of this message
	public PeerAddress peerID;

	public MessageID(int seq, PeerAddress pa){
		this.seqNum = seq;
		this.peerID = pa;
	}

	public int getSeqNum(){
		return seqNum;
	}

	public PeerAddress getPeer(){
		return peerID;
	}

}