package objectIO;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import nTupleTD.TrainingParams;

import org.apache.commons.io.FileUtils;

public class LoadSave {
	private String DEFAULT_DIR_AGENT = "agents";
	private final JFileChooserApprove fc;
	private AgentFilter agentFilter;
	private JFrame parent;

	public LoadSave(JFrame parent, String defaultDir, String filterDescription,
			String filterExtension) {
		fc = new JFileChooserApprove();
		fc.setCurrentDirectory(new File(DEFAULT_DIR_AGENT));
		this.parent = parent;
		this.DEFAULT_DIR_AGENT = defaultDir;
		agentFilter = new AgentFilter(filterDescription, filterExtension);
	}

	private static JDialog createProgressDialog(
			final IGetProgress streamProgress, final String msg, JFrame parent) {
		//
		// Do not create Dialog, if no parent frame is specified
		//
		if (parent == null)
			return null;
		//
		// Setup Progressbar Dialog
		//
		final JDialog dlg = new JDialog(parent, msg, true);
		final JProgressBar dpb = new JProgressBar(0, 100);
		dlg.add(BorderLayout.CENTER, dpb);
		dlg.add(BorderLayout.NORTH, new JLabel("Progress..."));
		dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dlg.setSize(300, 75);
		dlg.setLocationRelativeTo(parent);

		Thread t = new Thread(new Runnable() {
			public void run() {
				dlg.setVisible(true);
			}
		});
		t.start();

		//
		// dialog has to be closed, if not needed anymore with dlg.dispose()
		//
		return dlg;
	}

	private boolean saveObject(Object td, long estimatedObjectByteSize,
			boolean zip) {
		fc.setFileFilter((agentFilter));
		fc.setAcceptAllFileFilterUsed(false);

		int returnVal = fc.showSaveDialog(parent);

		boolean ret = true;

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String path = fc.getSelectedFile().getPath();

			if (!path.toLowerCase().endsWith(agentFilter.acceptExtension))
				path = path + "." + agentFilter.acceptExtension;
			ret = saveObject(td, estimatedObjectByteSize, zip, parent, path);

		} else
			ret = false;

