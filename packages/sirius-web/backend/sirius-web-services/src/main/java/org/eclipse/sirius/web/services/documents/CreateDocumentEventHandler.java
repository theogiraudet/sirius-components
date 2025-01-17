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
package org.eclipse.sirius.web.services.documents;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.sirius.components.collaborative.api.ChangeDescription;
import org.eclipse.sirius.components.collaborative.api.ChangeKind;
import org.eclipse.sirius.components.collaborative.api.IEditingContextEventHandler;
import org.eclipse.sirius.components.collaborative.api.Monitoring;
import org.eclipse.sirius.components.collaborative.dto.CreateDocumentInput;
import org.eclipse.sirius.components.collaborative.dto.CreateDocumentSuccessPayload;
import org.eclipse.sirius.components.core.api.ErrorPayload;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IInput;
import org.eclipse.sirius.components.core.api.IPayload;
import org.eclipse.sirius.components.core.configuration.StereotypeDescription;
import org.eclipse.sirius.components.emf.services.EditingContext;
import org.eclipse.sirius.components.emf.services.JSONResourceFactory;
import org.eclipse.sirius.emfjson.resource.JsonResource;
import org.eclipse.sirius.web.services.api.document.Document;
import org.eclipse.sirius.web.services.api.document.IDocumentService;
import org.eclipse.sirius.web.services.api.stereotypes.IStereotypeDescriptionService;
import org.eclipse.sirius.web.services.messages.IServicesMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import reactor.core.publisher.Sinks.Many;
import reactor.core.publisher.Sinks.One;

/**
 * Event handler used to create a new document from a stereotype.
 *
 * @author sbegaudeau
 */
@Service
public class CreateDocumentEventHandler implements IEditingContextEventHandler {

    private final Logger logger = LoggerFactory.getLogger(CreateDocumentEventHandler.class);

    private final IDocumentService documentService;

    private final IStereotypeDescriptionService stereotypeDescriptionService;

    private final IServicesMessageService messageService;

    private final Counter counter;

    public CreateDocumentEventHandler(IDocumentService documentService, IStereotypeDescriptionService stereotypeDescriptionService, IServicesMessageService messageService,
            MeterRegistry meterRegistry) {
        this.documentService = Objects.requireNonNull(documentService);
        this.stereotypeDescriptionService = Objects.requireNonNull(stereotypeDescriptionService);
        this.messageService = Objects.requireNonNull(messageService);

        // @formatter:off
        this.counter = Counter.builder(Monitoring.EVENT_HANDLER)
                .tag(Monitoring.NAME, this.getClass().getSimpleName())
                .register(meterRegistry);
        // @formatter:on
    }

    @Override
    public boolean canHandle(IEditingContext editingContext, IInput input) {
        return input instanceof CreateDocumentInput;
    }

    @Override
    public void handle(One<IPayload> payloadSink, Many<ChangeDescription> changeDescriptionSink, IEditingContext editingContext, IInput input) {
        this.counter.increment();

        IPayload payload = new ErrorPayload(input.getId(), this.messageService.unexpectedError());
        ChangeDescription changeDescription = new ChangeDescription(ChangeKind.NOTHING, editingContext.getId(), input);

        if (input instanceof CreateDocumentInput) {
            CreateDocumentInput createDocumentInput = (CreateDocumentInput) input;

            String name = createDocumentInput.getName().trim();
            String editingContextId = createDocumentInput.getEditingContextId();
            UUID stereotypeDescriptionId = createDocumentInput.getStereotypeDescriptionId();

            Optional<StereotypeDescription> optionalStereotypeDescription = this.stereotypeDescriptionService.getStereotypeDescriptionById(editingContextId, stereotypeDescriptionId);

            if (name.isBlank()) {
                payload = new ErrorPayload(input.getId(), this.messageService.invalidDocumentName(name));
                payloadSink.tryEmitValue(payload);
                changeDescriptionSink.tryEmitNext(changeDescription);
            } else if (optionalStereotypeDescription.isEmpty()) {
                payload = new ErrorPayload(input.getId(), this.messageService.stereotypeDescriptionNotFound(stereotypeDescriptionId));
                payloadSink.tryEmitValue(payload);
                changeDescriptionSink.tryEmitNext(changeDescription);
            } else {
                StereotypeDescription stereotypeDescription = optionalStereotypeDescription.get();
                this.createDocument(payloadSink, changeDescriptionSink, createDocumentInput, editingContext, editingContextId, name, stereotypeDescription);
            }
        }

    }

    private void createDocument(One<IPayload> payloadSink, Many<ChangeDescription> changeDescriptionSink, IInput input, IEditingContext editingContext, String editingContextId, String name,
            StereotypeDescription stereotypeDescription) {

        IPayload payload = new ErrorPayload(input.getId(), this.messageService.unexpectedError());
        ChangeDescription changeDescription = new ChangeDescription(ChangeKind.NOTHING, editingContext.getId(), input);

        // @formatter:off
        Optional<AdapterFactoryEditingDomain> optionalEditingDomain = Optional.of(editingContext)
                .filter(EditingContext.class::isInstance)
                .map(EditingContext.class::cast)
                .map(EditingContext::getDomain);
        // @formatter:on

        if (optionalEditingDomain.isPresent()) {
            AdapterFactoryEditingDomain adapterFactoryEditingDomain = optionalEditingDomain.get();
            ResourceSet resourceSet = adapterFactoryEditingDomain.getResourceSet();

            var optionalDocument = this.documentService.createDocument(editingContextId, name, stereotypeDescription.getContent());
            if (optionalDocument.isPresent()) {
                Document document = optionalDocument.get();

                JsonResource resource = new JSONResourceFactory().createResourceFromPath(document.getId().toString());
                try (var inputStream = new ByteArrayInputStream(document.getContent().getBytes())) {
                    resource.load(inputStream, null);
                } catch (IOException exception) {
                    this.logger.warn(exception.getMessage(), exception);
                }

                resource.eAdapters().add(new DocumentMetadataAdapter(name));

                resourceSet.getResources().add(resource);

                payload = new CreateDocumentSuccessPayload(input.getId());
                changeDescription = new ChangeDescription(ChangeKind.SEMANTIC_CHANGE, editingContext.getId(), input);
            }
        }

        payloadSink.tryEmitValue(payload);
        changeDescriptionSink.tryEmitNext(changeDescription);
    }

}
