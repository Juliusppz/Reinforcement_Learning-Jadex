package learning_hunterprey.learninghunter;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import learning_hunterprey.MoveAction;
import jadex.bdiv3x.runtime.Plan;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Grid2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Int;
import learning_hunterprey.learninghunter.LearningGraph;

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
 *   This is the main implementation of the self-learning hunting behavior. It uses the distance
 *   between the hunter and the closest prey to check if this scenario has already
 *   been learned. If so, it uses the memory it has to take the next step it remembers to be most successful.
 *   If it does not remember or has no good option, it moves randomly. For all consecutive steps near the prey
 *   it is learned which scenario and moves leads to which scenario. If scenarios do not exist, they are added.
 */
public class LearningHunterPlan extends Plan
{
	public LearningHunterPlan() {
		LearningGraph.initialize();
	}

	/**
	 *  Plan body.
	 */
	public void body()
	{
		Grid2D	env	= (Grid2D)getBeliefbase().getBelief("env").getFact();
		ISpaceObject	myself	= (ISpaceObject)getBeliefbase().getBelief("myself").getFact();
		String	lastdir	= null;

		while(true)
		{
			// Get current position.
			IVector2	pos	= (IVector2)myself.getProperty(Space2D.PROPERTY_POSITION);

			ISpaceObject	prey	= (ISpaceObject)getBeliefbase().getBelief("nearest_prey").getFact();
			if(prey!=null && pos.equals(prey.getProperty(Space2D.PROPERTY_POSITION)))
			{
				// Perform eat action.
				try
				{
					LearningGraph.addNewDistance(pos, (IVector2) prey.getProperty(Space2D.PROPERTY_POSITION));
					Map<String, Object> params = new HashMap<String, Object>();
					params.put(ISpaceAction.ACTOR_ID, getComponentDescription());
					params.put(ISpaceAction.OBJECT_ID, prey);
					Future<Void> fut = new Future<Void>();
					env.performSpaceAction("eat", params, new DelegationResultListener<Void>(fut));
					fut.get();
				}
				catch(RuntimeException e)
				{
				}
			}

			else
			{
				// Move towards the prey, if any
				if(prey!=null)
				{
					IVector2 preypostmp = (IVector2) prey.getProperty(Space2D.PROPERTY_POSITION);
					IVector2 size = env.getAreaSize();
					int sizex = size.getXAsInteger();
					int sizey = size.getYAsInteger();

					// This deals with the periodic boundary conditions, by virtually placing the prey outside the box
					if (preypostmp.getXAsInteger() - pos.getXAsInteger()>5) {
						preypostmp = new Vector2Int(preypostmp.getXAsInteger() - sizex, preypostmp.getYAsInteger());
					} else if (preypostmp.getXAsInteger() - pos.getXAsInteger()<-5) {
						preypostmp = new Vector2Int(preypostmp.getXAsInteger() + sizex, preypostmp.getYAsInteger());
					}
					if (preypostmp.getYAsInteger() - pos.getYAsInteger()>5) {
						preypostmp = new Vector2Int(preypostmp.getXAsInteger(), preypostmp.getYAsInteger() - sizey);
					} else if (preypostmp.getYAsInteger() - pos.getYAsInteger()<-5) {
						preypostmp = new Vector2Int(preypostmp.getXAsInteger(), preypostmp.getYAsInteger() + sizey);
					}
					IVector2 nextPos=null;
					
					//If the prey is too far away, do not remember the scenarios.
					if(Math.abs(preypostmp.getXAsInteger() - pos.getXAsInteger())>3 || Math.abs(preypostmp.getYAsInteger() - pos.getYAsInteger())>3) {
						LearningGraph.noPrey();
					}else {
						LearningGraph.addNewDistance(pos, preypostmp);

						String posDirs[] = MoveAction.getPossibleDirections(env, pos);
						boolean blockedDirBool[] = {true, true, true, true};

						for ( int i = 0; i < posDirs.length; i++ )
						{
							if (posDirs[i]=="up") {
								blockedDirBool[0] = false;
							} else if (posDirs[i]=="down") {
								blockedDirBool[1] = false;
							} else if (posDirs[i]=="left") {
								blockedDirBool[2] = false;
							} else if (posDirs[i]=="right") {
								blockedDirBool[3] = false;
							}
						}
						nextPos = LearningGraph.getNextPos(pos, preypostmp, blockedDirBool);
					}
					
					// If there is no good remembered option, move randomly.
					String newdir = null;
					if (nextPos==null) {
						Random rand = new Random();
						int largestindex=rand.nextInt(4);
						switch (largestindex) {
						case 0: newdir = "up";
						case 1: newdir = "down";
						case 2: newdir = "left";
						case 3: newdir = "right";
						}
						LearningGraph.setLastDirection(newdir);
					} else {
						newdir = MoveAction.getDirection(env, pos, nextPos);
					}

					if(!MoveAction.DIRECTION_NONE.equals(newdir))
					{
						lastdir	= newdir;
					}
					else
					{
						// Prey unreachable.
						getBeliefbase().getBelief("nearest_prey").setFact(null);						
					}
				}

				// When no prey, turn 90 degrees with probability 0.25, otherwise continue moving in same direction.
				else if(lastdir==null || Math.random()>0.75)
				{
					LearningGraph.noPrey();
					if(MoveAction.DIRECTION_LEFT.equals(lastdir) || MoveAction.DIRECTION_RIGHT.equals(lastdir))
					{
						lastdir	= Math.random()>0.5 ? MoveAction.DIRECTION_UP : MoveAction.DIRECTION_DOWN;
					}
					else
					{
						lastdir	= Math.random()>0.5 ? MoveAction.DIRECTION_LEFT : MoveAction.DIRECTION_RIGHT;
					}
				}else {
					LearningGraph.noPrey();
				}

				// Perform move action.
				try
				{
					Map<String, Object> params = new HashMap<String, Object>();
					params.put(ISpaceAction.ACTOR_ID, getComponentDescription());
					params.put(MoveAction.PARAMETER_DIRECTION, lastdir);
					Future<Void> fut = new Future<Void>();
					env.performSpaceAction("move", params, new DelegationResultListener<Void>(fut));
					fut.get();
				}
				catch(RuntimeException e)
				{
					// Move failed, forget about prey and turn 90 degrees.
					getBeliefbase().getBelief("nearest_prey").setFact(null);

					if(MoveAction.DIRECTION_LEFT.equals(lastdir) || MoveAction.DIRECTION_RIGHT.equals(lastdir))
					{
						lastdir	= Math.random()>0.5 ? MoveAction.DIRECTION_UP : MoveAction.DIRECTION_DOWN;
					}
					else
					{
						lastdir	= Math.random()>0.5 ? MoveAction.DIRECTION_LEFT : MoveAction.DIRECTION_RIGHT;
					}
				}
			}
		}
	}
}
