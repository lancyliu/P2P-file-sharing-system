import java.io.*;
import java.net.*;
import java.util.*;

//shall we log everything log.txt as the output??

public class peerClient extends Thread{

	public String peerName = new String();
	public String central_server_address = "localhost";
	String central_server_port = "20000";
	String peer_address = "localhost";
	static String rec_split = ">_<";
    static String send_split = "@_@";
    static String mid_split = "$_$";

	int peerPort;
	int peerID;
	String fileFolder;
	String dlFolder;
	boolean test;

	peerClient(String name, int port){
		this.peerName = name;
		this.peerPort = port;
		fileFolder = new String(peerName + "_files/");
	    dlFolder = new String(peerName + "_download/");
	    this.test = test;
	}


	public void run() {

		//String fileFolder = new String(peerName + "_file/");
	    //String dlFolder = new String(peerName + "_download/");
        System.out.println("start");
        register();
		
		while(true){
			//add an option of add a file to local folder.
			BufferedReader input = null;
			try{
				input = new BufferedReader(new InputStreamReader(System.in));
			//input = new BufferedReader(new InputStreamReader(System.in));


			System.out.println("Please choose between the following:");
		    System.out.println("1. look up files");
		    System.out.println("2. download files from other peer");
		    System.out.println("3. delete one file in the client");
            

            //Scanner scan = new Scanner(System.in);
		    int choice = Integer.parseInt(input.readLine());//scan.nextInt();

		    switch(choice){
			    case 1:
			    //register all the files in this peer to the central server;
		         System.out.println("please input the file's name you want to search");
		         //Scanner scan111 = new Scanner(System.in);
		         //String name1 = scan111.nextLine();
		         String name1 = input.readLine();
		         System.out.println("Gonna find the file " + name1);
		         ArrayList<String> result = lookup(name1);
		         System.out.println("Following is the result of lookup");
		         for(int m=0; m< result.size(); m++){
		         	System.out.println(result.get(m));
		         }
		         break;
		        case 2:
		         System.out.println("input name of files, you want to download");
		       
		         //int port = System.in.nextInt();
		         //String name2 = scan.nextLine();
		         String name2 = input.readLine();
		         //look up this file in central server.
		           System.out.println("Gonna find the file " + name2);

		         ArrayList<String> peers = lookup(name2);
		         if(peers != null){
		         	
		         	String peer_add;
		         	if(peers.size() > 1){
		         		System.out.println("found this file you want to download");
		         		System.out.println("There are " + peers.size() + " peers contain this file");
		         		System.out.println("Folloing is the name and port of the peer");
		         		for(int j=0; j<peers.size(); j++){
		         			System.out.println( j + " : " + peers.get(j));
		         		}

		         		System.out.println("Please input the index of the peer you want to connect");

		         		//Scanner sc = new Scanner(System.in);
		         		int index = Integer.parseInt(input.readLine());//sc.nextInt();

		         		while(index >= peers.size() || index <0){
		         			System.out.println("index error, please input again.");
		         			index = Integer.parseInt(input.readLine());
		         		}
		         		peer_add = peers.get(index);
		         		String[] str = peer_add.split(rec_split);
		         		//String peer_name = str[0];
		         		String peer_port = str[3];
		         		String peerAd = str[2];
		         		String p_name = str[1];
		         		download(Integer.parseInt(peer_port), peer_address, name2);
		         		System.out.println("The file is download, and saved in the local folder, name of folder is " + peerName + "_download");
		         	}
		         	else if(peers.size() == 1){
		         		
		         		peer_add = peers.get(0);
		         		if(peer_add.equals("") || peer_add.equals(" ")){
		         			System.out.println("No peer contains this file!");
		         		} 
		         		else{
		         			System.out.println("found this file you want to download");
		         			System.out.println("Folloing is the information of the peer:" + peers.get(0));
		         		     String[] str = peer_add.split(rec_split);
		         		     //String peer_name = str[0];
		         		     String peer_port = str[3];
		         		     String peerAd = str[2];
		         		     String p_name = str[1];
		         		     download(Integer.parseInt(peer_port), peer_address, name2);
		         		     System.out.println("The file is download, and saved in the local folder, name of folder is " + peerName + "_download");
		         		 }
		         	}
		         	else{
		         		System.out.println("No peer contains this file!");
		         	}
		         }
		         //peerSever ps = new peerSever((peerName,port).start();
		         //download(filename);
		         break;
		       case 3:
		         System.out.println("input the name of file you want to delete");
		         //Scanner s3 = new Scanner(System.in);
		         String name = input.readLine();//s3.nextLine();
		         System.out.println(name);
		         if(name != null){
		         	del_File(name);
		         	System.out.println(name + " is deleted! ");
		         }
		         else{
		         	System.out.println("input illegal!");
		         }
		         break;
		       case 4:
		          //register();
		          download(7878, "localhost", "t1.txt");
		          break;
		       /*
		          Scanner s4 = new Scanner(System.in);
		          int port = s4.nextInt();
		          String filename = "t1.txt";
		          String add = "localhost";
		          download(port,add,filename);
		          break;
		        */
		         //del_File(name);
		        // search this file in file folder, if not found, break, if found, del it in server also.	    
		    }
		}catch(Exception e){
			System.out.println(e.toString());
		}
		}
	}

