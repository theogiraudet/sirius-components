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
package org.eclipse.sirius.components.collaborative.forms.dto;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.UUID;

import org.eclipse.sirius.components.core.api.IPayload;
import org.eclipse.sirius.components.forms.Form;

/**
 * Payload used to indicate that the properties has been refreshed.
 *
 * @author hmarchadour
 */
public final class PropertiesRefreshedEventPayload implements IPayload {
    private final UUID id;

    private final Form form;

    public PropertiesRefreshedEventPayload(UUID id, Form form) {
        this.id = Objects.requireNonNull(id);
        this.form = Objects.requireNonNull(form);
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    public Form getForm() {
        return this.form;
    }

    @Override
    public String toString() {
        String pattern = "{0} '{'id: {1}, form: '{'id: {2}, label: {3}'}''}'"; //$NON-NLS-1$
        return MessageFormat.format(pattern, this.getClass().getSimpleName(), this.id, this.form.getId(), this.form.getLabel());
    }
}