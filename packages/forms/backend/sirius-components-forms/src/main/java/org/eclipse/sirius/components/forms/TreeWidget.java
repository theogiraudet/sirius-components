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
package org.eclipse.sirius.components.forms;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

import org.eclipse.sirius.components.annotations.Immutable;
import org.eclipse.sirius.components.forms.validation.Diagnostic;

/**
 * The Tree widget.
 *
 * @author pcdavid
 */
@Immutable
public final class TreeWidget extends AbstractWidget {
    private String label;

    private List<TreeNode> nodes;

    private List<String> expandedNodesIds;

    private TreeWidget() {
        // Prevent instantiation
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    public List<TreeNode> getNodes() {
        return this.nodes;
    }

    public List<String> getExpandedNodesIds() {
        return this.expandedNodesIds;
    }

    public static Builder newTreeWidget(String id) {
        return new Builder(id);
    }

    @Override
    public String toString() {
        String pattern = "{0} '{'id: {1}, label: {2}, nodeCount: {3}'}'"; //$NON-NLS-1$
        return MessageFormat.format(pattern, this.getClass().getSimpleName(), this.getId(), this.label, this.nodes.size());
    }

    /**
     * The builder used to create the tree.
     *
     * @author pcdavid
     */
    @SuppressWarnings("checkstyle:HiddenField")
    public static final class Builder {
        private String id;

        private String label;

        private String iconURL;

        private List<TreeNode> nodes;

        private List<String> expandedNodesIds;

        private List<Diagnostic> diagnostics;

        private Builder(String id) {
            this.id = Objects.requireNonNull(id);
        }

        public Builder label(String label) {
            this.label = Objects.requireNonNull(label);
            return this;
        }

        public Builder iconURL(String iconURL) {
            this.iconURL = iconURL;
            return this;
        }

        public Builder nodes(List<TreeNode> nodes) {
            this.nodes = Objects.requireNonNull(nodes);
            return this;
        }

        public Builder expandedNodesIds(List<String> expandedIds) {
            this.expandedNodesIds = Objects.requireNonNull(expandedIds);
            return this;
        }

        public Builder diagnostics(List<Diagnostic> diagnostics) {
            this.diagnostics = Objects.requireNonNull(diagnostics);
            return this;
        }

        public TreeWidget build() {
            TreeWidget treeWidget = new TreeWidget();
            treeWidget.id = Objects.requireNonNull(this.id);
            treeWidget.label = Objects.requireNonNull(this.label);
            treeWidget.iconURL = this.iconURL;
            treeWidget.nodes = Objects.requireNonNull(this.nodes);
            treeWidget.expandedNodesIds = Objects.requireNonNull(this.expandedNodesIds);
            treeWidget.diagnostics = Objects.requireNonNull(this.diagnostics);
            return treeWidget;
        }
    }
}
