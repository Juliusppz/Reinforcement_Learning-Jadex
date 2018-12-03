package learning_hunterprey.learninghunter;

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
 *   This class manages the Nodes in the LearningGraph. It has a list of Nodes (nodeList) to search for 
 *   Nodes with certain criteria. It adds a Node to the Graph, whenever the current scenario
 *   is unknown. Each Node gets references to other nodes if the previous move led to them.
 */
public class LearningGraph {
	
	private static Node lastNode;
	private static Node failNode;
	private static String lastDirection;
	private static List<Node> nodeList;
	private static LearningGraph instance = null;
	private static int hunted=0;
	//private static int lost=0;
	//private static boolean hadprey=false;
	
	protected LearningGraph() {}

	/**
	 *  The memory graph is initialized with a Node representing the reached target and a Node (failNode) that
	 *  represents all instances of a prey being far away.
	 */
	public static LearningGraph initialize() {
		if (instance == null) {
			instance = new LearningGraph();
			Node firstNode = new Node(0, 0);
			nodeList = new ArrayList<Node>();
			nodeList.add(firstNode);
			failNode = new Node(8, 8);
		}
		return instance;
	}

	/**
	 *  If no prey is near, the last move either was bad and is remembered as such or was not relevant.
	 *  In either case, the last direction and the last activated Node have to be adjusted.
	 */
	public static void noPrey() {
//		if ((lastNode!=null && lastNode!=failNode) || hadprey) {
//			lost++;
//			hunted++;
//			hadprey=false;
//			System.out.println(hunted+ " " + lost);
//		}
		
		if (lastNode!=null && lastDirection!=null) {
			lastNode.memorizeNextNode(failNode, lastDirection);
		}
		lastNode = failNode;
		lastDirection = null;
	}
	
	/**
	 *  To find the new position, the current distance vector between hunter and closest
	 *  prey is searched within the Nodes. If it is found, the next step is calculated by comparing 
	 *  the Nodes referenced by it.
	 */
	public static IVector2 getNextPos(IVector2 myPos, IVector2 preyPos, boolean blocked[]){
		int xdis = preyPos.getXAsInteger() - myPos.getXAsInteger();
		int ydis = preyPos.getYAsInteger() - myPos.getYAsInteger();
		if (!(nodeList==null)) {
			for (Node node : nodeList) {
				if (node.getXdis()==xdis && node.getYdis()==ydis) {
					lastNode=node;
					String direction = node.getNextStep(blocked);
					if (direction=="up") {
						lastDirection = "up";
						myPos = new Vector2Int(myPos.getXAsInteger(), myPos.getYAsInteger()-1);
						return myPos;
					} else if (direction=="down") {
						lastDirection = "down";
						myPos = new Vector2Int(myPos.getXAsInteger(), myPos.getYAsInteger()+1);
						return myPos;
					} else if (direction=="left") {
						lastDirection = "left";
						myPos = new Vector2Int(myPos.getXAsInteger()-1, myPos.getYAsInteger());
						return myPos;
					} else if (direction=="right") {
						lastDirection = "right";
						myPos = new Vector2Int(myPos.getXAsInteger()+1, myPos.getYAsInteger());
						return myPos;
					}
				}
			}
		}
		return null;
	}

	/**
	 *  This gives the last Node a reference to the current Node, based on the last direction. If
	 *  there is no Node corresponding to the current scenario, a new one is created and referenced.
	 */
	public static void addNewDistance(IVector2 myPos, IVector2 preyPos) {
//		hunted++;
//		hadprey=true;
//		System.out.println(hunted+ " " + lost);
		int xdis = preyPos.getXAsInteger() - myPos.getXAsInteger();
		int ydis = preyPos.getYAsInteger() - myPos.getYAsInteger();	
		if (nodeList!=null && lastNode!=null && lastDirection!=null) {
			for (Node node : nodeList) {
				if (node.getXdis()==xdis && node.getYdis()==ydis) {
					lastNode.memorizeNextNode(node, lastDirection);
					return;
				}
			}
			Node newNode = new Node(xdis, ydis);
			lastNode.memorizeNextNode(newNode, lastDirection);
			nodeList.add(newNode);
		}
	}

	public static void setLastDirection(String newdir) {
		lastDirection = newdir;
	}
	
}