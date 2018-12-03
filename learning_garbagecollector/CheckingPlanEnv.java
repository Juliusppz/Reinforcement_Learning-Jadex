package learning_garbagecollector;

import java.util.Random;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import learning_garbagecollector.GarbageCollectorBDI.Go;
import jadex.bdiv3.runtime.IPlan;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
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
 *   This is the main implementation of the self-learning search behavior. It uses the distance
 *   between the GarbageCollector and the closest garbage to check if this scenario has already
 *   been learned. If so, it uses the memory it has to take the learned next step. If not, it 
 *   moves randomly and if it makes progress to a known scenario, it memorizes the current scenario.
 */
@Plan
public class CheckingPlanEnv
{
	//-------- attributes --------

	@PlanCapability
	protected GarbageCollectorBDI collector;
	
	@PlanAPI
	protected IPlan rplan;
	
	
	public CheckingPlanEnv() {
		LearningGraph.initialize();
	}
	/**
	 *  The plan body.
	 *  
	 */
	@PlanBody
	public void body()
	{
		Space2D env = collector.getEnvironment();
		IVector2 size = env.getAreaSize();
		IVector2 mypos = collector.getPosition();
		IVector2 newpos = computeNextPosition(mypos, size, env);

		Go go = collector.new Go(newpos);
		rplan.dispatchSubgoal(go).get();
	}

	protected static IVector2 computeNextPosition(IVector2 pos, IVector2 size, Space2D env)
	{
		int sizex=size.getXAsInteger();
		int sizey=size.getYAsInteger();

		//get the closest piece of garbage
		ISpaceObject nearestGarbage= env.getNearestObject(pos, size.getLength().add(size.getLength()), "garbage");
		IVector2 garbagePos = (IVector2)nearestGarbage.getProperty(Space2D.PROPERTY_POSITION);

		IVector2 posNew = pos.copy();
		/**
		 *	Check whether there is a memory of the relative garbage position. If this is the case,
		 *	use the memory to get the new position or forget the memory if it would move the
		 *	GarbageCollector outside of the grid.
		 */
		if (LearningGraph.hasMemoryOf(pos, garbagePos)) {
			pos =LearningGraph.getNextPos(pos, garbagePos);
			if(pos.getXAsInteger()<0 || pos.getXAsInteger()>=sizex || pos.getYAsInteger()<0 || pos.getYAsInteger()>=sizey) {
				LearningGraph.deleteDistances(pos, garbagePos);
				System.out.println("Forgot something stupid!");
			}else{
				return pos;
			}
		}
		
		Random rand = new Random();
		int  randDirection;
		boolean boundaryHit=true;
		// Create a step in a random direction, as long as it does not leave the grid.
		while (boundaryHit) {
			randDirection= rand.nextInt(4);
			switch (randDirection) {
			case 0: 
				if (posNew.getXAsInteger()==sizex-1) { break;}
				posNew = new Vector2Int(posNew.getXAsInteger()+1, posNew.getYAsInteger());
				boundaryHit=false;
				break;
			case 1:
				if (posNew.getXAsInteger()==0) { break;}
				posNew = new Vector2Int(posNew.getXAsInteger()-1, posNew.getYAsInteger());
				boundaryHit=false;
				break;
			case 2:
				if (posNew.getYAsInteger()==sizey-1) { break;}
				posNew = new Vector2Int(posNew.getXAsInteger(), (posNew.getYAsInteger()+1)%sizey);
				boundaryHit=false;
				break;
			case 3:
				if (posNew.getYAsInteger()==0) { break;}
				posNew = new Vector2Int(posNew.getXAsInteger(), (posNew.getYAsInteger()-1)%sizey);
				boundaryHit=false;
				break;
			}
		}
		// If there is a memory of the next relative position, memorize the current one.
		if (LearningGraph.hasMemoryOf(posNew, garbagePos)) {
			LearningGraph.addNewDistance(pos, posNew, garbagePos);	
			System.out.println("Learnt something new!");
			
		}
		return posNew;
	}

}
