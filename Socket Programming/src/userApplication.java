// Dimitrios-Marios Exarcou 8805
// Computer Networks II

import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;



public class userApplication {

	// Main Method.
	public static void main(String[] args) throws IOException, LineUnavailableException, ClassNotFoundException {
		(new userApplication()).menu();	
	}
	
	
	
	// Menu Method.
	public void menu() throws IOException, LineUnavailableException, ClassNotFoundException {
		
		Scanner scanner = new Scanner(System.in);		
		while(true){
			
			int input, serverPort = 38036, clientPort = 48036; 
			
			System.out.println();
			System.out.println("======================================");
			System.out.println("|          User Application          |");
			System.out.println("======================================");
			System.out.println("|          1.Echo                    |");
			System.out.println("|          2.Image                   |");
			System.out.println("|          3.Sound (DPCM)            |");
			System.out.println("|          4.Sound (AQ-DPCM)         |");
			System.out.println("|          5.Ithakicopter            |");
			System.out.println("|          6.Vehicle OBD-II          |");
			System.out.println("|          7.Exit                    |");
			System.out.println("======================================");
			System.out.print("  Select Mode: ");
            
			input = scanner.nextInt();
        
			switch(input){
            
			case 1:
				echo(serverPort, clientPort);
				break;
			case 2:
				image(serverPort, clientPort);
				break;
			case 3:
				sound_DPCM(serverPort, clientPort);
				break;	
			case 4:
				sound_AQDPCM(serverPort, clientPort);
				break;
			case 5:
				ithakicopter(38048, 48078);
				break;
			case 6:
				vehicle_OBD_II(serverPort, clientPort);
				break;
			case 7:
				scanner.close();
				System.out.println("See you soon user...");
				return;
			default:
				System.out.println("Wrong Input! Please try again!");		
			}
		}	
	}
	
	
	
	// Echo Method.
	public void echo (int serverPort, int clientPort) throws SocketException,IOException,UnknownHostException{
			
		// Initializations.
		int counter = 0; int sumTime = 0, input, code;
		long initialTime= 0, requestTime = 0, responseTime = 0, elapsedTime = 0;
		String packetRequest = "", mode = "", message;
		ArrayList<Long> Measurements = new ArrayList<Long>();
		ArrayList<Integer> Packets = new ArrayList<Integer>();
		ArrayList<Float> Throughput = new ArrayList<Float>();
		ArrayList<String> Temperatures = new ArrayList<String>();
		
		// Taking input mode.
		System.out.println();
		System.out.println("1. Echo with delay ");
		System.out.println("2. Echo without delay ");
		System.out.print("Make a choice: ");
		Scanner scanner = new Scanner(System.in);
		input = scanner.nextInt();
		
		if (input == 1) {    
			mode = "with_delay";
			System.out.println();	
			System.out.print("Enter the echo request code: E");	    	
			code = scanner.nextInt();   
			packetRequest = "E" + Integer.toString(code);
		}else {
			mode = "without_delay";
			packetRequest = "E0000";
		}
        
		// Establishing connection with Ithaki.
		DatagramSocket s = new DatagramSocket();
		DatagramSocket r = new DatagramSocket(clientPort);	
		byte[] txbuffer = packetRequest.getBytes();	
		byte[] hostIP = {(byte)155, (byte)207, (byte)18, (byte)208};
		byte[] rxbuffer = new byte[2048];
		InetAddress hostAddress = InetAddress.getByAddress(hostIP);
		DatagramPacket p = new DatagramPacket(txbuffer,txbuffer.length, hostAddress,serverPort);	
		DatagramPacket q = new DatagramPacket(rxbuffer,rxbuffer.length);
		r.setSoTimeout(3200);

		// Sending and Receiving packets.
		initialTime = System.currentTimeMillis();		
		while (System.currentTimeMillis() - initialTime < 4*60*1000) {
			
			s.send(p);
			requestTime = System.currentTimeMillis();		
			try {
				r.receive(q);
				responseTime = System.currentTimeMillis();
				elapsedTime = responseTime - requestTime;	
				message = new String(rxbuffer, 0, q.getLength());
				System.out.println(message);
			} catch (Exception x) {
				System.out.println(x);
			}

			Measurements.add(elapsedTime);			
		}
		
		// Creating a File and writing Measurements.
		try {    
			FileWriter fw = new FileWriter("Echo_" + mode + ".txt");    
			for (int i = 0; i < Measurements.size(); i++){
				fw.write("" + Measurements.get(i) + "\r\n");
			}  
			fw.close(); 
			System.out.println("File written successfully.");   
		} catch (Exception e) {
			System.out.println("Error in writting file: " + e);
		}     
		
		// Moving average.   
		for (int i = 0; i < Measurements.size(); i++) {
			int j = i;
			while ((sumTime < 8*1000) && (j < Measurements.size())) {
				sumTime += Measurements.get(j);
		    		counter++;
		    		j++;
			}
	    	Throughput.add((float)counter/8);
	    	Packets.add(counter);
	    	counter = 0;
	    	sumTime = 0;
		}
	
		// Creating a File and writing Throughput.
		try{    
			FileWriter fw = new FileWriter("Throughput_" + mode + ".txt");    
			for (int i = 0; i < Throughput.size(); i++){
				fw.write("" + Throughput.get(i) + "\r\n");
			}  
			fw.close(); 
			System.out.println("File written successfully.");   
		}catch(Exception e){
			System.out.println("Error in writting file: " + e);
		} 
        

		// Temperatures
		System.out.println();
		System.out.println("Temperatures: ");
		for (int i = 1; i <= 1; i++) {
			packetRequest = "E0000T00";
			txbuffer = packetRequest.getBytes();
			p = new DatagramPacket(txbuffer,txbuffer.length, hostAddress,serverPort);
			s.send(p);
			
			try {
				r.receive(q);				
				message = new String(rxbuffer, 0, q.getLength());
				System.out.println(message);
				Temperatures.add(message);
			} catch (Exception x) {
				System.out.println(x);
			}
		}
        
		// Creating a File and writing Temperatures.
		try{    
			FileWriter fw = new FileWriter("Temperatures_" + mode + ".txt");    
		 	for (int i = 0; i < Temperatures.size(); i++){
				fw.write("" + Temperatures.get(i) + "\r\n");
		 	}  
			fw.close(); 
			System.out.println("File written successfully.");     
		}catch(Exception e){
			System.out.println("Error in writting file: " + e);
		} 

		r.close();
		s.close();
	}
		


