package com.quascenta.edocs.service;

import com.quascenta.edocs.entities.FolderObject;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Software_Development on 12/7/2017.
 */
@Service
public interface FolderObjectService {

    int add(FolderObject folderObject);

    public FolderObject getFolderObjectById(int folderID);

    int delete(int i, String comments, String changeControlNumber);

    List<FolderObject> getChildFoldersByFolderId(int folderId,String sortBy);
}
