package Model;

import jpcap.PacketReceiver;
import jpcap.packet.DatalinkPacket;
import jpcap.packet.EthernetPacket;
import jpcap.packet.IPPacket;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;
import jpcap.packet.UDPPacket;

/**  
 * 抓包监听器,实现PacketReceiver中的方法:打印出数据包说明     
 */    
public class TestPacketReceiver  implements PacketReceiver {  
	
	String host_ip = null;
	String service_ip = null;
	
	Model_Capture model;
	
	long seqnum;
	long acknum;
	int datalen;
	String flag;
	String yourMAC;
	Packet packet = null;
	
	//构造函数
	
	public TestPacketReceiver(String host_ip,String yourMAC){
		
		this.host_ip = host_ip;
		/*if(service_ip != null){
			this.service_ip = service_ip;
		}*/
		this.yourMAC = yourMAC;
	}
	public TestPacketReceiver(){
		
		
		
	}
      /**  
       * 实现的接包方法:  
       */    
      public void receivePacket(Packet packet) {   
          //Tcp包,在java Socket中只能得到负载数据    
        if(packet instanceof jpcap.packet.TCPPacket){    
            TCPPacket p=(TCPPacket)packet;  
            //&& this.service_ip != null
            if(this.host_ip != null ){
            	
            	dealByProtocol("TCP", p);
            	
            }else{
            	display(p);
            }
        }  
        //UDP数据
        else if(packet instanceof jpcap.packet.UDPPacket){    
            UDPPacket p=(UDPPacket)packet;           
           if(this.host_ip != null ){
        	   
        	   dealByProtocol("UDP", p);
           } 
           else{
           	
           	display(p);
           }
        }     
        
        
          /*************************************************
		 * 暂时不处理其他形式的数据包，目前阶段只考虑TCP
		 *************************************************/ 
           
        //如果你要在程序中构造一个ping报文,就要构建ICMPPacket包    
       /*else if(packet instanceof jpcap.packet.ICMPPacket){    
           ICMPPacket p=(ICMPPacket)packet;    
           //ICMP包的路由链    
           String router_ip="";    
           for(int i=0;i<p.router_ip.length;i++){    
               router_ip+=" "+p.router_ip[i].getHostAddress();    
           }    
            String s="@ @ @ ICMPPacket:| router_ip "+router_ip    
             +" |redir_ip: "+p.redir_ip    
             +" |mtu: "+p.mtu    
             +" |length: "+p.len;    
          System.out.println(s);    
        }    
        //是否地址转换协议请求包    
       else if(packet instanceof jpcap.packet.ARPPacket){    
           ARPPacket p=(ARPPacket)packet;    
           //Returns the hardware address (MAC address) of the sender    
           Object  saa=   p.getSenderHardwareAddress();    
           Object  taa=p.getTargetHardwareAddress();    
           String s="* * * ARPPacket:| SenderHardwareAddress "+saa    
             +"|TargetHardwareAddress "+taa    
             +" |len: "+p.len;    
         System.out.println(s);    
                
        }  */  
    //取得链路层数据头 :如果你想局网抓包或伪造数据包，嘿嘿    
   /*  DatalinkPacket datalink  =packet.datalink;    
     //如果是以太网包    
     if(datalink instanceof jpcap.packet.EthernetPacket){    
         EthernetPacket ep=(EthernetPacket)datalink;    
          String s="  datalink layer: "    
              +"|DestinationAddress: "+ep.getDestinationAddress()    
              +"|SourceAddress: "+ep.getSourceAddress();    
          System.out.println(s);    
    } */ 
//System.out.println("");
  }    
      
      
      /*************************************************
	 * 统一处理IP帧抓包结果，根据协议
	 *************************************************/
      public void dealByProtocol(String protocol,IPPacket p){
    	  
      	if(this.host_ip.equals(p.dst_ip.getHostAddress())){
      		
      		EthernetPacket ep = (EthernetPacket) p.datalink;
      		if(ep.getSourceAddress().equals(yourMAC)){
      			
      			//抓到的包如果是自己伪造的数据包，就丢弃掉不做响应
      			
      		}else{
      			this.packet = p;
      			System.out.println("抓取到"+protocol+"帧       服务器-->>>--设备");
      			//显示
      			display(p);
      		}
      		
      	}else if(this.host_ip.equals(p.src_ip.getHostAddress())){
      		
      		EthernetPacket ep = (EthernetPacket) p.datalink;
      		if(ep.getSourceAddress().equals(yourMAC)){
      			
      			//抓到的包如果是自己伪造的数据包，就丢弃掉不做响应
      			
      		}else {
      			 this.packet = p;
          		 System.out.println("抓取到"+protocol+"帧      设备-->>>--服务器");
          		 //显示
                   display(p);
				}
      		
      	}
      
    	  
    	  
      }
      /*************************************************
	 * end
	 *************************************************/

public Model_Capture setValue(TCPPacket p){
	
	//获取数据
	long acknum = p.ack_num;
    long seqnum = p.sequence;
    byte[] data = p.data;
    String ip_des = p.dst_ip.getHostAddress();
    String ip_sou = p.src_ip.getHostAddress();
    boolean push = p.psh;
    boolean ack = p.ack;
    boolean syn = p.syn;
    int dataLen = p.data.length;
    int port_sou = p.src_port;
    int port_des = p.dst_port;
    
    //装载数据model_serToHost
    Model_Capture model = new Model_Capture();
	model.setAcknum(acknum);
	model.setSeqnum(seqnum);
	model.setData(data);
	model.setDataLen(dataLen);
	model.setIp_des(ip_des);
	model.setIp_sou(ip_sou);
	model.setPort_des(port_des);
	model.setPort_sou(port_sou);
	//标志位需要先判断在装
	if(syn){
		model.setFlag("syn");
		
	}
	else if(ack&&push){	
		model.setFlag("ack/push");
	}
	else if(ack){	
		model.setFlag("ack");
	}
    
    //判断方向,true代表消息是来自服务起，false表示来自主机
    if(this.host_ip.equals(ip_des)&&this.service_ip.equals(ip_sou)){
    	
    	//这是一条由服务器传下来的数据
    	model.setTransation(true);
    }
    else if(this.host_ip.equals(ip_sou)&&this.service_ip.equals(ip_des)){
    	
    	//这是一条由主机传回去的数据
    	model.setTransation(false);
    }
	
    return model;
}


public void display(Packet packet){
	//取出参数
    /*long acknum = p.ack_num;
    long seqnum = p.sequence;
    byte[] data = p.data;
    String ip_des = p.dst_ip.getHostAddress();
    String ip_sou = p.src_ip.getHostAddress();
    boolean push = p.psh;
    boolean ack = p.ack;
    boolean syn = p.syn;
    int dataLen = p.data.length;*/
    
    if(packet instanceof jpcap.packet.TCPPacket){
    	
    	TCPPacket tcp = (TCPPacket)packet;
		System.out.println("TCPPacket:| dst_ip "+tcp.dst_ip+":"+
			   		tcp.dst_port+"|src_ip "+tcp.src_ip+":"+tcp.src_port
			   +" |acknum: "+tcp.ack_num+" |seqnum: "+tcp.sequence+" |ack"+tcp.ack+
			   " |push "+tcp.psh+"|datalen"+tcp.data.length);
	  
	   //MAC层数据
		DatalinkPacket datalink  =packet.datalink;       
      	EthernetPacket ep=(EthernetPacket)datalink;    
      	String s="  datalink layer: "    
              +"|DestinationAddress: "+ep.getDestinationAddress()    
              +"|SourceAddress: "+ep.getSourceAddress();    
      	System.out.println(s);    
    }
    
    if(packet instanceof jpcap.packet.UDPPacket){
    	
    	UDPPacket udp = (UDPPacket)packet;
		System.out.println("TCPPacket:| dst_ip "+udp.dst_ip+":"+
			   		udp.dst_port+"|src_ip "+udp.src_ip+":"+udp.src_port
			   +"|datalen"+udp.data.length);
    	//MAC层数据
    	DatalinkPacket datalink  =packet.datalink;       
    	EthernetPacket ep=(EthernetPacket)datalink;    
    	String s="  datalink layer: "    
    	              +"|DestinationAddress: "+ep.getDestinationAddress()    
    	              +"|SourceAddress: "+ep.getSourceAddress();    
    	System.out.println(s); 
    }
    
    
    /*if(this.host_ip != null){
    	
    	if(ip_des.equals(this.host_ip)||ip_sou.equals(this.host_ip)){
    		System.out.println("TCPPacket:| dst_ip "+ip_des+":"+
 			   		p.dst_port+"|src_ip "+ip_sou+":"+p.src_port 
 			   +" |acknum: "+acknum+" |seqnum: "+seqnum+" |ack"+ack+
 			   " |push "+push+"|datalen"+dataLen);
 	  
 	   //MAC层数据
    		DatalinkPacket datalink  =packet.datalink;       
	      		if(datalink instanceof jpcap.packet.EthernetPacket){    
	      			EthernetPacket ep=(EthernetPacket)datalink;    
	      			String s="  datalink layer: "    
	              +"|DestinationAddress: "+ep.getDestinationAddress()    
	              +"|SourceAddress: "+ep.getSourceAddress();    
System.out.println(s);    
	      		}  	
    	}

    }
    
    else if(this.host_ip == null){
System.out.println("TCPPacket:| dst_ip "+ip_des+":"+
		   		p.dst_port+"|src_ip "+ip_sou+":"+p.src_port 
		   +" |acknum: "+acknum+" |seqnum: "+seqnum+" |ack"+ack+
		   " |push "+push+"|datalen"+dataLen);
  
   //MAC层数据
 	   DatalinkPacket datalink  =packet.datalink;       
 	   if(datalink instanceof jpcap.packet.EthernetPacket){    
 		   EthernetPacket ep=(EthernetPacket)datalink;    
 		   String s="  datalink layer: "    
              +"|DestinationAddress: "+ep.getDestinationAddress()    
              +"|SourceAddress: "+ep.getSourceAddress();    
System.out.println(s);    
 	   }    
    }*/
	
}



public Model_Capture getModel() {
	return model;
}
public void setModel(Model_Capture model) {
	this.model = model;
}
public Packet getPacket() {
	return packet;
}
public void setPacket(Packet packet) {
	this.packet = packet;
}

}
