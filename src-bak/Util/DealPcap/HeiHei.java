package Util.DealPcap;

import java.io.File;
import java.io.FileInputStream;
import java.awt.image.BufferStrategy;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.omg.CORBA.PUBLIC_MEMBER;

/*
 * //文件头 数据包头+数据报  数据包头 +数据报  .......
 	pcap包结构为：24固定字节的Pcap头+16固定字节的数据头+数据内容（含mac帧、ip帧、tcp帧、http帧等）
  */
public class HeiHei {
	
	// L为前多帧数据内容总和（不包含数据包头），j为需要求出的帧的代号，此方法求出第j帧数据内容的长度
	// 此方法建立在流完全复制到数组里
	public static int frameLength(byte[] by, int L, int j) {
		
		
		// len为返回的第j帧数据内容的长度
		int len = 0; 
		StringBuffer s1 = new StringBuffer();
		
		
		try {
			int s = 20 + 16 * j + L;
			for (int i = s; i < s + 4; i++) {
				// System.out.print(Integer.toString((by[i] & 0xff) + 0x100,
				// 16).substring(1) + " ");
				s1.append(Integer.toString((by[s + s + 3 - i] & 0xff) + 0x100, 16).substring(1));
			}
			// System.out.println();
			String s2 = s1.toString();
			len = Integer.parseInt(s2, 16);
		} catch (Exception e) {
			e.printStackTrace();
		}
System.out.print(len + "字节");
		return len;
	}
	
	
	/*************************************************
	 * MAC地址查询
	 *************************************************/
	public String macString_des(int ethloc,byte by[]){
		StringBuilder sb = new StringBuilder();
		sb.append("	\tDes Mac");
		//6个字节的MAC地址信息
		for (int m = 0; m < 6; m++) {
			sb.append(":"+Integer.toString((by[ethloc + m] & 0xff) + 0x100, 16).substring(1));	
		}
System.out.print(sb.toString());
		return sb.toString();
		
	}
	
	
	
	public String macString_sou(int ethloc,byte by[]){
		StringBuilder sb = new StringBuilder();
		sb.append("	Sou Mac");
		//6个字节的MAC地址信息
		for (int n = 6; n < 12; n++) {
			sb.append(":"+Integer.toString((by[ethloc + n] & 0xff) + 0x100, 16).substring(1));	
		}
System.out.print(sb.toString());
		return sb.toString();
		
	}
	/*************************************************
	 * end
	 *************************************************/

	
	
	
	/*************************************************
	 * 该函数查询网络层类型(PPPOE或者直接ipv4)
	 *************************************************/
	public String netType(int ethloc,byte by[]){
		
		String nettype;
		nettype = Integer.toString((by[ethloc + 12] & 0xff) + 0x100, 16).substring(1)
				+ Integer.toString((by[ethloc + 13] & 0xff) + 0x100, 16).substring(1);
		
System.out.print("\t网络层类型" + nettype);
		
		if(nettype.equals("8864")){
			String nettype1 = Integer.toString((by[ethloc + 20] & 0xff) + 0x100, 16).substring(1)
					+ Integer.toString((by[ethloc + 21] & 0xff) + 0x100, 16).substring(1);
System.out.print("PPPoE(IPV4)"+nettype1);
			return nettype1;
		
		}
		else if(nettype.equals("0800")){
System.out.print("(IPV4)");
			return "0800";
		}
		
		return "0";
	}
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	/*************************************************
	 * 该函数处理IP地址
	 *************************************************/
public String ipAddress_sou(byte by[],int ethloc){
	
	

	StringBuilder sb = new StringBuilder();
	sb.append(" 源ip地址：");
	for (int f = 0; f < 4; f++) {
		sb.append(Integer.parseInt(Integer.toString((by[ethloc + 26 + f] & 0xff) + 
				0x100, 16).substring(1), 16));
		if(f !=3 ){
			sb.append(".");
		}
	}

System.out.print(sb.toString()+"\t");
	return sb.toString();
	
}




public String ipAddress_des(byte by[],int ethloc){
	
	
	StringBuilder sb = new StringBuilder();
	sb.append("	目的ip地址：");
	for (int f = 4; f < 8; f++) {
		sb.append(Integer.parseInt(Integer.toString((by[ethloc + 26 + f] & 0xff) + 
				0x100, 16).substring(1), 16));
		if(f != 7 ){
			sb.append(".");
		}
	}
System.out.print(sb.toString());
	return sb.toString();
	
}


