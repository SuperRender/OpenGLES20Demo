package com.example.gl2ddemo.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * һ���ƣ�����ͳһ����
 * @author Jax
 *
 */
public class CloudGroup {

	List<Cloud> imageList;
	
	public CloudGroup(){
		imageList=new ArrayList<Cloud>();
	}
	
	public CloudGroup(List<Cloud> list){
		if(list!=null){
			imageList=list;
		}else{
			imageList=new ArrayList<Cloud>();
		}
	}
	
	public void addImage(Cloud i){
		imageList.add(i);
	}
	
	public void addAllImage(List<Cloud> list){
		imageList.addAll(list);
	}
	
	public void removeImage(Cloud i){
		imageList.remove(i);
	}
	
	public void removeAllImage(){
		imageList.clear();
	}
	
	public void calculateBillboardDirection()
    {
    	//�����б���ÿ����ľ�ĳ���
    	for(int i=0;i<imageList.size();i++)
    	{
    		imageList.get(i).calculateBillboardDirection();
    	}
    }
    
    public void drawSelf(int texId)
    {//�����б��е�ÿ����ľ
    	for(int i=0;i<imageList.size();i++)
    	{
    		imageList.get(i).drawSelf();
    	}
    }
    
    public void sortList(){
    	Collections.sort(imageList);
    }
    
    public void moveToNextPosition(){
    	for(Cloud c:imageList){
    		c.moveToNextPosition();
    	}
    }
}
