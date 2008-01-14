/*
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
 * Copyright (C) 2008 Cheok YanCheng <yccheok@yahoo.com>
 */

package org.yccheok.jstock.gui.treetable;

/**
 *
 * @author Owner
 */
public abstract class AbstractTreeTableable implements TreeTableable {
    public TreeTableable[] getPath()
    {
        return getPathToRoot(this, 0);
    }
    
    private TreeTableable[] getPathToRoot(TreeTableable aNode, int depth) {
        TreeTableable[] retNodes;

        if(aNode == null) {
            if(depth == 0)
                return null;
            else
                retNodes = new TreeTableable[depth];
        }   
        else {
            depth++;
            retNodes = getPathToRoot(aNode.getParent(), depth);
            retNodes[retNodes.length - depth] = aNode;
        }   
        
        return retNodes;
    }    
}
