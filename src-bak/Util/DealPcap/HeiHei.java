package Util.DealPcap;

import java.io.File;
import java.io.FileInputStream;
import java.awt.image.BufferStrategy;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.omg.CORBA.PUBLIC_MEMBER;

/*
 * //�ļ�ͷ ���ݰ�ͷ+���ݱ�  ���ݰ�ͷ +���ݱ�  .......
 	pcap���ṹΪ��24�̶��ֽڵ�Pcapͷ+16�̶��ֽڵ�����ͷ+�������ݣ���mac֡��ip֡��tcp֡��http֡�ȣ�
  */
public class HeiHei {
	
	// LΪǰ��֡���������ܺͣ����������ݰ�ͷ����jΪ��Ҫ�����֡�Ĵ��ţ��˷��������j֡�������ݵĳ���
	// �˷�������������ȫ���Ƶ�������
	public static int frameLength(byte[] by, int L, int j) {
		
		
		// lenΪ���صĵ�j֡�������ݵĳ���
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
System.out.print(len + "�ֽ�");
		return len;
	}
	
	
	/*************************************************
	 * MAC��ַ��ѯ
	 *************************************************/
	public String macString_des(int ethloc,byte by[]){
		StringBuilder sb = new StringBuilder();
		sb.append("	\tDes Mac");
		//6���ֽڵ�MAC��ַ��Ϣ
		for (int m = 0; m < 6; m++) {
			sb.append(":"+Integer.toString((by[ethloc + m] & 0xff) + 0x100, 16).substring(1));	
		}
System.out.print(sb.toString());
		return sb.toString();
		
	}
	
	
	
