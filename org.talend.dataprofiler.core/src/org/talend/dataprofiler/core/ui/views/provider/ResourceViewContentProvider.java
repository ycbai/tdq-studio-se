// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.views.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.ui.action.provider.NewSourcePatternActionProvider;
import org.talend.dataprofiler.core.ui.utils.ComparatorsFactory;
import org.talend.dataprofiler.ecos.jobs.ComponentSearcher;
import org.talend.dataquality.PluginConstant;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dq.helper.resourcehelper.PatternResourceFileHelper;

/**
 * DOC rli class global comment. Detailled comment
 */
public class ResourceViewContentProvider extends WorkbenchContentProvider {

    private static Logger log = Logger.getLogger(ResourceViewContentProvider.class);

    // private static Logger log =
    // Logger.getLogger(ResourceViewContentProvider.class);

    private List<IContainer> needSortContainers;

    /**
     * DOC rli ResourceViewContentProvider constructor comment.
     */
    public ResourceViewContentProvider() {
        super();
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        needSortContainers = new ArrayList<IContainer>();
        // MOD mzhao 2009-03-20, Feature 6066.
        needSortContainers.add(root.getProject(PluginConstant.getRootProjectName()).getFolder(
                DQStructureManager.getDataProfiling()).getFolder(DQStructureManager.ANALYSIS));
        needSortContainers.add(root.getProject(PluginConstant.getRootProjectName()).getFolder(
                DQStructureManager.getDataProfiling()).getFolder(DQStructureManager.REPORTS));
        needSortContainers.add(root.getProject(PluginConstant.getRootProjectName()).getFolder(DQStructureManager.getMetaData())
                .getFolder(DQStructureManager.DB_CONNECTIONS));
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.ui.internal.navigator.resources.workbench.
     * ResourceExtensionContentProvider#getChildren(java.lang.Object)
     */
    @Override
    public Object[] getChildren(Object element) {
        // ~MOD mzhao 2009-03-20,Feature 6066.
        if (element instanceof IWorkspaceRoot) {
            Object currentOpenProject = null;
            for (Object child : super.getChildren(element)) {
                if (child instanceof IProject) {
                    if (((IProject) child).getName().equals(PluginConstant.getRootProjectName())) {
                        currentOpenProject = child;
                        break;
                    }
                }
            }
            List<Object> folders = new ArrayList<Object>();
            try {
                Object[] rootFolders = new Object[0];
                rootFolders = ((IProject) currentOpenProject).members(false);
                for (Object folder : rootFolders) {
                    if (folder instanceof IFolder && ((IFolder) folder).getName().startsWith(DQStructureManager.PREFIX_TDQ)) {
                        // ~ MOD mzhao 2009-04-13, Move reporting db folder into project folder.
                        if (((IFolder) folder).getName().trim().equals("TDQ_reporting_db")) {
                            continue;
                        }
                        // ~
                        folders.add(folder);
                    }
                }
            } catch (CoreException e) {
                log.error(e);
            }
            return folders.toArray();
        } else if (element instanceof IFile) {
            IFile file = (IFile) element;
            if (file.getName().endsWith(NewSourcePatternActionProvider.EXTENSION_PATTERN)) {
                Pattern pattern = PatternResourceFileHelper.getInstance().findPattern(file);
                return new Object[] { pattern };
            }
        } else if (element instanceof IFolder) {
            IFolder folder = (IFolder) element;
            if (folder.getName().equals(DQStructureManager.EXCHANGE)) {
                String category = "1";
                String version = CorePlugin.getDefault().getProductVersion().toString();
                return ComponentSearcher.getAvailableComponentExtensions(version, category).toArray();
            }
        }
        if (needSortContainers.contains(element)) {
            return sort(super.getChildren(element));
        }
        return super.getChildren(element);
    }

    /**
     * Sort the parameter objects, and return the sorted array.
     * 
     * @param elements
     * @return
     */
    @SuppressWarnings("unchecked")
    protected Object[] sort(Object[] elements) {
        if (elements == null) {
            return elements;
        }
        List<IResource> list = new ArrayList<IResource>();
        for (Object element : elements) {
            list.add((IResource) element);
            // if (element instanceof IFile) {
            // list.add((IFile) element);
            // } else {
            // log.error("The elemnt:" + ((IFolder) element).getFullPath() +
            // " can't display on the workspace!");
            // }
        }

        Collections.sort(list, ComparatorsFactory.buildComparator(ComparatorsFactory.FILEMODEL_COMPARATOR_ID));
        return list.toArray();
    }
}
