#    This reinforcement learning implementation shows a way to solve the cartpole 
#    environment of OpenAI gym.
#    Copyright (C) 2018 Julius Zauleck
#
#    This program is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version. This copyright notice may not be
#    removed.
#
#    This program is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with this program. If not, see <https://www.gnu.org/licenses/>.

package learning_garbagecollector;

import java.util.ArrayList;
import java.util.List;

import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Int;

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
 *   (at your option) any later version. This copyright notice may not be
 *   removed or changed.
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
 *   This class manages the Nodes in the LearningGraph. It has a list of Nodes (nodeList) to search for 
 *   Nodes with certain criteria. It adds a Node to the Graph, whenever the current scenario
 *   leads to a known scenario within a single step. The new Node gets a reference to the Node
 *   that represents the memory of the current scenario in addition to the information identifying
 *   the scenario.
 */
public class LearningGraph {
	
	private static List<Node> nodeList;
	private static LearningGraph instance = null;
	
	protected LearningGraph() {}

	/**
	 *  This initializer most notably creates an initial Node that represents the scenario of
	 *  the GarbageCollector and the closest garbage being in the same place. To this all new Nodes
	 *  are linked directly or indirectly.
	 */
	public static LearningGraph initialize() {
		if (instance == null) {
			instance = new LearningGraph();
			Node firstNode = new Node();
			firstNode.setXdis(0);
			firstNode.setYdis(0);
			nodeList = new ArrayList<Node>();
			nodeList.add(firstNode);
		}
		return instance;
	}

	/**
	 *  To find the new position, the current distance vector between GarbageCollector and closest
	 *  Garbage is searched within the Nodes. If it is found, the next step is calculated by comparing
	 *  the distance of the current Node and the one it references to.
	 */
	public static IVector2 getNextPos(IVector2 myPos, IVector2 garbagePos){
		int xdis = garbagePos.getXAsInteger() - myPos.getXAsInteger();
		int ydis = garbagePos.getYAsInteger() - myPos.getYAsInteger();
		if (!(nodeList==null)) {
			for (Node node : nodeList) {
				if (node.getXdis()==xdis && node.getYdis()==ydis) {
					int xdisnew = node.getNextNode().getXdis();
					int ydisnew = node.getNextNode().getYdis();
					myPos = new Vector2Int(myPos.getXAsInteger()+xdis-xdisnew,myPos.getYAsInteger()+ydis-ydisnew);
					return myPos;
				}
			}
		}
		return null;
	}

	/**
	 *  This adds a new Node with knowledge of the current and the next position. After identifying
	 *  the Node corresponding to the next position, a Node is created with the information of the
	 *  current relative position and linked to the Node of the next position.
	 */
	public static void addNewDistance(IVector2 myPosOld, IVector2 myPosNew, IVector2 garbagePos) {
		int xdis = garbagePos.getXAsInteger() - myPosNew.getXAsInteger();
		int ydis = garbagePos.getYAsInteger() - myPosNew.getYAsInteger();
		int xdisold = garbagePos.getXAsInteger() - myPosOld.getXAsInteger();
		int ydisold = garbagePos.getYAsInteger() - myPosOld.getYAsInteger();
		if (nodeList!=null) {
			for (Node node : nodeList) {
				if (node.getXdis()==xdis && node.getYdis()==ydis) {
					Node newNode = new Node(node,xdisold,ydisold); 
					nodeList.add(newNode);
					return;
				}
			}
		}
	}
	
	/**
	 *  If a scenario needs to be deleted, the Node corresponding to the scenario is identified.
	 *  From there all the directly and indirectly referencing Nodes are identified and added to 
	 *  deleteList. Afterwards all the Nodes in deleteList are deleted and removed from the nodeList.
	 */
	public static void deleteDistances(IVector2 myPos, IVector2 garbagePos) {
		int xdis = garbagePos.getXAsInteger() - myPos.getXAsInteger();
		int ydis = garbagePos.getYAsInteger() - myPos.getYAsInteger();
		if (nodeList!=null) {
			List<Node> deleteList = new ArrayList<Node>();
			for (Node node : nodeList) {
				if (node.getXdis()==xdis && node.getYdis()==ydis) {
					addAllReferencingNodes(deleteList,node);
				}
			}
			for (Node node : deleteList ) {
				nodeList.remove(node);
				node = null;
			}
		}
		
	}
	
	/**
	 *  This recursive method gathers all the Nodes that directly and indirectly reference
	 *  a certain Node.
	 */
	private static void addAllReferencingNodes(List<Node> deleteList, Node node) {
		deleteList.add(node);
		for (Node deleteNode : nodeList) {
			if (deleteNode.getNextNode()==node) {
				addAllReferencingNodes(deleteList, deleteNode);
			}
		}
	}

	/**
	 *  Checks whether memory of a certain scenario exists.
	 */
	public static boolean hasMemoryOf(IVector2 myPos, IVector2 garbagePos) {
		int xdis = garbagePos.getXAsInteger() - myPos.getXAsInteger();
		int ydis = garbagePos.getYAsInteger() - myPos.getYAsInteger();
		if (nodeList!=null) {
			for (Node node : nodeList) {
				if (node.getXdis()==xdis && node.getYdis()==ydis) {
					return true;
				}
			}
		}
		return false;
	}
	
}
