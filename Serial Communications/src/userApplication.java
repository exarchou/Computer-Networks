// Exarchou Dimitrios - Marios   8805  Computer Networks I

// Libraries.
import ithakimodem.Modem;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileOutputStream;


public class userApplication 
{

	// Main Method.
	public static void main(String[] args)
	{
		System.out.println(" *****   Dimitrios - Marios  *****");
		System.out.println(" *****     Exarchou 8805     *****");
		System.out.println(" *****  Computer Networks I  *****");
		(new userApplication()).menu();	
	}
	
	
	
	// Menu Method.
	public void menu()
	{
		Scanner scanner = new Scanner(System.in);
		
		while(true){
			
			int input;
			System.out.println();
			System.out.println("==================================");
			System.out.println("|        User Application        |");
			System.out.println("==================================");
            System.out.println("|        1.Echo                  |");
            System.out.println("|        2.Image                 |");
            System.out.println("|        3.Image with Noise      |");
            System.out.println("|        4.GPS                   |");
            System.out.println("|        5.ARQ                   |");
            System.out.println("|        6.Exit                  |");
            System.out.println("==================================");
            System.out.print("  Select Mode: ");
            
            input = scanner.nextInt();
            System.out.println();
            
            switch(input){
            
            	case 1:
            		echo();
            		break;
            	case 2:
            		imageWithoutNoise();
            		break;
            	case 3:
            		imageWithNoise();
            		break;
            	case 4:
            		gps();
            		break;
            	case 5:
            		arq();
            		break;
            	case 6:
            		scanner.close();
            		return;
            	default:
            		System.out.println("Wrong Input! Please try again!");		
            }
		}	
	}
	
	
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////// Echo Method.
	
	public void echo()
	{
		// Variables Declaration.
		int k, code;
		long initialTime= 0, requestTime = 0, responseTime = 0, elapsedTime = 0;
		String echo_request_code, message = "";
		ArrayList<Long> Measurements = new ArrayList<Long>();
		
		
		// Establishing the connection.
		Modem modem;
		modem = new Modem();
		modem.setSpeed(80000);
		modem.setTimeout(3000);
		modem.write("atd2310ithaki\r".getBytes()); // Turning into data mode.
		
		for (;;){
			
			try {
				k = modem.read();
				if (k == -1) break;
				System.out.print((char)k);
				
			}catch (Exception x) {	
				break;
			}
		}
		
		
		// Getting the code EXXXX.
		System.out.print("Enter the code: E");
		Scanner scanner = new Scanner(System.in);
		code = scanner.nextInt();
		echo_request_code = "E" + String.format("%04d", code) + "\r";
		// scanner.close();
				
		//  Creating a four-minutes loop for packets receiving.
		initialTime = System.currentTimeMillis();
		
		while (System.currentTimeMillis() - initialTime < 5*60*1000){
			
			message = "";
			responseTime = 0;
			requestTime = System.currentTimeMillis();
			// Requesting Information from server Ithaki.
			modem.write(echo_request_code.getBytes());
			
			for (;;){
				
				try{
					k = modem.read();
					if (k == -1) break;
					// System.out.print((char)k);
					message += (char)k;
					
					if (message.indexOf("PSTOP") != -1) {
						responseTime = System.currentTimeMillis();
						elapsedTime = responseTime - requestTime;
					}
				}catch(Exception x){
					break;
				}
			}
			
			System.out.print(message + "   ");
			System.out.println(elapsedTime + " msec");
			Measurements.add(elapsedTime);
			responseTime = 0;
		}
		
		
		// Creating a File and writing Measurements.
        try{    
            FileWriter fw = new FileWriter("Echo.txt");    
    	 	for (int i = 0; i < Measurements.size(); i++)
    	 	{
    	 		fw.write("" + Measurements.get(i) + "\r\n");
    	 	}  
            fw.close(); 
            System.out.println("File written successfully.");  
            
        }catch(Exception e){
        	System.out.println("Error in writting file: " + e);
        }     
		
	    modem.close();
	}
	
	
	
	
	
	////////////////////////////////////////////////////////////////////////////////////////Image without Noise Method.
	
