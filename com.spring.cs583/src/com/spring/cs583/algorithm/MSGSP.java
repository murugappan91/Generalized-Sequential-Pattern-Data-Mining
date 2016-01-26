package com.spring.cs583.algorithm;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


class MSGSP {

	
	private ArrayList<Item> allItems;
	private String dataFilePath;
	private float SDC;
	BufferedReader nextSeqReader;
	
	protected MSGSP (ArrayList<Item> allItems, String dataFilePath, float sdc)
    {
    	this.allItems = allItems;
    	this.dataFilePath = dataFilePath;
    	this.SDC = sdc;
    }
	
	/***
	 * Code to read data and parameter text file 
	 * and get support differential constraint input from parameter file 
	 */
	
	public static float getSuppDiffConst (String parameterFilePath)
	{
		float SuppDiffConst=0;
		BufferedReader  readSuppDiffConst;
		try
		{
			readSuppDiffConst=new BufferedReader(new FileReader(parameterFilePath));
			System.out.println("\nReading Parameter file for Support Differential Constraint");
			String curReadLine;
			while((curReadLine = readSuppDiffConst.readLine()) != null) {
				
				if(curReadLine.contains("SDC")){

					SuppDiffConst = Float.parseFloat(curReadLine.substring(curReadLine.indexOf('=') + 1).trim());
					System.out.println("SDC value read:"+SuppDiffConst);
				}			
			}
			readSuppDiffConst.close();	
		}
		catch (FileNotFoundException ex) 
		{
			System.out.println("\nParameter File not found error encountered!!!");	
		}
		catch (Exception ex)
		{
			System.out.println("\nSupport Differential Constraint not found in Parameter File!!!");
		}	
		
		
		return SuppDiffConst;
		
	}
	
	/**
	 * Function to retrieve MIS values from parameter file: retrieveMISval 
	 * */
	public static ArrayList<Item> retrieveMISval(String parameterFilePath){

		ArrayList<Item> items = new ArrayList<Item>();
		BufferedReader MISreader;	
	
		
		try {
			
			MISreader = new BufferedReader(new FileReader(parameterFilePath));
			String curReadLine;
			Item tempdata;
			System.out.println("Reading MIS values for each data item in Parameter File");
			while ((curReadLine = MISreader.readLine()) != null) {
				
				if(curReadLine.contains("MIS")){
					String tempLine = curReadLine;
					tempdata = new Item();
					
					tempdata.setItemNo(Integer.parseInt(tempLine.substring(tempLine.indexOf('(') + 1, tempLine.indexOf(')'))));
					tempLine = curReadLine;
					tempdata.setMinItemSupport(Float.parseFloat(tempLine.substring(tempLine.indexOf('=') + 1).trim()));
					items.add(tempdata);					
				} 				
			}
			
			MISreader.close();
			
		} 
		catch (FileNotFoundException ex) 
		{
			System.out.println("\nData File not found error encountered!!!");
			
		} 
		catch (Exception ex)
		{
			System.out.println("\nData File could not be read!!!");
		}	
		
		return items;
	}
	
	/**
	 * Function to read next data sequence from Data File
	 * */
	
	
	
	protected Pattern fetchNextSeq (boolean checkHead, String inputDataFilePath)
	{
	Integer headcount=0;
	ArrayList<Integer> items;
	ItemSet itemset;
	String curReadLine;
	Pattern patternObj = new Pattern();
	
	try {	
	
		if(checkHead){
			headcount = 0;
			nextSeqReader = new BufferedReader(new FileReader(inputDataFilePath));
		}
	
		if ((curReadLine = nextSeqReader.readLine()) != null) {
			
			patternObj = new Pattern();
			
			curReadLine = curReadLine.replace("<", "");
			curReadLine = curReadLine.replace(">", "");
			curReadLine = curReadLine.replace("{", "");
			
			String[] splitIndivItemSets = curReadLine.split("}");
							
			for(String tempItemSet : splitIndivItemSets)
			{
				String[] splitItems = tempItemSet.split(",");
				items = new ArrayList<Integer>();
				itemset = new ItemSet();
		
				for(String tempItem : splitItems)
				{
					items.add(Integer.parseInt(tempItem.trim()));						
				}
				
				itemset.setItems(items);
				patternObj.addItemSet(itemset);
			}
		 }
		else{
			nextSeqReader.close();
			patternObj = null;
		}
		
		headcount +=1;
			
	} 
	catch (FileNotFoundException ex) 
	{
		System.out.println("\nData File not found error encountered!!!");
		
	} 
	catch (Exception ex)
	{
		System.out.println("\nData File could not be read!!!");
	}	
	
	return patternObj;
}
	

