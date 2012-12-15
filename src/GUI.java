import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;

public class GUI extends JFrame implements AppletStub {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static JLabel label;
	public static JButton button;

	GUI() {
		createForm();
	}

	private void createForm() {
		label = new JLabel("Initializing..");
		try {
			for (UIManager.LookAndFeelInfo info : UIManager
					.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					changeLabelText(label, "LookAndFeel set to Nimbus");
					UIManager.setLookAndFeel(info.getClassName());
					break;
				} else {
					changeLabelText(label, "LookAndFeel set to system default");
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		setVisible(true);
		setSize(775, 600);
		setTitle("Runescape Loader by OneLuckyDuck");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setBackground(Color.BLACK);

		label = new JLabel("Loading Client...");
		label.setForeground(Color.GRAY.brighter());
		label.setFont(new Font("Consolas", Font.BOLD, 21));
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setVerticalAlignment(JLabel.TOP);
		getContentPane().add(label);
		parseWebsite(Variables.getLink());
		downloadJar(Variables.getLink()
				+ Variables.getParameters().get("archive"));
		loadClasses();
		getContentPane().remove(label);

	}

	private void loadClasses() {
		changeLabelText(label, "Loading classes");
		ClassLoader loader;
		try {
			loader = new URLClassLoader(new URL[] { new File("Runescape.jar")
					.toURI().toURL() });
			Class<?> client = loader.loadClass("Rs2Applet");
			startApplet(client);
		} catch (MalformedURLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void startApplet(Class<?> c) {
		changeLabelText(label, "Starting applet");
		try {
			Variables.setApplet((Applet) c.newInstance());
			Variables.getApplet().setStub(this);
			Variables.getApplet().init();
			Variables.getApplet().start();
			final Applet app = Variables.getApplet();
			getContentPane().add(app, BorderLayout.CENTER);
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void parseWebsite(final String url) {
		final String[] VALUES = { "document.write", "<param name=\"", "\">'",
				"'", "\\(", "\\)", "\"", " ", ";", "value" };
		changeLabelText(label, "Parsing website");
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new URL(url).openStream()));
			String line;
			for (String s : Variables.getParameters().values()) {
				System.out.println(s);
			}
			while ((line = in.readLine()) != null) {
				if (line.contains("app") && line.contains("write")) {
					Variables.getParameters().put("<app", "");
					Variables.getParameters().put("let ", "");
				} else if (line.contains("document.write")) {
					for (String s : VALUES) {
						line = line.replaceAll(s, "");
					}
					String[] split = line.split("=");
					if (split.length == 1) {
						Variables.getParameters().put(split[0], "");
					} else if (split.length == 2) {
						Variables.getParameters().put(split[0], split[1]);
					} else if (split.length == 3) {
						Variables.getParameters().put(split[0],
								split[1] + split[2]);
					}
				}
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void downloadJar(final String url) {
		changeLabelText(label, "Downloading jar");
		try {
			BufferedInputStream in = new BufferedInputStream(
					new URL(url).openStream());
			FileOutputStream fos = new FileOutputStream("Runescape.jar");
			BufferedOutputStream out = new BufferedOutputStream(fos, 1024);
			byte[] data = new byte[1024];
			int x;
			while ((x = in.read(data, 0, 1024)) >= 0) {
				out.write(data, 0, x);
			}
			in.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void changeLabelText(final JLabel l, final String text) {
		System.out.println(String.format("[INFORMATION] %s", text));
		try {
			EventQueue.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					l.setText(text);
				}

			});
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void appletResize(int width, int height) {
	}

	public final String getParameter(String name) {
		return Variables.getParameters().get(name);
	}

	public final URL getDocumentBase() {
		try {
			return new URL(Variables.getLink());
		} catch (MalformedURLException e) {
			return null;
		}
	}

	public final URL getCodeBase() {
		try {
			return new URL(Variables.getLink());
		} catch (MalformedURLException e) {
			return null;
		}
	}

	public final AppletContext getAppletContext() {
		return null;
	}

}
