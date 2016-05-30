import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class AppConnection {

	private ServerSocket serverSocket;
	public AppConnection() throws IOException
	{
		serverSocket = new ServerSocket(444);
	}
	
	public AppConnection(int port) throws IOException
	{
		serverSocket = new ServerSocket(port);
	}
	
	public void mainLoop() throws IOException
	{
		Socket clientSocket = serverSocket.accept();
		ObjectInputStream iStream = new ObjectInputStream(clientSocket.getInputStream());
		
		while (true)
		{
			try
			{
				if(iStream != null){
					SendDataToApp();
				}
				System.out.println(iStream.readObject());
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void SendDataToApp(){
		try{
			FileOutputStream fos = new FileOutputStream("t.tmp");
			ObjectOutputStream oStream = new ObjectOutputStream(fos);
			oStream.writeObject("Today");
			oStream.close();
		 
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