	public void imageWithoutNoise()
	{
		// Variables Declaration.
		int k, code, counter = 0;
		String image_request_code;
        ArrayList<Integer> decimalImage = new ArrayList<Integer>(); // A list that contains the integer values of received image.


		// Establishing the connection.
		Modem modem;
		modem = new Modem();
		modem.setSpeed(80000);
		modem.setTimeout(3000);
		modem.write("atd2310ithaki\r".getBytes()); // Turning into data mode.
		
		for (;;){
			
			try {
				k = modem.read();
				if (k == -1) break;
				System.out.print((char)k);	
				
			}catch (Exception x) {	
				break;
			}
		}
		
		
		// Getting the code MXXXX.
		System.out.print("Enter the code: M");
		Scanner scanner = new Scanner(System.in);
		code = scanner.nextInt();
		image_request_code = "M" + String.format("%04d", code) + "\r";
		// scanner.close();
		
		
		// Requesting Information from server Ithaki.
		modem.write(image_request_code.getBytes());
	
		for (;;){
			
			try{
				k = modem.read();
				if (k == -1) break;
				decimalImage.add(k);
				counter ++;
				
			}catch(Exception x){
				break;
			}
		}
			
		
		// Creating a jpg file using FileOutputStream.
        try{
            FileOutputStream binaryImage = new FileOutputStream("imageWithoutNoise.jpg");

            for(int i = 0 ; i < counter ; i++)
            {	
            	binaryImage.write(decimalImage.get(i));
            }
            binaryImage.close();
            System.out.println("Image created successfully."); 
            
        }catch(IOException ex){
        	System.out.println("Error in writting file: " + ex);
        }
        modem.close();
              
	}
	
	
	
	
	
	////////////////////////////////////////////////////////////////////////////////////////Image with Noise Method.
	
	public void imageWithNoise()
	{
		// Variables Declaration.
		int k, code, counter = 0;
		String image_request_code;
        ArrayList<Integer> decimalImage = new ArrayList<Integer>();

	
		// Establishing the connection.
		Modem modem;
		modem = new Modem();
		modem.setSpeed(80000);
		modem.setTimeout(3000);
		modem.write("atd2310ithaki\r".getBytes()); // Turning into data mode.
		
		for (;;){
			
			try {
				k = modem.read();
				if (k == -1) break;
				System.out.print((char)k);	
				
			}catch (Exception x) {	
				break;
			}
		}
		
		
		// Getting the code GXXXX.
		System.out.print("Enter the code: G");
		Scanner scanner = new Scanner(System.in);
		code = scanner.nextInt();
		image_request_code = "G" + String.format("%04d", code) + "\r";
		// scanner.close();
		
		
		// Requesting Information from server Ithaki.
		modem.write(image_request_code.getBytes());
	
		for (;;){
			
			try{
				k = modem.read();
				if (k == -1) break;
				decimalImage.add(k);
				counter ++;
				
			}catch(Exception x){
				break;
			}
		}
	
		
		// Creating a jpg file using FileOutputStream.
        try{
            FileOutputStream binaryImage = new FileOutputStream("imageWithNoise.jpg");

            for(int i = 0 ; i < counter ; i++)
            {	
            	binaryImage.write(decimalImage.get(i));
            }
            binaryImage.close();
            System.out.println("Image created successfully."); 
            
        }catch(IOException ex){
        	System.out.println("Error in writting file: " + ex);
        }
        modem.close();
        
	}
	
	
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////// Gps Method.
	
