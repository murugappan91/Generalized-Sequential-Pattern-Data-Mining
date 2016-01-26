package com.spring.cs583.algorithm;
import java.util.*;
import java.io.Serializable;


public class Pattern implements Serializable{

	
	private static final long serialVersionUID = 8041051556121532762L;
	private Integer count;
	private ArrayList<ItemSet> itemSetObj;

    protected Pattern(){    	
		 this.itemSetObj = new ArrayList<ItemSet>();
		 this.count = 0;
	 }

    protected Pattern(Pattern s){    	
		 
    	ItemSet itemset;
    	ArrayList<Integer> items;
    	
    	for(ItemSet is : s.getItemsets()){
    		
    		itemset = new ItemSet();
    		items = new ArrayList<Integer>(is.getItems());
    		
    		itemset.setItems(items);
    		this.addItemSet(itemset);    		
    	}    	
	 }
	
	/**
	 * Add an ItemSet to the Pattern	 
	 */
	public void addItemSet(ItemSet itemset){
		this.itemSetObj.add(itemset);
	}
	
	/**
	 * Returns all items in a Pattern
	 */
	public ArrayList<Integer> getAllItems(){
		
		ArrayList<Integer> allItems = new ArrayList<Integer>();
		
		for(ItemSet itemset : this.itemSetObj){
			allItems.addAll(itemset.getItems());
		}	
		
		return allItems;
	}
	
	/**
	 * Get the size of the Pattern
	 */
	public int getSize(){
		
		return this.getItemsets().size();
	}
	
	/**
	 * Get the length of the Pattern
	 */
	public int getlength(){
		
		return this.getAllItems().size();
	}
	
	/**
	 * Increment Pattern count by 1
	 */
	public void incrementCount(){
		this.count += 1;
	}
	
	/**
	 * Gets the minimum MIS item in a given pattern
	 * 
	 */
	public Float getMinMIS(ArrayList<Item> allItems){
		
		Float minMIS = allItems.get(allItems.size() - 1).getMinItemSupport();
		Float tmp = 0.0f;
		
		for(Integer item : this.getAllItems()){
			
			if((tmp = MSGSP.retrieveByItemNum(item, allItems).getMinItemSupport()) < minMIS)
				minMIS = tmp;			
		}	
		
		return minMIS;
	}
	
	
	
	/**
	 * Checks if this Pattern contains another Pattern
	 *
	 */
	public boolean contains(Pattern subpattern){
		
		if(subpattern.getItemsets().size() > this.getItemsets().size())
			return false;
		
		int count = 0;
		
		for(int i=0, j=0; j < this.getItemsets().size() && i < subpattern.getItemsets().size(); j++ ){
			
			if(this.getItemsets().get(j).contains(subpattern.getItemsets().get(i))){
				count = count + 1;
				i++;
			}
		}
		
		if(count == subpattern.getItemsets().size())		
			return true;
		else
			return false;
	}
	
	
	/**
	 * Returns the list of all distinct items
	 */
	public ArrayList<Integer> getAllDistinctItems() {
		
		ArrayList<Integer> allItems = new ArrayList<Integer>();
		
		for(ItemSet itemset : this.itemSetObj){
			allItems.addAll(itemset.getItems());
		}	
		
		//removing duplicates 
		return new ArrayList<Integer>(new HashSet<Integer>(allItems));
		
	}
	
