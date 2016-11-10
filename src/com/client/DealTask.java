package com.client;

import java.io.File;

import com.db.operation.CRUD;
import com.util.Config;
import com.util.Constance;
import com.util.Log;
import com.util.Tools;

/**
 * @author Boris
 * @description 
 * 2016��8��24��
 */
public class DealTask implements Runnable{
	private Tools tools = Tools.getTools();
	
	private String serverPath; //�������ϵĵ�ַ
	private String frequence;
	private String receiverIp;

	private int receiverPort;
	private int fileTotalTime;
	private int freqId;
	private int taskId; 
	private int grapId;
	
	private long controllerAddr;
	
	static{
		if(Config.RUN_ON_MYECLIPSE){
			System.load("D://WorkSpace//VC//G33DDC_AssignFrePointListen//Debug//G33DDC_AssignFrePointListen.dll");
		}else{
			System.loadLibrary("G33DDC_AssignFrePointListen");
		}
	}
	
	/**
	 * @param serverPath
	 * @param frequence
	 * @param receiverIp
	 * @param receiverPort
	 * @param fileTotalTime
	 * @param freqId
	 * @param taskId
	 * @param grapId
	 */
	public DealTask(String serverPath, String frequence, String receiverIp,
			int receiverPort, int fileTotalTime, int freqId, int taskId,
			int grapId) {
		super();
		this.serverPath = serverPath;
		this.frequence = frequence;
		this.receiverIp = receiverIp;
		this.receiverPort = receiverPort;
		this.fileTotalTime = fileTotalTime;
		this.freqId = freqId;
		this.taskId = taskId;
		this.grapId = grapId;
		
		this.controllerAddr = getControllerAddr();
	}

	public void doTask(){
		System.out.println("***********do task + "+ grapId + "***********");
		//�������ݿ���ջ�״̬
		updateReceiverStatus(receiverIp, receiverPort, Constance.Reveiver.BUSY);
		updateGrapTaskStatus(grapId, Constance.Task.DOING);
		
		String beginTime = tools.getCurrentTime();
		String localFilePath = tools.getProperty("local_save_path")  + frequence + "\\" + tools.getCurrentDay() +"\\" + beginTime + ".wav";
		// ��鱾�ش洢·���Ƿ���ڣ� �������򴴽�
		tools.makeDir(localFilePath);
		
		//��Ƶ ��Ƶ c++ʵ��
		doTask(controllerAddr, receiverIp, receiverPort, Integer.parseInt(frequence), localFilePath, fileTotalTime);
		
		File file = new File(localFilePath);
		if (!file.exists()) {
			Log.out.warn(String.format("����%s���ջ�ʧ��", receiverIp));
			updateReceiverStatus(receiverIp, receiverPort, Constance.Reveiver.FREE);
			updateGrapTaskStatus(grapId, Constance.Task.WAIT);
			return;
		}
		
		//�������ݿ���ջ�״̬
		updateReceiverStatus(receiverIp, receiverPort, Constance.Reveiver.FREE);
		updateGrapTaskStatus(grapId, Constance.Task.DONE);
		
		String endTime = tools.getCurrentTime();
		String fileName = beginTime + "_" + tools.getCurrentTime() + ".wav";
		
		String startT = tools.formatDate(beginTime);
		String endT = tools.formatDate(endTime);
		String path = frequence + "\\" + tools.getCurrentDay() +"\\" + beginTime + "_" + tools.getCurrentTime() + ".wav";
		String remoteSavePath = serverPath + path;
		
		//�����ļ���Զ�� �����뵽�ļ�����
		if(tools.cpSrcFileToDestFile(localFilePath, remoteSavePath)){
			 String sql = "insert into tab_file (file_id,file_name, start_time,end_time ,freq_id, sto_id, sto_path, score_status, task_id, grap_id) " +
		              "values(seq_global.nextval,'"+fileName+"', to_date('"+startT+"','yyyy-mm-dd hh24:mi:ss'),to_date('"+endT+"','yyyy-mm-dd hh24:mi:ss'),"+freqId+",2,'"+path+"',70,"+taskId+","+grapId+")";
			 CRUD crud = new CRUD();
			 crud.instert(sql);
		}
	}
	
	public void stopCurrentTask(){
		stopCurrentTask(controllerAddr);
	}
	
	public void updateReceiverStatus(String receiverIp, int receiverPort, int status){
		CRUD crud = new CRUD();
		String sql = "update tab_mam_receiver r set r.status = " + status + " where r.port = " + receiverPort + " and r.ip = '" + receiverIp + "'";
		crud.update(sql);
	}
	
	public void updateGrapTaskStatus(int grapId, int status){
		CRUD crud = new CRUD();
		System.out.println("����grap id " + grapId + " status" + status);
		String sql = "update tab_grap_task set status = " + status + " where grap_id = " + grapId; 
		crud.update(sql);
	}
	
	private native long getControllerAddr(); 
	private native void stopCurrentTask(long controllerAddr);
	private native void doTask(long controllerAddr, final String receiverIp, final int receiverPort, final int frequence, final String filePath, final int fileTotalTime);
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		doTask();
	}
	
	public static void main(String[] args) {
		DealTask dealTask = new DealTask("D:\\G33DDCfile_server\\", "06030000", "11D28423", 0, 30, 0, 0, 0);
		new Thread(dealTask).start();
		
//		DealTask dealTask2 = new DealTask("D:\\G33DDCfile_server\\", "06220000", "11D28423", 1, 10, 0, 0, 0);
//		new Thread(dealTask2).start();
	}

}