	public void gps()
	{
		// Variables Declaration.
		int k = 0, code, counter = 0, hours, mins, secs, totalTime, previousTime;
		String gps_request_code, message = "", time, width, length, lengthSecs, widthSecs, aabbcc, ddeezz, t;
		String [] Time = new String[2];
		String [] Width =  new String[2];
		String [] Length = new String[2];
		ArrayList<String> Messages = new ArrayList<String>();
		ArrayList<Integer> decimalImage = new ArrayList<Integer>();
		
		
		// Establishing the connection.
		Modem modem;
		modem = new Modem();
		modem.setSpeed(80000);
		modem.setTimeout(3000);
		modem.write("atd2310ithaki\r".getBytes()); // Turning into data mode.
		
		for (;;){
			
			try {
				k = modem.read();
				if (k == -1) break;
				System.out.print((char)k);	
				
			}catch (Exception x) {	
				break;
			}
		}
		
		
		// Getting the code PXXXX.
		System.out.print("Enter the code: P");
		Scanner scanner = new Scanner(System.in);
		code = scanner.nextInt();
		gps_request_code = "P" + String.format("%04d", code) + "R=1000199\r";
		// scanner.close();
		

		// Requesting Information from server Ithaki.
		modem.write(gps_request_code.getBytes());
			
		for (;;){
				
			try{
				k = modem.read();
				if (k == -1) break;
				System.out.print((char)k);
				message += (char)k;
				
				if (message.indexOf("\n") != -1)
				{
					if (message.indexOf("GPGGA") != -1) // I need only $GPGGGA header.
					{
						Messages.add(message);
					}
					message = "";
				}
					
				}catch(Exception x){
					break;
				}
		}
		
		
		// Creating a File and writing Data.
        try{    
            FileWriter fw = new FileWriter("GPS.txt");    
    	 	for (int i = 0; i < Messages.size(); i++)
    	 	{
    	 		fw.write("" + Messages.get(i) + "\r\n");
    	 	}  
            fw.close(); 
            
        }catch(Exception e){
        	System.out.println("Error in writting file: " + e);
        }    
        System.out.println("File written successfully.");   
	    
	    
        
	    // Getting an Image with 4 GPS traces that differ for at least 20 secs.
	    counter = 0;
	    previousTime = - 21; // Initializing negative to be sure that the first trace is acceptable.
	    gps_request_code = "P" + String.format("%04d", code); // Initializing gps_request_code to previous format.

	    for (int i = 0; i < Messages.size(); i++)
	    {
	    	message = Messages.get(i);
	    	
	    	// Parsing GPS message in time, width and length fields.
			time = message.substring(7,17);
			width = message.substring(18,27);
			length = message.substring(30,40);
			
			// Parsing the strings into to fields left and right of ".".
			Time = time.split("[.]");
			Width = width.split("[.]");
			Length = length.split("[.]");
			
			// Converting time to seconds.
			hours = Integer.valueOf(Time[0].substring(0,2));
			mins  = Integer.valueOf(Time[0].substring(2,4));
			secs  = Integer.valueOf(Time[0].substring(4,6));
			totalTime = secs + mins * 60 + hours * 3600;
			
			// Checking if temporal difference with previous trace is more than 20 seconds.
			if (totalTime - previousTime >= 20)
			{
				// Converting minutes to minutes and seconds.
				lengthSecs = Integer.toString(Integer.valueOf(Length[1]) * 60).substring(0,2);
				aabbcc = Integer.toString(Integer.valueOf(Length[0])) + lengthSecs;

				widthSecs = Integer.toString(Integer.valueOf(Width[1]) * 60).substring(0,2);
				ddeezz = Integer.toString(Integer.valueOf(Width[0])) + widthSecs;
				
				t = aabbcc + ddeezz;
				
				// Adding T = t to GPS request code.
				gps_request_code += "T=" + t;
				
				previousTime = totalTime;	
				counter++;
			}
			
			if (counter == 4) break;	
	    }
	    
	    
    	System.out.println(gps_request_code);
	    gps_request_code += "\r";
	    	
		// Requesting Image of Google Maps.
		modem.write(gps_request_code.getBytes());
	
		for (;;){
			
			try{
				k = modem.read();
				if (k == -1) break;
				decimalImage.add(k);
				
			}catch(Exception x){
				break;
			}
		}
	
		
		// Creating a jpg file using FileOutputStream.
        try{
            FileOutputStream binaryImage = new FileOutputStream("GPS.jpg");

            for(int i = 0 ; i < decimalImage.size() ; i++){
            	
            	binaryImage.write(decimalImage.get(i));
            }
            binaryImage.close();
            System.out.println("Image created successfully."); 
            
        }catch(IOException ex){
        	System.out.println("Error in writting file: " + ex);
        }
	    		
	    modem.close();
	}
	

	

	
	//////////////////////////////////////////////////////////////////////////////////////// ARQ Method.
	
