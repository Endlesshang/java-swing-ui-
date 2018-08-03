package Experiment2;

import java.awt.EventQueue;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

public class PrintTextThreadMain extends Thread{
	public static void main(String[] args) {
		//Vector<String> vector = new Vector<String>();
		LinkedList<FlightInformationElement> LList = new LinkedList<FlightInformationElement>();
		new LoadTextThread(LList).start();
		EventQueue.invokeLater(() -> {
			new ReceiveAndPrintTextThread(LList);
		});
	}
}
