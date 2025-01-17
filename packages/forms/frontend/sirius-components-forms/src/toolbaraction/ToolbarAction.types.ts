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
import { GQLButton } from '../form/FormEventFragments.types';

export interface ToolbarActionProps {
  editingContextId: string;
  formId: string;
  widget: GQLButton;
  readOnly: boolean;
}

export interface ToolbarActionStyleProps {
  backgroundColor: string | null;
  foregroundColor: string | null;
  fontSize: number | null;
  italic: boolean | null;
  bold: boolean | null;
  underline: boolean | null;
  strikeThrough: boolean | null;
  iconOnly: boolean;
}

export interface GQLPushButtonMutationData {
  pushButton: GQLPushButtonPayload;
}

export interface GQLPushButtonMutationVariables {
  input: GQLPushButtonInput;
}

export interface GQLPushButtonInput {
  id: string;
  editingContextId: string;
  representationId: string;
  buttonId: string;
}

export interface GQLPushButtonPayload {
  __typename: string;
}

export interface GQLPushButtonSuccessPayload extends GQLPushButtonPayload {}

export interface GQLErrorPayload extends GQLPushButtonPayload, GQLUpdateWidgetFocusPayload {
  message: string;
}

export interface GQLUpdateWidgetFocusMutationVariables {
  input: GQLUpdateWidgetFocusInput;
}

export interface GQLUpdateWidgetFocusInput {
  id: string;
  editingContextId: string;
  representationId: string;
  widgetId: string;
  selected: boolean;
}

export interface GQLUpdateWidgetFocusMutationData {
  updateWidgetFocus: GQLUpdateWidgetFocusPayload;
}

export interface GQLUpdateWidgetFocusPayload {
  __typename: string;
}

export interface GQLUpdateWidgetFocusSuccessPayload extends GQLUpdateWidgetFocusPayload {}
