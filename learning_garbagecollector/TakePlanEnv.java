package learning_garbagecollector;

import java.util.HashMap;
import java.util.Map;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanReason;
import learning_garbagecollector.GarbageCollectorBDI.Go;
import learning_garbagecollector.GarbageCollectorBDI.Pick;
import learning_garbagecollector.GarbageCollectorBDI.Take;
import jadex.bdiv3.runtime.IPlan;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;

/**
 *  Take some garbage and bring it to the burner.
 */
@Plan
public class TakePlanEnv
{
	//-------- attributes --------

	@PlanCapability
	protected GarbageCollectorBDI collector;
		
	@PlanAPI
	protected IPlan rplan;
		
	@PlanReason
	protected Take goal;
	
	/**
	 *  The plan body.
	 *  Part Author: Julius
	 *  
	 *  The part of the plan that moves the GarbageCollector back to the previous pickup
	 *  site was removed.
	 */
	@PlanBody
	public void body()
	{
		Space2D grid = (Space2D)collector.getEnvironment();

		// Pickup the garbarge.
		Pick pick = collector.new Pick();
		rplan.dispatchSubgoal(pick).get();

		// Go to the burner.
		ISpaceObject myself = collector.getMyself();
		IVector2 oldpos =(IVector2)myself.getProperty(Space2D.PROPERTY_POSITION);
		ISpaceObject burner = grid.getNearestObject(oldpos, null, "burner");
		IVector2 pos = (IVector2)burner.getProperty(Space2D.PROPERTY_POSITION);
		Go go = collector.new Go(pos);
		rplan.dispatchSubgoal(go).get();

		// Put down the garbarge.
		Future<Void> fut = new Future<Void>();
		DelegationResultListener<Void> lis = new DelegationResultListener<Void>(fut, true);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(ISpaceAction.ACTOR_ID, collector.getAgent().getComponentDescription());
		grid.performSpaceAction("drop", params, lis);
		fut.get();


	}

}