	public void register(){

		String msg = "REGISTRY";
		//send msg to the central server.
		/*
		  1.get the name of all files in this peer, store it in a string list, send this list to the c_server.
		  2.recieve response from central server, if true return success. otherwise, print failed.
		*/

		 /*
		 use socket and buffer stream. need to catch exception.
		 */

		//get all files in fileFolder.
		File files = new File(fileFolder);
		File[] filelist = files.listFiles();
	    ArrayList<String> fileName = new ArrayList<String>();
		

		if(filelist == null){
			//still registering ? ?
			System.out.println("there are no file in this peer.");
		}
		else{
		    for(File f : filelist){
			fileName.add(f.getName());
		    }
		}


		try{
			//if we add all info to each data, may not need syn in central server.
			//because we can regonize each request.
			//if we send request one by one, in server part we should consider the syn problem.
			// -----what if server also accept other request.
			Socket socket = null;//new Socket(central_server_address, Integer.parseInt(central_server_port));
			PrintWriter os = null; //new PrintWriter(socket.getOutputStream());

			//Socket socket = new Socket(central_server_address, Integer.parseInt(central_server_port));
			//PrintWriter os = new PrintWriter(socket.getOutputStream());

            //ed1: no syn
			//msg = msg;  // + "_"+ peerName + "_" + String.valueOf(peerPort);
			for(int i=0; i<fileName.size(); i++){
				
				String temp = "";
				temp = msg + send_split + fileName.get(i) + send_split + peerName + send_split + peer_address + send_split + String.valueOf(peerPort);
				System.out.println(temp);
				sendRegMsg(temp);
				/*
				socket = new Socket(central_server_address, Integer.parseInt(central_server_port));
				os = new PrintWriter(socket.getOutputStream());
				os.println(temp);
				os.flush();
				os.close();
				socket.close();
				*/
			}
			//os.flush();
			//os.close();

			/*
			ed2: syn edition
			os.println(msg);
			os.flush();
			os.println(peerName + '@' + String.valueOf(peerPort));
			os.flush();
			for(int i=0; i<fileName.length; i++){
				os.println(fileName.get(i));
				os.flush();
			}
			*/

			//get peerID from central server
			//BufferedReader b_read = new BufferedReader(
			//	     new InputStreamReader(socket.getInputStream()));
			//String response = b_read.readLine();
			//peerID = Integer.parseInt(response);

			//b_read.close();
			//socket.close();

			System.out.println("register success!");

		}catch(Exception e){
			System.out.println(e.toString());
		}
		
	}

	public void sendRegMsg(String message){
		try{
			Socket socket = new Socket(central_server_address,Integer.parseInt(central_server_port));
			PrintWriter output = null;

			output = new PrintWriter(socket.getOutputStream());
			output.println(message);
			output.flush();
			output.close();
			socket.close();
		}catch(Exception e){
			System.out.println(e.toString());
		}
		return;
	}

	public String getMsg(Socket socket){
		String response = "";
		BufferedReader bf = null;
		try{
			bf = new BufferedReader(
				new InputStreamReader(socket.getInputStream()));
			response = bf.readLine();
			//bf.close();
		}catch(Exception e){
			System.out.println(e.toString());
		}

		return response;

	}

