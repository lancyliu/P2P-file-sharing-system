import java.io.*;
import java.net.*;
import java.util.*;

/*
edit @ Jan 29
Programming assignment 1 for CS550
*/

public class cServer{
	protected static Hashtable<String, ArrayList<ClientInfo>>fileIndex = new Hashtable<String, ArrayList<ClientInfo>>();
	static int TotalClient = 0;// counting client number connected to indexing server
    static String serverAddress = "localhost"; //initial server address
    static final int serverPort = 20000; //initial server socket port
    static String send_split = ">_<"; //define the split sign of send 
    static String rec_split = "@_@"; // define the split sign of receive
    static String mid_split = "$_$"; // define the split sign of lookup
    /*
         * Indexing Server main method to run the server and listen request
         * shows the serveraddress and serverport
         * create an infinite loop to listen on port 20000 (socket port),when connection
         * is requested, it create a new thread return to listening 
         * use peer id to help for each peer make file lookup and search
    */

     public static void main(String[] args) throws Exception{

     	System.out.println("*******************Indexing Server Started*******************");
        System.out.println("* Server " + serverAddress + ", port " + serverPort + " waiting connected *");
	    int peerid = 1;

		new Listener().start();
		String serveAddress = "localhost";
		//System.out.println("server start");
       }
// listener use for keep listening clients' request
     public static class Listener extends Thread{
		 ServerSocket listener = null;
		 int port = serverPort;//Integer.parseInt(serverPort);
		 //the listenerSocket is the serverSocket which is used to handle client thread
		 public void run(){
			 try {
	                listener = new ServerSocket(port);
	                
	                if (listener != null) {
	                    while (true) {
	                        new Indexer(listener.accept()).start();
	                    }
	                }
	            } catch (IOException e) {
	                System.out.println(e.toString());
	                return;
	            } finally {
	                try {
	                    if (listener != null)
	                        listener.close();
	                } catch (IOException e) {
	                    System.out.println(e.toString());
	                }
	            }
		 }

        /*
             * handle peer's request which is received on a particular socket
             * this handler we called "Indexer"  
             * counting clients and get their IP address 
        */
		 public static class Indexer extends Thread{
		 	Socket socket = null;
	        String inputMsg = "";
			public Indexer(Socket socket) {
	                this.socket = socket;
	        }

	        public void run(){
	        	try{
					inputMsg = getInputMsg(socket);
					System.out.println("This is request sent by client: " + inputMsg);
					//get the header of the income message
					String[] message = inputMsg.split(rec_split);
					disPatch(message, socket);
					//functionSelect(headerIn, socket);
					//System.out.println("the size of hash table is :" + fileIndex.size());
					socket.close();
				}catch(Exception e){

					System.out.println("error with indexer "+ e.toString());

				}
	        }
		 }
    }


//function disPatch use to classify incoming massages and judge them whether registr
public static void disPatch(String[] message, Socket socket){
		 if(message[0].equals("REGISTRY")){
			 registry(message,socket);
			 //System.out.println("file registry!");
		 }else if(message[0].equals("LOOKUP")){
			 lookUp(message, socket);
		 }
		 else if(message[0].equals("DELETE")){
		 	deleteFile(message);
		 }
		 return;
	 }


public static String getInputMsg(Socket socket){
		 String input = "";
		 BufferedReader in = null;
		 try {
			 in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			 input = in.readLine();
		 }catch(Exception e){

			 System.out.println("error with get input msg: " + e.toString());
		 }
		 return input;
}

public static void registry(String[] MsgTokens,Socket socket){
	//System.out.println("test point in reg");
    String fileName = MsgTokens[1];
    String clientName = MsgTokens[2];
    String peerIP = MsgTokens[3];
    int peerPort = Integer.parseInt(MsgTokens[4]);

    ClientInfo clientInfo = new ClientInfo(clientName, peerIP,peerPort);

    if (fileIndex.containsKey(fileName)){
                      
            					  //if the server already has the file, we need registry the peerInfo
            					  ArrayList<ClientInfo> list= fileIndex.get(fileName);
            					  list.add(clientInfo);
            					  fileIndex.put(fileName, list);
     }
     else{
           ArrayList<ClientInfo> list = new ArrayList<ClientInfo>();
            					  list.add(clientInfo);
            					  fileIndex.put(fileName, list);
       }
       
       return;
 
}

//deal with lookup request
public static void lookUp(String[] message, Socket socket){
	String fileName = message[1];
    String clientName = message[2];
    String peerIP = message[3];
    int peerPort = Integer.parseInt(message[4]);
    ClientInfo clientInfo = new ClientInfo(clientName, peerIP ,peerPort);
    PrintWriter writer = null;

    String key = fileName;
    System.out.println("lookup file:" + key);
    String output = "";
    String files = "";
     try{
        if(fileIndex.containsKey(key)){
        ArrayList<ClientInfo> list = fileIndex.get(key);
        writer = new PrintWriter(socket.getOutputStream());
        files = String.valueOf(list.size());
        //sendMsg(files, socket);

        //writer.println(files);
        //writer.flush();
        System.out.println("Find "+ list.size()+ " files");
        output = output + files + "@";
        for(int j=0;j<list.size();j++){
             output = output+ key + send_split + list.get(j).clientName + send_split + list.get(j).peerIP + send_split + list.get(j).peerPort + "/";//mid_split;
             //System.out.println(output);
             //sendMsg(output,socket);
              //writer.println(output);
             // writer.flush();
            }
          writer.println(output);
          writer.flush();
         //sendMsg(output,socket);
        }
        else {
        	System.out.println("no such file");
        	writer = new PrintWriter(socket.getOutputStream());
            writer.println("0@0");
            writer.flush();
        }
        }catch(Exception e){
            		  System.out.println(e.toString());
        }
	return;
}

//function for send message
public static void sendMsg(String msg, Socket socket){
	PrintWriter pw = null;
	try{
		pw = new PrintWriter(socket.getOutputStream());
		pw.println(msg);
		pw.flush();
		pw.close();
	}catch(Exception e){
		System.out.println(e.toString());
	}
}
// deal with delete request
public static void deleteFile(String[] message){
	String fileName = message[1];
    String clientName = message[2];
    String peerIP = message[3];
    int peerPort = Integer.parseInt(message[4]);
    ClientInfo clientInfo = new ClientInfo(clientName, peerIP ,peerPort);

    if (fileIndex.containsKey(fileName)){
    //if the server has the file, we need delete this file, the next step is find which peer we need remove 
    ArrayList<ClientInfo> list2 = fileIndex.get(fileName);//get the arraylist of all clients which has the object file 
    if(list2.size() == 1){
    	
    	if(list2.get(0).clientName.equals(clientName) && list2.get(0).peerPort == peerPort){
    	
    		//System.out.println(list2.get(0).clientName);
    		fileIndex.remove(fileName);
    	}
    }
    else{
    	ArrayList<ClientInfo> temp_list = list2;
    	//System.out.println("test point in del 2");

          for(int z=0;z<list2.size();z++){//search the list find the object client
            if(list2.get(z).clientName.equals(clientName) && list2.get(z).peerIP.equals(peerIP) && list2.get(z).peerPort == peerPort){
                 temp_list.remove(list2.get(z));
                 //fileIndex.remove(fileName, list2.get(z));// delete the file and corresponding peer in hashtable
            }
           }
           fileIndex.put(fileName, temp_list);
       }
        }else{
            		  System.out.println("no such file exsist");
            	  }
    /*        	  

    try{}catch(Exception e){
    	System.out.println(e.toString());
    }
    */
}


}
