package org.example.studentjournal.services;

import org.example.studentjournal.DbManager;
import org.example.studentjournal.POJO.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupService {

    @Autowired
    private DbManager dbManager;

    public List<Group> getAllGroups() {
        try {
            return dbManager.getGroups();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching groups", e);
        }
    }

    public Optional<Group> getGroupById(int id) {
        return getAllGroups().stream().filter(g -> g.getId() == id).findFirst();
    }

    public void saveGroup(Group group) {
        try {
            if (group.getId() == 0) {
                dbManager.insertGroup(group.getName(), group.getCurriculum(), group.getTeacher(), group.getSubjects());
            } else {
                dbManager.updateGroup(group.getId(), group.getName(), group.getCurriculum(), group.getTeacher(), group.getSubjects());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error saving group", e);
        }
    }

    public void deleteGroup(int id) {
        try {
            dbManager.deleteGroup(id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting group", e);
        }
    }
}
