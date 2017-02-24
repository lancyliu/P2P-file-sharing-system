import java.io.*;
import java.net.*;
import java.util.*;

//

public class Test1{
	static String central_server_address = "localhost";
	static String central_server_port = "20000";
	static String peerAdd = "localhost";
	static String peerPort = "6678";

	static int Test_Num = 1000;
	static int Thread_Num = 5;

	

	public static void main(String[] args){
		//run central server
		//run peerServer.
		String file_name = "test1.txt";

        /***
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
		***/
		//test look up time. set up the file's name, cal the time cost by look up.
		
		System.out.println("At first, test the look up function.");
		for(int i=0; i < Thread_Num; i++){
			new lookUpTest(file_name).start();
		}
		
		System.out.println("test the download function");
		for(int j=0; j< Thread_Num; j++){
			new downLoadTest(Integer.parseInt(peerPort), peerAdd, file_name).start();
		}

		//new lookUpTest(file_name).start();
		//new downLoadTest(peerPort,peerAdd,file_name).start();
		//use this two to test download. at first, we should run the peerServer.
		//peerClient test = new peerClient("B",6678);
		//test.download(6677,"localhost","t1");
	}

private  static class lookUpTest extends Thread{
		private String file_name;

		public lookUpTest(String file_name){
			this.file_name = file_name;
		}

		public void run(){

			try{
				peerClient testClient = new peerClient("test", 8899);
				long startTime = System.currentTimeMillis();
				for(int i=0; i< Test_Num; i++){
					testClient.lookup(file_name);
				}
				long endTime = System.currentTimeMillis();
				long totalTime = endTime - startTime;
				System.out.println("Total time cost by this lookup thread is :" + totalTime);
			}catch(Exception e){
				System.out.println(e.toString());
			}
		}
	}


private	static class downLoadTest extends Thread{
		private int port;
		private String address;
		private String file;

		public downLoadTest(int port, String peerAdd, String file_name){
			this.port = port;
			this.address = peerAdd;
			this.file = file_name;
		}

		public void run(){

			try{
			   peerClient testClient = new peerClient("test2", 8898);
			   long startTime = System.currentTimeMillis();
			   for(int i=0; i < Test_Num; i++){
				   testClient.download(port,address,file);
			    }
			   long endTime = System.currentTimeMillis();
			   long totalTime = endTime - startTime;
			   System.out.println("Total time costs by this download Thread is: " + totalTime + "ms");
			}catch(Exception e){
				System.out.println(e.toString());
			}
		}
	}


}