	/**
	 * Check if the first item having the lowest minimum item support
	 */
	public boolean isFirstItemLowestMIS(ArrayList<Item> allItems) {
		
		boolean isFirstItemLowestMIS = false;
		
		if(this.getAllItems().size() <= 0)
			return isFirstItemLowestMIS;
		
		Integer firstItemNo = this.getAllItems().get(0);
		
		Item firstItem = MSGSP.retrieveByItemNum(firstItemNo, allItems);
		Float minMISVal = MSGSP.retrieveByItemNum(allItems.get(allItems.size()-1).getItemNo(), allItems).getMinItemSupport();
		Float tmpMISVal = 0.0f;
		
		for(int i=1; i < this.getAllItems().size(); i++){
			if(minMISVal > (tmpMISVal = MSGSP.retrieveByItemNum(this.getAllItems().get(i), allItems).getMinItemSupport())){
				minMISVal = tmpMISVal;
			}
		}
		
		if(firstItem.getMinItemSupport() < minMISVal){
			isFirstItemLowestMIS = true;
		}
		
		return isFirstItemLowestMIS;
	}
	
	/**
	 * Check if the last item having the lowest minimum item support
	 * 
	 */
	public boolean isLastItemLowestMIS(ArrayList<Item> allItems) {
		
		boolean isLastItemLowestMIS = false;
		
		if(this.getAllItems().size() <= 0)
			return isLastItemLowestMIS;
		
		Integer lastItemNo = this.getAllItems().get(this.getAllItems().size() - 1);
		
		Item lastItem = MSGSP.retrieveByItemNum(lastItemNo, allItems);
		Float minMISVal = MSGSP.retrieveByItemNum(allItems.get(allItems.size()-1).getItemNo(), allItems).getMinItemSupport();
		Float tmpMISVal = 0.0f;
		
		for(int i=0; i < this.getAllItems().size() - 1; i++){
			if(minMISVal > (tmpMISVal = MSGSP.retrieveByItemNum(this.getAllItems().get(i), allItems).getMinItemSupport())){
				minMISVal = tmpMISVal;
			}
		}
		
		if(lastItem.getMinItemSupport() < minMISVal){
			isLastItemLowestMIS = true;
		}
		
		
		return isLastItemLowestMIS;		
	}
	
	/**
	 * Checks if the last item in an itemset is a single item
	 */
	public boolean isSeperateItemSet(ArrayList<Integer> tmpS2) {
		
		boolean isSeperateItemSet = false;
		
		ItemSet lastItemSet = this.getItemsets().get(this.getItemsets().size() - 1);
		
		if(lastItemSet.getItems().size() == 1)
			isSeperateItemSet = true;
		else 
			isSeperateItemSet = false;
		
		return isSeperateItemSet;		
	}
	
	/**
	 * returns pattern last item
	 */
	public Integer getLastItem() {		
		return this.getAllItems().get(this.getAllItems().size()-1);
	}
	
	/**
	 * gets the minimum MIS item
	 */
	public int getMinMISItem(ArrayList<Item> allItems) {
		
		Float tmp,leastMIS = allItems.get(allItems.size() - 1).getMinItemSupport();
		int MinMISItem = 0;
		tmp= 0.0f;
		for(Integer item : this.getAllDistinctItems()){
			
			if((tmp = MSGSP.retrieveByItemNum(item, allItems).getMinItemSupport()) < leastMIS){
				leastMIS = tmp;	
				MinMISItem = item;
			}
		}	
		
		return MinMISItem;
	}
	
	/**
	 * Checks if this Pattern contains same another pattern
	 */
	public boolean isEqualTo(Pattern subpattern){
		
		if(subpattern.getItemsets().size() != this.getItemsets().size())
			return false;
		
		int count = 0;
		
		for(int i=0; i < this.getItemsets().size(); i++ ){
			
			if(this.getItemsets().get(i).isEqualTo(subpattern.getItemsets().get(i))){
				count = count + 1;
			}
		}
		
		if(count == subpattern.getItemsets().size())		
			return true;
		else
			return false;
	}
	
	public Integer getCount()
	{
		return this.count;
	}
	public void setCount(Integer _count)
	{
		this.count = _count;
	}
	public ArrayList<ItemSet> getItemsets()
	{
		return this.itemSetObj;
	}
	public void setItemsets(ArrayList<ItemSet> value) 
	{	
		this.itemSetObj = value;		
	}	
	
	
	
	
}
