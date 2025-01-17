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
package org.eclipse.sirius.components.view.emf.diagram.providers;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.sirius.components.collaborative.diagrams.api.IInitialDirectEditElementLabelProvider;
import org.eclipse.sirius.components.compatibility.api.IIdentifierProvider;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IObjectService;
import org.eclipse.sirius.components.diagrams.Diagram;
import org.eclipse.sirius.components.diagrams.Edge;
import org.eclipse.sirius.components.diagrams.Node;
import org.eclipse.sirius.components.emf.services.EditingContext;
import org.eclipse.sirius.components.interpreter.AQLInterpreter;
import org.eclipse.sirius.components.interpreter.Result;
import org.eclipse.sirius.components.interpreter.Status;
import org.eclipse.sirius.components.representations.VariableManager;
import org.eclipse.sirius.components.view.DiagramDescription;
import org.eclipse.sirius.components.view.DiagramElementDescription;
import org.eclipse.sirius.components.view.EdgeDescription;
import org.eclipse.sirius.components.view.LabelEditTool;
import org.eclipse.sirius.components.view.NodeDescription;
import org.eclipse.sirius.components.view.View;
import org.eclipse.sirius.components.view.emf.IJavaServiceProvider;
import org.eclipse.sirius.components.view.emf.IViewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * The {@link IInitialDirectEditElementLabelProvider} for view elements.
 *
 * @author gcoutable
 */
@Service
public class ViewInitialDirectEditElementLabelProvider implements IInitialDirectEditElementLabelProvider {

    private final Logger logger = LoggerFactory.getLogger(ViewInitialDirectEditElementLabelProvider.class);

    private final IIdentifierProvider identifierProvider;

    private final IViewService viewService;

    private final IObjectService objectService;

    private final List<IJavaServiceProvider> javaServiceProviders;

    private final ApplicationContext applicationContext;

    private final Function<DiagramElementDescription, UUID> idProvider = (diagramElementDescription) -> {
        // DiagramElementDescription should have a proper id.
        return UUID.nameUUIDFromBytes(EcoreUtil.getURI(diagramElementDescription).toString().getBytes());
    };

    public ViewInitialDirectEditElementLabelProvider(IIdentifierProvider identifierProvider, IViewService viewService, IObjectService objectService, List<IJavaServiceProvider> javaServiceProviders,
            ApplicationContext applicationContext) {
        this.identifierProvider = Objects.requireNonNull(identifierProvider);
        this.viewService = Objects.requireNonNull(viewService);
        this.objectService = Objects.requireNonNull(objectService);
        this.javaServiceProviders = Objects.requireNonNull(javaServiceProviders);
        this.applicationContext = Objects.requireNonNull(applicationContext);
    }

    @Override
    public boolean canHandle(org.eclipse.sirius.components.diagrams.description.DiagramDescription diagramDescription) {
        return this.identifierProvider.findVsmElementId(diagramDescription.getId()).isEmpty();
    }

