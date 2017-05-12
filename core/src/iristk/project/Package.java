package iristk.project;

import iristk.util.NameFilter;
import iristk.xml.XmlMarshaller;
import iristk.xml._package.Package.Classpath.Dll;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

public class Package {

	private static XmlMarshaller<iristk.xml._package.Package> packageReader = new XmlMarshaller<>("iristk.xml._package");

	private iristk.xml._package.Package spec;
	private File path;

	//public Package(iristk.xml._package.Package pack, File path) {
	//	this.xml = pack;
	//	this.path = path;
	//}

	public Package(File f) throws JAXBException, IOException, XMLStreamException {
		spec = packageReader.unmarshal(new File(f, "package.xml"));
		path = f.getCanonicalFile();
	}

	public String getName() {
		return spec.getName();
	}
	/**
	 * Returns version of this Package
	 * @return version number, 0 if no version property.
	 */
	public String getVersion() {
		String version = spec.getVersion();
		if(version==null){
			return "0";
		}else{			
			return version;
		}
	}

	public File getPath() {
		return path;
	}

	public File getPath(String subpath) {
		return new File(getPath(), subpath);
	}

	public File getLibPath(String sub) {
		return getPath("lib/" + sub);
	}

	public File getLibPath() {
		return getPath("lib");
	}

	public iristk.xml._package.Package getSpec() {
		return spec;
	}

	public List<String> getProvides(String className) {
		NameFilter filter = NameFilter.compile(className);
		ArrayList<String> result = new ArrayList<>();
		if (spec.getProvide() != null) {
			for (iristk.xml._package.Package.Provide.Class clazz : spec.getProvide().getClazz()) {
				if (filter.accepts(clazz.getType())) {
					result.add(clazz.getName());
				}
			}
		}
		return result;
	}
	/**
	 * System.loads all dll's in the Classpath of spec.
	 */
	public void loadLibs() {
		if (spec.getClasspath() != null) {
			for (Object entry : spec.getClasspath().getLibOrSrcOrDll()) {
				if (entry instanceof Dll) {
					Dll dll = (Dll)entry;
					if (dll.getArch() == null || dll.getArch().equals(System.getProperty("sun.arch.data.model"))) {
						System.load(getPath(dll.getPath()).getAbsolutePath());
					}
				}
			}
		}
	}

	public void loadLib(String pathWithinLib) {
		System.load(getLibPath(pathWithinLib).getAbsolutePath());
	}



}
