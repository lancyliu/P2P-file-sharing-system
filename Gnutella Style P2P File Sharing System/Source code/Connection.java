import java.io.*;
import java.net.*;
import java.util.*;

/*
To do list:
1. set up set/get functions in data structures used in this program.
*/

public class Connection{
	/*
	 functions needed around the connections between peers
     functions needed for query and hitquery.
	*/

     /*
 	 *  search function
 	 *  search file on the local peer
 	 */
 	public boolean search(String filename){
 		System.out.println("searching");
 		if(PeerInfo.files.size()!=0){
 			for(int i = 0; i < PeerInfo.files.size(); i++){
 				if(filename.equals(PeerInfo.files.get(i))){
 					return true;
 				}
 			}
 		}
 		return false;	
 	}

     /*this thread is used to send message to the target peer*/
     public class sendMsg implements Runnable{
     	public MsgInfo message;
     	public String targetIP;
     	public int targetPort;

     	public sendMsg(MsgInfo message, PeerAddress address){
     		this.message = message;
     		this.targetPort = Integer.parseInt(address.port);
     		this.targetIP = address.IP;
     	}

     	public void run(){
     		ObjectOutputStream os = null;
     		Socket socket = null;

     		try{
     			socket = new Socket(targetIP, targetPort);
			    //send msg to peer 
			    os = new ObjectOutputStream(socket.getOutputStream());
				os.writeObject(message);
				os.flush();
     		}catch(Exception e){
     			System.out.println(e.toString());
     		}
     	}
     }

     public void sendHitQueryMessage(MessageID msgId, String filename, PeerAddress result){
     	//start the thread of send hitquery message thread.  in this thread
     	// we package a message which contains all info needed as a hitquery message
     	//     then, call sendMsg to send message to the target peer. this peer's info stored in msgId.
     	sendHitQueryThread hquery = new sendHitQueryThread(msgId, filename,result);
     	Thread shq = new Thread(hquery);
     	PeerInfo.threadList.add(shq);////////////////////////////////////////////////////////
     	shq.start();
     	shq = null;
     }

     public class sendHitQueryThread implements Runnable{
     	//add message information needed when you want to send one hitQuery message
     	public MsgInfo message = new MsgInfo();
     	public MessageID msgId;
     	public String filename;
     	PeerAddress result;
     	//public String tartgetname;
     	//public String targetIP;
     	//public String targetPort;

     	public sendHitQueryThread(MessageID msgId, String filename, PeerAddress result){
     		this.msgId = msgId;
     		this.filename = filename;
     		this.result = result;
     		/*
     		this.tartgetname = tartgetname;
     		this.targetIP = targetIP;
     		this.targetPort = targetPort
     		*/
     	}

     	public void run(){
     		//acturally the messageID stored in the new message.msgId seems doesn't matter. cause we only use sequence number to
     		// search the next peer's info, when hitquery doen't matter which peer send me this message.
     		//Also hitquery does not consider ttl
     		//message.request = "hitquery";
     		//PeerAddress add = new PeerAddress(tartgetname, targetPort, targetIP);
     		//store which 
     		//message.setHitQuery
     		//message.result = result;
     		PeerAddress reciever = msgId.peerID;
     		MessageID newMid = new MessageID(msgId.seqNum, PeerInfo.address);
     		//message.msgID = newMid;
     		//message.filename = filename;
     		message.setHitQuery(newMid, filename, result);

     		//finished adding message.
     		//send this message
     		sendMsg send = new sendMsg(message, reciever);
     		Thread thread = new Thread(send);
     		thread.start();
     		thread = null;
     	}
     }

     public void handleHitQueryMsg(MsgInfo message){
     	handleHitQueryMsgThread ht = new handleHitQueryMsgThread(message);
     	Thread thread = new Thread(ht);
     	thread.start();
     	thread = null;
     }