	// Image Method.
	public void image (int serverPort, int clientPort) throws SocketException,IOException,UnknownHostException{
		
		// Initializations.
		int input, code;
		String packetRequest = "", mode = "";
		ArrayList<Byte> ByteImage = new ArrayList<Byte>(); // A list that contains the integer values of received image.
		
		// Taking input mode.
		System.out.println();
		System.out.println("1. Camera 1 ");
		System.out.println("2. Camera 2 ");
		System.out.print("Make a choice: ");
		Scanner scanner = new Scanner(System.in);
		input = scanner.nextInt();
        
		System.out.print("Enter the image request code: M"); 	
		code = scanner.nextInt(); 
	 
		if (input == 1) {
			mode = "CAM1";
			packetRequest = "M" + String.format("%04d", code);
		}else {
			mode = "CAM2";
			packetRequest = "M" + String.format("%04d", code) + " " + "CAM=PTZ";
		}
         
		// Establishing connection with Ithaki.
		DatagramSocket s = new DatagramSocket();
		DatagramSocket r = new DatagramSocket(clientPort);	
		byte[] txbuffer = packetRequest.getBytes();	
		byte[] hostIP = {(byte)155, (byte)207, (byte)18, (byte)208};
		byte[] rxbuffer = new byte[2048];
		InetAddress hostAddress = InetAddress.getByAddress(hostIP);
		DatagramPacket p = new DatagramPacket(txbuffer,txbuffer.length, hostAddress,serverPort);	
		DatagramPacket q = new DatagramPacket(rxbuffer,rxbuffer.length);
		r.setSoTimeout(3200);
			
		// Sending and receiving packets.
		s.send(p);
		for (;;) {
			try {
				r.receive(q);
				if (rxbuffer == null) break;
 				for (int i = 0 ; i < 128 ; i++) {	
					ByteImage.add(rxbuffer[i]);
 				}
			} catch (Exception x) {
				System.out.println(x);
				break;
			}
		}
		
		// Creating a jpg file using FileOutputStream.
		try {
			FileOutputStream binaryImage = new FileOutputStream("image_" + mode + ".jpg");
			for (int i = 0 ; i < ByteImage.size() ; i++){	
				binaryImage.write(ByteImage.get(i));
			}
			binaryImage.close();
			System.out.println("Image created successfully.");       
		}catch(IOException ex){
			System.out.println("Error in writting file: " + ex);
		}
		   
		r.close();
		s.close();
	}
	
	
	
