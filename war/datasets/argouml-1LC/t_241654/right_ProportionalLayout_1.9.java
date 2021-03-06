// $Id: ProportionalLayout.java,v 1.9 2004-10-28 14:18:19 mkl Exp $
// Copyright (c) 2003 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

/*
 * ProportionalLayout.java
 */
package org.argouml.swingext;

import java.awt.*;
import java.util.*;

/**
 * Allows components to be a set as a proportion to their container or
 * left as fixed size.  Components are resized accordingly when the
 * parent is resized.
 *
 * @author Bob Tarling
 */

public class ProportionalLayout extends LineLayout {

    private Hashtable componentTable;

    /**
     * The constructor.
     */
    public ProportionalLayout() {
        this(HORIZONTAL);
    }

    /**
     * The constructor. 
     * 
     * @param orientation the orientation
     */
    public ProportionalLayout(Orientation orientation) {
        super(orientation);
        componentTable = new Hashtable();
    }

    /**
     * @see java.awt.LayoutManager2#addLayoutComponent(java.awt.Component, 
     * java.lang.Object)
     */
    public final void addLayoutComponent(Component comp, Object constraints) {
        if (constraints == null) constraints = "";
        addLayoutComponent((String) constraints, comp);
    }

    /**
     * @see java.awt.LayoutManager#addLayoutComponent(java.lang.String, 
     * java.awt.Component)
     */
    public void addLayoutComponent(String name, Component comp) {
        try {
	    componentTable.put(comp, name);
        }
        catch (Exception e) {
	    componentTable.put(comp, "");
        }
    }

    /**
     * @see java.awt.LayoutManager#removeLayoutComponent(java.awt.Component)
     */
    public void removeLayoutComponent(Component comp) {
        componentTable.remove(comp);
    }

    /**
     * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
     */
    public void layoutContainer(Container parent) {
        // Find the total proportional size of all visible components
        double totalProportionalLength = 0;
        int totalLength;

        totalLength = getMyOrientation().getLengthMinusInsets(parent);

        Enumeration enumKeys = componentTable.keys();
        while (enumKeys.hasMoreElements()) {
            Component comp = (Component) enumKeys.nextElement();
            if (comp.isVisible()) {
                String size = (String) (componentTable.get(comp));
                if (size.length() != 0) {
                    totalProportionalLength += Double.parseDouble(size);
                }
                else {
                    totalLength -= getMyOrientation().getLength(comp);
                }
            }
        }

        Insets insets = parent.getInsets();
        Point loc = new Point(insets.top, insets.left);
        int length = 0;
        int nComps = parent.getComponentCount();
        for (int i = 0; i < nComps; i++) {
            Component comp = parent.getComponent(i);
            if (comp.isVisible()) {
                String proportionalLength = (String) (componentTable.get(comp));
                if (proportionalLength.length() != 0) {
                    length =
			(int)
			((totalLength * Double.parseDouble(proportionalLength))
			 / totalProportionalLength);
                    if (length < 0) length = 0;
                }
                else {
                    length = getMyOrientation().getLength(comp);
                }
                comp.setSize(getMyOrientation().setLength(parent.getSize(), 
                        length));
                comp.setLocation(loc);
                loc = getMyOrientation().addToPosition(loc, length);
            }
        }
    }

    /**
     * @return Returns the componentTable.
     */
    protected Hashtable getComponentTable() {
        return componentTable;
    }
}
