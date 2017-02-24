import java.io.*;
import java.net.*;
import java.util.*;

public class peerServer extends Thread{
	private String peerName;
	private int port;
	private Socket socket;
	static String send_split = ">_<";
    static String rec_split = "@_@";
    static String mid_split = "$_$";

	

	public peerServer(String name, int port, Socket socket){
		this.peerName = name;
		this.port = port;
		this.socket = socket;
	}

	public void run(){

		String fileFolder = new String(peerName + "_files/");
	    String dlFolder = new String(peerName + "_download/");
	    String peerAddress = "localhost";

		/*
		get request from other peer  ---the name of the file.
		put the file to the writher stream send it to the client.
		*/
		//get msg from client. divided it by "@"
		BufferedReader input = null;
		System.out.println("server start");
		try{
			 input = new BufferedReader(
			 	 new InputStreamReader( socket.getInputStream()));
		     String msg = input.readLine();
		     //System.out.println(msg);
		     String[] str = msg.split(rec_split);
		     //System.out.println(str[0]);
		     if(str[0].equals("DOWNLOAD")){
			    String file_name = str[1];
			    //read this file and send it to client.
			    String path = fileFolder + file_name;
			    //System.out.println(path); 
			    File file = new File(path); 
			    if(!file.exists()){
			 	   System.out.println("this file doesn't exists");
			    }
			    else{
			 	  try{
			 	    	DataInputStream file_reader = new DataInputStream(new BufferedInputStream(
			    	     	new FileInputStream(path)));
			 		    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			 		    byte[] buffer = new byte[2048];
			 		    while(true){
			 		    	int read = 0;
			 		    	if(file_reader != null){
			 		    		read = file_reader.read(buffer);
			 		    	}

			 		    	if(read == -1) break;

			 		    	out.write(buffer,0,read);
			 		    }
			 		    out.flush();

			 		    file_reader.close();
			 		    out.close();
			 	    }catch(Exception e){
			    	    System.out.println(e.toString());
			 	    }
			 	}

			 	input.close();
			 	socket.close();
			}
			else{
				socket.close();
				return;
			}
		}catch(Exception e){
			System.out.println(e.toString());
		}

	}

}