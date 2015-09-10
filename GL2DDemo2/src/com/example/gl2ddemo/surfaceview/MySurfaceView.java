package com.example.gl2ddemo.surfaceview;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.example.gl2ddemo.R;
import com.example.gl2ddemo.graph.Cloud;
import com.example.gl2ddemo.graph.CloudGroup;
import com.example.gl2ddemo.graph.Image_2D;
import com.example.gl2ddemo.graph.TextureRect;
import com.example.gl2ddemo.util.MatrixState;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

public class MySurfaceView extends GLSurfaceView
{
	public static final float UNIT_SIZE=1f;
	static float direction=0;//���߷���
    public static float cx=0;//�����x����
    public static float cz=30;//�����z����
    
	SceneRenderer mRender;
	
	public MySurfaceView(Context context)
	{
		super(context);
		this.setEGLContextClientVersion(2); //����ʹ��OPENGL ES2.0
        mRender = new SceneRenderer();	//����������Ⱦ��
        setRenderer(mRender);				//������Ⱦ��		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//������ȾģʽΪ������Ⱦ 
	}
	
	private class SceneRenderer implements GLSurfaceView.Renderer 
    {
		Cloud originalImage;
		int cloudTextureId;
		
		Image_2D background;
		int backgroundTextureId;
		
		CloudGroup group;
		
		MoveCloudThread moveThread;
		
		@Override
		public void onDrawFrame(GL10 gl)
		{
			//�����Ȼ�������ɫ����
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            
            MatrixState.pushMatrix();
            MatrixState.translate(0, -2, 0);
            background.drawSelf();
            MatrixState.popMatrix();
            
            //�������
            GLES20.glEnable(GLES20.GL_BLEND);
            //���û������
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            
            MatrixState.pushMatrix();
            MatrixState.translate(0, -2, 0);

            originalImage.drawSelf();
            group.drawSelf(cloudTextureId);
            
            MatrixState.popMatrix();
            //�رջ��
            GLES20.glDisable(GLES20.GL_BLEND);    
		}
		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height)
		{
			//�����Ӵ���С��λ�� 
        	GLES20.glViewport(0, 0, width, height); 
        	//����GLSurfaceView�Ŀ�߱�
            float ratio = (float) width / height;
            //���ô˷����������͸��ͶӰ����
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 1, 100);
            //���ô˷������������9����λ�þ���
            MatrixState.setCamera(cx,0,cz,0,0,0,0f,1.0f,0.0f);
		}
		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config)
		{
			//������Ļ����ɫRGBA
            GLES20.glClearColor(0.0f,0.0f,0.0f,1.0f);
            //����ȼ��
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            MatrixState.setInitStack();
            
            cloudTextureId=initTexture(R.drawable.cloud);
            backgroundTextureId=initTexture(R.drawable.bg);
            
            originalImage=new Cloud(0, 0, new TextureRect(MySurfaceView.this,5,5,20),cloudTextureId);
            background=new Image_2D(0, -50, 0, new TextureRect(MySurfaceView.this, 200, 100,-100),backgroundTextureId);
            group=new CloudGroup(getCloudsList(cloudTextureId));
            
            moveThread=new MoveCloudThread();
            moveThread.start();
		}
    }
	
	//���������id
	public int initTexture(int drawableId)
	{
		//��������ID
		int[] textures = new int[1];
		GLES20.glGenTextures
		(
				1,          //����������id������
				textures,   //����id������
				0           //ƫ����
		);      
		int textureId=textures[0];    
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_REPEAT);
        
        //ͨ������������ͼƬ
        InputStream is = this.getResources().openRawResource(drawableId);
        Bitmap bitmapTmp;
        try 
        {
        	bitmapTmp = BitmapFactory.decodeStream(is);
        } 
        finally 
        {
            try 
            {
                is.close();
            }
            catch(IOException e) 
            {
                e.printStackTrace();
            }
        }
        
        //ʵ�ʼ�������
        GLUtils.texImage2D
        (
        		GLES20.GL_TEXTURE_2D,   //�������ͣ���OpenGL ES�б���ΪGL10.GL_TEXTURE_2D
        		0, 					  //����Ĳ�Σ�0��ʾ����ͼ��㣬�������Ϊֱ����ͼ
        		bitmapTmp, 			  //����ͼ��
        		0					  //����߿�ߴ�
        );
        bitmapTmp.recycle(); 		  //������سɹ����ͷ�ͼƬ
        return textureId;
	}

	public List<Cloud> getCloudsList(int texId) {
		// TODO Auto-generated method stub
		TextureRect rect=new TextureRect(this, 5, 5, 15);
		List<Cloud> list=new ArrayList<Cloud>();
		//��������ٶȺ�λ��
		Random random=new Random();
		for(int i=0;i<10;i++){
			Cloud c=new Cloud(random.nextInt()%Cloud.border, i, rect,texId);
			int s;
			do{
				s=Math.abs(random.nextInt()%3);
			}while(s==0);
			c.setSpeed(s);
			list.add(c);
		}
		return list;
	}
	
	class MoveCloudThread extends Thread{
		boolean moveFlag=true;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(moveFlag){
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mRender.originalImage.moveToNextPosition();
				mRender.group.moveToNextPosition();
			}
		}
		public void stopMoving(){
			moveFlag=false;
		};
	}
	
	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		mRender.moveThread.stopMoving();
		super.onDetachedFromWindow();
	}
}