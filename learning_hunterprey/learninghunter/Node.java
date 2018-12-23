package learning_hunterprey.learninghunter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
 *   This implements a simple Node that has information about the relative position of the closest
 *   prey with respect to the hunter in xdis and ydis. In addition, each Node gets one list of references
 *   to NodeProbStructs for each direction. Each struct contains a Node and a probability value. This is then
 *   used to determine the most favorable direction.
 */
public class Node {

	private List<NodeProbStruct> upNodeList;
	private List<NodeProbStruct> downNodeList;
	private List<NodeProbStruct> leftNodeList;
	private List<NodeProbStruct> rightNodeList;
	private int xdis;
	private int ydis;
	static double BIGGERDISVALUE = 0.0; // These three values give weights on the possible reactions of the
	static double SAMEDISVALUE = 1.0;   // prey to the move. (getting closer, staying the same or moving away)
	static double LOWERDISVALUE = 3.0;
	static double QUALITYTHRESHOLD = 0.7;
	
	
	public Node() {
		upNodeList = new ArrayList<NodeProbStruct>();
		downNodeList = new ArrayList<NodeProbStruct>();
		leftNodeList = new ArrayList<NodeProbStruct>();
		rightNodeList = new ArrayList<NodeProbStruct>();
	}
	
	public Node(int x, int y) {
		upNodeList = new ArrayList<NodeProbStruct>();
		downNodeList = new ArrayList<NodeProbStruct>();
		leftNodeList = new ArrayList<NodeProbStruct>();
		rightNodeList = new ArrayList<NodeProbStruct>();
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
	
	/*
	 * Here for each direction, the remembered outcomes are weighted and added, to determine a
	 * value for each move. The most favorable move is then decided.
	 * 
	 * The order of the entries in blocked is as follows: up, down, left, right
	 */
	public String getNextStep(boolean blocked[]) {
		double directionprobs[] = {0.0, 0.0, 0.0, 0.0};
		double normalizer[] = {0.0, 0.0, 0.0, 0.0};
		int distance = Math.abs(xdis) + Math.abs(ydis);
		// First, the outcomes for all moves are collected.
		if (!blocked[0] && upNodeList.size()!=0) {
			for (NodeProbStruct nodestruct : upNodeList) {
				if (Math.abs(nodestruct.node.getXdis()) + Math.abs(nodestruct.node.getYdis())>distance) {
					directionprobs[0] += BIGGERDISVALUE*((double) nodestruct.prob);
					normalizer[0] += (double) nodestruct.prob;
				} else if (Math.abs(nodestruct.node.getXdis()) + Math.abs(nodestruct.node.getYdis())==distance) {
					directionprobs[0] += SAMEDISVALUE*((double) nodestruct.prob);
					normalizer[0] += (double) nodestruct.prob;
				} else {
					directionprobs[0] += LOWERDISVALUE*((double) nodestruct.prob);
					normalizer[0] += (double) nodestruct.prob;
				}
			}
			directionprobs[0] /= normalizer[0];
		}
		if (!blocked[1] && downNodeList.size()!=0) {
			for (NodeProbStruct nodestruct : downNodeList) {
				if (Math.abs(nodestruct.node.getXdis()) + Math.abs(nodestruct.node.getYdis())>distance) {
					directionprobs[1] += BIGGERDISVALUE*((double) nodestruct.prob);
					normalizer[1] += (double) nodestruct.prob;
				} else if (Math.abs(nodestruct.node.getXdis()) + Math.abs(nodestruct.node.getYdis())==distance) {
					directionprobs[1] += SAMEDISVALUE*((double) nodestruct.prob);
					normalizer[1] += (double) nodestruct.prob;
				} else {
					directionprobs[1] += LOWERDISVALUE*((double) nodestruct.prob);
					normalizer[1] += (double) nodestruct.prob;
				}
			}
			directionprobs[1] /= normalizer[1];
		}
		if (!blocked[2] && leftNodeList.size()!=0) {
			for (NodeProbStruct nodestruct : leftNodeList) {
				if (Math.abs(nodestruct.node.getXdis()) + Math.abs(nodestruct.node.getYdis())>distance) {
					directionprobs[2] += BIGGERDISVALUE*((double) nodestruct.prob);
					normalizer[2] += (double) nodestruct.prob;
				} else if (Math.abs(nodestruct.node.getXdis())+Math.abs(nodestruct.node.getYdis())==distance) {
					directionprobs[2] += SAMEDISVALUE*((double) nodestruct.prob);
					normalizer[2] += (double) nodestruct.prob;
				} else {
					directionprobs[2] += LOWERDISVALUE*((double) nodestruct.prob);
					normalizer[2] += (double) nodestruct.prob;
				}
			}
			directionprobs[2] /= normalizer[2];
		}
		if (!blocked[3] && rightNodeList.size()!=0) {
			for (NodeProbStruct nodestruct : rightNodeList) {
				if (Math.abs(nodestruct.node.getXdis()) + Math.abs(nodestruct.node.getYdis())>distance) {
					directionprobs[3] += BIGGERDISVALUE*((double) nodestruct.prob);
					normalizer[3] += (double) nodestruct.prob;
				} else if (Math.abs(nodestruct.node.getXdis()) + Math.abs(nodestruct.node.getYdis())==distance) {
					directionprobs[3] += SAMEDISVALUE*((double) nodestruct.prob);
					normalizer[3] += (double) nodestruct.prob;
				} else {
					directionprobs[3] += LOWERDISVALUE*((double) nodestruct.prob);
					normalizer[3] += (double) nodestruct.prob;
				}
			}
			directionprobs[3] /= normalizer[3];
		}
		
		double maxprob=0;
		for (int i=0; i<4; i++) {
			if (directionprobs[i]>maxprob) maxprob = directionprobs[i];
		}
		
		int largestindex;
		Random rand = new Random();
		if (maxprob<QUALITYTHRESHOLD) {
			largestindex = rand.nextInt(4); // If the best option is still bad, it is replaced by random action, to learn anew.
		}else {
			largestindex = getIndexOfLargest(directionprobs);
		}

		switch (largestindex) {
		case 0: return "up";
		case 1: return "down";
		case 2: return "left";
		case 3: return "right";
		}
		
		return null;
	}
	
	/*
	 * If a move led from Node A to Node B, either B is added to the corresponding move-lists of A or its
	 * probability is increased (if B is already in the list).
	 */
	public void memorizeNextNode(Node nN, String direction) {
		boolean existed = false;
		if (direction=="up") {
			for (NodeProbStruct nodestruct : upNodeList) {
				if (nN==nodestruct.node) {
					nodestruct.prob++;
					existed = true;
					break;
				}
			}
			if (!existed) {
				upNodeList.add(new NodeProbStruct(nN, 1));
			}
		} else if (direction=="down") {
			for (NodeProbStruct nodestruct : downNodeList) {
				if (nN==nodestruct.node) {
					nodestruct.prob++;
					existed = true;
					break;
				}
			}
			if (!existed) {
				downNodeList.add(new NodeProbStruct(nN, 1));
			}
		} else if (direction=="left") {
			for (NodeProbStruct nodestruct : leftNodeList) {
				if (nN==nodestruct.node) {
					nodestruct.prob++;
					existed = true;
					break;
				}
			}
			if (!existed) {
				leftNodeList.add(new NodeProbStruct(nN, 1));
			}
		} else if (direction=="right"){
			for (NodeProbStruct nodestruct : rightNodeList) {
				if (nN==nodestruct.node) {
					nodestruct.prob++;
					existed = true;
					break;
				}
			}
			if (!existed) {
				rightNodeList.add(new NodeProbStruct(nN, 1));
			}
		}
	}
	
	/*
	 * Helper function to get the index of the largest element in an array, while keeping the output evenly
	 * distributed among identically large elements.
	 */
	public int getIndexOfLargest(double[] array )
	{
		int numidentical = 0;
		int largest = 0;
		for ( int i = 1; i < array.length; i++ ) {
			if ( array[i] > array[largest] ) largest = i;
		}
		for ( int i = 0; i < array.length; i++ ) {
			if (array[i]==array[largest]) numidentical++;
		}
		if (numidentical>1) {
			int indexidenticals[] = new int[numidentical];
			Random rand = new Random();
			int randind=rand.nextInt(numidentical);

			int j=0;
			for ( int i = 0; i < array.length; i++ ) {
				if (array[i]==array[largest]) {
					indexidenticals[j] = i;
					j++;
				}
			}
		
		return indexidenticals[randind];
		}else {
			return largest;
		}
		
	}
}
