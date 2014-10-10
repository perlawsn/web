package org.dei.perla.rest.controller;

import org.dei.perla.channel.ChannelFactory;
import org.dei.perla.channel.IORequestBuilderFactory;
import org.dei.perla.channel.http.HttpChannelFactory;
import org.dei.perla.channel.http.HttpIORequestBuilderFactory;
import org.dei.perla.channel.simulator.SimulatorChannelFactory;
import org.dei.perla.channel.simulator.SimulatorIORequestBuilderFactory;
import org.dei.perla.channel.simulator.SimulatorMapperFactory;
import org.dei.perla.fpc.Attribute;
import org.dei.perla.fpc.Fpc;
import org.dei.perla.fpc.descriptor.DataType;
import org.dei.perla.message.MapperFactory;
import org.dei.perla.message.json.JsonMapperFactory;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Guido Rota 10/10/14.
 */
public class TestPerLaController {

    private static PerLaController ctrl;

    @BeforeClass
    public static void setup() throws PerLaException {
        List<String> pkgs = new ArrayList<>();
        pkgs.add("org.dei.perla.fpc.descriptor");
        pkgs.add("org.dei.perla.fpc.descriptor.instructions");
        pkgs.add("org.dei.perla.channel.http");
        pkgs.add("org.dei.perla.message.json");
        pkgs.add("org.dei.perla.channel.simulator");

        List<MapperFactory> mpFcts = new ArrayList<>();
        mpFcts.add(new SimulatorMapperFactory());
        mpFcts.add(new JsonMapperFactory());

        List<ChannelFactory> chFcts = new ArrayList<>();
        chFcts.add(new SimulatorChannelFactory());
        chFcts.add(new HttpChannelFactory());

        List<IORequestBuilderFactory> reqBldrFcts = new ArrayList<>();
        reqBldrFcts.add(new SimulatorIORequestBuilderFactory());
        reqBldrFcts.add(new HttpIORequestBuilderFactory());

        ctrl = new PerLaController(pkgs, mpFcts, chFcts, reqBldrFcts);
    }

    @Test
    public void testPerLaController() throws Exception {
        assertThat(ctrl.getAllFpcs().size(), equalTo(0));
        Fpc fpc = ctrl.createFpc(new FileInputStream("src/main/resources/simulator.xml"));
        assertThat(ctrl.getAllFpcs().size(), equalTo(1));

        Collection<Attribute> atts = new ArrayList<>();
        atts.add(new Attribute("temp_c", DataType.FLOAT));
        assertThat(ctrl.getAllTasks().size(), equalTo(0));
        int t = ctrl.queryPeriodic(atts, 1000);
        assertThat(ctrl.getAllTasks().size(), equalTo(1));
        ctrl.stopTask(t);
        assertThat(ctrl.getAllTasks().size(), equalTo(0));
    }

    @AfterClass
    public static void teardown() {
        ctrl.shutdown();
    }

}
