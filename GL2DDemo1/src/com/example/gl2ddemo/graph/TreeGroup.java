package com.example.gl2ddemo.graph;

import static com.example.gl2ddemo.surfaceview.MySurfaceView.*;

import java.util.ArrayList;
import java.util.List;

import com.example.gl2ddemo.surfaceview.MySurfaceView;

/**
 * һ����
 * @author Jax
 *
 */
public class TreeGroup
{
	TextureRect rect;
	public List<Image_2D> trees=new ArrayList<Image_2D>();
	
	public TreeGroup(MySurfaceView mv)
	{
		rect=new TextureRect(mv, 3, 5, 0);
		trees.add(new Image_2D(0,0,0,rect));
		trees.add(new Image_2D(8*UNIT_SIZE,0,0,rect));
		trees.add(new Image_2D(5.7f*UNIT_SIZE,5.7f*UNIT_SIZE,0,rect));
		trees.add(new Image_2D(0,-8*UNIT_SIZE,0,rect));
		trees.add(new Image_2D(-5.7f*UNIT_SIZE,5.7f*UNIT_SIZE,0,rect));
		trees.add(new Image_2D(-8*UNIT_SIZE,0,0,rect));
		trees.add(new Image_2D(-5.7f*UNIT_SIZE,-5.7f*UNIT_SIZE,0,rect));
		trees.add(new Image_2D(0,8*UNIT_SIZE,0,rect));
		trees.add(new Image_2D(5.7f*UNIT_SIZE,-5.7f*UNIT_SIZE,0,rect));
	}
	public void calculateBillboardDirection()
    {
    	//�����б���ÿ����ľ�ĳ���
    	for(int i=0;i<trees.size();i++)
    	{
    		trees.get(i).calculateBillboardDirection();
    	}
    }
    
    public void drawSelf(int texId)
    {//�����б��е�ÿ����ľ
    	for(int i=0;i<trees.size();i++)
    	{
    		trees.get(i).drawSelf(texId);
    	}
    }
}