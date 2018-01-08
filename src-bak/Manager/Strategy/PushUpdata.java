package Manager.Strategy;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

import Manager.Listenner.PackageListenner;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;
import jpcap.packet.UDPPacket;

public class PushUpdata {
	
	CopyOnWriteArrayList<PackageListenner>  listenners = new CopyOnWriteArrayList<PackageListenner>();
	
	String yourMAC;
	String hostMAC;
	String rounteMAC;
	String host_ip;
	
	Packet packet = null;
	
	public PushUpdata(String  yourMAC,String hostMAC,String rounteMAC,String host_ip){
		
		 this.yourMAC = yourMAC;
		 this.hostMAC = hostMAC;
		 this.rounteMAC = rounteMAC;
		 this.host_ip = host_ip;
	}
	
	public PushUpdata(){
		
		
	}
	
	 public synchronized Packet pushAfterDeal(Packet oldpacket,Packet newPacket){
		  
		 /*************************************************
		 * TCP数据包处理
		 *************************************************/
		  if(newPacket instanceof TCPPacket){
			  if(oldpacket == null){
				  	  //处理TCP数据
				  	  oldpacket = newPacket;
				  	  push(oldpacket);
//System.out.println("推送1");
				  }else {
					  	//先从同步集合中提取paket比较如果和packet_up不同，执行伪造数据，否则无操作
					  TCPPacket packet1 = (TCPPacket)oldpacket;
					  TCPPacket packet2 = (TCPPacket)newPacket;
					  
					  if(packet1.ack_num != packet2.ack_num || packet1.sequence != packet2.sequence || packet1.data.length!=packet2.data.length){
						  oldpacket = newPacket;
						  push(oldpacket);
//System.out.println("推送2");
					  }
				  }
			  
			  return oldpacket;
		  }
		  
		 /*************************************************
		 * UDP数据包
		 *************************************************/
		  else if(newPacket instanceof UDPPacket) {
			  //不需要判断重传，直接推送
			  push(newPacket);
			  //UDP帧不需要比较
			  return oldpacket;
		 }
		  
		  else {
			return null;
		}
		  		 
	 }
	 
	 public void addListenner(PackageListenner listenner){
		 this.listenners.add(listenner);
	 }
	 
	 
	 /*************************************************
	 * 推送消息给所有的监听者
	 *************************************************/
	 public void push(Packet packet){
		 
		 if(listenners.size()!= 0){
			 for(PackageListenner listenner:listenners){	 
				 listenner.listenPackage(yourMAC, hostMAC, packet, rounteMAC, host_ip);
			 }
			 
		 }
		 	 
	 }
	 
	 /*************************************************
	 * 检查监听者中是否有有反制成功的标志
	 * return 0 表示未成功反制，2表示反制成功
	 *************************************************/
	 public int cheakFlag(){
		 
		 if(listenners.size()!=0){
			 
			 for(PackageListenner listenner:listenners){	 
				 int flag = listenner.successFlag();
				 if(flag==2){
					 return 2;
				 }
			 }
			 
		 }
		 
		 return 0;
	 }
	 
	 public List<PackageListenner> getListenners(){
		 
		 return this.listenners;
		 
		 
	 }
}
