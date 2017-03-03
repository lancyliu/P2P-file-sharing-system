/*
Edit by Xin Liu
@Feb 26 2017
*/

import java.io.*;
import java.net.*;
import java.util.*;

/*
Function List:
1. used for lookup files in the network: this part may also used by peer server, then we set up a new class to store this function
   query: broadcast the request to its neighbor:  how to modify the new msg, how to store the old msg, which neighbor will recieve the broadcast
   sendMsg: send message to other peer: do not care the content of message, just a function to send msg. need socket and target address
pass 2. used for download:
   sendMsg: send message to target peer to ask the needed file.
   recieve file: same as assignment 1.
*/

public class PeerClient extends Thread{
	Connection connect;
	public PeerClient(Connection connect){
		this.connect = connect;
	}
	public void run(){
		//register all files
		//user choose to lookup a file or download a file or delete one file or exit.
		register();
		int flag = 0;
		if(PeerInfo.msgTable.size() >2000){
			PeerInfo.msgTable.clear();
		}
		while(true){
			BufferedReader input = null;
			try{
				input = new BufferedReader(new InputStreamReader(System.in));
				System.out.println("\nPlease choose from the following: ");
				System.out.println("1.lookup a file \n2. download a file \n3. unregister a file \n4. run test case");
				flag = Integer.parseInt(input.readLine());
				switch(flag){
					case 1:
					System.out.println("please input the file name you want to lookup");
					String name = input.readLine();
					System.out.println("Gonna search the file "+ name);
					//clear the result list befor searching
					if(PeerInfo.result != null && !PeerInfo.result.isEmpty()) PeerInfo.result.clear();
					//look up this file in the network. store the result in PeerInfo.result;
//////////////////////////////////////////////////////////
					MessageID mid = new MessageID(PeerInfo.numMessage+1, PeerInfo.address);
					MsgInfo msg = new MsgInfo();
					msg.setQuery(mid, name);
					msg.setTTL(PeerInfo.TTL);
					connect.sendQueryMsg(msg);
					//wait for sometime
					long startTime = System.currentTimeMillis();
					long wait = 200;
					long endtime = startTime + wait;
					long current = System.currentTimeMillis();
					while(current < endtime){
						current = System.currentTimeMillis();
					}
//////////////////////////////////////////////////////////
					//print the result.
					//System.out.println("test point1");
					if(PeerInfo.result.size() > 0){
						System.out.println("Found this file, the result is following:");
						int i=1;
						for(PeerAddress peer : PeerInfo.result){
							System.out.println(i + " : " + "name is :" + peer.name + " , IP address is : "+ peer.IP +", Port number is :"+ peer.port);
							i++;
						}
					}
					else{
						System.out.println("Cann't found this file in the network!");
					}
					break;

					case 2:
					System.out.println("please input the file name you want to download");
					String name2 = input.readLine();
					PeerInfo.result.clear();
					//look up this file in the network. store the result in PeerInfo.result;
//////////////////////////////////////////////////////////
					MessageID mid2 = new MessageID(PeerInfo.numMessage+1, PeerInfo.address);
					MsgInfo msg2 = new MsgInfo();
					msg2.setQuery(mid2, name2);
					msg2.setTTL(PeerInfo.TTL);
					connect.sendQueryMsg(msg2);
					//lookup(name);
					//wait for sometime
					//wait for sometime
					long start = System.currentTimeMillis();
					long wai = 1200;
					long end = start + wai;
					long cur = System.currentTimeMillis();
					while(cur < end){
						cur = System.currentTimeMillis();
					}
//////////////////////////////////////////////////////////
					//print the result.
					//System.out.println("test point1");
					if(PeerInfo.result.size() > 0){
						System.out.println("Found this file in the following peer, choose one you want to download(enter the index):");
						for(int i=1; i<= PeerInfo.result.size(); i++){
							PeerAddress peer = PeerInfo.result.get(i-1);
							System.out.println(i + " : " + "name is :" + peer.name + " , IP address is : "+ peer.IP +", Port number is :"+ peer.port);
						}
						int ch = Integer.parseInt(input.readLine());
					    if(ch > PeerInfo.result.size()){
						  System.out.println("Input illeagle, please type again");
					    }
					   else{
						 //start a download thread
						  DLThread getFile = new DLThread(name2, PeerInfo.result.get(ch-1));
						  Thread temp = new Thread(getFile);
						  temp.start();
						  System.out.println("download success. file stored in download folder!");
					    }
					}
					else{
						System.out.println("can not found this file in the network, please input again");
					}

					
					break;

					case 3:
					System.out.println("Input the file name you want to unregister");
					String name3 = input.readLine();
					System.out.println("do you want to delete the local file as well? if yes, please input 1, if no, please input 0");
					String d = input.readLine();
					if(d.equals("1")){
						delFile(name3,1);
					}
					else delFile(name3,0);
					break;
					case 4:
					new TestClient(connect).start();
					break;
					case 5:
					break;		
				}
			}catch(Exception e){
				System.out.println(e.toString());
			}


		}
	}


