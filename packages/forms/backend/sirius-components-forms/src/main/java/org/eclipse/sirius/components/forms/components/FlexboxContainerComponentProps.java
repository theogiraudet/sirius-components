/*******************************************************************************
 * Copyright (c) 2022 Obeo.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.components.forms.components;

import java.util.Objects;

import org.eclipse.sirius.components.forms.description.FlexboxContainerDescription;
import org.eclipse.sirius.components.representations.IProps;
import org.eclipse.sirius.components.representations.VariableManager;

/**
 * The properties of the flexbox container component.
 *
 * @author arichard
 */
public class FlexboxContainerComponentProps implements IProps {
    private final VariableManager variableManager;

    private final FlexboxContainerDescription flexboxContainerDescription;

    public FlexboxContainerComponentProps(VariableManager variableManager, FlexboxContainerDescription flexboxContainerDescription) {
        this.variableManager = Objects.requireNonNull(variableManager);
        this.flexboxContainerDescription = Objects.requireNonNull(flexboxContainerDescription);
    }

    public VariableManager getVariableManager() {
        return this.variableManager;
    }

    public FlexboxContainerDescription getFlexboxContainerDescription() {
        return this.flexboxContainerDescription;
    }
}
