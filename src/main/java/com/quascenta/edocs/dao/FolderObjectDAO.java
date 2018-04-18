package com.quascenta.edocs.dao;

import com.quascenta.edocs.entities.FolderObject;
import com.quascenta.edocs.entities.Module;

import java.util.List;

/**
 * Created by Software_Development on 12/7/2017.
 */
public interface FolderObjectDAO {



    void insertBatch(int docId, List<Module> modules);

    public FolderObject getFolderObjectById(int folderID);

    int addFolderObject(FolderObject folderObject);

    int deleteFolder(int folderId, String comments, String changeControlNumber);


    List<FolderObject> getChildFoldersByFolderId(int folderId, String sortBt);
}
