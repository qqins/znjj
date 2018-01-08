package Manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import Model.Model_a;
import Util.DealPcap.wireless802_airdump;

public class Manager_airdump {
	
	
	/*************************************************
	 * ���Զ�����
	 *************************************************/
	//δ��������
	
	List<Model_a> models;
	/*************************************************
	 * end
	 *************************************************/

	
	/*************************************************
	 * ������,����Mnanger����ʹ��
	 *************************************************/
	public static void main(String args[]){
		Manager_airdump ma = new Manager_airdump();
		
		
		ma.dealByprotocol("1122.pcap","192.168.8.100","06",null);
	}
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * ���ݲ�ͬЭ��������
	 *************************************************/
	public void dealByprotocol(String fileName,String hostIP,String net,File file){
		
		wireless802_airdump wa = new wireless802_airdump();
		List<Model_a> models;
		Manager_airdump ma = new Manager_airdump();
		
		models = wa.ergodic(fileName,hostIP,net,null);
System.out.println("������Ч֡��"+models.size());

		List<Model_a> findls = ma.find(models);
		System.out.println("����:"+findls.size()+"֡��ͬ");
		
		
		List<Model_a> dealls = ma.deal(findls);
		System.out.println("����"+dealls.size()+"�ֲ�ͬ����֡:");	
		
		
		int[] statistical = ma.statistical(findls, dealls);
		
		/*************************************************
		 * ����Я�����ݵ�֡�Ĳ�ͬ������ʾ
		 *************************************************/
		if(net.equals("06")){
					
			for(int i = 0; i<dealls.size();i++ ){	
			System.out.println(" "+dealls.get(i).getTotalLen()+" | "+statistical[i]+"  �������� |"+dealls.get(i).getaLayer().getTcpflag()+"|");				
					}
					
			ma.dispaly(dealls);		
		}
		else if(net.equals("11")){
			for(int i = 0; i<dealls.size();i++ ){	
				System.out.println(" "+dealls.get(i).getTotalLen()+" | "+statistical[i]+"|");				
						}
			ma.dispaly(dealls);	
		}
		
		
	}
	
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	
	/*************************************************
	 * �ú���������ȡ����SEQ��AC
	 *************************************************/
	public static void findSeqAck(List<Model_a> models){
		
		System.out.println("���һ֡���"+models.get((models.size()-1)).getIndex());
		System.out.print(" ���䷽��"+models.get((models.size()-1)).getwLayer().getTOorFrom());
				if(models.get((models.size()-1)).getwLayer().getTOorFrom().equals("10")){
			
		System.out.println(" from AP");	
				}else if(models.get((models.size()-1)).getwLayer().getTOorFrom().equals("01")){
					
					
		System.out.println(" To AP");				
				}
		System.out.println("  ֡����|"+models.get((models.size()-1)).getaLayer().getTcpflag());
		System.out.println("  ԴIP |"+models.get((models.size()-1)).getnLayer().getIp_sou()
						+"|   Ŀ��IP |"+models.get((models.size()-1)).getnLayer().getIp_des()+"|");
				
		System.out.println("  SEQ |"+models.get((models.size()-1)).getaLayer().getSeqnumber()+
						"|  ACK |"+models.get((models.size()-1)).getaLayer().getAcknumber()+"|");
		
			
	}
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	
	/*************************************************
	 * ���������Ĺ��ܶ����ṩʹ��(����ʾ����������,���ṩ�������)
	 * file1 �� hostIP1����Ҫ��ȡ�����ݵ��ļ�������IP
	 * file2 �� hostIP2������Ҫ����SEQ��ACK �Լ�����IP
	 * ���߿���һ����Ҳ���Բ�ͬ�������Դ�һ��pcap����ȡҪα���
	 * ���ݣ���һ��pacp�����õ�SEQ��ACK��Ҳ����2�ߴ�ͬһ��
	 * pcap����ȡ
	 *************************************************/
	public  List<Model_a> run(String file1 , String hostIP1,String file2 , String hostIP2){
		
		wireless802_airdump wa = new wireless802_airdump();
		List<Model_a> models;
		Manager_airdump ma = new Manager_airdump();
		
		//���models������ȡҪҪ���͵�����
		models = wa.ergodic(file1,hostIP1,"06",null);
		
		//���models������ȡ���е�SEQ��ACK������MODELS������ͬ��Ҳ����Ϊ��ͬ��
		//Ĭ������ͬ�� 
		this.models = wa.ergodic(file2,hostIP2,"06",null);
		
		List<Model_a> findls = ma.find_run(models);
		List<Model_a> dealls = ma.deal(findls);
		//int[] statistical = ma.statistical(findls, dealls);	
		
		return dealls;
	}
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	/*************************************************
	 * ���������Ĺ��ܶ����ṩʹ��(����ʾ����������,���ṩ�������)
	 * file1 �� hostIP1����Ҫ��ȡ�����ݵ��ļ�������IP
	 * file2 �� hostIP2������Ҫ����SEQ��ACK �Լ�����IP
	 * ���߿���һ����Ҳ���Բ�ͬ�������Դ�һ��pcap����ȡҪα���
	 * ���ݣ���һ��pacp�����õ�SEQ��ACK��Ҳ����2�ߴ�ͬһ��
	 * pcap����ȡ
	 *************************************************/
	public  List<Model_a> run(String file1 , String hostIP1){
		
		wireless802_airdump wa = new wireless802_airdump();
		List<Model_a> models;
		Manager_airdump ma = new Manager_airdump();
		
		//���models������ȡҪҪ���͵�����
		models = wa.ergodic(file1,hostIP1,"06",null);
		
		//���models������ȡ���е�SEQ��ACK������MODELS������ͬ��Ҳ����Ϊ��ͬ��
		//Ĭ������ͬ�� 
		//this.models = wa.ergodic(file2,hostIP2,"06",null);
		
		List<Model_a> findls = ma.find_run(models);
		List<Model_a> dealls = ma.deal(findls);
		//int[] statistical = ma.statistical(findls, dealls);	
		
		return dealls;
	}
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * �ú���������ȡָ�������TCP������ֻ��ȡ����������TCP
	 *************************************************/
	
	
	
	
	/*************************************************
	 * end
	 *************************************************/
	
                 
	/*************************************************
	 * �ú��������ж�����list�Ƿ����
	 *************************************************/
	public String list_equal(List ls1,List ls2){
		
		StringBuilder sb = new StringBuilder();
		
		int flag=0;
		
		if(ls1.size() != ls2.size()){
			
			//����list���Ȳ��ȣ���2�߲���
			sb.append("01");
		}
		else{
			//����list������ȣ����ж��������Ƿ����
			for(int i=0;i<ls1.size();i++){
				
				if(ls1.get(i).equals(ls2.get(i))){
					//��Ⱦͼ����ж���һλ	
					continue;
				}
				else{
					//����Ⱦͷ�����λ�úͲ���ȵ�Ԫ��
					
					sb.append("		��"+(i+1)+"λ��ͬ  ");
					sb.append("��Ӧֵ:"+ls1.get(i)+"   "+ls2.get(i));
					flag++;
				}
			}
			
		}
		
		if(flag==0){
			
			if(sb.length() == 0)
			sb.append("00");
		}
		else {
			sb.append("   ��"+flag+"����ͬ");
		}
		return sb.toString();
	}
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	/*************************************************
	 * �ú������ڼ����������ȡ����,���Ա�
	 *************************************************/
	public List<Model_a> find(List<Model_a> models){
		
		List<Model_a> kinds = new ArrayList<Model_a>();
		
		if(models.size() != 0 ){
			

			for(int i = 0;i<models.size();i++){
				
				int  flag =0;
				String ss = null;
				if(models.get(i).getData() != null){
					/*if(kinds.size() == 0){
						kinds.add(models.get(i));
					}
					*/
					for(int j=0;j<kinds.size();j++){
						ss = list_equal(models.get(i).getData(), kinds.get(j).getData());
						if(ss.equals("00")){
							//��ͬ��������
System.out.println(" "+models.get(i).getIndex()+"֡�ѱ����� ��"+kinds.get(j).getIndex()+"֡��ͬ"+"  ֡����Ϊ:"+kinds.get(j).getTotalLen());		
							continue;
						}
						else {
							//��ͬ
							if(!ss.equals("01")){
System.out.println(" "+models.get(i).getIndex()+" ֡��"+kinds.get(j).getIndex()+"֡������ͬ"+"  ֡����Ϊ:"+kinds.get(j).getTotalLen()+"  "+ss);	
							}
							flag++;
							continue;
						}
					}	
				    if(flag == kinds.size()){
				    	//���������еĶ���ͬ,�Ͱ��������
				    	kinds.add(models.get(i));
System.out.println("��ͬ�����ݼ���,֡���:"+models.get(i).getIndex()+"  ֡����Ϊ:"+models.get(i).getTotalLen());
				    }
				}
			}
			
		}	
		
		
		return kinds;
	}
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	
	/*************************************************
	 * �ú������ڼ����������ȡ����,���Ա�(����ӡ��̨����,�ṩ��runʹ��)
	 *************************************************/
	public List<Model_a> find_run(List<Model_a> models){
		
		List<Model_a> kinds = new ArrayList<Model_a>();
		
		if(models.size() != 0 ){
			

			for(int i = 0;i<models.size();i++){
				
				int  flag =0;
				String ss = null;
				if(models.get(i).getData() != null){
					/*if(kinds.size() == 0){
						kinds.add(models.get(i));
					}
					*/
					for(int j=0;j<kinds.size();j++){
						ss = list_equal(models.get(i).getData(), kinds.get(j).getData());
						if(ss.equals("00")){
							//��ͬ��������
//System.out.println(" "+models.get(i).getIndex()+"֡�ѱ����� ��"+kinds.get(j).getIndex()+"֡��ͬ"+"  ֡����Ϊ:"+kinds.get(j).getTotalLen());		
							continue;
						}
						else {
							//��ͬ
							if(!ss.equals("01")){
//System.out.println(" "+models.get(i).getIndex()+" ֡��"+kinds.get(j).getIndex()+"֡������ͬ"+"  ֡����Ϊ:"+kinds.get(j).getTotalLen()+"  "+ss);	
							}
							flag++;
							continue;
						}
					}	
				    if(flag == kinds.size()){
				    	//���������еĶ���ͬ,�Ͱ��������
				    	kinds.add(models.get(i));
//System.out.println("��ͬ�����ݼ���,֡���:"+models.get(i).getIndex()+"  ֡����Ϊ:"+models.get(i).getTotalLen());
				    }
				}
			}
			
		}	
		
		
		return kinds;
	}
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	
	/*************************************************
	 * �ú�������������ȡ������ͬ֡
	 *************************************************/
	
