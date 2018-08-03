package Experiment2;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public interface Exchange {
	public static String englishTimeTochinese(String english) {
		String year = english.substring(5, 7);
		String month = "6";
		String day = english.substring(0, 2);
		String hour = english.substring(7, 9);
		String minute = english.substring(9, 11);
		return year + "��" + month + "��" + day + "��" + hour + "ʱ" + minute + "��";
	}
	public static String englishAirportToChinese(String airport) {
		switch (airport) {
		case "WUH":
			return "�人";
		case "HIA":
			return "����˹��";
		case "CSX":
			return "��ɳ";
		case "XMN":
			return "����";
		case "XIY":
			return "����";
		case "DLC":
			return "����";
		case "CGO":
			return "֣��";
		case "ICN":
			return "�׶�";
		case "YNT":
			return "��̨";
		case "SHA":
			return "�Ϻ�����";
		case "HRB":
			return "������";
		case "HGN":
			return "ҹ����";
		case "HGH":
			return "����";
		case "HKG":
			return "���";
		case "CAN":
			return "����";
		case "DYG":
			return "����";
		case "SZX":
			return "����";
		case "KMG":
			return "����";
		case "KHH":
			return "����";
		case "FSZ":
			return "����";
		case "CJU":
			return "����";
		case "CGQ":
			return "����";
		case "TAO":
			return "�ൺ";
		case "NGB":
			return "����";
		case "CKG":
			return "����";
		case "KWL":
			return "����";
		case "SHE":
			return "����";
		case "LHW":
			return "����";
		case "NGO":
			return "������";
		case "CTU":
			return "�ɶ�";
		case "URC":
			return "��³ľ��";
		case "TSA":
			return "̨��";
		case "SYX":
			return "����";	
		case "YCU":
			return "�˳�";
		case "DSN":
			return "������˹";
		case "HET":
			return "���ͺ���";
		case "TYN":
			return "̫ԭ";	
		case "CIH":
			return "����";
		case "SIN":
			return "�¼���";
		case "JHG":
			return "��˫����";
		case "HAK":
			return "����";
		case "JJN":
			return "Ȫ��";
		case "MFM":
			return "����";
		case "WNZ":
			return "����";
		case "FOC":
			return "����";
		case "KUL":
			return "����";
		case "LXA":
			return "����";
		case "CIF":
			return "���";
		case "PVG":
			return "�Ϻ��ֶ�";
		case "NTG":
			return "��ͨ";
		case "NNG":
			return "����";
		case "CNX":
			return "����";
		case "KWE":
			return "����";
		case "TSN":
			return "���";
		case "ZHA":
			return "տ��";
		case "TXN":
			return "��ɽ";
		case "BAV":
			return "��ͷ";
		case "JMU":
			return "��ľ˹";
		default:return airport;
		}
	}
	public static void insertCounterBasedFlightnumberKey(DefaultTableModel dm, String flid, String counter) {
		
		//����tableģ���е�ÿ��Ԫ�أ�������Ҫ����Ϣ
		for(int i=0;i<dm.getRowCount();i++)
		{
			for (int j = 0; j < dm.getColumnCount(); j++) {
				if ( (dm.getValueAt(i, j) != null) && (dm.getValueAt(i, j).equals(flid)) ) {
					//JOptionPane.showMessageDialog(null, counter);
					dm.setValueAt(counter, i, j + 4);
				}
			}
		}
	}
	public static void updateFlightInformation(DefaultTableModel dm, String originalFlightInformation, String newStopoverStation, String newreceiveStation, String newLeftTime, String newCounter) {
		
		//����tableģ���е�ÿ��Ԫ�أ�������Ҫ����Ϣ
		for(int i=0;i<dm.getRowCount();i++)
		{
			for (int j = 0; j < dm.getColumnCount(); j++) {
				if ( (dm.getValueAt(i, j) != null) && (dm.getValueAt(i, j).equals(originalFlightInformation)) ) {
					dm.setValueAt(newStopoverStation, i, j + 1);
					dm.setValueAt(newreceiveStation, i, j + 2);
					dm.setValueAt(newLeftTime, i, j + 3);
					dm.setValueAt(newCounter, i, j + 4);
				}
			}
		}
	}
}
