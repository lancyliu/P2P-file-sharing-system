import java.io.*;
import java.net.*;
import java.util.*;
public class PeerServer extends Thread{
            public ServerSocket serversocket;
            public Socket socket;
            public BufferedReader br;
            public Connection connect;
            
            public PeerServer(ServerSocket serversocket, Connection connect)throws IOException{
            	super();
            	this.serversocket = serversocket;
            	this.connect = connect;
            	start();
            }
            public void run(){
            	Socket socket = null;
            	  try{
            		  while(true){
            			  socket = serversocket.accept();
            			  handler(socket, connect);
            		  }
            	  }catch(Exception e){
            		  e.printStackTrace();
            	  }finally{
            		  try{
            			  if(socket != null){
            				  socket.close();
            			  }
            		  }catch(IOException e){
            			  e.printStackTrace();
            		  }
            	  }
            }
            public void handler(Socket socket, Connection connect)throws IOException{
            	new Thread(new Runnable() {
            		public void run(){
            			String request;
            			MessageID msgID = null;
            			int TTL;
            			String filename = null;
            			PeerAddress result = null; 
            			PrintWriter writer = null;
            			ObjectInputStream is = null;
            			ObjectOutputStream os = null;
            			boolean flag = false; // if find file in local peer, flag = true
            			try{
                			is = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                			os = new ObjectOutputStream(socket.getOutputStream());
                			writer = new PrintWriter(socket.getOutputStream());
                			Object obj = is.readObject();
                			MsgInfo setQuery = (MsgInfo)obj;
                			request = setQuery.request;
                			if(request.equals("query")){
                                //System.out.println("query from + " + setQuery.msgID.peerID.name);
                				msgID = setQuery.msgID; 
                				TTL = setQuery.TTL;
                				filename = setQuery.filename;
                				//if(TTL>0){
                					//for(int i = TTL; i>0; i-- ){
                                if(TTL >=0){
                						//connect.sendQueryMsg(setQuery);
                                        connect.handleQueryMsg(setQuery);
                    					//flag = connect.search(filename);
                    					//if(flag){
                    				//		System.out.println(filename + "is on" + PeerInfo.address.name );
                    				//		connect.sendHitQueryMessage(msgID, filename, PeerInfo.address);
                    				        
                    				//	}else{
                    						//connect.sendQueryMsg(setQuery);//it's wrong,how? 
                    				//	}
                                    }
                					//}
                					//TTL = TTL - 1;
                					
                				//}else{
                					
                				}else if(request.equals("hitquery")){
                                    //System.out.println("hitting from " + setQuery.msgID.peerID.name);
                			         connect.handleHitQueryMsg(setQuery);
                			}
                            else if(request.equals("download")){
                                String file_name = setQuery.getFileName(); //filename;
                                //read this file and send it to client.
                                String path = PeerInfo.fileFolder + file_name;
                                System.out.println(path);
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
                                socket.close();
                            }
            			}catch (IOException e){
                				e.printStackTrace();
                			}catch(Exception e){
                				e.printStackTrace();
                			}finally{
                                try {  
                                    is.close();  
                                } catch(Exception ex) {}  
                                try {  
                                    os.close();  
                                } catch(Exception ex) {}  
                                try {  
                                    socket.close();  
                                } catch(Exception ex) {}  
                            } 
                			}
                		}).start();
            		}
            	
            
}
