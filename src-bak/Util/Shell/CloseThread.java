package Util.Shell;

import java.util.Timer;
import java.util.TimerTask;

public class CloseThread implements Runnable{

	private int delayTime;
	
	
	@Override
	public void run() {
		//System.out.println("enter thread");
		timer1();
	}
	
	
	public CloseThread(int closeDalay){
		
		this.delayTime=closeDalay;
	}
	public void timer1() {
	    final Timer timer = new Timer();
	    timer.schedule(new TimerTask() {
	      public void run() {
	    	  ReadShell rs = new ReadShell();
	    	  rs.readCMD("pkill xterm");  
	        timer.cancel();
	      }
	    }, delayTime*1000);   // 设定延迟的时间
	}
}
