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

import { gql, useMutation } from '@apollo/client';
import IconButton from '@material-ui/core/IconButton';
import Snackbar from '@material-ui/core/Snackbar';
import CloseIcon from '@material-ui/icons/Close';
import { useMachine } from '@xstate/react';
import { useEffect } from 'react';
import { v4 as uuid } from 'uuid';
import { RichTextEditor } from '../richtexteditor/RichTextEditor';
import { PropertySectionLabel } from './PropertySectionLabel';
import {
  GQLEditRichTextInput,
  GQLEditRichTextMutationData,
  GQLEditRichTextMutationVariables,
  GQLEditRichTextPayload,
  GQLErrorPayload,
  GQLUpdateWidgetFocusInput,
  GQLUpdateWidgetFocusMutationData,
  GQLUpdateWidgetFocusMutationVariables,
  GQLUpdateWidgetFocusPayload,
  RichTextPropertySectionProps,
} from './RichTextPropertySection.types';
import {
  ChangeValueEvent,
  InitializeEvent,
  RichTextPropertySectionContext,
  RichTextPropertySectionEvent,
  RichTextPropertySectionMachine,
  SchemaValue,
  ShowToastEvent,
} from './RichTextPropertySectionMachine';

export const editRichTextMutation = gql`
  mutation editRichText($input: EditRichTextInput!) {
    editRichText(input: $input) {
      __typename
      ... on ErrorPayload {
        message
      }
    }
  }
`;

export const updateWidgetFocusMutation = gql`
  mutation updateWidgetFocus($input: UpdateWidgetFocusInput!) {
    updateWidgetFocus(input: $input) {
      __typename
      ... on ErrorPayload {
        message
      }
    }
  }
`;

const isErrorPayload = (payload: GQLEditRichTextPayload | GQLUpdateWidgetFocusPayload): payload is GQLErrorPayload =>
  payload.__typename === 'ErrorPayload';

/**
 * Defines the content of a Rich Text property section.
 * The content is submitted when the focus is lost.
 */
export const RichTextPropertySection = ({
  editingContextId,
  formId,
  widget,
  subscribers,
  readOnly,
}: RichTextPropertySectionProps) => {
  const [{ value: schemaValue, context }, dispatch] = useMachine<
    RichTextPropertySectionContext,
    RichTextPropertySectionEvent
  >(RichTextPropertySectionMachine);
  const { toast } = schemaValue as SchemaValue;
  const { value, message } = context;

  useEffect(() => {
    const initializeEvent: InitializeEvent = { type: 'INITIALIZE', value: widget.stringValue };
    dispatch(initializeEvent);
  }, [dispatch, widget.stringValue]);

  const onChange = (newText: string) => {
    const changeValueEvent: ChangeValueEvent = { type: 'CHANGE_VALUE', value: newText };
    dispatch(changeValueEvent);
  };

  const [editRichText, { loading: updateRichTextLoading, data: updateRichTextData, error: updateRichTextError }] =
    useMutation<GQLEditRichTextMutationData, GQLEditRichTextMutationVariables>(editRichTextMutation);
  const sendEditedValue = (newValue) => {
    const input: GQLEditRichTextInput = {
      id: uuid(),
      editingContextId,
      representationId: formId,
      richTextId: widget.id,
      newValue: newValue,
    };
    const variables: GQLEditRichTextMutationVariables = { input };
    editRichText({ variables });
  };

  useEffect(() => {
    if (!updateRichTextLoading) {
      let hasError = false;
      if (updateRichTextError) {
        const showToastEvent: ShowToastEvent = {
          type: 'SHOW_TOAST',
          message: 'An unexpected error has occurred, please refresh the page',
        };
        dispatch(showToastEvent);

        hasError = true;
      }
      if (updateRichTextData) {
        const { editRichText } = updateRichTextData;
        if (isErrorPayload(editRichText)) {
          const { message } = editRichText;
          const showToastEvent: ShowToastEvent = { type: 'SHOW_TOAST', message };
          dispatch(showToastEvent);

          hasError = true;
        }
      }

      if (hasError) {
        const initializeEvent: InitializeEvent = { type: 'INITIALIZE', value: widget.stringValue };
        dispatch(initializeEvent);
      }
    }
  }, [updateRichTextLoading, updateRichTextData, updateRichTextError, widget, dispatch]);

  const [
    updateWidgetFocus,
    { loading: updateWidgetFocusLoading, data: updateWidgetFocusData, error: updateWidgetFocusError },
  ] = useMutation<GQLUpdateWidgetFocusMutationData, GQLUpdateWidgetFocusMutationVariables>(updateWidgetFocusMutation);
  const sendUpdateWidgetFocus = (selected: boolean) => {
    const input: GQLUpdateWidgetFocusInput = {
      id: uuid(),
      editingContextId,
      representationId: formId,
      widgetId: widget.id,
      selected,
    };
    const variables: GQLUpdateWidgetFocusMutationVariables = {
      input,
    };
    updateWidgetFocus({ variables });
  };

  useEffect(() => {
    if (!updateWidgetFocusLoading) {
      if (updateWidgetFocusError) {
        const showToastEvent: ShowToastEvent = {
          type: 'SHOW_TOAST',
          message: 'An unexpected error has occurred, please refresh the page',
        };
        dispatch(showToastEvent);
      }
      if (updateWidgetFocusData) {
        const { updateWidgetFocus } = updateWidgetFocusData;
        if (isErrorPayload(updateWidgetFocus)) {
          const { message } = updateWidgetFocus;
          const showToastEvent: ShowToastEvent = { type: 'SHOW_TOAST', message };
          dispatch(showToastEvent);
        }
      }
    }
  }, [updateWidgetFocusLoading, updateWidgetFocusData, updateWidgetFocusError, dispatch]);

  const onFocus = () => sendUpdateWidgetFocus(true);
  const onSave = (newValue: string) => {
    sendUpdateWidgetFocus(false);
    sendEditedValue(newValue);
  };

  return (
    <div>
      <PropertySectionLabel label={widget.label} subscribers={subscribers} />
      <div data-testid={widget.label}>
        <RichTextEditor
          value={value}
          placeholder={widget.label}
          onChange={onChange}
          onFocus={onFocus}
          onSave={onSave}
          readOnly={readOnly}
        />
      </div>
      <Snackbar
        anchorOrigin={{
          vertical: 'bottom',
          horizontal: 'right',
        }}
        open={toast === 'visible'}
        autoHideDuration={3000}
        onClose={() => dispatch({ type: 'HIDE_TOAST' })}
        message={message}
        action={
          <IconButton size="small" aria-label="close" color="inherit" onClick={() => dispatch({ type: 'HIDE_TOAST' })}>
            <CloseIcon fontSize="small" />
          </IconButton>
        }
        data-testid="error"
      />
    </div>
  );
};
