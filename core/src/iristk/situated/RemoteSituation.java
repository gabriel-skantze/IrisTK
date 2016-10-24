package iristk.situated;

import iristk.system.EventMonitorPanel;
import iristk.system.IrisGUI;
import iristk.system.IrisSystem;

public class RemoteSituation {

	public RemoteSituation(String address) throws Exception {
		IrisSystem system = new IrisSystem("RemoteSituation");
		system.connectToBroker("furhat", address);
		IrisGUI gui = new IrisGUI(system);
		new SituationPanel(gui, system, SituationPanel.TOPVIEW);
		new SituationPanel(gui, system, SituationPanel.SIDEVIEW);
		new EventMonitorPanel(gui, system);
		system.sendStartSignal();
	}
	
	public static void main(String[] args) throws Exception {
		if (args.length == 0)
			new RemoteSituation("127.0.0.1");
		else
			new RemoteSituation(args[0]);
	}
	
}
