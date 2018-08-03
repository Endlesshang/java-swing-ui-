package Experiment2;

public class FlightInformationElement {
	
	//这个元素类有实验所需的各项信息，和两个构造方法来构造首发信息实例或者更新信息实例
	
	boolean repeat;
	String flightNumber;
	String flid;
	String stopoverStation;
	String receiveStation;
	String leftTime;
	String counter;
	public FlightInformationElement(String flightNumber, String flid, String stopoverStation, String receiveStation, String leftTime, String counter) {
		this.flightNumber = flightNumber;
		this.flid = flid;
		this.stopoverStation = stopoverStation;
		this.receiveStation = receiveStation;
		this.leftTime = leftTime;
		this.counter = counter;
		this.repeat = false;
	}
	public FlightInformationElement(Boolean repeat, String flightNumber, String flid, String stopoverStation, String receiveStation, String leftTime, String counter) {
		this.flightNumber = flightNumber;
		this.flid = flid;
		this.stopoverStation = stopoverStation;
		this.receiveStation = receiveStation;
		this.leftTime = leftTime;
		this.counter = counter;
		this.repeat = repeat;
	}
}
