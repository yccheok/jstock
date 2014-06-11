package net.sf.nachocalendar.components;


import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.CardLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;

/*
   PLEASE READ THIS:

   You may use this code for your own uses, including scavenging
   functions from it.  However, I insist that I remain permanently
   acknowledged in your source code and, if possible, in the final
   product (executable, web page, what have you).  In addition, please
   email me if you plan to use part of this code.

   I wrote this program in my spare time for fun.  It is neither
   elegant nor polished.  You have been warned.

   Chris Dolan (dolan@astro.wisc.edu)  Nov 25, 1998

*/

/* 

   This code is written for Java 1.0 

   Note that I'm more of a C programmer than a C++ or Java programmer,
   so my use of object-oriented tools is less than elegant.

*/

/*

There are three classes in this applet:

 - MoonPhase

   The main body which includes all of the initialization and event
   handling.

 - MoonCanvas/MoonCanvas2

   The canvases on which all of the graphics are produced.  This
   includes the double-buffered rendering.  The primary entrance is
   the paintSky function.  One shows the Top view while the other
   shows the Earth view.

 - PhaseThread

   The code that signals the animation.  This code runs in a parallel
   background thread so the user can interact with the applet while
   the graphics are being animated.

*/

public class MoonPhase extends Applet {
  public double phase;
  public double sundist, moondist;
  public double sunsize, earthsize, moonsize;
  public int width, height;
  public boolean both;

// Double buffering Objects
  public Image buf;                 // bitmap for double buffering
  public Graphics gBuf;             // gc to draw on bitmap

// GUI objects -- Should be private unless absolutely necessary
  private MoonCanvas canvas;
  public MoonCanvas2 canvas2;
  private TextField fov;
  private Choice viewmenu;
  private Panel center;
  public Canvas current;
  private Button runbutton;

  public void init() {
    Dimension d = getSize();

    width = d.width;
    height = d.height;

    setBackground(Color.lightGray);
    setFont(new Font("Helvetica", Font.PLAIN, 12));
    setLayout(new BorderLayout());

    buf = createImage(width, height);
    gBuf = buf.getGraphics();


    phase = 0.0;
    sundist = 1.0;
    moondist = 0.3;
    sunsize = 0.2;
    earthsize = 0.15;
    moonsize = 0.07;
    both = false;

    center = new Panel();
    center.setLayout(new CardLayout());
    add("Center", center);
    center.add("Top", canvas = new MoonCanvas(this));
    center.add("Earth", canvas2 = new MoonCanvas2(this));
    canvas2.update(canvas2.getGraphics());

    current = canvas;

    Panel bottom = new Panel();
    bottom.setLayout(new GridLayout(0,1));
    add("South", bottom);
    
    Panel panel = new Panel();
    panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    bottom.add(panel);

    panel.add(new Label("Moon Phase (0.0 to 1.0)", Label.RIGHT));

    fov = new TextField(String.valueOf(phase), 12);
    fov.setEditable(true);
    panel.add(fov);

    runbutton = new Button("Animate");
    panel.add(runbutton);

    panel = new Panel();
    panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    bottom.add(panel);

    panel.add(new Label("Point of View", Label.RIGHT));

    viewmenu = new Choice();
    viewmenu.addItem("Top View");
    viewmenu.addItem("Earth View");
    viewmenu.addItem("Both");
    viewmenu.select(0);
    panel.add(viewmenu);

    /*
    panel = new Panel();
    panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
    bottom.add(panel);

    panel.add(new Label("Animation Speed: Faster", Label.RIGHT));

    animslider = new Scrollbar(Scrollbar.HORIZONTAL, 0.0, -1.0, 1.0);
    panel.add(animslider);

    panel.add(new Label("Slower", Label.RIGHT));
    */
  }

  public void destroy() {
    gBuf.dispose();
  }

