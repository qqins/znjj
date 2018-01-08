package Util.Timer;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class TimerDialog {
	
	
	private String message = null;  
    private int secends = 0;  
    private JLabel label = new JLabel();  
    private JButton confirm,cancel;   
    private JDialog dialog = null;  
    int result = -5;  
    public int  showDialog(JFrame father, String message, int sec) {  
          
        this.message = message;  
        secends = sec;  
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setText(message);   
        label.setBounds(0,0,300,50);
        
        ScheduledExecutorService s = Executors.newSingleThreadScheduledExecutor();  
        
        
        /*confirm = new JButton("½ÓÊÜ");  
        confirm.setBounds(100,40,60,20);  
        confirm.addActionListener(new ActionListener() {              
            @Override  
            public void actionPerformed(ActionEvent e) {  
                result = 0;  
                TimerDialog.this.dialog.dispose();  
            }  
        });  
        cancel = new JButton("¾Ü¾ø");  
        cancel.setBounds(190,40,60,20);  
        cancel.addActionListener(new ActionListener() {  
              
            @Override  
            public void actionPerformed(ActionEvent e) {  
                result = 1;  
                TimerDialog.this.dialog.dispose();  
            }  
        });  */
        
        
        dialog = new JDialog(father, true);  
        dialog.setTitle(""+secends+"s will be closed");  
        dialog.setLayout(null); 
        
        dialog.add(label);  
        //dialog.add(confirm);  
        //dialog.add(cancel);  
        s.scheduleAtFixedRate(new Runnable() {  
              
            @Override  
            public void run() {  
                // TODO Auto-generated method stub  
                  
                TimerDialog.this.secends--;  
                if(TimerDialog.this.secends == 0) {  
                    TimerDialog.this.dialog.dispose();  
                }else {  
                    dialog.setTitle(""+secends+"s will be closed");  
                }  
            }  
        }, 1, 1, TimeUnit.SECONDS);  
        dialog.pack();  
        dialog.setSize(new Dimension(300,100));  
        dialog.setLocationRelativeTo(father);  
        dialog.setVisible(true);  
        return result;  
    }  

}