	// Sound with DPCM Method.
	public void sound_DPCM (int serverPort, int clientPort) throws SocketException,IOException,UnknownHostException, LineUnavailableException{
				
		// Initializations.
		int input, code, counter = 0, Q = 8, numPackets = 999, nibble1, nibble2, difference1, difference2, beta = 1, x1 = 0, x2 = 0;
		String packetRequest = "", mode = "";
		byte pairByte;
		byte[] song = new byte[256 * numPackets];
		ArrayList<Integer> samples = new ArrayList<Integer>();
		ArrayList<Integer> differences = new ArrayList<Integer>();

		// Taking input mode.
		System.out.println();
		System.out.println("1. Song ");
		System.out.println("2. Frequency ");
		System.out.print("Make a choice: ");
		Scanner scanner = new Scanner(System.in);
		input = scanner.nextInt();

		System.out.print("Enter the sound request code: A"); 	
		code = scanner.nextInt(); 

		if (input == 1) {
			mode = "song";
			packetRequest = "A" + String.format("%04d", code) + "F999"; 	
		}else {
			mode = "frequency";
			packetRequest = "A" + String.format("%04d", code) + "T999";
		}

		// Establishing connection with Ithaki.
		DatagramSocket s = new DatagramSocket();
		DatagramSocket r = new DatagramSocket(clientPort);	
		byte[] txbuffer = packetRequest.getBytes();	
		byte[] hostIP = {(byte)155, (byte)207, (byte)18, (byte)208};
		byte[] rxbuffer = new byte[2048];
		InetAddress hostAddress = InetAddress.getByAddress(hostIP);
		DatagramPacket p = new DatagramPacket(txbuffer,txbuffer.length, hostAddress,serverPort);	
		DatagramPacket q = new DatagramPacket(rxbuffer,rxbuffer.length);
		r.setSoTimeout(3200);

		// Sending and Receiving packets.
		s.send(p);

		for (int i = 0; i < numPackets; i++) {	
			try {
				r.receive(q);
				for (int j = 0 ; j < 128 ; j++) {
					pairByte = rxbuffer[j];
					nibble1 = pairByte & 15; // 00001111
					nibble2 = (pairByte & 240) >> 4; // 11110000

					difference1 = (nibble1 - 8);
					difference2 = (nibble2 - 8);
					differences.add(difference1);
					differences.add(difference2);
					difference1 = difference1 * beta;
					difference2 = difference2 * beta;

					x1 = x2 + difference1; // Initially x2 = 0
					x2 = x1 + difference2;
					samples.add(x1);
					samples.add(x2);
					song[counter++] = (byte) x1;
					song[counter++] = (byte) x2;
				}
			} catch (Exception x) {
				System.out.println(x);
				break;
			}
		}

		// Playing the song
		if (input == 1) {		
			System.out.println("Playing a song...");
			AudioFormat linearPCM = new AudioFormat(8000,Q,1,true,false);
			SourceDataLine lineOut = AudioSystem.getSourceDataLine(linearPCM);
			lineOut.open(linearPCM,32000);
			lineOut.start();
			lineOut.write(song,0,song.length);
			lineOut.stop();
			lineOut.close();
		}

		// Creating a File and writing Samples.
		try {    
			FileWriter fw = new FileWriter("DPCM_" + mode + "_samples.txt");    
			for (int i = 0; i < samples.size(); i++) {
				fw.write("" + samples.get(i) + "\r\n");
			}  
			fw.close(); 
			System.out.println("File written successfully.");    
		} catch(Exception e) {
			System.out.println("Error in writting file: " + e);
		} 

		// Creating a File and writing Differences.
		try {    
			FileWriter fw = new FileWriter("DPCM_" + mode + "_differences.txt");    
			for (int i = 0; i < differences.size(); i++) {
				fw.write("" + differences.get(i) + "\r\n");
			}  
			fw.close(); 
			System.out.println("File written successfully.");     
		} catch(Exception e) {
			System.out.println("Error in writting file: " + e);
		} 

		r.close();
		s.close();
	}
	
	
	// Sound with AQ-DPCM Method.
	public void sound_AQDPCM (int serverPort, int clientPort) throws SocketException,IOException,UnknownHostException, LineUnavailableException{
		
		// Initializations.
		int input, code, counter = 0, Q = 16, numPackets = 999, nibble1, nibble2, difference1, difference2, beta, x1 = 0, x2 = 0, mean, temp = 0;
		String packetRequest = "", mode = "";
		byte pairByte;
		byte[] song = new byte[256 * 2 * numPackets];
		byte[] mByte = new byte[4]; // I need 4 bytes for an integer
		byte[] bByte = new byte[4];
		ArrayList<Integer> samples = new ArrayList<Integer>();
		ArrayList<Integer> differences = new ArrayList<Integer>();
		ArrayList<Integer> means = new ArrayList<Integer>();
		ArrayList<Integer> betas = new ArrayList<Integer>();

		// Taking input mode.
		System.out.println();
		System.out.println("1. Song ");
		System.out.println("2. Frequency ");
		System.out.print("Make a choice: ");
		Scanner scanner = new Scanner(System.in);
		input = scanner.nextInt();

		System.out.print("Enter the sound request code: A"); 	
		code = scanner.nextInt(); 

		if (input == 1) {
			mode = "song";
			packetRequest = "A" + String.format("%04d", code) + "AQF999";  	
		}else {
			mode = "frequency";
			packetRequest = "A" + String.format("%04d", code) + "AQT999";
		}

		// Establishing connection with Ithaki.
		DatagramSocket s = new DatagramSocket();
		DatagramSocket r = new DatagramSocket(clientPort);	
		byte[] txbuffer = packetRequest.getBytes();	
		byte[] hostIP = {(byte)155, (byte)207, (byte)18, (byte)208};
		byte[] rxbuffer = new byte[132];
		InetAddress hostAddress = InetAddress.getByAddress(hostIP);
		DatagramPacket p = new DatagramPacket(txbuffer,txbuffer.length, hostAddress,serverPort);	
		DatagramPacket q = new DatagramPacket(rxbuffer,rxbuffer.length);
		r.setSoTimeout(800);

		// Sending and Receiving packets.
		s.send(p);
		for (int i = 0; i < numPackets; i++) {
			try {
				r.receive(q);

				// Mean bytes
				if ((rxbuffer[1] & 0x80) == 0) { // 10000000 Mask
					mByte[3] = (byte) 0x00;
					mByte[2] = (byte) 0x00;
					mByte[1] = rxbuffer[1];
					mByte[0] = rxbuffer[0];
				}else {
					mByte[3] = (byte) 0xff;
					mByte[2] = (byte) 0xff;
					mByte[1] = rxbuffer[1];
					mByte[0] = rxbuffer[0];
				}
				mean = ByteBuffer.wrap(mByte).order(ByteOrder.LITTLE_ENDIAN).getInt(); 
				means.add(mean);

				// Beta bytes
				if ((rxbuffer[3] & 0x80) == 0) { // 10000000 Mask
					bByte[3] = (byte) 0x00;
					bByte[2] = (byte) 0x00;
					bByte[1] = rxbuffer[3];
					bByte[0] = rxbuffer[2];

				}else {
					bByte[3] = (byte) 0xff;
					bByte[2] = (byte) 0xff;
					bByte[1] = rxbuffer[3];
					bByte[0] = rxbuffer[2];
				}

				beta = ByteBuffer.wrap(bByte).order(ByteOrder.LITTLE_ENDIAN).getInt(); 
				betas.add(beta);	

				// Rest bytes
				for (int j = 4 ; j < 132 ; j++){

					pairByte = rxbuffer[j];
					nibble1 = (int)(pairByte & 0x0000000F); // 00000000000000000000000000001111
					nibble2 = (int)((pairByte & 0x000000F0) >> 4); // 00000000000000000000000011110000

					difference1 = (nibble2 - 8); 
					difference2 = (nibble1 - 8);
					differences.add(difference1);
					differences.add(difference2);
					difference1 = difference1 * beta; 
					difference2 = difference2 * beta;					

					x1 = mean + difference1 + temp; // Initially temp = 0
					x2 = mean + difference1 + difference2;
					temp = difference2;

					samples.add(x1);
					samples.add(x2);
					song[counter++] = (byte) (x1 & 0x000000FF);
					song[counter++] = (byte) ((x1 & 0x0000FF00) >> 8);
					song[counter++] = (byte) (x2 & 0x000000FF);
					song[counter++] = (byte) ((x2 & 0x0000FF00) >> 8); 
				}
			} catch (Exception x) {
				System.out.println(x);
				break;
			}	
		}

		// Playing the song.
		if (input == 1) {		
			System.out.println("Playing a song...");
			AudioFormat linearPCM = new AudioFormat(8000,Q,1,true,false);
			SourceDataLine lineOut = AudioSystem.getSourceDataLine(linearPCM);
			lineOut.open(linearPCM,32000);
			lineOut.start();
			lineOut.write(song,0,song.length);
			lineOut.stop();
			lineOut.close();
		}

		// Creating a File and writing Samples.
		try {    
			FileWriter fw = new FileWriter("AQ_DPCM_" + mode + "_samples.txt");    
			for (int i = 0; i < samples.size(); i++) {
				fw.write("" + samples.get(i) + "\r\n");
			}  
			fw.close(); 
			System.out.println("File written successfully.");        
		} catch(Exception e) {
			System.out.println("Error in writting file: " + e);
		} 

		// Creating a File and writing Differences.
		try {    
			FileWriter fw = new FileWriter("AQ_DPCM_" + mode + "_differences.txt");    
			for (int i = 0; i < differences.size(); i++) {
				fw.write("" + differences.get(i) + "\r\n");
			}  
			fw.close(); 
			System.out.println("File written successfully.");  
		} catch(Exception e) {
			System.out.println("Error in writting file: " + e);
		} 

		// Creating a File and writing Means.
		try {    
			FileWriter fw = new FileWriter("AQ_DPCM_" + mode + "_means.txt");    
			for (int i = 0; i < means.size(); i++) {
				fw.write("" + means.get(i) + "\r\n");
			}  
			fw.close(); 
			System.out.println("File written successfully.");    
		} catch(Exception e) {
			System.out.println("Error in writting file: " + e);
		} 

		// Creating a File and writing Betas.
		try {    
			FileWriter fw = new FileWriter("AQ_DPCM_" + mode + "_betas.txt");    
			for (int i = 0; i < betas.size(); i++) {
				fw.write("" + betas.get(i) + "\r\n");
			}  
			fw.close(); 
			System.out.println("File written successfully.");   
		} catch(Exception e) {
			System.out.println("Error in writting file: " + e);
		} 

		r.close();
		s.close();
	}
	

