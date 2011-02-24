package fi.finlit.edith;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.multimap.MultiHashMap;

import com.mysema.commons.lang.IteratorAdapter;
import com.mysema.rdfbean.model.ID;
import com.mysema.rdfbean.model.RDFConnection;
import com.mysema.rdfbean.model.STMT;
import com.mysema.rdfbean.model.UID;
import com.mysema.rdfbean.sesame.NativeRepository;

public class FixUsers {

    public static void main(String[] args){
        NativeRepository repository = new NativeRepository();
        repository.setIndexes("spoc,posc,opsc");
        repository.setDataDirName("/opt/rdfbean/edith");
        repository.initialize();

        try{
           RDFConnection connection = repository.openConnection();
            try{
                fixUsers(connection);
            }finally{
                connection.close();
            }

        }finally{
            repository.close();
        }

    }

    private static void fixUsers(RDFConnection connection){
        Pattern pattern = Pattern.compile("[\\wä]+");
        UID username = new UID(EDITH.NS, "username");
        UID lastName = new UID(EDITH.NS, "lastName");

        // get all users and create replacements
        Map<String, ID> validUsers = new HashMap<String, ID>();
        MultiMap<String, ID> invalidUsers = new MultiHashMap<String, ID>();
        List<STMT> lastNames = IteratorAdapter.asList(connection.findStatements(null, lastName, null, null, false));
        for (STMT stmt : lastNames){
            STMT usernameStmt = IteratorAdapter.asList(connection.findStatements(stmt.getSubject(), username, null, null, false)).get(0);
            if (pattern.matcher(stmt.getObject().getValue()).matches()){
                validUsers.put(usernameStmt.getObject().getValue(), usernameStmt.getSubject());
            }else{
                invalidUsers.put(usernameStmt.getObject().getValue(), usernameStmt.getSubject());
            }
        }

        // perform replacements
        for (String name : invalidUsers.keySet()){
            Set<STMT> added = new HashSet<STMT>();
            ID validId = validUsers.get(name);
            System.err.println(validId + " for " + invalidUsers.get(name));
            for (ID invalidId : invalidUsers.get(name)){
                List<STMT> objStmts = IteratorAdapter.asList(connection.findStatements(null, null, invalidId, null, false));
                for (STMT stmt : objStmts){
                    added.add(new STMT(stmt.getSubject(), stmt.getPredicate(), validId, stmt.getContext()));
                }

                connection.remove(invalidId, null, null, null);
                connection.remove(null, null, invalidId, null);
            }

            added.removeAll(IteratorAdapter.asList(connection.findStatements(validId, null, null, null, false)));
            added.removeAll(IteratorAdapter.asList(connection.findStatements(null, null, validId, null, false)));

            connection.update(null, added);
            System.err.println("added " + added.size());
        }

    }

}