	/**
	 * Function to return all pattern of a specific size
	 */
	public static ArrayList<Pattern> computeSizeOfPattern(ArrayList<Pattern> frequentPatterns, int size) {
		
		ArrayList<Pattern> TempPatternSize = new ArrayList<Pattern>();
		
		for(Pattern seq : frequentPatterns){		
			if(seq.getAllItems().size() == size){
				TempPatternSize.add(seq);
			}
		}
		
		return TempPatternSize;
	}
	
	
	/**
	 * get the Item from Item number
	 * @param itemNo - Item number
	 * @param allItems list of all Items
	 * @return Item
	 */
	protected static Item retrieveByItemNum(Integer itemNo, ArrayList<Item> FullPattern){
			
		Item TempItem = null;
			
		for(Item temp : FullPattern){
			
			if(temp.getItemNo().equals(itemNo)){
				TempItem = temp;
				break;
			}
		}
			
		return TempItem;
	}
	
	/**
	 * Function returns support difference of a sequence
	 */
	public static float getSupportDifference(ItemSet is, ArrayList<Item> allItems) {
		
		
		Float tmp = 0.0f,highestMISfromData = 0.0f,leastMISfromData;
		leastMISfromData=allItems.get(allItems.size() - 1).getMinItemSupport();
		
		for(Integer item : is.getItems()){
			if((tmp = retrieveByItemNum(item, allItems).getActualSupport()) < leastMISfromData)
				leastMISfromData = tmp;
			
			if((tmp = retrieveByItemNum(item, allItems).getActualSupport()) > highestMISfromData)
				highestMISfromData = tmp;
		}
		
		return (highestMISfromData - leastMISfromData);		
	}
	
	/**
	 * Function return all k-1 candidate sequences
	 */
	public static ArrayList<ItemSet> retrieveSubsequence (Pattern seq){		
		
		int n = seq.getAllItems().size();
		int[] patternPointer = new int[n];
		ArrayList<ItemSet> subSequences = new ArrayList<ItemSet>();
		
		for(int i=0; i<n;i++){
			patternPointer[i] = (1<<i);
		}
		
		for(int i=0;i<(1<<n);i++){
				
			ArrayList<Integer> newList = new ArrayList<Integer>();
				
			for(int j=0; j<n;j++){
				if((patternPointer[j] & i) != 0){
					newList.add(seq.getAllItems().get(j));					
				}
			}
			
			if(newList.size() == n-1){				
				ItemSet is = new ItemSet();
				is.setItems(newList);				
				subSequences.add(is);
			}			
		}
		
		return subSequences;
	}
	
	
	/**
	 * checks if a list of patterns contain a given pattern
	 */
	public static boolean isPatternContains(ArrayList<Pattern> pattern, Pattern toCheck) {

		boolean isContains = false;

		for(Pattern tempPattern : pattern){

			if(tempPattern.contains(toCheck)){
				
				isContains = true;
				break;				
			}				
		}

		return isContains;
	}	
	
	/**
	 * checks if a list of patterns contains duplicate pattern
	 * 
	 */
	public static boolean ListHas(ArrayList<Pattern> pattern, Pattern toCheck) {

		boolean contains = false;

		for(Pattern tempPattern : pattern){

			if(tempPattern.isEqualTo(toCheck)){
				
				contains = true;
				break;				
			}				
		}

		return contains;
	}	

	
	
