package com.spring.cs583.algorithm;

import java.io.Serializable;

public class Item implements Serializable{
	/**
	 *  PoJo class for items
	 */
	private static final long serialVersionUID = -5217756535200298382L;
	private Float actualSupport = 0.0f; 
	private Integer itemNo; 
	private Integer supportCount = 0;
	private Float minItemSupport = 0.0f;

	
	public Float getActualSupport()
	{
	    return this.actualSupport;
	}
	
	public void setActualSupport(Float value)
	{	    
	     this.actualSupport = value;
	}
	
	public Float getMinItemSupport()
	{
	    return this.minItemSupport;
	}
	
	public void setMinItemSupport(Float value)
	{	    
	     this.minItemSupport = value;
	}
	
	public Integer getItemNo()
	{
	    return this.itemNo;
	}
	
	public void setItemNo(Integer value)
	{	    
	     this.itemNo = value;
	}
	
	
	public Integer getSupportCount()
	{
	    return this.supportCount;
	}
	
	public void setSupportCount(Integer value)
	{	    
	     this.supportCount = value;
	}

}
