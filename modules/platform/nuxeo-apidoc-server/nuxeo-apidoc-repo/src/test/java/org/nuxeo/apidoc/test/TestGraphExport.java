/*
 * (C) Copyright 2020 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Anahide Tchertchian
 */
package org.nuxeo.apidoc.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.apidoc.export.api.Export;
import org.nuxeo.apidoc.export.api.Exporter;
import org.nuxeo.apidoc.export.graphs.api.GRAPH_TYPE;
import org.nuxeo.apidoc.snapshot.DistributionSnapshot;
import org.nuxeo.apidoc.snapshot.SnapshotManager;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * @since 11.2
 */
@RunWith(FeaturesRunner.class)
@Features({ RuntimeSnaphotFeature.class })
public class TestGraphExport extends AbstractApidocTest {

    @Inject
    protected CoreSession session;

    @Inject
    protected SnapshotManager snapshotManager;

    @Test
    public void testGetExporters() throws Exception {
        assertNull(snapshotManager.getExporter("foo"));
        List<Exporter> exporters = snapshotManager.getExporters();
        assertEquals(2, exporters.size());
        assertEquals("jsonGraph", exporters.get(0).getName());
        assertEquals("dotGraph", exporters.get(1).getName());
    }

    @Test
    public void testGetExporter() throws Exception {
        assertNull(snapshotManager.getExporter("foo"));
        assertNotNull(snapshotManager.getExporter("jsonGraph"));
        assertNotNull(snapshotManager.getExporter("dotGraph"));
    }

    protected void checkDefaultExports(DistributionSnapshot snapshot) throws Exception {
        assertNotNull(snapshot);
        Exporter exporter = snapshotManager.getExporter("jsonGraph");
        Export export = exporter.getExport(snapshot, null, Map.of("pretty", "true"));
        assertEquals("jsonGraph", export.getName());
        assertEquals("Basic Graph", export.getTitle());
        assertEquals("Complete graph, with dependencies, without a layout", export.getDescription());
        assertEquals(GRAPH_TYPE.BASIC.name(), export.getType());
        assertFalse(export.getProperties().isEmpty());
        assertEquals(Map.of("pretty", "true"), export.getProperties());
        checkContentEquals("export/graphs/basic_graph.json", export.getBlob().getString());

        exporter = snapshotManager.getExporter("dotGraph");
        export = exporter.getExport(snapshot, null, null);
        assertEquals("dotGraph", export.getName());
        assertEquals("DOT Graph", export.getTitle());
        assertEquals("Complete Graph exported in DOT format", export.getDescription());
        assertEquals(GRAPH_TYPE.BASIC.name(), export.getType());
        assertTrue(export.getProperties().isEmpty());
        checkContentEquals("export/graphs/jgrapht.dot", export.getBlob().getString());
    }

    @Test
    public void testExportLive() throws Exception {
        checkDefaultExports(snapshotManager.getRuntimeSnapshot());
    }

    @Ignore("Need to implement persisted distribs serialization")
    @Test
    public void testExportPersisted() throws Exception {
        checkDefaultExports(snapshotManager.persistRuntimeSnapshot(session));
    }

}
