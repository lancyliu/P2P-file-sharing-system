import java.io.Serializable;

public class PeerAddress implements Serializable{
	private static final long serialVersionUID = 1L;
	public String name;
	public String port;
	public String IP;


	public PeerAddress(){}
	
	public PeerAddress(String name, String port, String IP){
		this.name = name;
		this.port = port;
		this.IP = IP;
	}

	public String getName(){
		return this.name;
	}

	public String getPort(){
		return this.port;
	}

	public String getIP(){
		return this.IP;
	}
}