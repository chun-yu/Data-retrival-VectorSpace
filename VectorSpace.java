import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;
import java.lang.*;


public class VectorSpace{
	public static Map<String, String> indexMap = new HashMap<String, String>();
	public static Map<String, Integer> indexIDFMap = new HashMap<String, Integer>();
	public static Map<String, String> docMap = new HashMap<String, String>();
	public static Map<String, HashMap<String, Integer>> tfMap = new HashMap<String, HashMap<String, Integer>>();
	public static Map<String, HashMap<String, Integer>> DoctfMap = new HashMap<String, HashMap<String, Integer>>();
	public static Map<String, Integer> termsMap = new HashMap<String, Integer>();
	public static Map<String, Double> resultMap = new HashMap<String, Double>();
	StringBuilder report = new StringBuilder();
	
	public void storeQuery(String filename){
		String line;
		StringBuilder sValue = new StringBuilder("");
			try {
				FileReader fr=new FileReader(new File("./Query/"+filename));
				BufferedReader br=new BufferedReader(fr);
				while ((line=br.readLine()) != null) {
					line = line.substring(0,line.indexOf('-'));
					sValue.append(line);
				}
				indexMap.put(filename, sValue.toString());
				//storeDoc(sValue.toString(),"doc_list.txt");
				br.close();
			}
			catch (IOException e) {System.out.println(e);}
		
		indexMap = sortHashMapByComparator(indexMap);
		setTerms();
	}
	public void setTerms(){
		String docword;

		for (Map.Entry<String, String> entry : indexMap.entrySet()){
			int maxFreq = 1;
		    String term = entry.getValue().toString();
			String[] terms = term.split(" ");
			HashMap<String, Integer> freqMap = new HashMap<String, Integer>();
			for(String s: terms){
				if(freqMap.containsKey(s)){
					int currentFreq = freqMap.get(s);
					freqMap.put(s, currentFreq+1);
					if(currentFreq+1 > maxFreq) maxFreq = currentFreq+1;
				}else{
					freqMap.put(s, 1);
				}
				if(indexIDFMap.containsKey(s)){
				}else{
					for(Map.Entry<String, HashMap<String,Integer>> entryDoc : DoctfMap.entrySet()){
						HashMap<String, Integer> freqMap2=entryDoc.getValue();
						if(freqMap2.containsKey(s)){
							if(indexIDFMap.containsKey(s)){
								if(indexIDFMap.get(s)<2265){
									int currentFreq = indexIDFMap.get(s);
									indexIDFMap.put(s, currentFreq+1);
								}else{}
							}else{
									indexIDFMap.put(s, 1);
							}
						}
					}
					if(!(indexIDFMap.containsKey(s)))	indexIDFMap.put(s,0);
				}
			}
			freqMap.put("MAX",maxFreq);
			tfMap.put(entry.getKey().toString(),freqMap);
		}
	}
	public void setQueryIDF(){
		for (Map.Entry<String, String> entry : indexMap.entrySet()){
		    String term = entry.getValue().toString();
			String[] terms = term.split(" ");
			for(String s: terms){
				if(termsMap.containsKey(s)){
				}else{
					for(Map.Entry<String, HashMap<String,Integer>> entryQuery : tfMap.entrySet()){
						HashMap<String, Integer> freqMap2=entryQuery.getValue();
						if(freqMap2.containsKey(s)){
							if(termsMap.containsKey(s)){
								if(termsMap.get(s)<16){
									int currentFreq = termsMap.get(s);
									termsMap.put(s, currentFreq+1);
								}else{}
							}else{
									termsMap.put(s, 1);
							}
						}
					}
					if(!(termsMap.containsKey(s)))	termsMap.put(s,0);
				}
			}
		}
	}
	public void storeDoc(String filename){
		StringBuilder sValue = new StringBuilder("");
		String sCurrentLine;
			try {
				BufferedReader br=new BufferedReader(new FileReader(new File("./Document/"+filename)));
				sCurrentLine=br.readLine();sCurrentLine=br.readLine();sCurrentLine=br.readLine();
				while ((sCurrentLine = br.readLine()) != null) {
					sCurrentLine = sCurrentLine.substring(0,sCurrentLine.indexOf('-'));
					sValue.append(sCurrentLine);
				}
				docMap.put(filename, sValue.toString());
				br.close();
			}
			catch (IOException e) {System.out.println(e);}
		docMap = sortHashMapByComparator(docMap);
	}
	
