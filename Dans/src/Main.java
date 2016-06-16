import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Main {
	private int[] servoStanden = new int[18];

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Main().doDanceMoves();
	}
	
	public void doDanceMoves(){
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("D:\\IDPgit\\python\\Dans.txt"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
		    String line = br.readLine();

		    while (line != null) {
		    	checkString(line);
		        line = br.readLine();
		    }
		} 
		catch(Exception e){
			e.printStackTrace();
		}finally {
		    try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void checkString(String input){
		if(input.contains(":")){
			setServoStand(input);
		}
		else{
			
		}
	}
	
	public void setServoStand(String input){
		String [] parts = input.split(":");
		
		int i = 0;
		i = Integer.parseInt(parts [0]);
		servoStanden [i] = Integer.parseInt(parts [1]);
		
	}

}