  String fixString(double d) {
    String t = String.valueOf(Math.round(d*100000.0)/100000.0);
    if (t.endsWith("0001")) {
      t = t.substring(0, t.length()-4);
    }
    while (t.endsWith("0"))
      t = t.substring(0, t.length()-1);
    return t;
  }

  /*public synchronized boolean handleEvent(Event event) {
    if (event.target == phaseThread && event.id == Event.ACTION_EVENT) {
      phase += 0.02;
      if (phase > 1.0)
  phase--;
      current.update(current.getGraphics());
      return true;
    } else if (event.target == fov && event.id == Event.ACTION_EVENT) {
      double a = new Double(fov.getText()).doubleValue();
      if (a < 0.0)
        a = 0.0;
      if (a > 1.0)
        a = 1.0;
      fov.setText(fixString(a));
      phase = a;
      current.update(current.getGraphics());
      return true;
    } else if (event.target == runbutton && event.id == Event.ACTION_EVENT) {
      if (phaseThread == null) {
        phaseThread = new PhaseThread(this);
        theThread = new Thread(phaseThread);
        theThread.setPriority(Thread.MIN_PRIORITY);
        phaseThread.setTime(30);
        theThread.start();
        runbutton.setLabel("Stop");
      } else {
        theThread.interrupt();
  phaseThread = null;
        runbutton.setLabel("Animate");
        fov.setText(fixString(phase));
      }
      return true;
    } else if (event.target == animslider && event.id == Event.ACTION_EVENT) {
      animspeed = 0.02 * Math.pow(10.0, animslider.getValue());
      return true;
    } else if (event.target == viewmenu) {
      Canvas thing;

      both = false;
      switch(viewmenu.getSelectedIndex()) {
        case 2:
          both = true;
        case 0:
          card.show(center, "Top");
          canvas.update(canvas.getGraphics());
          current = canvas;
          break;
        case 1:
          card.show(center, "Earth");
          canvas2.update(canvas2.getGraphics());
          current = canvas2;
          break;
      }
      return true;
    } else {
      return super.handleEvent(event);
    }
  }*/
}


class MoonCanvas extends Canvas {

  private Dimension d;
  private MoonPhase top;
  private int xmid, ymid;
  private double scale;

  public MoonCanvas(MoonPhase parent) {
    top = parent;

    scale = 0.5;

    setBackground(Color.black);
    setForeground(Color.white);
  }

  protected void paintSun(Graphics g) {
    int size = (int)(d.width*scale*top.sunsize/2);

    g.setColor(Color.yellow);
    g.fillOval(xmid - size - (int)(d.width*scale*top.sundist), 
  ymid - size, size*2, size*2);
  }

  protected void paintMoon(Graphics g) {
    int size = (int)(d.width*scale*top.moonsize/2);

    g.setColor(Color.darkGray);
    g.fillOval(xmid - size + (int)(d.width * scale * top.moondist * 
    Math.cos(top.phase*2.0*Math.PI)), 
  ymid - size - (int)(d.width * scale * top.moondist * 
    Math.sin(top.phase*2.0*Math.PI)), size*2, size*2);
    g.setColor(Color.white);
    g.fillArc(xmid - size + (int)(d.width * scale * top.moondist * 
    Math.cos(top.phase*2.0*Math.PI)), 
  ymid - size - (int)(d.width * scale * top.moondist * 
    Math.sin(top.phase*2.0*Math.PI)), size*2, size*2, 90, 180);
  }

  protected void paintEarth(Graphics g) {
    int size = (int)(d.width*scale*top.earthsize/2);

    g.setColor(Color.blue.darker());
    g.fillOval(xmid - size, ymid - size, size*2, size*2);
    g.setColor(Color.blue.brighter());
    g.fillArc(xmid - size, ymid - size, size*2, size*2, 90, 180);
  }

