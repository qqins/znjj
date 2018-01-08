package Model;

import java.util.List;

import Model.Layer.ApplicationLayer;
import Model.Layer.LogicLinkCtrol;
import Model.Layer.NetworkLayer;
import Model.Layer.WireLess802;

public class Model_a {

	
	int index;
	int totalLen;
	
	WireLess802 wLayer;
	LogicLinkCtrol lLayer;
	NetworkLayer nLayer;
	ApplicationLayer aLayer;
	
	
	List<String> data;
	byte[] data_byte;

	public int getIndex() {
		return index;
	}


	public void setIndex(int index) {
		this.index = index;
	}


	public int getTotalLen() {
		return totalLen;
	}


	public void setTotalLen(int totalLen) {
		this.totalLen = totalLen;
	}


	public WireLess802 getwLayer() {
		return wLayer;
	}


	public void setwLayer(WireLess802 wLayer) {
		this.wLayer = wLayer;
	}


	public LogicLinkCtrol getlLayer() {
		return lLayer;
	}


	public void setlLayer(LogicLinkCtrol lLayer) {
		this.lLayer = lLayer;
	}


	public NetworkLayer getnLayer() {
		return nLayer;
	}


	public void setnLayer(NetworkLayer nLayer) {
		this.nLayer = nLayer;
	}


	public ApplicationLayer getaLayer() {
		return aLayer;
	}


	public void setaLayer(ApplicationLayer aLayer) {
		this.aLayer = aLayer;
	}


	public List<String> getData() {
		return data;
	}


	public void setData(List<String> data) {
		this.data = data;
	}


	public byte[] getData_byte() {
		return data_byte;
	}


	public Model_a setData_byte(byte[] data_byte) {
		this.data_byte = data_byte;
		
		return this;
	}
}
