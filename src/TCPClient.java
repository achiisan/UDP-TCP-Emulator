import java.net.*;
import java.io.*;
import java.util.Random;
import java.util.Scanner;

public class TCPClient{
    private Scanner scan;
	private DatagramSocket datagramSocket;
	private TCPHeader header;
	InetAddress receiverAddress;
	 DatagramPacket packet; 
	 LogViewer logs = new LogViewer();
	public static void main(String[] args) throws IOException{
		
       TCPClient client =  new TCPClient();
   
       
       //3-way handshake
       client.initiateHandShake();
       client.sendData();
       client.fourwayhs();
       
    }
    public TCPClient() throws IOException{
        datagramSocket = new DatagramSocket();
        byte[] buffer = new byte[100];
        
         header = new TCPHeader();
      receiverAddress =  InetAddress.getLocalHost();
     packet  = new DatagramPacket(
                buffer, buffer.length, receiverAddress, 10224);
    }

    void initiateHandShake() {
    	//first handshake
    	Random rand = new Random();
    	
    	header.synnum = rand.nextInt(1000);
    	header.acknum = 0;
    	header.flags[0] = 1;
    	header.flags[1] = 0;
    	header.flags[2] = 0;
    	
    	//SYN ONLY
    	String data = header.synnum+"#"+header.acknum+"#"+header.flags[0]+"#"+header.flags[1]+"#"+header.flags[2];
    	byte[] data2 = data.getBytes();
    	packet.setData(data2);
    	logs.SetString("SENDING = "+data+"\n");
    	
    	try {
			datagramSocket.send(packet);
			//RECEIVE SYN + ACK FROM SERVER
			datagramSocket.receive(packet);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	data2 = packet.getData();
    	 logs.SetString("RECEIVED"+new String(data2)+"\n");
    	
    	data = new String(data2);
    	String[] datacoll = data.split("#");
    	
    	header.acknum = Integer.parseInt(datacoll[0]) + 1;
    	header.flags[1] = 1;
    	header.flags[0] = 0;
    	header.flags[2] = 0;
    	header.window_size = 2;
    	
    	 data = header.synnum+"#"+header.acknum+"#"+header.flags[0]+"#"+header.flags[1]+"#"+header.flags[2];
    	 packet.setData(data.getBytes());	
    	 logs.SetString("SENDING = "+data+"\n");
    	 try {
    			datagramSocket.send(packet);
    			//SEND ACK TO SERVER
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	 
    	 logs.SetString("====================\nCONNECTION ESTABLISHED"+"\n");
    }
    
    public void sendData() {
    	logs.SetString("SEND DATA"+"\n");
    	int droppc = 10; //dropping percentage
    	
    	byte[] buf = new byte[1000];
    	DatagramPacket p = new DatagramPacket(buf, buf.length, receiverAddress, 10224);
    	byte[] msg = new byte[1000];
    	int i = 0;
    	while(true){
    	try {
			datagramSocket.setSoTimeout(20000); //if this timeouts, this means that the server will not send any more data.
    		datagramSocket.receive(p);
			logs.SetString("RECEIVED FROM SERVER = "+new String(p.getData())+"\n");
			String ans = new String(p.getData());
			String []tok = ans.split("#");
			
			int slideno = Integer.parseInt(tok[0]);
			
			msg[slideno] = tok[1].getBytes()[0];
			
			i = slideno + 1;
			
			if(i % header.window_size == 0 || i == header.window_size){
				int j = i - header.window_size;
				
				for(; j<i; j++){
					String response = "0#"+j+"#0#1#0#"+header.window_size;
					
					byte[] buffr= response.getBytes();
					logs.SetString("SENDING RESPONSE..."+response+"\n");
					
					
					Random rand = new Random();
					int rn = rand.nextInt(100);
					
					if(rn <= droppc){
						logs.SetString("DROPPED PACKET..."+"\n");
					}else{
						packet.setData(buffr);
						datagramSocket.send(packet);
					}
					
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logs.SetString("============================================="+"\n");
			logs.SetString("MESSAGE RECEIVED "+ new String(msg)+"\n");
			break;
		}
    	}
    		
    	
    }
    
    public void fourwayhs(){
    	logs.SetString("FOUR WAY HANDSHAKE"+"\n");
        try {
        	
        	//RECEIEVE SYN FROM CLIENT
			datagramSocket.receive(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        byte[] buffer = new byte[1024];
        buffer = packet.getData();
      
        String data = new String(buffer);
        logs.SetString("RECEIVED = " +data+"\n");
        String [] args = data.split("#");
        
        
        //ack + fin
    	header.acknum = Integer.parseInt(args[0]);
    	header.flags[1] = 1;
    	header.flags[2] = 1;
    	
		
    	
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
	        buffer = new byte[1024];
	        buffer = packet.getData();
	      
	        data = new String(buffer);
	        logs.SetString("RECEIVED ="+data+"\n");
	        logs.SetString(data+"\n");
	        logs.SetString("=========================================================\nCONNECTION CLOSED"+"\n");
	        
    }
    
    
}
