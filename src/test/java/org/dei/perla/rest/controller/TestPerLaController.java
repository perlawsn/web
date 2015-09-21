package org.dei.perla.rest.controller;

import org.dei.perla.core.Plugin;
import org.dei.perla.core.channel.http.HttpChannelPlugin;
import org.dei.perla.core.channel.simulator.SimulatorChannelPlugin;
import org.dei.perla.core.channel.simulator.SimulatorMapperFactory;
import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.DataType;
import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.message.json.JsonMapperFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileInputStream;
import java.util.ArrayList;
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
        List<Plugin> plugins = new ArrayList<>();
        plugins.add(new JsonMapperFactory());
        plugins.add(new SimulatorMapperFactory());
        plugins.add(new HttpChannelPlugin());
        plugins.add(new SimulatorChannelPlugin());

        ctrl = new PerLaController(plugins);
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

        List<Attribute> data = new ArrayList<>();
        data.add(Attribute.create("temp_c", DataType.FLOAT));
        assertThat(ctrl.getAllTasks().size(), equalTo(0));

        RestTask t = ctrl.queryPeriodic(data, 1000);
        assertThat(ctrl.getAllTasks().size(), equalTo(1));
        assertThat(t.getPeriod(), equalTo(1000L));
        Attribute a = Attribute.create("temp_c", DataType.FLOAT);
        assertTrue(t.getAttributes().contains(a));

        ctrl.stopTask(t.getId());
        assertThat(ctrl.getAllTasks().size(), equalTo(0));
    }

}
