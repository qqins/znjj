package Util.Shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import sun.awt.windows.ThemeReader;

public class ReadShell {
	

	/*public static void main(String args[]) {
		ReadShell  rs = new ReadShell();
		
		rs.readCMD("ps -aux");	
	}*/
	private Process process = null;
	private int delayToDestroy;
	private int flag;
	
	private List<String> processList = new ArrayList<String>();
	
	public ReadShell() {
		super();
		
	}
	
	public ReadShell(int time ) {
		super();
		this.delayToDestroy = time;
	}
	
	public ReadShell(int time ,int flag) {
		super();
		this.delayToDestroy = time;
		this.flag= flag;
	}


	/*************************************************
	 * ִ�е�����shell����,����ʾ���
	 *************************************************/
	public List<String>  readCMD(String cmd){
		
		//Process process = null;
		
		try {
			process = Runtime.getRuntime().exec(cmd);
			
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
						String line = "";
						try {
							while ((line = input.readLine()) != null) {
								processList.add(line);
								//System.out.println(line);
							}
						} catch (IOException e) {
							//e.printStackTrace();
							System.out.println("capture stop!");
						}
						
						try {
							input.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}).start();
				
			//}
			
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						BufferedReader input1 = new BufferedReader(new InputStreamReader(process.getErrorStream()));
						String line1 = "";
						try {
							while ((line1 = input1.readLine()) != null) {
								//processList.add(line);
								//System.out.println(line1);
							}
						} catch (IOException e) {
							//e.printStackTrace();
							System.out.println("capture stop!");
						}
						try {
							input1.close();
						} catch (IOException e) {
							//e.printStackTrace();
						}
					}
				}).start();
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						
						try {
							if(delayToDestroy!=0){
								System.out.println("cmd exist time:"+delayToDestroy);
								Thread.sleep(delayToDestroy);
								process.destroy();
							}else {
								System.out.println("cmd can't stop auto until finish");
							}
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
				
			int exit = process.waitFor();
			//boolean exit = process.waitFor(20, TimeUnit.SECONDS);
			System.out.println("cmd:"+cmd+"  exit:"+exit);
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*for (String line : processList) {
			System.out.println(line);
		}*/
		
		return processList;
	}
	
	
	/*************************************************
	 * end
	 *************************************************/
	
	//destroy
	public void destroy(){
		
		if(this.process!=null){
			
			process.destroy();
		}
	}
	
	
	/*************************************************
	 * ִ��һ���ű����������ʾ���ؽ��
	 *************************************************/
	public void  readSH(String shpath){
		
		try {
			
			//"nohup /pmapp/liuyi/java/t.sh&";  ksh
			String[] cmd = {"/bin/bash", shpath};
			Process pcs = Runtime.getRuntime().exec(cmd);
			
			//timer1(pcs);	
			
			try {
				int extValue = pcs.waitFor();
				
				System.out.println("shell---"+" exit:"+extValue);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.out.println("�ű�ִ���쳣");
				e.printStackTrace();
			} //������ 0 ��ʾ�����˳� 1��ʾ�쳣�˳�
			
			
			
			/*InputStreamReader ir = new InputStreamReader(pcs.getInputStream());    
			LineNumberReader input = new LineNumberReader(ir);    
			String line = null;  */
			
			//��ӡ��̨����
			/*while ((line = input.readLine()) != null){    
			System.out.println(line);    
			}  
			
			if(null != input){    
			input.close();    
			}

			if(null != ir){    
			ir.close();    
			} 
			*/
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("�ű���ȡ�쳣");
			e.printStackTrace();
		}
		
		
	}
	
	public void timer1(final Process pcs) {
	    final Timer timer = new Timer();
	    timer.schedule(new TimerTask() {
	      public void run() {
	       pcs.destroy();
	        timer.cancel();
	        
	      
			//System.out.println("shijiandao");
	      }
	    }, 10000);// �趨ָ����ʱ��time,�˴�Ϊ2000����	  
}
	
	
	/*************************************************
	 * end
	 *************************************************/

}
