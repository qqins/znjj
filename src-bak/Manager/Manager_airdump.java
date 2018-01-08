package Manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import Model.Model_a;
import Util.DealPcap.wireless802_airdump;

public class Manager_airdump {
	
	
	/*************************************************
	 * 属性定义区
	 *************************************************/
	//未定义属性
	
	List<Model_a> models;
	/*************************************************
	 * end
	 *************************************************/

	
	/*************************************************
	 * 主函数,测试Mnanger功能使用
	 *************************************************/
	public static void main(String args[]){
		Manager_airdump ma = new Manager_airdump();
		
		
		ma.dealByprotocol("1122.pcap","192.168.8.100","06",null);
	}
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * 根据不同协议来遍历
	 *************************************************/
	public void dealByprotocol(String fileName,String hostIP,String net,File file){
		
		wireless802_airdump wa = new wireless802_airdump();
		List<Model_a> models;
		Manager_airdump ma = new Manager_airdump();
		
		models = wa.ergodic(fileName,hostIP,net,null);
System.out.println("包含有效帧："+models.size());

		List<Model_a> findls = ma.find(models);
		System.out.println("共有:"+findls.size()+"帧不同");
		
		
		List<Model_a> dealls = ma.deal(findls);
		System.out.println("共有"+dealls.size()+"种不同长度帧:");	
		
		
		int[] statistical = ma.statistical(findls, dealls);
		
		/*************************************************
		 * 分析携带数据的帧的不同并且显示
		 *************************************************/
		if(net.equals("06")){
					
			for(int i = 0; i<dealls.size();i++ ){	
			System.out.println(" "+dealls.get(i).getTotalLen()+" | "+statistical[i]+"  数据类型 |"+dealls.get(i).getaLayer().getTcpflag()+"|");				
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
	 * 该函数用来提取最后的SEQ和AC
	 *************************************************/
	public static void findSeqAck(List<Model_a> models){
		
		System.out.println("最后一帧序号"+models.get((models.size()-1)).getIndex());
		System.out.print(" 传输方向"+models.get((models.size()-1)).getwLayer().getTOorFrom());
				if(models.get((models.size()-1)).getwLayer().getTOorFrom().equals("10")){
			
		System.out.println(" from AP");	
				}else if(models.get((models.size()-1)).getwLayer().getTOorFrom().equals("01")){
					
					
		System.out.println(" To AP");				
				}
		System.out.println("  帧类型|"+models.get((models.size()-1)).getaLayer().getTcpflag());
		System.out.println("  源IP |"+models.get((models.size()-1)).getnLayer().getIp_sou()
						+"|   目的IP |"+models.get((models.size()-1)).getnLayer().getIp_des()+"|");
				
		System.out.println("  SEQ |"+models.get((models.size()-1)).getaLayer().getSeqnumber()+
						"|  ACK |"+models.get((models.size()-1)).getaLayer().getAcknumber()+"|");
		
			
	}
	
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	
	/*************************************************
	 * 把主函数的功能对外提供使用(不显示分析的数据,但提供分析结果)
	 * file1 和 hostIP1代表要提取的数据的文件和主机IP
	 * file2 和 hostIP2代表需要计算SEQ和ACK 以及主机IP
	 * 两者可以一样，也可以不同，即可以从一个pcap包获取要伪造的
	 * 数据，另一个pacp包来得到SEQ和ACK，也可以2者从同一个
	 * pcap包获取
	 *************************************************/
	public  List<Model_a> run(String file1 , String hostIP1,String file2 , String hostIP2){
		
		wireless802_airdump wa = new wireless802_airdump();
		List<Model_a> models;
		Manager_airdump ma = new Manager_airdump();
		
		//这个models用于提取要要发送的数据
		models = wa.ergodic(file1,hostIP1,"06",null);
		
		//这个models用于提取包中的SEQ和ACK，两个MODELS可以相同，也可以为不同的
		//默认是相同的 
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
	 * 把主函数的功能对外提供使用(不显示分析的数据,但提供分析结果)
	 * file1 和 hostIP1代表要提取的数据的文件和主机IP
	 * file2 和 hostIP2代表需要计算SEQ和ACK 以及主机IP
	 * 两者可以一样，也可以不同，即可以从一个pcap包获取要伪造的
	 * 数据，另一个pacp包来得到SEQ和ACK，也可以2者从同一个
	 * pcap包获取
	 *************************************************/
	public  List<Model_a> run(String file1 , String hostIP1){
		
		wireless802_airdump wa = new wireless802_airdump();
		List<Model_a> models;
		Manager_airdump ma = new Manager_airdump();
		
		//这个models用于提取要要发送的数据
		models = wa.ergodic(file1,hostIP1,"06",null);
		
		//这个models用于提取包中的SEQ和ACK，两个MODELS可以相同，也可以为不同的
		//默认是相同的 
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
	 * 该函数用来提取指定方向的TCP，比如只提取发往主机的TCP
	 *************************************************/
	
	
	
	
	/*************************************************
	 * end
	 *************************************************/
	
                 
	/*************************************************
	 * 该函数用于判断两个list是否相等
	 *************************************************/
	public String list_equal(List ls1,List ls2){
		
		StringBuilder sb = new StringBuilder();
		
		int flag=0;
		
		if(ls1.size() != ls2.size()){
			
			//两个list长度不等，则2者不等
			sb.append("01");
		}
		else{
			//两个list长度相等，择判断其内容是否相等
			for(int i=0;i<ls1.size();i++){
				
				if(ls1.get(i).equals(ls2.get(i))){
					//相等就继续判断下一位	
					continue;
				}
				else{
					//不相等就返回其位置和不相等的元素
					
					sb.append("		第"+(i+1)+"位不同  ");
					sb.append("对应值:"+ls1.get(i)+"   "+ls2.get(i));
					flag++;
				}
			}
			
		}
		
		if(flag==0){
			
			if(sb.length() == 0)
			sb.append("00");
		}
		else {
			sb.append("   共"+flag+"处不同");
		}
		return sb.toString();
	}
	/*************************************************
	 * end
	 *************************************************/
	
	
	
	/*************************************************
	 * 该函数用于检测是所有提取数据,并对比
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
							//相同不做处理
System.out.println(" "+models.get(i).getIndex()+"帧已被加入 与"+kinds.get(j).getIndex()+"帧相同"+"  帧长度为:"+kinds.get(j).getTotalLen());		
							continue;
						}
						else {
							//不同
							if(!ss.equals("01")){
System.out.println(" "+models.get(i).getIndex()+" 帧与"+kinds.get(j).getIndex()+"帧长度相同"+"  帧长度为:"+kinds.get(j).getTotalLen()+"  "+ss);	
							}
							flag++;
							continue;
						}
					}	
				    if(flag == kinds.size()){
				    	//检测出与所有的都不同,就把这个加入
				    	kinds.add(models.get(i));
System.out.println("不同的数据加入,帧序号:"+models.get(i).getIndex()+"  帧长度为:"+models.get(i).getTotalLen());
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
	 * 该函数用于检测是所有提取数据,并对比(不打印后台数据,提供给run使用)
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
							//相同不做处理
//System.out.println(" "+models.get(i).getIndex()+"帧已被加入 与"+kinds.get(j).getIndex()+"帧相同"+"  帧长度为:"+kinds.get(j).getTotalLen());		
							continue;
						}
						else {
							//不同
							if(!ss.equals("01")){
//System.out.println(" "+models.get(i).getIndex()+" 帧与"+kinds.get(j).getIndex()+"帧长度相同"+"  帧长度为:"+kinds.get(j).getTotalLen()+"  "+ss);	
							}
							flag++;
							continue;
						}
					}	
				    if(flag == kinds.size()){
				    	//检测出与所有的都不同,就把这个加入
				    	kinds.add(models.get(i));
//System.out.println("不同的数据加入,帧序号:"+models.get(i).getIndex()+"  帧长度为:"+models.get(i).getTotalLen());
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
	 * 该函数用来处理提取出不的同帧
	 *************************************************/
	
	public List<Model_a> deal(List<Model_a> models){

		List<Model_a> kinds = new ArrayList<Model_a>();
		
		if(models.size() != 0 ){
			
			for(int i = 0;i<models.size();i++){
				
				int  flag =0;

				for(int j=0;j<kinds.size();j++){
					if(kinds.get(j).getData().size() ==  models.get(i).getData().size()){
							
						//长度相同不加入
					}else{
						flag++;
					}
					
				}	
			   
				if(flag == kinds.size()){
					
					kinds.add(models.get(i));
//System.out.println("不同的数据加入,帧序号:"+models.get(i).getIndex()+"  帧长度为:"+models.get(i).getTotalLen());
				}
				
			}
			
		}	
		return kinds;	
	}
	/*************************************************
	 * end
	 *************************************************/
	
	
	/*************************************************
	 * 该函数用于统计不同帧长长度的数量
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
	 *该函数显示不同的帧中的数据模版
	 *************************************************/
	
	public void dispaly(List<Model_a> kinds){
		
		for(int i = 0;i<kinds.size();i++){
			
			List<String> data =  kinds.get(i).getData();
System.out.print(" 第"+i+"类型,帧长度|"+kinds.get(i).getTotalLen()+"|  数据长度|"+kinds.get(i).getData().size()+"|");
System.out.println("  模版序号|"+kinds.get(i).getIndex()+"|");
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
