package Experiment2;

import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoadTextThread extends Thread {

	// �����ڲ���Ա�����ڷ�֧�ṹ���ظ������ֲ�����
	// ������ֱ�洢schd����Ŀ�ĵ���Ϣ��chkt��̨��Ϣ
	List<FlightInformationElement> LoadedTextElementList;
	List<String> flightInformationList;
	List<String> counterInformationList;
	boolean stopover;
	String airCompany;
	String flightNumber;
	String stopoverStation;
	String receiveStation;
	String startingStation;
	String startingTime;
	String flidNumber;
	String counter;

	public LoadTextThread(List<FlightInformationElement> LoadedTextList) {
		this.LoadedTextElementList = LoadedTextList;
		flightInformationList = new LinkedList<String>();
		counterInformationList = new LinkedList<String>();
		stopover = false;
		airCompany = "";
		flightNumber = "";
		stopoverStation = "";
		receiveStation = "";
		startingStation = "";
		startingTime = "";
		flidNumber = "";
		counter = "";
	}

	public void clear() {
		stopover = false;
		airCompany = "";
		flightNumber = "";
		stopoverStation = "";
		receiveStation = "";
		startingStation = "";
		startingTime = "";
		flidNumber = "";
		counter = "";
	}

	// ������������յ��ĺ�����Ϣ���ڹ�̨��Ϣ������Ѱ���뺽�����Ӧ�İ�Ʊ��̨
	public void findCounter() {
		try {
			if (counterInformationList.size() != 0) {

				// �ڹ�̨��Ϣ������fildΪ��Ǳ�����Ѱ��Ϣ���ҵ���Ϣ��ʹ��������ʽ��ƥ��
				// ��û����Ϣ�򷵻���Ӧ��ʾ
				for (String temp : counterInformationList) {
					if ((temp.indexOf(flidNumber) != -1)) {
						Pattern counterPattern = Pattern.compile("chkc=([A-Z][0-9][0-9])");
						Matcher counterMatcher = counterPattern.matcher(temp);
						if (counterMatcher.find()) {
							counter = counterMatcher.group(1);
							// return true;
						}
					} else {
						counter = "δ���յ������Ϣ";
					}
				}
			} else {
				counter = "δ���յ������Ϣ";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// return false;
	}

	// ������Ϣ���������Ѻܳ���schd������Ϣ�ַ������гɶ��Ƭ�δ洢����Ա������
	public void cutMessage(String lineTxt)
	{
		Pattern airCompanyPattren = Pattern.compile("alcd=(\\w+)");
		Matcher airCompanyMatcher = airCompanyPattren.matcher(lineTxt);
		if (airCompanyMatcher.find()) {
			airCompany = airCompanyMatcher.group(1);
		}
		Pattern flightNumberPattern = Pattern.compile("flno=(\\w+)");
		Matcher flightNumberMatcher = flightNumberPattern.matcher(lineTxt);
		if (flightNumberMatcher.find()) {
			flightNumber = flightNumberMatcher.group(1);
		}
		//������rtno=3���ַ�������ú����о�ͣվ
		if (lineTxt.indexOf("rtno=3") != -1) {
			stopover = true;

			Pattern stopoverStationPattern = Pattern.compile("apcd=([A-Z]{3})");
			Matcher stopoverStationMatcher = stopoverStationPattern.matcher(lineTxt);
			if (stopoverStationMatcher.find() && stopoverStationMatcher.find()) {
				
				stopoverStation = stopoverStationMatcher.group(1);
			}

			Pattern receiveStationPattern = Pattern.compile("apcd=([A-Z]{3})");
			Matcher receiveStationMatcher = receiveStationPattern.matcher(lineTxt);
			if (receiveStationMatcher.find()&&receiveStationMatcher.find()&&receiveStationMatcher.find()) {

				receiveStation = receiveStationMatcher.group(1);
			}

		} else {

			Pattern receiveStationPattern = Pattern.compile("apcd=([A-Z]{3})");
			Matcher receiveStationMatcher = receiveStationPattern.matcher(lineTxt);
			if (receiveStationMatcher.find() && receiveStationMatcher.find()) {
				
				receiveStation = receiveStationMatcher.group(1);
			}

		}
		Pattern startingTimePattern = Pattern.compile("sodt=([0-9]{2}[A-Z]{3}[0-9]{6})");
		Matcher startingTimeMatcher = startingTimePattern.matcher(lineTxt);
		if (startingTimeMatcher.find()) {
			startingTime = startingTimeMatcher.group(1);
		}
		//System.out.println(airCompany + flightNumber + stopoverStation + receiveStation + startingTime);
	}

	public void run() {
		try {
			String lineTxt = null;
			// ʹ��try-with�����м򻯴���
			try (Socket socket = new Socket("10.5.25.193", 9999);
					Scanner scanner = new Scanner(socket.getInputStream())) {
				while (scanner.hasNextLine()) {

					lineTxt = scanner.nextLine();

					// ���յ�������Ϣ���߹�̨��Ϣ�Ȱ���Ϣ������Ӧ��list�б�����
					if ((lineTxt.indexOf("schd") != -1) && (lineTxt.indexOf("mvin=D") != -1)) {
						flightInformationList.add(lineTxt);
						System.out.println(lineTxt);
					} else if (lineTxt.indexOf("CKDT") != -1) {
						counterInformationList.add(lineTxt);
						System.out.println(lineTxt);
					}

					boolean repeatedInformation = false;

					// ���ַ����д������������ؼ��֣�������ַ����Ǻ���Ŀ�ĵ���Ϣ
					if ((lineTxt.indexOf("schd") != -1) && (lineTxt.indexOf("mvin=D") != -1)) {
						// ��ú����Ψһ��ʶflid
						Pattern flidNumberPattern = Pattern.compile("flid=([0-9]{8})");
						Matcher flidNumberMatcher = flidNumberPattern.matcher(lineTxt);
						if (flidNumberMatcher.find()) {
							flidNumber = flidNumberMatcher.group(1);
						}
						// ��������Ŀ�ĵ���Ϣ���鿴�Ƿ�Ϊ���µ���Ϣ
						for (int i = 0; i < (flightInformationList.size() - 1); i++) {
							if (flightInformationList.get(i).indexOf(flidNumber) != -1) {
								repeatedInformation = true;
							}
						}
						// ���׷���Ϣ�͸�����Ϣ����֧�ṹ
						if (!repeatedInformation) {
							stopover = false;

							cutMessage(lineTxt);

							findCounter();
							if (lineTxt != null) {
								if (stopover == false) {
									// ���Ƿ��о�ͣվ����֧����
									synchronized (LoadedTextElementList) {
										// �����ڴ���ı����֮�����add�������Ԫ�أ�Ԫ�ص�������һ��Ԫ���࣬��������˱���ʵ��������ĸ�����Ϣ
										LoadedTextElementList.add(new FlightInformationElement(
												airCompany + "-" + flightNumber, flidNumber, "����ͣ",
												Exchange.englishAirportToChinese(receiveStation),
												Exchange.englishTimeTochinese(startingTime), counter));
										LoadedTextElementList.notifyAll();
									}
								} else {
									synchronized (LoadedTextElementList) {
										// �����ڴ���ı����֮�����add�������Ԫ�أ�Ԫ�ص�������һ��Ԫ���࣬��������˱���ʵ��������ĸ�����Ϣ
										LoadedTextElementList
												.add(new FlightInformationElement(airCompany + "-" + flightNumber,
														flidNumber, Exchange.englishAirportToChinese(stopoverStation),
														Exchange.englishAirportToChinese(receiveStation),
														Exchange.englishTimeTochinese(startingTime), counter));
										LoadedTextElementList.notifyAll();
									}
								}
							}
							clear();
						}
						// �����Ǹ�����Ϣ�Ĵ���
						else {
							cutMessage(lineTxt);
							if (stopover == false) {
								synchronized (LoadedTextElementList) {
									// add������ӵ�Ԫ������repeat=true��ʶ�������߳̿��Ը��������ʶʶ��Ϊ������Ϣ
									LoadedTextElementList
											.add(new FlightInformationElement(true, airCompany + "-" + flightNumber,
													flidNumber, "����ͣ", Exchange.englishAirportToChinese(receiveStation),
													Exchange.englishTimeTochinese(startingTime), counter));
									LoadedTextElementList.notifyAll();
								}
							} else {
								synchronized (LoadedTextElementList) {
									// add������ӵ�Ԫ������repeat=true��ʶ�������߳̿��Ը��������ʶʶ��Ϊ������Ϣ
									LoadedTextElementList
											.add(new FlightInformationElement(true, airCompany + "-" + flightNumber,
													flidNumber, Exchange.englishAirportToChinese(stopoverStation),
													Exchange.englishAirportToChinese(receiveStation),
													Exchange.englishTimeTochinese(startingTime), counter));
									LoadedTextElementList.notifyAll();
								}
							}
						}
					}
					// ������ckdt�ؼ��֣��������ϢΪ��̨��Ϣ
					else if (lineTxt.indexOf("CKDT") != -1) {

						Pattern flidNumberPattern = Pattern.compile("flid=([0-9]{8})");
						Matcher flidNumberMatcher = flidNumberPattern.matcher(lineTxt);
						if (flidNumberMatcher.find()) {
							flidNumber = flidNumberMatcher.group(1);
						}
						// ������̨��Ϣ�����ж��Ƿ��Ǹ��µĹ�̨��Ϣ
						for (int i = 0; i < (counterInformationList.size() - 1); i++) {
							if (counterInformationList.get(i).indexOf(flidNumber) != -1) {
								repeatedInformation = true;
							}
						}

						// ���Ƿ�Ϊ���µ���Ϣ����֧�ṹ����
						if (!repeatedInformation) {
							String flid = "";
							Pattern counterPattern = Pattern.compile("chkc=([A-Z][0-9][0-9])");
							Matcher counterMatcher = counterPattern.matcher(lineTxt);
							if (counterMatcher.find()) {
								counter = counterMatcher.group(1);
							}

							Pattern airCompanyPattren = Pattern.compile("ffid=(\\w+-\\w+)");
							Matcher airCompanyMatcher = airCompanyPattren.matcher(lineTxt);
							if (airCompanyMatcher.find()) {
								flid = airCompanyMatcher.group(1);
							}

							// ��̨����Ӧ��Ϣֻ�к���ź͹�̨���������ݣ�������Ϊnone
							synchronized (LoadedTextElementList) {
								LoadedTextElementList.add(
										new FlightInformationElement(flid, "none", "none", "none", "none", counter));
								LoadedTextElementList.notifyAll();
							}
						}
						// �����ǶԸ�����Ϣ���Ĵ�������Ԫ������������һ��repeat��ʶ�������ĸ�����һ��
						else {
							Pattern counterPattern = Pattern.compile("chkc=([A-Z][0-9][0-9])");
							Matcher counterMatcher = counterPattern.matcher(lineTxt);
							if (counterMatcher.find()) {
								counter = counterMatcher.group(1);
							}
							String flid = "";
							Pattern airCompanyPattren = Pattern.compile("ffid=(\\w+-\\w+)");
							Matcher airCompanyMatcher = airCompanyPattren.matcher(lineTxt);
							if (airCompanyMatcher.find()) {
								flid = airCompanyMatcher.group(1);
							}
							synchronized (LoadedTextElementList) {
								// System.out.println(flid);
								LoadedTextElementList.add(new FlightInformationElement(true, flid, "none", "none",
										"none", "none", counter));
								// System.out.println(counter);
								LoadedTextElementList.notifyAll();
							}
							
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			System.err.println("��ȡ�������ݳ���");
			e.printStackTrace();
		}
	}
}
