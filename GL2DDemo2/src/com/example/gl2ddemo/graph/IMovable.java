package com.example.gl2ddemo.graph;

public interface IMovable {

	/**
	 * �ƶ�����һ��λ��
	 */
	public void moveToNextPosition();
	
	/**
	 * �����ٶ�
	 * @param speed
	 */
	public void setSpeed(int speed);
	
	/**
	 * ���÷���
	 * @param isRight
	 */
	public void setDirection(boolean isRight);
}
