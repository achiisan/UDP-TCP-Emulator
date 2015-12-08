import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Random;
import java.io.*;
public class TCPServer {

    private DatagramSocket datagramSocket;
    private DatagramPacket packet; 
    TCPHeader header;
    LogViewer logs = new LogViewer();
	public static void main(String[] args) throws IOException{
		
		
		TCPServer server = new TCPServer();
		
		server.waitConn();
		server.SendData();
		server.fourwayhs();
    }
    
 
    public TCPServer() throws IOException {
    datagramSocket = new DatagramSocket(10224);
    header = new TCPHeader();
    byte[] buffer = new byte[100];
    packet = new DatagramPacket(buffer, buffer.length);

  //  Thread t = new Thread(this);
  //  t.start();
    
    header.synnum = 0;
    header.acknum = 0;
    header.flags[0] = 0;
    header.flags[1] = 0;
    header.flags[2] = 0;
 
    
}  
    //3-way handshake
    public void waitConn() {
    	byte[] buffer = new byte[100];
        logs.SetString("WAITING FOR CONNECTION..."+"\n");
    	   logs.SetString("START 3-WAY HANDSHAKE"+"\n");
    	   header.window_size = 2;
    	   
    	        try {
    	        	//RECEIEVE SYN FROM CLIENT
					datagramSocket.receive(packet);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	        buffer = packet.getData();
    	      
    	        
    	        String data = new String(buffer);
    	        logs.SetString("RECEIVED DATA FROM CLIENT"+"\n");
    	        logs.SetString(data+"\n");
    	        String[] datacoll = data.split("#");
    	        
    	        //get the acknowledgement number and set the acknowledgment to 1
    	        header.acknum = Integer.parseInt(datacoll[0])+1;
    	        header.flags[1] = 1;
    	        
    	        //set a syn request to the client
    	        Random rand = new Random();
    	        header.synnum = rand.nextInt(1000);
    	        header.flags[0] = 1;
    	        
    	        String datatopass= header.synnum+"#"+header.acknum+"#"+header.flags[0]+"#"+header.flags[1]+"#"+header.flags[2]+"#"+header.window_size;
    	        buffer = datatopass.getBytes();
    	        packet.setData(buffer);
    	        try {
    	        	//SEND SYN + ACK
    	        	logs.SetString("SEND DATA TO CLIENT:"+datatopass+"\n");
    	        	
					datagramSocket.send(packet);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
    	           	        
    	        try {
    	        	//WAIT FOR ACK FROM CLIENT
					datagramSocket.receive(packet);
					logs.SetString(new String(packet.getData())+"\n");
					logs.SetString("==============================================\nCONNECTION ESTABLISHED"+"\n");
					
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}    
    	        
    	}
    
  
    public void fourwayhs(){
    		//send fin flag to client
    	logs.SetString("FOUR WAY HANDSHAKE.."+"\n");
    		header.acknum = 0;
    		header.flags[0] = 0; //syn flag
    		header.flags[1] = 0; //ack flag
    		header.flags[2] = 1; //fin flag
    	
    		byte[] buffer = new byte[1024];
    	   String datatopass= header.synnum+"#"+header.acknum+"#"+header.flags[0]+"#"+header.flags[1]+"#"+header.flags[2]+"#"+header.window_size;
	        buffer = datatopass.getBytes();
	        packet.setData(buffer);
	        logs.SetString("SENDING = "+datatopass+"\n");
	        try {
				datagramSocket.send(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        try {
	        	//RECEIEVE SYN FROM CLIENT
				datagramSocket.receive(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        buffer = packet.getData();
	      
	        String data = new String(buffer);
	        logs.SetString("RECEIVED = "+data+"\n");
	        String [] args = data.split("#");
	    	//send fin flag to client
    		header.acknum = Integer.parseInt(args[0])+1;
    		header.flags[0] = 0; //syn flag
    		header.flags[1] = 1; //ack flag
    		header.flags[2] = 0; //fin flag
    	
    		 buffer = new byte[1024];
    	    datatopass= header.synnum+"#"+header.acknum+"#"+header.flags[0]+"#"+header.flags[1]+"#"+header.flags[2]+"#"+header.window_size;
	        buffer = datatopass.getBytes();
	        packet.setData(buffer);
	        logs.SetString("SENDING = "+datatopass+"\n");
	        try {
				datagramSocket.send(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        logs.SetString("================================================\nSERVER CONNECTION CLOSED."+"\n");
	        
	        
    }
    public void SendData() {
    	Random rand = new Random();
    	int dropconn = 10; //dropping percentage
    	//window sliding mechanism
    	 String toSend = "Michael Bernard Bryan D. Doron";
    	 
    	 byte[] b = toSend.getBytes();
    	 int length = b.length;
    	 int inter = length / header.window_size;
    	 int butal = length % header.window_size;
    	 if(butal != 0)
    		 inter++;
    	 
    	 
    	 for(int i =0; i<inter; i++){
    		
    		 for(int j=0; j<header.window_size; j++){
    		 	try {
    		 	 	byte []tempb = new byte[1];
        		 	tempb[0] = b[(i*header.window_size)+j];
        		 	header.synnum = ((i*header.window_size)+j); //update syn number per send of byte of data
        		 	//send stream number & data
        		 	String tosend = ((i*header.window_size)+j)+"#"+new String(tempb);
        		 	tempb = tosend.getBytes();
        		 	
        		 	logs.SetString("SENDING = "+ new String(tempb)+"\n");
        		 	packet.setData(tempb);
        		 	
        		 	if(rand.nextInt(100) <= dropconn){
        		 		logs.SetString("DROPPED PACKET.."+"\n");
        		 	}else{
        		 		datagramSocket.send(packet);
        		 	}
        		 	try{
        		 		Thread.sleep(2000);
        		 	}catch(Exception e){}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logs.SetString("FINISHED GETTING FILES"+"\n");
					break;
				}
    		 }
    		 
    		 for(int j = 0; j<header.window_size; j++){
    			 try {
    				 byte[] k = new byte[1000];
    				DatagramPacket p = new DatagramPacket(k, k.length);
    				datagramSocket.setSoTimeout(10000);
					datagramSocket.receive(p);
					logs.SetString("RECEIVED ACK = "+new String(p.getData())+"\n");
					
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logs.SetString("TIMEOUT REACHED. RESENDING WINDOW..."+"\n");
					i--; //decrement the window size to resend again
					
					break;
					
				}
    		 }
    	 }
    	 
    	 
    	 
    }
}