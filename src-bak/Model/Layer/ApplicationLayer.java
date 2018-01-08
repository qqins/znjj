package Model.Layer;

public class ApplicationLayer {
	
	
	//UDP TCP
	int HeaderLen;
	int port_sou;
	int port_des;
	
	//TCP
	long seqnumber;
	long acknumber;
	String tcpflag;
	int windowSize;

	public int getHeaderLen() {
		return HeaderLen;
	}

	public void setHeaderLen(int HeaderLen) {
		this.HeaderLen = HeaderLen;
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

	public long getSeqnumber() {
		return seqnumber;
	}

	public void setSeqnumber(long seqnumber) {
		this.seqnumber = seqnumber;
	}

	public long getAcknumber() {
		return acknumber;
	}

	public void setAcknumber(long acknumber) {
		this.acknumber = acknumber;
	}

	public String getTcpflag() {
		return tcpflag;
	}

	public void setTcpflag(String tcpflag) {
		this.tcpflag = tcpflag;
	}

	public int getWindowSize() {
		return windowSize;
	}

	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
	}
}