	public static void main (String [] args)
	{
		String dataFilePath = System.getProperty("user.dir") + "/data/data.txt";
      //  System.out.println("Present Working Directory:"+System.getProperty("user.dir"));
        String parameterFilePath = System.getProperty("user.dir") + "/data/para.txt";
        String outputFilePath = System.getProperty("user.dir") + "/output/output.txt";
        MSGSP objself=new MSGSP(retrieveMISval(parameterFilePath),dataFilePath,getSuppDiffConst(parameterFilePath));
        ArrayList<Pattern> frequentPatterns = objself.Algo();

        //to print output in console:
    	ArrayList<Pattern> outputSequence = new ArrayList<Pattern>();
		
    	System.out.println("MS GSP Algorithm output");
    	try{
    		
    		File file = new File(outputFilePath);
			
			if(file.exists()){
				file.delete();
			}
			
			BufferedWriter outputData = new BufferedWriter(new FileWriter(file));
			
			for(int i=1; (outputSequence = computeSizeOfPattern(frequentPatterns, i)).size() > 0 ; i++){
				
				outputData.write("\nThe number of length " + i + " sequential patterns is " + outputSequence.size());
				outputData.write("\n");
				
				//System.out.println("");
				//System.out.println("The number of length " + i + " sequential patterns is " + outputSequence.size());
				
				for(Pattern seq : outputSequence){
					outputData.write("Pattern : <");
					//System.out.print("Pattern:  <");
			
					for(ItemSet itemset : seq.getItemsets()){
						
						outputData.write("{");
						//System.out.print("{");
						
						for(int j=0; j < itemset.getItems().size(); j++){
							
							if(j == itemset.getItems().size() - 1){
								outputData.write(itemset.getItems().get(j).toString());
								//System.out.print(itemset.getItems().get(j));
								
							}
							else{
								outputData.write(itemset.getItems().get(j) + ",");
								//System.out.print(itemset.getItems().get(j) + ",");
								
							}						
						}
						
						outputData.write("}");
						//System.out.print("}");
						
					}	
					outputData.write("> Count : " + seq.getCount() + " \n");
					//System.out.print(">  Count : " + seq.getCount() + " \n");
					
				}	
			}
			
			outputData.close();
			//System.out.println("");
			//System.out.println("Algorithm Terminated!");
			
    	}
    	catch(IOException e){
    		System.out.println("File IO Error: " + e);
    	}
    	
    	
        
	}

	/**
	 * MSGSP Algorithm
	 * */
    
    public ArrayList<Pattern> Algo()
    {
     	ArrayList<Pattern> frequentSeq = new ArrayList<Pattern>();
    	ArrayList<Pattern> candidateKeyCur;
		ArrayList<Pattern> frequentK_1Clone;
    	ArrayList<Integer> seedSet;
    	ArrayList<Pattern> frequentK_1;
    	ArrayList<Pattern> frequentSet1;

    	sortOnMIS();		// Line 1 : M
    	seedSet = initPass();		// Line 2 : init-Pass
    	frequentSet1 = frequentSet_1(seedSet);		// Line 3 : F1
    	
    	frequentSeq.addAll(frequentSet1);
    	
    	for(int k=2; (frequentK_1 = computeSizeOfPattern(frequentSeq, k-1)).size() > 0 ; k++){	
    		
    		candidateKeyCur = new ArrayList<Pattern>();
    		
    		if(k==2){
				candidateKeyCur = level2CandidateGen(seedSet, frequentSeq);				
			}
			else{
				frequentK_1Clone = (ArrayList<Pattern>) deepCopy(frequentK_1);
				
				candidateKeyCur = candidateGenAbove3(frequentK_1Clone, k-1);
			}
    		    		   		
    		Pattern seq = fetchNextSeq(true, this.dataFilePath);

			int seqCount = 0;
			
			while(seq != null){
				
				seqCount = seqCount + 1;
												
				for(Pattern checkCandidate : candidateKeyCur){ 
				
					if(seq.contains(checkCandidate)){
						checkCandidate.incrementCount();
					}
				}		
				
				seq = fetchNextSeq(false, null);
			}
			
			ArrayList<Pattern> frequentSet_k = new ArrayList<Pattern>();
			
			for(Pattern tmpseq : candidateKeyCur){
				
				if(((float)tmpseq.getCount()/seqCount) >= tmpseq.getMinMIS(this.allItems))
					frequentSet_k.add(tmpseq);
			}
			
    		frequentSeq.addAll(frequentSet_k);     
    	}
    	

    	return frequentSeq;
    
    }
    
    

	/**
     * Initial pass over the data to generate the seed set L
     */
    public ArrayList<Integer> initPass(){		
		
		Integer minSupportItem = 0;
		Float minMIS = 0.0f;
		ArrayList<Integer> seedSet = new ArrayList<Integer>(); 
		
		calculateActualItemSup();		//Compute actual item support
		
		for(Item itm : this.allItems){
			
			if(minSupportItem == 0){
				if(itm.getActualSupport() >= itm.getMinItemSupport()){
					minSupportItem = itm.getItemNo();
					minMIS = itm.getMinItemSupport();
					seedSet.add(minSupportItem);
				}		
			}
			else{
				if(itm.getActualSupport() >= minMIS){
					seedSet.add(itm.getItemNo());
				}
			}	
		}
				
		return seedSet;
	}

