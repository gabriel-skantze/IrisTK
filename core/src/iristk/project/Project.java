package iristk.project;

import iristk.util.Utils;
import iristk.xml._package.Package.Classpath.Lib;
import iristk.xml._package.Package.Classpath.Src;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.xml.bind.JAXBException;

public class Project {

	public static Project main;

	private static boolean initialized = false;

	private String name;
	private String version;
	private File path;
	private List<Project> depends = new ArrayList<>();
	private List<Package> packages = new ArrayList<>();
	private List<File> packageDirs = new ArrayList<>();	
	private List<File> packageRoots = new ArrayList<>();

	private Project() {}

	static {
		initialize();
	}

	public static synchronized void initialize() {
		if (!initialized ) {
			initialized = true;
			File projectFile = findProjectFile();
			if (projectFile != null) {
				main = readProject(projectFile);
				main.updatePackages();
			} else {
				System.out.println("No project found");
			}
		}
	}
	
	private static File findProjectFile() {
		File projectFile = new File(new File(System.getProperty("user.dir")), "project.properties");
		if (projectFile.exists())
			return projectFile;
		String projDir = System.getProperty("iristk.project");
		if (projDir != null) {
			projectFile = new File(new File(projDir), "project.properties");
			if (projectFile.exists())
				return projectFile;
		}
		return null;
	}

	public synchronized void updatePackages() {
		packages.clear();
		for (File root : packageRoots) {
			if (root.exists() && root.isDirectory()) {
				for (File dir : root.listFiles()) {
					addPackage(dir);
				}
			}
		}
		for (File dir : packageDirs) {
			if (dir.exists()) {
				addPackage(dir);
			}
		}
		for (Project dep : depends) {
			dep.updatePackages();
		}
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public File getPath() {
		return path;
	}

	public File getPath(String subpath) {
		return new File(path, subpath);
	}
	
	public List<Project> getDepends() {
		return depends;
	}

	public synchronized static Project readProject(File file) {
		Project project = new Project();
		project.path = file.getParentFile();
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(file));
			project.version = props.getProperty("version");
			project.name = props.getProperty("name");
			String packages = props.getProperty("packages");
			if (packages != null) {
				for (String p : packages.split(";")) {
					p = Utils.replaceEnv(p.trim());
					if (p.endsWith("/*")) {
						p = p.replace("/*", "");
						File f = new File(p);
						if (!f.isAbsolute()) {
							f = new File(project.path, p);
						} 
						if (f.exists()) {
							project.addPackageRoots(f);
						}
					} else {
						File f = new File(p);
						if (!f.isAbsolute()) {
							f = new File(project.path, p);
						} 
						if (f.exists()) {
							project.addPackageDirs(f);
						}
					}
				}
			}
			if (props.getProperty("depends") != null) {
				for (String dep : props.getProperty("depends").split(";")) {
					dep = Utils.replaceEnv(dep.trim());
					File projectFile = new File(new File(dep), "project.properties");
					if (projectFile.exists()) {
						project.depends.add(readProject(projectFile));
					} else {
						System.out.println("Cannot find dependent project " + dep);
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return project;
	}
	
	public synchronized void addPackageRoots(File... dirs) {
		OUTER:
		for (File dir : dirs) {
			for (File r: packageRoots) {
				try {
					if (dir.getCanonicalPath().equals(r.getCanonicalPath()))
						continue OUTER;
				} catch (IOException e) {
					continue OUTER;
				}
			}
			packageRoots.add(dir);
		}
	}

	public synchronized void removePackageDirs(File dir) {
		for (File r: new ArrayList<>(packageDirs)) {
			try {
				if (dir.getCanonicalPath().equals(r.getCanonicalPath())) {
					packageDirs.remove(r);
				}
			} catch (IOException e) {
			}
		}
	}
	
	public synchronized void addPackageDirs(File... dirs) {
		OUTER:
		for (File dir : dirs) {
			for (File r: packageDirs) {
				try {
					if (dir.getCanonicalPath().equals(r.getCanonicalPath()))
						continue OUTER;
				} catch (IOException e) {
					continue OUTER;
				}
			}
			packageDirs.add(dir);
		}
	}

	private void addPackage(File dir) {
		if (new File(dir, "package.xml").exists()) {
			try {
				Package pack = new Package(dir);
				if (!hasPackage(packages, pack.getName()))
					packages.add(new Package(dir));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		}
	}
	
	public synchronized String getSrcPaths() {
		StringBuilder srcpath = new StringBuilder();
		for (Package pack : getPackages()) {
			if (pack.getSpec().getClasspath() != null) {
				for (Object entry : pack.getSpec().getClasspath().getLibOrSrcOrDll()) {
					if (entry instanceof Src) {
						srcpath.append(new File(pack.getPath(), ((Src)entry).getPath()).getAbsolutePath() + ";");
					}
				} 
			}
		}
		return srcpath.toString();
	}

	public synchronized String getClasspath() {
		StringBuilder classpath = new StringBuilder();
		for (Package pack : getAllPackages()) {
			if (pack.getSpec().getClasspath() != null) {
				for (Object entry : pack.getSpec().getClasspath().getLibOrSrcOrDll()) {
					if (entry instanceof Lib) {
						classpath.append(new File(pack.getPath(), ((Lib)entry).getPath()).getAbsolutePath() + ";");
					} else if (entry instanceof Src) {
						classpath.append(new File(pack.getPath(), ((Src)entry).getOutput()).getAbsolutePath() + ";");
					}
				} 
			}
		}
		return classpath.toString();
	}

	public List<Package> getPackages() {
		return packages;
	}

	public synchronized List<Package> getAllPackages() {
		List<Package> plist = new ArrayList<>();
		plist.addAll(packages);
		for (Project dep : depends) {
			for (Package p : dep.getAllPackages()) {
				if (!hasPackage(plist, p.getName()))
					plist.add(p);
			}
		}
		return plist;
	}
	
	private static boolean hasPackage(List<Package> packages, String name) {
		for (Package pack : packages) {
			if (pack.getName().equals(name))
				return true;
		}
		return false;
	}

	public synchronized Package getPackage(String packName) {
		for (Package pack : getAllPackages()) {
			if (pack.getName().equals(packName))
				return pack;
		}
		return null;
	}

	public synchronized Package getPackage(Class<?> packageClass) {
		try {
			String clazzPath = packageClass.getClassLoader().getResource(packageClass.getName().replace('.', '/') + ".class").toURI().toString();
			if (clazzPath.startsWith("jar:"))
				clazzPath = clazzPath.replaceFirst("jar:", "");
			for (Package pack : getAllPackages()) {
				if (clazzPath.toUpperCase().startsWith(pack.getPath().toURI().toString().toUpperCase())) {
					return pack;
				}
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public synchronized List<String> getPackageProvides(String className) {
		ArrayList<String> result = new ArrayList<>();
		for (Package pack : getAllPackages()) {
			result.addAll(pack.getProvides(className));
		}
		return result;
	}

	public static File parseURI(String uris) {
		try {
			URI uri = new URI(uris);
			if (uri.getScheme() != null && uri.getScheme().equals("iristk")) {
				if (Project.main.getPackage(uri.getHost()) != null) {
					return Project.main.getPackage(uri.getHost()).getPath(uri.getPath());
				}
			} 
		} catch (URISyntaxException e) {
		}
		return null;
	}

	public List<Project> getAllProjects() {
		List<Project> list = new ArrayList<>();
		list.add(this);
		for (Project dep : depends) {
			list.addAll(dep.getAllProjects());
		}
		return list;
	}


}
