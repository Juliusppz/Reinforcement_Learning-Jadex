package learning_garbagecollector;

/**
 *    This source code file is part of an extension of the Jadex examples 
 *    GarbageCollector and HunterPrey. In each example one of the agents 
 *    is endowed with learning capabilities, resulting in improving 
 *    behavior during a single simulation run. Copyright (C) 2018, 
 *    Julius Zauleck.
 *    
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *   
 *  
 *  
 *   This implements a simple Node that has information about the relative position of the closest
 *   garbage with respect to the GarbageCollector in xdis and ydis. In addition, each Node (except
 *   for the initial Node) gets a reference to a Node which represents the respective next step.
 */
public class Node {
	
	private Node nextNode;
	private int xdis;
	private int ydis;
	
	public Node() {
	}
	
	public Node(Node nN, int x, int y) {
		nextNode=nN;
		xdis=x;
		ydis=y;
	}
	
	public int getXdis() {
		return xdis;
	}
	
	public int getYdis() {
		return ydis;
	}
	
	public void setXdis(int x) {
		xdis=x;
	}
	
	public void setYdis(int y) {
		ydis=y;
	}
	
	public Node getNextNode() {
		return nextNode;
	}
	
	public void setNextNode(Node nN) {
		nextNode=nN;
	}
	
}
