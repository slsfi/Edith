/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNFileRevision;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;

import fi.finlit.edith.EDITH;

/**
 * SubversionServiceImpl is the default implementation of the SubversionService
 * interface
 *
 * @author tiwe
 * @version $Id$
 */
public class SubversionServiceImpl implements SubversionService {

    private static final Logger logger = LoggerFactory
            .getLogger(SubversionServiceImpl.class);

    static {
        FSRepositoryFactory.setup();
    }

    private final SVNClientManager clientManager;

    private SVNRepository svnRepository;

    private final File svnRepo;

    private final SVNURL repoSvnURL;

    private final String documentRoot;

    private final String materialTeiRoot;

    private final File svnCache;

    private final File readCache;

    private final File workingCopies;

    private final AuthService authService;

    public SubversionServiceImpl(
            @Inject @Symbol(EDITH.SVN_CACHE_DIR) File svnCache,
            @Inject @Symbol(EDITH.REPO_FILE_PROPERTY) File svnRepo,
            @Inject @Symbol(EDITH.REPO_URL_PROPERTY) String repoURL,
            @Inject @Symbol(EDITH.SVN_DOCUMENT_ROOT) String documentRoot,
            @Inject @Symbol(EDITH.MATERIAL_TEI_ROOT) String materialTeiRoot,
            AuthService authService) {
        this.clientManager = SVNClientManager.newInstance();
        this.svnCache = svnCache;
        this.readCache = new File(svnCache + "/readCache");
        this.workingCopies = new File(svnCache + "/workingCopies");
        this.svnRepo = svnRepo;
        this.documentRoot = documentRoot;
        this.materialTeiRoot = materialTeiRoot;
        this.authService = authService;
        try {
            this.repoSvnURL = SVNURL.parseURIEncoded(repoURL);
        } catch (SVNException e) {
            throw new RuntimeException(e);
        }
        this.svnRepository = null;
    }

    public void initialize() {
        logger.info("Initializing SVN repository on: "
                + svnRepo.getAbsolutePath());
        try {
            svnRepository = SVNRepositoryFactory.create(repoSvnURL);
            if (svnRepo.exists()) {
                return;
            }
            SVNRepositoryFactory.createLocalRepository(svnRepo, true, false);

            clientManager.getCommitClient().doMkDir(
                    new SVNURL[] { repoSvnURL.appendPath(documentRoot.split("/")[1], false),
                            repoSvnURL.appendPath(documentRoot, false) },
                    "created initial folders");

            if (new File(materialTeiRoot).exists()) {
                for (File file : new File(materialTeiRoot).listFiles()) {
                    if (file.isFile()) {
                        importFile(documentRoot + "/" + file.getName(), file);
                    }
                }
            }
        } catch (SVNException s) {
            throw new RuntimeException(s.getMessage(), s);
        }
    }