	public ArrayList<String> lookup(String file_name){
		/*1. send name to central server.
		  2. waiting response from c_server
		  3. if found: get number of peers and port/name of peer
		     if not found...
		 */

		ArrayList<String> peerAddress = new ArrayList<String>();
		String msg = "LOOKUP";

		try{
			Socket socket = new Socket(central_server_address,Integer.parseInt(central_server_port));
			//send msg to server
			PrintWriter pw = new PrintWriter(socket.getOutputStream());

			msg = msg + send_split + file_name + send_split + peerName + send_split + peer_address + send_split + String.valueOf(peerPort);
			pw.println(msg);
			pw.flush();

			//get response from server;
			//DataInputStream bf = new DataInputStream(socket.getInputStream());
			BufferedReader bf = new BufferedReader(
				new InputStreamReader(socket.getInputStream()));

			String message = bf.readLine();
			//System.out.println(message);
			String[] first = message.split("@");

			int num = Integer.parseInt(first[0]); //number of peers contain file
			if(num == 0){
				//doesn't exits.
				System.out.println("couldn't find this file in server.");
			}
			else{
				//exits
				//get peer address
				String[] second = first[1].split("/");
				for(int i=0; i<num; i++){
					//System.out.println("found target peer!");
					//String temp_address = getMsg(socket);
					//String temp_address = bf.readLine();
					String temp_address = second[i];
					//System.out.println(temp_address);
					peerAddress.add(temp_address);
					//String temp2 = temp_address.replace(mid_split, "");
					//peerAddress.add(temp2);
				}

			}
           
			bf.close();
			pw.close();
			socket.close();
            //this address contains peer name and peer port. divided by @
			return peerAddress;
			
		}catch(Exception e){
			System.out.println(e.toString());
		}
		return peerAddress;
	}


	public void download(int targetport, String peerAdd, String file_name){
		//send msg to target peer.
		//when send msg to peer, we already make sure that this peer contains the file we want.

		String msg = "DOWNLOAD";
		int bufferSize = 2048;
		byte[] buffer = new byte[bufferSize];

		//if downlosd folder doesn't exits, create it.
		//if the file doesn't exitst, create it.
		String path = dlFolder + file_name;
		//System.out.println(path);
		File folder = new File(dlFolder);
		if(!folder.exists()){
			//create this folder.
			folder.mkdir();
		}
		File fi = new File(path);
		if(!fi.exists()){
			//create text file to write the data recieved from peer.
			try{
				fi.createNewFile();
			}catch(Exception e){
				System.out.println(e.toString());
			}
		}


		
		msg = msg + send_split + file_name;

		try{
			Socket socket = new Socket(peerAdd, targetport);
			//send msg to peer
			PrintWriter pw = new PrintWriter(socket.getOutputStream());
			pw.println(msg);
			pw.flush();

			//get file from peer and save it in download folder.
			DataInputStream input = new DataInputStream(
				   new BufferedInputStream(socket.getInputStream()));

			DataOutputStream out = new DataOutputStream(
				   new BufferedOutputStream(new FileOutputStream(path)));
			//System.out.println("start downloading....");
			while(true){
				int read = 0;
				if(input != null){
					read = input.read(buffer);
				}

				if(read == -1){
					break;
				}

				out.write(buffer,0,read);
			}

			socket.close();
			input.close();
			out.close();
			pw.close();

			//System.out.println("download success!");


		}catch(Exception e){
			System.out.println(e.toString());
		}


	}


	public void del_File(String filename){
		//1. del in local folder.
		String del_path = fileFolder + filename;
		File del_file = new File(del_path);
		if(!del_file.exists()){
			System.out.println("can't find this file in local folder, choose again");
			return;
		}

		del_file.delete();

		//2. send msg to server, to delete it in server.
		String msg = "DELETE"+send_split + filename + send_split  + peerName + send_split + peer_address + send_split + peerPort;
		sendRegMsg(msg);
		/*
		try{
			Socket socket = new Socket(central_server_address,Integer.parseInt(central_server_port));
			PrintWriter pw = new PrintWriter(socket.getOutputStream());
			pw.println(msg);
			pw.flush();
			System.out.println("deleted!");

			//get response from server, if del success.
        
			DataInputStream input = new DataInputStream(
				   new BufferedInputStream(socket.getInputStream()));
			int flag = input.readInt();
			if(flag == 1){
				System.out.println("delete success");
			}
			else if(flag == 0){
				System.out.println("failed! file not found on server.");
			}
			else{
				System.out.println("failed!");
			}
            
			socket.close();
			pw.close();
			//input.close();
		}catch(Exception e){
			System.out.println(e.toString());
		}
		*/

	}


}