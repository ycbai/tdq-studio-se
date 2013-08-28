// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.record.linkage.ui.composite.tableviewer;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.record.linkage.ui.action.MatchRuleActionGroup;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.provider.MatchAnalysisTableContentProvider;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.provider.MatchRuleLabelProvider;
import org.talend.dataquality.record.linkage.ui.composite.utils.MatchRuleAnlaysisUtils;
import org.talend.dataquality.record.linkage.ui.i18n.internal.DefaultMessagesImpl;
import org.talend.dataquality.record.linkage.utils.HandleNullEnum;
import org.talend.dataquality.record.linkage.utils.MatchingTypeEnum;
import org.talend.dataquality.rules.KeyDefinition;
import org.talend.dataquality.rules.MatchKeyDefinition;
import org.talend.dataquality.rules.MatchRule;
import org.talend.dataquality.rules.MatchRuleDefinition;
import org.talend.dataquality.rules.impl.RulesFactoryImpl;

/**
 * created by zshen on Jul 31, 2013 Detailled comment
 *
 */
public class MatchRuleTableViewer extends AbstractMatchAnalysisTableViewer {

    private static Logger log = Logger.getLogger(MatchRuleTableViewer.class);

    private MatchRule matchRule = null;

    /**
     * DOC zshen MatchRuleTableViewer constructor comment.
     *
     * @param parent
     * @param style
     */
    public MatchRuleTableViewer(Composite parent, int style, boolean isAddColumn) {
        super(parent, style, isAddColumn);

    }

    /**
     * DOC zshen Comment method "getCellEditor".
     *
     * @param headers
     * @return
     */
    @Override
    protected CellEditor[] getCellEditor(List<String> headers) {
        CellEditor[] editors = new CellEditor[headers.size()];
        for (int i = 0; i < editors.length; ++i) {
            switch (i) {
            case 2:
                editors[i] = new ComboBoxCellEditor(innerTable, MatchingTypeEnum.getAllTypes(), SWT.READ_ONLY);
                break;
            case 5:
                editors[i] = new ComboBoxCellEditor(innerTable, HandleNullEnum.getAllTypes(), SWT.READ_ONLY);
                break;
            default:
                editors[i] = new TextCellEditor(innerTable);
            }
        }
        return editors;
    }

    /**
     * DOC zshen Comment method "getTableLabelProvider".
     *
     * @return
     */
    @Override
    protected IBaseLabelProvider getTableLabelProvider() {
        return new MatchRuleLabelProvider();
    }

    /**
     * DOC zshen Comment method "getTableContentProvider".
     *
     * @return
     */
    @Override
    protected IContentProvider getTableContentProvider() {
        return new MatchAnalysisTableContentProvider();
    }

    /**
     * DOC zshen Comment method "getCellModifier".
     *
     * @return
     */
    @Override
    protected ICellModifier getTableCellModifier() {
        return new MatchRuleCellModifier(this);
    }

    /**
     *
     * add new Element
     *
     * @param columnName the name of column
     * @param analysis the context of this add operation perform on.
     */
    @Override
    public boolean addElement(String columnName, Analysis analysis) {
        if (matchRule == null) {
            log.error(DefaultMessagesImpl.getString("MatchRuleTableViewer.NULL_MATCH_RULE_INSTANCE") + analysis.getName()); //$NON-NLS-1$
            return false;
        }
        MatchRuleDefinition matchRuleDef = RulesFactoryImpl.eINSTANCE.createMatchRuleDefinition();
        return addElement(columnName, matchRuleDef);
    }

    @Override
    public void removeElement(String columnName, Analysis analysis) {
        EList<MatchKeyDefinition> matchKeys = matchRule.getMatchKeys();
        Iterator<MatchKeyDefinition> matchKeyIterator = matchKeys.iterator();
        while (matchKeyIterator.hasNext()) {
            KeyDefinition keyDef = matchKeyIterator.next();
            if (StringUtils.equals(keyDef.getColumn(), columnName)) {
                matchKeys.remove(keyDef);
                remove(keyDef);
                break;
            }
        }
    }

    /**
     * use this value to compute the vaule of column width
     *
     * @return
     */
    @Override
    protected int getHeaderDisplayWeight() {
        return 8;
    }

    private boolean isAddedAlready(String columnName) {
        if (this.getInput() instanceof MatchRule) {
            MatchRule inputElement = (MatchRule) this.getInput();
            for (MatchKeyDefinition element : inputElement.getMatchKeys()) {
                if (element.getColumn().equals(columnName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * DOC zhao Comment method "setMatchRule".
     *
     * @param matchRule2
     */
    public void setMatchRule(MatchRule matchRule) {
        this.matchRule = matchRule;
    }

    @Override
    public void addContextMenu() {
        MatchRuleActionGroup actionGroup = new MatchRuleActionGroup(this);
        actionGroup.fillContextMenu(new MenuManager());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.talend.dataquality.record.linkage.ui.composite.tableviewer.AbstractMatchAnalysisTableViewer#addElement(java
     * .lang.String, org.talend.dataquality.rules.MatchRuleDefinition)
     */
    @Override
    public boolean addElement(String columnName, MatchRuleDefinition matchRuleDef) {
        MatchKeyDefinition newMatchkey = MatchRuleAnlaysisUtils.createDefaultMatchRow(columnName);
        matchRule.getMatchKeys().add(newMatchkey);
        add(newMatchkey);
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.talend.dataquality.record.linkage.ui.composite.tableviewer.AbstractMatchAnalysisTableViewer#removeElement
     * (org.talend.dataquality.rules.KeyDefinition, org.talend.dataquality.analysis.Analysis)
     */
    @Override
    public void removeElement(KeyDefinition keyDef, Analysis analysis) {
        MatchRuleDefinition matchRuleDef = RulesFactoryImpl.eINSTANCE.createMatchRuleDefinition();
        removeElement(keyDef, matchRuleDef);

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.talend.dataquality.record.linkage.ui.composite.tableviewer.AbstractMatchAnalysisTableViewer#removeElement
     * (org.talend.dataquality.rules.KeyDefinition, org.talend.dataquality.rules.MatchRuleDefinition)
     */
    @Override
    public void removeElement(KeyDefinition keyDef, MatchRuleDefinition matchRuleDef) {
        EList<MatchKeyDefinition> matchKeys = matchRule.getMatchKeys();
        Iterator<MatchKeyDefinition> matchKeyIterator = matchKeys.iterator();
        while (matchKeyIterator.hasNext()) {
            KeyDefinition tempkeyDef = matchKeyIterator.next();
            if (StringUtils.equals(keyDef.getName(), tempkeyDef.getName())) {
                matchKeys.remove(keyDef);
                remove(keyDef);
                break;
            }
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.talend.dataquality.record.linkage.ui.composite.tableviewer.AbstractMatchAnalysisTableViewer#moveUpElement
     * (org.talend.dataquality.rules.KeyDefinition, org.talend.dataquality.rules.MatchRuleDefinition)
     */
    @Override
    public void moveUpElement(KeyDefinition keyDef, MatchRuleDefinition matchRuleDef) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.talend.dataquality.record.linkage.ui.composite.tableviewer.AbstractMatchAnalysisTableViewer#moveDownElement
     * (org.talend.dataquality.rules.KeyDefinition, org.talend.dataquality.rules.MatchRuleDefinition)
     */
    @Override
    public void moveDownElement(KeyDefinition keyDef, MatchRuleDefinition matchRuleDef) {
        // TODO Auto-generated method stub

    }

}