	/*
	register all files in fileFolder. Because this system do not has central server, just store the file's name in an arrayList.
	*/
	public void register(){
		File files = new File(PeerInfo.fileFolder);
		File[] filelist = files.listFiles();
	    ArrayList<String> fileName = new ArrayList<String>();
		
		if(filelist == null){
			//still registering ? ?
			System.out.println("there are no file in this peer.");
		}
		else{
		    for(File f : filelist){
		    	PeerInfo.files.add(f.getName());
		    }
		}
	}

	public void delFile(String filename, int local){
		//delete one files, if local ==0 do not delete it in local folder
		// if local == 1 delete it in local folder.

		//remove it from the register list
		if(PeerInfo.files.contains(filename)){
			PeerInfo.files.remove(filename);
			System.out.println("File :" + filename + " is unregistered!");
		}
		else{
			System.out.println("File :" + filename + " is not registered file, please choose again.");
		}

		if(local == 1){
			String del_path = PeerInfo.fileFolder + filename;
		    File del_file = new File(del_path);
		    if(!del_file.exists()){
			  System.out.println("can't find this file in local folder, choose again");
			   return;
			}
			del_file.delete();
			System.out.println("Delete success!");
			
		}

		return;
	}

	public class DLThread implements Runnable{
		
		public PeerAddress target;
		public String name;

		public DLThread(String name, PeerAddress target){
			this.name = name;
			this.target = target;
		}

		public void run(){
			//1. send message to peer
			MsgInfo message = new MsgInfo();
			message.request = "download";
			message.filename = name;
			//message.msgID.peerID.name = target.name;
			//message.msgID.peerID.IP = target.IP;
			//message.msgID.peerID.port = target.port;

			int bufferSize = 2048;
		    byte[] buffer = new byte[bufferSize];

		    //if download folder doesn't exits, create it.
		    //if the file doesn't exitst, create it.
		    String path = PeerInfo.dlFolder + name;
		    //System.out.println(path);
		    File folder = new File(PeerInfo.dlFolder);
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

		    ObjectOutputStream os = null;
		    try{
		    	Socket socket = new Socket(target.IP, Integer.parseInt(target.port));
			    //send msg to peer 
			    os = new ObjectOutputStream(socket.getOutputStream());
				os.writeObject(message);
				os.flush();

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
			   os.close();
		    }catch(Exception e){
		    	System.out.println(e.toString());
		    }
	    }
	}


	public static class TestClient extends Thread{
		Connection connect;
		public TestClient(Connection connect){
			this.connect = connect;
		}
		public void run(){
			String testfile = "test1.txt";
				//start the server part
				
				//read one file 200 times.
				
				long start = System.currentTimeMillis();
				long average = 0;
				for(int i=0; i<200; i++){
					long startTime = System.currentTimeMillis();
					PeerInfo.result.clear();
					PeerInfo.time.clear();
					//PeerInfo.threadList().clear();
					MessageID mid = new MessageID(PeerInfo.numMessage+1, PeerInfo.address);
					MsgInfo msg = new MsgInfo();
					msg.setQuery(mid, testfile);
					msg.setTTL(PeerInfo.TTL);
					msg.setTTL(PeerInfo.TTL);
					connect.sendQueryMsg(msg);
					long st = System.currentTimeMillis();
					long wait = 200;
					long et = st + wait;
					long current = System.currentTimeMillis();
					while(current < et){
						current = System.currentTimeMillis();
					}
				    long total = 0;
				    for(int j=0; j<PeerInfo.time.size(); j++){
				    	total += (PeerInfo.time.get(j) - startTime);
				    }
				    if(PeerInfo.time.size() > 0){
				         long avg = total/PeerInfo.time.size();
				         average += avg;
				         //System.out.println("Max time cost by one search is:" + max);
				         //System.out.println("Min time cost by one search is:" + min);
				         //System.out.println("average time cost by one search is:" + avg);
				     }
				     else{
				     	//System.out.println("not found!");
				     }
				}
				
				//long outEnd = System.currentTimeMillis();
				//long totalTime = outEnd - outStart;
				//read the time list

				System.out.println("\nTotal Time cost by searching one file 200 times costs: " + average + "ms");
				float a = average/200;
				//System.out.printf("%.2f", a);
				System.out.println("\nAverage time cost by searching 200 times is: " + a + "ms");
				return;
		}
	}



}