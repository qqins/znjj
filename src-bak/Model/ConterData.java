package Model;

import java.util.List;

public class ConterData {

	private List<Model_a> counter_data_list = null;
	private List<Model_a> counter_heart_list_toHost = null;
	private List<Model_a> counter_heart_list_toService = null;
	private List<Model_a> fake_status_list_toService = null;
	
	
	/**
	 * @param counter_data_list
	 * @param counter_heart_list_toHost
	 * @param counter_heart_list_toService
	 * @param fake_status_list_toService
	 */
	public ConterData(List<Model_a> counter_data_list,
			List<Model_a> counter_heart_list_toHost,
			List<Model_a> counter_heart_list_toService,
			List<Model_a> fake_status_list_toService) {
		super();
		this.counter_data_list = counter_data_list;
		this.counter_heart_list_toHost = counter_heart_list_toHost;
		this.counter_heart_list_toService = counter_heart_list_toService;
		this.fake_status_list_toService = fake_status_list_toService;
	}

	public ConterData(List<Model_a> counter_data_list){
		
		this.counter_data_list = counter_data_list;
	}
	
	
   public ConterData(){
		
		
	}
	
	

	public List<Model_a> getCounter_data_list() {
		return counter_data_list;
	}

	public void setCounter_data_list(List<Model_a> counter_data_list) {
		this.counter_data_list = counter_data_list;
	}

	public List<Model_a> getCounter_heart_list_toHost() {
		return counter_heart_list_toHost;
	}

	public void setCounter_heart_list_toHost(List<Model_a> counter_heart_list_toHost) {
		this.counter_heart_list_toHost = counter_heart_list_toHost;
	}

	public List<Model_a> getCounter_heart_list_toService() {
		return counter_heart_list_toService;
	}

	public void setCounter_heart_list_toService(
			List<Model_a> counter_heart_list_toService) {
		this.counter_heart_list_toService = counter_heart_list_toService;
	}

	public List<Model_a> getFake_status_list_toService() {
		return fake_status_list_toService;
	}

	public void setFake_status_list_toService(
			List<Model_a> fake_status_list_toService) {
		this.fake_status_list_toService = fake_status_list_toService;
	}
}
