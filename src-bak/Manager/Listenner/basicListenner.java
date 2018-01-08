package Manager.Listenner;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;

import Model.ConterData;
import Model.DB_csv_device;
import Model.Model_a;
import Util.DealPcap.wireless802_airdump1;
import Util.SetPacket.SetTCP;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;

public class basicListenner implements PackageListenner {

	//private Packet packet;
	private String hostMAC = null; 
	//private String dataType = "TCP";
	//private ConterData conterData = new ConterData();
	
	private DB_csv_device dbModel = null;
	BlockingQueue<Packet> sendQueue;
	
	
	//��־λ
	private int flag = 0;
	
	//construct
	public basicListenner(BlockingQueue<Packet> sendQueue,String hostMac,DB_csv_device dbModel){
		
		this.sendQueue = sendQueue;
		this.hostMAC = hostMac;	
		this.dbModel = dbModel;
	}
	
	public basicListenner(){
		
				
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
						   
						   if(this.dbModel!=null){
							   
							   if(dbModel.getAthome().length>0&&flag==0){
								   Model_a model_a = new Model_a();
								   
								   //�������
								   model_a.setData_byte(dbModel.getAthome());
								   
								   System.out.println("α�췢���豸������������.....");
								   tcp =  st.fakePacket(tcpPacket.dst_port, tcpPacket.src_port, 
							    			 seq, ack, tcpPacket.dst_ip.getHostAddress(), tcpPacket.src_ip.getHostAddress(),
							    			 yourMAC, hostMAC, model_a);
								   //
								  // conterData.getCounter_data_list().remove(0);
								   //
								   this.flag=1;
					System.out.println("α�����ݳɹ�   seq|"+tcp.sequence+" ack|"+tcp.ack_num+"  frameLen|"+tcp.data.length); 
								    
							   }
							   
							   else {	   
								   //��������ɷ��ƶ��е����ݺ���������ظ��������ݣ��ż���α��ACK�ظ������򲻻ظ�
								   if(tcpPacket.data.length != 0&&flag==1){ 
										   System.out.println("α�췢���豸������������.....");
										   tcp =  st.fakePacket(tcpPacket.dst_port, tcpPacket.src_port, 
									    			 seq, ack, tcpPacket.dst_ip.getHostAddress(), tcpPacket.src_ip.getHostAddress(),
									    			 yourMAC, hostMAC, null);
							System.out.println("α�����ݳɹ�   seq|"+tcp.sequence+" ack|"+tcp.ack_num+" ack|"+tcp.ack); 
										   
									flag=2;
									System.out.println("flag:"+flag);
								   }   
							   }
						   }
						   
						  
						  	   
					   }   
				   }
				   
				   //����������������
				   else if(tcpPacket.dst_ip.getHostAddress().equals(host_ip)){
					  
				   }
				 
				   if(tcp != null){
					   
					   //this.packet = tcp;
					   
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
			
			//return this.packet;
			return null;
		}
		
	public BlockingQueue<Packet> getSendQueue() {
		return sendQueue;
	}


	public void setSendQueue(BlockingQueue<Packet> sendQueue) {
		this.sendQueue = sendQueue;
	}


	@Override
	public int successFlag() {
		return this.flag;
	}

}
