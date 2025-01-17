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
import { gql, useMutation } from '@apollo/client';
import { ServerContext } from '@eclipse-sirius/sirius-components-core';
import FormControl from '@material-ui/core/FormControl';
import FormHelperText from '@material-ui/core/FormHelperText';
import IconButton from '@material-ui/core/IconButton';
import Snackbar from '@material-ui/core/Snackbar';
import { makeStyles, Theme } from '@material-ui/core/styles';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableRow from '@material-ui/core/TableRow';
import Typography from '@material-ui/core/Typography';
import CloseIcon from '@material-ui/icons/Close';
import DeleteIcon from '@material-ui/icons/Delete';
import { MouseEvent, useContext, useEffect, useRef, useState } from 'react';
import { v4 as uuid } from 'uuid';
import { GQLListItem } from '../form/FormEventFragments.types';
import { getTextDecorationLineValue } from './getTextDecorationLineValue';
import {
  GQLClickListItemMutationData,
  GQLClickListItemMutationVariables,
  GQLDeleteListItemMutationData,
  GQLDeleteListItemPayload,
  GQLErrorPayload,
  ListPropertySectionProps,
} from './ListPropertySection.types';
import { PropertySectionLabel } from './PropertySectionLabel';

export const deleteListItemMutation = gql`
  mutation deleteListItem($input: DeleteListItemInput!) {
    deleteListItem(input: $input) {
      __typename
      ... on ErrorPayload {
        message
      }
    }
  }
`;

export const clickListItemMutation = gql`
  mutation clickListItem($input: ClickListItemInput!) {
    clickListItem(input: $input) {
      __typename
      ... on ErrorPayload {
        message
      }
    }
  }
`;
interface StyleProps {
  color: string | null;
  fontSize: number | null;
  italic: boolean | null;
  bold: boolean | null;
  underline: boolean | null;
  strikeThrough: boolean | null;
}
const useListPropertySectionStyles = makeStyles<Theme, StyleProps>((theme) => ({
  cell: {
    display: 'flex',
    flexDirection: 'row',
    alignItems: 'center',
  },
  icon: {
    width: '16px',
    height: '16px',
    marginRight: theme.spacing(2),
  },
  canBeSelectedItem: {
    '&:hover': {
      textDecoration: 'underline',
      cursor: 'pointer',
    },
  },
  style: {
    color: ({ color }) => (color ? color : 'inherit'),
    fontSize: ({ fontSize }) => (fontSize ? fontSize : 'inherit'),
    fontStyle: ({ italic }) => (italic ? 'italic' : 'inherit'),
    fontWeight: ({ bold }) => (bold ? 'bold' : 'inherit'),
    textDecorationLine: ({ underline, strikeThrough }) => getTextDecorationLineValue(underline, strikeThrough),
  },
}));

const NONE_WIDGET_ITEM_ID = 'none';

const isErrorPayload = (payload: GQLDeleteListItemPayload): payload is GQLErrorPayload =>
  payload.__typename === 'ErrorPayload';

export function useClickHandler<T>(
  onSimpleClick: (element: T) => void,
  onDoubleClick: (element: T) => void,
  delay: number = 250
): (element: T) => void {
  const eventRef = useRef<T | null>(null);
  const [clicks, setClicks] = useState(0);
  useEffect(() => {
    let singleClickTimer;
    if (clicks === 1) {
      singleClickTimer = setTimeout(function () {
        onSimpleClick?.(eventRef.current);
        setClicks(0);
        eventRef.current = null;
      }, delay);
    } else if (clicks === 2) {
      onDoubleClick?.(eventRef.current);
      eventRef.current = null;
      setClicks(0);
    }
    return () => clearTimeout(singleClickTimer);
  }, [clicks, onSimpleClick, onDoubleClick, delay]);
  return (element) => {
    eventRef.current = element;
    setClicks(clicks + 1);
  };
}

