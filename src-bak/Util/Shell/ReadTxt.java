package Util.Shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ReadTxt {

	public static void main(String[] args) throws Exception {

		int result = new ReadTxt().readByPath_line("F:\\test_pcap\\print1.txt",6);
		if(result>0){
			System.out.println("handshake："+result);
		}else {
			System.out.println("handshake："+result);
		}
	}
	
	
	
	/*************************************************
	 * 读取指定行
	 *************************************************/
	public void readByPath(String filePath) {
		
		//1.取文件句柄
		//2.获得文件句柄当做是输入一个字节码流，需要对这个输入流进行读取
		//3.读取输入流，生成字节流
		//4.一行行的读取
		
		File file = new File(filePath);
		
		if(file.isFile()&&file.exists()){
			
			FileInputStream fi;
			try {
				fi = new FileInputStream(file);
				InputStreamReader ir = new InputStreamReader(fi, "UTF-8");
				
				BufferedReader br = new BufferedReader(ir);
				
				String s = null;
				
				while((s=br.readLine())!=null){
					
					System.out.println(s);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			    
			
		}
		
	}
	
	

/*************************************************
 * 读取指定文件的指定行,分段取出的行后，提取特定的值返回
 *************************************************/
public int readByPath_line(String filePath,int line) {
		
		//1.取文件句柄
		//2.获得文件句柄当做是输入一个字节码流，需要对这个输入流进行读取
		//3.读取输入流，生成字节流
		//4.一行行的读取
		int flag =0;
		
		int result = 0;
		
		File file = new File(filePath);
		
		if(file.isFile()&&file.exists()){
			
			FileInputStream fi;
			try {
				fi = new FileInputStream(file);
				InputStreamReader ir = new InputStreamReader(fi, "UTF-8");
				
				BufferedReader br = new BufferedReader(ir);
				
				String s = null;
				
				while((s=br.readLine())!=null){
					
					if(flag==line){
//System.out.println(s);
						String[] ss = s.trim().split("WPA");
						
						if(ss.length!=0){
							
							String target = ss[1].trim().substring(1, 2);
							
							result = Integer.parseInt(target);
						}
						
					}
					flag++;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			    
			
		}
		
		return result;
	}


public void splitStringByflag_index(String string,String flag,int index){
	
	
	String[] result = null;
	if(string.split(flag).length!=0){
		
		result = string.split(flag); 
		
		
	}else{
		
		System.out.println("无法提取指定字符串");
	}
		
	
}

}
