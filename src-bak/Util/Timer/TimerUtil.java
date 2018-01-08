package Util.Timer;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;



public class TimerUtil {
	 public static void main(String[] args) {
		    //timer1();
		    //timer2();
		    //timer3();
		    //timer4();
		  }
	 
	 
	 /*************************************************
	 *  第一种方法：设定指定任务task在指定时间time执行 schedule(TimerTask task, Date time)
	 *************************************************/
	 public  void timer1(TimerTask timerTask,int delayTime) {
		    final Timer timer = new Timer();
		    timer.schedule(/*new TimerTask() {
		      public void run() {
		        System.out.println("-------设定要指定任务--------");
		        timer.cancel();  
		      }
		    }*/timerTask, delayTime*1000);// 设定指定的时间time,此处为2000毫秒	  
	}
	 
	 
	 /*************************************************
	 * 第二种方法：设定指定任务task在指定延迟delay后进行固定延迟peroid的执行
	 *************************************************/
	  // schedule(TimerTask task, long delay, long period)
	  public  void timer2(TimerTask timerTask,int delayTime,int period) {
	    Timer timer = new Timer();
	    timer.schedule(/*new TimerTask() {
	      public void run() {
	        System.out.println("-------设定要指定任务--------");
	      }
	    }*/timerTask, delayTime*1000, period*1000);
	  }
	  
	  
	  /*************************************************
	 * 第三种方法：设定指定任务task在指定延迟delay后进行固定频率peroid的执行。
	 *************************************************/
	  // scheduleAtFixedRate(TimerTask task, long delay, long period)
	  public void timer3() {
	    Timer timer = new Timer();
	    timer.scheduleAtFixedRate(new TimerTask() {
	      public void run() {
	        System.out.println("-------设定要指定任务--------");
	      }
	    }, 1000, 2000);
	  }
	  
	  /*************************************************
	 * 第四种方法：安排指定的任务task在指定的时间firstTime开始进行重复的固定速率period执行．
	 *************************************************/
	  // Timer.scheduleAtFixedRate(TimerTask task,Date firstTime,long period)
	  public void timer4() {
			  Calendar calendar = Calendar.getInstance();
			  calendar.set(Calendar.HOUR_OF_DAY, 12); // 控制时
			  calendar.set(Calendar.MINUTE, 0);// 控制分
			  calendar.set(Calendar.SECOND, 0);// 控制秒
			  
			  java.util.Date time = calendar.getTime();// 得出执行任务的时间,此处为今天的12：00：00
			  Timer timer = new Timer();
			  timer.scheduleAtFixedRate(new TimerTask() {
					  public void run() {
					  System.out.println("-------设定要指定任务--------");
					  }
			  }, time, 1000 * 60 * 60 * 24);
	 }
	 
}
