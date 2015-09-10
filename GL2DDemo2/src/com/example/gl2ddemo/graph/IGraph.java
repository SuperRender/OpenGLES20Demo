package com.example.gl2ddemo.graph;

import android.opengl.GLSurfaceView;

public interface IGraph {

	/**
	 * ��ʼ�����������
	 * @param vertices ��������
	 * @param normals ������
	 * @param texCoors ��������
	 */
	public void initVertexData();
	
	/**
	 * ��ʼ����ɫ��
	 * @param mv
	 */
	public void initShader(GLSurfaceView mv);
	
	/**
	 * ��������
	 * @param texId
	 */
	public void drawSelf(int texId);
}