	/*************************************************
	 * end
	 *************************************************/
	/*************************************************
	 * 该函数算出IP头长度
	 *************************************************/
public int iplength(byte by[],int ethloc){
	
		int ipheadlen = 0;
		String s1 = Integer.toString((by[ethloc + 14] & 0xff) + 0x100, 16).substring(1);
		ipheadlen = 4 * Integer.parseInt(s1.substring(1), 16);
System.out.print("\tip头长度:" + ipheadlen + "字节");
		return ipheadlen;
	
}
	/*************************************************
	 * end
	 *************************************************/

/*************************************************
 * 该函数算出端口号和传输协议
 *************************************************/
public String prosty(int ipheadlen,int ethloc,byte by[]){
	
	String prosty = Integer.toString((by[ethloc+23] & 0xff) + 0x100, 16).substring(1);
System.out.print(" \t协议：" + prosty+" ");
	return prosty;
	
}

public String port_sou(int ipheadlen,int ethloc,byte by[]){
	

System.out.print("源端口：");
	StringBuffer port1 = new StringBuffer();
	for (int i = 0; i < 2; i++) {
		port1.append(Integer.toString((by[ethloc+14 + i + ipheadlen] & 0xff) + 0x100, 16)
				.substring(1));
	}
System.out.print(Integer.parseInt(port1.toString(), 16));
	
	return port1.toString();
	
}


public String port_des(int ipheadlen,int ethloc,byte by[]){
	
	
System.out.print("\t目的端口：");
	StringBuffer port2 = new StringBuffer();
	for (int i = 2; i < 4; i++) {
		port2.append(Integer.toString((by[ethloc+14 + i + ipheadlen] & 0xff) + 0x100, 16)
				.substring(1));
	}
System.out.print(Integer.parseInt(port2.toString(), 16));
	return port2.toString();
	
}
/*************************************************
 * end
 *************************************************/


/*************************************************
 * 该函数求出TCP头部长度
 *************************************************/
public int tcpHeadlen(byte by[],int ethloc,int ipheadlen){
	
		String x1 = Integer.toString((by[ethloc+26 + ipheadlen] & 0xff) + 0x100, 16)
				.substring(1);
		String x2 = x1.substring(1) + x1.substring(0, 1);
		int tcpheadlen = 4 * Integer.parseInt(x2, 16);
System.out.print("\ttcp头长：");
System.out.print(tcpheadlen + "字节");
		return tcpheadlen;
	
}
/*************************************************
 * end
 *************************************************/


/*************************************************
 * 该函数对TCP类型进行判断(SYN  SYN/ACK ACK PUSH/ACK FIN)
 *************************************************/
public String tcpType(String prosty,int ethloc,byte by[],int ipheadlen,String nettype){
	
	
	if(prosty.equals("06")){
		int index=0;
		if(nettype.equals("0021")){
			index = 8;
		}
		else if(nettype.equals("0800")){
		}
		
		String tcpType = Integer.toString((by[ethloc+27+index + ipheadlen] & 0xff) + 0x100, 16)
				.substring(1);
		
		//02是SYN标志
		if(tcpType.equals("02")){
System.out.print(" \tSYN位为1，TCP起始位(握手)");
			
		}
		
		//12为SYN/ACK
		else if(tcpType.equals("12")){
			
			
		}
		//10为ACK
		else if(tcpType.equals("10")){
				
		}
		//18为PUSH/ACK
		else if(tcpType.equals("18")){
				
		}
		//11为FIN/ACK结束传输
		else if(tcpType.equals("11")){
			
System.out.print(" \tFIN位为1，TCP结束");
		}
		
		return tcpType;
		
	}
	return "0";
	
}
/*************************************************
 * end
 *************************************************/

/*************************************************
 * 该函数求出seq num 和ACK num
 *************************************************/
public long seqNumber(int ethloc,int ipheadlen,byte by[]){
	StringBuffer seq = new StringBuffer();
	for (int i = 0; i < 4; i++) {
		seq.append(Integer.toString((by[ethloc+18 + i + ipheadlen] & 0xff) + 0x100, 16)
				.substring(1));
	}
System.out.print("	\tseqNumber:"+Long.parseLong(seq.toString(), 16));
	return Long.parseLong(seq.toString(), 16);
}

public long ackNumber(int ethloc,int ipheadlen,byte by[]){
	StringBuffer seq = new StringBuffer();
	for (int i = 0; i < 4; i++) {
		seq.append(Integer.toString((by[ethloc+22 + i + ipheadlen] & 0xff) + 0x100, 16)
				.substring(1));
	}
System.out.print("	\tackNumber:"+Long.parseLong(seq.toString(), 16));
	return Long.parseLong(seq.toString(), 16);
}


/*************************************************
 * end
 *************************************************/
		
