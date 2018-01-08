package Util.SetPacket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.omg.CORBA.PRIVATE_MEMBER;

import Model.Model_a;
import Util.DealPcap.wireless802_airdump1;
import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.packet.EthernetPacket;
import jpcap.packet.UDPPacket;

public class SetUDP {
	
	
	//private static int ident=0;

	public static void main(String[] args) {
		
		
		/*SetUDP ss= new SetUDP();
		
		wireless802_airdump1 w = new wireless802_airdump1();
		List<Model_a> ls = w.ergodic("F:\\test_pcap\\sck_old.pcap");
		
		System.out.println("ffffff:"+ls.size());
		
		
		List<Model_a> ls1 = w.getFrameByindex(new int[]{27577}, ls);
		
		System.out.println(ls1.get(0).getData_byte().length);
		UDPPacket udp = ss.fakeudp(80, 49155, "112.124.42.42", "192.168.1.107", "60:D8:19:4a:b2:fc", "b4:43:0d:aa:50:61", ls1.get(0).getData_byte());	
		JpcapSender js = getCrad(1);
		js.sendPacket(udp);*/
	}

	public UDPPacket fakeudp(int src_port,int dst_port,String ip_sou,String ip_des,String mac_src,String mac_des,byte[] data){
		
		UDPPacket  udp = new UDPPacket(src_port, dst_port);
		
		InetAddress sip = null;
		InetAddress dip = null;
		try {
			sip = InetAddress.getByName(ip_sou);   //ģ���ֻ�APP�˵�IP��ַ
			dip = InetAddress.getByName(ip_des);	//������·�����µ�IP��ַ

		} catch (UnknownHostException e1) {
System.out.println("IP��ַ��ʼ��ʧ��");
			e1.printStackTrace();
		}
		
		if(sip != null&&dip != null){
			//ip��ַ���Ը��ݶ�̬ץ���õ���IP��ַ����
			//int ident ÿ��һ������Ӧ������1 ��������
			//52���������ڣ�ͬ������ͨ��ץȡ�İ����ж�,17��ָ�ϲ�ʹ��UDP
			//�����IP
			
			udp.setIPv4Parameter(0, false, false, false, 0, false, true, false, 0, 0, 52, 17,sip, dip);
			//ident++;
			//��·��
			EthernetPacket ether = new EthernetPacket();		
			ether.frametype = EthernetPacket.ETHERTYPE_IP;
			
			ether.src_mac = getMacBytes(mac_src);  //�Լ����Ե�����
			ether.dst_mac = getMacBytes(mac_des);  //����������,����·������?
			
			udp.datalink = ether;
			
			
			if(data != null&&data.length!=0){
				udp.data = data;
			}else{
				udp.data = "".getBytes();
			}
		}
		
		
		return udp;
	}
	
	
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
}
