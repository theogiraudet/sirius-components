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
import { ListItemNode, ListNode } from '@lexical/list';
import { $convertFromMarkdownString, TRANSFORMERS } from '@lexical/markdown';
import { LexicalComposer } from '@lexical/react/LexicalComposer';
import { useLexicalComposerContext } from '@lexical/react/LexicalComposerContext';
import { MarkdownShortcutPlugin } from '@lexical/react/LexicalMarkdownShortcutPlugin';
import { RichTextPlugin } from '@lexical/react/LexicalRichTextPlugin';
import { HeadingNode, QuoteNode } from '@lexical/rich-text';
import { makeStyles } from '@material-ui/core/styles';
import { useCallback, useEffect } from 'react';
import { ListPlugin } from './ListPlugin';
//import { ListPlugin } from '@lexical/react/LexicalListPlugin';
import { RichTextEditorProps } from './RichTextEditor.types';
import { ToolbarPlugin } from './ToolbarPlugin';

interface ContentEditableProps {
  onFocus: () => void;
  readOnly: boolean;
}

/**
 * A content-editable div managed by lexical, but which also invokes our onFocus callback.
 */
const ContentEditable = ({ onFocus, readOnly }: ContentEditableProps): JSX.Element => {
  const [editor] = useLexicalComposerContext();
  const ref = useCallback(
    (rootElement: null | HTMLElement) => {
      editor.setRootElement(rootElement);
    },
    [editor]
  );

  return <div ref={ref} contentEditable={!readOnly} spellCheck={false} onFocus={onFocus} />;
};

/**
 * Updates the editor's content when we get a new value for the widget's text.
 */
const ValueUpdater = ({ value }: { value: string }): JSX.Element | null => {
  const [editor] = useLexicalComposerContext();
  useEffect(() => {
    editor.update(() => {
      console.log(`ValueUpdater.convertFromMarkdownString(${value})`);
      $convertFromMarkdownString(value, TRANSFORMERS);
    });
  }, [editor, value]);
  return null;
};

const useRichTextEditorStyles = makeStyles((theme) => ({
  editorContainer: {
    marginTop: theme.spacing(2),
    borderRadius: '2px',
    borderColor: theme.palette.divider,
    borderWidth: '1px',
    borderStyle: 'solid',
    color: '#000',
    position: 'relative',
    //lineHeight: theme.typography.fontSize * 1.4,
    fontWeight: Number(theme.typography.fontWeightRegular),
    textAlign: 'left',
  },
  editorPlaceholder: {
    color: theme.palette.grey[700],
    overflow: 'hidden',
    position: 'absolute',
    textOverflow: 'ellipsis',
    fontSize: theme.typography.fontSize,
    userSelect: 'none',
    display: 'inline-block',
    pointerEvents: 'none',
  },
  editorParagraph: {
    margin: 0,
    marginBottom: theme.spacing(2),
    position: 'relative',
  },
  editorTextBold: {
    fontWeight: 'bold',
  },
  editorTextItalic: {
    fontStyle: 'italic',
  },
  editorTextUnderline: {
    textDecoration: 'underline',
  },
  editorTextStrikethrough: {
    textDecoration: 'line-through',
  },
  editorTextUnderlineStrikethrough: {
    textDecoration: 'underline line-through',
  },
  editorTextCode: {
    backgroundColor: theme.palette.grey[200],
    padding: '1px 0.25rem',
    fontFamily: 'Menlo, Consolas, Monaco, monospace',
    fontSize: '94%',
  },
  editorHeading1: {
    fontFamily: theme.typography.h4.fontFamily,
    fontSize: theme.typography.h4.fontSize,
    fontWeight: theme.typography.h4.fontWeight,
    lineHeight: theme.typography.h4.lineHeight,
    letterSpacing: theme.typography.h4.letterSpacing,
    color: theme.palette.text.primary,
    margin: 0,
    marginBottom: theme.spacing(3),
    padding: 0,
  },
  editorHeading2: {
    fontFamily: theme.typography.h5.fontFamily,
    fontSize: theme.typography.h5.fontSize,
    fontWeight: theme.typography.h5.fontWeight,
    lineHeight: theme.typography.h5.lineHeight,
    letterSpacing: theme.typography.h5.letterSpacing,
    color: theme.palette.text.secondary,
    margin: 0,
    marginTop: theme.spacing(2),
    padding: 0,
  },
  editorListOl: {
    padding: 0,
    margin: 0,
    marginLeft: theme.spacing(2),
    listStyle: 'decimal',
  },
  editorListUl: {
    padding: 0,
    margin: 0,
    marginLeft: theme.spacing(2),
    listStyle: 'circle',
  },
  editorListitem: {
    margin: `${theme.spacing(2)} ${theme.spacing(8)} ${theme.spacing(2)} ${theme.spacing(8)}`,
  },
  editorNestedListitem: {
    listStyleType: 'none',
  },
}));

export const RichTextEditor = ({ value, pristine, placeholder, readOnly, onFocus, onSave }: RichTextEditorProps) => {
  const classes = useRichTextEditorStyles();
  const theme = {
    placeholder: classes.editorPlaceholder,
    paragraph: classes.editorParagraph,
    heading: {
      h1: classes.editorHeading1,
      h2: classes.editorHeading2,
    },
    list: {
      nested: {
        listitem: classes.editorNestedListitem,
      },
      ol: classes.editorListOl,
      ul: classes.editorListUl,
      listitem: classes.editorListitem,
    },
    text: {
      bold: classes.editorTextBold,
      italic: classes.editorTextItalic,
      underline: classes.editorTextUnderline,
      strikethrough: classes.editorTextStrikethrough,
      underlineStrikethrough: classes.editorTextUnderlineStrikethrough,
      code: classes.editorTextCode,
    },
  };
  const initialConfig = {
    namespace: 'RichTextEditor',
    onError: console.error,
    theme,
    nodes: [HeadingNode, ListNode, ListItemNode, QuoteNode],
    editorState: () => $convertFromMarkdownString(value, TRANSFORMERS),
  };
  console.log('RichTextEditor.render(pristine: ' + pristine + ', value: ' + JSON.stringify(value) + ')');

  return (
    <LexicalComposer initialConfig={initialConfig}>
      <ToolbarPlugin readOnly={readOnly} onSave={onSave} />
      <div className={classes.editorContainer}>
        <RichTextPlugin
          contentEditable={<ContentEditable onFocus={onFocus} readOnly={readOnly} />}
          placeholder={<div className={classes.editorPlaceholder}>{placeholder}</div>}
        />
        {pristine ? <ValueUpdater value={value} /> : null}
        <ListPlugin />
        <MarkdownShortcutPlugin transformers={TRANSFORMERS} />
      </div>
    </LexicalComposer>
  );
};
