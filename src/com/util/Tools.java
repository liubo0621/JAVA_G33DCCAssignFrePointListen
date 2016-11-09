package com.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class Tools {
	public static Tools getTools(){
		return new Tools();
	}
	
	// ��ָ��byte������16���Ƶ���ʽ��ӡ������̨
	public void printHexString(final byte[] b) {
		printHexString(b, b.length);
	}
	
    /** 
     * @Method: printHexString 
     * @Description: ��16�������
     * @param b
     * @param length
     * void
     */ 
    public void printHexString(final byte[] b, int length){
    	String msg = "";
    	for (int i = 0; i < length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			msg += "[" + i + "]" + hex.toUpperCase() +  " ";
		}
		System.out.println(msg + "\n");
	}
    
    /** 
     * @Method: getBufferType 
     * @Description: ȡ�������ͣ�STCP����
     * @param b
     * return ���ر�������
     * String
     */  
    public String getBufferType(final byte[] b){
    	String bufferType = null;
    	switch (b[0] & 0xff) {
		case 0x00:
			bufferType = "�ǼǱ�: ";
			break;
		case 0x01:
			bufferType = "�㱨��: ";
			break;
		case 0x02:
			bufferType = "ȷ�ϱ�: ";
			break;
		case 0x03:
			bufferType = "���䱨: ";
			break;
		case 0x04:
			bufferType = "���뱨�� ";
			break;
		case 0x05:
			bufferType = "��·̽�ⱨ�� ";
			break;
		default:
			break;
		}
    	return bufferType;
    }
    
    /** 
     * @Method: printSTCP 
     * @Description: ��16���Ƹ�ʽ���SCTP���� 
     * @param buffer
     * void
     */ 
    public void printStr(byte[] buffer){
    	String msg = "";
    	try {
    		for (int i = 0; i < buffer.length; i++) {
    			byte[] b = {(byte) (buffer[i] & 0xFF)};
    			msg += new String(b, "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	System.out.println(msg + "\n");
    }
    
    /** 
     * @Method: getLocalIP 
     * @Description: ���ر�����ַ
     * @return
     * String
     */ 
    public String getLocalIP(){
		String ip = null;
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
		return ip;
	}
    
    private FileOutputStream out = null;
    private String filePath = null;
    /** 
     * @Method: writeToFile 
     * @Description: д���ݵ��ļ�
     * @param path �ļ������·��+�ļ���
     * @param content byte����  ��������
     * @param startPos ������ʼλ�� ����λ�õ��ļ�ĩβ
     * void
     */ 
    public void writeToFile(final String path, byte[] content, int startPos){
    	//�����ļ� ���out��Ϊ�� ˵����д���ļ� ��flush���ļ�Ȼ��close�� Ȼ������newһ��
    	if (!path.equals(filePath)) {
    		//�ж�Ŀ¼�Ƿ���� �����ڴ���
    		File file = new File(path);
    		if (!file.getParentFile().exists()) {
    			file.getParentFile().mkdirs();
    		}
    		
    		if (out != null) {
    			try {
					out.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					try {
						out.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
    		try {
				out = new FileOutputStream(path, true);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		filePath = path;
		}
    
    
    	try {
    		if (!out.getFD().valid()) {
    			out = new FileOutputStream(path, true);
			}
			out.write(content, startPos, content.length - startPos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
       
    /** 
     * @Method: writeToFileEnd 
     * @Description: д���ݽ���ʱ���� 
     * void
     */ 
    public void writeToFileEnd(){
    	if (out != null) {
			try {
				out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
    }
    
    /** 
     * @Method: mvSrcFileToDestFile 
     * @Description: �ƶ��ļ�
     * @param srcFileName Դ�ļ�������·�����ļ�����
     * @param destFileName Ŀ���ļ�������·�����ļ�����
     * void
     */ 
    public void mvSrcFileToDestFile(final String srcFileName, final String destFileName){
    	File path = new File(destFileName);
		if (!path.getParentFile().exists()) {
			path.getParentFile().mkdirs();
		}
		
    	File file = new File(srcFileName);
    	try {
			FileInputStream is = new FileInputStream(file);
			FileOutputStream os = new FileOutputStream(destFileName, true);
			byte[] buffer = new byte[1024];
			int byteRead;
			while((byteRead = is.read(buffer)) != -1){
				os.write(buffer, 0, byteRead);
			}

			os.flush();
			os.close();
			is.close();
			file.delete();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * @Method: cpSrcFileToDestFile 
     * @Description:�����ļ�
     * @param srcFileName Դ�ļ�
     * @param destFileName Ŀ���ļ�
     * void
     */
    public boolean cpSrcFileToDestFile(final String srcFileName, final String destFileName){
    	File path = new File(destFileName);
		if (!path.getParentFile().exists()) {
			path.getParentFile().mkdirs();
		}
		
    	File file = new File(srcFileName);
    	if (!file.exists() || file.length() < 102400) { //�ļ�С��100k
			return false; //������
		}
    	try {
			FileInputStream is = new FileInputStream(file);
			FileOutputStream os = new FileOutputStream(destFileName, true);
			
			byte[] buffer = new byte[1024];
			int byteRead;
			while((byteRead = is.read(buffer)) != -1){
				os.write(buffer, 0, byteRead);
			}
			
			os.flush();
			os.close();
			is.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return true;
    }
    
    public void makeDir(String destFileName){
    	File path = new File(destFileName);
		if (!path.getParentFile().exists()) {
			path.getParentFile().mkdirs();
		}
    }
    
    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//�������ڸ�ʽ
    public String getCurrentTime(){
    	return df.format(new Date());
    }
    
    private SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");//�������ڸ�ʽ
    public String getCurrentDay(){
    	return df2.format(new Date());
    }
    
    public long getCurrentSecond(){
    	return new Date().getTime() / 1000;
    }
    
    SimpleDateFormat df3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public String formatDate(String date){
    	try {
			date = df3.format(df.parse(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return date;
    }
    
    //��ȡproperties�ļ�
    
    static Properties pps = new Properties();
    static{
		try {
			String path = Thread.currentThread().getContextClassLoader().getResource("gafpl.properties").getPath();
			pps.load(new FileInputStream(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	public String getProperty(String key) {
		return pps.getProperty(key).trim();
	}
	
}
