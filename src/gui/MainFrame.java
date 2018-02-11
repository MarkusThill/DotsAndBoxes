package gui;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.UIManager;
import nTupleTD.TDLearning;
import objectIO.LoadSave;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

import dotsAndBoxes.GameState;

/**
 * Main-Frame
 * 
 * @author Markus Thill
 * 
 */
public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	public static final String TITLE = "TD Learning for Dots-And-Boxes";
	public static final String VERSION = "v0.0";

	private void init() {
		addWindowListener(new WindowClosingAdapter());
		setSize(950, 610);
		setBounds(5, 5, 950, 610);
		setVisible(true);
		setResizable(false);
	}

	private MainFrame(String title) {
		super(title);
		// t_Game = new C4Game(this);
		setLayout(new BorderLayout(10, 10));
		// setJMenuBar(t_Game.c4Menu);
		// add(t_Game, BorderLayout.CENTER);
		add(new Label(" "), BorderLayout.SOUTH);

		pack();
	}

	private static class WindowClosingAdapter extends WindowAdapter {
		public WindowClosingAdapter() {
		}

		public void windowClosing(WindowEvent event) {
			event.getWindow().setVisible(false);
			event.getWindow().dispose();
			System.exit(0);
		}
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException,
			InterruptedException {
		try {
			UIManager.setLookAndFeel(new WindowsLookAndFeel());
		} catch (Exception e) {
		}

		// Dimension screenSize =
		// java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		// // Determine Font-Size-factor based on screen-Height
		// // Assume as basis 1080
		// float fontFactor = (float) (screenSize.getHeight() / 1080.0f);
		// System.out.println("ScreenSize: " + screenSize);
		//
		// for (Iterator i =
		// UIManager.getLookAndFeelDefaults().keySet().iterator(); i.hasNext();)
		// {
		// String key = new String(i.next().toString());
		// if(key.endsWith(".font")) {
		// Font font = UIManager.getFont(key);
		// Font biggerFont = font.deriveFont(fontFactor*font.getSize2D());
		// // change ui default to bigger font
		// UIManager.put(key,biggerFont);
		// }
		// }

		final MainFrame t_Frame = new MainFrame(TITLE + " " + VERSION);
		t_Frame.init();

		// // //TODO:
		// // //Remove Tests
		// LoadSave ls = new LoadSave(t_Frame, ".", "Filter", "fir.zip");
		// //
		// // long[] x = new long[200];
		// // ls.saveObjectAsZip(x, 200 * Long.SIZE);
		// // System.out.println(ls.loadZipToObject());
		//
		// //
		// //
		// //
		// ls = new LoadSave(t_Frame, ".", "Filter", "xml");
		// TDParams tdPar = new TDParams();
		// NTupleParams nPar = new NTupleParams();
		// TrainingParams par = new TrainingParams();
		// par.evaluationPlayAs = EvaluationPlayAs.PLAY_BOTH;
		// par.infoInterval = new InfoInterval(0, 2, 100);
		// par.m = 3;
		// par.n = 3;
		// par.nPar = nPar;
		// par.numGames = 1000;
		// par.tdPar = tdPar;
		// par.trackInfoMeasures = new TrackedInfoMeasures(
		// InfoMeasures.GLOBAL_ALPHA, InfoMeasures.SUCESSRATE);
		//
		// nPar.nTupleMasks = NTupleFactory.createNTupleList(nPar, par.m,
		// par.n);
		// nPar.lutSelection = new LUTSelection(1.0f, 2.0f, 3.0f, 4.0f);
		//
		// ls.saveObjectToXML(par, TrainingParams.class);
		//
		// par = (TrainingParams) ls.loadObjectFromXML(TrainingParams.class);

		LoadSave ls = new LoadSave(t_Frame, ".", "Filter", "agt.zip");
		TDLearning td = (TDLearning) ls.loadZipToObject();

		td.getBestMoves(new GameState(3, 3));
	}
}