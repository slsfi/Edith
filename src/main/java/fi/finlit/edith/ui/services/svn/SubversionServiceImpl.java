/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services.svn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNRevisionProperty;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNFileRevision;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;

import fi.finlit.edith.EDITH;

/**
 * SubversionServiceImpl is the default implementation of the SubversionService interface
 *
 * @author tiwe
 * @version $Id$
 */
public class SubversionServiceImpl implements SubversionService {

    private static final Logger logger = LoggerFactory.getLogger(SubversionServiceImpl.class);

    static {
        FSRepositoryFactory.setup();
    }

    private SVNClientManager clientManager;

    private final String documentRoot;

    private final File readCache;

    private final SVNURL repoSvnURL;

    private final File svnCache;

    private final File svnRepo;

    private SVNRepository svnRepository;

    private final String teiMaterialRoot;

    private final File workingCopies;

    public SubversionServiceImpl(@Inject @Symbol(EDITH.SVN_CACHE_DIR) File svnCache,
            @Inject @Symbol(EDITH.REPO_FILE_PROPERTY) File svnRepo,
            @Inject @Symbol(EDITH.REPO_URL_PROPERTY) String repoURL,
            @Inject @Symbol(EDITH.SVN_DOCUMENT_ROOT) String documentRoot,
            @Inject @Symbol(EDITH.TEI_MATERIAL_ROOT) String materialTeiRoot) {
        clientManager = SVNClientManager.newInstance();
        this.svnCache = svnCache;
        readCache = new File(svnCache + "/readCache");
        workingCopies = new File(svnCache + "/workingCopies");
        this.svnRepo = svnRepo;
        this.documentRoot = documentRoot;
        teiMaterialRoot = materialTeiRoot;
        try {
            repoSvnURL = SVNURL.parseURIEncoded(repoURL);
        } catch (SVNException e) {
            throw new SubversionException(e);
        }
        svnRepository = null;
    }

