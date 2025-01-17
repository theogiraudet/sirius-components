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
package org.eclipse.sirius.components.view.emf.form;

import java.util.Objects;
import java.util.function.Function;

import org.eclipse.sirius.components.forms.LinkStyle;
import org.eclipse.sirius.components.forms.LinkStyle.Builder;
import org.eclipse.sirius.components.representations.VariableManager;
import org.eclipse.sirius.components.view.LinkDescriptionStyle;

/**
 * The style provider for the Link Description widget of the View DSL.
 *
 * @author fbarbin
 */
public class LinkStyleProvider implements Function<VariableManager, LinkStyle> {

    private final LinkDescriptionStyle viewStyle;

    public LinkStyleProvider(LinkDescriptionStyle viewStyle) {
        this.viewStyle = Objects.requireNonNull(viewStyle);
    }

    @Override
    public LinkStyle apply(VariableManager variableManager) {
        Builder linkStyleBuilder = LinkStyle.newLinkStyle();

        String color = this.viewStyle.getColor();
        if (color != null && !color.isBlank()) {
            linkStyleBuilder.color(color);
        }
        int fontSize = this.viewStyle.getFontSize();
        boolean italic = this.viewStyle.isItalic();
        boolean bold = this.viewStyle.isBold();
        boolean underline = this.viewStyle.isUnderline();
        boolean strikeThrough = this.viewStyle.isStrikeThrough();

        // @formatter:off
        return linkStyleBuilder
                .fontSize(fontSize)
                .italic(italic)
                .bold(bold)
                .underline(underline)
                .strikeThrough(strikeThrough)
                .build();
        // @formatter:on
    }

}
