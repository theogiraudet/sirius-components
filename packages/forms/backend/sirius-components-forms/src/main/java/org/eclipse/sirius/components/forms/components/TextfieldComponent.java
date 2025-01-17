/*******************************************************************************
 * Copyright (c) 2019, 2022 Obeo.
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

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.eclipse.sirius.components.forms.description.TextfieldDescription;
import org.eclipse.sirius.components.forms.elements.TextfieldElementProps;
import org.eclipse.sirius.components.forms.elements.TextfieldElementProps.Builder;
import org.eclipse.sirius.components.forms.validation.DiagnosticComponent;
import org.eclipse.sirius.components.forms.validation.DiagnosticComponentProps;
import org.eclipse.sirius.components.representations.Element;
import org.eclipse.sirius.components.representations.IComponent;
import org.eclipse.sirius.components.representations.IStatus;
import org.eclipse.sirius.components.representations.VariableManager;

/**
 * The component used to render the textfield.
 *
 * @author sbegaudeau
 */
public class TextfieldComponent implements IComponent {

    private TextfieldComponentProps props;

    public TextfieldComponent(TextfieldComponentProps props) {
        this.props = Objects.requireNonNull(props);
    }

    @Override
    public Element render() {
        VariableManager variableManager = this.props.getVariableManager();
        TextfieldDescription textfieldDescription = this.props.getTextfieldDescription();

        String id = textfieldDescription.getIdProvider().apply(variableManager);
        String label = textfieldDescription.getLabelProvider().apply(variableManager);
        String iconURL = textfieldDescription.getIconURLProvider().apply(variableManager);
        String value = textfieldDescription.getValueProvider().apply(variableManager);
        BiFunction<VariableManager, String, IStatus> genericHandler = textfieldDescription.getNewValueHandler();
        Function<String, IStatus> specializedHandler = newValue -> {
            return genericHandler.apply(variableManager, newValue);
        };
        var textfieldStyle = textfieldDescription.getStyleProvider().apply(variableManager);

        List<Element> children = List.of(new Element(DiagnosticComponent.class, new DiagnosticComponentProps(textfieldDescription, variableManager)));

        // @formatter:off
        Builder textfieldElementPropsBuilder = TextfieldElementProps.newTextfieldElementProps(id)
                .label(label)
                .value(value)
                .newValueHandler(specializedHandler)
                .children(children);

        if (iconURL != null) {
            textfieldElementPropsBuilder.iconURL(iconURL);
        }
        if (textfieldStyle != null) {
            textfieldElementPropsBuilder.style(textfieldStyle);
        }

        TextfieldElementProps textfieldElementProps = textfieldElementPropsBuilder.build();

        return new Element(TextfieldElementProps.TYPE, textfieldElementProps);
        // @formatter:on
    }
}
