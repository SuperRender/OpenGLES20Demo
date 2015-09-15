uniform mat4 uMVPMatrix; //�ܱ任����
uniform float uPointSize;//��ߴ�
uniform float uTime;
attribute vec3 aVelocity;  //�����ٶ�
void main()     
{
   float currTime=mod(uTime,6);
   float px=aVelocity.x;
   float py=aVelocity.y-currTime*60;
   float pz=aVelocity.z;
   //�����ܱ任�������˴λ��ƴ˶���λ��                         		
   gl_Position = uMVPMatrix * vec4(px,py,pz,1); 
   //�������ӳߴ�
   gl_PointSize=uPointSize;  
}