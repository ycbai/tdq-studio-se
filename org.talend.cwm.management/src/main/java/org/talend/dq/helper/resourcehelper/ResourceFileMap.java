// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.helper.resourcehelper;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.top.repository.ProxyRepositoryManager;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC rli class global comment. Detailled comment
 */
public abstract class ResourceFileMap {

    protected Map<IFile, Resource> registedResourceMap = new HashMap<IFile, Resource>();

    public void register(IFile fileString, Resource resource) {
        registedResourceMap.put(fileString, resource);
    }

    public boolean exist(IFile file) {
        return registedResourceMap.get(file) != null;
    }

    public void remove(IFile file) {
        registedResourceMap.remove(file);
    }

    public void clear() {
        registedResourceMap.clear();
    }

    /**
     * DOC rli Comment method "createResource".
     * 
     * @param file
     * @return
     */
    public synchronized Resource getFileResource(IFile file) {
        Resource res;

        if (exist(file)) {
            res = registedResourceMap.get(file);
        } else {
            URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), false);
            res = EMFSharedResources.getInstance().getResource(uri, true);
            register(file, res);
        }

        return res;
    }

    /**
     * DOC zshen Comment method "createResource".
     * 
     * @param file
     * @return
     */
    public synchronized Resource getFileResource(IFile file, boolean reload) {
        Resource res;

        if (exist(file)) {
            res = registedResourceMap.get(file);
        } else {
            URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), false);
            if (reload) {
                res = EMFSharedResources.getInstance().reloadResource(uri);
            } else {
                res = EMFSharedResources.getInstance().getResource(uri, false);
            }
            register(file, res);
        }

        return res;
    }

    /**
     * DOC bZhou Comment method "delete".
     * 
     * @param ifile
     * @throws Exception
     */
    public ReturnCode delete(IFile ifile) throws Exception {
        ReturnCode rc = new ReturnCode();

        Resource resource = getFileResource(ifile);

        List<ModelElement> dependencyClients = EObjectHelper.getDependencyClients(ifile);

        if (!dependencyClients.isEmpty()) {
            rc.setOk(false);
            rc.setMessage("This item is depended by [ " + dependencyClients.get(0).getName() + " ]! it can't be deleted!");
        } else {
            // remove dependency
            EObjectHelper.removeDependencys(ifile);

            // a hook to deal with others
            deleteRelated(ifile);

            // unload resource
            String uriStr = resource.getURI().toString();
            remove(ifile);
            EMFSharedResources.getInstance().unloadResource(uriStr);

            // delete file physically
            if (ifile.isLinked()) {
                File file = new File(ifile.getRawLocation().toOSString());
                if (file.exists()) {
                    file.delete();
                }
            }
            ifile.delete(true, new NullProgressMonitor());

            IFile propFile = ResourcesPlugin.getWorkspace().getRoot().getFile(
                    ifile.getFullPath().removeFileExtension().addFileExtension(FactoriesUtil.PROPERTIES_EXTENSION));
            // MOD qiongli bug 14575.unloadResource for propFile
            resource = getFileResource(propFile);
            remove(propFile);
            EMFSharedResources.getInstance().unloadResource(resource.getURI().toString());

            if (propFile.exists()) {
                propFile.delete(true, new NullProgressMonitor());
            }

            // svn commit
            ProxyRepositoryManager.getInstance().save();

            // finish
            ifile.getParent().refreshLocal(IResource.DEPTH_INFINITE, null);
            rc.setMessage("Delete file successfully!");
        }

        return rc;
    }

    /**
     * DOC bZhou Comment method "getModelElement".
     * 
     * @param resource
     * @return
     */
    public final ModelElement getModelElement(Resource resource) {
        EList<EObject> contents = resource.getContents();
        if (contents != null && !contents.isEmpty()) {
            return (ModelElement) contents.get(0);
        }

        return null;
    }

    /**
     * DOC bZhou Comment method "getModelElement".
     * 
     * @param IFile
     * @return
     */
    public final ModelElement getModelElement(IFile file) {
        Resource fileResource = getFileResource(file);

        return getModelElement(fileResource);
    }

    protected abstract void deleteRelated(IFile file);

    protected abstract boolean checkFile(IFile file);

    public abstract IFile findCorrespondingFile(ModelElement element);

}
