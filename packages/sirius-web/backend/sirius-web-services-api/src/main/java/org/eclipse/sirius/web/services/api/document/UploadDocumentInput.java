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
package org.eclipse.sirius.web.services.api.document;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.UUID;

import org.eclipse.sirius.components.core.api.IInput;
import org.eclipse.sirius.components.graphql.api.UploadFile;

/**
 * The input object for the upload document mutation.
 *
 * @author hmarchadour
 */
public final class UploadDocumentInput implements IInput {

    private final UUID id;

    private final String editingContextId;

    private final UploadFile file;

    private final boolean checkProxies;

    public UploadDocumentInput(UUID id, String editingContextId, UploadFile file, boolean checkProxies) {
        this.id = Objects.requireNonNull(id);
        this.editingContextId = Objects.requireNonNull(editingContextId);
        this.file = Objects.requireNonNull(file);
        this.checkProxies = checkProxies;
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    public boolean isCheckProxies() {
        return this.checkProxies;
    }

    public String getEditingContextId() {
        return this.editingContextId;
    }

    public UploadFile getFile() {
        return this.file;
    }

    @Override
    public String toString() {
        String pattern = "{0} '{'id: {1}, editingContextId: {2}, file: '{'name: {3}'}''}'"; //$NON-NLS-1$
        return MessageFormat.format(pattern, this.getClass().getSimpleName(), this.id, this.editingContextId, this.file.getName());
    }

}
