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
			sip = InetAddress.getByName(ip_sou);   //模拟手机APP端的IP地址
			dip = InetAddress.getByName(ip_des);	//主机在路由器下的IP地址

		} catch (UnknownHostException e1) {
System.out.println("IP地址初始化失败");
			e1.printStackTrace();
		}
		
		if(sip != null&&dip != null){
			//ip地址可以根据动态抓包得到的IP地址填入
			//int ident 每发一个包，应该增加1 ，计数器
			//52是生命周期，同样可以通过抓取的包来判断,17是指上层使用UDP
			//网络层IP
			
			udp.setIPv4Parameter(0, false, false, false, 0, false, true, false, 0, 0, 52, 17,sip, dip);
			//ident++;
			//链路层
			EthernetPacket ether = new EthernetPacket();		
			ether.frametype = EthernetPacket.ETHERTYPE_IP;
			
			ether.src_mac = getMacBytes(mac_src);  //自己电脑的网卡
			ether.dst_mac = getMacBytes(mac_des);  //安防主机的,或者路由器的?
			
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
	 * 辅助函数
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
		 * 5 获取用来发送数据的网卡
		 *************************************************/
		  public static JpcapSender getCrad(int deviceNum){
			  
				NetworkInterface[] devices = null;
				
				 try{    
					 //获取本机上的网络接口对象数组    
					  devices = JpcapCaptor.getDeviceList();
	System.out.println("网卡获取成功  ");  
					 }catch(Exception ef){    
					            ef.printStackTrace();    
		System.out.println("显示网络接口数据失败:  "+ef);    
					    }  
				 
				JpcapSender js = null;
				
				try {
					js = JpcapSender.openDevice(devices[deviceNum]);
	System.out.println("已获取网卡(发送使用)  "+deviceNum+"  JSender建立成功");  				
				} catch (IOException e1) {
		System.out.println("JSender建立不成功");
					e1.printStackTrace();
				}
				  
			  return js;
		  } 
		  /*************************************************
		 * end
		 *************************************************/
}
