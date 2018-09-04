package com.codecool.am_i_tea.text_editor;

import com.codecool.am_i_tea.text_editor.editor_utility.BulletsUtility;
import com.codecool.am_i_tea.text_editor.editor_utility.DocumentUtility;
import com.codecool.am_i_tea.text_editor.editor_utility.NumbersUtility;
import com.codecool.am_i_tea.text_editor.editor_utility.ParaUtility;

import javax.swing.text.Element;
import javax.swing.text.StyledDocument;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NumbersActionListener implements ActionListener {

    private MyEditor myEditor;
    private DocumentUtility documentUtility;
    private ParaUtility paraUtility;
    private NumbersUtility numbersUtility;

    private MyEditor.NumbersActionType numbersActionType;
    private int n;

    public NumbersActionListener(MyEditor myEditor,
                                 MyEditor.NumbersActionType actionType,
                                 DocumentUtility documentUtility,
                                 ParaUtility paraUtility,
                                 NumbersUtility numbersUtility) {

        this.myEditor = myEditor;
        this.documentUtility = documentUtility;
        this.paraUtility = paraUtility;
        this.numbersUtility = numbersUtility;
        numbersActionType = actionType;
    }

    /*
     * Common routine for insert and remove numbers actions. This routine
     * loops thru the selected text and inserts or removes a number.
     * - For insert action: inserts a number at the beginning of each para
     * of selected text. The paras already bulleted or numbered are ignored.
     *  Note that the numbering always starts from 1.
     * - For remove action: removes the number in case a para is numbered
     * for the selected text.
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        StyledDocument doc = documentUtility.getEditorDocument();
        String selectedText = myEditor.editor__.getSelectedText();

        if ((selectedText == null) || (selectedText.trim().isEmpty())) {

            myEditor.editor__.requestFocusInWindow();
            return;
        }

        Element paraEle = doc.getParagraphElement(myEditor.editor__.getSelectionStart());
        int paraEleStart = paraEle.getStartOffset();
        int paraEleEnd = 0;
        boolean firstPara = true;

        NUMBERS_PARA_LOOP:
        do {
            paraEle = doc.getParagraphElement(paraEleStart);
            paraEleEnd = paraEle.getEndOffset();

            if ((paraEleEnd - paraEleStart) <= 1) { // empty line

                if (firstPara) {

                    firstPara = false;
                    n = 0;
                }

                paraEleStart = paraEleEnd;
                paraEle = doc.getParagraphElement(paraEleStart);
                continue NUMBERS_PARA_LOOP;
            }

            switch (numbersActionType) {

                case INSERT:

                    if (paraUtility.isBulletedPara(paraEleStart)) {

                        break; // switch
                    }

                    if (firstPara) {

                        firstPara = false;
                        n = 0;
                    }

                    if (paraUtility.isNumberedPara(paraEleStart)) {

                        // remove any existing number
                        numbersUtility.removeNumber(paraEleStart, numbersUtility.getNumberLength(paraEleStart));
                    }

                    if (! paraUtility.isNumberedPara(paraEleStart)) {

                        Integer nextN = new Integer(++n);
                        numbersUtility.insertNumber(paraEleStart, paraEleStart, nextN);
                    }

                    break; // switch

                case REMOVE:

                    if (paraUtility.isNumberedPara(paraEleStart)) {

                        numbersUtility.removeNumber(paraEleStart, numbersUtility.getNumberLength(paraEleStart));
                    }
            }

            // Get the updated para element details after numbering
            paraEle = doc.getParagraphElement(paraEleStart);
            paraEleEnd = paraEle.getEndOffset();

            paraEleStart = paraEleEnd;

        } while (paraEleEnd <= myEditor.editor__.getSelectionEnd());
        // NUMBERS_PARA_LOOP

        myEditor.editor__.requestFocusInWindow();
    }
}
