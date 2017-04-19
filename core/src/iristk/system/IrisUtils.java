/*******************************************************************************
 * Copyright (c) 2014 Gabriel Skantze.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Gabriel Skantze - initial API and implementation
 ******************************************************************************/
package iristk.system;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.swing.UIManager;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import iristk.flow.FlowCompiler;
import iristk.project.Launcher;
import iristk.project.Project;
import iristk.project.Package;
import iristk.util.FileFinder;
import iristk.util.StringUtils;
import iristk.util.Utils;

public class IrisUtils {
	
	private static Logger logger = getLogger(IrisUtils.class);

	private static boolean logInitialized = false;

	public static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	private static void initLogger() {
		if (!logInitialized) {
			//Package core = CorePackage.PACKAGE;
			//if (core != null && core.getPath("log4j.properties").exists())
			//	PropertyConfigurator.configure(core.getPath("log4j.properties").getAbsolutePath());
			//else
			PropertyConfigurator.configure(IrisUtils.class.getResource("log4j.properties"));
			//Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
			logInitialized = true;
		}
	}
	
	public static Logger getLogger(Class<?> clazz) {
		initLogger();
		return LoggerFactory.getLogger(clazz);
	}
	
	public static org.apache.log4j.Logger getRootLogger() {
		initLogger();
		return org.apache.log4j.Logger.getRootLogger();
	}
	
