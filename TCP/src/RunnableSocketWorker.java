import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class RunnableSocketWorker implements Runnable {

	Socket socket;
	public RunnableSocketWorker(Socket socket) {
		// TODO Auto-generated constructor stub
		this.socket = socket;
		run();
	}

	@Override
	public void run() {
		InputStreamReader IR;
		try {
			
			while(true){
				IR = new InputStreamReader(socket.getInputStream());
				BufferedReader BR = new BufferedReader(IR);
				String MESSAGE = BR.readLine();
				System.out.println(MESSAGE);
				
				if(MESSAGE != null)
				{
					//SendAskedData();
					PrintStream PS = new PrintStream(socket.getOutputStream());
					PS.println("Message Recieved");
					System.out.println("Message send");
				
				}
			}
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