	/*************************************************
	 * 将原本的主函数改为遍历函数，
	 * 找出是TCP的每一帧
	 * 并交给Manager处理
	 *************************************************/

public static void main(String[] args) {
		
		/*************************************************
		 * 定义区
		 *************************************************/
		File file1 = new File("F:\\1122.pcap");
//		File file2 = new File("F:\\result.txt");
		
		HeiHei heihei =new HeiHei();
		//List<TcpSequence> listTcp = new ArrayList<TcpSequence>();
		
		int ss = 24;
		int L = 0;
		
		int len;
		int j = 1;
		int ethloc;
		int ipheadlen;
		int tcpHeadlen;
		int seglen;
		
		long seqnumber;
		long acknumber;
		
		
		String nettype = null;
		
		String mac_des;
		String mac_sou;
		
		String ipAddress_sou;
		String ipAddress_des;
		
		String prosty;
		String port_des;
		String port_sou;
		
		String tcpType;
		
		
		/*************************************************
		 * end
		 *************************************************/
		
		/*************************************************
		 * 初始化
		 *************************************************/
		try {
//			if (!file2.exists()) {
//				file2.createNewFile();
//			}
			FileInputStream fis = new FileInputStream(file1);

//			FileOutputStream fos = new FileOutputStream(file2);
			int size = fis.available();
System.out.println("总" + size);
			byte by[] = new byte[size];
			fis.read(by);
		/*************************************************
		 * end
		 *************************************************/
			
			
			
			while (ss < 200000) {
System.out.print("\n" + "第" + j + "帧（已去掉数据包头）：" );
				
				//第一次循环时  by是读进来的数据,L=0，j=1
				len = HeiHei.frameLength(by, L, j);
				
				// 当前帧数据内容起始位置，去掉pcap头和数据包头
				//ethloc = 40 + L + 16 * (j - 1);
				
				//MAC地址
				//mac_des = heihei.macString_des(ethloc, by);
				//mac_sou = heihei.macString_sou(ethloc, by);
				
				//网络层类型
				/*nettype = heihei.netType(ethloc, by);
				
				if(nettype.equals("0021")){
					ethloc += 8;
				}
				*/
				//只显示IPV4的
				//if(nettype.equals("0800") || nettype.equals("0021")){
					//获取IP头长度
					//ipheadlen = heihei.iplength(by, ethloc);
							
					//获取IP地址
					//ipAddress_des = heihei.ipAddress_des(by,ethloc);
					
					//ipAddress_sou = heihei.ipAddress_sou(by, ethloc);
					
					//获取传输协议
					//prosty = heihei.prosty(ipheadlen, ethloc, by);
					
					//只对TCP协议做出响应
					//if(prosty.equals("06")){
						
						//如果是TCP协议，则NEW一个TcpSequence来装载读出的数据
						//TcpSequence ts = new TcpSequence();
						
						//获取传输端口
						//port_des = heihei.port_des(ipheadlen, ethloc, by);
						//port_sou = heihei.port_sou(ipheadlen, ethloc, by);	
						
						//提取tcp头长度
						//tcpHeadlen= heihei.tcpHeadlen(by, ethloc, ipheadlen);
						
						//判断SYN ACK FIN标志
						//tcpType = heihei.tcpType(prosty, ethloc, by, ipheadlen, nettype);
						
						//求seq number和ack number	
						//seqnumber = heihei.seqNumber(ethloc, ipheadlen, by);
						//acknumber = heihei.ackNumber(ethloc, ipheadlen, by);
						
						//segementLen:数据长度
						//seglen = len-ipheadlen-tcpHeadlen-14;
						
						//下一分段序号预测
						
						/*long nextseqnumber = seqnumber + seglen;
						if(seglen == 0){
							nextseqnumber +=1;
						}*/
						
						//装载数据
						/*ts.setIndex(j);
						ts.setTcpType(tcpType);
						
						ts.setIpAddress_des(ipAddress_des);
						ts.setIpAddress_sou(ipAddress_sou);
						ts.setMac_des(mac_des);
						ts.setMac_sou(mac_sou);
						ts.setPort_des(port_des);
						ts.setPort_sou(port_sou);
						
						ts.setSeqNumber(seqnumber);
						ts.setAckNumber(acknumber);
						ts.setNextSeqNumber(nextseqnumber);
						ts.setLen(seglen);
						
						listTcp.add(ts);*/
					//}
					
				//}
				
				ss = ss + 16 + len;
				j++;
				L = L + len;
				len = 0;
			}
System.out.print("\n总共帧数：" + (j - 1));

			fis.close();
//			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//return listTcp;
	}

}
