/*
 * OperatorFigure.java
 *
 * Created on May 19, 2007, 3:16 PM
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Copyright (C) 2009 Yan Cheng Cheok <yccheok@yahoo.com>
 */

package org.yccheok.jstock.gui.analysis;

import org.yccheok.jstock.gui.*;
import java.io.IOException;
import java.awt.geom.*;
import static org.jhotdraw.draw.AttributeKeys.*;
import java.util.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.geom.*;
import org.jhotdraw.util.*;
import org.jhotdraw.xml.*;
import org.yccheok.jstock.analysis.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author yccheok
 */
public abstract class OperatorFigure extends GraphicalCompositeFigure implements java.beans.PropertyChangeListener {
    
    private LinkedList<AbstractConnector> connectors;
    private Operator operator;
    
    /** Creates a new instance. */
    protected OperatorFigure(Operator operator) {        
        super(new RectangleFigure());
        
        this.operator = operator;
        // Register listener.
        ((AbstractOperator)this.operator).addPropertyChangeListener(this);
        
        setLayouter(new VerticalLayouter());
        
        RectangleFigure nameCompartmentPF = new RectangleFigure();
        STROKE_COLOR.set(nameCompartmentPF, null);
        nameCompartmentPF.setAttributeEnabled(STROKE_COLOR, false);
        FILL_COLOR.set(nameCompartmentPF, null);
        nameCompartmentPF.setAttributeEnabled(FILL_COLOR, false);
        ListFigure nameCompartment = new ListFigure(nameCompartmentPF);
        ListFigure attributeCompartment = new ListFigure();
        SeparatorLineFigure separator1 = new SeparatorLineFigure();
        
        applyAttributes(getPresentationFigure());
        
        add(nameCompartment);
        add(separator1);
        add(attributeCompartment);
        
        Insets2D.Double insets = new Insets2D.Double(4,8,4,8);
        LAYOUT_INSETS.set(nameCompartment, insets);
        LAYOUT_INSETS.set(attributeCompartment, insets);
        
        TextFigure nameFigure;
        nameCompartment.add(nameFigure = new TextFigure());
        FONT_BOLD.set(nameFigure, true);
        nameFigure.setEditable(false);        
        nameFigure.setAttributeEnabled(FONT_BOLD, false);
        
        TextFigure durationFigure;
        attributeCompartment.add(durationFigure = new TextFigure());
        FONT_BOLD.set(durationFigure, true);
        durationFigure.setEditable(false);        
        durationFigure.setText("0");
        durationFigure.setAttributeEnabled(FONT_BOLD, false);
        
        TextFigure startTimeFigure;
        attributeCompartment.add(startTimeFigure = new TextFigure());
        startTimeFigure.setEditable(false);
        startTimeFigure.setText("0");
        startTimeFigure.setAttributeEnabled(FONT_BOLD, false);
        
        applyAttributes(this);
        setAttributeEnabled(STROKE_DASHES, false);
        
        ResourceBundleUtil labels =
                ResourceBundleUtil.getBundle("org.yccheok.jstock.data.Labels");
        
        setName(labels.getString("operatorDefaultName"));
        
        createConnectors();
    }
    
    private void createConnectors() {
        connectors = new LinkedList<AbstractConnector>();
        
        final Operator _operator = this.getOperator();
        
        final int numOfInputConnector = _operator.getNumOfInputConnector();
        final int numOfOutputConnector = _operator.getNumOfOutputConnector();
        final double inputStep = 1.0 / (double)(numOfInputConnector << 1);
        final double outputStep = 1.0 / (double)(numOfOutputConnector << 1);

        // Create input connectors first.
        for(int i = 0; i < numOfInputConnector; i++) {
            connectors.add(new InputConnector(this, new RelativeLocator(0, inputStep + inputStep * (double)i * 2.0), i));
        }

        // Follow by output connectors.
        for(int i = 0; i < numOfOutputConnector; i++) {
            connectors.add(new OutputConnector(this, new RelativeLocator(1, outputStep + outputStep * (double)i * 2.0), i));
        }        
    }
    
    @Override
    public Collection<Handle> createHandles(int detailLevel) {
        java.util.List<Handle> handles = new LinkedList<Handle>();
        if (detailLevel == 0) {
            handles.add(new MoveHandle(this, RelativeLocator.northWest()));
            handles.add(new MoveHandle(this, RelativeLocator.northEast()));
            handles.add(new MoveHandle(this, RelativeLocator.southWest()));
            handles.add(new MoveHandle(this, RelativeLocator.southEast()));
            
            final Operator _operator = this.getOperator();

            final int numOfInputConnector = _operator.getNumOfInputConnector();
            final int numOfOutputConnector = _operator.getNumOfOutputConnector();

            assert((numOfInputConnector + numOfOutputConnector) == connectors.size());
            // The connectors iteration sequence, is highly dependent on implementation
            // of createConnectors method.
            
            int connectorIndex = 0;
            for (int index = 0; index < numOfInputConnector; index++, connectorIndex++) {
                final Class type = _operator.getInputClass(index);
                final org.jhotdraw.draw.Connector c = connectors.get(connectorIndex);
                ConnectorHandle connectorHandle = new ConnectorHandle(c, new DependencyFigure());
                connectorHandle.setToolTipText(type.getSimpleName());
                handles.add(connectorHandle);
            }
            for (int index = 0; index < numOfOutputConnector; index++, connectorIndex++) {
                final Class type = _operator.getOutputClass(index);
                final org.jhotdraw.draw.Connector c = connectors.get(connectorIndex);
                ConnectorHandle connectorHandle = new ConnectorHandle(c, new DependencyFigure());
                connectorHandle.setToolTipText(type.getSimpleName());
                handles.add(connectorHandle);
            }
        }
        return handles;
    }
    
