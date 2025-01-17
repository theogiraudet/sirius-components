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
import { Selection } from '@eclipse-sirius/sirius-components-core';
import { GQLFormDescriptionEditorToolbarAction } from './FormDescriptionEditorEventFragment.types';

export interface ToolbarActionProps {
  editingContextId: string;
  representationId: string;
  siblings: GQLFormDescriptionEditorToolbarAction[];
  toolbarAction: GQLFormDescriptionEditorToolbarAction;
  selection: Selection;
  setSelection: (newSelection: Selection) => void;
}

export interface ToolbarActionState {
  message: string | null;
  selected: boolean;
}
