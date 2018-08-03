package Experiment2;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ReceiveAndPrintTextThread extends JFrame {
	
	//使用链表进行线程间信息传输
	private LinkedList<FlightInformationElement> receivedTableDataList;
	private JTable flightInformationTable;
	private DefaultTableModel flightInformationTableModel;
	//private LinkedList<FlightInformationElement> finalTableDataList;

	class OptionDataThread extends Thread {
		//这个内部类用来接收LoadTextThread类传递的信息
		private LinkedList<FlightInformationElement> receivedThreadDataList;

		public OptionDataThread(LinkedList<FlightInformationElement> receivedThreadDataList) {
			this.receivedThreadDataList = receivedThreadDataList;
		}

		public void run() {
			while (receivedThreadDataList.size() != 0) {
				synchronized (receivedThreadDataList) {
					if (receivedThreadDataList.size() == 0) {
						try {
							//先对临界资源上锁，若临界的链表中没有数据，则中断等待唤醒
							receivedThreadDataList.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				//内部线程类把接收到的信息转入Jframe类中的链表中
				receivedTableDataList.add(receivedThreadDataList.removeLast());
			}
		}
	}

	public ReceiveAndPrintTextThread(LinkedList<FlightInformationElement> linkedList) {
		//主类的构造函数先创建内部类的实例
		//声明table模型，按钮，滚动条
		this.receivedTableDataList = linkedList;
		new OptionDataThread(linkedList).start();
		int n = 1;
		flightInformationTableModel = new DefaultTableModel(new String[] { "序号", "航班号", "经停站", "终点站", "起飞时间", "柜台号" },
				0);
		DefaultTableModel a = new DefaultTableModel(new String[] { "序号", "航班号", "经停站", "终点站", "起飞时间", "柜台号" },
				0);
		flightInformationTable = new JTable(flightInformationTableModel);
		JScrollPane flightInformationScrollPane = new JScrollPane(flightInformationTable);
		JButton startButton = new JButton("开始");
		
		startButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				//按下按钮后使按钮变灰，防止多次触发事件而产生多个线程
				startButton.setEnabled(false);
				new Thread(() -> {
					while (true) {
						synchronized (receivedTableDataList) {
							if (receivedTableDataList.size() == 0) {
								try {
									receivedTableDataList.wait();
								} catch (InterruptedException e1) {
									e1.printStackTrace();
								}
							}
							FlightInformationElement flightInformation = receivedTableDataList.removeLast();
							
							String number = Integer.toString(n);
							//对是否为更新的信息作判断，依据是类实例中的repeat变量
							if (!flightInformation.repeat) {
								if (flightInformation.flid != "none") {
									//调用EDT线程为表格添加新的一行
									EventQueue.invokeLater(() -> {
										flightInformationTableModel.addRow(new String[] { number,
												flightInformation.flightNumber, flightInformation.stopoverStation,
												flightInformation.receiveStation, flightInformation.leftTime,
												flightInformation.counter });
										
									});

								} else {
									//如果是先接收到的航班目的地信息，后接收到的柜台信息，则调用Exchange接口的方法更改表格
									Exchange.insertCounterBasedFlightnumberKey(flightInformationTableModel,
											flightInformation.flightNumber, flightInformation.counter);
								}
							}
							else {
								//如果是更新的信息，则调用相应方法找到表格中的旧信息将其覆盖
								if (flightInformation.flid != "none") {
									Exchange.updateFlightInformation(flightInformationTableModel, flightInformation.flightNumber, flightInformation.stopoverStation, flightInformation.receiveStation, flightInformation.leftTime, flightInformation.counter);
								}
								else {
									System.out.println(flightInformation.counter);
									Exchange.insertCounterBasedFlightnumberKey(flightInformationTableModel, flightInformation.flightNumber, flightInformation.counter);
								}
							}
						}
					}
					
				}).start();
			}
		});
		add(flightInformationScrollPane, BorderLayout.CENTER);
		add(startButton, BorderLayout.SOUTH);
		pack();
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
	}
}
