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
import Button from '@material-ui/core/Button';
import { makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import { useEffect, useRef, useState } from 'react';
import { WidgetProps } from './WidgetEntry.types';

const useStyles = makeStyles((theme) => ({
  selected: {
    color: theme.palette.primary.main,
  },
}));

export const ButtonWidget = ({ widget, selection }: WidgetProps) => {
  const classes = useStyles();

  const [selected, setSelected] = useState<boolean>(false);

  const ref = useRef<HTMLInputElement | null>(null);

  useEffect(() => {
    if (ref.current && selection.entries.find((entry) => entry.id === widget.id)) {
      ref.current.focus();
      setSelected(true);
    } else {
      setSelected(false);
    }
  }, [selection, widget]);

  return (
    <div>
      <Typography variant="subtitle2" className={selected ? classes.selected : ''}>
        {widget.label}
      </Typography>
      <Button
        data-testid={widget.label}
        variant="contained"
        color="primary"
        onFocus={() => setSelected(true)}
        onBlur={() => setSelected(false)}
        ref={ref}>
        Lorem
      </Button>
    </div>
  );
};
