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
package org.eclipse.sirius.components.view.emf;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.sirius.components.collaborative.api.IRepresentationDescriptionsProvider;
import org.eclipse.sirius.components.collaborative.api.RepresentationDescriptionMetadata;
import org.eclipse.sirius.components.compatibility.api.IIdentifierProvider;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.emf.services.EditingContext;
import org.eclipse.sirius.components.interpreter.AQLInterpreter;
import org.eclipse.sirius.components.representations.IRepresentationDescription;
import org.eclipse.sirius.components.representations.VariableManager;
import org.eclipse.sirius.components.view.View;
import org.springframework.stereotype.Service;

/**
 * Used to provide the {@link RepresentationDescriptionMetadata}s of a given object from for all views.
 *
 * @author arichard
 */
@Service
public class ViewRepresentationDescriptionsProvider implements IRepresentationDescriptionsProvider {

    private final IIdentifierProvider identifierProvider;

    private final IViewService viewService;

    private final List<IJavaServiceProvider> javaServiceProviders;

    public ViewRepresentationDescriptionsProvider(IIdentifierProvider identifierProvider, IViewService viewService, List<IJavaServiceProvider> javaServiceProviders) {
        this.identifierProvider = Objects.requireNonNull(identifierProvider);
        this.viewService = Objects.requireNonNull(viewService);
        this.javaServiceProviders = Objects.requireNonNull(javaServiceProviders);
    }

    @Override
    public boolean canHandle(IRepresentationDescription representationDescription) {
        return this.identifierProvider.findVsmElementId(representationDescription.getId()).isEmpty();
    }

    @Override
    public List<RepresentationDescriptionMetadata> handle(IEditingContext editingContext, Object object, IRepresentationDescription representationDescription) {
        List<RepresentationDescriptionMetadata> result = new ArrayList<>();
        var viewRepresentationDescription = this.viewService.getRepresentationDescription(representationDescription.getId());
        if (viewRepresentationDescription.isPresent()) {
            String defaultName = viewRepresentationDescription.map(view -> this.getDefaultName(view, editingContext, object)).orElse(representationDescription.getLabel());
            result.add(new RepresentationDescriptionMetadata(representationDescription.getId(), representationDescription.getLabel(), defaultName));
        }
        return result;
    }

    private List<EPackage> getAccessibleEPackages(IEditingContext editingContext) {
        if (editingContext instanceof EditingContext) {
            Registry packageRegistry = ((EditingContext) editingContext).getDomain().getResourceSet().getPackageRegistry();
            // @formatter:off
            return packageRegistry.values().stream()
                                  .filter(EPackage.class::isInstance)
                                  .map(EPackage.class::cast)
                                  .collect(Collectors.toList());
            // @formatter:on
        } else {
            return List.of();
        }
    }

    private String getDefaultName(org.eclipse.sirius.components.view.RepresentationDescription viewRepresentationDescription, IEditingContext editingContext, Object self) {
        String titleExpression = viewRepresentationDescription.getTitleExpression();
        if (titleExpression != null && !titleExpression.isBlank()) {
            List<EPackage> accessibleEPackages = this.getAccessibleEPackages(editingContext);
            AQLInterpreter interpreter = this.createInterpreter((View) viewRepresentationDescription.eContainer(), accessibleEPackages);
            VariableManager variableManager = new VariableManager();
            variableManager.put(VariableManager.SELF, self);
            return interpreter.evaluateExpression(variableManager.getVariables(), titleExpression).asString().orElse(null);
        }
        return null;
    }

    private AQLInterpreter createInterpreter(View view, List<EPackage> visibleEPackages) {
        // @formatter:off
        List<Class<?>> serviceClasses = this.javaServiceProviders.stream()
                                            .flatMap(provider -> provider.getServiceClasses(view).stream())
                                            .collect(Collectors.toList());
        // @formatter:on
        return new AQLInterpreter(serviceClasses, visibleEPackages);
    }
}
