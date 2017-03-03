/*
Edit by Xin Liu
 @ Feb 26
*/
 
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Properties;
/*
this file describe the structure of a Peer, consists of two part, server & client
once start  a peer, start the server part first, then start the client thread.
*/

public class Peer{
	public static void main(String args[]){
		/*
		  1. ask user to enter the peer's name
		  2. read config.txt
		  3. setup folders (same to assignment 1) 
		  4. start server thread
		  5. start client thread
		*/

		System.out.println("This is an Gnutella-style P2P File Sharing System");
		System.out.println("Please input the name of the peer:");
		Scanner scan = new Scanner(System.in);
		String name = scan.nextLine();
		PeerInfo.address.name = name;

		boolean flag = readConfig(name);
		//set up the folders of peer, ensure there are enough test files in the folder, and the size of the file is enough.
		if(flag){
			setFolder(name);
			Connection connect = new Connection();
			//start server thread.
			//start client thread.
			ServerSocket server = null;
			try{
			    server = new ServerSocket(Integer.parseInt(PeerInfo.address.port));
			    //System.out.println("\nServer started!");
			    new PeerServer(server, connect);
			    }catch(IOException e){
			    	e.printStackTrace();
			    }
			//new PeerServer(connect, serversocket).start();
			new PeerClient(connect).start();
		}

	}

/*
read config file. from config file, we can get the ip,port and neighbors of the peer
*/
	public static boolean readConfig(String name){
		String path = PeerInfo.config;
		//File config = new File(path);
		//if(config.isFile() && config.exists()){
		//read config file
		String key = name;
		Properties pro = new Properties();
		InputStream input = null;
		try{
			input = new FileInputStream(path);
			pro.load(input);
			//read peer's ip
			PeerInfo.address.IP = pro.getProperty(key + ".ip");
			PeerInfo.address.port = pro.getProperty(key + ".port");
			//read neighbors.
			String neighbors = pro.getProperty(key + ".neighbors");
			if(neighbors != null){
				String[] arr = neighbors.split(",");
			    if(arr.length >0){
			    	for(int i=0; i<arr.length; i++){
			    		String nip = pro.getProperty(arr[i] + ".ip");
			    		String nport = pro.getProperty(arr[i] + ".port");
			    		//System.out.println("neighbors port is:" + nport);
			    		PeerAddress naddress = new PeerAddress(arr[i], nport, nip);
			    		PeerInfo.neighbors.add(naddress);
			    	}
			    }
			}
			return true;
			}catch(Exception e){
				System.out.println("config file not exists, Please set up your network");
				System.out.println(e.toString());
				return false;
			}
	}


	public static void setFolder(String peerName){

		//set up fileFolder and dlFolder.
		PeerInfo.fileFolder = peerName + "_files/";
		PeerInfo.dlFolder = peerName + "_download/";
		
		try{

			File folder1 = new File(PeerInfo.fileFolder);
			if(!folder1.exists()){
				folder1.mkdir();
			}
			//ensure the files in the folder.
			files(PeerInfo.fileFolder);

			File folder2 = new File(PeerInfo.dlFolder);
			if(!folder2.exists()){
				folder2.mkdir();
			}
		}catch(Exception e){
		   System.out.println(e.toString());
		}
	}
/* 
following two function aims to ensure that, each client contians at least 10 files in its folder, and each file is at least 2kb
*/

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
		    }catch(Exception e){
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