	public List<Model_a> deal(List<Model_a> models){

		List<Model_a> kinds = new ArrayList<Model_a>();
		
		if(models.size() != 0 ){
			
			for(int i = 0;i<models.size();i++){
				
				int  flag =0;

				for(int j=0;j<kinds.size();j++){
					if(kinds.get(j).getData().size() ==  models.get(i).getData().size()){
							
						//������ͬ������
					}else{
						flag++;
					}
					
				}	
			   
				if(flag == kinds.size()){
					
					kinds.add(models.get(i));
//System.out.println("��ͬ�����ݼ���,֡���:"+models.get(i).getIndex()+"  ֡����Ϊ:"+models.get(i).getTotalLen());
				}
				
			}
			
		}	
		return kinds;	
	}
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * �ú�������ͳ�Ʋ�ͬ֡�����ȵ�����
	 *************************************************/
	public int[] statistical(List<Model_a> models,List<Model_a> kinds){
		
		
		int species = kinds.size();
		int[] flags  = new int[species];
		
		if(kinds.size() != 0){	
			for(int i = 0;i<models.size();i++){
					
				for(int j= 0;j<kinds.size();j++){
					if(models.get(i).getTotalLen()==kinds.get(j).getTotalLen()){
							
						flags[j]++;
					}
				}			
			}
		}
		return flags;	
	}
	
	/*************************************************
	 * end
	 *************************************************/
	
	/*************************************************
	 *�ú�����ʾ��ͬ��֡�е�����ģ��
	 *************************************************/
	
	public void dispaly(List<Model_a> kinds){
		
		for(int i = 0;i<kinds.size();i++){
			
			List<String> data =  kinds.get(i).getData();
System.out.print(" ��"+i+"����,֡����|"+kinds.get(i).getTotalLen()+"|  ���ݳ���|"+kinds.get(i).getData().size()+"|");
System.out.println("  ģ�����|"+kinds.get(i).getIndex()+"|");
			for(int j = 0 ; j<data.size();j++){
				
System.out.print(data.get(j)+" ");
			if((j+1)%16 ==0 && j>0){
System.out.print("  |"+(j+1)+"| ");					
System.out.println(" ");	
					}
				
			}
System.out.println(" ");		
		}
		
		
	}

	public List<Model_a> getModels() {
		return models;
	}

	public void setModels(List<Model_a> models) {
		this.models = models;
	}
	
	/*************************************************
	 * end
	 *************************************************/
}
