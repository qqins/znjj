package Util.DealPcap;

import java.io.File;
import java.io.FileInputStream;
import java.awt.image.BufferStrategy;
import java.io.*;
import java.lang.reflect.Method;

/*
 * //�ļ�ͷ ���ݰ�ͷ+���ݱ�  ���ݰ�ͷ +���ݱ�  .......
 	pcap���ṹΪ��24�̶��ֽڵ�Pcapͷ+16�̶��ֽڵ�����ͷ+�������ݣ���mac֡��ip֡��tcp֡��http֡�ȣ�
  */
public class wireless802 {
	
	/*************************************************
	 * �˷��������j֡�������ݵĳ���
	 * LΪǰ��֡���������ܺͣ����������ݰ�ͷ����jΪ��Ҫ�����֡�Ĵ���
	 * �˷�������������ȫ���Ƶ�������
	 *************************************************/
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
		System.out.println(len + "�ֽ�\t");
		return len;
	}
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	/*************************************************
	 * �ú��������ж�802.11������ /802.11b g ������������
	 *************************************************/
	public String channelType(int ethloc,byte by[]){
		
		String nettype;
		
		//����Ƿ�Ϊ802.11bЭ�� 0xa000
		nettype = Integer.toString((by[ethloc + 12] & 0xff) + 0x100, 16).substring(1)
				+ Integer.toString((by[ethloc + 13] & 0xff) + 0x100, 16).substring(1);
//System.out.print(nettype);
		if(nettype.equals("a000")){
			
System.out.print("\t�ŵ����ͣ�802.11b");
			return "802.11b";
		}
		else if(nettype.equals("8004")){
			System.out.print("\t�ŵ����ͣ�802.11g");
			return "802.11g";
		}
		else {
			System.out.print("\t�ŵ����ͣ�other");
			return "other";
		}
		
	}
	/*************************************************
	 * end
	 *************************************************/
	
	/*************************************************
	 * �ú������802.11��֡����     ��ʼλ��+18
	 *************************************************/
	public String frametype(String nettype,byte by[],int ethloc){
		
		String datatype = Integer.toString((by[ethloc] & 0xff) + 0x100, 16).substring(1)
				+ Integer.toString((by[ethloc + 1] & 0xff) + 0x100, 16).substring(1);
		String bString = wireless802.hexString2binaryString(datatype);
		
		//ȡ����ʾ��֡���͵�bitֵ
		String subdatatype = bString.substring(0, 4);
System.out.print("\t	��֡����:"+subdatatype);
		
		String datatype2 = bString.substring(4, 6);
		
		if(datatype2.equals("00")){
			System.out.print("\t֡����:00����֡");
			return "00";
		}
		else if(datatype2.equals("01")){
			System.out.print("\t֡����:01����֡");
			return "01";
		}
		else {
			System.out.print("\t֡����:10����֡");
			return "10";
		}
	
	}
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * �ú����������֡���շ�MAC��ַ+28
	 *************************************************/
	public String macAddress(String frametype,byte by[],int ethloc){
		//����֡
		if(frametype.equals("00")){
			
			StringBuilder sb = new StringBuilder();
			ethloc += 28;
			sb.append("	TansAddress(souAd)");
			for (int m = 0; m < 6; m++) {	
				sb.append(":"+Integer.toString((by[ethloc + m] & 0xff) + 0x100, 16).substring(1));
			}
			
			sb.append("	BSSID");
			for (int n = 6; n < 12; n++) {
				sb.append(":"+Integer.toString((by[ethloc + n] & 0xff) + 0x100, 16).substring(1));
				//System.out.print(":" + Integer.toString((by[ethloc + n] & 0xff) + 0x100, 16).substring(1));
			}
//sb.deleteCharAt(0);		
			System.out.print(sb.toString());
			return sb.toString();
		}
		
		//����֡
		else if(frametype.equals("10")){
			ethloc += 28;
			StringBuilder sb = new StringBuilder();
			sb.append("	TansAddress(BSSID)");
			//System.out.print("\tTansAddress(BSSID)");
			for (int m = 0; m < 6; m++) {	
				sb.append(":"+Integer.toString((by[ethloc + m] & 0xff) + 0x100, 16).substring(1));
			}
			
			sb.append("	souAd");
			for (int n = 6; n < 12; n++) {
				sb.append(":"+Integer.toString((by[ethloc + n] & 0xff) + 0x100, 16).substring(1));
				//System.out.print(":" + Integer.toString((by[ethloc + n] & 0xff) + 0x100, 16).substring(1));
			}
//sb.deleteCharAt(0);		
			System.out.print(sb.toString());
			return sb.toString();
		}
		
		//����֡  δ����RTS ACK��
		else if(frametype.equals("01")){
			ethloc += 22;
			StringBuilder sb = new StringBuilder();
			sb.append("	ReceiverAddress(.)");
			//System.out.print("\tReceiverAddress(.)");
			for (int m = 0; m < 6; m++) {
				sb.append(":"+Integer.toString((by[ethloc + m] & 0xff) + 0x100, 16).substring(1));
				//System.out.print(":" + Integer.toString((by[ethloc + m] & 0xff) + 0x100, 16).substring(1));
			}
			
			System.out.print(sb.toString());
			return sb.toString();
		}
		return null;
	}
	
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * �ú��������жϱ�802.11��װ����ʵЭ������+48
	 *************************************************/
	public String nettype(int ethloc,byte by[]){
		ethloc += 48; //(48) type
		String nettype;
		nettype = Integer.toString((by[ethloc ] & 0xff) + 0x100, 16).substring(1)
				+ Integer.toString((by[ethloc + 1] & 0xff) + 0x100, 16).substring(1);
		
		if(nettype.equals("0806")){
			System.out.print("	ʵ�ʷ�װЭ��:");
			System.out.print("ARPЭ��  ("+nettype+")");
			return "0806";
		}
		else if(nettype.equals("86dd")){
			System.out.print("	ʵ�ʷ�װЭ��:");
			System.out.print("IPv6Э��(SSDP,DHCPV6) ("+nettype+")");
			return "86dd";
		}
		else if(nettype.equals("0800")) {
			System.out.print("	ʵ�ʷ�װЭ��:");
			System.out.print("IPV4Э��(NBNS,LLMNR,IGMPv3,QICQ) ("+nettype+")");
			return "0800";
		}
		return "is802.11";
		
	}
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	/*************************************************
	 * �ú�����16���Ƶ�ת��Ϊ2����
	 *************************************************/
	public static  String hexString2binaryString(String hexString)
	{
		if (hexString == null || hexString.length() % 2 != 0)
			return null;
		String bString = "", tmp;
		for (int i = 0; i < hexString.length(); i++)
		{
			tmp = "0000"
					+ Integer.toBinaryString(Integer.parseInt(hexString
							.substring(i, i + 1), 16));
			bString += tmp.substring(tmp.length() - 4);
		}
		return bString;
	}	
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * �ú�����ȡradiotap header����
	 *************************************************/
	public int radioHead(int ethloc ,byte by[]){
		String ss;
		ss = Integer.toString((by[ethloc + 3 ] & 0xff) + 0x100, 16).substring(1)
				+ Integer.toString((by[ethloc + 2] & 0xff) + 0x100, 16).substring(1);
		
		int radioHead = Integer.parseInt(ss, 16);
System.out.print("   radiotap headLen:"+radioHead);		
		return radioHead;
	}
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * �ú�����ȡ�����־flag
	 *************************************************/
	public void flags(int ethloc,byte by[]){
		
		
		String ss = Integer.toString((by[ethloc + 1] & 0xff) + 0x100, 16).substring(1);
				
		String bString = wireless802.hexString2binaryString(ss);
		
System.out.print("	flags:"+bString);

		String protect = bString.substring(1, 2);
System.out.print("	protect:"+protect);	
		String to = bString.substring(6, 8);
		
		if(to.equals("01")){
System.out.print("	To   AP:"+to);		
		}
		else if(to.equals("10")){
System.out.print("	From AP:"+to);		
		}
		else{
System.out.print("	        ");	
		}

	}
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	/*************************************************
	 * ������
	 *************************************************/
	public static void main(String[] args) {
		
		/*************************************************
		 * ���������
		 *************************************************/
		File file1 = new File("F:\\222.pcap");
		//File file2 = new File("F:\\result.txt");
		int ss = 24;
		int L = 0;
		
		int len;
		int j = 1;
		int ethloc;
		int radioHead;
		
		// String iplen;
		String channeltype;
		String nettype;
		String frametype;
		String macAddress;
		String flags;
		/*************************************************
		 * end
		 *************************************************/
		
		
		/*************************************************
		 * ���ݳ�ʼ������
		 *************************************************/
		wireless802 wireless802 = new wireless802();
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
			
			/*************************************************
			 * ѭ����ʼ����ÿһ֡����
			 *************************************************/
			while (ss < size) {
				System.out.print("\n" + "��" + j + "֡����ȥ�����ݰ�ͷ����");
				
				//��һ��ѭ��ʱ  by�Ƕ�����������,L=0��j=1,�����ÿһ֡�ĳ���
				len = wireless802.frameLength(by, L, j);
				
				// ��ǰ֡����������ʼλ�ã�ȥ��pcapͷ�����ݰ�ͷ
				ethloc = 40 + L + 16 * (j - 1);
				
				//��ȡradioHeader����
				radioHead = wireless802.radioHead(ethloc, by);
				
				
				//�ж��ŵ�����
				channeltype  = wireless802.channelType(ethloc, by);
				
				//�ж�֡����
				ethloc += radioHead;
				frametype = wireless802.frametype(channeltype, by, ethloc);
				
				//��ȡ������Ϣ
				wireless802.flags(ethloc, by);

				//��Ѱ��ʵЭ������,ֻ��802.11���д���,��װΪ����Э����ݲ�����
				//nettype = wireless802.nettype(ethloc, by);
				
				//��802.11Э���֡�ķ���
			/*	if(nettype.equals("is802.11")){
					//MAC��ַ
					macAddress = wireless802.macAddress(frametype, by, ethloc);
				}
				
				//�Թ���֡����
				if(frametype.equals("00")){
					
				//��Ҫ��������ݴ���	
System.out.print("	�������ݴ���");
				}*/
				
				
				//����һ֡��ʼλ�õ���
				ss = ss + 16 + len;
				j++;
				L = L + len;
				len = 0;
System.out.println("");				
			}
			/*************************************************
			 * end
			 *************************************************/
			
			System.out.print("\n�ܹ�֡����" + (j - 1));

			fis.close();
//			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