		//
		// Rescan current directory, hope it helps
		//
		fc.rescanCurrentDirectory();
		return ret;
	}

	private static boolean saveObject(Object td, long estimatedObjectByteSize,
			boolean zip, JFrame parent, String path) {
		boolean ret = false;
		String filePath = "";
		File file = new File(path);
		filePath = file.getPath();

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filePath);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
			ret = false;
		}

		OutputStream gz = null;
		try {
			gz = new GZIPOutputStream(fos) {
				{
					def.setLevel(Deflater.BEST_COMPRESSION);
				}
			};

		} catch (IOException e1) {
			e1.printStackTrace();
			ret = false;
		}

		// estimate agent size
		long bytes = estimatedObjectByteSize;
		final IOProgress p = new IOProgress(bytes);
		OutputStream os = (zip ? gz : fos);
		ProgressTrackingOutputStream ptos = null;
		try {
			ptos = new ProgressTrackingOutputStream(os, p);
		} catch (IOException e1) {
			ret = false;
			e1.printStackTrace();
		}

		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(ptos);
		} catch (IOException e) {
			ret = false;
			e.printStackTrace();
		}

		//
		// Only creates dialog, if parent != null
		//
		final JDialog dlg = createProgressDialog(ptos, "Saving...", parent);
		try {
			oos.writeObject(td);
		} catch (IOException e) {
			ret = false;
			e.printStackTrace();
		}

		try {
			oos.flush();
			oos.close();
			fos.close();
		} catch (IOException e) {
			ret = false;
			e.printStackTrace();
		}

		//
		// Do this, because the JDialog freezes if small objects are
		// loaded...
		//
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
			ret = false;
		}

		if (dlg != null) {
			dlg.setVisible(false);
			dlg.dispose();
		}
		return ret;
	}

	public boolean saveObjectAsZip(Object o, long estimatedObjectByteSize)
			throws IOException {
		return saveObject(o, estimatedObjectByteSize, true);
	}

	public boolean saveObject(Object o, long estimatedObjectByteSize)
			throws IOException {
		return saveObject(o, estimatedObjectByteSize, false);
	}

	public static boolean saveObjectAsZip(Object o,
			long estimatedObjectByteSize, String path) throws IOException {
		return saveObject(o, estimatedObjectByteSize, true, null, path);
	}

	public static boolean saveObject(Object o, long estimatedObjectByteSize,
			String path) throws IOException {
		return saveObject(o, estimatedObjectByteSize, false, null, path);
	}

	private static int estimateGZIPLength(File f) {
		RandomAccessFile raf;
		int fileSize = 0;
		try {
			raf = new RandomAccessFile(f, "r");
			raf.seek(raf.length() - 4);
			byte[] bytes = new byte[4];
			raf.read(bytes);
			fileSize = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
					.getInt();
			if (fileSize < 0)
				fileSize += (1L << 32);
			raf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return fileSize;
	}

	private Object loadObject(boolean zip) {
		Object so;
		fc.rescanCurrentDirectory();
		fc.setFileFilter((agentFilter));
		fc.setAcceptAllFileFilterUsed(false);

		int returnVal = fc.showOpenDialog(parent);
		String filePath = "";

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			filePath = file.getPath();
			so = loadObject(zip, filePath, parent);
		} else
			so = null;;
		return so;
	}

	private static Object loadObject(boolean zip, String filePath, JFrame parent) {
		ObjectInputStream ois = null;
		FileInputStream fis = null;
		File file = new File(filePath);
		Object so = null;
		try {
			fis = new FileInputStream(filePath);

			InputStream gs = (zip ? new GZIPInputStream(fis) : fis);

			long fileLength = (long) (estimateGZIPLength(file));
			final ProgressTackingInputStream ptis = new ProgressTackingInputStream(
					gs, new IOProgress(fileLength));
			ois = new ObjectInputStream(ptis);

			final JDialog dlg = createProgressDialog(ptis, "Loading...", parent);
			so = ois.readObject();

			//
			// Do this, because the JDialog freezes if small objects are
			// loaded...
			//
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return null;
			}

			dlg.setVisible(false);
			dlg.dispose();
		} catch (IOException e) {
			e.printStackTrace();
			// return "ERROR: Could not open file " + filePath + "!";
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (ois != null)
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return so;
	}

	public Object loadZipToObject() {
		return loadObject(true);
	}

	public Object loadObject() {
		return loadObject(false);
	}
	
	public static Object loadZipToObject(String filePath) {
		return loadObject(true, filePath, null);
	}

	public static Object loadObject(String filePath) {
		return loadObject(false, filePath, null);
	}

	public boolean saveObjectToXML(Object o, Class<?> c) {
		fc.setFileFilter((agentFilter));
		fc.setAcceptAllFileFilterUsed(false);

		int returnVal = fc.showSaveDialog(parent);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String path = fc.getSelectedFile().getPath();
			if (!path.toLowerCase().endsWith(agentFilter.acceptExtension))
				path = path + "." + agentFilter.acceptExtension;
			return saveObjectToXML(path, o, c);
		}
		return false;
	}

	public static boolean saveObjectToXML(String filePath, Object o, Class<?>... c) {
		// create JAXB context and initializing Marshaller
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(c);

			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			//
			// for getting nice formatted output
			//
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);

			// specify the location and name of xml file to be created
			File XMLfile = new File(filePath);

			//
			// Writing to XML file
			//
			jaxbMarshaller.marshal(o, XMLfile);

			// Writing to console: just for debug-purposes
			// jaxbMarshaller.marshal(o, System.out);
		} catch (JAXBException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public Object loadObjectFromXML(Class<?> c) {
		fc.rescanCurrentDirectory();
		fc.setFileFilter((agentFilter));
		fc.setAcceptAllFileFilterUsed(false);

		int returnVal = fc.showOpenDialog(parent);
		String filePath;

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			filePath = fc.getSelectedFile().getPath();
			return loadObjectFromXML(filePath, TrainingParams.INVOLVED_CLASSES);
		}
		return null;
	}

	public static boolean createFolder(File dir, PrintStream logStream) {

		//
		// Create the folder
		//
		int repeat = 0;

		boolean success = false;
		do { // 10 attempts to create the file
			success = dir.mkdir();
			repeat++;
			if (!success)
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		} while (!success && repeat < 10);

		if (!success)
			return false;

		//
		// Wait until directory is created
		//
		while (!dir.isDirectory()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return true;
	}

	public static boolean deleteFolder(File dir, PrintStream logStream) {
		try {
			FileUtils.deleteDirectory(dir);
		} catch (IOException e) {
			if (logStream != null)
				e.printStackTrace(logStream);
			e.printStackTrace();
			return false;
		}
		//
		// Wait until directory is deleted
		//
		while (dir.isDirectory()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public static File[] getDirectoryFiles(String dirPath, String extension) {
		File[] files = getDirectoryFiles(dirPath);
		return fileFilter(files, extension);
	}

	// extension should be something like "xml"
	public static File[] fileFilter(File[] fileList, String extension) {
		List<File> xmlFiles = new ArrayList<>(fileList.length);
		for (int i = 0; i < fileList.length; i++) {
			String filename = fileList[i].getName();
			if (filename.endsWith("." + extension.toLowerCase())
					|| filename.endsWith("." + extension.toUpperCase())) {
				xmlFiles.add(fileList[i]);
			}
		}
		return xmlFiles.toArray(new File[xmlFiles.size()]);
	}

	public static File[] getDirectoryFiles(String dirPath) {
		File dir = new File(dirPath);
		return dir.listFiles();
	}

	public static Object loadObjectFromXML(String filePath, Class<?>... c) {
		Object o = null;
		try {
			// create JAXB context and initializing Marshaller
			JAXBContext jaxbContext = JAXBContext.newInstance(c);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			// specify the location and name of xml-file to be read
			File XMLfile = new File(filePath);

			// this will create the Java object
			o = jaxbUnmarshaller.unmarshal(XMLfile);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return o;
	}

}