	public double getTF(String t, String name){
		HashMap<String, Integer> freqMap;
	    int ft = 0;int maxFreq = 1;
		String max = "MAX";
	    if(tfMap.containsKey(name)){
			freqMap=tfMap.get(name);
			maxFreq=freqMap.get(max);
			if(freqMap.containsKey(t))	ft=freqMap.get(t);
		}
		return 0.55+((0.45*ft)/(maxFreq));
	}
	public void setDocTF(){
		HashMap<String, Integer> freqMap;
		String name;String[] docterms;
		for (Map.Entry<String, String> entry : docMap.entrySet()){
			freqMap = new HashMap<String, Integer>();
			name = entry.getKey().toString();
			docterms=entry.getValue().toString().split(" ");
			for(String s: docterms){
				if(freqMap.containsKey(s)){
					int currentFreq = freqMap.get(s);
					freqMap.put(s, currentFreq+1);
				}else{
					freqMap.put(s, 1);
				}
			}
			DoctfMap.put(name, freqMap);
		}
	}
	public double getDocTF(String n, String d){
		int ft = 0;
		HashMap<String, Integer> freqMap;
	    if(DoctfMap.containsKey(n)){
			freqMap = DoctfMap.get(n);
			if(freqMap.containsKey(d)) ft = freqMap.get(d);
		}
		return ft;
	}

	public double getIDF(String s){
		int docFreq = 0;
		String docword;
		if(indexIDFMap.containsKey(s))	docFreq=indexIDFMap.get(s);;
		if(docFreq==0)	return 0;
		return Math.log((2265.0/docFreq)+1);
	}
	public double getQueryIDF(String s){
		int docFreq = 0;
		String docword;
		if(termsMap.containsKey(s))	docFreq=termsMap.get(s);;
		if(docFreq==0)	return 0;
		return Math.log(16.0/docFreq);
	}
	
	public double[] getVector(String name){
		Vector<Double> tokens;
			tokens = new Vector<Double>();
			String term = indexMap.get(name).toString();
			String[] terms = term.split(" ");
				for (String s :terms){
						tokens.add(getTF(s,name)*getQueryIDF(s));
				}
		
		double ret[] = new double[tokens.size()];
		for(int i=0;i<tokens.size();i++){
			ret[i] = tokens.get(i).doubleValue();
		}
		return ret;
	}
	public double[] getDocVector(String queryNmae,String name){
		Vector<Double> tokens;
			tokens = new Vector<Double>();
			String term = indexMap.get(queryNmae).toString();
			String[] terms = term.split(" ");
				for (String s :terms){
						tokens.add(getDocTF(name,s)*getIDF(s));
				}
		
		double ret[] = new double[tokens.size()];
		for(int i=0;i<tokens.size();i++){
			ret[i] = tokens.get(i).doubleValue();
		}
		return ret;
	}
	public double getCosineSimScore(double[] vectorA, double[] vectorB){
		int n = vectorA.length;
		double sumAB = 0;
		double A2 = 0;
		double B2 = 0;
		for(int i=0;i<n;i++){
			double Ai = vectorA[i];
			double Bi = vectorB[i];
			sumAB += Ai*Bi;
			A2 += Math.pow(Ai,2);
			B2 += Math.pow(Bi,2);
		}
		
		return sumAB/(Math.sqrt(A2)*Math.sqrt(B2));
	}
	public void retrieve(){
		double[] queryVector ;
		String docword;String[] terms;String queryNmae;
		report.append("Query,RetrievedDocuments"+"\n");
		for (Map.Entry<String, String> Indexentry : indexMap.entrySet()){
			queryNmae=Indexentry.getKey().toString();
			queryVector=getVector(Indexentry.getKey().toString());
			for (Map.Entry<String, String> Docentry : docMap.entrySet()){
				docword = Docentry.getKey().toString();
				resultMap.put(docword,getCosineSimScore(getDocVector(queryNmae,docword),queryVector));
			}
			resultMap = sortMapResult(resultMap);
			report.append(Indexentry.getKey()+",");
			for (Map.Entry<String, Double> resultentry : resultMap.entrySet()){
				report.append(resultentry.getKey()+" ");
			}
			report.append("\n");
		}
		writeTestResults("submission.txt");
	}

	public HashMap<String, String> sortHashMapByComparator(Map<String, String> unsortMap) {
		// Convert Map to List
		List<Map.Entry <String, String>> list = 
			new LinkedList<Map.Entry<String, String>>(unsortMap.entrySet());
		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
			public int compare(Map.Entry<String, String> o1,
					Map.Entry<String, String> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
		});
		// Convert sorted map back to a Map
		HashMap<String, String> sortedMap = new LinkedHashMap<String, String>();
		for (Iterator<Map.Entry<String, String>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, String> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

	public void writeTestResults(String filename){
		Writer writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "utf-8"));
			writer.write(report.toString());
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
		   try {writer.close();} catch (Exception ex) {/*ignore*/}
		}
	}
	public HashMap<String, Double> sortMapResult(Map<String, Double> unsortMap) {
		// Convert Map to List
		List<Map.Entry <String, Double>> list = 
			new LinkedList<Map.Entry<String, Double>>(unsortMap.entrySet());
		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> o1,
					Map.Entry<String, Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		// Convert sorted map back to a Map
		HashMap<String, Double> sortedMap = new LinkedHashMap<String, Double>();
		for (Iterator<Map.Entry<String, Double>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Double> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
}