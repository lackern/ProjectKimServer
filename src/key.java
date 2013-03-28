/* CS3283 Project
 * 
 * Loads node information from tinyOs output: "src/TinyOs.txt"
 * 
 */

import java.io.BufferedReader;
import java.io.FileReader;

public class key 
{
	//private BufferedWriter out;
	private BufferedReader in;
	private static String[] nodeInfo;

	// Constructor
	public key() {
		nodeInfo = new String[10];
	}

	public static void main(String[] args){
		key pp = new key();
		pp.loadTinyOsInfo();

		for(int i = 0; i < 10; i++){
			System.out.println(nodeInfo[i]);
		}
	}
	
	public String loadTinyOsInfo() 
	{
		try 
		{
			/** Load TinyOs parameters **/
			in = new BufferedReader(new FileReader("src/keyCodeList.txt"));
			String read = in.readLine();
			int counter = 0;

			while (read != null) 
			{
				//System.out.println(read);
				nodeInfo[counter] = read;
				//System.out.println(anArray[counter]);
				read = in.readLine();
				counter++;
			}
			return "Load information successful";
		}catch (Exception e) {
			return "File Corrupted!!" + "The Problem is : " + e.getMessage();
		}

	}

	public String[] getTinyOsInfo() {

		System.out.println(loadTinyOsInfo() +" [TinyOsLoader.java]"); 

		return nodeInfo;
	}

}