    /**
     * Compute the frequent itemset 1 from the seed set L and 
     */
    public ArrayList<Pattern> frequentSet_1(ArrayList<Integer> seedSet)
    {
    	ItemSet tempItemSet = new ItemSet();	
    	Pattern tempPat = new Pattern();
    	Item tempItem = new Item();
		ArrayList<Pattern> frequentset1 = new ArrayList<Pattern>();
		
		for(Integer item : seedSet){
		
			tempItem = retrieveByItemNum(item, this.allItems);
			
			if(tempItem.getActualSupport() >= tempItem.getMinItemSupport()){
				
				tempItemSet = new ItemSet();
				tempItemSet.addItem(item);						
				
				tempPat = new Pattern();
				tempPat.addItemSet(tempItemSet);
				
				frequentset1.add(tempPat);				
			}			
		}
		
		//Compute frequent 1 itemset count
		
		Pattern patternObj; 
		Integer seqCount = 0;
		
		patternObj = fetchNextSeq(true, dataFilePath);

		while(patternObj != null){	
				
			seqCount = seqCount + 1;
				
			for(Pattern checkCandidate : frequentset1){ 
				
				if(checkCandidate != null && patternObj != null){
					
					if(patternObj.getAllItems().containsAll(checkCandidate.getAllItems())){
						
						checkCandidate.incrementCount();
					}					
				}
			}
			
			patternObj = fetchNextSeq(false, null);
		}
		
		return frequentset1;
	}
    
    

    
	/**
     * Function: level2-candidate-gen-SPM
     */
    public ArrayList<Pattern> level2CandidateGen(ArrayList<Integer> seedSet, ArrayList<Pattern> frequentSeq){
    	
    	ArrayList<Pattern> level2Candidates = new ArrayList<Pattern>();
		
        for (int a=0; a< seedSet.size();a++){    
        	
        	 Item tmpItem1 = retrieveByItemNum(seedSet.get(a), this.allItems);
        	 if(tmpItem1.getActualSupport() >= tmpItem1.getMinItemSupport()){
        	 
             for (int b=a+1;b< seedSet.size();b++){
            	 
	            	
	            	 Item tmpItem2 = retrieveByItemNum(seedSet.get(b), this.allItems);   
	            	 	
	            	float supportDifference =(tmpItem2.getActualSupport() - tmpItem1.getActualSupport());
	            	 
	            	if( tmpItem2.getActualSupport() >= tmpItem1.getMinItemSupport() && Math.abs(supportDifference) <= this.SDC)  {
	            		
	            		ItemSet tempItemSet3 = new ItemSet();    
   	                    Pattern tempPattern2 = new Pattern(); 
   	                    
	            		if(tmpItem1.getMinItemSupport() == tmpItem2.getMinItemSupport()){
	            			if(tmpItem1.getItemNo() < tmpItem2.getItemNo()){
	            			 tempItemSet3.addItem(tmpItem1.getItemNo()); 
	   		                 tempItemSet3.addItem(tmpItem2.getItemNo());    
	   		                }
	            			else{
	            				tempItemSet3.addItem(tmpItem2.getItemNo());
	            				tempItemSet3.addItem(tmpItem1.getItemNo());
	            			}
	            		}
	            		else{
	            			 tempItemSet3.addItem(tmpItem1.getItemNo()); 
	   		                 tempItemSet3.addItem(tmpItem2.getItemNo());  
	   		                 
	            		}
	            		
	            		tempPattern2.addItemSet(tempItemSet3);  
   		                level2Candidates.add(tempPattern2); // To generate : <{x,y}>
   		                
   		                
	           		 	 ItemSet tempItemSet1 = new ItemSet();                  		 
	                	 Pattern tempPattern1 = new Pattern();   
	                 
		                 tempItemSet1.addItem(tmpItem1.getItemNo()); 	                     
		                 tempPattern1.addItemSet(tempItemSet1);  
		                 
		                 ItemSet tempItemSet2 = new ItemSet();         
		                 tempItemSet2.addItem(tmpItem2.getItemNo()); 
		                 tempPattern1.addItemSet(tempItemSet2);
		                 
		                 level2Candidates.add(tempPattern1);   // To generate : <{x},{y}> 
		                 
		                 
		                 
		                 // To generate <{y}{x}>
		                 
		                 ItemSet tempIS1 = new ItemSet();
		                 Pattern tempP1 = new Pattern();
		                 
		                 tempIS1.addItem(tmpItem2.getItemNo());
		                 tempP1.addItemSet(tempIS1);
		                 
		                 ItemSet tempIS2 = new ItemSet();
		                 tempIS2.addItem(tmpItem1.getItemNo());
		                 tempP1.addItemSet(tempIS2);
		                 
		                 level2Candidates.add(tempP1);
		                 
		                 //To generate <{y,x}>
		                 
//		                 ItemSet tempIS3 = new ItemSet();
//		                 Pattern tempP2 = new Pattern();
//		                 
//		                 tempIS3.addItem(tmpItem2.getItemNo());
//		                 tempIS3.addItem(tmpItem1.getItemNo());
//		                 
//		                 tempP2.addItemSet(tempIS3);
//		                 level2Candidates.add(tempP2);
//		                 
		                 
	            	 }
	             
             }   
        }
        }
        

        level2Candidates = pruneStep(level2Candidates,frequentSeq);
        
    	return level2Candidates;
    }
    