    @Override
    public String getInitialDirectEditElementLabel(Object graphicalElement, Diagram diagram, IEditingContext editingContext) {
        String initialDirectEditElementLabel = ""; //$NON-NLS-1$
        String diagramDescriptionId = diagram.getDescriptionId();
        var optionalDiagramDescription = this.viewService.getRepresentationDescription(diagramDescriptionId).filter(DiagramDescription.class::isInstance).map(DiagramDescription.class::cast);

        if (optionalDiagramDescription.isPresent()) {
            DiagramDescription diagramDescription = optionalDiagramDescription.get();
            Optional<LabelEditTool> optionalLabelEditTool = Optional.empty();
            Optional<Object> semanticElement = Optional.empty();

            if (graphicalElement instanceof Node) {
                Node node = (Node) graphicalElement;
                UUID descriptionId = node.getDescriptionId();
                optionalLabelEditTool = this.getNodeDescription(diagramDescription.getNodeDescriptions(), descriptionId).map(NodeDescription::getLabelEditTool);
                semanticElement = this.objectService.getObject(editingContext, node.getTargetObjectId());
                initialDirectEditElementLabel = node.getLabel().getText();
            } else if (graphicalElement instanceof Edge) {
                Edge edge = (Edge) graphicalElement;
                UUID descriptionId = edge.getDescriptionId();
                optionalLabelEditTool = this.getEdgeDescription(diagramDescription.getEdgeDescriptions(), descriptionId).map(EdgeDescription::getLabelEditTool);
                semanticElement = this.objectService.getObject(editingContext, edge.getTargetObjectId());
                initialDirectEditElementLabel = edge.getCenterLabel().getText();
            }

            if (optionalLabelEditTool.isPresent() && semanticElement.isPresent()) {
                LabelEditTool labelEditTool = optionalLabelEditTool.get();
                if (labelEditTool.getInitialDirectEditLabelExpression() != null && !labelEditTool.getInitialDirectEditLabelExpression().isBlank()) {
                    AQLInterpreter interpreter = this.createInterpreter((View) diagramDescription.eContainer(), editingContext);
                    VariableManager variableManager = new VariableManager();
                    variableManager.put(VariableManager.SELF, semanticElement.get());
                    variableManager.put("view", graphicalElement); //$NON-NLS-1$
                    variableManager.put("diagram", diagram); //$NON-NLS-1$
                    Result result = interpreter.evaluateExpression(variableManager.getVariables(), labelEditTool.getInitialDirectEditLabelExpression());
                    if (result.getStatus().compareTo(Status.WARNING) <= 0 && result.asString().isPresent()) {
                        initialDirectEditElementLabel = result.asString().get();
                    }
                }
            }

        }

        return initialDirectEditElementLabel;
    }

    private Optional<NodeDescription> getNodeDescription(List<NodeDescription> nodeDescriptions, UUID descriptionId) {
        var optionalNodeDescription = nodeDescriptions.stream().filter(nodeDescription -> descriptionId.equals(this.idProvider.apply(nodeDescription))).findFirst();

        if (optionalNodeDescription.isEmpty()) {
            Stream<NodeDescription> childrenStream = nodeDescriptions.stream().map(NodeDescription::getChildrenDescriptions).flatMap(Collection::stream);
            Stream<NodeDescription> borderNodeStream = nodeDescriptions.stream().map(NodeDescription::getBorderNodesDescriptions).flatMap(Collection::stream);
            List<NodeDescription> childrenDescription = Stream.concat(childrenStream, borderNodeStream).collect(Collectors.toList());
            optionalNodeDescription = this.getNodeDescription(childrenDescription, descriptionId);
        }

        return optionalNodeDescription;
    }

    private Optional<EdgeDescription> getEdgeDescription(List<EdgeDescription> edgeDescriptions, UUID descriptionId) {
        // @formatter:off
        return edgeDescriptions.stream()
                .filter(edgeDescription -> descriptionId.equals(this.idProvider.apply(edgeDescription)))
                .findFirst();
        // @formatter:on
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

    private AQLInterpreter createInterpreter(View view, IEditingContext editingContext) {
        List<EPackage> visibleEPackages = this.getAccessibleEPackages(editingContext);
        AutowireCapableBeanFactory beanFactory = this.applicationContext.getAutowireCapableBeanFactory();
        // @formatter:off
        List<Object> serviceInstances = this.javaServiceProviders.stream()
                .flatMap(provider -> provider.getServiceClasses(view).stream())
                .map(serviceClass -> {
                    try {
                        return beanFactory.createBean(serviceClass);
                    } catch (BeansException beansException) {
                        this.logger.warn("Error while trying to instantiate Java service class " + serviceClass.getName(), beansException); //$NON-NLS-1$
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        // @formatter:on
        return new AQLInterpreter(List.of(), serviceInstances, visibleEPackages);
    }

}
