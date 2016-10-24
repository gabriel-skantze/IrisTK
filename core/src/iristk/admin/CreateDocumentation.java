package iristk.admin;

import iristk.util.Pair;
import iristk.util.ProcessReader;
import iristk.util.Utils;

import java.io.File;
import java.util.*;

public class CreateDocumentation {

	public static class Menu extends ArrayList<MenuSection> {
		public Menu(String struct) {
			List<MenuItem> items = null;
			for (String row : struct.split("\n")) {
				row = row.trim();
				if (row.length() == 0)
					continue;
				if (!row.contains(";")) {
					items = new ArrayList<>();
					add(new MenuSection(row, items));
				} else {
					String[] pair = row.split(";");
					items.add(new MenuItem(pair[0].trim(), pair[1].trim()));
				}
			}
		}};
		
	public static class MenuSection extends Pair<String, List<MenuItem>> {
		public MenuSection(String first, List<MenuItem> second) {
			super(first, second);
		}};
		
	public static class MenuItem extends Pair<String, String> {
		public MenuItem(String first, String second) {
			super(first, second);
		}};
	
	private static String menu(Menu menu, String id) {
		StringBuilder menus = new StringBuilder();

		for (MenuSection section : menu) {

			OUTER: {
				for (MenuItem item : section.getSecond()) {
					if (item.getFirst().equals(id)) {
						menus.append("<section class=\"active\">");
					 	break OUTER;
					}
				}
				menus.append("<section>");
			}

			menus.append("<p class=\"title\" data-section-title><a href=\"#\">" + section.getFirst() + "</a></p>");
			menus.append("<div class=\"content\" data-section-content>");
			menus.append("<ul class=\"side-nav\">");
			for (MenuItem item : section.getSecond()) {
				String page = item.getFirst();
				if (!page.contains("."))
					page += ".html";
				if (item.getFirst().equals(id))
					menus.append("<li class=\"active\">");
				else
					menus.append("<li>");
				menus.append("<a href=\"" + page + "\">" + item.getSecond() + "</a></li>");
			}
			menus.append("</ul></div></section>");
		}
		return menus.toString();
	}
		
	public static void createWebsite() throws Exception {
		Menu website_menu = new Menu(Utils.readTextFile(new File("doc/markdown/website.txt")));
		Menu guide_menu = new Menu(Utils.readTextFile(new File("doc/markdown/guide.txt")));
		String website_templ = Utils.readString(CreateDocumentation.class.getResourceAsStream("website.html"));
		String guide_templ = Utils.readString(CreateDocumentation.class.getResourceAsStream("guide.html"));
		File mddir = new File("doc/markdown");
		for (String filen : mddir.list()) {
			if (filen.endsWith(".md")) {				
				String id = filen.replace(".md", "");
				System.out.println(id);
				
				String content = ProcessReader.readProcess("pandoc " + new File(mddir, filen));

				String guide_html = guide_templ.replace("<?CONTENT?>", content).replace("<?MENU?>", menu(guide_menu, id));
				String website_html = website_templ.replace("<?CONTENT?>", content).replace("<?MENU?>", menu(website_menu, id));
				Utils.writeTextFile(new File("doc/website/" + id + ".html"), website_html);
				Utils.writeTextFile(new File("doc/guide/" + id + ".html"), guide_html);

			}
		}
		
	}
	
	public static void createManual() throws Exception {
		Menu menu = new Menu(Utils.readTextFile(new File("doc/markdown/guide.txt")));
		StringBuilder manualmd = new StringBuilder();
		for (MenuSection section : menu) {
			manualmd.append("# " + section.getFirst() + "\n\n");
			for (MenuItem item : section.getSecond()) {
				File md = new File("doc/markdown/" + item.getFirst() + ".md");
				if (md.exists()) {
					manualmd.append(Utils.readTextFile(md) + "\n\n");
				}
			}
		}
		Utils.writeTextFile(new File("doc/guide/manual.md"), manualmd.toString());
		System.out.println(ProcessReader.readProcess("pandoc doc/guide/manual.md -o doc/guide/manual.html --toc -N -s -t html5"));
	}
	
	public static void main(String[] args) throws Exception {
		createWebsite();
		//createManual();
	}

}