    public void destroy() {
        try {
            svnRepository.closeSession();
            svnRepository = null;
            FileUtils.deleteDirectory(svnCache);
            FileUtils.deleteDirectory(svnRepo);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    @SuppressWarnings("deprecation")
    @Override
    public long importFile(String svnPath, File file) {
        try {
            return clientManager.getCommitClient().doImport(file,
                    repoSvnURL.appendPath(svnPath, false), svnPath + " added",
                    false).getNewRevision();
        } catch (SVNException s) {
            throw new RuntimeException(s.getMessage(), s);
        }

    }

    @SuppressWarnings("deprecation")
    public long commit(File file) {
        try {
            return clientManager.getCommitClient().doCommit(
                    new File[] { file }, true, file.getName() + " committed", false,
                    true).getNewRevision();
        } catch (SVNException s) {
            throw new RuntimeException(s.getMessage(), s);
        }
    }

    @Override
    public Collection<String> getEntries(String svnFolder, long revision) {
        try {
            List<SVNDirEntry> entries = new ArrayList<SVNDirEntry>();
            svnRepository.getDir(svnFolder, revision, false, entries);
            List<String> rv = new ArrayList<String>(entries.size());
            for (SVNDirEntry entry : entries) {
                rv.add(entry.getName());
            }
            return rv;
        } catch (SVNException s) {
            throw new RuntimeException(s.getMessage(), s);
        }
    }

    @Override
    public InputStream getStream(String svnPath, long revision) throws IOException {
        try {
            if (revision == -1) {
                revision = getLatestRevision(svnPath);
            }
            File documentFolder = new File(readCache, URLEncoder.encode(svnPath,
                    "UTF-8"));
            File documentFile = new File(documentFolder, String
                    .valueOf(revision));
            if (!documentFile.exists()) {
                documentFolder.mkdirs();
                OutputStream out = new FileOutputStream(documentFile);
                try {
                    svnRepository.getFile(svnPath, revision, null, out);
                } finally {
                    // SVNRepository.getFile doesn't close OutputStream,
                    // so we need to close it manually
                    out.close();
                }
            }
            return new FileInputStream(documentFile);
        } catch (SVNException s) {
            throw new RuntimeException(s.getMessage(), s);
        }

    }

    private List<SVNFileRevision> getFileRevisions(String svnPath)
            throws SVNException {
        long latest = svnRepository.getLatestRevision();
        List<SVNFileRevision> revisions = new ArrayList<SVNFileRevision>();
        svnRepository.getFileRevisions(svnPath, revisions, 0, latest);
        return revisions;
    }

    private long getLatestRevision(String svnPath) throws SVNException {
        List<SVNFileRevision> revisions = getFileRevisions(svnPath);
        long revision = 0;
        for (SVNFileRevision rev : revisions) {
            if (revision < rev.getRevision()) {
                revision = rev.getRevision();
            }
        }
        return revision;
    }

    @Override
    public List<Long> getRevisions(String svnPath) {
        try {
            List<SVNFileRevision> revisions = getFileRevisions(svnPath);
            List<Long> revisionIds = new ArrayList<Long>(revisions.size());
            for (SVNFileRevision rev : revisions) {
                revisionIds.add(rev.getRevision());
            }
            return revisionIds;
        } catch (SVNException s) {
            throw new RuntimeException(s.getMessage(), s);
        }
    }

    @Override
    public void delete(String svnPath) {
        try {
            SVNURL targetURL = repoSvnURL.appendPath(svnPath, false);
            System.out.println(clientManager.getCommitClient().doDelete(
                    new SVNURL[] { targetURL }, "removed " + svnPath));
        } catch (SVNException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("deprecation")
    public void update(File file) {
        try {
            clientManager.getUpdateClient().doUpdate(file,
                    SVNRevision.create(getLatestRevision()), true);
        } catch (SVNException s) {
            throw new RuntimeException(s.getMessage(), s);
        }
    }

    @Override
    public long getLatestRevision() {
        try {
            return svnRepository.getLatestRevision();
        } catch (SVNException s) {
            throw new RuntimeException(s.getMessage(), s);
        }
    }

    @SuppressWarnings("deprecation")
    public void checkout(File destination, long revision) {
        try {
            clientManager.getUpdateClient().doCheckout(repoSvnURL.appendPath(documentRoot, false), destination,
                    SVNRevision.create(revision),
                    SVNRevision.create(revision), false);
        } catch (SVNException s) {
            throw new RuntimeException(s.getMessage(), s);
        }
    }

    @Override
    public long commit(String svnPath, long revision, UpdateCallback callback) {
        File userCheckout = new File(workingCopies + "/" + authService.getUsername()); // TODO will get overwritten(?) & caching?
        svnPath = svnPath.replaceAll(documentRoot, "");
        checkout(userCheckout, revision);
        File file = new File(userCheckout + "/" + svnPath);
        File tmp = null;
        try {
            tmp = File.createTempFile(UUID.randomUUID().toString(), null);
            callback.update(new FileInputStream(file), new FileOutputStream(tmp));
            FileUtils.copyFile(tmp, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (tmp != null) {
                tmp.delete();
            }
        }
        return commit(file);
    }

}