    public void setClientManager(SVNClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @SuppressWarnings("deprecation")
    public void checkout(File destination, long revision) {
        try {
            clientManager.getUpdateClient().doCheckout(repoSvnURL.appendPath(documentRoot, true),
                    destination, SVNRevision.create(revision), SVNRevision.create(revision), true);
        } catch (SVNException s) {
            throw new SubversionException(s.getMessage(), s);
        }
    }

    @SuppressWarnings("deprecation")
    public long commit(File file) {
        try {
            return clientManager
                    .getCommitClient()
                    .doCommit(new File[] { file }, true, file.getName() + " committed", false,
                            true).getNewRevision();
        } catch (SVNException s) {
            throw new SubversionException(s.getMessage(), s);
        }
    }

    @Override
    public long commit(String svnPath, long revision, String username, UpdateCallback callback) {
        File userCheckout = new File(workingCopies + "/" + username);
        String path = svnPath.substring(documentRoot.length());
        if (userCheckout.exists()) {
            // long updateStart = System.currentTimeMillis();
            update(userCheckout);
            // System.err.println("Update finished in: " + (System.currentTimeMillis() -
            // updateStart));
        } else {
            // long checkoutStart = System.currentTimeMillis();
            checkout(userCheckout, revision);
            // System.err.println("Checkout finished in: " + (System.currentTimeMillis() -
            // checkoutStart));
        }
        File file = new File(userCheckout + "/" + path);
        File tmp = null;
        try {
            tmp = File.createTempFile(UUID.randomUUID().toString(), null);
            InputStream is = new FileInputStream(file);
            OutputStream os = new FileOutputStream(tmp);
            try {
                callback.update(is, os);
            } finally {
                os.close();
                is.close();
            }
            FileUtils.copyFile(tmp, file);
        } catch (IOException e) {
            throw new SubversionException(e);
        } finally {
            if (tmp != null && !tmp.delete()) {
                logger.error("Delete of " + tmp.getAbsolutePath() + " failed");
            }
        }
        // long commitStart = System.currentTimeMillis();
        long newRevision = commit(file);
        // System.err.println("Commit finished in: " + (System.currentTimeMillis() - commitStart));
        return newRevision != -1 ? newRevision : getLatestRevision();
    }

    @Override
    public void delete(String svnPath) {
        try {
            SVNURL targetURL = repoSvnURL.appendPath(svnPath, false);
            logger.info(clientManager.getCommitClient()
                    .doDelete(new SVNURL[] { targetURL }, "removed " + svnPath).toString());
        } catch (SVNException e) {
            throw new SubversionException(e.getMessage(), e);
        }
    }

    @Override
    public void destroy() {
        try {
            svnRepository.closeSession();
            svnRepository = null;
            FileUtils.deleteDirectory(svnCache);
            FileUtils.deleteDirectory(svnRepo);
        } catch (IOException e) {
            throw new SubversionException(e.getMessage(), e);
        }
    }

    @Override
    public Map<String, String> getEntries(String svnFolder, long revision) {
        try {
            List<SVNDirEntry> entries = new ArrayList<SVNDirEntry>();
            svnRepository.getDir(svnFolder, revision, false, entries);
            Map<String, String> rv = new HashMap<String, String>(entries.size());
            for (SVNDirEntry entry : entries) {
                if (entry.getKind().equals(SVNNodeKind.DIR)) {
                    rv.putAll(getEntries(svnFolder + "/" + entry.getName(), revision));
                } else {
                    rv.put(svnFolder + "/" + entry.getName(), entry.getName());
                }
            }
            return rv;
        } catch (SVNException s) {
            throw new SubversionException(s.getMessage(), s);
        }
    }

    private List<SVNFileRevision> getFileRevisions(String svnPath) throws SVNException {
        long latest = svnRepository.getLatestRevision();
        List<SVNFileRevision> revisions = new ArrayList<SVNFileRevision>();
        svnRepository.getFileRevisions(svnPath, revisions, 0, latest);
        return revisions;
    }

    @Override
    public long getLatestRevision() {
        try {
            return svnRepository.getLatestRevision();
        } catch (SVNException s) {
            throw new SubversionException(s.getMessage(), s);
        }
    }

    @Override
    public long getLatestRevision(String svnPath) {
        try {
            List<SVNFileRevision> revisions = getFileRevisions(svnPath);
            long revision = 0;
            for (SVNFileRevision rev : revisions) {
                if (revision < rev.getRevision()) {
                    revision = rev.getRevision();
                }
            }
            return revision;
        } catch (SVNException e) {
            throw new SubversionException(e);
        }
    }

    @Override
    public List<RevisionInfo> getRevisions(String svnPath) {
        try {
            List<SVNFileRevision> revisions = getFileRevisions(svnPath);
            List<RevisionInfo> revisionInfos = new ArrayList<RevisionInfo>(revisions.size());
            for (SVNFileRevision rev : revisions) {
                long svnRevision = rev.getRevision();
                String created = rev.getRevisionProperties().getStringValue(
                        SVNRevisionProperty.DATE);
                String creator = rev.getRevisionProperties().getStringValue(
                        SVNRevisionProperty.AUTHOR);
                revisionInfos.add(new RevisionInfo(svnRevision, created, creator));
            }
            return revisionInfos;
        } catch (SVNException s) {
            throw new SubversionException(s.getMessage(), s);
        }
    }

    @Override
    public InputStream getStream(String svnPath, long revision) throws IOException {
        try {
            long rev = revision;
            if (rev == -1) {
                rev = getLatestRevision(svnPath);
            }
            File documentFolder = new File(readCache, URLEncoder.encode(svnPath, "UTF-8"));
            File documentFile = new File(documentFolder, String.valueOf(rev));
            if (!documentFile.exists()) {
                if (!documentFolder.exists() && !documentFolder.mkdirs()) {
                    throw new IOException("Could not create directory: "
                            + documentFolder.getAbsolutePath());
                }
                OutputStream out = new FileOutputStream(documentFile);
                try {
                    svnRepository.getFile(svnPath, rev, null, out);
                } finally {
                    out.close();
                }
            }
            if (documentFile.isFile()) {
                return new FileInputStream(documentFile);
            }
            return null;
        } catch (SVNException s) {
            throw new SubversionException(s.getMessage(), s);
        }

    }

    @SuppressWarnings("deprecation")
    @Override
    public long importFile(String svnPath, File file) {
        try {
            return clientManager
                    .getCommitClient()
                    .doImport(file, repoSvnURL.appendPath(svnPath, false), svnPath + " added",
                            true).getNewRevision();
        } catch (SVNException s) {
            throw new SubversionException(s.getMessage(), s);
        }

    }

    @Override
    public void initialize() {
        logger.info("Initializing SVN repository on: " + svnRepo.getAbsolutePath());
        try {
            svnRepository = SVNRepositoryFactory.create(repoSvnURL);
            if (svnRepo.exists()) {
                return;
            }
            SVNRepositoryFactory.createLocalRepository(svnRepo, true, false);

            clientManager.getCommitClient()
                    .doMkDir(
                            new SVNURL[] {
                                    repoSvnURL.appendPath(documentRoot.split("/")[1], false),
                                    repoSvnURL.appendPath(documentRoot, false) },
                            "created initial folders");

            if (new File(teiMaterialRoot).exists()) {
                for (File file : new File(teiMaterialRoot).listFiles()) {
                    if (file.getName().endsWith(".svn")) {
                        continue;
                    }
                    importFile(documentRoot + "/" + file.getName(), file);
                }
            }
        } catch (SVNException s) {
            throw new SubversionException(s.getMessage(), s);
        }
    }

    @SuppressWarnings("deprecation")
    public void update(File file) {
        try {
            clientManager.getUpdateClient().doUpdate(file, SVNRevision.create(getLatestRevision()),
                    true);
        } catch (SVNException s) {
            throw new SubversionException(s.getMessage(), s);
        }
    }

    @Override
    public List<FileItem> getFileItems(String path, int revision) {
        try {
            List<SVNDirEntry> entries = new ArrayList<SVNDirEntry>();
            svnRepository.getDir(path, revision, false, entries);
            List<FileItem> fileItems = new ArrayList<FileItem>();
            for (SVNDirEntry entry : entries) {
                if (entry.getKind().equals(SVNNodeKind.DIR)) {
                    fileItems.add(new FileItem(entry.getName(), path + "/" + entry.getRelativePath(), true, new ArrayList<FileItem>()));
                } else {
                    fileItems.add(new FileItem(entry.getName(), path + "/" + entry.getRelativePath(), false, null));
                }
            }
           return fileItems;
        } catch (SVNException s) {
            throw new SubversionException(s.getMessage(), s);
        }
    }
}
