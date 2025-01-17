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
import { Selection } from '@eclipse-sirius/sirius-components-core';
import { v4 as uuid } from 'uuid';
import { assign, Machine } from 'xstate';
import {
  GQLForm,
  GQLFormRefreshedEventPayload,
  GQLPropertiesEventPayload,
  GQLSubscriber,
  GQLSubscribersUpdatedEventPayload,
  GQLWidgetSubscription,
  GQLWidgetSubscriptionsUpdatedEventPayload,
} from '../form/FormEventFragments.types';

export interface FormBasedViewStateSchema {
  states: {
    toast: {
      states: {
        visible: {};
        hidden: {};
      };
    };
    formBasedView: {
      states: {
        empty: {};
        unsupportedSelection: {};
        idle: {};
        ready: {};
        complete: {};
      };
    };
  };
}

export type SchemaValue = {
  toast: 'visible' | 'hidden';
  formBasedView: 'empty' | 'unsupportedSelection' | 'idle' | 'ready' | 'complete';
};

export interface FormBasedViewContext {
  id: string;
  currentSelection: Selection | null;
  form: GQLForm | null;
  subscribers: GQLSubscriber[];
  widgetSubscriptions: GQLWidgetSubscription[];
  message: string | null;
}

export type ShowToastEvent = { type: 'SHOW_TOAST'; message: string };
export type HideToastEvent = { type: 'HIDE_TOAST' };
export type SwitchSelectionEvent = {
  type: 'SWITCH_SELECTION';
  selection: Selection | null;
};
export type HandleSubscriptionResultEvent = {
  type: 'HANDLE_SUBSCRIPTION_RESULT';
  result: GQLPropertiesEventPayload;
};
export type HandleCompleteEvent = { type: 'HANDLE_COMPLETE' };
export type FormBasedViewEvent =
  | SwitchSelectionEvent
  | HandleSubscriptionResultEvent
  | HandleCompleteEvent
  | ShowToastEvent
  | HideToastEvent;

const isFormRefreshedEventPayload = (payload: GQLPropertiesEventPayload): payload is GQLFormRefreshedEventPayload =>
  payload.__typename === 'FormRefreshedEventPayload';
const isSubscribersUpdatedEventPayload = (
  payload: GQLPropertiesEventPayload
): payload is GQLSubscribersUpdatedEventPayload => payload.__typename === 'SubscribersUpdatedEventPayload';
const isWidgetSubscriptionsUpdatedEventPayload = (
  payload: GQLPropertiesEventPayload
): payload is GQLWidgetSubscriptionsUpdatedEventPayload =>
  payload.__typename == 'WidgetSubscriptionsUpdatedEventPayload';

export const formBasedViewMachine = Machine<FormBasedViewContext, FormBasedViewStateSchema, FormBasedViewEvent>(
  {
    type: 'parallel',
    context: {
      id: uuid(),
      currentSelection: null,
      form: null,
      subscribers: [],
      widgetSubscriptions: [],
      message: null,
    },
    states: {
      toast: {
        initial: 'hidden',
        states: {
          hidden: {
            on: {
              SHOW_TOAST: {
                target: 'visible',
                actions: 'setMessage',
              },
            },
          },
          visible: {
            on: {
              HIDE_TOAST: {
                target: 'hidden',
                actions: 'clearMessage',
              },
            },
          },
        },
      },
      formBasedView: {
        initial: 'empty',
        states: {
          empty: {
            on: {
              SWITCH_SELECTION: [
                {
                  cond: 'isSelectionUnsupported',
                  target: 'unsupportedSelection',
                  actions: ['switchSelection', 'clearForm'],
                },
                {
                  target: 'idle',
                  actions: 'switchSelection',
                },
              ],
            },
          },
          unsupportedSelection: {
            on: {
              SWITCH_SELECTION: [
                {
                  cond: 'isSelectionUnsupported',
                  target: 'unsupportedSelection',
                  actions: ['switchSelection', 'clearForm'],
                },
                {
                  target: 'idle',
                  actions: 'switchSelection',
                },
              ],
            },
          },
          idle: {
            on: {
              SWITCH_SELECTION: [
                {
                  cond: 'isSelectionUnsupported',
                  target: 'unsupportedSelection',
                  actions: ['switchSelection', 'clearForm'],
                },
                {
                  target: 'idle',
                  actions: 'switchSelection',
                },
              ],
              HANDLE_SUBSCRIPTION_RESULT: [
                {
                  cond: 'isFormRefreshedEventPayload',
                  target: 'ready',
                  actions: 'handleSubscriptionResult',
                },
                {
                  target: 'idle',
                  actions: 'handleSubscriptionResult',
                },
              ],
              HANDLE_COMPLETE: {
                target: 'complete',
              },
            },
          },
          ready: {
            on: {
              SWITCH_SELECTION: [
                {
                  cond: 'isSelectionUnsupported',
                  target: 'unsupportedSelection',
                  actions: ['switchSelection', 'clearForm'],
                },
                {
                  target: 'idle',
                  actions: 'switchSelection',
                },
              ],
              HANDLE_SUBSCRIPTION_RESULT: {
                target: 'ready',
                actions: 'handleSubscriptionResult',
              },
              HANDLE_COMPLETE: {
                target: 'complete',
              },
            },
          },
          complete: {
            on: {
              SWITCH_SELECTION: [
                {
                  cond: 'isSelectionUnsupported',
                  target: 'unsupportedSelection',
                  actions: ['switchSelection', 'clearForm'],
                },
                {
                  target: 'idle',
                  actions: 'switchSelection',
                },
              ],
            },
          },
        },
      },
    },
  },
  {
    guards: {
      isFormRefreshedEventPayload: (_, event) => {
        const { result } = event as HandleSubscriptionResultEvent;
        return isFormRefreshedEventPayload(result);
      },
      isSelectionUnsupported: (_, event) => {
        const { selection } = event as SwitchSelectionEvent;
        return !selection;
      },
    },
    actions: {
      switchSelection: assign((_, event) => {
        const { selection } = event as SwitchSelectionEvent;
        return { id: uuid(), currentSelection: selection };
      }),
      clearForm: assign((_, event) => {
        return { form: null };
      }),
      handleSubscriptionResult: assign((_, event) => {
        const { result } = event as HandleSubscriptionResultEvent;
        if (isFormRefreshedEventPayload(result)) {
          const { form } = result;
          return { form };
        } else if (isSubscribersUpdatedEventPayload(result)) {
          const { subscribers } = result;
          return { subscribers };
        } else if (isWidgetSubscriptionsUpdatedEventPayload(result)) {
          const { widgetSubscriptions } = result;
          return { widgetSubscriptions };
        }
        return {};
      }),
      setMessage: assign((_, event) => {
        const { message } = event as ShowToastEvent;
        return { message };
      }),
      clearMessage: assign((_) => {
        return { message: null };
      }),
    },
  }
);