    public void setName(String newValue) {
        getNameFigure().willChange();
        getNameFigure().setText(newValue);
        getNameFigure().changed();
    }
    public String getName() {
        return getNameFigure().getText();
    }
    public void setAttribute(String attribute) {
        getAttributeFigure().willChange();
        getAttributeFigure().setText(attribute);
        getAttributeFigure().changed();
    }
    public String getAttribute() {
        return getAttributeFigure().getText();
    }
    public void setValue(String value) {
        getValueFigure().willChange();
        getValueFigure().setText(value);
        getValueFigure().changed();
    }
    public String getValue() {
        return getValueFigure().getText();
    }
    
    @Override public org.jhotdraw.draw.Connector findConnector(Point2D.Double p, ConnectionFigure figure) {
        // return closest connector
        double min = Double.MAX_VALUE;
        org.jhotdraw.draw.Connector closest = null;
        for (org.jhotdraw.draw.Connector c : connectors) {
            Point2D.Double p2 = Geom.center(c.getBounds());
            double d = Geom.length2(p.x, p.y, p2.x, p2.y);
            if (d < min) {
                min = d;
                closest = c;
            }
        }
        return closest;
    }
    
    @Override public org.jhotdraw.draw.Connector findCompatibleConnector(org.jhotdraw.draw.Connector c, boolean isStart) {
        return connectors.getFirst();
    }
    
    private TextFigure getNameFigure() {
        return (TextFigure) ((ListFigure) getChild(0)).getChild(0);
    }
    private TextFigure getAttributeFigure() {
        return (TextFigure) ((ListFigure) getChild(2)).getChild(0);
    }
    private TextFigure getValueFigure() {
        return (TextFigure) ((ListFigure) getChild(2)).getChild(1);
    }

    @SuppressWarnings("unchecked")
    private void applyAttributes(Figure f) {
        Map<AttributeKey,Object> attr = ((AbstractAttributedFigure) getPresentationFigure()).getAttributes();
        for (Map.Entry<AttributeKey, Object> entry : attr.entrySet()) {
            f.setAttribute(entry.getKey(), entry.getValue());
        }
    }
    
    @Override
    public OperatorFigure clone() {
        throw new java.lang.UnsupportedOperationException();
    }
    
    @Override
    public void read(DOMInput in) throws IOException {
        double x = in.getAttribute("x", 0d);
        double y = in.getAttribute("y", 0d);
        double w = in.getAttribute("w", 0d);
        double h = in.getAttribute("h", 0d);
        setBounds(new Point2D.Double(x,y), new Point2D.Double(x+w,y+h));
        readAttributes(in);
        in.openElement("model");
        in.openElement("name");
        setName((String) in.readObject());
        in.closeElement();
        in.openElement("attribute");
        setAttribute((String) in.readObject());
        in.closeElement();
        in.openElement("value");
        setValue((String) in.readObject());
        in.closeElement();

        in.openElement("connectors");
        connectors.clear();
        final int size = in.getElementCount();
        
        for(int i = 0; i < size; i++) {
            final AbstractConnector c = (AbstractConnector)in.readObject(i);
            connectors.add(c);
        }
        in.closeElement(); 
        
        in.closeElement();
    }
    
    @Override
    public void write(DOMOutput out) throws IOException {
        Rectangle2D.Double r = getBounds();
        out.addAttribute("x", r.x);
        out.addAttribute("y", r.y);
        writeAttributes(out);
        out.openElement("model");
        out.openElement("name");
        out.writeObject(getName());
        out.closeElement();
        out.openElement("attribute");
        out.writeObject(getAttribute());
        out.closeElement();
        out.openElement("value");
        out.writeObject(getValue());
        out.closeElement(); 

        out.openElement("connectors");
        for(AbstractConnector child : connectors)
            out.writeObject(child);
        out.closeElement(); 
                
        out.closeElement();
    }
    
    public int getLayer() {
        return 0;
    }
    
    public String toString() {
        return "OperatorFigure#"+hashCode()+" "+getName()+" "+getAttribute()+" "+getValue();
    }
    
    public void addNotify(Drawing d)
    {
        super.addNotify(d);
        System.out.println(this + " addNotify");
    }
    
    public void removeNotify(Drawing d)
    {
        super.removeNotify(d);
        System.out.println(this + " removeNotify");
    }
    
    public Operator getOperator()
    {
        return operator;
    }
    
    public void setOperator(Operator operator)
    {
        this.operator = operator;
    }
    
    public void propertyChange(java.beans.PropertyChangeEvent propertyChangeEvent)
    {        
        if(propertyChangeEvent.getPropertyName().equals("value")) {
            this.setValue("" + propertyChangeEvent.getNewValue());
        }
        else if(propertyChangeEvent.getPropertyName().equals("attribute")) {
            this.setAttribute("" + propertyChangeEvent.getNewValue());
        }
        else {
            log.error("Unknown property change : " + propertyChangeEvent.getPropertyName());
        }
    }  
    
    private static final Log log = LogFactory.getLog(OperatorFigure.class);
}
