/*******************************************************************************
 * Copyright (c) 2021, 2022 Obeo and others.
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

import { gql, useSubscription } from '@apollo/client';
import { WorkbenchViewComponentProps } from '@eclipse-sirius/sirius-components-core';
import IconButton from '@material-ui/core/IconButton';
import Snackbar from '@material-ui/core/Snackbar';
import { makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import CloseIcon from '@material-ui/icons/Close';
import { useMachine } from '@xstate/react';
import { useEffect } from 'react';
import {
  formRefreshedEventPayloadFragment,
  subscribersUpdatedEventPayloadFragment,
  widgetSubscriptionsUpdatedEventPayloadFragment,
} from '../form/FormEventFragments';
import { ListPropertySection } from '../propertysections/ListPropertySection';
import {
  HandleCompleteEvent,
  HandleSubscriptionResultEvent,
  HideToastEvent,
  RepresentationsViewContext,
  RepresentationsViewEvent,
  representationsViewMachine,
  SchemaValue,
  ShowToastEvent,
  SwitchSelectionEvent,
} from './RepresentationsViewMachine';

const representationsEventSubscription = gql(`
  subscription representationsEvent($input: RepresentationsEventInput!) {
    representationsEvent(input: $input) {
      __typename
      ... on SubscribersUpdatedEventPayload {
        ...subscribersUpdatedEventPayloadFragment
      }
      ... on WidgetSubscriptionsUpdatedEventPayload {
        ...widgetSubscriptionsUpdatedEventPayloadFragment
      }
      ... on FormRefreshedEventPayload {
        ...formRefreshedEventPayloadFragment
      }
    }
  }
  ${subscribersUpdatedEventPayloadFragment}
  ${widgetSubscriptionsUpdatedEventPayloadFragment}
  ${formRefreshedEventPayloadFragment}
`);

const useRepresentationsViewStyles = makeStyles((theme) => ({
  content: {
    padding: theme.spacing(1),
  },
}));

export const RepresentationsView = ({
  editingContextId,
  selection,
  setSelection,
  readOnly,
}: WorkbenchViewComponentProps) => {
  const classes = useRepresentationsViewStyles();

  const [{ value, context }, dispatch] = useMachine<RepresentationsViewContext, RepresentationsViewEvent>(
    representationsViewMachine
  );

  const { toast, representationsView } = value as SchemaValue;
  const { id, currentSelection, formId, widget, subscribers, message } = context;

  useEffect(() => {
    if (selection.entries.length > 0 && selection.entries[0].id !== currentSelection?.id) {
      const switchSelectionEvent: SwitchSelectionEvent = {
        type: 'SWITCH_SELECTION',
        selection: selection.entries[0],
      };
      dispatch(switchSelectionEvent);
    }
  }, [currentSelection, selection, dispatch]);

  const { error } = useSubscription(representationsEventSubscription, {
    variables: {
      input: {
        id,
        editingContextId,
        objectId: currentSelection?.id,
      },
    },
    fetchPolicy: 'no-cache',
    skip: representationsView === 'empty' || representationsView === 'unsupportedSelection',
    onSubscriptionData: ({ subscriptionData }) => {
      const handleDataEvent: HandleSubscriptionResultEvent = {
        type: 'HANDLE_SUBSCRIPTION_RESULT',
        result: subscriptionData,
      };
      dispatch(handleDataEvent);
    },
    onSubscriptionComplete: () => {
      const completeEvent: HandleCompleteEvent = { type: 'HANDLE_COMPLETE' };
      dispatch(completeEvent);
    },
  });

  useEffect(() => {
    if (error) {
      const { message } = error;
      const showToastEvent: ShowToastEvent = { type: 'SHOW_TOAST', message };
      dispatch(showToastEvent);
    }
  }, [error, dispatch]);

  let content = null;
  if (!selection || representationsView === 'unsupportedSelection') {
    content = (
      <div className={classes.content}>
        <Typography variant="subtitle2">No object selected</Typography>
      </div>
    );
  }
  if ((representationsView === 'idle' && widget) || representationsView === 'ready') {
    content = (
      <div className={classes.content}>
        <ListPropertySection
          editingContextId={editingContextId}
          formId={formId}
          readOnly={readOnly}
          widget={widget}
          subscribers={subscribers}
          setSelection={setSelection}
        />
      </div>
    );
  }

  return (
    <>
      {content}
      <Snackbar
        anchorOrigin={{
          vertical: 'bottom',
          horizontal: 'right',
        }}
        open={toast === 'visible'}
        autoHideDuration={3000}
        onClose={() => dispatch({ type: 'HIDE_TOAST' } as HideToastEvent)}
        message={message}
        action={
          <IconButton
            size="small"
            aria-label="close"
            color="inherit"
            onClick={() => dispatch({ type: 'HIDE_TOAST' } as HideToastEvent)}>
            <CloseIcon fontSize="small" />
          </IconButton>
        }
        data-testid="error"
      />
    </>
  );
};