export const ListPropertySection = ({
  editingContextId,
  formId,
  widget,
  subscribers,
  readOnly,
  setSelection,
}: ListPropertySectionProps) => {
  const props: StyleProps = {
    color: widget.style?.color ?? null,
    fontSize: widget.style?.fontSize ?? null,
    italic: widget.style?.italic ?? null,
    bold: widget.style?.bold ?? null,
    underline: widget.style?.underline ?? null,
    strikeThrough: widget.style?.strikeThrough ?? null,
  };
  const classes = useListPropertySectionStyles(props);
  const [message, setMessage] = useState<string | null>(null);
  const { httpOrigin } = useContext(ServerContext);

  let items = [...widget.items];
  if (items.length === 0) {
    items.push({
      id: NONE_WIDGET_ITEM_ID,
      imageURL: '',
      label: 'None',
      kind: 'Unknown',
      deletable: false,
    });
  }

  const [deleteListItem, { loading: deleteLoading, error: deleteError, data: deleteData }] =
    useMutation<GQLDeleteListItemMutationData>(deleteListItemMutation);

  const [clickListItem, { loading: clickLoading, error: clickError, data: clickData }] = useMutation<
    GQLClickListItemMutationData,
    GQLClickListItemMutationVariables
  >(clickListItemMutation);

  const onDelete = (_: MouseEvent<HTMLElement>, item: GQLListItem) => {
    const variables = {
      input: {
        id: uuid(),
        editingContextId,
        representationId: formId,
        listId: widget.id,
        listItemId: item.id,
      },
    };
    deleteListItem({ variables });
  };

  useEffect(() => {
    if (!deleteLoading) {
      if (deleteError) {
        setMessage('An unexpected error has occurred, please refresh the page');
      }
      if (deleteData) {
        const { deleteListItem } = deleteData;
        if (isErrorPayload(deleteListItem)) {
          setMessage(deleteListItem.message);
        }
      }
    }
  }, [deleteLoading, deleteError, deleteData]);

  useEffect(() => {
    if (!clickLoading) {
      if (clickError) {
        setMessage('An unexpected error has occurred, please refresh the page');
      }
      if (clickData) {
        const { clickListItem } = clickData;
        if (isErrorPayload(clickListItem)) {
          setMessage(clickListItem.message);
        }
      }
    }
  }, [clickLoading, clickError, clickData]);
  const onSimpleClick = (item: GQLListItem) => {
    const { id, label, kind } = item;
    setSelection({ entries: [{ id, label, kind }] });
    const variables: GQLClickListItemMutationVariables = {
      input: {
        id: uuid(),
        editingContextId,
        representationId: formId,
        listId: widget.id,
        listItemId: item.id,
        clickEventKind: 'SINGLE_CLICK',
      },
    };

    clickListItem({ variables });
  };
  const onDoubleClick = (item: GQLListItem) => {
    const { id, label, kind } = item;
    setSelection({ entries: [{ id, label, kind }] });
    const variables: GQLClickListItemMutationVariables = {
      input: {
        id: uuid(),
        editingContextId,
        representationId: formId,
        listId: widget.id,
        listItemId: item.id,
        clickEventKind: 'DOUBLE_CLICK',
      },
    };
    clickListItem({ variables });
  };

  const clickHandler = useClickHandler<GQLListItem>(onSimpleClick, onDoubleClick);

  const getTableCellContent = (item: GQLListItem): JSX.Element => {
    return (
      <Typography
        className={`${classes.canBeSelectedItem} ${classes.style}`}
        onClick={() => clickHandler(item)}
        color="textPrimary"
        data-testid={`representation-${item.id}`}>
        {item.imageURL ? (
          <img className={classes.icon} width="16" height="16" alt={item.label} src={httpOrigin + item.imageURL} />
        ) : null}
        {item.label}
      </Typography>
    );
  };

  return (
    <FormControl error={widget.diagnostics.length > 0} fullWidth>
      <PropertySectionLabel label={widget.label} subscribers={subscribers} />
      <Table size="small">
        <TableBody>
          {items.map((item) => (
            <TableRow key={item.id}>
              <TableCell>{getTableCellContent(item)}</TableCell>
              <TableCell align="right">
                <IconButton
                  aria-label="deleteListItem"
                  onClick={(event) => onDelete(event, item)}
                  disabled={readOnly || !item.deletable}
                  data-testid={`delete-representation-${item.id}`}>
                  <DeleteIcon />
                </IconButton>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
      <FormHelperText>{widget.diagnostics[0]?.message}</FormHelperText>
      <Snackbar
        anchorOrigin={{
          vertical: 'bottom',
          horizontal: 'right',
        }}
        open={!!message}
        autoHideDuration={3000}
        onClose={() => setMessage(null)}
        message={message}
        action={
          <IconButton size="small" aria-label="close" color="inherit" onClick={() => setMessage(null)}>
            <CloseIcon fontSize="small" />
          </IconButton>
        }
        data-testid="error"
      />
    </FormControl>
  );
};
