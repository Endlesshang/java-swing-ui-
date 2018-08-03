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
	
	//ʹ����������̼߳���Ϣ����
	private LinkedList<FlightInformationElement> receivedTableDataList;
	private JTable flightInformationTable;
	private DefaultTableModel flightInformationTableModel;
	//private LinkedList<FlightInformationElement> finalTableDataList;

	class OptionDataThread extends Thread {
		//����ڲ�����������LoadTextThread�ഫ�ݵ���Ϣ
		private LinkedList<FlightInformationElement> receivedThreadDataList;

		public OptionDataThread(LinkedList<FlightInformationElement> receivedThreadDataList) {
			this.receivedThreadDataList = receivedThreadDataList;
		}

		public void run() {
			while (receivedThreadDataList.size() != 0) {
				synchronized (receivedThreadDataList) {
					if (receivedThreadDataList.size() == 0) {
						try {
							//�ȶ��ٽ���Դ���������ٽ��������û�����ݣ����жϵȴ�����
							receivedThreadDataList.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				//�ڲ��߳���ѽ��յ�����Ϣת��Jframe���е�������
				receivedTableDataList.add(receivedThreadDataList.removeLast());
			}
		}
	}

	public ReceiveAndPrintTextThread(LinkedList<FlightInformationElement> linkedList) {
		//����Ĺ��캯���ȴ����ڲ����ʵ��
		//����tableģ�ͣ���ť��������
		this.receivedTableDataList = linkedList;
		new OptionDataThread(linkedList).start();
		int n = 1;
		flightInformationTableModel = new DefaultTableModel(new String[] { "���", "�����", "��ͣվ", "�յ�վ", "���ʱ��", "��̨��" },
				0);
		DefaultTableModel a = new DefaultTableModel(new String[] { "���", "�����", "��ͣվ", "�յ�վ", "���ʱ��", "��̨��" },
				0);
		flightInformationTable = new JTable(flightInformationTableModel);
		JScrollPane flightInformationScrollPane = new JScrollPane(flightInformationTable);
		JButton startButton = new JButton("��ʼ");
		
		startButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				//���°�ť��ʹ��ť��ң���ֹ��δ����¼�����������߳�
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
							//���Ƿ�Ϊ���µ���Ϣ���жϣ���������ʵ���е�repeat����
							if (!flightInformation.repeat) {
								if (flightInformation.flid != "none") {
									//����EDT�߳�Ϊ�������µ�һ��
									EventQueue.invokeLater(() -> {
										flightInformationTableModel.addRow(new String[] { number,
												flightInformation.flightNumber, flightInformation.stopoverStation,
												flightInformation.receiveStation, flightInformation.leftTime,
												flightInformation.counter });
										
									});

								} else {
									//������Ƚ��յ��ĺ���Ŀ�ĵ���Ϣ������յ��Ĺ�̨��Ϣ�������Exchange�ӿڵķ������ı��
									Exchange.insertCounterBasedFlightnumberKey(flightInformationTableModel,
											flightInformation.flightNumber, flightInformation.counter);
								}
							}
							else {
								//����Ǹ��µ���Ϣ���������Ӧ�����ҵ�����еľ���Ϣ���串��
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