    /**
     * Function to generate candidates of level 3 and above
     * */
public ArrayList<Pattern> candidateGenAbove3(ArrayList<Pattern> frequentK_1, Integer size){
    	
	ArrayList<Integer> tempPattern1 = new ArrayList<Integer>();
	ArrayList<Integer> tempPattern2 = new ArrayList<Integer>();	
	ArrayList<Pattern> tempCandidatePattern = new ArrayList<Pattern>();
    Pattern candidate = new Pattern();
		
		
		for(int a=0; a < frequentK_1.size(); a++ ){
			
			Pattern pattern1 = frequentK_1.get(a);
			for(int b=0; b < frequentK_1.size(); b++){
			
					Pattern pattern2 = frequentK_1.get(b);
					
					tempPattern1 = new ArrayList<Integer>(pattern1.getAllItems());
					tempPattern2 = new ArrayList<Integer>(pattern2.getAllItems());
					
					if(pattern1.isFirstItemLowestMIS(this.allItems))
					{						
						
						tempCandidatePattern.addAll(joinFirstLeast(pattern1, pattern2, tempPattern1, tempPattern2, size));						
					}
					else if(pattern2.isLastItemLowestMIS(this.allItems))
					{
						System.out.println("Last item");
						tempCandidatePattern.addAll(joinLastLeast(pattern1, pattern2, tempPattern1, tempPattern2, size));	
					}
					else{	
						
						if((candidate = joinStep(pattern1, pattern2)) != null)
							tempCandidatePattern.add(candidate);
					}						
								
			}			
		}
		

    	
    	return pruneStep(tempCandidatePattern, frequentK_1);
    }
    
