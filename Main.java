import java.io.*;
import java.util.Arrays;

public class Main{

	public void readQueryText(VectorSpace vp,String filename){	
		File fi = new File(filename);
		String line;
        if( fi.exists()){
			try {
				FileReader fr=new FileReader(filename);
				BufferedReader br=new BufferedReader(fr);
				while ((line=br.readLine()) != null) {
					vp.storeQuery(line);
				}
				br.close();
			}
			catch (IOException e) {System.out.println(e);}
		}
	}
	public void readDocText(VectorSpace vp,String filename){	
		File fi = new File(filename);
		String line;
        if( fi.exists()){
			try {
				FileReader fr=new FileReader(filename);
				BufferedReader br=new BufferedReader(fr);
				while ((line=br.readLine()) != null) {
					vp.storeDoc(line);
				}
				br.close();
			}
			catch (IOException e) {System.out.println(e);}
		}
	}
	
	public static void main(String args[]){	
	long time1, time2;
	time1 = System.currentTimeMillis();
		Main main = new Main();
		VectorSpace vp = new VectorSpace();
		main.readDocText(vp,"doc_list.txt");
		vp.setDocTF();
		main.readQueryText(vp,"query_list.txt");
		vp.setQueryIDF();
		vp.retrieve();
	time2 = System.currentTimeMillis();
	System.out.println("Time spend: " + (time2-time1)/1000 + " Second");
	}
	
	
}