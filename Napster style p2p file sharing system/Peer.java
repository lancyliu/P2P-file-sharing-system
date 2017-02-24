import java.io.*;
import java.net.*;
import java.util.*;

public class Peer{
	/*1. input peer's name, port
	  2. set up two new folder. 
	  ---name of folder: 
	      1. peer_name + files: save the files of peer
	      2. peer_name + download: save the files download from other peer.
	  3. open Peer的 client and server's thread

	*/

	//String fileFolder = new String(); //this folder store the files this peer contains;
	//String dlFolder = new String(); //this folder store the files downloaded from other peer

	//set up fileFolder and dlFolder.
	//String fileFolder = peerName + "_files";
	//String dlFolder = peerName + "download";

	public static void main(String args[]) throws IOException{

		int peerID; //got this id after register to central server

	    String central_server_address = "localhost";
	    String central_server_port = "20000";
		//get info from user, name of client, port of client, files in this client..
		System.out.println("set up a new peer, please input the name of the peer(press enter to end):");
		Scanner scan = new Scanner(System.in);
		String peerName = scan.nextLine();
		System.out.println("please input the port number of peer:");
		int peerPort = scan.nextInt();
		//ask user to name some files for this client.
		//System.out.println("If you want to add some files, please input the name of the files.");
		//System.out.println("--the name of the files is divied by the space");
		//String fileNames = scan.nextLine();

		//set up fileFolder and dlFolder.
	    String fileFolder = peerName + "_files";
	    String dlFolder = peerName + "_download";

	    File folder1 = new File(fileFolder);
	    if(!folder1.exists()){
	    	folder1.mkdir();
	    }

	    //this part also can be done in the following.
	    File folder2 = new File(dlFolder);
	    if(!folder2.exists()){
	    	folder2.mkdir();
	    }
		// start peer client thread 
		// start peer server thread
		System.out.println("Start the client..........");
		System.out.println("The name of the client is: " + peerName);

		/*
		1. run peer client. in run() function. register this peer, get peerID.
		  then ask user to chosse: A. look up -- central server.
		                            B. download.--- start peer server thread.
		*/

		//set up the files in this client.
		String path = fileFolder;
	    files(path);

		new peerClient(peerName, peerPort).start();

		/*
		2. run peer server.
		   at first, run a server socket, listen the msg from other peer
		*/
		//System.out.println("test point 1");
		   /*
		new Thread(){
			ServerSocket pServer = null;
			public void run(){
				try{
					pServer = new ServerSocket(peerPort);

					if(pServer != null){
						while(true){

							//start the server process
							new peerServer(peerName,peerPort,pServer.accept()).start();
						}
					}
				}catch(Exception e){
					System.out.println("Error occur when start peer server thread");
				}
			}
		}.start();
		*/
		/*
		
		*/

		ServerSocket listener = new ServerSocket(peerPort);
		try{
			if(listener != null){
		while(true){
			new peerServer(peerName,peerPort,listener.accept()).start();
		}
		}
	    }catch(Exception e){
	    	System.out.println(e.toString());
	    }finally{
	    	listener.close();
	    }
	   

	}


/* following two function aims to ensure that, each client contians at least 10 files in its folder, and each file is at least 2kb*/

   public static void files(String path){
	File f = new File(path);
	File[] file_list = f.listFiles();
	if(file_list.length < 10){
		try{
			int init = file_list.length;
			for(int i=init; i < 11; i++){
				String name = "/test" + i + ".txt";
				String filePath = path + name;
				File newfile = new File(filePath);
				if(!newfile.exists()) {
					newfile.createNewFile();
					//writeTofile(filePath);
				}
			}
			File[] f_list = f.listFiles();
			int ll = f_list.length;
            
			for(int j=0; j<ll; j++){
				//if length is smaller than 1kb
				if(f_list[j].length() < 1024){

					writeTofile(f_list[j]);
				}
			}
		}
		catch(Exception e){
			System.out.println(e.toString());
		}
	}
	return;

}


public static void writeTofile(File fi){
	String a = new String("this is content of " + fi.getName());

	String path = fi.getPath();

	try{
		FileWriter fileWritter = new FileWriter(fi.getPath(),true);
        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);

        while(fi.length() < 2048){
        	bufferWritter.write(a);
        	bufferWritter.flush();
        }
        
        bufferWritter.close();

	}catch(Exception e){
		System.out.println(e.toString());
	}

	return;
}

}
