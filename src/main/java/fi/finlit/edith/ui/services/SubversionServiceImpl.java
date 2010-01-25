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

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNFileRevision;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCommitClient;

import fi.finlit.edith.EDITH;

/**
 * SubversionServiceImpl provides
 *
 * @author tiwe
 * @version $Id$
 */
public class SubversionServiceImpl implements SubversionService{

    static{
        FSRepositoryFactory.setup();
    }
    
    @Inject
    private SVNClientManager clientManager;
        
    @Inject 
    @Symbol(EDITH.SVN_CACHE_DIR)
    private File svnCache;
    
    @Inject
    private SVNRepository svnRepository;
    
    @SuppressWarnings("deprecation")
    @Override
    public void add(String svnPath, File file){
        try{
            SVNURL repoURL = svnRepository.getRepositoryRoot(false);
            SVNCommitClient commitClient = clientManager.getCommitClient();
            commitClient.doImport(file, repoURL.appendPath(svnPath, false), svnPath + " added", false);    
        }catch(SVNException s){
            throw new RuntimeException(s.getMessage(), s);
        }
                
    }
    
    @Override
    public long commit(String svnPath, File file) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Collection<String> getEntries(String svnFolder, long revision){
        try{
            List<SVNDirEntry> entries = new ArrayList<SVNDirEntry>();
            svnRepository.getDir(svnFolder, revision, false, entries);
            List<String> rv = new ArrayList<String>(entries.size());
            for (SVNDirEntry entry : entries){
                rv.add(entry.getName());
            }
            return rv;   
        }catch(SVNException s){
            throw new RuntimeException(s.getMessage(), s);
        }                
    }
    
    @Override
    public File getFile(String svnPath, long revision) throws IOException{
        try{
            if (revision == -1){
                revision = getLatestRevision(svnPath);
            }
            File documentFolder = new File(svnCache, URLEncoder.encode(svnPath,"UTF-8")); 
            File documentFile = new File(documentFolder, String.valueOf(revision));
            if (!documentFile.exists()){
                documentFolder.mkdirs();
                svnRepository.getFile(svnPath, revision, null, new FileOutputStream(documentFile));
            }                
            return documentFile;    
        }catch(SVNException s){
            throw new RuntimeException(s.getMessage(), s);
        }
        
    }
    
    private List<SVNFileRevision> getFileRevisions(String svnPath) throws SVNException{
        long latest = svnRepository.getLatestRevision();
        List<SVNFileRevision> revisions = new ArrayList<SVNFileRevision>(); 
        svnRepository.getFileRevisions(svnPath, revisions, 0, latest);
        return revisions;
    }
    
    private long getLatestRevision(String svnPath) throws SVNException{        
        List<SVNFileRevision> revisions = getFileRevisions(svnPath); 
        long revision = 0;
        for (SVNFileRevision rev : revisions){
            if (revision < rev.getRevision()){
                revision = rev.getRevision();
            }
        }
        return revision;
    }

    @Override
    public List<Long> getRevisions(String svnPath){
        try{
            List<SVNFileRevision> revisions = getFileRevisions(svnPath);
            List<Long> revisionIds = new ArrayList<Long>(revisions.size());
            for (SVNFileRevision rev : revisions){
                revisionIds.add(rev.getRevision());
            }
            return revisionIds;    
        }catch(SVNException s){
            throw new RuntimeException(s.getMessage(), s);
        }        
    }

    @Override
    public void remove(String svnPath){
        try {
            SVNURL repoURL = svnRepository.getRepositoryRoot(false);
            SVNURL targetURL = repoURL.appendPath(svnPath, false);
            clientManager.getCommitClient().doDelete(new SVNURL[]{targetURL}, "removed " + svnPath);
        } catch (SVNException e) {
            throw new RuntimeException(e.getMessage(), e);
        }        
    }

    @Override
    public void update(String svnPath, File file) {
        throw new UnsupportedOperationException();
        
    }
    
}
