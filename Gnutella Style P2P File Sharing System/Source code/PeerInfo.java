/* this file is data structure used in the program
   PeerInfo class store all the information about one peer
*/

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.io.Serializable;


// store all the information about a peer
//this class to not need to serializable, also the variable in it should be static
// so that all the variable is shared in the whole project

public class PeerInfo{

	//files of this peer want to regiter is in peername+fileFolder
	//files of this peer download from internet is in peername+dlFolder
	//config file in the same path and name is config.
	public static String fileFolder = new String(); //= "_files";
	public static String dlFolder = new String();  //"_download";
	//public static String config = "config.txt";
	public static String config = "config.properties";
	public static int TTL = 2;
	public static int numMessage; //= 0;  //number of message recieved.
	//message assosative table
	public static ConcurrentHashMap<Integer, MessageID> msgTable = new ConcurrentHashMap<Integer, MessageID>();
	//address of this peer
	public static PeerAddress address = new PeerAddress();

	public static ArrayList<PeerAddress> result = new ArrayList<PeerAddress>(); //save the search result of one file.
    //neighbors of this peer
    public static ArrayList<PeerAddress> neighbors = new ArrayList<PeerAddress>();
    //files thie peer want to share
    public static ArrayList<String> files = new ArrayList<String>();

    //thread list
    public static ArrayList<Thread> threadList = new ArrayList<Thread>();
    //this array store the time when found one result. when testing the avg time needed, we can use the value
    // stored in this list, 
    public static ArrayList<Long> time = new ArrayList<Long>();
}