  protected void paintSky(Graphics g) {
    d = getSize();
    xmid = d.width/2;
    ymid = d.height/2;

    g.setColor(Color.black);
    g.fillRect(0, 0, d.width, d.height);

    if (top.both) {
      d.width /= 2;
      top.canvas2.paintSky(g, d.width, d.height, xmid + d.width/2, ymid);
      g.setColor(Color.black);
      g.fillRect(0, 0, d.width, d.height);
      g.setColor(Color.red);
      g.drawLine(d.width, 0, d.width, d.height);
      xmid = d.width/2;
    }

    xmid += d.width/5;

    paintSun(g);
    paintMoon(g);
    paintEarth(g);
  }

  public void paint(Graphics g) {
    paintSky(top.gBuf);
    g.drawImage(top.buf, 0, 0, this);
  }

  public void update(Graphics g) {
    // override this because the default implementation always
    // calls clearRect first, causing unwanted flicker
    paint(g);
  }

/* While the mouse is down, animate the moon */
  public boolean mouseDown(Event evt, int x, int y) {
    return true;
  }

  public boolean mouseUp(Event evt, int x, int y) {
    return true;
  }
}


class MoonCanvas2 extends Canvas {

  private Dimension d;
  private MoonPhase top;
  private int xmid, ymid;
  private double scale, extrasun;

  public MoonCanvas2(MoonPhase parent) {
    top = parent;
    scale = 0.2;
    extrasun = 1.05;

    setBackground(Color.black);
    setForeground(Color.white);
  }

  protected void paintSun(Graphics g) {
    int size = (int)(d.width*scale*extrasun/2);

    g.setColor(Color.yellow);
    g.fillOval(xmid - size - (int)(d.width * scale * 
  (top.phase-0.5) * 720), ymid - size, size*2, size*2);
  }

  protected void paintMoon(Graphics g) {
    int i, limit, sign;
    int size = (int)(d.width*scale/2);
    double p = top.phase;

    p = p < 0.5 ? p+0.5 : p-0.5;

    limit = size;
    sign = p < 0.5 ? 1 : -1;
//    sign = 1;

    g.setColor(Color.darkGray);
    g.fillOval(xmid - size, ymid - size, size*2, size*2);
    g.setColor(Color.white);
    for (i = -limit; i <= limit; i++)
      g.drawLine(xmid + sign * (int)(limit * 
    Math.sqrt(1.0-(double)(i*i)/((double)(limit*limit)))),
    ymid + i,
    xmid + sign * (int)(limit * 
      Math.cos(2.0*Math.PI*p) *
                    Math.sqrt(1.0-(double)(i*i)/((double)(limit*limit)))),
    ymid + i);

  }

  public void paintSky(Graphics g) {
    // pre-clear the bitmap or the applet
    // remove this if you paint the entire area anyway

    d = getSize();
    xmid = d.width/2;
    ymid = d.height/2;

    paintSky(g, d.width, d.height, xmid, ymid);
  }

  public void paintSky(Graphics g, int x, int y, int xm, int ym) {
    // pre-clear the bitmap or the applet
    // remove this if you paint the entire area anyway

    xmid = xm;
    ymid = ym;
    d.width = x;
    d.height = y;
    g.setColor(Color.black);
    g.fillRect(0, 0, x, y);

    paintSun(g);
    paintMoon(g);
  }

  public void paint(Graphics g) {
    paintSky(top.gBuf);
    g.drawImage(top.buf, 0, 0, this);
  }

  public void update(Graphics g) {
    // override this because the default implementation always
    // calls clearRect first, causing unwanted flicker
    paint(g);
  }
}

class PhaseThread implements Runnable {

  public PhaseThread(MoonPhase parent) {
    super();
  }

  public void setTime(int t) {
  }

  public void run() {
    /*while (true) {
      top.deliverEvent(new Event(this, Event.ACTION_EVENT, this));
      try {
        Thread.sleep(theTime);
      } catch (InterruptedException e){}
    }*/
  }
}
