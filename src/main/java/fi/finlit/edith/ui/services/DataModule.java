/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.services;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.springframework.security.providers.dao.SaltSource;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCommitClient;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.domain.Profile;
import fi.finlit.edith.domain.User;

/**
 * DataModule provides
 *
 * @author tiwe
 * @version $Id$
 */
public class DataModule {
    
    public static void contributeSeedEntity(
            OrderedConfiguration<Object> configuration,
            @Inject @Symbol(EDITH.REPO_FILE_PROPERTY) String svnRepoPath,
            SaltSource saltSource,            
            PasswordEncoder passwordEncoder) throws Exception {

        System.err.println("Initializing DataModule");
        
        // svn
        FSRepositoryFactory.setup();
        File svnRepo = new File(svnRepoPath);
        if (!svnRepo.exists() || svnRepo.list().length == 0){
            svnRepo.mkdirs();
            initializeSVN(svnRepo);    
        }
        
        // users
        addUsers(configuration, saltSource, passwordEncoder);     
        
    }
    
    @SuppressWarnings("deprecation")
    private static void initializeSVN(File svnRepo) throws SVNException {
        System.err.println("Initializing SVN repository on: " + svnRepo.getAbsolutePath());
        SVNURL repoURL = SVNRepositoryFactory.createLocalRepository(svnRepo, true , false );          
        SVNClientManager clientManager = SVNClientManager.newInstance();
        SVNCommitClient commitClient = clientManager.getCommitClient();
        
        commitClient.doMkDir(new SVNURL[]{
                repoURL.appendPath("documents", false),
                repoURL.appendPath("documents/trunk", false)
        }, "created initial folders");
        
        SVNURL folder = repoURL.appendPath("documents/trunk", false);
        
        // TODO : make this location configurable
        if (new File("etc/demo-material/tei").exists()){
            for (File file : new File("etc/demo-material/tei").listFiles()){
                if (file.isFile()){
                    commitClient.doImport(file, folder.appendPath(file.getName(), false), file.getName() + " added", false);    
                }            
            }    
        }         
    }

    @SuppressWarnings("unchecked")
    private static void addUsers(OrderedConfiguration<Object> configuration,
            SaltSource saltSource, PasswordEncoder passwordEncoder) throws IOException {
        List<String> lines = IOUtils.readLines(DataModule.class.getResourceAsStream("/users.csv"));
        for (String line : lines){
            String[] values = line.split(";");                       
            User user = new User();
            user.setUsername(values[0].toLowerCase());
            user.setEmail(values[2]);
            user.setFirstName(values[0]);
            user.setLastName(values[1]);
            if (values[2].endsWith("mysema.com")){
                user.setProfile(Profile.Admin);
            }else{
                user.setProfile(Profile.User);
            }            
            
            // encode password
            UserDetailsImpl userDetails = new UserDetailsImpl(
                    user.getUsername(), user.getPassword(), 
                    user.getProfile().getAuthorities());
            String password = passwordEncoder.encodePassword(user.getUsername(),saltSource.getSalt(userDetails));
            user.setPassword(password);
            
            configuration.add("user-" + user.getUsername(), user);
        }
    }  

}
