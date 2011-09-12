/**
 * Metaphase Editor - WYSIWYG HTML Editor Component
 * Copyright (C) 2010  Rudolf Visagie
 * Full text of license can be found in com/metaphaseeditor/LICENSE.txt
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * The author can be contacted at metaphase.editor@gmail.com.
 */

package com.metaphaseeditor;

import com.metaphaseeditor.action.AddAttributesAction;
import com.metaphaseeditor.action.ClearFormattingAction;
import com.metaphaseeditor.action.DecreaseIndentAction;
import com.metaphaseeditor.action.FindReplaceAction;
import com.metaphaseeditor.action.FormatAction;
import com.metaphaseeditor.action.IncreaseIndentAction;
import com.metaphaseeditor.action.InsertHtmlAction;
import com.metaphaseeditor.action.InsertTextAction;
import com.metaphaseeditor.action.RemoveAttributesAction;
import com.metaphaseeditor.action.UnlinkAction;
import com.swabunga.spell.engine.SpellDictionary;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.swing.JTextComponentSpellChecker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.print.PrinterException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.zip.ZipInputStream;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import org.openflexo.toolbox.ImageIconResource;

/**
 *
 * @author Rudolf Visagie
 */
public class MetaphaseEditorPanel extends JPanel {

    public static final String SOURCE_PANEL_KEY = "SourcePanel";
    public static final String SOURCE_BUTTON_KEY = "SourcePanel.SourceButton";

    public static final String PAGE_PANEL_KEY = "PagePanel";
    public static final String OPEN_BUTTON_KEY = "PagePanel.OpenButton";
    public static final String SAVE_BUTTON_KEY = "PagePanel.SaveButton";
    public static final String NEW_BUTTON_KEY = "PagePanel.NewButton";
    public static final String PREVIEW_BUTTON_KEY = "PagePanel.PreviewButton";
   
    public static final String EDIT_PANEL_KEY = "EditPanel";
    public static final String CUT_BUTTON_KEY = "EditPanel.CutButton";
    public static final String COPY_BUTTON_KEY = "EditPanel.CopyButton";
    public static final String PASTE_BUTTON_KEY = "EditPanel.PasteButton";
    public static final String PASTE_AS_TEXT_BUTTON_KEY = "EditPanel.PasteAsTextButton";

    public static final String TOOLS_PANEL_KEY = "ToolsPanel";
    public static final String PRINT_BUTTON_KEY = "ToolsPanel.PrintButton";
    public static final String SPELL_CHECK_BUTTON_KEY = "ToolsPanel.SpellcheckButton";
    
    public static final String UNDO_REDO_PANEL_KEY = "UndoRedoPanel";
    public static final String UNDO_BUTTON_KEY = "UndoRedoPanel.UndoButton";
    public static final String REDO_BUTTON_KEY = "UndoRedoPanel.RedoButton";

    public static final String SEARCH_PANEL_KEY = "SearchPanel";
    public static final String FIND_BUTTON_KEY = "SearchPanel.FindButton";
    public static final String REPLACE_BUTTON_KEY = "SearchPanel.ReplaceButton";
    
    public static final String FORMAT_PANEL_KEY = "FormatPanel";
    public static final String SELECT_ALL_BUTTON_KEY = "FormatPanel.SelectAllButton";
    public static final String CLEAR_FORMATTING_BUTTON_KEY = "FormatPanel.ClearFormattingButton";
    
    public static final String TEXT_EFFECT_PANEL_KEY = "TextEffectPanel";
    public static final String BOLD_BUTTON_KEY = "TextEffectPanel.BoldButton";
    public static final String ITALIC_BUTTON_KEY = "TextEffectPanel.ItalicButton";
    public static final String UNDERLINE_BUTTON_KEY = "TextEffectPanel.UnderlineButton";
    public static final String STRIKE_BUTTON_KEY = "TextEffectPanel.StrikethroughButton";

    public static final String SUB_SUPER_SCRIPT_PANEL_KEY = "SubSuperScriptPanel";
    public static final String SUB_SCRIPT_BUTTON_KEY = "SubSuperScriptPanel.SubscriptButton";
    public static final String SUPER_SCRIPT_BUTTON_KEY = "SubSuperScriptPanel.SuperscriptButton";

    public static final String LIST_PANEL_KEY = "ListPanel";
    public static final String NUMBERED_LIST_BUTTON_KEY = "ListPanel.InsertRemoveNumberedListButton";
    public static final String BULLETED_BUTTON_KEY = "ListPanel.InsertRemoveBulletedListButton";

    public static final String BLOCK_PANEL_KEY = "BlockPanel";
    public static final String DECREASE_INDENT_BUTTON_KEY = "BlockPanel.DecreaseIndentButton";
    public static final String INCREASE_INDENT_BUTTON_KEY = "BlockPanel.IncreaseIndentButton";
    public static final String BLOCK_QUOTE_BUTTON_KEY = "BlockPanel.BlockQuoteButton";
    public static final String DIV_BUTTON_KEY = "BlockPanel.CreateDivButton";  
    public static final String PARAGRAPH_BUTTON_KEY = "BlockPanel.CreateParagraphButton";

    public static final String JUSTIFICATION_PANEL_KEY = "JustificationPanel";
    public static final String LEFT_JUSTIFY_BUTTON_KEY = "JustificationPanel.LeftJustifyButton";
    public static final String CENTER_JUSTIFY_BUTTON_KEY = "JustificationPanel.CenterJustifyButton";
    public static final String RIGHT_JUSTIFY_BUTTON_KEY = "JustificationPanel.RightJustifyButton";
    public static final String BLOCK_JUSTIFY_BUTTON_KEY = "JustificationPanel.BlockJustifyButton";

    public static final String LINK_PANEL_KEY = "LinkPanel";
    public static final String LINK_BUTTON_KEY = "LinkPanel.LinkButton";
    public static final String UNLINK_BUTTON_KEY = "LinkPanel.UnlinkButton";
    public static final String ANCHOR_BUTTON_KEY = "LinkPanel.AnchorButton";

    public static final String MISC_PANEL_KEY = "MiscPanel";
    public static final String IMAGE_BUTTON_KEY = "MiscPanel.InsertImage";
    public static final String TABLE_BUTTON_KEY = "MiscPanel.InsertTableButton";
    public static final String HORIZONTAL_LINE_BUTTON_KEY = "MiscPanel.InsertHorizontalLineButton";
    public static final String SPECIAL_CHAR_BUTTON_KEY = "MiscPanel.InsertSpecialCharButton";

    public static final String FONT_PANEL_KEY = "FontComboBox";
    public static final String FONT_SIZE_PANEL_KEY = "FontSizeComboBox";
    public static final String PARAGRAPH_FORMAT_PANEL_KEY = "ParagraphFormatComboBox";
 
    public static final String COLOR_PANEL_KEY = "ColorPanel";
    public static final String TEXT_COLOR_BUTTON_KEY = "ColorPanel.TextColorButton";
    public static final String BACKGROUND_COLOR_BUTTON_KEY = "ColorPanel.BackgroundColorButton";

    public static final String ABOUT_PANEL_KEY = "AboutPanel";
    public static final String ABOUT_BUTTON_KEY = "AboutPanel.AboutButton";

    private JTextComponentSpellChecker spellChecker = null;
    private SpellDictionary dictionary = null;
    private JTextArea htmlTextArea;
    private boolean htmlSourceMode = false;    
    private SpecialCharacterDialog specialCharacterDialog = new SpecialCharacterDialog(null, true);
    private Hashtable<Object, Action> editorKitActions;
    private SpellCheckDictionaryVersion spellCheckDictionaryVersion = SpellCheckDictionaryVersion.LIBERAL_US;
    private String customDictionaryFilename = null;

    /** Listener for the edits on the current document. */
    protected UndoableEditListener undoHandler = new UndoHandler();

    /** UndoManager that we add edits to. */
    protected UndoManager undo = new UndoManager();

    private UndoAction undoAction = new UndoAction();
    private RedoAction redoAction = new RedoAction();
    private HTMLEditorKit.CutAction cutAction = new HTMLEditorKit.CutAction();
    private HTMLEditorKit.CopyAction copyAction = new HTMLEditorKit.CopyAction();
    private HTMLEditorKit.PasteAction pasteAction = new HTMLEditorKit.PasteAction();
    private FindReplaceAction findReplaceAction;

    private HTMLEditorKit editorKit = new HTMLEditorKit();

    private JPopupMenu contextMenu;

    private List<ContextMenuListener> contextMenuListeners = new ArrayList<ContextMenuListener>();
    private List<EditorMouseMotionListener> editorMouseMotionListeners = new ArrayList<EditorMouseMotionListener>();

    private enum ParagraphFormat {
        PARAGRAPH_FORMAT("Format", null),
        NORMAL("Normal", Tag.P),
        HEADING1("Heading 1", Tag.H1),
        HEADING2("Heading 2", Tag.H2),
        HEADING3("Heading 3", Tag.H3),
        HEADING4("Heading 4", Tag.H4),
        HEADING5("Heading 5", Tag.H5),
        HEADING6("Heading 6", Tag.H6),
        FORMATTED("Formatted", Tag.PRE),
        ADDRESS("Address", Tag.ADDRESS);

