package Manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import Model.Model_a;
import Util.DealPcap.wireless802_airdump;

public class Manager_scanForSend {
	
	//public static void readFile(boolean scanFlag){}
	static wireless802_airdump wa = new wireless802_airdump();
	static Long seq = (long) 0;
	static Long ack = (long) 0;
	
	public static void main(String[] args){
			
		  //�Ƿ�����ѭ��ɨ��
		  boolean scanFlag = true;
		  
		  String path="F:\\test_pcap";
		  File file=new File(path);
		  File[] tempList = file.listFiles();
		  int beginLen = beginToScan(path, tempList);
		  
		  
		  while(scanFlag){	
			file = new File(path);
			 tempList = file.listFiles();
			  int nowLen = tempList.length;
			  if(nowLen>beginLen){
				  beginLen = nowLen;
System.out.println("���º���������"+tempList.length); 
				  //��ʾ�������µ�һ���ļ�
					String fileName = tempList[nowLen-1].getName();
System.out.println(fileName);
				  //���µ�ͬʱҪ�������ݰ�����ȡ�����µ�SEQ��ACK,���뵽���߳�
				startThread(null,"192.168.8.101",fileName,beginLen);
			  }else if(nowLen<beginLen){
				  beginLen = nowLen;
System.out.println("���º���������"+tempList.length);
			  }
		  }
	}
	
	
	
	public static int beginToScan(String path,File[] tempList){
		
		  System.out.println("��Ŀ¼�³�ʼ���������"+tempList.length);
		  int beginLen = tempList.length;
		  for (int i = 0; i < tempList.length; i++) {
			   if (tempList[i].isFile()) {
			   //��ȡĳ���ļ����µ������ļ�
			    System.out.println(tempList[i].getName());
			   }
			   
			   if (tempList[i].isDirectory()) {
			    //��ȡĳ���ļ����µ������ļ���
				  System.out.println(tempList[i].getName());
			   }
		 }
		return beginLen;
	}
	
	
	public static void startThread(final File file,final String ip,final String fileName,final int beginLen){
		
		List<Long> ls = new ArrayList<Long>();
		/*************************************************
		 * �ɵ�runableд�����޷���ֵ
		 *************************************************/
		//File[] tl = tempList;
		
		
		java.lang.Runnable run = new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				List<Model_a> models =  wa.ergodic(fileName, ip, "06",null);
				//�ж�flag��ʽ��10Ϊack��18Ϊpush/ack
				int add_seq = 0,add_ack = 0;
				if(models.get((models.size()-1)).getaLayer().getTcpflag().equals("18")){
					if(models.get((models.size()-1)).getwLayer().getTOorFrom().equals("10")){
						add_seq = models.get((models.size()-1)).getData().size();	
					}else if(models.get((models.size()-1)).getwLayer().getTOorFrom().equals("01")){
						add_ack =models.get((models.size()-1)).getData().size();
					}
					
				}
				//ͬ����
				synchronized (seq) {
					
					if(models.get((models.size()-1)).getwLayer().getTOorFrom().equals("10")){//10�� from ap
						if(models.get((models.size()-1)).getaLayer().getSeqnumber() > seq ){
							seq = models.get((models.size()-1)).getaLayer().getSeqnumber()+add_seq;
System.out.println("����seq:"+seq);
						}
					}
					else if(models.get((models.size()-1)).getwLayer().getTOorFrom().equals("01")){//01��to ap
						if(models.get((models.size()-1)).getaLayer().getAcknumber() > seq ){
							seq = models.get((models.size()-1)).getaLayer().getAcknumber()+add_ack;
System.out.println("����seq:"+seq);
						}
					}
				}
				
				
				synchronized (ack) {
					
					if(models.get((models.size()-1)).getwLayer().getTOorFrom().equals("10")){
						if(models.get((models.size()-1)).getaLayer().getAcknumber() > ack){
							ack = models.get((models.size()-1)).getaLayer().getAcknumber();
System.out.println("����ack:"+ack);
						}		
					}else if(models.get((models.size()-1)).getwLayer().getTOorFrom().equals("01")){
						if(models.get((models.size()-1)).getaLayer().getSeqnumber() > ack){
							ack = models.get((models.size()-1)).getaLayer().getSeqnumber();
System.out.println("����ack:"+ack);
						}
						
					}	
				}	
			}
				
		};
		//�����߳�
		 new Thread(run).start();
		/*************************************************
		 * end
		 *************************************************/
		/*ExecutorService executorService = Executors.newCachedThreadPool(); 
		 Future<List<Long>> future = executorService.submit(new Callable<List<Long>>() {  
	            public List<Long> call() throws Exception {  
	            	List<Long> ls = new ArrayList<Long>();
	            	List<Model_a> models =  wa.ergodic(null, "192.168.8.100", "06",file);
	            	long seq = models.get((models.size()-1)).getaLayer().getSeqnumber();
	            	long ack = models.get((models.size()-1)).getaLayer().getAcknumber();
	            	
System.out.println("1:"+seq);
					ls.add(seq);
					ls.add(ack);
	                return ls;  
	            }  
	        });  
		 
		 
		while(true) {
			 if(future.isDone()){
				 try {
System.out.println(future.get().size());
					break;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
			 else{		 
	System.out.println("�̻߳�������");		 
			 }
			
		}
		 */
		
	}	
}
