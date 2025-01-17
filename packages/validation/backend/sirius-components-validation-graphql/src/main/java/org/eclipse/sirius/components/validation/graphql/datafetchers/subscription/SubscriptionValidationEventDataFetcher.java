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
package org.eclipse.sirius.components.validation.graphql.datafetchers.subscription;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Objects;

import org.eclipse.sirius.components.annotations.spring.graphql.SubscriptionDataFetcher;
import org.eclipse.sirius.components.collaborative.validation.api.IValidationEventProcessor;
import org.eclipse.sirius.components.collaborative.validation.api.ValidationConfiguration;
import org.eclipse.sirius.components.collaborative.validation.dto.ValidationEventInput;
import org.eclipse.sirius.components.core.api.IPayload;
import org.eclipse.sirius.components.graphql.api.IDataFetcherWithFieldCoordinates;
import org.eclipse.sirius.components.graphql.api.IEventProcessorSubscriptionProvider;
import org.eclipse.sirius.components.graphql.api.IExceptionWrapper;
import org.reactivestreams.Publisher;

import graphql.schema.DataFetchingEnvironment;

/**
 * The data fetcher used to send the refreshed validation to a subscription.
 *
 * @author sbegaudeau
 */
@SubscriptionDataFetcher(type = "Subscription", field = "validationEvent")
public class SubscriptionValidationEventDataFetcher implements IDataFetcherWithFieldCoordinates<Publisher<IPayload>> {

    private static final String INPUT_ARGUMENT = "input"; //$NON-NLS-1$

    private final ObjectMapper objectMapper;

    private final IExceptionWrapper exceptionWrapper;

    private final IEventProcessorSubscriptionProvider eventProcessorSubscriptionProvider;

    public SubscriptionValidationEventDataFetcher(ObjectMapper objectMapper, IExceptionWrapper exceptionWrapper, IEventProcessorSubscriptionProvider eventProcessorSubscriptionProvider) {
        this.objectMapper = Objects.requireNonNull(objectMapper);
        this.exceptionWrapper = Objects.requireNonNull(exceptionWrapper);
        this.eventProcessorSubscriptionProvider = Objects.requireNonNull(eventProcessorSubscriptionProvider);
    }

    @Override
    public Publisher<IPayload> get(DataFetchingEnvironment environment) throws Exception {
        Object argument = environment.getArgument(INPUT_ARGUMENT);
        var input = this.objectMapper.convertValue(argument, ValidationEventInput.class);
        var validationConfiguration = new ValidationConfiguration(input.getEditingContextId());

        return this.exceptionWrapper
                .wrapFlux(() -> this.eventProcessorSubscriptionProvider.getSubscription(input.getEditingContextId(), IValidationEventProcessor.class, validationConfiguration, input), input);
    }
}
