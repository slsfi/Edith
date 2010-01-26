/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNFileRevision;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCommitClient;

import fi.finlit.edith.EDITH;

/**
 * SubversionServiceImpl provides
 *
 * @author tiwe
 * @version $Id$
 */
public class SubversionServiceImpl implements SubversionService {

    static {
        FSRepositoryFactory.setup();
    }

    private SVNClientManager clientManager;

    private File svnCache;

    private SVNRepository svnRepository;

    private File svnRepo;

    private SVNCommitClient commitClient;

    private SVNURL repoSvnURL;

    public SubversionServiceImpl(
            @Inject @Symbol(EDITH.SVN_CACHE_DIR) File svnCache,
            @Inject @Symbol(EDITH.REPO_FILE_PROPERTY) File svnRepo,
            @Inject @Symbol(EDITH.REPO_URL_PROPERTY) String repoURL) {
        this.clientManager = SVNClientManager.newInstance();
        this.commitClient = clientManager.getCommitClient();
        this.svnCache = svnCache;
        this.svnRepo = svnRepo;
        this.repoSvnURL = null;
        try {
            repoSvnURL = SVNURL.parseURIEncoded(repoURL);
        } catch (SVNException e) {
            throw new RuntimeException(e);
        }
        this.svnRepository = null;
    }

    public void initialize() {
        System.err.println("Initializing SVN repository on: "
                + svnRepo.getAbsolutePath());
        try {
            svnRepository = SVNRepositoryFactory.create(repoSvnURL);
            if (svnRepo.exists()) {
                return;
            }
            SVNRepositoryFactory.createLocalRepository(svnRepo, true, false);

            commitClient.doMkDir(new SVNURL[] {
                    repoSvnURL.appendPath("documents", false),
                    repoSvnURL.appendPath("documents/trunk", false) },
                    "created initial folders");

            if (new File("etc/demo-material/tei").exists()) {
                for (File file : new File("etc/demo-material/tei").listFiles()) {
                    if (file.isFile()) {
                        importFile("documents/trunk/" + file.getName(), file);
                    }
                }
            }
        } catch (SVNException s) {
            throw new RuntimeException(s.getMessage(), s);
        }
    }

    public void destroy() {
        try {
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
            // TODO inject via symbol
            commitClient = clientManager.getCommitClient();
            return commitClient.doImport(file,
                    repoSvnURL.appendPath(svnPath, false), svnPath + " added",
                    false).getNewRevision();
        } catch (SVNException s) {
            throw new RuntimeException(s.getMessage(), s);
        }

    }

    @Override
    public long commit(String svnPath, File file) {
        throw new UnsupportedOperationException();
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
    public File getFile(String svnPath, long revision) throws IOException {
        try {
            if (revision == -1) {
                revision = getLatestRevision(svnPath);
            }
            File documentFolder = new File(svnCache, URLEncoder.encode(svnPath,
                    "UTF-8"));
            File documentFile = new File(documentFolder, String
                    .valueOf(revision));
            if (!documentFile.exists()) {
                documentFolder.mkdirs();
                svnRepository.getFile(svnPath, revision, null,
                        new FileOutputStream(documentFile));
            }
            return documentFile;
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
            System.out.println(commitClient.doDelete(new SVNURL[] { targetURL }, "removed "
                    + svnPath));
        } catch (SVNException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void update(String svnPath, File file) {
        throw new UnsupportedOperationException();

    }

    @Override
    public long getLatestRevision() {
        try {
            return svnRepository.getLatestRevision();
        } catch (SVNException s) {
            throw new RuntimeException(s.getMessage(), s);
        }
    }

}
