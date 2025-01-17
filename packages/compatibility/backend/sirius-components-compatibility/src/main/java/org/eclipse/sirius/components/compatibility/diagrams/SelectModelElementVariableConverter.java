/*******************************************************************************
 * Copyright (c) 2021, 2022 Obeo.
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
package org.eclipse.sirius.components.compatibility.diagrams;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.sirius.components.compatibility.api.IAQLInterpreterFactory;
import org.eclipse.sirius.components.compatibility.api.IIdentifierProvider;
import org.eclipse.sirius.components.compatibility.services.selection.api.ISelectModelElementVariableConverter;
import org.eclipse.sirius.components.core.api.IObjectService;
import org.eclipse.sirius.components.interpreter.AQLInterpreter;
import org.eclipse.sirius.components.interpreter.Result;
import org.eclipse.sirius.components.representations.GetOrCreateRandomIdProvider;
import org.eclipse.sirius.components.representations.VariableManager;
import org.eclipse.sirius.components.selection.description.SelectionDescription;
import org.eclipse.sirius.viewpoint.description.tool.SelectModelElementVariable;
import org.springframework.stereotype.Service;

/**
 * This class is used to convert a Sirius SelectModelElementVariable to a Sirius Web SelectionDescription. We only
 * handle SelectModelElementVariable for ContainerCreationDescription for now, and only one SelectModelElementVariable
 * per ContainerCreationDescription.
 *
 * @author arichard
 */
@Service
public class SelectModelElementVariableConverter implements ISelectModelElementVariableConverter {

    private final IObjectService objectService;

    private final IIdentifierProvider identifierProvider;

    private final IAQLInterpreterFactory interpreterFactory;

    public SelectModelElementVariableConverter(IObjectService objectService, IIdentifierProvider identifierProvider, IAQLInterpreterFactory interpreterFactory) {
        this.objectService = Objects.requireNonNull(objectService);
        this.identifierProvider = Objects.requireNonNull(identifierProvider);
        this.interpreterFactory = Objects.requireNonNull(interpreterFactory);
    }

    @Override
    public SelectionDescription convert(SelectModelElementVariable selectModelElementVariable, org.eclipse.sirius.diagram.description.DiagramDescription diagramDescription) {
        AQLInterpreter interpreter = this.interpreterFactory.create(diagramDescription);
        // @formatter:off
        SelectionDescription selectionDescription = SelectionDescription.newSelectionDescription(this.identifierProvider.getIdentifier(selectModelElementVariable))
                .objectsProvider(variableManager -> {
                    Result result = interpreter.evaluateExpression(variableManager.getVariables(), selectModelElementVariable.getCandidatesExpression());
                    return result.asObjects().orElse(List.of()).stream()
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                })
                .messageProvider(variableManager -> {
                    String message = selectModelElementVariable.getMessage();
                    if (message == null) {
                        message = ""; //$NON-NLS-1$
                    }
                    return message;
                })
                .idProvider(new GetOrCreateRandomIdProvider())
                .labelProvider(variableManager -> variableManager.get(VariableManager.SELF, Object.class).map(this.objectService::getLabel).orElse(null))
                .iconURLProvider(variableManager -> variableManager.get(VariableManager.SELF, Object.class).map(this.objectService::getImagePath).orElse(null))
                .targetObjectIdProvider(variableManager -> variableManager.get(VariableManager.SELF, Object.class).map(this.objectService::getId).orElse(null))
                .selectionObjectsIdProvider(variableManager -> variableManager.get(VariableManager.SELF, Object.class).map(this.objectService::getId).orElse(null))
                .label("Selection Description") //$NON-NLS-1$
                .canCreatePredicate(variableManager -> false)
                .build();
        // @formatter:on
        return selectionDescription;
    }

}
