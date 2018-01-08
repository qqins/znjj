package Util.Timer;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class TimerCounterDialog {
	
	
	//private String message = null;  
    private volatile int secends = 0;  
    private JLabel label = new JLabel();  
    private JButton confirm,cancel;   
    private JDialog dialog = null; 
  
    int result;  
    public int  showDialog(JFrame father, String message) {  
          
       // this.message = message;  
    	
    	 cancel = new JButton("Cancel");  
         cancel.setBounds(100,40,80,20);  
         cancel.addKeyListener(new KeyListener() {
			
			
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if(e.getKeyCode()==KeyEvent.VK_ENTER){
					
					 TimerCounterDialog.this.cancel.doClick();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
			
			}
		});
         cancel.addActionListener(new ActionListener() {  
             @Override  
             public void actionPerformed(ActionEvent e) {  
                 result = 1;  
                 TimerCounterDialog.this.dialog.dispose();  
             }  
         });
         
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setText(message);   
        label.setBounds(0,0,300,50);
        
        ScheduledExecutorService s = Executors.newSingleThreadScheduledExecutor();  
        
        dialog = new JDialog(father, true);  
        dialog.setTitle(""+secends+"s has been consumed");  
        dialog.setLayout(null); 
        
        dialog.add(label);  
        //dialog.add(confirm);  
        dialog.add(cancel);  
        s.scheduleAtFixedRate(new Runnable() {  
              
            @Override  
            public void run() {  
                // TODO Auto-generated method stub  
            	
            	
               if(TimerCounterDialog.this.secends==-1){
            	   TimerCounterDialog.this.dialog.dispose();
               }else {
            	   TimerCounterDialog.this.secends++;
            	   TimerCounterDialog.this.dialog.setTitle(""+secends+"s has been consumed");  
			}
                   
              
            }  
        }, 1, 1, TimeUnit.SECONDS); 
        
        
        
        dialog.pack();  
        dialog.setSize(new Dimension(300,100));  
        dialog.setLocationRelativeTo(father);  
        dialog.setVisible(true);  
        return result;  
    }  
    
    public  void close(){
    	
    	/*new Thread(new Runnable() {
			
			@Override
			public void run() {
				TimerCounterDialog.this.secends=-1;
			}
		}).start();*/
    	TimerCounterDialog.this.secends = -1;
    	
    }

}
