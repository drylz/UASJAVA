import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;


public class Server {

	public static final int PORT = 8989;
	private static HashMap<String,String> status = new HashMap<String,String>();
	private static HashMap<String,String> client_or_server = new HashMap<String,String>();
	private static HashMap<String,Socket> socket = new HashMap<String,Socket>();
	private static HashMap<String,String> client = new HashMap<String,String>();
	private static HashMap<String,PrintWriter> pw = new HashMap<String,PrintWriter>();
	
	public static void main(String[]args)throws Exception {
		System.out.println("Customer Service chat server is running.");
		String []arrayIPcust = {"192.168.43.109","192.168.43.247" };
		
        ServerSocket listener = new ServerSocket(PORT);
        try {
        	
        	Socket custServ  = null;
        	Socket client = null;
        	
            while (true) {
            	System.out.println("run");
            	Socket temp = listener.accept();
            	String temptemp = temp.getInetAddress().toString();
            	String temptemptemp = temptemp.replace("/", "" );
            	boolean bool = false;
            	for(String statuss : arrayIPcust ){
            		
            		if(statuss.equals(temptemptemp)){
            			bool = true;
            			break;
//                    	status.put(clientIP,"connected");
            		}
            		
            	}
            	if(bool == true){
            		status.put(temptemptemp,"connected");
        			custServ = temp;
        			System.out.println("Customer Service connected with ip : " + temptemptemp);
        			
        			
            	}
            	
            	else if(bool == false){
	            	status.put(temptemptemp,"connected");
	    			client = temp;
	    			System.out.println("Client connected with ip : " + temptemptemp);
	    			
            	}
            	
            	
            	if(custServ != null && client != null){
//            		custServ.notify();
//            		client.notify();
            		 new Handler(client,custServ).start();
            		custServ = null;
            		System.out.println(custServ);
            		client = null;
            		System.out.println(client);
            	}
                
            }
        }catch(IllegalMonitorStateException e){
        	e.printStackTrace();
        }
        finally {
            listener.close();
        }

	}
	
	
	private static class Handler extends Thread {
		private String name, clientIP,custServIP;
        private Socket socketClient, socketCustServ;
        private BufferedReader inClient,inCustServ;
        private PrintWriter outClient, outCustServ;
        
        
        
        public Handler(Socket socketClient , Socket socketCustServ) {
            this.socketClient = socketClient;
            this.socketCustServ = socketCustServ;
            clientIP = socketClient.getRemoteSocketAddress().toString();
            custServIP = socketCustServ.getRemoteSocketAddress().toString();
            
        }
        
        public void run() {
            try {

            	String CustServname="";
                inClient = new BufferedReader(new InputStreamReader(
                		socketClient.getInputStream()));
                outClient = new PrintWriter(socketClient.getOutputStream()
                		, true);
                inCustServ = new BufferedReader(new InputStreamReader(
                        socketCustServ.getInputStream()));
                outCustServ = new PrintWriter(socketCustServ.getOutputStream()
                   		, true);
                	
                while (true) { 
                	
                	while(CustServname.equals("")){
                		CustServname = login(outCustServ,inCustServ);
                	}
                    outClient.println("SUBMITNAME");
                    name = inClient.readLine();
                    if (name == null) {
                    	outClient.println("Nama sudah terpakai.");
                        return;
                    }
                    synchronized (status) {
                        if (!status.containsValue((name))) {
                            client.put(clientIP, name);
                            break;
                        }
                    }
                }

                
                outClient.println("NAMEACCEPTED");
                outCustServ.println("Name : " + name);
                pw.put(custServIP,outCustServ);
                pw.put(clientIP,outClient);
                

               
                while (!socketClient.isClosed() && !socketCustServ.isClosed()) {
                	try{
                		Thread t = new listener(socketClient,socketCustServ,"Client : " + name,outCustServ);
                		Thread t1 = new listener(socketCustServ,socketClient, "Customer Service : " +CustServname, outClient);
                		t.start();
                		t1.start();
                		

                	}catch(Exception e){
                		e.printStackTrace();
                		
                	}if(socketClient.isClosed() || socketCustServ.isClosed()){
                		break;
                	}
                }
            } catch (IOException e) {
                System.out.println(e);
            } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
               
                if (name != null) {
                    status.remove(clientIP, "connected");
                    System.out.println("Status " + clientIP + " : REMOVED!!" );
                    
                    status.remove(custServIP, "connected");
                    System.out.println("Status " + custServIP + " : REMOVED!!" );
                }
                if (outClient != null) {
                	for(String ipClient : pw.keySet()){
                		if(ipClient.equals(clientIP)){
                			outClient.println("Disconnected from Server");
                		}
                	}
                	for(String ipCustServ : pw.keySet()){
                		if(ipCustServ.equals(custServIP)){
                			outCustServ.println("Disconnected from " + name);
                		}
                	}
                	pw.remove(custServIP, outCustServ);
                    pw.remove(clientIP,outClient);
                }
                try {
                    socketClient.close();
                    socketCustServ.close();
                } catch (IOException e) {
                }
            }
        }
    }
	
	public static String login(PrintWriter outCustServ, BufferedReader inCustServ) throws Exception, IOException{
		String hasil = null;
		try{
			Database db = new Database();
			db.getConn();
			
			outCustServ.println("LOGIN");
		    String username = inCustServ.readLine();
		   	outCustServ.println("USERNAMEACCEPTED");
		   	String password  = inCustServ.readLine();
		    	
		   	hasil = db.login(username, password);
		    	
		   	if(hasil.equals("NULL")){
				outCustServ.println("COBALAGI"); 
				db.close();
				login(outCustServ,inCustServ);
		}
			if(!hasil.equals("NULL"))
				db.close();
				return hasil;
				
			}
		catch(Exception e){
			e.printStackTrace();
		}
		return hasil;
	}
	
	private static class listener extends Thread{
		Socket socketClient, socketCustServ;
		PrintWriter pw;
		String name;
		listener(Socket socket,Socket socket1, String name,PrintWriter pw){
			socketClient = socket;
			this.name = name;
			this.pw = pw;
		}
		
		@Override
		public void run(){
			while(true){
				try{
				InputStream inputStreamClient = socketClient.getInputStream();
//    			InputStream inputStreamCust = socketCustServ.getInputStream();
    			
    			BufferedReader buffrClient = new BufferedReader(new InputStreamReader(inputStreamClient));
//    			BufferedReader buffrCust = new BufferedReader(new InputStreamReader(inputStreamCust));
    			
//    			PrintWriter pw = 
    			
    			String lineClient = buffrClient.readLine();
    			
//    			String lineCust = buffrCust.readLine();
    			
    			if(!lineClient.equals("@DC") || lineClient !=null){
    				
    				pw.println("From " + name + " : " + lineClient);
    			}if(lineClient.equals("@DC")){
    				interrupt();
    				System.out.println("thread interrupted");
    				break;
    			}
    			
//    			if(lineCust !=null){
//    				outClient.println("From Customer Service : " + lineCust);
//    			}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
			
		}
	}
	





	
}
