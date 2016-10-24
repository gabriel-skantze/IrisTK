package iristk.project;

import iristk.util.ArgParser;
import iristk.util.ProcessRunner;
import iristk.util.Utils;
import iristk.util.ZipUtils;
import iristk.util.ProcessRunner.ProcessListener;
import iristk.xml._package.Package.Classpath.Lib;
import iristk.xml._package.Package.Classpath.Src;
import iristk.xml._package.Package.Run.Command;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.Diagnostic.Kind;

public class Launcher {
	
	private static Set<String> sysLoaderClassPaths = new HashSet<String>();

	public static HashSet<String> javaLibPaths = new HashSet<>();
	
	public static void main(String[] args) throws Throwable {
		try {
			Project.initialize();
			System.out.println(Project.main.getName() + " version " + Project.main.getVersion() + " at " + Project.main.getPath());
			System.out.println();
			if (args.length == 0) {
				listCommands();
			} else {
				startCommand(args[0], cropArgs(args, 1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void install(String[] args) {
		try {
			if (!new File(new File(System.getProperty("user.dir")), getExecutable().getName()).exists()) {
				System.out.println("ERROR: Your must be in the " + Project.main.getName() + " root folder when installing");
				System.exit(0);
			}
			System.out.println("Installing " + Project.main.getName() + "...\n");
			System.out.println("Setting %" + Project.main.getName() + "% to " + System.getProperty("user.dir") + "\n");
			setEnv(Project.main.getName(), System.getProperty("user.dir"));
			String path = getEnv("PATH");
			if (path == null || !path.contains("%" + Project.main.getName() + "%")) {
				System.out.println("Adding %" + Project.main.getName() + "% to %PATH%\n");
				setEnv("PATH", path + ";%" + Project.main.getName() + "%");
			}
			setupEclipse(new String[0]);
			System.out.println("\nPress enter to continue...");
			Scanner keyboard = new Scanner(System.in);
			keyboard.nextLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void javadoc(String[] args) throws IOException {
		Project.main.getPath();
		String outdir = new File(Project.main.getPath(), "doc/javadoc").getAbsolutePath();
		String cp = Project.main.getClasspath();
		String srcpaths = Project.main.getSrcPaths();
		String cmd = "javadoc -Xdoclint:none -windowtitle \"" + Project.main.getName() + " Javadoc\" -d " + outdir + " -quiet -sourcepath \"" + srcpaths + "\" -classpath \"" + cp + "\" -subpackages iristk";
		System.out.println(cmd);
		new ProcessRunner(cmd, System.out, System.err).waitFor();
	}
	
	public static String getEnv(String name) throws Exception {
		String value = ProcessRunner.eval("reg query HKCU\\Environment /v " + name).trim();
		for (String line : value.split("\n")) {
			Matcher m = Pattern.compile(name + " +[^ ]+ +(.*)", Pattern.CASE_INSENSITIVE).matcher(line.trim());
			if (m.matches())
				return m.group(1);
		}
		return null;
	}

	public static void setEnv(String name, String value) throws Exception {
		//value = value.replace("%", "~");
		//String result = getCmd("reg add HKCU\\Environment /f /v " + name + " /t " + type + " /d \"" + value + "\"").trim();
		String result = ProcessRunner.eval("setx " + name + " \"" + value + "\"").trim();
		//if (!result.contains("successfully")) {
		if (!result.contains("SUCCESS")) {
			throw new Exception(result);
		}
	}
	
	public static void  zipPackage(String[] args) {
		if (args.length == 0) {
			System.err.println("No package provided");
			System.exit(0);
		}
		String packName = args[0];
		Package pack = Project.main.getPackage(packName);
		if (pack == null) {
			System.err.println("Could not find package " + packName);
			System.exit(0);
		}
		File zipfile = new File(pack.getName() + ".zip");
		try {
			ZipUtils.zipDirectory(pack.getPath(), pack.getPath(), zipfile);
			System.out.println("\nCreated zip: " + zipfile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static void setupEclipse(String[] args) {
		try {
			ArgParser parser = new ArgParser();
			parser.allowRestArguments(true);
			parser.addBooleanArg("i", "Independent project");
			parser.parse(args);
			boolean independent = (Boolean)parser.get("i");
			List<String> rest = parser.getRestArguments();
			Project proj = null;
			if (rest.size() == 0) {
				proj = Project.main;
			} else {
				File pfile = new File(rest.get(0), "project.properties");
				if (!pfile.exists()) {
					System.out.println("Could not find " + pfile.getAbsolutePath());
					System.exit(0);
				}
				proj = Project.readProject(pfile);
				proj.updatePackages();
			}
			System.out.println("Setting up Eclipse classpath for " + proj.getName());
			File cpFile = new File(proj.getPath(), ".classpath").getCanonicalFile();
			writeProjectClasspathFile(proj, cpFile, independent);
			File projFile = new File(proj.getPath(), ".project").getCanonicalFile();
			if (!projFile.exists()) {
				writeProjectFile(projFile, proj.getName());
			}
			/*
				File pkgFile = new File(new File(args[0]), "package.xml").getCanonicalFile();
				
				Package pkgXml = IrisUtils.readPackageXml(pkgFile);
				File pkgPath = pkgFile.getParentFile();
				String packageName = pkgXml.getName();
				System.out.println("Setting up Eclipse classpath for package " + packageName);
				if (pkgXml.getClasspath() != null) {
					File cpFile = new File(pkgPath, ".classpath").getCanonicalFile();
					writePackageClasspathFile(cpFile, pkgXml.getClasspath());
				}
				File projFile = new File(pkgPath, ".project").getCanonicalFile();
				if (!projFile.exists()) {
					writeProjectFile(projFile, "IrisTK - " + packageName);
				}
			} else {
				System.out.println("Setting up Eclipse classpath for IrisTK");
				File cpFile = new File(IrisUtils.getIristkPath(), ".classpath");
				writeIrisTKClasspathFile(cpFile);
			}
			*/
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writeProjectFile(File file, String projectName) throws IOException {
		System.out.println("Creating file " + file.getAbsolutePath());
		Utils.writeTextFile(file, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<projectDescription><name>" + projectName + "</name><comment></comment>"
				+ "<projects></projects><buildSpec><buildCommand><name>org.eclipse.jdt.core.javabuilder</name>"
				+ "<arguments></arguments></buildCommand></buildSpec>"
				+ "<natures><nature>org.eclipse.jdt.core.javanature</nature></natures></projectDescription>");
	}

	/*
	private static void writePackageClasspathFile(File file, Classpath cp) throws FileNotFoundException {
		System.out.println("Creating file " + file.getAbsolutePath());
		PrintWriter pw = new PrintWriter(file); 
		pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		pw.println("<classpath>");
		pw.println("<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER\"/>");
		for (Object entry : cp.getLibOrSrcOrDll()) {
			if (entry instanceof Lib) {
				pw.println("<classpathentry exported=\"true\" kind=\"lib\" path=\"" + ((Lib)entry).getPath() + "\"/>");
			} else if (entry instanceof Src) {
				pw.println("<classpathentry kind=\"src\" path=\"" + ((Src)entry).getPath() + "\"/>");
			}
		}
		pw.println("<classpathentry combineaccessrules=\"false\" kind=\"src\" path=\"/IrisTK\"/>");
		pw.println("<classpathentry kind=\"output\" path=\"bin\"/>");
		pw.println("</classpath>");
		pw.close();
	}
	*/

	private static void writeProjectClasspathFile(Project proj, File file, boolean independent) throws FileNotFoundException {
		System.out.println("Creating file " + file.getAbsolutePath());
		PrintWriter pw = new PrintWriter(file); 
		pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		pw.println("<classpath>");
		pw.println("<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER\"/>");
		String root = proj.getPath().getAbsolutePath();
		for (Package pack : proj.getPackages()) {
			//System.out.println(pack.getName());
			if (pack.getSpec().getClasspath() != null) {
				if (!pack.getPath().getAbsolutePath().startsWith(root)) {
					//System.out.println("Skipping " + IrisUtils.getPackagePath(pack.getName()).getAbsolutePath());
					continue;
				}
				String path = pack.getPath().getAbsolutePath().replace(root, "").replace("\\", "/");
				if (path.startsWith("/"))
					path = path.substring(1);
				if (path.length() > 0 && !path.endsWith("/"))
					path = path += "/";
				for (Object entry : pack.getSpec().getClasspath().getLibOrSrcOrDll()) {
					if (entry instanceof Lib) {
						pw.println("<classpathentry exported=\"true\" kind=\"lib\" path=\"" + path + ((Lib)entry).getPath() + "\"/>");
						//System.out.println(((Lib)entry).getPath());
					} else if (entry instanceof Src) {
						if (pack.getPath("src").exists()) {
							pw.println("<classpathentry kind=\"src\" output=\"" + path + ((Src)entry).getOutput() + "\" path=\"" + path + ((Src)entry).getPath() + "\"/>");
							//System.out.println(pad(pack.getName(), 40) + "Source folder");
						} else {
							pw.println("<classpathentry kind=\"lib\" path=\"" + path + ((Src)entry).getOutput() + "\"/>");
							//System.out.println(pad(pack.getName(), 40) + "Class folder");
						}
					}
				}
			}
		}

		for (Project dep : proj.getDepends()) {
			if (independent) {
				for (Package pack : dep.getAllPackages()) {
					if (pack.getSpec().getClasspath() != null) {
						for (Object entry : pack.getSpec().getClasspath().getLibOrSrcOrDll()) {
							if (entry instanceof Lib) {
								pw.println("<classpathentry kind=\"lib\" " + 
										"path=\"" + pack.getPath(((Lib)entry).getPath()).getAbsolutePath() + "\"" +
										"/>");
							} else if (entry instanceof Src) {
								pw.println("<classpathentry kind=\"lib\" " + 
										"path=\"" + pack.getPath(((Src)entry).getOutput()).getAbsolutePath() + "\" " +
										"sourcepath=\"" + pack.getPath(((Src)entry).getPath()).getAbsolutePath() + "\"" +
										"/>");
							}
						}
					}
				}
			} else {
				pw.println("<classpathentry combineaccessrules=\"false\" exported=\"true\" kind=\"src\" path=\"/" + dep.getName() + "\"/>");
			}
		}
		//pw.println("<classpathentry kind=\"output\" path=\"core/bin\"/>");
		pw.println("</classpath>");
		pw.close();
	}

	
	public static void listCommands() {
		System.out.println("Usage:");
		String cmd = getExecutable().getName();
		System.out.println(cmd + " [command] [args]\n");
		System.out.println("Available commands in packages:");
		for (Package pack : Project.main.getAllPackages()) {
			if (pack.getSpec().getRun() != null) {
				System.out.println("\n" + pack.getName() + ":");
				for (Command command : pack.getSpec().getRun().getCommand()) {
					String descr = "";
					if (command.getDescr() != null) descr = command.getDescr();
					System.out.println("  " + Utils.pad(command.getName(), 15) + descr);
				}
			}
		}
	}
	
	public static File getExecutable() {
		return new File(System.getProperty("iristk.exefile"));
	}

	public static void listPackages(String[] args) {
		for (Package pack : Project.main.getAllPackages()) {
			System.out.println(Utils.pad(pack.getName(), 20) + pack.getPath());
		}
	}

	private static void startCommand(String progName, String[] args) throws Throwable {
		setupClasspath();

		Command command = findCommand(progName);
		if (command != null) {
			//File progDir = IrisUtils.getPackagePath(program.getName());
			if (command.getMethod() != null) {
				Class<?>[] paramTypes = new Class<?>[1];
				paramTypes[0] = String[].class; 
				Method method = Class.forName(command.getClazz()).getMethod(command.getMethod(), paramTypes);
				if (method != null) {
					try {
						method.invoke(null, (Object) args);
					} catch (InvocationTargetException e) {
						throw e.getTargetException();
					}
					return;
				}
			} else {
				//runJava(command.isArch32(), command.getClazz(), args, System.out, System.err, true, null);

				Class<?>[] paramTypes = new Class<?>[1];
				paramTypes[0] = String[].class;
				Method method = Class.forName(command.getClazz()).getMethod("main", paramTypes);
				if (method != null) {
					try {
						method.invoke(null, (Object) args);
					} catch (InvocationTargetException e) {
						throw e.getTargetException();
					}
					return;
				}

				return;

			}
		}

		//runJava(false, progName, args, System.out, System.err, true, null);
		System.out.println("No command with the name '" + progName + "' found");
	}
	
	public static void setupClasspath() {
		try {
			URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
			Class<?> sysclass = URLClassLoader.class;
			Method method = sysclass.getDeclaredMethod("addURL", new Class[] {URL.class});
			method.setAccessible(true);
			for (Package pack : Project.main.getAllPackages()) {
				if (pack.getSpec().getClasspath() != null) {
					for (Object entry : pack.getSpec().getClasspath().getLibOrSrcOrDll()) {
						URL url = null;
						if (entry instanceof Lib) {
							url = pack.getPath(((Lib)entry).getPath()).toURI().toURL();
						} else if (entry instanceof Src) {
							url = pack.getPath(((Src)entry).getOutput()).toURI().toURL();
						}
						if (url != null) {
							if (!sysLoaderClassPaths.contains(url.toString())) {
								method.invoke(sysloader, new Object[] {url});
								sysLoaderClassPaths.add(url.toString());
							}
						}
					} 
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Command findCommand(String cmdName) {
		for (Package pack : Project.main.getAllPackages()) {
			if (pack.getSpec().getRun() != null) {
				for (Command cmd : pack.getSpec().getRun().getCommand()) {
					if (cmd.getName().equals(cmdName) || (pack.getName() + "." + cmd.getName()).equals(cmdName)) {
						return cmd;
					}
				}
			}
		}
		return null;
	}

	private static String getJavaCmd(boolean force32) throws Exception {
		if (force32 && System.getProperty("sun.arch.data.model").equals("64")) {
			File javaDir = new File(System.getenv("ProgramFiles(x86)"),  "Java");
			if (javaDir.exists() && javaDir.isDirectory()) {
				String[] versions = new String[]{"jdk1.8", "jre8", "jre1.8"};
				for (String v : versions) {
					for (File f : javaDir.listFiles()) {
						if (f.getName().startsWith(v)) {
							File javaexe = new File(new File(f.getPath(), "bin"), "java.exe");
							if (javaexe.exists()) {
								return javaexe.getAbsolutePath();
							}
						}
					}
				}
			}
			throw new Exception("Could not find Java 32-bit runtime");
		} else {
			File javaexe = new File(new File(new File(System.getProperty("java.home")), "bin"), "java.exe");
			if (javaexe.exists()) {
				return javaexe.getAbsolutePath();
			}
			throw new Exception("Could not find Java runtime");
		}
	}
	
	private static String[] cropArgs(String[] args, int n) {
		if (n > args.length) n = args.length;
		String[] result = new String[args.length - n];
		System.arraycopy(args, n, result, 0, result.length);
		return result;
	}

	public static void compilePackage(File packagePath) throws Exception {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if (ToolProvider.getSystemJavaCompiler() == null) {
			throw new Exception("Could not find Java Compiler");
		}
		File outputDir = new File(packagePath, "bin");
		Utils.deleteFolder(outputDir);
		outputDir.mkdirs();
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, Locale.US, null);
		List<String> files = new ArrayList<>();
		Utils.listFiles(new File(packagePath, "src").getAbsolutePath(), files, ".*\\.java$");
		for (String f: files) {
			System.out.println("Compiling " + f);
		}
		Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(files);
		Iterable<String> args = Arrays.asList("-d", outputDir.getAbsolutePath(), "-cp", Project.main.getClasspath());
		JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, args, null, compilationUnits);
		boolean success = task.call();
		try {
			fileManager.close();
		} catch (IOException e) {
		}
		for (Diagnostic<? extends JavaFileObject> diag : diagnostics.getDiagnostics()) {
			if (diag.getKind() == Kind.ERROR) {
				int javaLine = (int) diag.getLineNumber();
				String message = diag.getMessage(Locale.US);
				throw new Exception(message);
			}
		}
		if (!success) {
			throw new Exception("Compilation failed for unknown reason");
		}
	}

	public static ProcessRunner runCommand(String name, String... args) throws Exception {
		ArrayList<String> cmd = new ArrayList<String>();
		cmd.add(name);
		for (String a : args)
			cmd.add(a);
		return runJava(false, Launcher.class.getName(), null, cmd.toArray(new String[0]), System.out, System.err, false, null, null);
	}

	public static ProcessRunner runJava(boolean force32, String mainClass, String[] options, String[] args, OutputStream stdout, OutputStream stderr, boolean waitFor, ProcessListener listener, File workingDir) throws Exception {
		List<String> cmd = new ArrayList<>();
		cmd.add(getJavaCmd(force32));
		cmd.add("-cp");
		cmd.add(Project.main.getClasspath());
		cmd.add("-Diristk.project=\"" + Project.main.getPath() + "\"");
		if (options != null)
			for (String op : options) {
				cmd.add(op);
			}
		cmd.add(mainClass);
		if (args != null)
			for (String arg : args) {
				cmd.add(arg);
			}
		//String cmd = "\"" + javaCmd + "\" -cp \"" + IrisUtils.getClasspath() + "\" " + mainClass + " " + StringUtils.join(args, " ");
		ProcessRunner proc = new ProcessRunner(cmd, stdout, stderr, listener, workingDir);
		if (waitFor)
			proc.waitFor();
		return proc;
	}

	public static void checkDepends(String[] args) throws IOException {
		ArgParser parser = new ArgParser();
		parser.addBooleanArg("r", "check recursively");
		parser.parse(args);
		System.out.println("Checking dependencies in " + Project.main.getPath());
		for (Package pack : Project.main.getAllPackages()) {
			List<String> cmd = new ArrayList<>();
			cmd.add("jdeps");
			cmd.add("-cp");
			cmd.add(Project.main.getClasspath());
			cmd.add("-v");
			if ((Boolean)parser.get("r"))
				cmd.add("-R");
			cmd.add(pack.getPath("bin").getAbsolutePath());
			ProcessRunner proc = new ProcessRunner(cmd, null, System.err, new ProcessListener() {

				@Override
				public void processOutput(String line) {
					if (line.contains("not found"))
						System.out.println(line);
				}

				@Override
				public void processDone(int result) {
				}
			}, null);
			proc.waitFor();
		}
	}

	public static void addJavaLibPath(File path) {
		if (!javaLibPaths.contains(path.getAbsolutePath())) {
			try {
				System.setProperty("java.library.path", System.getProperty("java.library.path") + ";" + path.getAbsolutePath());
				Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
				fieldSysPath.setAccessible( true );
				fieldSysPath.set( null, null );
				javaLibPaths.add(path.getAbsolutePath());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean is64arch() {
		return System.getProperty("sun.arch.data.model").equals("64");
	}
	
	
}