     public class handleHitQueryMsgThread implements Runnable{
     	// while recieve a hitquery message, how we handle it
     	//1. if current peer is the peer who send the request. then stop hitquery, save the result. search file done.
     	//2. if current peer is not the peer who send the request. we need send this hit query message to the right place.
     	public MsgInfo message;
     	public handleHitQueryMsgThread(MsgInfo message){
     		this.message = message;
     	}
     	public void run(){
     	MessageID msgID = message.msgID;
     	int index = msgID.seqNum;
     	MessageID newMsg = null;//= new MessageID();
     	if(PeerInfo.msgTable.containsKey(index)){
     		// this peer have send this message. read the message table.
     		newMsg = PeerInfo.msgTable.get(index);
     	}

     	if(newMsg != null){
     		if(newMsg.peerID == PeerInfo.address){
     			// conditon 1. stop hit query. save result to result list.
     			// Once recieve one result, print the current time., used in the test.
     			long curTime = System.currentTimeMillis();
     			//if this result not in result array, add it.
     			boolean flag = true;
     			for(int x=0; x<PeerInfo.result.size(); x++){
     				//System.out.println("current result is : " + PeerInfo.result.get(x).getName());
     				if(message.result.getName().equals( PeerInfo.result.get(x).getName())){
     					flag = false;
     					//break;
     				}
     			}
     			if(flag){
     				//System.out.println("found 1 result, time is: " + curTime);
     			    PeerInfo.result.add(message.result);
     			    PeerInfo.time.add(curTime);
     			}
     			
     		}
     		else{
     			//newMsg.msgID.peerID = PeerInfo.address;
     			sendHitQueryMessage(newMsg, message.filename, message.result);
     		}
     	}
     	return;
     }
     }

     public void handleQueryMsg(MsgInfo message){
     	// if peer contains the file requested, send hitquery mesg.
     	// then send query message to all its neighbors.
     	String filename = message.getFileName();
     	int Ttl = message.getTTL();
     	if(Ttl <0) return;
     	for(int i=0; i<PeerInfo.files.size(); i++){
     		if(PeerInfo.files.get(i).equals(filename)){
     			sendHitQueryMessage(message.msgID, filename, PeerInfo.address);
     			break;
     		}
     	}

     	//braodcast.
     	sendQueryMsg(message);
     	//this message is send from upstream
     	//PeerAddress upstream = message.getMsgID().getPeer();
     }

     public void sendQueryMsg(MsgInfo message){

     	//update message to a new message and send it to the neighbors.
     	sendQueryMsgThread query = new sendQueryMsgThread(message);
     	Thread q = new Thread(query);
     	//PeerInfo.threadList.add(q);////////////////////////////////////////////////////////////////////////////
     	q.start();
     	q = null;
     }

     public class sendQueryMsgThread implements Runnable{
     	public MsgInfo oldMsg;

     	public sendQueryMsgThread(MsgInfo message){
     		this.oldMsg = message;
     	}

     	public void run(){
     		MessageID newMsgID = null;
     		MsgInfo newMsg = new MsgInfo();
     		int TTL = oldMsg.getTTL() -1;
     		if(TTL >=0){
     		//this message is send from upstream. use upstream to see whether we should send message to one neighbor.
     		PeerAddress upstream = oldMsg.getMsgID().getPeer();
     		//System.out.println("upstream is: " + upstream.getName());
     		//System.out.println(PeerInfo.neighbors.size());
     		for(int i=0; i<PeerInfo.neighbors.size(); i++){
     			if(upstream.getName().equals(PeerInfo.neighbors.get(i).getName()) &&
     				upstream.getIP().equals(PeerInfo.neighbors.get(i).getIP())&&
     				upstream.getPort().equals(PeerInfo.neighbors.get(i).getPort())){
     				//do nothing, cause this message is send from that neighbors, do not send it back.
     			}
     		    else{
     		    	PeerInfo.numMessage += 1;
     		    	// we want send new message to the neighbors. thus the message should be: from this peer.
     		    	newMsgID = new MessageID(PeerInfo.numMessage, PeerInfo.address);
     		    	newMsg.setQuery(newMsgID, oldMsg.getFileName());
     		    	newMsg.setTTL(TTL);
     		    	//also we should add old msg to the message table.
     		    	PeerInfo.msgTable.put(PeerInfo.numMessage, oldMsg.getMsgID());
     		    	//send this message
     		    	PeerAddress reciever = PeerInfo.neighbors.get(i);
     		    	//System.out.println("reciever is :" + reciever.name);
     		        sendMsg send = new sendMsg(newMsg, reciever);
     		        Thread thread = new Thread(send);
     		        thread.start();
     		        thread = null;
     		    }
     		}
     	}

     	} 
     }




}