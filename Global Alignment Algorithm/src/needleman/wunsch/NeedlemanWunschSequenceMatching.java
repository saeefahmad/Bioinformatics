package needleman.wunsch;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public class NeedlemanWunschSequenceMatching {
    public static void main(String [] args) throws IOException {  
    	String txt1 = readFile("A.txt"); //"GGACGTACG"
    	String txt2 = readFile("B.txt"); //"TACGGGTAT"
    	// String txt3 = readFile("C.txt");
    	int[][] dynamicTable = new int[txt1.length() + 1][txt2.length() + 1];
    	String[][] dynamicPointer = new String[txt1.length() + 1][txt2.length() + 1];
    	dynamicTable[0][0] = 0;
    	for (int i=0; i<=txt1.length(); i++) {
    		dynamicTable[i][0] = (-1)*i;
    	}
    	for (int j=0; j<=txt2.length(); j++ ) {
    		dynamicTable[0][j] = (-1)*j;
    	}
    	dynamicPointer[0][0] = null;
    	for (int i=1; i<=txt1.length(); i++ ) {
    		dynamicPointer[i][0] = "up";
    	}
    	for (int j=1; j<=txt2.length(); j++ ) {
    		dynamicPointer[0][j] = "left";
    	}
    	
    	int sigma = 0;
    	for (int i=1; i<dynamicTable.length; i++ ) { 
    		for (int j=1; j<dynamicTable[0].length; j++ ) {
    			if(txt1.charAt(i-1) == txt2.charAt(j-1)){
    				sigma = 1;
    			} else {
    				sigma = -1;
    			}
    			
    			int case1 = dynamicTable[i][j-1]-1; // left
    			int case2 = dynamicTable[i-1][j]-1; // up
    			int case3 = dynamicTable[i-1][j-1]+sigma; // diagonal
    			if (txt1.charAt(i-1) == txt2.charAt(j-1)) {
    				dynamicTable[i][j] = case3;
					dynamicPointer[i][j] = "diag";
    			} else if(case1 > case2) {
    				if(case1 > case3){
    					// case1 is the greatest 
    					dynamicTable[i][j] = case1;
    					dynamicPointer[i][j] = "left";
    				} else if(case1 == case3) {
    					// both case1 & case3 are the greatest
    					dynamicTable[i][j] = case3; // or case1;
    					dynamicPointer[i][j] = "diag:left";
    				} else {
    					// case 3 is the greatest
    					dynamicTable[i][j] = case3;
    					dynamicPointer[i][j] = "diag";
    				}
    			} else if (case1 == case2 && case1 > case3) {
    				//both case1 & case2 are the greatest
    				dynamicTable[i][j] = case1; // or case2;
					dynamicPointer[i][j] = "left:up";
    			} else if (case1 == case2 && case2 == case3) {
    				//all case1, case2 & case3 are same
    				dynamicTable[i][j] = case3; // or case2; or case2;
					dynamicPointer[i][j] = "diag:up:left";
    			} else {
    				if(case2 > case3) {
    					//case2 is the greatest
    					dynamicTable[i][j] = case2;
    					dynamicPointer[i][j] = "up";
    				} else if (case2 == case3) {
    					//both case2 & case3 are the greatest
    					dynamicTable[i][j] = case3; // or case2
    					dynamicPointer[i][j] = "diag:up";
    				} else {
    					//case3 is the greatest 
    					dynamicTable[i][j] = case3;
    					dynamicPointer[i][j] = "diag";
    				}
    			}
        	}
    	}
    	
    	String backTrackString1 = "";
    	String backTrackString2 = "";
    	String backTrackMatchsigns = ""; 
    	int m = txt1.length();
    	int n = txt2.length();
    	while (m>0 || n>0) {
    		String[] splitDynPtr = dynamicPointer[m][n].split(":");
    		if (splitDynPtr[0].equals("diag")) {
    			backTrackString1 += txt1.charAt(m-1);
    			backTrackString2 += txt2.charAt(n-1);
    			if (txt1.charAt(m-1) == txt2.charAt(n-1) && txt1.charAt(m-1) != 0 && txt2.charAt(n-1) != 0) {
    				backTrackMatchsigns += "|";
    			} else {
    				backTrackMatchsigns += ".";
    			}
    			m = m-1;
    			n = n-1;
        	} else if (splitDynPtr[0].equals("left")) {
    			backTrackString1 += "-";
    			backTrackString2 += txt2.charAt(n-1);
    			backTrackMatchsigns += " ";
    			n = n-1;
    		} else if (splitDynPtr[0].equals("up")) {
    			backTrackString1 += txt1.charAt(m-1);
    			backTrackString2 += "-";
    			backTrackMatchsigns += " ";
    			m = m-1;
    		} else {
    			break;
    		}
    	}
    	
    	String revBackTrackStr1 = new StringBuffer(backTrackString1).reverse().toString();
    	String revBackTrackStr2 = new StringBuffer(backTrackString2).reverse().toString();
    	String revBackTrackMatch = new StringBuffer(backTrackMatchsigns).reverse().toString();
    	
    	int l = 50;
    	int cnt = 0;
    	if(revBackTrackStr1.length()%l != 0) {
    		cnt = revBackTrackStr1.length()/l+1; 
    	} else {
    		cnt = revBackTrackStr1.length()/l;
    	}
    	PrintWriter writer = new PrintWriter("alignment.txt", "UTF-8");
    	for (int k = 0; k<cnt; k++) {
			if (k == cnt-1) {
				int q = revBackTrackStr1.length()-k*l; 
				
				writer.println(revBackTrackStr1.substring((k)*l, (k)*l+q));
				writer.println(revBackTrackMatch.substring((k)*l, (k)*l+q));
				writer.println(revBackTrackStr2.substring((k)*l, (k)*l+q));
				writer.println("\n\n");
			} else {			
				writer.println(revBackTrackStr1.substring((k)*l, (k)*l+l));
				writer.println(revBackTrackMatch.substring((k)*l, (k)*l+l));
				writer.println(revBackTrackStr2.substring((k)*l, (k)*l+l));
				writer.println("\n\n");
			}
    	}
    	writer.close();

    	int k = cnt-1;
    	int q = revBackTrackStr1.length()-k*l;  
    	System.out.println("Alignment Score: "+dynamicTable[txt1.length()][txt2.length()]);   
    	System.out.println("Sequence alignment is generated. Please check \'alignment.txt\' file "
    			+ "in main directory where src folder is located.");
    }

    public static String readFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }
	
	
}
