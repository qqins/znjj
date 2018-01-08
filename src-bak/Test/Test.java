package Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.packet.UDPPacket;
import Util.SetPacket.SetUDP;

public class Test {
	String param1 = null;
	
	/**
     * ���ڽ���ʮ�������ַ��������Сд�ַ�����
     */
    private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    /**
     * ���ڽ���ʮ�������ַ�������Ĵ�д�ַ�����
     */
    private static final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	
	public static void main(String[] args) {
		
		scan();
		//readFile(false);
		//new ReadCSV("F:\\test_pcap\\test.csv").readCsv(3);
		//ReadShell rs = new ReadShell();
		//rs.readSH("/home/liu/shelltest/ifconfig_test");
		
		/*Test test = new Test();
		test.readProperties();  
		System.out.println(test.param1);;*/
		
		
		/*************************************************
		 * �������
		 *************************************************/
		/*Scanner s = new Scanner(System.in);
		int a= s.nextInt();
		System.out.println(a);
		s.close();*/
		/*************************************************
		 * UDP����
		 *************************************************/
		/*SetUDP ss= new SetUDP();
		UDPPacket udp = ss.fakeudp(3442, 3254, "192.168.1.100", "192.168.1.101", "60:D8:19:4a:b2:fc", "AC:CF:23:87:BD:2E", new byte[]{3,5,6});
		JpcapSender js = getCrad(1);
		for(int i = 0;i<5;i++){
			
			js.sendPacket(udp);
			
		}*/
		
	}
	
	
	/*************************************************
	 * �ֽ�����ת��
	 *************************************************/
	public static String  bytesToHexString(byte[] src){  
	    StringBuilder stringBuilder = new StringBuilder("");  
	    if (src == null || src.length <= 0) {  
	        return null;  
	    }  
	    for (int i = 0; i < src.length; i++) {  
	        int v = src[i] & 0xFF;  
	        String hv = Integer.toHexString(v);  
	        if (hv.length() < 2) {  
	            stringBuilder.append(0);  
	        }  
	        stringBuilder.append(hv);  
	    }  
	    return stringBuilder.toString();  
	} 
	
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * 5 ��ȡ�����������ݵ�����
	 *************************************************/
	  public static JpcapSender getCrad(int deviceNum){
		  
			NetworkInterface[] devices = null;
			
			 try{    
				 //��ȡ�����ϵ�����ӿڶ�������    
				  devices = JpcapCaptor.getDeviceList();
System.out.println("������ȡ�ɹ�  ");  
				 }catch(Exception ef){    
				            ef.printStackTrace();    
	System.out.println("��ʾ����ӿ�����ʧ��:  "+ef);    
				    }  
			 
			JpcapSender js = null;
			
			try {
				js = JpcapSender.openDevice(devices[deviceNum]);
System.out.println("�ѻ�ȡ����(����ʹ��)  "+deviceNum+"  JSender�����ɹ�");  				
			} catch (IOException e1) {
	System.out.println("JSender�������ɹ�");
				e1.printStackTrace();
			}
			  
		  return js;
	  } 
	  /*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * 4
	 *************************************************/
	public  void readProperties(){
		
		
		
		String param2 = null;
		Properties prop = new Properties();
		InputStream in = Object. class .getResourceAsStream( "/prameter.properties" );    
        try  {    
           prop.load(in);    
           param1 = prop.getProperty( "ScanAP_CSV_path" ).trim();    
           param2 = prop.getProperty( "i2" ).trim();    
       }  catch  (IOException e) {    
           e.printStackTrace();    
       }  
        System.out.println(param2);  
        
        /*
        File fileB = new File( this.getClass().getResource( "" ).getPath());  
        
        System. out .println( "fileB path: " + fileB);*/
		
	}
	/*************************************************
	 * 1
	 *************************************************/
	public static byte[] hexStringToByte(String hex) {   
	    int len = (hex.length() / 2);   
	    byte[] result = new byte[len];   
	    char[] achar = hex.toCharArray();   
	    for (int i = 0; i < len; i++) {   
	     int pos = i * 2;   
	     result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));   
	    }   
	    return result;   
	}  
	
	private static byte toByte(char c) {   
	    byte b = (byte) "0123456789ABCDEF".indexOf(c);   
	    return b;   
	}
	
	
	/*************************************************
	 * 2
	 *************************************************/
	  public static byte [] getMacBytes(String mac){
		  byte []macBytes = new byte[6];
		  String [] strArr = mac.split(":");
		  
		  for(int i = 0;i < strArr.length; i++){
		   int value = Integer.parseInt(strArr[i],16);
		   macBytes[i] = (byte) value;
		  }
		  return macBytes;
	 }
	  /*************************************************
	 * 3
	 *************************************************/
	  public static void scan(){	
			//int deviceNum = 3;
			NetworkInterface[] devices = null;
			 try{    
				 //��ȡ�����ϵ�����ӿڶ�������    
				  devices = JpcapCaptor.getDeviceList();    
				  for(int i=0;i<devices.length;i++){    
				     NetworkInterface nc = devices[i];  
	System.out.println("��"+i+"���ӿ�:"+nc.description);
	System.out.println("Name:"+nc.name);
	
	System.out.println("MAC:"+bytesToHexString(nc.mac_address));
				     //һ�鿨�Ͽ����ж����ַ:       
				     for(int t=0;t<nc.addresses.length;t++){    
	System.out.println(" addresses["+t+"]: IP| "+nc.addresses[t].address.toString());
				     }    
	System.out.println("");

				   }          
				 }catch(Exception ef){    
				            ef.printStackTrace();    
	System.out.println("��ʾ����ӿ�����ʧ��:  "+ef);    
				    }  	  
	  }
	  
	  
	  public static void readFile(boolean scanFlag){
		  
		  String path="F:\\test_pcap";
		  File file=new File(path);
		  File[] tempList = file.listFiles();
		  System.out.println("��Ŀ¼�¶��������"+tempList.length);
		  int beginLen = tempList.length;
		  for (int i = 0; i < tempList.length; i++) {
			   if (tempList[i].isFile()) {
			   //��ȡĳ���ļ����µ������ļ�
			    System.out.println(tempList[i].getName());
			   }
			   
			   if (tempList[i].isDirectory()) {
			    //��ȡĳ���ļ����µ������ļ���
				  System.out.println(tempList[i].getName());
			   }
		 }
		  
		  
		  while(scanFlag){	  
			  tempList = file.listFiles();
			  int nowLen = tempList.length;
			  if(nowLen>beginLen){
				  beginLen = nowLen;
				  System.out.println("��Ŀ¼�¶��������"+tempList.length);
				  for (int i = 0; i < tempList.length; i++) {
					   if (tempList[i].isFile()) {
					   //��ȡĳ���ļ����µ������ļ�
						   System.out.println(tempList[i].getName());
					   }
					   
					   if (tempList[i].isDirectory()) {
					    //��ȡĳ���ļ����µ������ļ���
						   System.out.println(tempList[i].getName());
					   }
				 }
			  }
		  }
		  	  
		  
	  }
}
