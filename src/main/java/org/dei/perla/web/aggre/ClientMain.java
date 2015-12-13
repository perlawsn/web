package org.dei.perla.web.aggre;


	
	import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

	import org.dei.perla.core.Plugin;
import org.dei.perla.core.channel.http.HttpChannelPlugin;
import org.dei.perla.core.channel.simulator.SimulatorChannelPlugin;
import org.dei.perla.core.channel.simulator.SimulatorMapperFactory;
import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.fpc.FpcCreationException;
import org.dei.perla.core.message.json.JsonMapperFactory;
import org.dei.perla.core.message.urlencoded.UrlEncodedMapperFactory;
import org.dei.perla.rest.controller.AggregatorPerLaController;
import org.dei.perla.rest.controller.PerLaController;
import org.dei.perla.rest.controller.PerLaException;

	public class ClientMain {
		
		private static AggregatorPerLaController ctrl;
		
		public static void main(String args[]){
		    List<Plugin> plugins = new ArrayList<>();
	        plugins.add(new JsonMapperFactory());
	        plugins.add(new SimulatorMapperFactory());
	        plugins.add(new HttpChannelPlugin());
	        plugins.add(new SimulatorChannelPlugin());
	        
	        try {
				ctrl = new AggregatorPerLaController(plugins);
				Fpc fpc = ctrl.createFpc(new FileInputStream("src/main/java/org/dei/perla/web/aggre/simulator.xml"));
			} catch (PerLaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        ctrl.start();
		}
		
		
}