    /**
     * Function to do prune step
     */
    private ArrayList<Pattern> pruneStep(ArrayList<Pattern> tempCandidatePattern, ArrayList<Pattern> frequentK_1) {

    	int tmpsize = 0;
    	ArrayList<Pattern> subSeq =  new ArrayList<Pattern>();  
    	ArrayList<Pattern> prunedPattern = new ArrayList<Pattern>();
    	
    	if(frequentK_1.size() > 0){
    		tmpsize = frequentK_1.get(0).getAllItems().size();
    	}
    	
    	for(Pattern seq : tempCandidatePattern){
    		    		
    		if(seq.getAllDistinctItems().contains(this.allItems.get(0))){
    			
    			ArrayList<ItemSet> seeds = retrieveSubsequence(seq);     			
    			
    			for(int i=0; i < seeds.size(); i++){
    				
    				int pointer = 0;
    				
    				if(i != pointer){
    				
	    				Pattern newseq = new Pattern();
	    				
	    				while(newseq.getAllItems().size() < tmpsize && pointer < seeds.size()){
	    					
	    					ItemSet is = new ItemSet();
	    					is.setItems(new ArrayList<Integer>(seeds.get(pointer).getItems()));
	    					newseq.addItemSet(is);   
	    					
	    					pointer = pointer + 1;
	    					
	    					if(newseq.getAllItems().size() == tmpsize){
	    						subSeq.add(newseq);
	    					}
	    				} 
    				}
    			}

    			int count = 0;
    			
    			for(int j=0; j<subSeq.size() ; j++){
    				
    				if(isPatternContains(frequentK_1, subSeq.get(j))){    				
    					count = count++;
    				}    				
    			}
    			
    			if(count == subSeq.size()){
    				if(!ListHas(prunedPattern, seq))
    					prunedPattern.add(seq);
    			}
    		}
    		else{
    			if(!ListHas(prunedPattern, seq))
    				if(seq.getlength() != 0)
    				   prunedPattern.add(seq);
    		}    		
    	}
    	
		return prunedPattern;
	}


    
    /**
     * Join step when the last item in the pattern has lowest minimum item support
     */
    private ArrayList<Pattern> joinLastLeast(Pattern pat1, Pattern pat2,ArrayList<Integer> tempPattern1, ArrayList<Integer> tempPattern2, int size){

    	Pattern candidateObj = new Pattern();
		ArrayList<Pattern> candidatePattern = new ArrayList<Pattern>();
		
		if(condition1R(pat1,pat2) &&  condition2R(pat1,pat2)){
		
			if(pat1.getItemsets().get(0).getItems().size() == 1){
				
				candidateObj = new Pattern();
				
				ItemSet firstItemSet = new ItemSet();
				ArrayList<Integer> newfirstItem = new ArrayList<Integer>();
				newfirstItem.add(new Integer(pat1.getAllItems().get(0)));
				
				firstItemSet.setItems(newfirstItem);
				
				candidateObj.addItemSet(firstItemSet);
				
				for(ItemSet is : pat2.getItemsets()){
					ItemSet itemSet1 = new ItemSet();
					ArrayList<Integer> Item1 = new ArrayList<Integer>(is.getItems());
					itemSet1.setItems(Item1);
					candidateObj.addItemSet(itemSet1);
				}
				
						candidatePattern.add(candidateObj);
					
				
				if((pat2.getlength() == 2 && pat2.getSize() == 2) && 
						(retrieveByItemNum(tempPattern1.get(0), this.allItems).getMinItemSupport() > retrieveByItemNum(tempPattern2.get(0), this.allItems).getMinItemSupport())) {
					
					candidateObj = new Pattern();
					
					for(int i=0; i<pat2.getItemsets().size();i++){
						ItemSet itemSet1 = new ItemSet();
						
						ArrayList<Integer> Item1 = new ArrayList<Integer>();
						
						if(i == 0){
							Item1 = new ArrayList<Integer>();
							Item1.add(new Integer(pat1.getAllItems().get(0)));
							Item1.addAll(pat2.getItemsets().get(i).getItems());
						}
						else{
							Item1 = new ArrayList<Integer>(pat2.getItemsets().get(i).getItems());
						}
							
						itemSet1.setItems(Item1);
						candidateObj.addItemSet(itemSet1);
					}
														


							candidatePattern.add(candidateObj);
			
				}					
			}
			else if(((pat2.getlength() == 2 && pat2.getSize() == 1) && 
					(retrieveByItemNum(tempPattern1.get(0), this.allItems).getMinItemSupport() > retrieveByItemNum(tempPattern2.get(0), this.allItems).getMinItemSupport())) 
					|| (pat2.getlength() > 2)){
				
				candidateObj = new Pattern();
				
				for(int i=0; i<pat2.getItemsets().size();i++){
					ItemSet itemSet1 = new ItemSet();
					
					ArrayList<Integer> Item1 = new ArrayList<Integer>();
					
					if(i == 0){
						Item1 = new ArrayList<Integer>();
						Item1.add(new Integer(pat1.getAllItems().get(0)));
						Item1.addAll(pat2.getItemsets().get(i).getItems());
					}
					else{
						Item1 = new ArrayList<Integer>(pat2.getItemsets().get(i).getItems());
					}
						
					itemSet1.setItems(Item1);
					candidateObj.addItemSet(itemSet1);
				}
													


						candidatePattern.add(candidateObj);

			}
			
		}		
		
    	return candidatePattern;
    }
    
