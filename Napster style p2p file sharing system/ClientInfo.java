//package indexServer;
import java.io.*;
import java.net.*;
public class ClientInfo implements Serializable{
         /*
          * 
          */
	    private static final long serialVersionUID = 2L;
	    public String clientName;
	    public String peerIP;
	    public int peerPort;
	    public ClientInfo(String name,String ip,int port){
	    	clientName = name;
	    	peerIP = ip;
	    	peerPort = port;
	    }
}
	    