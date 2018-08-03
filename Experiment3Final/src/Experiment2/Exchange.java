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
		return year + "年" + month + "月" + day + "日" + hour + "时" + minute + "分";
	}
	public static String englishAirportToChinese(String airport) {
		switch (airport) {
		case "WUH":
			return "武汉";
		case "HIA":
			return "哈里斯堡";
		case "CSX":
			return "长沙";
		case "XMN":
			return "厦门";
		case "XIY":
			return "西安";
		case "DLC":
			return "大连";
		case "CGO":
			return "郑州";
		case "ICN":
			return "首尔";
		case "YNT":
			return "烟台";
		case "SHA":
			return "上海虹桥";
		case "HRB":
			return "哈尔滨";
		case "HGN":
			return "夜丰颂";
		case "HGH":
			return "杭州";
		case "HKG":
			return "香港";
		case "CAN":
			return "广州";
		case "DYG":
			return "大勇";
		case "SZX":
			return "深圳";
		case "KMG":
			return "昆明";
		case "KHH":
			return "高雄";
		case "FSZ":
			return "静冈";
		case "CJU":
			return "济南";
		case "CGQ":
			return "长春";
		case "TAO":
			return "青岛";
		case "NGB":
			return "宁波";
		case "CKG":
			return "重庆";
		case "KWL":
			return "桂林";
		case "SHE":
			return "沈阳";
		case "LHW":
			return "兰州";
		case "NGO":
			return "名古屋";
		case "CTU":
			return "成都";
		case "URC":
			return "乌鲁木齐";
		case "TSA":
			return "台北";
		case "SYX":
			return "三亚";	
		case "YCU":
			return "运城";
		case "DSN":
			return "鄂尔多斯";
		case "HET":
			return "呼和浩特";
		case "TYN":
			return "太原";	
		case "CIH":
			return "长治";
		case "SIN":
			return "新加坡";
		case "JHG":
			return "西双版纳";
		case "HAK":
			return "海口";
		case "JJN":
			return "泉州";
		case "MFM":
			return "澳门";
		case "WNZ":
			return "温州";
		case "FOC":
			return "福州";
		case "KUL":
			return "呱啦";
		case "LXA":
			return "拉萨";
		case "CIF":
			return "赤峰";
		case "PVG":
			return "上海浦东";
		case "NTG":
			return "南通";
		case "NNG":
			return "南宁";
		case "CNX":
			return "清迈";
		case "KWE":
			return "贵阳";
		case "TSN":
			return "天津";
		case "ZHA":
			return "湛江";
		case "TXN":
			return "黄山";
		case "BAV":
			return "包头";
		case "JMU":
			return "佳木斯";
		default:return airport;
		}
	}
	public static void insertCounterBasedFlightnumberKey(DefaultTableModel dm, String flid, String counter) {
		
		//遍历table模型中的每个元素，查找想要的信息
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
		
		//遍历table模型中的每个元素，查找想要的信息
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
