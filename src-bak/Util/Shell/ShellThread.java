package Util.Shell;

public class ShellThread implements Runnable{
	
	String shellPath= null;
	public ShellThread(String path){
		
		this.shellPath = path;
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		ReadShell rs = new ReadShell();
		rs.readSH(shellPath);
	}

}
