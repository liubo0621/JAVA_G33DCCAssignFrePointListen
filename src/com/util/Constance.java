package com.util;

/**
 * @author Boris
 * @description ����
 * 2016��8��9��
 */
public class Constance {
	//���ջ�
	public static final class Reveiver{
		//���ջ�״̬
		public final static int FREE      = 1200;
		public final static int BUSY      = 1201;
		public final static int EXCEPTION = 1202;
	}
	
	//����
	public static final class Task{
		public final static int WAIT   = 70;
		public final static int DOING  = 71;
		public final static int DONE   = 72;
		public final static int FAILED = 73;
	}
	
	//Ƶ�����ȼ�
	public static final class FreqPri{
		public final static int URGENCY = 110; //�Ӽ�
		public final static int NORMAL  = 111;
	}

}
