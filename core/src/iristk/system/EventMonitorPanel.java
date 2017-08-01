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

import iristk.util.ColorGenerator;
import iristk.util.Record;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class EventMonitorPanel extends JPanel implements EventListener {

	private static final int STATE_HEIGHT = 25;
	private static final int REDRAW_THRESHOLD = 100;
	private static final int TRACK_LABEL_WIDTH = 100;
	private static final int REDRAW_ADJUST = 300;
	
	private static final int MAX_EVENTS = 200;
	private static final int MAX_STATES = 50;

	private EventTracksPanel eventTracks;
	private IrisSystem system;
	private JCheckBox followNow;

	public EventMonitorPanel(IrisSystem system) {
		this.system = system;
		setLayout(new BorderLayout());
		eventTracks = new EventTracksPanel();

		//JScrollPane scrollPane = new JScrollPane(eventTracks, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		add(eventTracks, BorderLayout.CENTER);
		
		JPanel controls = new JPanel(new FlowLayout());
			
		followNow = new JCheckBox("Follow");
		followNow.setSelected(true);
		followNow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				eventTracks.setFollowNow(followNow.isSelected());
			}
		});
		controls.add(followNow);
		controls.add(new JLabel("ScrollWheel: Pan, Ctrl-SW: Scroll, Alt-SW: Zoom"));
		
		add(controls, BorderLayout.PAGE_END);
		
		system.addEventListener(this);
		
	}
	
	public EventMonitorPanel(IrisGUI gui, IrisSystem system) {
		this(system);
		gui.addDockPanel("eventmonitor", "Event Monitor", this, true);
	}

	@Override
	public void onEvent(Event event) {
		if (event.getName().equals("monitor.system.start")) {
			eventTracks.setupTracks();
		} else if (event.getName().equals("monitor.module.state")) {
			@SuppressWarnings("unchecked")
			List<String> lstates = (List<String>) event.getList("states");
			eventTracks.monitorState(event.getSender(), lstates);
		} else {
			eventTracks.addEvent(event);
		}
	}
	
	private class EventTracksPanel extends JPanel implements ActionListener, MouseMotionListener, MouseWheelListener, MouseListener {

		private BufferedImage canvas; 
		private long startTime = -1;

		private int msecPerPixel = 64;

		public int canvasOffset = 0;

		private Timer timer;

		private float[] skips = new float[]{0.1f, 0.5f, 1f, 5f, 10f, 15f, 20f, 30f, 60f, 300f, 600f};
		private boolean followNow = true;
		private ArrayList<Track> tracks = new ArrayList<Track>();
		private int topTrack = 0;

		public EventTracksPanel() {
			addMouseMotionListener(this);
			addMouseWheelListener(this);
			addMouseListener(this);
			timer = new Timer(100, this);
			timer.start();

		}

		public void setFollowNow(boolean selected) {
			this.followNow = selected;
		}

		public synchronized void addEvent(Event event) {
			Track track = eventTracks.getTrack(event.getSender());
			int trackn = eventTracks.getTrackN(event.getSender());
			EventTracksPanel.EventMark mark = new EventMark(track, event, this);
			track.events.add(mark);
			if (track.events.size() > MAX_EVENTS)
				track.events.remove(0);
			if (canvas != null && trackn >= eventTracks.topTrack) {
				Graphics2D g = canvas.createGraphics();
				mark.draw(g);
			}
		}

		private synchronized void zoomCanvas(int dir, int x) {
			if (dir > 0 && msecPerPixel < 600) {
				int time = canvasTime(x);
				msecPerPixel = msecPerPixel * 2;
				canvasOffset = (time / msecPerPixel) + TRACK_LABEL_WIDTH - x;
				drawCanvas();
				repaint();
			} else if (dir < 0 && msecPerPixel > 1) {
				int time = canvasTime(x);
				msecPerPixel = msecPerPixel / 2;
				canvasOffset = (time / msecPerPixel) + TRACK_LABEL_WIDTH - x;
				drawCanvas();
				repaint();
			}
		}

		private synchronized void moveCanvas(int diff) {
			canvasOffset += diff;
			drawCanvas();
			repaint();
		}
		
		private synchronized void scrollCanvas(int diff) {
			topTrack += diff;
			if (topTrack < 0)
				topTrack = 0;
			else if (topTrack >= tracks.size()) {
				topTrack = tracks.size() - 1;
			}
			drawCanvas();
			repaint();
		}

		private int canvasX(long time) {
			return (int) (time / msecPerPixel) - canvasOffset + TRACK_LABEL_WIDTH;
		}

		private int canvasX() {
			return canvasX(System.currentTimeMillis() - startTime);
		}

		private int canvasTime(int x) {
			return (x + canvasOffset - TRACK_LABEL_WIDTH) * msecPerPixel;
		}

		@Override
		public synchronized void actionPerformed(ActionEvent e) {
			if (canvas != null && startTime != -1) {
				if (followNow) {
					boolean redraw = false;
					while (canvasX() > canvas.getWidth() - REDRAW_THRESHOLD) {
						canvasOffset += REDRAW_ADJUST;
						redraw = true;
					}
					while (canvasX() < TRACK_LABEL_WIDTH) {
						canvasOffset -= 100;
						redraw = true;
					}
					if (redraw) {
						drawCanvas();
					}
				}
				repaint();
			}
		}

		private void drawCanvas() {
			int cHeight = STATE_HEIGHT;
			for (Track track : tracks) {
				cHeight += track.getHeight();
			}
			if (canvas == null || canvas.getWidth() != getWidth() || canvas.getHeight() != cHeight)
				canvas = new BufferedImage(Math.max(getWidth(), 10), cHeight, BufferedImage.TYPE_3BYTE_BGR);
			Graphics2D g = canvas.createGraphics();
			g.setColor(Color.white);
			g.fillRect(0, 0, canvas.getWidth(), cHeight);

			drawTimeline(g);
			int y = STATE_HEIGHT;
			
			g.setColor(Color.gray);
			g.fillRect(0, y, TRACK_LABEL_WIDTH, cHeight - 1);
			for (int t = topTrack; t < tracks.size(); t++) {
				Track track = tracks.get(t);
				g.setColor(Color.black);
				g.drawLine(0, track.top()+y, canvas.getWidth(), track.top()+y);		

				g.setColor(Color.white);
				g.drawString(track.name, 5, track.top() + 16 + y);

				// Don't draw states that will be overdrawn anyway, find the first one that we should draw
				int start = 0;
				for (int i = track.states.size() - 1; i >= 0; i--) {
					if (track.states.get(i).willCover() && canvasX(track.states.get(i).time) < TRACK_LABEL_WIDTH) {
						start = i;
						break;
					}
				}
				for (int i = start; i < track.states.size(); i++) {
					track.states.get(i).draw(g);
				}
				for (int i = 0; i < track.events.size(); i++) {
					track.events.get(i).draw(g);
				}
			}

		}
		
		private void drawTimeline(Graphics2D g) {
			g.setColor(Color.lightGray);
			//g.fillRect(0, canvas.getHeight() - STATE_HEIGHT, canvas.getWidth(), STATE_HEIGHT);
			g.fillRect(0, 0, canvas.getWidth(), STATE_HEIGHT);
			g.setColor(Color.black);
			float skip = 1f;
			float minw = Float.MAX_VALUE;
			for (float s : skips) {
				float w = Math.abs(((s * 1000f) / msecPerPixel) - 100);
				if (w < minw) {
					skip = s;
					minw = w;
				}
			}
			float from = Math.round(canvasOffset * msecPerPixel / 1000f);
			from = Math.round(from / skip) * skip;
			float to = from + Math.round(canvas.getWidth() * msecPerPixel / 1000f);
			for (float t = from; t < to; t += skip) {
				String label;
				if (skip < 1)
					label = String.format(Locale.US, "%.1fs", t);
				else if (skip < 60)
					label = String.format(Locale.US, "%.0fs", t);
				else
					label = String.format(Locale.US, "%.0fm", t / 60);
				int x = canvasX((int)(t * 1000f));
				//g.drawLine(x, canvas.getHeight() - STATE_HEIGHT, x, canvas.getHeight());
				g.drawLine(x, 0, x, STATE_HEIGHT);
				//g.drawString(label, x + 3, canvas.getHeight() - 5);
				g.drawString(label, x + 3, STATE_HEIGHT/2);
			}
		}

		private synchronized void setupTracks() {
			startTime = System.currentTimeMillis();
			tracks.clear();
			for (int i = 0; i < system.getModules().size(); i++) {
				tracks.add(new Track(system.getModules().get(i).getName()));
			}
			drawCanvas();
			repaint();
		}

		private Track getTrack(String name) {
			for (Track track : tracks) {
				if (track.name.equals(name))
					return track;
			}
			Track track = new Track(name);
			tracks.add(track);
			return track;
		}
		
		private int getTrackN(String name) {
			int n = 0;
			for (Track track : tracks) {
				if (track.name.equals(name))
					return n;
				n++;
			}
			return -1;
		}
		
		private synchronized void monitorState(String sender, List<String> lstates) {
			Track track = getTrack(sender);
			String[] states;
			if (lstates == null || lstates.size() == 0) {
				states = new String[]{null};
			} else {
				states = lstates.toArray(new String[0]);
			}
			String[] prevStates;
			if (track.states.size() > 0)
				prevStates = track.states.get(track.states.size()-1).states;
			else
				prevStates = new String[0];
			StateMark stateMark = new StateMark(track, states, prevStates);
			track.states.add(stateMark);
			if (track.states.size() > MAX_STATES)
				track.states.remove(0);
			if (canvas != null) {
				if (track.nStates < states.length) {
					track.nStates = states.length;
					drawCanvas();
				}
				Graphics2D g = canvas.createGraphics();
				stateMark.draw(g);
			}
		}

		public class EventMark {

			public Event event;
			public int time;
			private Track track;
			private EventTracksPanel monitor;

			public EventMark(Track track, Event event, EventTracksPanel monitor) {
				this.monitor = monitor;
				this.event = event;
				this.track = track;
				this.time = (int) (System.currentTimeMillis() - monitor.startTime);
			}

			public int getX() {
				return monitor.canvasX(time);
			}

			public int getY() {
				return track.top() + STATE_HEIGHT;
			}

			public void draw(Graphics2D g) {
				int x = getX();
				if (x <= TRACK_LABEL_WIDTH || x > monitor.canvas.getWidth())
					return;
				int y = getY();
				if (event.getName().startsWith("monitor")) {
					g.setColor(Color.orange);
				} else if (event.getName().startsWith("sense")) {
					g.setColor(Color.blue);
				} else if (event.getName().startsWith("action")) {
					g.setColor(Color.red);
				} else {
					g.setColor(Color.gray);
				}
				g.drawLine(x, y + 1, x, y + STATE_HEIGHT - 1);
			}

			public String getLabel() {
				Record lrec = new Record(event);
				/*
				if (event.getId() != null)
					lrec.put("id", event.getId());
				if (event.getTime() != null)
					lrec.put("time", event.getTime());
					*/
				return event.getName() + "\n" + lrec.toStringIndent();
			}

			public String toJsonString() {
				return event.toJSON().toString();
			}
			
			/*
			public String getXml() {
				try {
					return XmlUtils.indentXml(event.toXmlString());
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				}
				return null;
			}
			*/
		}

		private boolean contains(String[] array, String value) {
			for (int i = 0; i < array.length; i++) {
				if (array[i] != null && array[i].equals(value)) {
					return true;
				}
			}
			return false;
		}
		
		private int find(String[] array, String value) {
			for (int i = 0; i < array.length; i++) {
				if (array[i] != null && array[i].equals(value)) {
					return i;
				}
			}
			return -1;
		}

		private class StateMark {

			public String[] states;
			public int time;
			private Track track;
			
			private static final String KEEP = "__KEEP__";
			private static final String CLEAR = "__CLEAR__";

			public StateMark(Track track, String[] newStates, String[] prevStates) {
				states = new String[Math.max(newStates.length, prevStates.length)];
				Arrays.fill(states, CLEAR);
				for (int i = 0; i < prevStates.length; i++) {
					int index = find(newStates, prevStates[i]);
					if (index > -1) {
						if (newStates[index].equals(prevStates[i]))
							states[i] = KEEP;
						else
							states[i] = prevStates[i];
						newStates[index] = null;
					}
				}
				OUTER:
				for (int i = 0; i < newStates.length; i++) {
					if (newStates[i] != null) {
						for (int j = 0; j < states.length; j++) {
							if (states[j].equals(CLEAR)) {
								states[j] = newStates[i];
								continue OUTER;
							}
						}
					}
				}
				this.track = track;
				this.time = (int) (System.currentTimeMillis() - startTime);
			}

			public boolean willCover() {
				for (int i = 0; i < states.length; i++) {
					if (states[i].equals(KEEP)) {
						return false;
					}
				}
				return true;
			}

			public void draw(Graphics2D g) {
				int x = canvasX(time);
				if (x > canvas.getWidth())
					return;
				if (x < TRACK_LABEL_WIDTH)
					x = TRACK_LABEL_WIDTH;
				int y = track.top() + STATE_HEIGHT * 2;
				for (int i = 0; i < states.length; i++) {
					String label = states[i];
					if (label.equals(KEEP)) {
					} else if (label.equals(CLEAR)) {
						g.setColor(Color.white);
						g.fillRect(x, y, canvas.getWidth() - x, STATE_HEIGHT);
					} else {
						g.setColor(ColorGenerator.getColor(label));
						g.fillRect(x, y, canvas.getWidth() - x, STATE_HEIGHT);
						g.setColor(Color.gray);
						g.drawString(label, x + 2, y + 18);
						g.drawLine(x, y, x, y + STATE_HEIGHT);
					}
					y += STATE_HEIGHT;
				}
			}

		}

		private class Track {

			public ArrayList<EventMark> events = new ArrayList<EventMark>();
			public ArrayList<StateMark> states = new ArrayList<StateMark>();
			public int nStates = 0;
			public String name;

			public Track(String name) {
				this.name = name;
			}

			public int top() {
				int top = 0;
				for (int i = topTrack; i < tracks.size(); i++) {
					if (tracks.get(i) == this)
						return top;
					else
						top += tracks.get(i).getHeight();
				}
				return 0;
			}

			public int getHeight() {
				return STATE_HEIGHT * (nStates + 1);
			}

			public ArrayList<EventMark> getEvents(int time) {
				ArrayList<EventMark> result = new ArrayList<EventMark>();
				synchronized (EventTracksPanel.this) {
					for (EventMark event : events) {
						if (Math.abs(event.time - time) <= msecPerPixel * 2) {
							result.add(event);
						}
					}
				}
				return result;
			}

		}

		private List<EventMark> currentEvents = null;


		@Override
		protected synchronized void paintComponent(Graphics g) {
			//long t = System.currentTimeMillis();
			if (canvas != null) {
				if (canvas.getWidth() != getWidth()) 
					drawCanvas();
				//EventTracksPanel.this.setSize(getWidth(), canvas.getHeight());
				g.clearRect(0, 0, getWidth(), getHeight());
				g.drawImage(canvas, 0, 0, null);
				g.setColor(Color.green);
				int x = canvasX();
				if (x > TRACK_LABEL_WIDTH)
					g.drawLine(x, 0, x, canvas.getHeight() - 1);
				if (currentEvents != null && currentEvents.size() > 0) {
					String label = "";
					for (EventMark mark : currentEvents) {
						if (label.length() > 0)
							label += "\n";
						label += mark.getLabel();
					}
					drawLabel(g, label, currentEvents.get(0).getX(), currentEvents.get(0).getY() + STATE_HEIGHT);
				}
			}

			//System.out.println("C: " + (t - System.currentTimeMillis()));
		}

		public List<EventMark> getEvents(int x, int y) {
			int time = canvasTime(x);
			for (int i = topTrack; i < tracks.size(); i++) {
				Track track = tracks.get(i);
				if (y > track.top() + STATE_HEIGHT && y < track.top() + STATE_HEIGHT * 2) {
					return track.getEvents(time);
				}
			}
			return null;
		}

		@Override
		public void mouseDragged(MouseEvent e) {
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			currentEvents = getEvents(e.getX(), e.getY());
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			int rot = e.getWheelRotation();
			int mod = e.getModifiersEx();
			if ((mod & InputEvent.ALT_DOWN_MASK) > 0) {
				zoomCanvas(rot, e.getX());
			} else if ((mod & InputEvent.CTRL_DOWN_MASK) > 0) {
				scrollCanvas(rot);
			} else {
				moveCanvas(rot * 50);
			}
		}

		private void drawLabel(Graphics g, String text, int x, int y) {
			FontMetrics m = g.getFontMetrics(g.getFont());
			String[] label = text.split("\n");
			int w = 0;
			for (String l : label) {
				w = Math.max(m.stringWidth(l), w);
			}
			w += 4;
			int h = m.getHeight() * label.length + 4;
			g.setColor(new Color(255, 255, 200));
			g.fillRect(x, y, w, h);
			g.setColor(Color.black);
			g.drawRect(x, y, w, h);
			for (int i = 0; i < label.length; i++) {
				g.drawString(label[i], x + 3, y + (i + 1) * m.getHeight());
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			List<EventMark> marks = getEvents(e.getX(), e.getY());
			if (marks != null)
				for (EventMark mark : marks)
					System.out.println(mark.toJsonString());
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

	}
}