    /**
     * Join step when the first item in the sequence has lowest minimum item support
     */
    private ArrayList<Pattern> joinFirstLeast(Pattern pat1, Pattern pat2,ArrayList<Integer> tempPattern1, ArrayList<Integer> tempPattern2, int size){

    	Pattern candidateObj = new Pattern();
		ArrayList<Pattern> candidatePattern = new ArrayList<Pattern>();
		ItemSet lastItemSet = new ItemSet();
		ArrayList<Integer> newLastItem = new ArrayList<Integer>();
		
		if(condition1(pat1,pat2) &&  condition2(pat1,pat2)){
		
			if(pat2.getItemsets().get(pat2.getItemsets().size() - 1).getItems().size() == 1){
				
				candidateObj = new Pattern();
				
				for(ItemSet is : pat1.getItemsets()){
					ItemSet newItemSet = new ItemSet();
					ArrayList<Integer> newItems = new ArrayList<Integer>(is.getItems());
					newItemSet.setItems(newItems);
					candidateObj.addItemSet(newItemSet);
				}
				
				
				newLastItem.add(new Integer(pat2.getLastItem()));

				lastItemSet.setItems(newLastItem);
				
				candidateObj.addItemSet(lastItemSet);
				
						candidatePattern.add(candidateObj);
				
				
				if((pat1.getSize() == 2 && pat1.getlength() == 2) &&
						(retrieveByItemNum(tempPattern2.get(tempPattern2.size() - 1), this.allItems).getMinItemSupport() > retrieveByItemNum(tempPattern1.get(tempPattern1.size() - 1), this.allItems).getMinItemSupport())) {
					
					candidateObj = new Pattern();
					
					for(ItemSet is : pat1.getItemsets()){
						ItemSet newItemSet = new ItemSet();
						ArrayList<Integer> newItems = new ArrayList<Integer>(is.getItems());
						newItemSet.setItems(newItems);
						candidateObj.addItemSet(newItemSet);
					}
					
					candidateObj.getItemsets().get(candidateObj.getItemsets().size() - 1).addItem(new Integer(pat2.getLastItem()));
					
							candidatePattern.add(candidateObj);
				}					
			}

			else if(((pat1.getlength() == 2 && pat1.getSize() == 1) &&
					(retrieveByItemNum(tempPattern2.get(tempPattern2.size() - 1), this.allItems).getMinItemSupport() > retrieveByItemNum(tempPattern1.get(tempPattern1.size() - 1), this.allItems).getMinItemSupport())) 
					|| (pat1.getlength() > 2)){ 
				
				candidateObj = new Pattern();
				
				for(ItemSet is : pat1.getItemsets()){
					ItemSet newItemSet = new ItemSet();
					ArrayList<Integer> newItems = new ArrayList<Integer>(is.getItems());
					newItemSet.setItems(newItems);
					candidateObj.addItemSet(newItemSet);
				}
				
				candidateObj.getItemsets().get(candidateObj.getItemsets().size() - 1).addItem(new Integer(pat2.getLastItem()));
					
						candidatePattern.add(candidateObj);
			}
			
		}		
		
    	return candidatePattern;
    }
    
    /**
     * General GSP join step
     * @return candidate
     */
     Pattern joinStep(Pattern pat1, Pattern pat2){
    	
    	 ArrayList<Integer> tempPattern1;
 		ArrayList<Integer> tempPattern2;
		Pattern candidateObj = new Pattern();
	
		
    	tempPattern1 = new ArrayList<Integer>(pat1.getAllItems());
		tempPattern2 = new ArrayList<Integer>(pat2.getAllItems());
		
		if(tempPattern1.size() > 0 && tempPattern2.size()>0){
			
			tempPattern1.remove(0);
			tempPattern2.remove(tempPattern2.size() - 1);
			
			if(tempPattern1.equals(tempPattern2)){
				
				if(pat2.isSeperateItemSet(tempPattern2)){
					
					candidateObj = new Pattern();
					
					candidateObj.setItemsets(pat1.getItemsets());
					ItemSet appendItemSet = new ItemSet();
					appendItemSet.addItem(pat2.getLastItem());
					
					candidateObj.addItemSet(appendItemSet);
					

				}
				else{
					candidateObj = new Pattern();
					
					for(Integer i=0; i < pat1.getItemsets().size(); i++){
						
						if(i == pat1.getItemsets().size() - 1){
							
							ItemSet appendItemSet = pat1.getItemsets().get(i);
							appendItemSet.addItem(pat2.getLastItem());
							
							candidateObj.addItemSet(appendItemSet);
							

						}
						else{
							candidateObj.addItemSet(pat1.getItemsets().get(i));
						}										
					}					
				}
				
			}			
		}
    	
    	return candidateObj;
    }
    
