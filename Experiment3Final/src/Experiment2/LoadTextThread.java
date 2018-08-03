package Experiment2;

import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoadTextThread extends Thread {

	// 声明内部成员以免在分支结构中重复声明局部变量
	// 两个表分别存储schd航班目的地信息和chkt柜台信息
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

	// 这个方法基于收到的航班信息来在柜台信息链表中寻找与航班相对应的办票柜台
	public void findCounter() {
		try {
			if (counterInformationList.size() != 0) {

				// 在柜台信息表中以fild为标记遍历搜寻信息，找到信息则使用正则表达式来匹配
				// 若没有信息则返回相应提示
				for (String temp : counterInformationList) {
					if ((temp.indexOf(flidNumber) != -1)) {
						Pattern counterPattern = Pattern.compile("chkc=([A-Z][0-9][0-9])");
						Matcher counterMatcher = counterPattern.matcher(temp);
						if (counterMatcher.find()) {
							counter = counterMatcher.group(1);
							// return true;
						}
					} else {
						counter = "未接收到相关信息";
					}
				}
			} else {
				counter = "未接收到相关信息";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// return false;
	}

	// 剪切消息方法用来把很长的schd航班信息字符串剪切成多个片段存储到成员变量中
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
		//若存在rtno=3的字符串，则该航班有经停站
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
			// 使用try-with语句进行简化代码
			try (Socket socket = new Socket("10.5.25.193", 9999);
					Scanner scanner = new Scanner(socket.getInputStream())) {
				while (scanner.hasNextLine()) {

					lineTxt = scanner.nextLine();

					// 接收到航班信息或者柜台信息先把信息存入相应的list列表里面
					if ((lineTxt.indexOf("schd") != -1) && (lineTxt.indexOf("mvin=D") != -1)) {
						flightInformationList.add(lineTxt);
						System.out.println(lineTxt);
					} else if (lineTxt.indexOf("CKDT") != -1) {
						counterInformationList.add(lineTxt);
						System.out.println(lineTxt);
					}

					boolean repeatedInformation = false;

					// 若字符串中存在下列两个关键字，则这个字符串是航班目的地信息
					if ((lineTxt.indexOf("schd") != -1) && (lineTxt.indexOf("mvin=D") != -1)) {
						// 获得航班的唯一标识flid
						Pattern flidNumberPattern = Pattern.compile("flid=([0-9]{8})");
						Matcher flidNumberMatcher = flidNumberPattern.matcher(lineTxt);
						if (flidNumberMatcher.find()) {
							flidNumber = flidNumberMatcher.group(1);
						}
						// 遍历航班目的地信息表，查看是否为更新的信息
						for (int i = 0; i < (flightInformationList.size() - 1); i++) {
							if (flightInformationList.get(i).indexOf(flidNumber) != -1) {
								repeatedInformation = true;
							}
						}
						// 对首发信息和更新信息作分支结构
						if (!repeatedInformation) {
							stopover = false;

							cutMessage(lineTxt);

							findCounter();
							if (lineTxt != null) {
								if (stopover == false) {
									// 对是否有经停站作分支处理
									synchronized (LoadedTextElementList) {
										// 对用于传输的表加锁之后进行add方法添加元素，元素的主体是一个元素类，里面包含了本次实验所输出的各类信息
										LoadedTextElementList.add(new FlightInformationElement(
												airCompany + "-" + flightNumber, flidNumber, "不经停",
												Exchange.englishAirportToChinese(receiveStation),
												Exchange.englishTimeTochinese(startingTime), counter));
										LoadedTextElementList.notifyAll();
									}
								} else {
									synchronized (LoadedTextElementList) {
										// 对用于传输的表加锁之后进行add方法添加元素，元素的主体是一个元素类，里面包含了本次实验所输出的各类信息
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
						// 下面是更新信息的处理
						else {
							cutMessage(lineTxt);
							if (stopover == false) {
								synchronized (LoadedTextElementList) {
									// add方法添加的元素中有repeat=true标识，其它线程可以根据这个标识识别为更新信息
									LoadedTextElementList
											.add(new FlightInformationElement(true, airCompany + "-" + flightNumber,
													flidNumber, "不经停", Exchange.englishAirportToChinese(receiveStation),
													Exchange.englishTimeTochinese(startingTime), counter));
									LoadedTextElementList.notifyAll();
								}
							} else {
								synchronized (LoadedTextElementList) {
									// add方法添加的元素中有repeat=true标识，其它线程可以根据这个标识识别为更新信息
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
					// 若含有ckdt关键字，则这段信息为柜台信息
					else if (lineTxt.indexOf("CKDT") != -1) {

						Pattern flidNumberPattern = Pattern.compile("flid=([0-9]{8})");
						Matcher flidNumberMatcher = flidNumberPattern.matcher(lineTxt);
						if (flidNumberMatcher.find()) {
							flidNumber = flidNumberMatcher.group(1);
						}
						// 遍历柜台信息表来判断是否是更新的柜台信息
						for (int i = 0; i < (counterInformationList.size() - 1); i++) {
							if (counterInformationList.get(i).indexOf(flidNumber) != -1) {
								repeatedInformation = true;
							}
						}

						// 对是否为更新的信息作分支结构处理
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

							// 柜台的相应信息只有航班号和柜台号两项数据，其它都为none
							synchronized (LoadedTextElementList) {
								LoadedTextElementList.add(
										new FlightInformationElement(flid, "none", "none", "none", "none", counter));
								LoadedTextElementList.notifyAll();
							}
						}
						// 下面是对更新信息作的处理，出来元素类里面多加了一道repeat标识，其它的跟上述一样
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
			System.err.println("读取网络内容出错。");
			e.printStackTrace();
		}
	}
}
