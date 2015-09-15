package com.example.gl2ddemo.surfaceview;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.example.gl2ddemo.R;
import com.example.gl2ddemo.graph.three_d.Particle;
import com.example.gl2ddemo.graph.two_d.CloudGroup;
import com.example.gl2ddemo.graph.two_d.Image_2D;
import com.example.gl2ddemo.graph.two_d.TextureRect;
import com.example.gl2ddemo.util.MatrixState;
import com.example.gl2ddemo.util.TextureUtil;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

public class MySurfaceView extends GLSurfaceView
{
	public static final float UNIT_SIZE=1f;
	static float direction=0;//���߷���
    public static float cx=0;//�����x����
    public static float cz=30;//�����z����
    
    //�߽�
    public static int SURFACE_BORDER_X=150;
    public static int SURFACE_BORDER_Y=200;
    public static int SURFACE_BORDER_Z=30;
    
	SceneRenderer mRender;
	Context mContext;
	
	public MySurfaceView(Context context)
	{
		super(context);
		mContext=context;
		this.setEGLContextClientVersion(2); //����ʹ��OPENGL ES2.0
        mRender = new SceneRenderer();	//����������Ⱦ��
        setRenderer(mRender);				//������Ⱦ��		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//������ȾģʽΪ������Ⱦ 
	}
	
	private class SceneRenderer implements GLSurfaceView.Renderer 
    {
		Image_2D background;
		int backgroundTextureId;
		
		CloudGroup group;
		
		MoveCloudThread moveThread;
		
//		Particle grainForDraw;
		
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

            group.drawSelf();
            
            MatrixState.popMatrix();
            //�رջ��
            GLES20.glDisable(GLES20.GL_BLEND);    
            
            //�����ֳ�
            MatrixState.pushMatrix();
            //��������ϵͳ
//            grainForDraw.drawSelf();
            //�ָ��ֳ�
            MatrixState.popMatrix();
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
            
            backgroundTextureId=TextureUtil.initTexture(R.drawable.background,mContext);
            background=new Image_2D(0, -50, 0, new TextureRect(MySurfaceView.this, 200, 100,-100),backgroundTextureId);
            group=new CloudGroup(mContext,MySurfaceView.this);
            
            moveThread=new MoveCloudThread();
            moveThread.start();
            
//            grainForDraw=new Particle(MySurfaceView.this,
//        			3,		//��Ĵ�С
//        			8000,	//��ĸ���
//        			true
//        			);
		}
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