/*
 * Copyright (C) 2006-2013 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package org.talend.dataquality.matchmerge.mfb;

import org.talend.dataquality.matchmerge.MatchMergeAlgorithm;
import org.talend.dataquality.matchmerge.Record;

class DefaultCallback implements MatchMergeAlgorithm.Callback {

    static MatchMergeAlgorithm.Callback INSTANCE = new DefaultCallback();

    private DefaultCallback() {
    }

    @Override
    public void onBeginRecord(Record record) {
    }

    @Override
    public void onMatch(Record record1, Record record2, MatchResult matchResult) {
    }

    @Override
    public void onNewMerge(Record record) {
    }

    @Override
    public void onRemoveMerge(Record record) {
    }

    @Override
    public void onDifferent(Record record1, Record record2, MatchResult matchResult) {
    }

    @Override
    public void onEndRecord(Record record) {
    }

    @Override
    public boolean isInterrupted() {
        return false;
    }

    @Override
    public void onBeginProcessing() {
    }

    @Override
    public void onEndProcessing() {
    }

    @Override
    public void onBeginPostMergeProcess() {
    }

    @Override
    public void onEndPostMergeProcess() {
    }
}