package com.example.gl2ddemo.surfaceview;

import static com.example.gl2ddemo.activity.MainActivity.HEIGHT;
import static com.example.gl2ddemo.activity.MainActivity.WIDTH;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.example.gl2ddemo.R;
import com.example.gl2ddemo.graph.Desert;
import com.example.gl2ddemo.graph.TreeGroup;
import com.example.gl2ddemo.util.MatrixState;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.MotionEvent;

public class MySurfaceView extends GLSurfaceView
{
	public static final float UNIT_SIZE=1f;
	static float direction=0;//���߷���
    public static float cx=0;//�����x����
    public static float cz=15;//�����z����
    static final float DEGREE_SPAN=(float)(3.0/180.0f*Math.PI);//�����ÿ��ת���ĽǶ�
    //�߳�ѭ���ı�־λ
    boolean flag=true;
    float x;
    float y;
    float Offset=15;
	SceneRenderer mRender;
	float preX;
	float preY;
    
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		x=event.getX();
		y=event.getY();
		switch(event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				flag=true;
				new Thread()
				{
					@Override
					public void run()
					{
						while(flag)
						{
							if(x>0&&x<WIDTH/2&&y>0&&y<HEIGHT/2)
							{//��ǰ
								Offset=Offset-0.5f;
							}
							else if(x>WIDTH/2&&x<WIDTH&&y>0&&y<HEIGHT/2)
							{//���
								Offset=Offset+0.5f;
							}
							else if(x>0&&x<WIDTH/2&&y>HEIGHT/2&&y<HEIGHT)
							{
								direction=direction+DEGREE_SPAN;
							}
							else if(x>WIDTH/2&&x<WIDTH&&y>HEIGHT/2&&y<HEIGHT)
							{
								direction=direction-DEGREE_SPAN;
							}
							try
							{
								Thread.sleep(100);
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
						}
					}
				}.start();
			break;
			case MotionEvent.ACTION_UP:
				flag=false;
			break;
		}
		//�����µĹ۲�Ŀ���XZ����
		cx=(float)(Math.sin(direction)*Offset);//�۲�Ŀ���x���� 
        cz=(float)(Math.cos(direction)*Offset);//�۲�Ŀ���z����     
        
        //�����������ĳ���
        mRender.group.calculateBillboardDirection();
        
        //�����������ӵ�ľ�������
        Collections.sort(mRender.group.trees);
        //�����µ������λ��
        MatrixState.setCamera(cx,0,cz,0,0,0,0,1,0);
		return true;
	}
	
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
		int treeTextureId;
		
		TreeGroup group;
		
		Desert desert;
		int desertId;
		
		@Override
		public void onDrawFrame(GL10 gl)
		{
			//�����Ȼ�������ɫ����
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            
            MatrixState.pushMatrix();
            MatrixState.translate(0, -2, 0);
            desert.drawSelf(desertId);
            MatrixState.popMatrix();
            
            //�������
            GLES20.glEnable(GLES20.GL_BLEND);
            //���û������
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            
            MatrixState.pushMatrix();
            MatrixState.translate(0, -2, 0);

            group.drawSelf();
            
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
            GLES20.glClearColor(1.0f,1.0f,1.0f,1.0f);
            //����ȼ��
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            MatrixState.setInitStack();

            treeTextureId=initTexture(R.drawable.tree);
            
            desert=new Desert
                    (
                    	MySurfaceView.this,
                    	new float[]
        	            {
        	          		0,0, 0,6, 6,6,
        	          		6,6, 6,0, 0,0
        	            } ,
        	            30,
        	            20
                    ); 
            desertId=initTexture(R.drawable.desert); 

            group=new TreeGroup(MySurfaceView.this,treeTextureId);
            
            Collections.sort(mRender.group.trees);
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

	
}