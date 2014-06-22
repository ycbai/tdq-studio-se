// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.action;

import java.io.File;
import java.net.URI;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;
import org.talend.commons.utils.VersionUtils;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dq.helper.FileUtils;
import org.talend.dq.helper.ProxyRepositoryManager;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.RepositoryNode;
import org.talend.resource.ResourceManager;

/**
 * DOC bZhou class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 55206 2011-02-15 17:32:14Z bzhou $
 * 
 */
public abstract class AbstractImportFileAction extends Action implements ICheatSheetAction {

    protected RepositoryNode node;

    public AbstractImportFileAction(RepositoryNode node) {
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.IMPORT));
        this.node = node;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        if (node != null) {
            try {

                Map<File, IPath> resultMap = computeFilePath();

                if (resultMap != null) {
                    for (File file : resultMap.keySet()) {
                        // MOD msjian TDQ-4608 2012-3-6: when the file is *.jasper, copy it.
                        IPath path = resultMap.get(file);
                        if (file.getName().endsWith(PluginConstant.JASPER_STRING)) {
                            IPath fullPath = ResourceManager.getJRXMLFolder().getFullPath();
                            URI locationURI = ResourceManager.getRoot().findMember(fullPath).getLocationURI();
                            File targetFile = new File(locationURI.getPath() + System.getProperty("file.separator") + path //$NON-NLS-1$
                                    + System.getProperty("file.separator") + file.getName()); //$NON-NLS-1$
                            org.apache.commons.io.FileUtils.copyFile(file, targetFile);
                        } else {
                            createItem(file, path);
                        }
                        // TDQ-4608~
                    }
                }

            } catch (Exception e) {
                ExceptionHandler.process(e);
            }

            saveAndRefresh();
        }
    }

    /**
     * DOC bZhou Comment method "createItem".
     * 
     * @param initFile
     * @param path
     * @return
     * @throws Exception
     */
    protected Item createItem(File initFile, IPath path) throws Exception {
        Property property = PropertiesFactory.eINSTANCE.createProperty();
        property.setVersion(VersionUtils.DEFAULT_VERSION);
        property.setStatusCode(PluginConstant.EMPTY_STRING);
        property.setLabel(FileUtils.getName(initFile));

        Item item = initItem(initFile);
        if (item != null) {
            item.setProperty(property);
            property.setItem(item);

            IProxyRepositoryFactory repositoryFactory = ProxyRepositoryFactory.getInstance();
            property.setId(repositoryFactory.getNextId());
            if (path != null) {
                repositoryFactory.createParentFoldersRecursively(ERepositoryObjectType.getItemType(item), path);
            }
            repositoryFactory.create(item, path, true);
        }
        return item;
    }

    /**
     * DOC bZhou Comment method "saveAndRefresh".
     */
    private void saveAndRefresh() {
        ProxyRepositoryManager.getInstance().save();
        CorePlugin.getDefault().refreshDQView(node);
        CorePlugin.getDefault().refreshWorkSpace();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.cheatsheets.ICheatSheetAction#run(java.lang.String[],
     * org.eclipse.ui.cheatsheets.ICheatSheetManager)
     */
    public void run(String[] params, ICheatSheetManager manager) {
        // ADD xqliu TDQ-4285 2011-12-27
        if (!CheatSheetActionHelper.canRun()) {
            return;
        }
        // ~ TDQ-4285

        run();
    }

    public abstract Map<File, IPath> computeFilePath() throws Exception;

    public abstract Item initItem(File srcFile) throws Exception;

    public abstract ERepositoryObjectType getRepositoryType();
}
