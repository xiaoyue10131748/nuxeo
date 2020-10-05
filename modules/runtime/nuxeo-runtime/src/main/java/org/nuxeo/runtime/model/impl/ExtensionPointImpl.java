/*
 * (C) Copyright 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Nuxeo - initial API and implementation
 *
 * $Id$
 */

package org.nuxeo.runtime.model.impl;

import java.io.Serializable;

import org.nuxeo.common.xmap.XAnnotatedObject;
import org.nuxeo.common.xmap.XAnnotatedRegistry;
import org.nuxeo.common.xmap.XMap;
import org.nuxeo.common.xmap.XMapException;
import org.nuxeo.common.xmap.annotation.XContent;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XNodeList;
import org.nuxeo.common.xmap.annotation.XObject;
import org.nuxeo.common.xmap.annotation.XParent;
import org.nuxeo.runtime.model.Extension;
import org.nuxeo.runtime.model.ExtensionPoint;
import org.nuxeo.runtime.model.RegistrationInfo;
import org.nuxeo.runtime.model.Registry;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
@XObject
public class ExtensionPointImpl implements ExtensionPoint, Serializable {

    private static final long serialVersionUID = 3959978759388449332L;

    @XNode("@name")
    public String name;

    @XNode("@target")
    public String superComponent;

    @XContent("documentation")
    public String documentation;

    @XNodeList(value = "object@class", type = Class[].class, componentType = Class.class)
    public transient Class<?>[] contributions;

    public transient XMap xmap;

    @XParent
    public transient RegistrationInfo ri;

    // potential registry class declaration
    @XNode(value = "registry@class")
    transient String registryKlass;

    // potential registry annotations on contribution classes
    transient XAnnotatedRegistry[] registries;

    @Override
    public Class<?>[] getContributions() {
        return contributions;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDocumentation() {
        return documentation;
    }

    @Override
    public String getSuperComponent() {
        return superComponent;
    }

    protected XMap getXmap() {
        if (xmap == null) {
            xmap = new XMap();
            registries = new XAnnotatedRegistry[contributions.length];
            for (int i = 0; i < contributions.length; i++) {
                Class<?> contrib = contributions[i];
                if (contrib != null) {
                    XAnnotatedObject xao = xmap.register(contrib);
                    registries[i] = xmap.getRegistry(xao);
                } else {
                    throw new RuntimeException(
                            "Unknown implementation class when contributing to " + ri.getComponent().getName());
                }
            }
        }
        return xmap;
    }

    public Object[] loadContributions(RegistrationInfo owner, Extension extension) {
        return loadContributions(ri, extension);
    }

    @Override
    public Object[] loadContributions(Extension extension) {
        Object[] contribs = extension.getContributions();
        if (contribs != null) {
            // contributions already computed - this should be an overloaded (extended) extension point
            return contribs;
        }
        // should compute now the contributions
        if (contributions != null) {
            try {
                contribs = getXmap().loadAll(new XMapContext(extension.getContext()), extension.getElement());
            } catch (XMapException e) {
                throw new RuntimeException(
                        e.getMessage() + " while processing component: " + extension.getComponent().getName().getName(),
                        e);
            }
            extension.setContributions(contribs);
        } else {
            throw new RuntimeException(String.format(
                    "Cannot contribute contributions from component '%s': extension point '%s:%s' is missing contribution classes",
                    extension.getComponent().getName(), superComponent, name));
        }
        return contribs;
    }

    @Override
    public Registry getRegistry() {
        if (registries == null) {
            // compute them first

        }
        if (registries.length > 0) {
            return registries[0];
        }
        return null;
    }

}
