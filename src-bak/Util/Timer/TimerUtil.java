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
	 *  ��һ�ַ������趨ָ������task��ָ��ʱ��timeִ�� schedule(TimerTask task, Date time)
	 *************************************************/
	 public  void timer1(TimerTask timerTask,int delayTime) {
		    final Timer timer = new Timer();
		    timer.schedule(/*new TimerTask() {
		      public void run() {
		        System.out.println("-------�趨Ҫָ������--------");
		        timer.cancel();  
		      }
		    }*/timerTask, delayTime*1000);// �趨ָ����ʱ��time,�˴�Ϊ2000����	  
	}
	 
	 
	 /*************************************************
	 * �ڶ��ַ������趨ָ������task��ָ���ӳ�delay����й̶��ӳ�peroid��ִ��
	 *************************************************/
	  // schedule(TimerTask task, long delay, long period)
	  public  void timer2(TimerTask timerTask,int delayTime,int period) {
	    Timer timer = new Timer();
	    timer.schedule(/*new TimerTask() {
	      public void run() {
	        System.out.println("-------�趨Ҫָ������--------");
	      }
	    }*/timerTask, delayTime*1000, period*1000);
	  }
	  
	  
	  /*************************************************
	 * �����ַ������趨ָ������task��ָ���ӳ�delay����й̶�Ƶ��peroid��ִ�С�
	 *************************************************/
	  // scheduleAtFixedRate(TimerTask task, long delay, long period)
	  public void timer3() {
	    Timer timer = new Timer();
	    timer.scheduleAtFixedRate(new TimerTask() {
	      public void run() {
	        System.out.println("-------�趨Ҫָ������--------");
	      }
	    }, 1000, 2000);
	  }
	  
	  /*************************************************
	 * �����ַ���������ָ��������task��ָ����ʱ��firstTime��ʼ�����ظ��Ĺ̶�����periodִ�У�
	 *************************************************/
	  // Timer.scheduleAtFixedRate(TimerTask task,Date firstTime,long period)
	  public void timer4() {
			  Calendar calendar = Calendar.getInstance();
			  calendar.set(Calendar.HOUR_OF_DAY, 12); // ����ʱ
			  calendar.set(Calendar.MINUTE, 0);// ���Ʒ�
			  calendar.set(Calendar.SECOND, 0);// ������
			  
			  java.util.Date time = calendar.getTime();// �ó�ִ�������ʱ��,�˴�Ϊ�����12��00��00
			  Timer timer = new Timer();
			  timer.scheduleAtFixedRate(new TimerTask() {
					  public void run() {
					  System.out.println("-------�趨Ҫָ������--------");
					  }
			  }, time, 1000 * 60 * 60 * 24);
	 }
	 
}