	public void arq()
	{
		// Variables Declaration.
		int i, k, code, counterPacks = 0, counterNack = 0, fcs, number, L, repeats = 0;
		int[] cryptoCode = new int[16];
		int[] NumOfRepeats = new int[6]; // I have noticed that usually happen less than 5 repeats.
		double P, BER;
		long initialTime, responseTime, requestTime, elapsedTime;
		boolean noise = false;
		String ack_request_code, nack_request_code, message = "";
		ArrayList<Long> Measurements = new ArrayList<Long>();


		// Establishing the connection.
		Modem modem;
		modem = new Modem();
		modem.setSpeed(80000);
		modem.setTimeout(3000);
		modem.write("atd2310ithaki\r".getBytes()); // Turning into data mode.
		
		for (;;){
			
			try {
				k = modem.read();
				if (k == -1) break;
				System.out.print((char)k);
				
			}catch (Exception x) {	
				break;
			}
		}
		
		
		// Getting the code QXXXX.
		System.out.print("Enter the  ACK code: Q");
		Scanner scanner = new Scanner(System.in);
		code = scanner.nextInt();
		ack_request_code = "Q" + String.format("%04d", code) + "\r";
		// Getting the code RXXXX.
		System.out.print("Enter the NACK code: R");
		code = scanner.nextInt();
		nack_request_code = "R" + String.format("%04d", code) + "\r";
		// scanner.close();
		
		
		//  Creating a four-minutes loop for packets receiving.
		initialTime = System.currentTimeMillis();
		
		while ((System.currentTimeMillis() - initialTime < 5*60*1000) || noise){ // If time loop finishes after a wrong transmission, i am waiting until it is correctly transmitted.
			
			message = "";
			responseTime = 0;
			elapsedTime = 0;
			requestTime = System.currentTimeMillis();
			
			
			// Requesting data from server Ithaki, based on value of boolean noise.
			if (!noise){
				NumOfRepeats[repeats]++;
				repeats = 0;
				modem.write(ack_request_code.getBytes());

			}else {
				modem.write(nack_request_code.getBytes());
			}
			
			
			// Reading data.
			for (;;){
				
				try{
					k = modem.read();
					if (k == -1) break;
					// System.out.print((char)k);
					message += (char)k;
					
					if (message.indexOf("PSTOP") != -1) { // Found PSTOP.
						responseTime = System.currentTimeMillis();
						elapsedTime = responseTime - requestTime;
					}
				}catch(Exception x){
					break;
				}
			}
				
		
			// Parsing the 16 char crypto code.
			for (i = 0; i < 16; i++)
			{
				cryptoCode[i] = Integer.valueOf(message.charAt(message.indexOf('<') + 1 + i));		
			}
			
			// Parsing the FCS decimal integer.
			fcs = Integer.valueOf(message.substring(message.indexOf('>') + 2, message.indexOf('>') + 5));
			
			// Checking if package was transmitted without noise.
			number = cryptoCode[0];
			
			for (i = 1; i < 16; i++)
			{
				number = number ^ cryptoCode[i]; // XOR operator in digits of crypto code consecutively.
			}
			
			if (number == fcs){
				
				noise = false;
				
			}else {
				
				noise = true;
				counterNack++;
				repeats++;
			}
			
			// Printing message and time
			System.out.print(message + "   ");
			System.out.println(elapsedTime + " msec");
			Measurements.add(elapsedTime);
			responseTime = 0;
			counterPacks++;	
		}
		
		modem.close();
		
		
		// Creating a File and writing Measurements.
        try{    
            FileWriter fw = new FileWriter("ARQ.txt");    
    	 	for (i = 0; i < Measurements.size(); i++)
    	 	{
    	 		fw.write("" + Measurements.get(i) + "\r\n");
    	 	}  
            fw.close(); 
            System.out.println("File written successfully.");  
            
        }catch(Exception e){
        	System.out.println("Error in writting file: " + e);
        } 
        
        
		// Creating a File and writing number of Rebroadcasts.
        try{    
            FileWriter fw = new FileWriter("Rebroadcasts.txt");    
    	 	for (i = 0; i < 6; i++) // I have noticed that usually happen less than 5 repeats.
    	 	{
    	 		fw.write("" + NumOfRepeats[i] + "\r\n");
    	 	}  
            fw.close(); 
            System.out.println("File written successfully.");  
            
        }catch(Exception e){
        	System.out.println("Error in writting file: " + e);
        } 
		
  
	     // Calculating BER.
	     L = 16 * 8; //The length is equal to the number of characters (16) times the number of bits per character (8).
	     P = (double) (counterPacks - counterNack) / counterPacks; // Probability of successful transmission.
	     BER = (double) (1.0 - Math.pow(P, 1.0/L));
	     System.out.println("Bit Error Rate: " + BER);

	}
	


}





// End of Program.