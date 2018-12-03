package learning_hunterprey;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;

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
 *   This starts up the Jadex platform and the HunterPrey
 */
public class Startup
{
	public static void main(String[] args)
	{
		PlatformConfiguration configuration = PlatformConfiguration.getDefaultNoGui();
		configuration.addComponent("learning_hunterprey.HunterPrey.application.xml");
		configuration.setAutoShutdown(true);
		IExternalAccess platform = Starter.createPlatform(configuration).get();
	}
}