package Experiment2;

public class FlightInformationElement {
	
	//���Ԫ������ʵ������ĸ�����Ϣ�����������췽���������׷���Ϣʵ�����߸�����Ϣʵ��
	
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
