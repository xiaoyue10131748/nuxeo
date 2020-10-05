/*
 * (C) Copyright 2020 Nuxeo (http://nuxeo.com/) and others.
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
package org.nuxeo.common.xmap.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation representing the registry to create for annotated {@link XObject} class.
 *
 * @since TODO
 */
@XMemberAnnotation(XMemberAnnotation.REGISTRY)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface XRegistry {

    /**
     * Returns the xpath expression for the node to bind to for id retrieval.
     * <p>
     * If there is no specific id to be used, use {@link XRegistry#uniqueId()}.
     */
    String id() default "";

    /**
     * Returns true if there is only one id (use case for registries holding only one item).
     * <p>
     * This boolean should set to true if no id is set.
     */
    boolean uniqueId() default false;

    /**
     * Returns the path expression specifying the XML node to bind to for enablement retrieval.
     */
    String enabled() default "enabled";

    /**
     * Returns true if items should be merged on the registry.
     * <p>
     * The merge can only be done if an {@link #id()} is specified or if {@link #uniqueId()} is true.
     */
    boolean merge() default false;

}
