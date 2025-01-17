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
package org.eclipse.sirius.components.selection.description;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import org.eclipse.sirius.components.annotations.Immutable;
import org.eclipse.sirius.components.representations.IRepresentationDescription;
import org.eclipse.sirius.components.representations.VariableManager;

/**
 * The root concept of the description of a selection representation.
 *
 * @author arichard
 */
@Immutable
public final class SelectionDescription implements IRepresentationDescription {

    private String id;

    private String label;

    private Function<VariableManager, String> idProvider;

    private Function<VariableManager, String> labelProvider;

    private Function<VariableManager, String> targetObjectIdProvider;

    private Function<VariableManager, String> iconURLProvider;

    private Function<VariableManager, String> messageProvider;

    private Function<VariableManager, List<?>> objectsProvider;

    private Function<VariableManager, String> selectionObjectsIdProvider;

    private Predicate<VariableManager> canCreatePredicate;

    private SelectionDescription() {
        // Prevent instantiation
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    public Function<VariableManager, String> getIdProvider() {
        return this.idProvider;
    }

    public Function<VariableManager, String> getLabelProvider() {
        return this.labelProvider;
    }

    public Function<VariableManager, String> getTargetObjectIdProvider() {
        return this.targetObjectIdProvider;
    }

    public Function<VariableManager, String> getIconURLProvider() {
        return this.iconURLProvider;
    }

    public Function<VariableManager, String> getMessageProvider() {
        return this.messageProvider;
    }

    public Function<VariableManager, List<?>> getObjectsProvider() {
        return this.objectsProvider;
    }

    public Function<VariableManager, String> getSelectionObjectsIdProvider() {
        return this.selectionObjectsIdProvider;
    }

    @Override
    public Predicate<VariableManager> getCanCreatePredicate() {
        return this.canCreatePredicate;
    }

    public static Builder newSelectionDescription(String id) {
        return new Builder(id);
    }

    @Override
    public String toString() {
        String pattern = "{0} '{'id: {1}, label: {2}'}'"; //$NON-NLS-1$
        return MessageFormat.format(pattern, this.getClass().getSimpleName(), this.id, this.label);
    }

    /**
     * Builder used to create the selection description.
     *
     * @author arichard
     */
    @SuppressWarnings("checkstyle:HiddenField")
    public static final class Builder {
        private String id;

        private String label;

        private Function<VariableManager, String> idProvider;

        private Function<VariableManager, String> labelProvider;

        private Function<VariableManager, String> targetObjectIdProvider;

        private Function<VariableManager, String> iconURLProvider;

        private Function<VariableManager, String> messageProvider;

        private Function<VariableManager, List<?>> objectsProvider;

        private Function<VariableManager, String> selectionObjectsIdProvider;

        private Predicate<VariableManager> canCreatePredicate;

        private Builder(String id) {
            this.id = Objects.requireNonNull(id);
        }

        public Builder label(String label) {
            this.label = Objects.requireNonNull(label);
            return this;
        }

        public Builder idProvider(Function<VariableManager, String> idProvider) {
            this.idProvider = Objects.requireNonNull(idProvider);
            return this;
        }

        public Builder labelProvider(Function<VariableManager, String> labelProvider) {
            this.labelProvider = Objects.requireNonNull(labelProvider);
            return this;
        }

        public Builder targetObjectIdProvider(Function<VariableManager, String> targetObjectIdProvider) {
            this.targetObjectIdProvider = Objects.requireNonNull(targetObjectIdProvider);
            return this;
        }

        public Builder iconURLProvider(Function<VariableManager, String> iconURLProvider) {
            this.iconURLProvider = Objects.requireNonNull(iconURLProvider);
            return this;
        }

        public Builder messageProvider(Function<VariableManager, String> messageProvider) {
            this.messageProvider = Objects.requireNonNull(messageProvider);
            return this;
        }

        public Builder objectsProvider(Function<VariableManager, List<?>> objectsProvider) {
            this.objectsProvider = Objects.requireNonNull(objectsProvider);
            return this;
        }

        public Builder selectionObjectsIdProvider(Function<VariableManager, String> selectionObjectsIdProvider) {
            this.selectionObjectsIdProvider = Objects.requireNonNull(selectionObjectsIdProvider);
            return this;
        }

        public Builder canCreatePredicate(Predicate<VariableManager> canCreatePredicate) {
            this.canCreatePredicate = Objects.requireNonNull(canCreatePredicate);
            return this;
        }

        public SelectionDescription build() {
            SelectionDescription selectionDescription = new SelectionDescription();
            selectionDescription.id = Objects.requireNonNull(this.id);
            selectionDescription.label = Objects.requireNonNull(this.label);
            selectionDescription.idProvider = Objects.requireNonNull(this.idProvider);
            selectionDescription.labelProvider = Objects.requireNonNull(this.labelProvider);
            selectionDescription.targetObjectIdProvider = Objects.requireNonNull(this.targetObjectIdProvider);
            selectionDescription.iconURLProvider = Objects.requireNonNull(this.iconURLProvider);
            selectionDescription.messageProvider = Objects.requireNonNull(this.messageProvider);
            selectionDescription.objectsProvider = Objects.requireNonNull(this.objectsProvider);
            selectionDescription.selectionObjectsIdProvider = Objects.requireNonNull(this.selectionObjectsIdProvider);
            selectionDescription.canCreatePredicate = Objects.requireNonNull(this.canCreatePredicate);
            return selectionDescription;
        }

    }
}
