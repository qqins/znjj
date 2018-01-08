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
		 * TCP���ݰ�����
		 *************************************************/
		  if(newPacket instanceof TCPPacket){
			  if(oldpacket == null){
				  	  //����TCP����
				  	  oldpacket = newPacket;
				  	  push(oldpacket);
//System.out.println("����1");
				  }else {
					  	//�ȴ�ͬ����������ȡpaket�Ƚ������packet_up��ͬ��ִ��α�����ݣ������޲���
					  TCPPacket packet1 = (TCPPacket)oldpacket;
					  TCPPacket packet2 = (TCPPacket)newPacket;
					  
					  if(packet1.ack_num != packet2.ack_num || packet1.sequence != packet2.sequence || packet1.data.length!=packet2.data.length){
						  oldpacket = newPacket;
						  push(oldpacket);
//System.out.println("����2");
					  }
				  }
			  
			  return oldpacket;
		  }
		  
		 /*************************************************
		 * UDP���ݰ�
		 *************************************************/
		  else if(newPacket instanceof UDPPacket) {
			  //����Ҫ�ж��ش���ֱ������
			  push(newPacket);
			  //UDP֡����Ҫ�Ƚ�
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
	 * ������Ϣ�����еļ�����
	 *************************************************/
	 public void push(Packet packet){
		 
		 if(listenners.size()!= 0){
			 for(PackageListenner listenner:listenners){	 
				 listenner.listenPackage(yourMAC, hostMAC, packet, rounteMAC, host_ip);
			 }
			 
		 }
		 	 
	 }
	 
	 /*************************************************
	 * �����������Ƿ����з��Ƴɹ��ı�־
	 * return 0 ��ʾδ�ɹ����ƣ�2��ʾ���Ƴɹ�
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
