package com.spring.cs583.algorithm;

import java.io.Serializable;
import java.util.*;
public class ItemSet implements Serializable {

    /**
	 * Itemset class used to hold items
	 */
	private static final long serialVersionUID = 6072438989863008728L;
	ArrayList<Integer> items;
	
	
    protected ItemSet()
    {    	
    	this.items = new ArrayList<Integer>();
    }  
    protected ItemSet(ArrayList<Integer> fullItemSet)
    {    	
    this.items = fullItemSet;
    }
    
    //Function adds an item to existing list
	public void addItem(Integer itemNo)
	{
		this.items.add(itemNo);
	}
	public ArrayList<Integer> getItems()
	{	
		return this.items;
	}
    public void setItems(ArrayList<Integer> _items)
    {
		this.items = _items;		
	}
	
    
    /**
     * Function checks if two itemsets are equal or not
     * */
	public boolean isEqualTo(ItemSet subItemSet)
	{
					
		if(subItemSet.getItems().size() != this.getItems().size())
			return false;
		
		int count = 0;
		
		for(int i=0; i < this.getItems().size(); i++ ){
			
			if(this.getItems().get(i).equals(subItemSet.getItems().get(i))){
				count = count + 1;
			}
		}
		
		if(count == subItemSet.getItems().size())		
			return true;
		else
			return false;
	}
	
	/**
	 * This class checks if this itemset contains sub-itemset
	 * */
	public boolean contains(ItemSet subItemSet){
					
		if(subItemSet.getItems().size() > this.getItems().size())
			return false;
		
		int count = 0;
		
		for(int i=0, j=0; j < this.getItems().size() && i < subItemSet.getItems().size(); j++ ){
			
			if(this.getItems().get(j).equals(subItemSet.getItems().get(i))){
				count = count + 1;
				i++;
			}
		}
		
		if(count == subItemSet.getItems().size())		
			return true;
		else
			return false;
	}
      

}
