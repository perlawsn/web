package org.dei.perla.rest.controller;

import org.dei.perla.core.channel.ChannelFactory;
import org.dei.perla.core.channel.IORequestBuilderFactory;
import org.dei.perla.core.channel.http.HttpChannelFactory;
import org.dei.perla.core.channel.http.HttpIORequestBuilderFactory;
import org.dei.perla.core.channel.simulator.SimulatorChannelFactory;
import org.dei.perla.core.channel.simulator.SimulatorIORequestBuilderFactory;
import org.dei.perla.core.channel.simulator.SimulatorMapperFactory;
import org.dei.perla.core.engine.Attribute;
import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.message.MapperFactory;
import org.dei.perla.core.message.json.JsonMapperFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Guido Rota 10/10/14.
 */
public class TestPerLaController {

    private static PerLaController ctrl;

    @BeforeClass
    public static void setup() throws PerLaException {
        List<String> pkgs = new ArrayList<>();
        pkgs.add("org.dei.perla.core.descriptor");
        pkgs.add("org.dei.perla.core.descriptor.instructions");
        pkgs.add("org.dei.perla.core.channel.http");
        pkgs.add("org.dei.perla.core.message.json");
        pkgs.add("org.dei.perla.core.channel.simulator");

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
        assertThat(fpc, notNullValue());
        assertThat(ctrl.getAllFpcs().size(), equalTo(1));
        Fpc retrieved = ctrl.getFpc(fpc.getId());
        assertThat(retrieved, notNullValue());
        assertThat(retrieved.getId(), equalTo(fpc.getId()));

        Collection<Attribute> atts = new ArrayList<>();
        atts.add(Attribute.create("temp_c", DataType.FLOAT));
        assertThat(ctrl.getAllTasks().size(), equalTo(0));

        RestTask t = ctrl.queryPeriodic(atts, 1000);
        assertThat(ctrl.getAllTasks().size(), equalTo(1));
        assertThat(t.getPeriod(), equalTo(1000L));
        assertTrue(t.getAttributes().containsAll(atts));

        ctrl.stopTask(t.getId());
        assertThat(ctrl.getAllTasks().size(), equalTo(0));
    }

}
