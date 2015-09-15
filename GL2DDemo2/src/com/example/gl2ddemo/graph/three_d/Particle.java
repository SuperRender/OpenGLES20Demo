package com.example.gl2ddemo.graph.three_d;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.example.gl2ddemo.graph.IGraph;
import com.example.gl2ddemo.util.MatrixState;
import com.example.gl2ddemo.util.ShaderUtil;
import static com.example.gl2ddemo.surfaceview.MySurfaceView.*;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

public class Particle implements IGraph{

	private FloatBuffer mVelocityBuffer;//�����ٶ����ݻ���
    float scale;	//��ߴ�
    
    String mVertexShader;	//������ɫ��    	 
    String mFragmentShader;	//ƬԪ��ɫ��
    
    int mProgram;			//�Զ�����Ⱦ������ɫ������id
    int muMVPMatrixHandle;	//�ܱ任��������   
    int uPointSizeHandle;	//����ߴ��������
    int uColorHandle;		//������ɫ��������
    int uTimeHandle;		//������ɫ��������
    int vCount=1;

    int maVelocityHandle; 	//�����ٶ��������� 
    
    //���ڼ���ʱ��
    float timeLive=0;
    long timeStamp=0;
	boolean isRandom;
	
    
    public Particle(GLSurfaceView mv,float scale,int count,boolean isRandom) {
    	this.scale=scale;
    	this.vCount=count;
    	this.isRandom=isRandom;
    	initVertexData();
    	initShader(mv);
	}
    
	@Override
	public void initVertexData() {
		float[] velocity=new float[vCount*3];
		if(isRandom){
			//������������
			for(int i=0;i<vCount;i++){
	        	velocity[i*3]=(float) (Math.random()*SURFACE_BORDER_X*3)-SURFACE_BORDER_X;
	        	velocity[i*3+1]=(float) (Math.random()*SURFACE_BORDER_Y*3-SURFACE_BORDER_Y);
	        	velocity[i*3+2]=(float)(Math.random()*SURFACE_BORDER_Z);
	        }
		}
        //���������ٶ����ݻ���
        ByteBuffer vbb = ByteBuffer.allocateDirect(velocity.length*4);
        vbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        mVelocityBuffer = vbb.asFloatBuffer();//ת��Ϊint�ͻ���
        mVelocityBuffer.put(velocity);//�򻺳����з��붥����������
        mVelocityBuffer.position(0);//���û�������ʼλ��
	}

	@Override
	public void initShader(GLSurfaceView mv) {
		//���ض�����ɫ���Ľű�����       
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex_yh.sh", mv.getResources());
        //����ƬԪ��ɫ���Ľű�����
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag_yh.sh", mv.getResources());  
        //���ڶ�����ɫ����ƬԪ��ɫ����������
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        //��ȡ�����ж����ٶ���������id  
        maVelocityHandle = GLES20.glGetAttribLocation(mProgram, "aVelocity");        
        //��ȡ�������ܱ任��������id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix"); 
        //��ȡ����ߴ��������
        uPointSizeHandle = GLES20.glGetUniformLocation(mProgram, "uPointSize"); 
        //��ȡ������ɫ��������
        uColorHandle = GLES20.glGetUniformLocation(mProgram, "uColor"); 
        //��ȡ������ɫ��������
        uTimeHandle=GLES20.glGetUniformLocation(mProgram, "uTime"); 
	}

	public void drawSelf(){
		drawSelf(0);
	}
	
	@Override
	public void drawSelf(int texId) {
		long currTimeStamp=System.nanoTime()/1000000;
    	if(currTimeStamp-timeStamp>=10){
    		timeLive+=0.02f;
    		timeStamp=currTimeStamp;
    	}
		
		//�ƶ�ʹ��ĳ����ɫ������
   	    GLES20.glUseProgram(mProgram);
        //�����ձ任��������ɫ������
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);  
        //������ߴ紫����ɫ������
        GLES20.glUniform1f(uPointSizeHandle, scale);
        //��ʱ�䴫����ɫ������
        GLES20.glUniform1f(uTimeHandle, timeLive);    
        //��������ɫ������ɫ������
        GLES20.glUniform3fv(uColorHandle, 1, new float[]{0.5647f,0.5412f,0.4784f}, 0);
        //���붥���ٶ�����    
        GLES20.glVertexAttribPointer(
        		maVelocityHandle,   
        		3, 
        		GLES20.GL_FLOAT, 
        		false,
                3*4, 
                mVelocityBuffer   
        );
        //������λ����������
        GLES20.glEnableVertexAttribArray(maVelocityHandle);         
        //���Ƶ�    
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vCount); 
	}

}