	private static class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
		@Override
		public void uncaughtException(Thread t, Throwable e) {
			logger.error("Exception in thread " + t.getName() + ": " + e.getClass().getName() + " - " + e.getMessage(), e.getCause());
		}
	}
	
	public static void compileFlow(String[] args) {
		try {
			//, new File(System.getProperty("user.dir")
			Launcher.runJava(false, FlowCompiler.class.getName(), null, args, System.out, System.err, true, null, null);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public static void cleanTempFolder(String[] args) {
		if (IrisUtils.getTempDir().exists()) {
			if (Utils.cleanFolder(IrisUtils.getTempDir())) {
				logger.info("Succeeded in cleaning folder: " + IrisUtils.getTempDir());
			} else {
				logger.warn("Did not succeed to clean folder: " + IrisUtils.getTempDir());
			}
		} else  {
			logger.warn("Temporary folder not found: " + IrisUtils.getTempDir());
		}
	}

	private static Map<String,Properties> getTemplateProperties() {
		//ZipFile zipFile = null;
		//try {
			Map<String,Properties> result = new HashMap<>();
			//zipFile = new ZipFile(Project.main.getPackage(IrisUtils.class).getLibPath("templates.zip"));
			for (Project proj : Project.main.getAllProjects()) {
				File templPath = proj.getPath("templates");
				if (templPath.exists() && templPath.isDirectory()) {
					for (File tf : templPath.listFiles()) {
						File tfp = new File(tf, "template.properties");
						if (tfp.exists()) {
							Properties prop = new Properties();
							try {
								prop.load(new FileReader(tfp));
							} catch (IOException e) {
								e.printStackTrace();
							}
							prop.put("templatePath", tf.getAbsolutePath());
							result.put(tf.getName(), prop);
						}
					}
				}
			}
			return result;
			/*
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while(entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (entry.getName().matches("[^/]+/template.properties")) {
					String templ = entry.getName().replace("/template.properties", "");
					Properties prop = new Properties();
					prop.load(zipFile.getInputStream(entry));
					result.put(templ, prop);
				}
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (zipFile != null)
					zipFile.close();
			} catch (Exception e){
			}
		}
		return null;
		*/
	}

	public static void createFromTemplate(String[] args) throws IOException{
			if (args.length < 2) {
				System.out.println("Usage: " + Launcher.getExecutable().getName() + " create [template] [name] [path]\n");
				System.out.println("Available templates:");
				Map<String,Properties> props = getTemplateProperties(); 
				for (String templ : props.keySet()) {
					System.out.println("  " + Utils.pad(templ, 20) + props.get(templ).getProperty("description"));
				}
			} else {
				createFromTemplate(args[0], args[1], args.length > 2 ? args[2] : null);
			}
	}
	
	public static void createFromTemplate(String templateName, String name, String path) throws IOException {
		if (!name.matches("[0-9a-zA-Z_]+")) 
			throw new IOException("Bad name: '" + name + "'");
		Properties props = getTemplateProperties().get(templateName); 
		if (props == null)
			throw new IOException("Couldn't find template '" + templateName + "'");
		boolean createProject = false;
		if (path == null) {
			// No path specified, place it inside the project
			path = props.getProperty("path");
			if (path == null)
				path = ".";
			path = Project.main.getPath(path).getAbsolutePath();
		} else {
			// Create a new project
			createProject = true;
		}
		//System.out.println(new File(new File(path), name));
		path = Utils.replaceEnv(path);
		//System.out.println(new File(new File(path), name));
		File packagePath = new File(new File(path), name).getCanonicalFile();
		if (packagePath.exists())
			throw new IOException(packagePath + " already exists");
		String templateIdLC = "$" + StringUtils.lcFirst(templateName) + "$";
		String templateIdUC = "$" + StringUtils.ucFirst(templateName) + "$";
		String templRoot = props.getProperty("templatePath");
		for (String filen : FileFinder.findAll(templRoot)) {
			File inputFile = new File(filen);
			if (inputFile.getName().equals("template.properties"))
				continue;
			String entryName = filen.replace(templRoot + "\\", "");
			entryName = entryName.replace(templateIdLC, StringUtils.lcFirst(name));
			entryName = entryName.replace(templateIdUC, StringUtils.ucFirst(name));
			File targetFile = new File(packagePath, entryName);
			targetFile.getParentFile().mkdirs();
			System.out.println("Creating: " + targetFile.getCanonicalPath());
			String content = Utils.readTextFile(inputFile);
			content = content.replace(templateIdLC, StringUtils.lcFirst(name));
			content = content.replace(templateIdUC, StringUtils.ucFirst(name));
			Utils.writeTextFile(targetFile, content);
		}
		if (createProject) {
			Properties prop = new Properties();
			prop.setProperty("name", name);
			prop.setProperty("version",  new SimpleDateFormat("yyyy.MM.dd").format(new Date()));
			prop.setProperty("packages", ".");
			prop.setProperty("depends", "%" + Project.main.getName() + "%");
			File propFile = new File(packagePath, "project.properties");
			prop.store(new FileWriter(propFile), null);
			System.out.println("Creating: " + propFile.getCanonicalPath());
		}
		try {
			Launcher.compilePackage(packagePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static File getTempDir() {
		return new File(System.getProperty("java.io.tmpdir") + File.separator + "iristk");
	}
	
	public static File getTempDir(String name) {
		return new File(System.getProperty("java.io.tmpdir") + File.separator + "iristk" + File.separator + name);
	}

	public static void addCoreLibPath() {
		if (Launcher.is64arch()) {
			Launcher.addJavaLibPath(CorePackage.PACKAGE.getPath("lib/x64"));
		} else {
			Launcher.addJavaLibPath(CorePackage.PACKAGE.getPath("lib/x86"));
		}
	}

}


/*
public static void createFromTemplate(String templateName, String name, String path) throws IOException {
	if (!name.matches("[0-9a-zA-Z_]+")) 
		throw new IOException("Bad name: '" + name + "'");
	Properties props = getTemplateProperties().get(templateName); 
	if (props == null)
		throw new IOException("Couldn't find template '" + templateName + "'");
	if (path == null) {
		path = props.getProperty("path");
		if (path == null)
			path = ".";
	}
	path = Utils.replaceEnv(path);
	File packagePath = new File(new File(path), name).getCanonicalFile();
	ZipFile zipFile = new ZipFile(Project.main.getPackage(IrisUtils.class).getLibPath("templates.zip"));
	Enumeration<? extends ZipEntry> entries = zipFile.entries();
	String templateIdLC = "$" + StringUtils.lcFirst(templateName) + "$";
	String templateIdUC = "$" + StringUtils.ucFirst(templateName) + "$";
	while(entries.hasMoreElements()) {
		ZipEntry entry = entries.nextElement();
		if (entry.getName().startsWith(templateName + "/")) {
			String entryName = entry.getName().replaceFirst(templateName + "/", "");
			if (entryName.equals("template.properties")) continue;
			entryName = entryName.replace(templateIdLC, StringUtils.lcFirst(name));
			entryName = entryName.replace(templateIdUC, StringUtils.ucFirst(name));
			//entryName = entryName.replace("/_", "/");
			File file = new File(packagePath, entryName);
			if(entry.isDirectory()) {
				if (!file.exists()) {
					System.out.println("Creating directory: " + file.getCanonicalPath());
					file.mkdirs();
				}
			} else {
				System.out.println("Creating file: " + file.getCanonicalPath());
				InputStream in = zipFile.getInputStream(entry);
				Scanner s = new Scanner(in).useDelimiter("\\A");
				String content = s.hasNext() ? s.next() : "";
				content = content.replace(templateIdLC, StringUtils.lcFirst(name));
				content = content.replace(templateIdUC, StringUtils.ucFirst(name));
				OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
				out.write(content.getBytes());
				in.close();
				out.close();
			}
		}
	}
	zipFile.close();
	try {
		Launcher.compilePackage(packagePath);
	} catch (Exception e) {
		e.printStackTrace();
	}
}
*/