    /**
     * Condition 1 in join step
     * 
     */
    private boolean condition1(Pattern s1, Pattern s2) {
		
		boolean isCondition1 = false;
		
		ArrayList<Integer> tmpS1 = new ArrayList<Integer>(s1.getAllItems());
		ArrayList<Integer> tmpS2 = new ArrayList<Integer>(s2.getAllItems());
				
		if(tmpS1.size() > 1 && tmpS2.size() > 0){
			
			tmpS1.remove(1);
			tmpS2.remove(tmpS2.size() - 1);
			if(tmpS1.equals(tmpS2))
				isCondition1 = true;
			else
				isCondition1 = false;			
		}
		
		return isCondition1;
	}

    /**
     * Condition 2 in join step normal order
     */
	private boolean condition2(Pattern s1, Pattern s2) {
		
		boolean isCondition2 = false;
		
		ArrayList<Integer> tmpS1 = new ArrayList<Integer>(s1.getAllItems());
		ArrayList<Integer> tmpS2 = new ArrayList<Integer>(s2.getAllItems());
		
		if(tmpS1.size() > 0 && tmpS2.size() > 0){
		
			//if(misValues.get(tmpS2.get(tmpS2.size() - 1)).getMinItemSupport() > misValues.get(tmpS1.get(0)).getMinItemSupport())
			if(retrieveByItemNum(tmpS2.get(tmpS2.size() - 1), this.allItems).getMinItemSupport() > retrieveByItemNum(tmpS1.get(0), this.allItems).getMinItemSupport())
				isCondition2 = true;
			else
				isCondition2 = false;
		}
				
		return isCondition2;
	}
	
	
	/**
     * Condition 1 in join step for reverse order
     */
    private boolean condition1R (Pattern pat1, Pattern pat2) {
                
                
                
                ArrayList<Integer> tempPattern1 = new ArrayList<Integer>(pat1.getAllItems());
                ArrayList<Integer> tempPattern2 = new ArrayList<Integer>(pat2.getAllItems());
                boolean isCondition1 = false;               
                if(tempPattern2.size() > 1 && tempPattern1.size() > 0)
                { 
                        
                        tempPattern2.remove(1);
                        tempPattern1.remove(0);
                        
                        
                        if(tempPattern1.equals(tempPattern2))
                                isCondition1 = true;
                        else
                                isCondition1 = false;                        
                }
                
                return isCondition1;
        }
    /**
     * Condition 2 in join step for reverse order
     */
        private boolean condition2R(Pattern pat1, Pattern pat2) {
                
               
                
                ArrayList<Integer> tempPattern1 = new ArrayList<Integer>(pat1.getAllItems());
                ArrayList<Integer> tempPattern2 = new ArrayList<Integer>(pat2.getAllItems());
                boolean isCondition2 = false;
                if(tempPattern1.size() > 0 && tempPattern2.size() > 0){
                
                        
                        if(retrieveByItemNum(tempPattern1.get(0), this.allItems).getMinItemSupport() > retrieveByItemNum(tempPattern2.get(tempPattern2.size() - 1), this.allItems).getMinItemSupport())
                                isCondition2 = true;
                        else
                                isCondition2 = false;
                }
                                
                return isCondition2;
                
        }

    
    /**
     * Compute the actual item support from the data
     */
    public void calculateActualItemSup(){		
		
    	Integer sequenceCount = 0;		
		
		Pattern seq = fetchNextSeq(true, dataFilePath);
		
		while(seq != null){
			
			sequenceCount = sequenceCount + 1;
			
			for(Integer item : seq.getAllDistinctItems()){
				
				for(Item itm : this.allItems){
					
					if(itm.getItemNo() == item){
						
						itm.setSupportCount(itm.getSupportCount() + 1);
					}
				}
			}
			
			seq = fetchNextSeq(false, null);
		}
		
		for(Item itm : this.allItems){
			
			itm.setActualSupport((float)itm.getSupportCount()/sequenceCount);
		}
	}
    
    /**
     * Sort Items based on minimum support values
     */
    public void sortOnMIS(){		
		
	    Collections.sort(this.allItems, new ItemSort());
	}
    
  	public class ItemSort implements Comparator<Item> {
  	    @Override
  	    public int compare(Item o1, Item o2) {
  	        return o1.getMinItemSupport().compareTo(o2.getMinItemSupport());
  	    }
  	}

  	 /**
     * This method makes a "deep copy" of any Java object it is given.
     */
     public static Object deepCopy(Object object) {
       try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(baos);
         oos.writeObject(object);
         ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
         ObjectInputStream ois = new ObjectInputStream(bais);
         return ois.readObject();
       }
       catch (Exception e) {
         e.printStackTrace();
         return null;
       }
     }
    
     }
