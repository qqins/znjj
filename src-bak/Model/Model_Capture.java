package Model;

public class Model_Capture {
	long acknum ;
    long seqnum  ;
    byte[] data ;
    String ip_des;
    String ip_sou ;
    String flag;
    int dataLen ;
    int port_sou ;
    int port_des ;
    
    //true代表消息是来自服务起，false表示来自主机
    boolean transation;
    
    
    
    public boolean issame(Model_Capture mc){
    	
    	if(this.getAcknum() == mc.getAcknum()&& this.getSeqnum() == mc.getSeqnum()&& this.getDataLen() == mc.getDataLen())
    		return true;
    	
    	else 
    		return false;
    }
    
	public long getAcknum() {
		return acknum;
	}
	public void setAcknum(long acknum) {
		this.acknum = acknum;
	}
	public long getSeqnum() {
		return seqnum;
	}
	public void setSeqnum(long seqnum) {
		this.seqnum = seqnum;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public String getIp_des() {
		return ip_des;
	}
	public void setIp_des(String ip_des) {
		this.ip_des = ip_des;
	}
	public String getIp_sou() {
		return ip_sou;
	}
	public void setIp_sou(String ip_sou) {
		this.ip_sou = ip_sou;
	}
	
	public int getDataLen() {
		return dataLen;
	}
	public void setDataLen(int dataLen) {
		this.dataLen = dataLen;
	}
	public int getPort_sou() {
		return port_sou;
	}
	public void setPort_sou(int port_sou) {
		this.port_sou = port_sou;
	}
	public int getPort_des() {
		return port_des;
	}
	public void setPort_des(int port_des) {
		this.port_des = port_des;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public boolean isTransation() {
		return transation;
	}
	public void setTransation(boolean transation) {
		this.transation = transation;
	}
}
