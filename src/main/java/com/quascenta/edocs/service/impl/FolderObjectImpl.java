package com.quascenta.edocs.service.impl;

import com.quascenta.edocs.dao.FolderObjectDAO;
import com.quascenta.edocs.entities.FolderObject;
import com.quascenta.edocs.service.FolderObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Software_Development on 12/7/2017.
 */
@Component
@Service
public class FolderObjectImpl implements FolderObjectService{

    @Autowired
    FolderObjectDAO folderObjectDAO;

    @Override
    public int add(FolderObject folderObject) { return folderObjectDAO.addFolderObject(folderObject);

    }

    @Override
    public FolderObject getFolderObjectById(int folderID) {
        return  folderObjectDAO.getFolderObjectById(folderID);
    }

    @Override
    public int delete(int folderId, String comments, String changeControlNumber) {
        return folderObjectDAO.deleteFolder(folderId, comments, changeControlNumber);
    }

    @Override
    public List<FolderObject> getChildFoldersByFolderId(int folderId, String sortBy) {
        return folderObjectDAO.getChildFoldersByFolderId(folderId,sortBy);
    }

}