	// Ithakicopter Method
	public void ithakicopter (int serverPort, int clientPort) throws SocketException,IOException,UnknownHostException,LineUnavailableException,ClassNotFoundException {
		
		// Initializations.
		int code;
		String message = "", packetRequest = "";
		ArrayList<String> Measurements = new ArrayList<String>();

		// Taking input.
		System.out.print("Enter the Ithakicopter code: Q");	
		Scanner scanner = new Scanner(System.in);
		code = scanner.nextInt(); 
		packetRequest = "Q" + String.format("%04d", code);

		// Establishing connection with Ithaki.
		DatagramSocket s = new DatagramSocket();
		DatagramSocket r = new DatagramSocket(clientPort);	
		byte[] txbuffer = packetRequest.getBytes();	
		byte[] hostIP = {(byte)155, (byte)207, (byte)18, (byte)208};
		byte[] rxbuffer = new byte[5000];
		InetAddress hostAddress = InetAddress.getByAddress(hostIP);
		DatagramPacket p = new DatagramPacket(txbuffer,txbuffer.length, hostAddress,serverPort);	
		DatagramPacket q = new DatagramPacket(rxbuffer,rxbuffer.length);
		r.setSoTimeout(5000);

		// Sending and Receiving packets.
		for (int i = 0; i < 60; i++) {
			try {
				// s.send(p);
				r.receive(q);
				message = new String(rxbuffer, 0, q.getLength());
				System.out.println(message);
				Measurements.add(message);
			} catch (Exception e) {
				System.out.println(e);
			} 
		}

		// Creating a File and writing Measurements.
		try {    
			FileWriter fw = new FileWriter("Ithakicopter.txt");    
			for (int i = 0; i < Measurements.size(); i++) {
				fw.write("" + Measurements.get(i).substring(64,67) + "\r\n");
			}  
			fw.close(); 
			System.out.println("File written successfully.");  
		} catch(Exception e) {
			System.out.println("Error in writting file: " + e);
		} 	

		r.close();
		s.close();		
	}
	
	
	// Vehicle OBD-II Method.
	public void vehicle_OBD_II (int serverPort, int clientPort) throws SocketException,IOException,UnknownHostException,LineUnavailableException,ClassNotFoundException {
		
		// Initializations.
		int input, code, pid, XX, YY, decodedResponse = 0;
		long initialTime= 0, requestTime = 0, responseTime = 0, elapsedTime = 0;
		String message = "", FirstByte = "", SecondByte = "", packetRequest = "", mode = "";
		String[] pids_table = new String[] {"1F", "0F", "11", "0C", "0D","05"};
		ArrayList<String> Responses = new ArrayList<String>();
		ArrayList<Integer> decodedResponses = new ArrayList<Integer>();

		// Taking input mode.
		System.out.println();
		System.out.println("Make a decision");
		System.out.println("0. Engine run time");
		System.out.println("1. Intake air temperature");
		System.out.println("2. Throttle position");
		System.out.println("3. Engine RPM");
		System.out.println("4. Vehicle speed");
		System.out.println("5. Coolant temperature");
		Scanner scanner = new Scanner(System.in);
		input = scanner.nextInt();

		System.out.print("Enter the vehicle OBD-II code: V");		
		code = scanner.nextInt(); 
		packetRequest = "V" + String.format("%04d", code) + "OBD=01 " + pids_table[input];

		// Establishing connection with Ithaki.
		DatagramSocket s = new DatagramSocket();
		DatagramSocket r = new DatagramSocket(clientPort);	
		byte[] txbuffer = packetRequest.getBytes();	
		byte[] hostIP = {(byte)155, (byte)207, (byte)18, (byte)208};
		byte[] rxbuffer = new byte[2048];
		InetAddress hostAddress = InetAddress.getByAddress(hostIP);
		DatagramPacket p = new DatagramPacket(txbuffer,txbuffer.length, hostAddress,serverPort);	
		DatagramPacket q = new DatagramPacket(rxbuffer,rxbuffer.length);
		r.setSoTimeout(3200);

		// Sending and Receiving packets.
		s.send(p);
		initialTime = System.currentTimeMillis();

		while (System.currentTimeMillis() - initialTime < 4*60*1000) {
			s.send(p);
			for (;;) {
				try {
					r.receive(q);			
					message = new String(rxbuffer, 0, q.getLength());
					System.out.println(message);
					break;
				} catch (Exception x) {
					System.out.println(x);
					break;
				}
			}
			Responses.add(message);				
		}

		// Decoding Responses.
		for (int i = 0; i < Responses.size(); i++) {	

			message = Responses.get(i);
			switch (input) {

				case 0:
					FirstByte = message.substring(6,8);
					SecondByte = message.substring(9,11);
					XX = Integer.parseInt(FirstByte,16);
					YY = Integer.parseInt(SecondByte,16);
					decodedResponse = 256 * XX + YY;
					break;

				case 1:
					FirstByte = message.substring(6,8);
					XX = Integer.parseInt(FirstByte,16);
					decodedResponse = XX - 40;
					break;

				case 2:
					FirstByte = message.substring(6,8);
					XX = Integer.parseInt(FirstByte,16);
					decodedResponse = XX * 100 / 255;
					break;

				case 3:
					FirstByte = message.substring(6,8);
					SecondByte = message.substring(9,11);
					XX = Integer.parseInt(FirstByte,16);
					YY = Integer.parseInt(SecondByte,16);
					decodedResponse = ((XX * 256) + YY) / 4;
					break;

				case 4:
					FirstByte = message.substring(6,8);
					XX = Integer.parseInt(FirstByte,16);
					decodedResponse = XX;
					break;

				case 5:
					FirstByte = message.substring(6,8);
					XX = Integer.parseInt(FirstByte,16);
					decodedResponse = XX - 40;
					break;
			}
			decodedResponses.add(decodedResponse);		
		}

		// Creating Files and writing Responses.
		try{    
			FileWriter fw = new FileWriter("Vehicle_" + Integer.toString(input) + ".txt");    
			for (int i = 0; i < decodedResponses.size(); i++) {
				fw.write("" + decodedResponses.get(i) + "\r\n");
			}  
			fw.close(); 
			System.out.println("File written successfully.");     
		}catch(Exception e){
			System.out.println("Error in writting file: " + e);
		}  
		
		r.close();
		s.close();
	}

}