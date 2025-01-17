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
package org.eclipse.sirius.web.graphql.datafetchers.mutation;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.eclipse.sirius.components.annotations.spring.graphql.MutationDataFetcher;
import org.eclipse.sirius.components.core.api.IPayload;
import org.eclipse.sirius.components.graphql.api.IDataFetcherWithFieldCoordinates;
import org.eclipse.sirius.web.graphql.schema.MutationTypeProvider;
import org.eclipse.sirius.web.services.api.images.DeleteImageInput;
import org.eclipse.sirius.web.services.api.images.DeleteImageSuccessPayload;
import org.eclipse.sirius.web.services.api.images.ICustomImageEditService;

import graphql.schema.DataFetchingEnvironment;

/**
 * The data fetcher used to delete a custom image.
 * <p>
 * It will be used to handle the following GraphQL field:
 * </p>
 *
 * <pre>
 * type Mutation {
 *   deleteImage(imageId: String!): DeleteImagePayload!
 * }
 * </pre>
 *
 * @author fbarbin
 */
@MutationDataFetcher(type = MutationTypeProvider.TYPE, field = MutationDeleteImageDataFetcher.DELETE_IMAGE_FIELD)
public class MutationDeleteImageDataFetcher implements IDataFetcherWithFieldCoordinates<CompletableFuture<IPayload>> {

    public static final String DELETE_IMAGE_FIELD = "deleteImage"; //$NON-NLS-1$

    private final ObjectMapper objectMapper;

    private final ICustomImageEditService customImageEditService;

    public MutationDeleteImageDataFetcher(ObjectMapper objectMapper, ICustomImageEditService customImageEditService) {
        this.objectMapper = Objects.requireNonNull(objectMapper);
        this.customImageEditService = Objects.requireNonNull(customImageEditService);
    }

    @Override
    public CompletableFuture<IPayload> get(DataFetchingEnvironment environment) throws Exception {
        Object argument = environment.getArgument(MutationTypeProvider.INPUT_ARGUMENT);
        var input = this.objectMapper.convertValue(argument, DeleteImageInput.class);
        this.customImageEditService.delete(input.getImageId());
        return CompletableFuture.completedFuture(new DeleteImageSuccessPayload(input.getId()));
    }
}