	public String macString_sou(int ethloc,byte by[]){
		StringBuilder sb = new StringBuilder();
		sb.append("	Sou Mac");
		//6���ֽڵ�MAC��ַ��Ϣ
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
	 * �ú�����ѯ���������(PPPOE����ֱ��ipv4)
	 *************************************************/
	public String netType(int ethloc,byte by[]){
		
		String nettype;
		nettype = Integer.toString((by[ethloc + 12] & 0xff) + 0x100, 16).substring(1)
				+ Integer.toString((by[ethloc + 13] & 0xff) + 0x100, 16).substring(1);
		
System.out.print("\t���������" + nettype);
		
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
	 * �ú�������IP��ַ
	 *************************************************/
public String ipAddress_sou(byte by[],int ethloc){
	
	

	StringBuilder sb = new StringBuilder();
	sb.append(" Դip��ַ��");
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
	sb.append("	Ŀ��ip��ַ��");
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
	 * �ú������IPͷ����
	 *************************************************/
public int iplength(byte by[],int ethloc){
	
		int ipheadlen = 0;
		String s1 = Integer.toString((by[ethloc + 14] & 0xff) + 0x100, 16).substring(1);
		ipheadlen = 4 * Integer.parseInt(s1.substring(1), 16);
System.out.print("\tipͷ����:" + ipheadlen + "�ֽ�");
		return ipheadlen;
	
}
	/*************************************************
	 * end
	 *************************************************/

/*************************************************
 * �ú�������˿ںźʹ���Э��
 *************************************************/
public String prosty(int ipheadlen,int ethloc,byte by[]){
	
	String prosty = Integer.toString((by[ethloc+23] & 0xff) + 0x100, 16).substring(1);
System.out.print(" \tЭ�飺" + prosty+" ");
	return prosty;
	
}

public String port_sou(int ipheadlen,int ethloc,byte by[]){
	

System.out.print("Դ�˿ڣ�");
	StringBuffer port1 = new StringBuffer();
	for (int i = 0; i < 2; i++) {
		port1.append(Integer.toString((by[ethloc+14 + i + ipheadlen] & 0xff) + 0x100, 16)
				.substring(1));
	}
System.out.print(Integer.parseInt(port1.toString(), 16));
	
	return port1.toString();
	
}


public String port_des(int ipheadlen,int ethloc,byte by[]){
	
	
System.out.print("\tĿ�Ķ˿ڣ�");
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
 * �ú������TCPͷ������
 *************************************************/
public int tcpHeadlen(byte by[],int ethloc,int ipheadlen){
	
		String x1 = Integer.toString((by[ethloc+26 + ipheadlen] & 0xff) + 0x100, 16)
				.substring(1);
		String x2 = x1.substring(1) + x1.substring(0, 1);
		int tcpheadlen = 4 * Integer.parseInt(x2, 16);
System.out.print("\ttcpͷ����");
System.out.print(tcpheadlen + "�ֽ�");
		return tcpheadlen;
	
}
/*************************************************
 * end
 *************************************************/


/*************************************************
 * �ú�����TCP���ͽ����ж�(SYN  SYN/ACK ACK PUSH/ACK FIN)
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
		
		//02��SYN��־
		if(tcpType.equals("02")){
System.out.print(" \tSYNλΪ1��TCP��ʼλ(����)");
			
		}
		
		//12ΪSYN/ACK
		else if(tcpType.equals("12")){
			
			
		}
		//10ΪACK
		else if(tcpType.equals("10")){
				
		}
		//18ΪPUSH/ACK
		else if(tcpType.equals("18")){
				
		}
		//11ΪFIN/ACK��������
		else if(tcpType.equals("11")){
			
System.out.print(" \tFINλΪ1��TCP����");
		}
		
		return tcpType;
		
	}
	return "0";
	
}
/*************************************************
 * end
 *************************************************/

/*************************************************
 * �ú������seq num ��ACK num
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
	 * ��ԭ������������Ϊ����������
	 * �ҳ���TCP��ÿһ֡
	 * ������Manager����
	 *************************************************/

public static void main(String[] args) {
		
		/*************************************************
		 * ������
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
		 * ��ʼ��
		 *************************************************/
		try {
//			if (!file2.exists()) {
//				file2.createNewFile();
//			}
			FileInputStream fis = new FileInputStream(file1);

//			FileOutputStream fos = new FileOutputStream(file2);
			int size = fis.available();
System.out.println("��" + size);
			byte by[] = new byte[size];
			fis.read(by);
		/*************************************************
		 * end
		 *************************************************/
			
			
			
			while (ss < 200000) {
System.out.print("\n" + "��" + j + "֡����ȥ�����ݰ�ͷ����" );
				
				//��һ��ѭ��ʱ  by�Ƕ�����������,L=0��j=1
				len = HeiHei.frameLength(by, L, j);
				
				// ��ǰ֡����������ʼλ�ã�ȥ��pcapͷ�����ݰ�ͷ
				//ethloc = 40 + L + 16 * (j - 1);
				
				//MAC��ַ
				//mac_des = heihei.macString_des(ethloc, by);
				//mac_sou = heihei.macString_sou(ethloc, by);
				
				//���������
				/*nettype = heihei.netType(ethloc, by);
				
				if(nettype.equals("0021")){
					ethloc += 8;
				}
				*/
				//ֻ��ʾIPV4��
				//if(nettype.equals("0800") || nettype.equals("0021")){
					//��ȡIPͷ����
					//ipheadlen = heihei.iplength(by, ethloc);
							
					//��ȡIP��ַ
					//ipAddress_des = heihei.ipAddress_des(by,ethloc);
					
					//ipAddress_sou = heihei.ipAddress_sou(by, ethloc);
					
					//��ȡ����Э��
					//prosty = heihei.prosty(ipheadlen, ethloc, by);
					
					//ֻ��TCPЭ��������Ӧ
					//if(prosty.equals("06")){
						
						//�����TCPЭ�飬��NEWһ��TcpSequence��װ�ض���������
						//TcpSequence ts = new TcpSequence();
						
						//��ȡ����˿�
						//port_des = heihei.port_des(ipheadlen, ethloc, by);
						//port_sou = heihei.port_sou(ipheadlen, ethloc, by);	
						
						//��ȡtcpͷ����
						//tcpHeadlen= heihei.tcpHeadlen(by, ethloc, ipheadlen);
						
						//�ж�SYN ACK FIN��־
						//tcpType = heihei.tcpType(prosty, ethloc, by, ipheadlen, nettype);
						
						//��seq number��ack number	
						//seqnumber = heihei.seqNumber(ethloc, ipheadlen, by);
						//acknumber = heihei.ackNumber(ethloc, ipheadlen, by);
						
						//segementLen:���ݳ���
						//seglen = len-ipheadlen-tcpHeadlen-14;
						
						//��һ�ֶ����Ԥ��
						
						/*long nextseqnumber = seqnumber + seglen;
						if(seglen == 0){
							nextseqnumber +=1;
						}*/
						
						//װ������
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
System.out.print("\n�ܹ�֡����" + (j - 1));

			fis.close();
//			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//return listTcp;
	}

}