        private String text;
        private Tag tag;

        ParagraphFormat(String text, Tag tag) {
            this.text = text;
            this.tag = tag;
        }

        public Tag getTag() {
            return tag;
        }

        public String getText() {
            return text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    private enum FontItem {
        FONT("Font", null),
        ARIAL("Arial", "Arial"),
        COMIC_SANS_MS("Comic Sans MS", "Comic Sans MS"),
        COURIER_NEW("Courier New", "Courier New"),
        GEORGIA("Georgia", "Georgia"),
        LUCINDA_SANS_UNICODE("Lucinda Sans Unicode", "Lucinda Sans Unicode"),
        TAHOMA("Tahoma", "Tahoma"),
        TIMES_NEW_ROMAN("Times New Roman", "Times New Roman"),
        TREBUCHET_MS("Trebuchet MS", "Trebuchet MS"),
        VERDANA("Verdana", "Verdana");

        private String text;
        private String fontName;

        FontItem(String text, String fontName) {
            this.text = text;
            this.fontName = fontName;
        }

        public String getText() {
            return text;
        }

        public String getFontName() {
            return fontName;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    private enum FontSize {
        FONT_SIZE("Size", -1),
        SIZE8("8", 8),
        SIZE9("9", 9),
        SIZE10("10", 10),
        SIZE11("11", 11),
        SIZE12("12", 12),
        SIZE14("14", 14),
        SIZE18("18", 18),
        SIZE20("20", 20),
        SIZE22("22", 22),
        SIZE24("24", 24),
        SIZE26("26", 26),
        SIZE28("28", 28),
        SIZE36("36", 36),
        SIZE48("48", 48),
        SIZE72("72", 72);

        private String text;
        private int size;

        FontSize(String text, int size) {
            this.text = text;
            this.size = size;
        }

        public String getText() {
            return text;
        }

        public int getSize() {
            return size;
        }

        @Override
        public String toString() {
            return text;
        }
    }
    
    private javax.swing.JPanel toolbarPanel;
    private javax.swing.JTextPane htmlTextPane;
    private javax.swing.JScrollPane mainScrollPane;

    private javax.swing.text.html.HTMLDocument htmlDocument;
     
    private javax.swing.JPanel sourcePanel;
    private javax.swing.JButton sourceButton;

    private javax.swing.JPanel pagePanel;
    private javax.swing.JButton openButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JButton newButton;
    private javax.swing.JButton previewButton;
   
    private javax.swing.JPanel editPanel;
    private javax.swing.JButton cutButton;
    private javax.swing.JButton copyButton;
    private javax.swing.JButton pasteButton;
    private javax.swing.JButton pasteAsTextButton;

    private javax.swing.JPanel toolsPanel;
    private javax.swing.JButton printButton;
    private javax.swing.JButton spellcheckButton;
    
    private javax.swing.JPanel undoRedoPanel;
    private javax.swing.JButton undoButton;
    private javax.swing.JButton redoButton;

    private javax.swing.JPanel searchPanel;
    private javax.swing.JButton findButton;
    private javax.swing.JButton replaceButton;
    
    private javax.swing.JPanel formatPanel;
    private javax.swing.JButton selectAllButton;
    private javax.swing.JButton clearFormattingButton;
    
   
    private javax.swing.JPanel textEffectPanel;
    private javax.swing.JButton boldButton;
    private javax.swing.JButton italicButton;
    private javax.swing.JButton underlineButton;
    private javax.swing.JButton strikethroughButton;

    private javax.swing.JPanel subSuperScriptPanel;
    private javax.swing.JButton subscriptButton;
    private javax.swing.JButton superscriptButton;

    private javax.swing.JPanel listPanel;
    private javax.swing.JButton insertRemoveNumberedListButton;
    private javax.swing.JButton insertRemoveBulletedListButton;

    private javax.swing.JPanel blockPanel;
    private javax.swing.JButton decreaseIndentButton;
    private javax.swing.JButton increaseIndentButton;
    private javax.swing.JButton blockQuoteButton;
    private javax.swing.JButton createDivButton;  
    private javax.swing.JButton createParagraphButton;

    private javax.swing.JPanel justificationPanel;
    private javax.swing.JButton leftJustifyButton;
    private javax.swing.JButton centerJustifyButton;
    private javax.swing.JButton rightJustifyButton;
    private javax.swing.JButton blockJustifyButton;

    private javax.swing.JPanel linkPanel;
    private javax.swing.JButton linkButton;
    private javax.swing.JButton unlinkButton;
    private javax.swing.JButton anchorButton;

    private javax.swing.JPanel miscPanel;
    private javax.swing.JButton insertImage;
    private javax.swing.JButton insertTableButton;
    private javax.swing.JButton insertHorizontalLineButton;
    private javax.swing.JButton insertSpecialCharButton;

    private javax.swing.JComboBox fontComboBox;
    private javax.swing.JComboBox fontSizeComboBox;
    private javax.swing.JComboBox paragraphFormatComboBox;
 
    private javax.swing.JPanel colorPanel;
    private javax.swing.JButton textColorButton;
    private javax.swing.JButton backgroundColorButton;

    private javax.swing.JPanel aboutPanel;
    private javax.swing.JButton aboutButton;

    /** Creates new form MetaphaseEditorPanel */
    public MetaphaseEditorPanel(MetaphaseEditorConfiguration configuration)
    {        
        initComponents();
        updateComponents(configuration);

        createEditorKitActionTable();
        
        htmlTextArea = new JTextArea();
        htmlTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        htmlTextPane.setContentType("text/html");

        findReplaceAction = new FindReplaceAction("Find/Replace", htmlTextPane);

        cutButton.setAction(cutAction);
        cutButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/cut.png"));
        cutButton.setText("");
        cutButton.setToolTipText("Cut");

        copyButton.setAction(copyAction);
        copyButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/copy.png"));
        copyButton.setText("");
        copyButton.setToolTipText("Copy");

        pasteButton.setAction(pasteAction);
        pasteButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/paste.png"));
        pasteButton.setText("");
        pasteButton.setToolTipText("Paste");

        undoButton.setAction(undoAction);
        undoButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/undo.png"));
        undoButton.setText("");
        undoButton.setToolTipText("Undo");

        redoButton.setAction(redoAction);
        redoButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/redo.png"));
        redoButton.setText("");
        redoButton.setToolTipText("Redo");

        findButton.setAction(findReplaceAction);
        findButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/find.png"));
        findButton.setText("");
        findButton.setToolTipText("Find");

        replaceButton.setAction(findReplaceAction);
        replaceButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/replace.png"));
        replaceButton.setText("");
        replaceButton.setToolTipText("Replace");

        clearFormattingButton.setAction(new ClearFormattingAction(this, "Remove Format"));
        clearFormattingButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/removeformat.png"));
        clearFormattingButton.setText("");
        clearFormattingButton.setToolTipText("Remove Format");

        boldButton.setAction(new HTMLEditorKit.BoldAction());
        boldButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/bold.png"));
        boldButton.setText("");
        boldButton.setToolTipText("Bold");

        italicButton.setAction(new HTMLEditorKit.ItalicAction());
        italicButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/italic.png"));
        italicButton.setText("");
        italicButton.setToolTipText("Italic");

        underlineButton.setAction(new HTMLEditorKit.UnderlineAction());
        underlineButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/underline.png"));
        underlineButton.setText("");
        underlineButton.setToolTipText("Underline");

        strikethroughButton.setAction(new StrikeThroughAction());
        strikethroughButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/strikethrough.png"));
        strikethroughButton.setText("");
        strikethroughButton.setToolTipText("Strike Through");

        subscriptButton.setAction(new SubscriptAction());
        subscriptButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/subscript.png"));
        subscriptButton.setText("");
        subscriptButton.setToolTipText("Subscript");

        superscriptButton.setAction(new SuperscriptAction());
        superscriptButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/superscript.png"));
        superscriptButton.setText("");
        superscriptButton.setToolTipText("Superscript");

        //TODO: change increase and decrease indent to add inner <li> when inside bulleted or numbered list
        increaseIndentButton.setAction(new IncreaseIndentAction("Increase Indent", this));
        increaseIndentButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/incindent.png"));
        increaseIndentButton.setText("");
        increaseIndentButton.setToolTipText("Increase Indent");

        decreaseIndentButton.setAction(new DecreaseIndentAction("Decrease Indent", this));
        decreaseIndentButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/decindent.png"));
        decreaseIndentButton.setText("");
        decreaseIndentButton.setToolTipText("Decrease Indent");

        blockQuoteButton.setAction(new FormatAction(this, "Block Quote", Tag.BLOCKQUOTE));
        blockQuoteButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/blockquote.png"));
        blockQuoteButton.setText("");
        blockQuoteButton.setToolTipText("Block Quote");

        leftJustifyButton.setAction(new HTMLEditorKit.AlignmentAction("Left Align",StyleConstants.ALIGN_LEFT));
        leftJustifyButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/leftjustify.png"));
        leftJustifyButton.setText("");
        leftJustifyButton.setToolTipText("Left Justify");

        centerJustifyButton.setAction(new HTMLEditorKit.AlignmentAction("Center Align",StyleConstants.ALIGN_CENTER));
        centerJustifyButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/centerjustify.png"));
        centerJustifyButton.setText("");
        centerJustifyButton.setToolTipText("Center Justify");

        rightJustifyButton.setAction(new HTMLEditorKit.AlignmentAction("Left Align",StyleConstants.ALIGN_RIGHT));
        rightJustifyButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/rightjustify.png"));
        rightJustifyButton.setText("");
        rightJustifyButton.setToolTipText("Right Justify");

        blockJustifyButton.setAction(new HTMLEditorKit.AlignmentAction("Justified Align",StyleConstants.ALIGN_JUSTIFIED));
        blockJustifyButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/blockjustify.png"));
        blockJustifyButton.setText("");
        blockJustifyButton.setToolTipText("Block Justify");

        unlinkButton.setAction(new UnlinkAction(this, "Unlink"));
        unlinkButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/unlink.png"));
        unlinkButton.setText("");
        unlinkButton.setToolTipText("Unlink");

        //TODO: horizontal rule - doesn't insert correctly if within anything other than P, ie. TD or H1
        insertHorizontalLineButton.setAction(new HTMLEditorKit.InsertHTMLTextAction("Insert Horizontal Line", "<hr/>", Tag.P, Tag.HR, Tag.BODY, Tag.HR));
        insertHorizontalLineButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/horizontalline.png"));
        insertHorizontalLineButton.setText("");
        insertHorizontalLineButton.setToolTipText("Insert Horizontal Line");

        paragraphFormatComboBox.setRenderer(new ParagraphFormatListCellRenderer());
        paragraphFormatComboBox.removeAllItems();
        ParagraphFormat[] paragraphFormats = ParagraphFormat.values();
        for (int i=0; i<paragraphFormats.length; i++) {
            paragraphFormatComboBox.addItem(paragraphFormats[i]);
        }

        fontComboBox.setRenderer(new FontListCellRenderer());
        fontComboBox.removeAllItems();
        FontItem[] fontItems = FontItem.values();
        for (int i=0; i<fontItems.length; i++) {
            fontComboBox.addItem(fontItems[i]);
        }

        fontSizeComboBox.setRenderer(new FontSizeListCellRenderer());
        fontSizeComboBox.removeAllItems();
        FontSize[] fontSizes = FontSize.values();
        for (int i=0; i<fontSizes.length; i++) {
            fontSizeComboBox.addItem(fontSizes[i]);
        }

        setToolbarFocusActionListener(this);

        htmlTextPane.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
        htmlTextPane.getActionMap().put("Undo", undoAction);

        htmlTextPane.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");
        htmlTextPane.getActionMap().put("Redo", redoAction);

        htmlTextPane.getInputMap().put(KeyStroke.getKeyStroke("control F"), "Find");
        htmlTextPane.getActionMap().put("Find", findReplaceAction);

        htmlTextPane.getInputMap().put(KeyStroke.getKeyStroke("control R"), "Replace");
        htmlTextPane.getActionMap().put("Replace", findReplaceAction);

        contextMenu = new JPopupMenu();
        JMenuItem cutMenuItem = new JMenuItem();
        cutMenuItem.setAction(cutAction);
        cutMenuItem.setText("Cut");
        cutMenuItem.setMnemonic('C');
        cutMenuItem.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/cut.png"));
        JMenuItem copyMenuItem = new JMenuItem();
        copyMenuItem.setAction(copyAction);
        copyMenuItem.setText("Copy");
        copyMenuItem.setMnemonic('o');
        copyMenuItem.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/copy.png"));
        JMenuItem pasteMenuItem = new JMenuItem();
        pasteMenuItem.setAction(pasteAction);
        pasteMenuItem.setText("Paste");
        pasteMenuItem.setMnemonic('P');
        pasteMenuItem.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/paste.png"));
        contextMenu.add(cutMenuItem);
        contextMenu.add(copyMenuItem);
        contextMenu.add(pasteMenuItem);

        htmlTextPane.addMouseMotionListener(new DefaultEditorMouseMotionListener());
        htmlTextPane.setEditorKit(editorKit);

	startNewDocument();

        initSpellChecker();
    }

    // The following two methods allow us to find an
    // action provided by the editor kit by its name.
    private void createEditorKitActionTable() {
        editorKitActions = new Hashtable<Object, Action>();
        Action[] actionsArray = editorKit.getActions();
        for (int i = 0; i < actionsArray.length; i++) {
            Action a = actionsArray[i];
            editorKitActions.put(a.getValue(Action.NAME), a);
        }
    }

    private Action getEditorKitActionByName(String name) {
        return editorKitActions.get(name);
    }

    protected void resetUndoManager() {
        undo.discardAllEdits();
        undoAction.update();
        redoAction.update();
    }

    public void startNewDocument(){
            Document oldDoc = htmlTextPane.getDocument();
            if(oldDoc != null)
                    oldDoc.removeUndoableEditListener(undoHandler);
            htmlDocument = (HTMLDocument)editorKit.createDefaultDocument();
            htmlTextPane.setDocument(htmlDocument);
            htmlTextPane.getDocument().addUndoableEditListener(undoHandler);
            resetUndoManager();
            //TODO: check if necessary
            htmlDocument.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
            htmlDocument.setPreservesUnknownTags(false);            
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    private void initComponents() {

        htmlDocument = new javax.swing.text.html.HTMLDocument();

        cutButton = new javax.swing.JButton();
        copyButton = new javax.swing.JButton();
        pasteAsTextButton = new javax.swing.JButton();
        pasteButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        newButton = new javax.swing.JButton();
        previewButton = new javax.swing.JButton();
        openButton = new javax.swing.JButton();
        printButton = new javax.swing.JButton();
        spellcheckButton = new javax.swing.JButton();
        undoButton = new javax.swing.JButton();
        redoButton = new javax.swing.JButton();
        findButton = new javax.swing.JButton();
        replaceButton = new javax.swing.JButton();
        selectAllButton = new javax.swing.JButton();
        clearFormattingButton = new javax.swing.JButton();
        boldButton = new javax.swing.JButton();
        italicButton = new javax.swing.JButton();
        strikethroughButton = new javax.swing.JButton();
        underlineButton = new javax.swing.JButton();
        subscriptButton = new javax.swing.JButton();
        superscriptButton = new javax.swing.JButton();
        insertRemoveNumberedListButton = new javax.swing.JButton();
        insertRemoveBulletedListButton = new javax.swing.JButton();
        decreaseIndentButton = new javax.swing.JButton();
        increaseIndentButton = new javax.swing.JButton();
        createDivButton = new javax.swing.JButton();
        blockQuoteButton = new javax.swing.JButton();
        createParagraphButton = new javax.swing.JButton();
        leftJustifyButton = new javax.swing.JButton();
        centerJustifyButton = new javax.swing.JButton();
        blockJustifyButton = new javax.swing.JButton();
        rightJustifyButton = new javax.swing.JButton();
        insertTableButton = new javax.swing.JButton();
        insertHorizontalLineButton = new javax.swing.JButton();
        insertSpecialCharButton = new javax.swing.JButton();
        insertImage = new javax.swing.JButton();
        paragraphFormatComboBox = new javax.swing.JComboBox();
        fontComboBox = new javax.swing.JComboBox();
        fontSizeComboBox = new javax.swing.JComboBox();
        textColorButton = new javax.swing.JButton();
        backgroundColorButton = new javax.swing.JButton();
        aboutButton = new javax.swing.JButton();
        sourceButton = new javax.swing.JButton();
        linkButton = new javax.swing.JButton();
        unlinkButton = new javax.swing.JButton();
        anchorButton = new javax.swing.JButton();

        htmlTextPane = new javax.swing.JTextPane();


        cutButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/cut.png")); // NOI18N
        cutButton.setToolTipText("Cut");
        cutButton.setName(CUT_BUTTON_KEY);

        copyButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/copy.png")); // NOI18N
        copyButton.setToolTipText("Copy");
        copyButton.setName(COPY_BUTTON_KEY);

        pasteAsTextButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/paste_as_text.png")); // NOI18N
        pasteAsTextButton.setToolTipText("Paste as plain text");
        pasteAsTextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasteAsTextButtonActionPerformed(evt);
            }
        });
        pasteAsTextButton.setName(PASTE_AS_TEXT_BUTTON_KEY);

        pasteButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/paste.png")); // NOI18N
        pasteButton.setToolTipText("Paste");
        pasteButton.setName(PASTE_BUTTON_KEY);

 
        saveButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/save.png")); // NOI18N
        saveButton.setToolTipText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        saveButton.setName(SAVE_BUTTON_KEY);

        newButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/newpage.png")); // NOI18N
        newButton.setToolTipText("New");
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });
        newButton.setName(NEW_BUTTON_KEY);

        previewButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/preview.png")); // NOI18N
        previewButton.setToolTipText("Preview");
        previewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previewButtonActionPerformed(evt);
            }
        });
        previewButton.setName(PREVIEW_BUTTON_KEY);

        openButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/open.png")); // NOI18N
        openButton.setToolTipText("Open");
        openButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openButtonActionPerformed(evt);
            }
        });
        openButton.setName(OPEN_BUTTON_KEY);

        printButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/print.png")); // NOI18N
        printButton.setToolTipText("Print");
        printButton.addActionListener(new java.awt.event.ActionListener() {
        	public void actionPerformed(java.awt.event.ActionEvent evt) {
        		printButtonActionPerformed(evt);
        	}
        });
        printButton.setName(PRINT_BUTTON_KEY);

        spellcheckButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/spellcheck.png")); // NOI18N
        spellcheckButton.setToolTipText("Check Spelling");
        spellcheckButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                spellcheckButtonActionPerformed(evt);
            }
        });
        spellcheckButton.setName(SPELL_CHECK_BUTTON_KEY);

        undoButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/undo.png")); // NOI18N
        undoButton.setToolTipText("Undo");
        undoButton.setName(UNDO_BUTTON_KEY);

        redoButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/redo.png")); // NOI18N
        redoButton.setToolTipText("Redo");
        redoButton.setName(REDO_BUTTON_KEY);

        findButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/find.png")); // NOI18N
        findButton.setToolTipText("Find");
        findButton.setName(FIND_BUTTON_KEY);

        replaceButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/replace.png")); // NOI18N
        replaceButton.setToolTipText("Replace");
        replaceButton.setName(REPLACE_BUTTON_KEY);

        selectAllButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/selectall.png")); // NOI18N
        selectAllButton.setToolTipText("Select All");
        selectAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllButtonActionPerformed(evt);
            }
        });
        selectAllButton.setName(SELECT_ALL_BUTTON_KEY);

        clearFormattingButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/removeformat.png")); // NOI18N
        clearFormattingButton.setToolTipText("Remove Format");
        clearFormattingButton.setName(CLEAR_FORMATTING_BUTTON_KEY);

        boldButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/bold.png")); // NOI18N
        boldButton.setToolTipText("Bold");
        boldButton.setName(BOLD_BUTTON_KEY);

        italicButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/italic.png")); // NOI18N
        italicButton.setToolTipText("Italic");
        italicButton.setName(ITALIC_BUTTON_KEY);

        strikethroughButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/strikethrough.png")); // NOI18N
        strikethroughButton.setToolTipText("Strike Through");
        strikethroughButton.setName(STRIKE_BUTTON_KEY);

        underlineButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/underline.png")); // NOI18N
        underlineButton.setToolTipText("Underline");
        underlineButton.setName(UNDERLINE_BUTTON_KEY);

         subscriptButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/subscript.png")); // NOI18N
        subscriptButton.setToolTipText("Subscript");
        subscriptButton.setName(SUB_SCRIPT_BUTTON_KEY);

        superscriptButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/superscript.png")); // NOI18N
        superscriptButton.setToolTipText("Superscript");
        superscriptButton.setName(SUPER_SCRIPT_BUTTON_KEY);

        insertRemoveNumberedListButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/numberlist.png")); // NOI18N
        insertRemoveNumberedListButton.setToolTipText("Insert/Remove Numbered List");
        insertRemoveNumberedListButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertRemoveNumberedListButtonActionPerformed(evt);
            }
        });
        insertRemoveNumberedListButton.setName(NUMBERED_LIST_BUTTON_KEY);

        insertRemoveBulletedListButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/bulletlist.png")); // NOI18N
        insertRemoveBulletedListButton.setToolTipText("Insert/Remove Bulleted List");
        insertRemoveBulletedListButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertRemoveBulletedListButtonActionPerformed(evt);
            }
        });
        insertRemoveBulletedListButton.setName(BULLETED_BUTTON_KEY);

       decreaseIndentButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/decindent.png")); // NOI18N
        decreaseIndentButton.setToolTipText("Decrease Indent");
        decreaseIndentButton.setName(DECREASE_INDENT_BUTTON_KEY);

        increaseIndentButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/incindent.png")); // NOI18N
        increaseIndentButton.setToolTipText("Increase Indent");
        increaseIndentButton.setName(INCREASE_INDENT_BUTTON_KEY);

        createDivButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/creatediv.png")); // NOI18N
        createDivButton.setToolTipText("Create Div Container");
        createDivButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createDivButtonActionPerformed(evt);
            }
        });
        createDivButton.setName(DIV_BUTTON_KEY);

        blockQuoteButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/blockquote.png")); // NOI18N
        blockQuoteButton.setToolTipText("Block Quote");
        blockQuoteButton.setName(BLOCK_QUOTE_BUTTON_KEY);

        createParagraphButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/createparagraph.png")); // NOI18N
        createParagraphButton.setToolTipText("Create Paragraph");
        createParagraphButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createParagraphButtonActionPerformed(evt);
            }
        });
        createParagraphButton.setName(PARAGRAPH_BUTTON_KEY);

        leftJustifyButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/leftjustify.png")); // NOI18N
        leftJustifyButton.setToolTipText("Left Justify");
        leftJustifyButton.setName(LEFT_JUSTIFY_BUTTON_KEY);

        centerJustifyButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/centerjustify.png")); // NOI18N
        centerJustifyButton.setToolTipText("Center Justify");
        centerJustifyButton.setName(CENTER_JUSTIFY_BUTTON_KEY);

        blockJustifyButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/blockjustify.png")); // NOI18N
        blockJustifyButton.setToolTipText("Block Justify");
        blockJustifyButton.setName(BLOCK_JUSTIFY_BUTTON_KEY);

        rightJustifyButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/rightjustify.png")); // NOI18N
        rightJustifyButton.setToolTipText("Right Justify");
        rightJustifyButton.setName(RIGHT_JUSTIFY_BUTTON_KEY);

         insertTableButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/table.png")); // NOI18N
        insertTableButton.setToolTipText("Table");
        insertTableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertTableButtonActionPerformed(evt);
            }
        });
        insertTableButton.setName(TABLE_BUTTON_KEY);

        insertHorizontalLineButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/horizontalline.png")); // NOI18N
        insertHorizontalLineButton.setToolTipText("Insert Horizontal Line");
        insertHorizontalLineButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertHorizontalLineButtonActionPerformed(evt);
            }
        });
        insertHorizontalLineButton.setName(HORIZONTAL_LINE_BUTTON_KEY);

        
        insertSpecialCharButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/symbol.png")); // NOI18N
        insertSpecialCharButton.setToolTipText("Insert Special Character");
        insertSpecialCharButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertSpecialCharButtonActionPerformed(evt);
            }
        });
        insertSpecialCharButton.setName(SPECIAL_CHAR_BUTTON_KEY);

        insertImage.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/image.png")); // NOI18N
        insertImage.setToolTipText("Image");
        insertImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertImageActionPerformed(evt);
            }
        });
        insertImage.setName(IMAGE_BUTTON_KEY);

        paragraphFormatComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Normal", "Heading 1", "Heading 2", "Heading 3", "Heading 4", "Heading 5", "Heading 6", "Formatted", "Address", "Normal (DIV)" }));
        paragraphFormatComboBox.setToolTipText("Paragraph Format");
        paragraphFormatComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paragraphFormatComboBoxActionPerformed(evt);
            }
        });
        paragraphFormatComboBox.setName(PARAGRAPH_FORMAT_PANEL_KEY);
        
        fontComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Arial", "Comic Sans MS", "Courier New", "Georgia", "Lucinda Sans Unicode", "Tahoma", "Times New Roman", "Trebuchet MS", "Verdana" }));
        fontComboBox.setToolTipText("Font");
        fontComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fontComboBoxActionPerformed(evt);
            }
        });
        fontComboBox.setName(FONT_PANEL_KEY);

        fontSizeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "26", "28", "36", "48", "72" }));
        fontSizeComboBox.setToolTipText("Font Size");
        fontSizeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fontSizeComboBoxActionPerformed(evt);
            }
        });
        fontSizeComboBox.setName(FONT_SIZE_PANEL_KEY);

        textColorButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/textcolor.png")); // NOI18N
        textColorButton.setToolTipText("Text Color");
        textColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textColorButtonActionPerformed(evt);
            }
        });
        textColorButton.setName(TEXT_COLOR_BUTTON_KEY);

        backgroundColorButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/backgroundcolor.png")); // NOI18N
        backgroundColorButton.setToolTipText("Background Color");
        backgroundColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backgroundColorButtonActionPerformed(evt);
            }
        });
        backgroundColorButton.setName(BACKGROUND_COLOR_BUTTON_KEY);

        aboutButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/about.png")); // NOI18N
        aboutButton.setToolTipText("About Metaphase Editor");
        aboutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutButtonActionPerformed(evt);
            }
        });
        aboutButton.setName(ABOUT_BUTTON_KEY);

        sourceButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/source.png")); // NOI18N
        sourceButton.setText("Source");
        sourceButton.setToolTipText("Source");
        //sourceButton.setPreferredSize(new java.awt.Dimension(87, 24));
        sourceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sourceButtonActionPerformed(evt);
            }
        });
        sourceButton.setName(SOURCE_BUTTON_KEY);

         linkButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/link.png")); // NOI18N
        linkButton.setToolTipText("Link");
        linkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                linkButtonActionPerformed(evt);
            }
        });
        linkButton.setName(LINK_BUTTON_KEY);

        unlinkButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/unlink.png")); // NOI18N
        unlinkButton.setToolTipText("Unlink");
        unlinkButton.setName(UNLINK_BUTTON_KEY);

        anchorButton.setIcon(new ImageIconResource("Icons/MetaphaseEditor/icons/anchor.png")); // NOI18N
        anchorButton.setToolTipText("Anchor");
        anchorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                anchorButtonActionPerformed(evt);
            }
        });
        anchorButton.setName(ANCHOR_BUTTON_KEY);
      
        htmlTextPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                htmlTextPaneMouseClicked(evt);
            }
        });
        htmlTextPane.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                htmlTextPaneKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                htmlTextPaneKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                htmlTextPaneKeyTyped(evt);
            }
        });
        mainScrollPane = new JScrollPane();
        mainScrollPane.setViewportView(htmlTextPane);

        setLayout(new BorderLayout());

        toolbarPanel = new JPanel();
        toolbarPanel.setLayout(new BoxLayout(toolbarPanel,BoxLayout.Y_AXIS));

        add(toolbarPanel,BorderLayout.NORTH);
        add(mainScrollPane,BorderLayout.CENTER);
 
    }
    
    public void updateComponents(MetaphaseEditorConfiguration configuration) 
    {

    	toolbarPanel.removeAll();
    	
    	if (configuration.hasOption(SOURCE_PANEL_KEY))
    		sourcePanel = makeGroup(SOURCE_PANEL_KEY,configuration,sourceButton);

    	if (configuration.hasOption(PAGE_PANEL_KEY))
    		pagePanel = makeGroup(PAGE_PANEL_KEY,configuration,openButton,saveButton,newButton,previewButton);
    	
    	if (configuration.hasOption(EDIT_PANEL_KEY))
    		editPanel = makeGroup(EDIT_PANEL_KEY,configuration,cutButton,copyButton,pasteButton,pasteAsTextButton);
    	
    	if (configuration.hasOption(TOOLS_PANEL_KEY))
    		toolsPanel = makeGroup(TOOLS_PANEL_KEY,configuration,printButton,spellcheckButton);
    	
    	if (configuration.hasOption(UNDO_REDO_PANEL_KEY))
    		undoRedoPanel = makeGroup(UNDO_REDO_PANEL_KEY,configuration,undoButton,redoButton);
    	
    	if (configuration.hasOption(SEARCH_PANEL_KEY))
    		searchPanel = makeGroup(SEARCH_PANEL_KEY,configuration,findButton,replaceButton);         
    	
    	if (configuration.hasOption(FORMAT_PANEL_KEY))
    		formatPanel = makeGroup(FORMAT_PANEL_KEY,configuration,selectAllButton,clearFormattingButton);
    	
    	if (configuration.hasOption(TEXT_EFFECT_PANEL_KEY))
    		textEffectPanel = makeGroup(TEXT_EFFECT_PANEL_KEY,configuration,boldButton,italicButton,strikethroughButton,underlineButton);
    	
    	if (configuration.hasOption(SUB_SUPER_SCRIPT_PANEL_KEY))
    		subSuperScriptPanel = makeGroup(SUB_SUPER_SCRIPT_PANEL_KEY,configuration,subscriptButton,superscriptButton);
    	
    	if (configuration.hasOption(LIST_PANEL_KEY))
    		listPanel = makeGroup(LIST_PANEL_KEY,configuration,insertRemoveNumberedListButton,insertRemoveBulletedListButton);
    	
    	if (configuration.hasOption(BLOCK_PANEL_KEY))
    		blockPanel = makeGroup(BLOCK_PANEL_KEY,configuration,decreaseIndentButton,increaseIndentButton,blockQuoteButton,createDivButton,createParagraphButton);
    	
    	if (configuration.hasOption(JUSTIFICATION_PANEL_KEY))
    		justificationPanel = makeGroup(JUSTIFICATION_PANEL_KEY,configuration,leftJustifyButton,centerJustifyButton,rightJustifyButton, blockJustifyButton);
    	
    	if (configuration.hasOption(LINK_PANEL_KEY))
    		linkPanel = makeGroup(LINK_PANEL_KEY,configuration,linkButton,unlinkButton,anchorButton);
    	
    	if (configuration.hasOption(MISC_PANEL_KEY))
    		miscPanel = makeGroup(MISC_PANEL_KEY,configuration,insertImage,insertTableButton,insertHorizontalLineButton, insertSpecialCharButton);
    	
    	if (configuration.hasOption(COLOR_PANEL_KEY))
    		colorPanel = makeGroup(COLOR_PANEL_KEY,configuration,textColorButton,backgroundColorButton);

    	if (configuration.hasOption(ABOUT_PANEL_KEY))
    		aboutPanel = makeGroup(ABOUT_PANEL_KEY,configuration,aboutButton);

    	toolbarPanel.add(makeLinePanel(1,configuration));
        toolbarPanel.add(makeLinePanel(2,configuration));
        toolbarPanel.add(makeLinePanel(3,configuration));

        toolbarPanel.revalidate();
        toolbarPanel.repaint();
    }
    
    private JPanel makeGroup(String groupName, final MetaphaseEditorConfiguration configuration, JButton... buttons)
    {
    	JPanel returned = new JPanel();
    	returned.setName(groupName);
    	//returned.setBorder(BorderFactory.createEtchedBorder());
       	returned.setBorder(BorderFactory.createEmptyBorder());
    	returned.setLayout(new FlowLayout(FlowLayout.LEADING,0,0));

       	Vector<JButton> buttonList = new Vector<JButton>();
        
    	for (JButton b : buttons) {
    		if (configuration.hasOption(b.getName()))
    			buttonList.add(b);
    	}

    	Collections.sort(buttonList,new Comparator<JButton>() {
    		@Override
    		public int compare(JButton o1, JButton o2) {
    			return configuration.getOption(o1.getName()).index-configuration.getOption(o2.getName()).index;
    		}		
    	});

    	for (JButton c : buttonList) {
    		returned.add(c);
    	}
    	return returned;
    }

    private JPanel makeLinePanel(int line, final MetaphaseEditorConfiguration configuration)
    {
    	Vector<JComponent> components = new Vector<JComponent>();
    	if (configuration.hasOption(SOURCE_PANEL_KEY,line))
    		components.add(sourcePanel);
    	if (configuration.hasOption(PAGE_PANEL_KEY,line))
    		components.add(pagePanel);
    	if (configuration.hasOption(EDIT_PANEL_KEY,line))
    		components.add(editPanel);
    	if (configuration.hasOption(TOOLS_PANEL_KEY,line))
    		components.add(toolsPanel);
    	if (configuration.hasOption(UNDO_REDO_PANEL_KEY,line))
    		components.add(undoRedoPanel);
    	if (configuration.hasOption(SEARCH_PANEL_KEY,line))
    		components.add(searchPanel);
    	if (configuration.hasOption(FORMAT_PANEL_KEY,line))
    		components.add(formatPanel);
    	if (configuration.hasOption(TEXT_EFFECT_PANEL_KEY,line))
    		components.add(textEffectPanel);
    	if (configuration.hasOption(SUB_SUPER_SCRIPT_PANEL_KEY,line))
    		components.add(subSuperScriptPanel);
    	if (configuration.hasOption(LIST_PANEL_KEY,line))
    		components.add(listPanel);
    	if (configuration.hasOption(BLOCK_PANEL_KEY,line))
    		components.add(blockPanel);
    	if (configuration.hasOption(JUSTIFICATION_PANEL_KEY,line))
    		components.add(justificationPanel);
    	if (configuration.hasOption(LINK_PANEL_KEY,line))
    		components.add(linkPanel);
    	if (configuration.hasOption(MISC_PANEL_KEY,line))
    		components.add(miscPanel);
    	if (configuration.hasOption(PARAGRAPH_FORMAT_PANEL_KEY,line))
    		components.add(paragraphFormatComboBox);
    	if (configuration.hasOption(FONT_PANEL_KEY,line))
    		components.add(fontComboBox);
    	if (configuration.hasOption(FONT_SIZE_PANEL_KEY,line))
    		components.add(fontSizeComboBox);
    	if (configuration.hasOption(COLOR_PANEL_KEY,line))
    		components.add(colorPanel);
    	if (configuration.hasOption(ABOUT_PANEL_KEY,line))
    		components.add(aboutPanel);

       	Collections.sort(components,new Comparator<JComponent>() {
    		@Override
    		public int compare(JComponent o1, JComponent o2) {
    			return configuration.getOption(o1.getName()).index-configuration.getOption(o2.getName()).index;
    		}		
    	});

       	JPanel returned = new JPanel();
       	returned.setLayout(new FlowLayout(FlowLayout.LEADING,10,0));
       	for (JComponent c : components) {
       		returned.add(c);
       	}
       	return returned;
    }
    
    private void printButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printButtonActionPerformed
        try {
            htmlTextPane.print();
        } catch (PrinterException e) {
            throw new MetaphaseEditorException(e.getMessage(), e);
        }
    }//GEN-LAST:event_printButtonActionPerformed

    public JTextPane getHtmlTextPane() {
        return htmlTextPane;
    }

    public void setEditorToolTipText(String string) {
        htmlTextPane.setToolTipText(string);
    }

    public void addEditorMouseMotionListener(EditorMouseMotionListener editorMouseMotionListener) {
        editorMouseMotionListeners.add(editorMouseMotionListener);
    }

    public void removeEditorMouseMotionListener(EditorMouseMotionListener editorMouseMotionListener) {
        editorMouseMotionListeners.remove(editorMouseMotionListener);
    }

    public AttributeSet getSelectedParagraphAttributes() {
        int start = htmlTextPane.getSelectionStart();        

        Element element = htmlDocument.getParagraphElement(start);
        MutableAttributeSet attributes = new SimpleAttributeSet(element.getAttributes());

        Element charElement = htmlDocument.getCharacterElement(start);
        if (charElement != null) {
            Element impliedParagraph = charElement.getParentElement();
            if (impliedParagraph != null) {
                Element listElement = impliedParagraph.getParentElement();
                if (listElement.getName().equals("li")) {
                    // re-add the existing attributes to the list item
                    AttributeSet listElementAttrs = listElement.getAttributes();
                    Enumeration currentAttrEnum = listElementAttrs.getAttributeNames();
                    while (currentAttrEnum.hasMoreElements()) {
                        Object attrName = currentAttrEnum.nextElement();
                        Object attrValue = listElement.getAttributes().getAttribute(attrName);
                        if ((attrName instanceof String || attrName instanceof HTML.Attribute) && attrValue instanceof String) {
                            attributes.addAttribute(attrName, attrValue);
                        }
                    }
                }
            }
        }

        return attributes;
    }

    public void addAttributesToSelectedParagraph(Map<String, String> attributes) {
        new AddAttributesAction(this, "Add Attributes To Selected Paragraph", attributes).actionPerformed(null);
    }

    public void removeAttributesFromSelectedParagraph(String[] attributeNames) {
        new RemoveAttributesAction(this, "Remove Attributes From Selected Paragraph", attributeNames).actionPerformed(null);
    }

    public String getDocument() {
        return htmlTextPane.getText();
    }

    public void setDocument(String value) {
        try {
            StringReader reader = new StringReader(value);
            Document oldDoc = htmlTextPane.getDocument();
            if(oldDoc != null)
                oldDoc.removeUndoableEditListener(undoHandler);
            htmlDocument = (HTMLDocument)editorKit.createDefaultDocument();
            editorKit.read(reader, htmlDocument, 0);
            htmlDocument.addUndoableEditListener(undoHandler);
            htmlTextPane.setDocument(htmlDocument);
            resetUndoManager();
        } catch (BadLocationException e) {
            throw new MetaphaseEditorException(e.getMessage(), e);
        } catch (IOException e) {
            throw new MetaphaseEditorException(e.getMessage(), e);
        }
    }

    public JPopupMenu getContextMenu() {
        return contextMenu;
    }

    public void addStyleSheetRule(String rule) {
        StyleSheet styleSheet = editorKit.getStyleSheet();
        styleSheet.addRule(rule);
    }

    public void refreshAfterAction() {
        int pos = htmlTextPane.getCaretPosition();
        htmlTextPane.setText(htmlTextPane.getText());
        htmlTextPane.validate();
        try {
            htmlTextPane.setCaretPosition(pos);
        } catch (IllegalArgumentException e) {
            // swallow the exception
            // seems like a bug in the JTextPane component
            // only happens occasionally when pasting text at the end of a document
            System.err.println(e.getMessage());
        }
    }

    private void insertRemoveNumberedListButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertRemoveNumberedListButtonActionPerformed
        new HTMLEditorKit.InsertHTMLTextAction("Insert Bulleted List", "<ol><li></li></ol>", Tag.BODY, Tag.OL).actionPerformed(evt);
    }//GEN-LAST:event_insertRemoveNumberedListButtonActionPerformed

    private void textColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textColorButtonActionPerformed
        Color color = JColorChooser.showDialog(null, "Text Color", null);
        if (color != null) {
            new StyledEditorKit.ForegroundAction("Color",color).actionPerformed(evt);
        }
    }//GEN-LAST:event_textColorButtonActionPerformed

    private void selectAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllButtonActionPerformed
        htmlTextPane.selectAll();
    }//GEN-LAST:event_selectAllButtonActionPerformed

    private void aboutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutButtonActionPerformed
        AboutDialog aboutDialog = new AboutDialog(null, true);
        aboutDialog.setVisible(true);
    }//GEN-LAST:event_aboutButtonActionPerformed

    private void backgroundColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backgroundColorButtonActionPerformed
        Color color = JColorChooser.showDialog(null, "Text Color", null);
        if (color != null) {
            new BackgroundColorAction(color).actionPerformed(evt);
        }
    }//GEN-LAST:event_backgroundColorButtonActionPerformed

    private void sourceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sourceButtonActionPerformed
        if (htmlSourceMode) {
            htmlTextPane.setText(htmlTextArea.getText());
            mainScrollPane.setViewportView(htmlTextPane);
            htmlSourceMode = false;

            setToolbarComponentEnable(this, true);
        } else {
            htmlTextArea.setText(htmlTextPane.getText());
            mainScrollPane.setViewportView(htmlTextArea);
            htmlSourceMode = true;

            setToolbarComponentEnable(this, false);
        }
    }//GEN-LAST:event_sourceButtonActionPerformed

    private void insertHorizontalLineButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertHorizontalLineButtonActionPerformed
        
    }//GEN-LAST:event_insertHorizontalLineButtonActionPerformed

    private void insertSpecialCharButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertSpecialCharButtonActionPerformed
        String specialChars = specialCharacterDialog.showDialog();
        if (specialChars != null) {
            new InsertTextAction(this, "Insert Special Character", specialChars).actionPerformed(evt);
        }
    }//GEN-LAST:event_insertSpecialCharButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        try {
            File current = new File(".");
            JFileChooser chooser = new JFileChooser(current);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setFileFilter(new HTMLFileFilter());
            for (;;) {
                int approval = chooser.showSaveDialog(this);
                if (approval == JFileChooser.APPROVE_OPTION){
                    File newFile = chooser.getSelectedFile();
                    if (newFile.exists()){
                        String message = newFile.getAbsolutePath()
                            + " already exists. \n"
                            + "Do you want to replace it?";
                        int option = JOptionPane.showConfirmDialog(this, message, "Save", JOptionPane.YES_NO_CANCEL_OPTION);
                        if (option == JOptionPane.YES_OPTION) {
                            File currentFile = newFile;
                            FileWriter fw = new FileWriter(currentFile);
                            fw.write(htmlTextPane.getText());
                            fw.close();
                            break;
                        } else if (option == JOptionPane.NO_OPTION) {
                            continue;
                        } else if (option == JOptionPane.CANCEL_OPTION) {
                            break;
                        }
                    } else {
                        File currentFile = new File(newFile.getAbsolutePath());
                        FileWriter fw = new FileWriter(currentFile);
                        fw.write(htmlTextPane.getText());
                        fw.close();
                        break;
                    }
                } else {
                    break;
                }
            }
        } catch (FileNotFoundException e){
            throw new MetaphaseEditorException(e.getMessage(), e);
        } catch(IOException e){
            throw new MetaphaseEditorException(e.getMessage(), e);
        }
    }//GEN-LAST:event_saveButtonActionPerformed

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to erase all the current content and start a new document?", "New", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            startNewDocument();

            if (htmlSourceMode) {
                htmlTextArea.setText(htmlTextPane.getText());
            }
        }
    }//GEN-LAST:event_newButtonActionPerformed

    private void paragraphFormatComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paragraphFormatComboBoxActionPerformed
        ParagraphFormat paragraphFormat = (ParagraphFormat)paragraphFormatComboBox.getSelectedItem();
        if (paragraphFormat != null && paragraphFormat.getTag() != null) {
            new FormatAction(this, "Paragraph Format", paragraphFormat.getTag()).actionPerformed(evt);
        }
        if (paragraphFormatComboBox.getItemCount() > 0) {
            paragraphFormatComboBox.setSelectedIndex(0);
        }
    }//GEN-LAST:event_paragraphFormatComboBoxActionPerformed

    private void fontComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fontComboBoxActionPerformed
        FontItem fontItem = (FontItem)fontComboBox.getSelectedItem();
        if (fontItem != null && fontItem.getFontName() != null) {
            new HTMLEditorKit.FontFamilyAction(fontItem.getText(), fontItem.getFontName()).actionPerformed(evt);
        }
        if (fontComboBox.getItemCount() > 0) {
            fontComboBox.setSelectedIndex(0);
        }
    }//GEN-LAST:event_fontComboBoxActionPerformed

    private void fontSizeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fontSizeComboBoxActionPerformed
        FontSize fontSize = (FontSize)fontSizeComboBox.getSelectedItem();
        if (fontSize != null && fontSize.getSize() != -1) {
            new HTMLEditorKit.FontSizeAction(fontSize.getText(), fontSize.getSize()).actionPerformed(evt);
        }
        if (fontSizeComboBox.getItemCount() > 0) {
            fontSizeComboBox.setSelectedIndex(0);
        }
    }//GEN-LAST:event_fontSizeComboBoxActionPerformed

    private void previewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previewButtonActionPerformed
        try {
            if (htmlSourceMode) {
                htmlTextPane.setText(htmlTextArea.getText());
            }
            File tempFile = File.createTempFile("metaphaseeditorpreview",".html");
            tempFile.deleteOnExit();
            FileWriter fw = new FileWriter(tempFile);
            fw.write(htmlTextPane.getText());
            fw.close();            

            Desktop.getDesktop().browse(tempFile.toURI());
        } catch (IOException e) {
            throw new MetaphaseEditorException(e.getMessage(), e);
        }
    }//GEN-LAST:event_previewButtonActionPerformed

    private void insertTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertTableButtonActionPerformed
        TableDialog tableDialog = new TableDialog(null, true);
        String tableHtml = tableDialog.showDialog();
        if (tableHtml != null) {
            try {
                editorKit.insertHTML(htmlDocument, htmlTextPane.getCaretPosition(), tableHtml, 0, 0, Tag.TABLE);
                refreshAfterAction();
            } catch (BadLocationException e) {
                throw new MetaphaseEditorException(e.getMessage(), e);
            } catch (IOException e) {
                throw new MetaphaseEditorException(e.getMessage(), e);
            }
        }
    }//GEN-LAST:event_insertTableButtonActionPerformed

    private void openButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openButtonActionPerformed
       try{
            File current = new File(".");
            JFileChooser chooser = new JFileChooser(current);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setFileFilter(new HTMLFileFilter());
            int approval = chooser.showOpenDialog(this);
            if (approval == JFileChooser.APPROVE_OPTION){
                File currentFile = chooser.getSelectedFile();
                if (currentFile.exists()) {
                    FileReader fr = new FileReader(currentFile);
                    Document oldDoc = htmlTextPane.getDocument();
                    if(oldDoc != null)
                        oldDoc.removeUndoableEditListener(undoHandler);
                    htmlDocument = (HTMLDocument)editorKit.createDefaultDocument();
                    editorKit.read(fr, htmlDocument, 0);
                    htmlDocument.addUndoableEditListener(undoHandler);
                    htmlTextPane.setDocument(htmlDocument);
                    resetUndoManager();
                } else {
                    JOptionPane.showMessageDialog(null, "The selected file does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch(BadLocationException e){
            throw new MetaphaseEditorException(e.getMessage(), e);
        } catch(FileNotFoundException e){
            throw new MetaphaseEditorException(e.getMessage(), e);
        } catch(IOException e){
            throw new MetaphaseEditorException(e.getMessage(), e);
        }
    }//GEN-LAST:event_openButtonActionPerformed

    private void pasteAsTextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasteAsTextButtonActionPerformed
	Clipboard clipboard = getToolkit().getSystemClipboard();
        Transferable transferable = clipboard.getContents(null);
        if(transferable !=null) {
            try {
                String plainText = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                plainText = plainText.replaceAll("\\r\\n", "<br/>");
                plainText = plainText.replaceAll("\\n", "<br/>");
                plainText = plainText.replaceAll("\\r", "<br/>");
                new InsertHtmlAction(this, "Paste as Text", "<p>" + plainText + "</p>", Tag.P).actionPerformed(null);
            } catch (UnsupportedFlavorException e) {
                throw new MetaphaseEditorException(e.getMessage(), e);
            } catch (IOException e) {
                throw new MetaphaseEditorException(e.getMessage(), e);
            }
        }
    }//GEN-LAST:event_pasteAsTextButtonActionPerformed

    private void htmlTextPaneMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_htmlTextPaneMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON3) {
            for (int i=0; i<contextMenuListeners.size(); i++) {
                contextMenuListeners.get(i).beforeContextMenuPopup();
            }
            contextMenu.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_htmlTextPaneMouseClicked

    private void anchorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_anchorButtonActionPerformed
        AnchorDialog anchorDialog = new AnchorDialog(null, true);
        String anchorHtml = anchorDialog.showDialog();
        if (anchorHtml != null) {
            new InsertHtmlAction(this, "Anchor", anchorHtml, Tag.A).actionPerformed(evt);
        }
    }//GEN-LAST:event_anchorButtonActionPerformed

    private void insertImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertImageActionPerformed
        try {
            ImageDialog imageDialog = new ImageDialog(null, true);
            String html = imageDialog.showDialog();
            if (html != null) {
                if (imageDialog.isLink()) {
                    editorKit.insertHTML(htmlDocument, htmlTextPane.getCaretPosition(), html, 0, 0, Tag.A);
                } else {
                    editorKit.insertHTML(htmlDocument, htmlTextPane.getCaretPosition(), html, 0, 0, Tag.IMG);
                }
                refreshAfterAction();
            }
        } catch (BadLocationException e) {
            throw new MetaphaseEditorException(e.getMessage(), e);
        } catch (IOException e) {
            throw new MetaphaseEditorException(e.getMessage(), e);
        }
    }//GEN-LAST:event_insertImageActionPerformed

    private void insertRemoveBulletedListButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertRemoveBulletedListButtonActionPerformed
        new HTMLEditorKit.InsertHTMLTextAction("Insert Bulleted List", "<ul><li></li></ul>", Tag.BODY, Tag.UL).actionPerformed(evt);
    }//GEN-LAST:event_insertRemoveBulletedListButtonActionPerformed

    private void htmlTextPaneKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_htmlTextPaneKeyPressed
    }//GEN-LAST:event_htmlTextPaneKeyPressed

    private void htmlTextPaneKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_htmlTextPaneKeyReleased
        
    }//GEN-LAST:event_htmlTextPaneKeyReleased

    private void htmlTextPaneKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_htmlTextPaneKeyTyped
        if (evt.getKeyChar() == 10) {
            // TODO: currently this inserts two list items, fix this. PS it's not because of the two actions below, they will only insert when encountering either a UL or OL
            new HTMLEditorKit.InsertHTMLTextAction("Insert List Item", "<li></li>", Tag.UL, Tag.LI).actionPerformed(null);
            new HTMLEditorKit.InsertHTMLTextAction("Insert List Item", "<li></li>", Tag.OL, Tag.LI).actionPerformed(null);
        }
    }//GEN-LAST:event_htmlTextPaneKeyTyped

    private void createDivButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createDivButtonActionPerformed
        DivDialog divDialog = new DivDialog(null, true);
        String divHtml = divDialog.showDialog();
        if (divHtml != null) {
            try {
                editorKit.insertHTML(htmlDocument, htmlTextPane.getCaretPosition(), divHtml, 0, 0, Tag.DIV);
                refreshAfterAction();
            } catch (BadLocationException e) {
                throw new MetaphaseEditorException(e.getMessage(), e);
            } catch (IOException e) {
                throw new MetaphaseEditorException(e.getMessage(), e);
            }
        }
    }//GEN-LAST:event_createDivButtonActionPerformed

    private void linkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_linkButtonActionPerformed
        LinkDialog linkDialog = new LinkDialog(null, true);
        String html = linkDialog.showDialog();
        if (html != null) {
            new InsertHtmlAction(this, "Anchor", html, Tag.A).actionPerformed(evt);            
        }
    }//GEN-LAST:event_linkButtonActionPerformed

    private void spellcheckButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_spellcheckButtonActionPerformed
        //JOptionPane.showMessageDialog(null, "The spelling checker functionality is currently unavailable.");        
        Thread thread = new Thread() {
            public void run() {
                try {
                    spellChecker.spellCheck(htmlTextPane);
                    JOptionPane.showMessageDialog(null, "The spelling check is complete.", "Check Spelling", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    throw new MetaphaseEditorException(ex.getMessage(), ex);
                }
            }
        };
        thread.start();
    }//GEN-LAST:event_spellcheckButtonActionPerformed

    private void createParagraphButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createParagraphButtonActionPerformed
        new InsertHtmlAction(this, "Paragraph", "<p>TODO: modify paragraph contents</p>", Tag.P).actionPerformed(evt);
    }//GEN-LAST:event_createParagraphButtonActionPerformed

    private void setToolbarFocusActionListener(JComponent component) {
        Component[] vComponents = component.getComponents();
        for (int i=0; i<vComponents.length; i++) {
            if (vComponents[i] instanceof JButton) {
                JButton button = (JButton)vComponents[i];
                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        htmlTextPane.requestFocus();
                    }
                });
            } else if (vComponents[i] instanceof JComboBox) {
                JComboBox comboBox = (JComboBox)vComponents[i];
                comboBox.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent ae) {
                        htmlTextPane.requestFocus();
                    }
                });
            } else if (vComponents[i] instanceof JPanel) {
                JPanel panel = (JPanel)vComponents[i];
                setToolbarFocusActionListener(panel);
            }
        }
    }

    private void setToolbarComponentEnable(JComponent component, boolean enabled) {
        Component[] vComponents = component.getComponents();
        for (int i=0; i<vComponents.length; i++) {
            if (vComponents[i] == sourceButton || vComponents[i] == newButton || vComponents[i] == previewButton || vComponents[i] == aboutButton) {
                return;
            } else if (vComponents[i] instanceof JButton) {
                JButton button = (JButton)vComponents[i];
                button.setEnabled(enabled);
            } else if (vComponents[i] instanceof JComboBox) {
                JComboBox comboBox = (JComboBox)vComponents[i];
                comboBox.setEnabled(enabled);
            } else if (vComponents[i] instanceof JPanel) {
                JPanel panel = (JPanel)vComponents[i];
                setToolbarComponentEnable(panel, enabled);
            }
        }
    }

    public void addContextMenuListener(ContextMenuListener contextMenuListener) {
        contextMenuListeners.add(contextMenuListener);
    }

    public void removeContextMenuListener(ContextMenuListener contextMenuListener) {
        contextMenuListeners.remove(contextMenuListener);
    }

    public void initSpellChecker() {
        try {
            ZipInputStream zipInputStream = null;
            InputStream inputStream = null;
            if (spellCheckDictionaryVersion == SpellCheckDictionaryVersion.CUSTOM) {
                if (customDictionaryFilename == null) {
                    throw new MetaphaseEditorException("The dictionary version has been set to CUSTOM but no custom dictionary file name has been specified.");
                }
                inputStream = new FileInputStream(customDictionaryFilename);
            } else {
                inputStream = new FileInputStream(spellCheckDictionaryVersion.getFile());// this.getClass().getResourceAsStream(spellCheckDictionaryVersion.getFilename());
            }
            zipInputStream = new ZipInputStream(inputStream);
            zipInputStream.getNextEntry();
            dictionary = new SpellDictionaryHashMap(new BufferedReader(new InputStreamReader(zipInputStream)));
            spellChecker = new JTextComponentSpellChecker(dictionary, null, "Check Spelling");
        } catch (FileNotFoundException e) {
            throw new MetaphaseEditorException(e.getMessage(), e);
        } catch (IOException e) {
            throw new MetaphaseEditorException(e.getMessage(), e);
        }
    }

    public void setCustomDictionaryFilename(String customDictionaryFilename) {
        this.customDictionaryFilename = customDictionaryFilename;
    }

    public String getCustomDictionaryFilename() {
        return customDictionaryFilename;
    }

    public void setDictionaryVersion(SpellCheckDictionaryVersion spellCheckDictionaryVersion) {
        this.spellCheckDictionaryVersion = spellCheckDictionaryVersion;

        initSpellChecker();
    }

    public SpellCheckDictionaryVersion getDictionaryVersion() {
        return spellCheckDictionaryVersion;
    }

 
    class SubscriptAction extends StyledEditorKit.StyledTextAction {
        public SubscriptAction() {
            super(StyleConstants.Subscript.toString());
        }

        public void actionPerformed(ActionEvent ae)
        {
            JEditorPane editor = getEditor(ae);
            if (editor != null) {
                boolean subscript = (StyleConstants.isSubscript(getStyledEditorKit(editor).getInputAttributes())) ? false : true;
                SimpleAttributeSet sas = new SimpleAttributeSet();
                StyleConstants.setSubscript(sas, subscript);
                setCharacterAttributes(editor, sas, false);
            }
        }
    }

    class SuperscriptAction extends StyledEditorKit.StyledTextAction
    {
        public SuperscriptAction() {
            super(StyleConstants.Superscript.toString());
        }

        public void actionPerformed(ActionEvent ae) {
            JEditorPane editor = getEditor(ae);
            if (editor != null) {
                StyledEditorKit kit = getStyledEditorKit(editor);
                boolean superscript = (StyleConstants.isSuperscript(kit.getInputAttributes())) ? false : true;
                SimpleAttributeSet sas = new SimpleAttributeSet();
                StyleConstants.setSuperscript(sas, superscript);
                setCharacterAttributes(editor, sas, false);
            }
        }
    }

    class StrikeThroughAction extends StyledEditorKit.StyledTextAction
    {
        public StrikeThroughAction() {
            super(StyleConstants.StrikeThrough.toString());
        }

        public void actionPerformed(ActionEvent ae) {
            JEditorPane editor = getEditor(ae);
            if (editor != null) {
                StyledEditorKit kit = getStyledEditorKit(editor);
                boolean strikeThrough = (StyleConstants.isStrikeThrough(kit.getInputAttributes())) ? false : true;
                SimpleAttributeSet sas = new SimpleAttributeSet();
                StyleConstants.setStrikeThrough(sas, strikeThrough);
                setCharacterAttributes(editor, sas, false);
            }
        }
    }

    class BackgroundColorAction extends StyledEditorKit.StyledTextAction
    {
        private Color color;
        public BackgroundColorAction(Color color) {
            super(StyleConstants.StrikeThrough.toString());
            this.color = color;
        }

        public void actionPerformed(ActionEvent ae) {
            JEditorPane editor = getEditor(ae);
            if (editor != null) {
                SimpleAttributeSet sas = new SimpleAttributeSet();
                StyleConstants.setBackground(sas, color);
                setCharacterAttributes(editor, sas, false);
            }
        }
    }

    
    class UndoHandler implements UndoableEditListener {
        /**
         * Messaged when the Document has created an edit, the edit is
         * added to <code>undo</code>, an instance of UndoManager.
         */
        public void undoableEditHappened(UndoableEditEvent e) {
            undo.addEdit(e.getEdit());
            undoAction.update();
            redoAction.update();
        }
    }

    class UndoAction extends AbstractAction {
        public UndoAction() {
            super("Undo");
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent actionEvent) {
            try {
                undo.undo();
            } catch (CannotUndoException e) {
                throw new MetaphaseEditorException(e.getMessage(), e);
            }
            update();
            redoAction.update();
        }

        protected void update() {
            if(undo.canUndo()) {
                setEnabled(true);
            }else {
                setEnabled(false);
            }
        }
    }

    class RedoAction extends AbstractAction {

        public RedoAction() {
            super("Redo");
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent actionEvent) {
            try {
                undo.redo();
            } catch (CannotRedoException e) {
                throw new MetaphaseEditorException(e.getMessage(), e);
            }
            update();
            undoAction.update();
        }

        protected void update() {
            if(undo.canRedo()) {
                setEnabled(true);
            }else {
                setEnabled(false);
            }
        }
    }

    class HTMLFileFilter extends javax.swing.filechooser.FileFilter{
        public boolean accept(File f){
            return ((f.isDirectory()) ||(f.getName().toLowerCase().indexOf(".htm") > 0));
        }

        public String getDescription(){
            return "html";
        }
    }

    class ParagraphFormatListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
            Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (index == 0) {
                component.setEnabled(false);
            }
            ParagraphFormat paragraphFormat = (ParagraphFormat)value;
            if (paragraphFormat.getTag() != null) {
                JLabel label = (JLabel)component;
               // label.setText("<html><" + paragraphFormat.getTag().toString() + ">" + label.getText() + "</" + paragraphFormat.getTag().toString() + ">");
            }
            return component;
        }        
    }

    class FontListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
            Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (index == 0) {
                component.setEnabled(false);
            }
            FontItem fontItem = (FontItem)value;
            if (fontItem.getFontName() != null) {
                Font currentFont = component.getFont();
                component.setFont(new Font(fontItem.getFontName(), currentFont.getStyle(), currentFont.getSize()));
            }
            return component;
        }
    }

    class FontSizeListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
            Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);            
            if (index == 0) {
                component.setEnabled(false);
            }
            FontSize fontSize = (FontSize)value;
            if (fontSize.getSize() != -1) {
                Font currentFont = component.getFont();
                component.setFont(new Font(currentFont.getName(), currentFont.getStyle(), fontSize.getSize()));
            }
            return component;
        }
    }

    class DefaultEditorMouseMotionListener implements MouseMotionListener {

        public void mouseDragged(MouseEvent me) {
            int pos = htmlTextPane.viewToModel(me.getPoint());

            if (pos >= 0) {
                Element element = htmlDocument.getParagraphElement(pos);
                MutableAttributeSet attributes = new SimpleAttributeSet(element.getAttributes());

                EditorMouseEvent editorMouseEvent = new EditorMouseEvent();
                editorMouseEvent.setNearestParagraphAttributes(attributes);
                for (int i=0; i<editorMouseMotionListeners.size(); i++) {
                    editorMouseMotionListeners.get(i).mouseDragged(editorMouseEvent);
                }
            }
        }

        public void mouseMoved(MouseEvent me) {
            int pos = htmlTextPane.viewToModel(me.getPoint());

            if (pos >= 0) {
                Element element = htmlDocument.getParagraphElement(pos);
                MutableAttributeSet attributes = new SimpleAttributeSet(element.getAttributes());

                EditorMouseEvent editorMouseEvent = new EditorMouseEvent();
                editorMouseEvent.setNearestParagraphAttributes(attributes);
                for (int i=0; i<editorMouseMotionListeners.size(); i++) {
                    editorMouseMotionListeners.get(i).mouseMoved(editorMouseEvent);
                }
            }
        }
    }
}
