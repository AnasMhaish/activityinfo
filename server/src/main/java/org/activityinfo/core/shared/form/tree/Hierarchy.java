package org.activityinfo.core.shared.form.tree;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.core.shared.form.FormClass;

import java.util.*;

/**
 * Models a hierarchy of choices for the user
 */
public class Hierarchy {

    private Map<Cuid, Level> levelMap = Maps.newHashMap();
    private List<Level> roots = Lists.newArrayList();
    private List<Level> levels = Lists.newArrayList();

    public Hierarchy(FormTree.Node node) {

        // Find all of the form class here
        for(FormTree.Node child : node.getChildren()) {
            FormClass formClass = child.getDefiningFormClass();
            if(!levelMap.containsKey(formClass.getId())) {
                levelMap.put(formClass.getId(), new Level(formClass));
            }
        }

        // Assign parents...
        for(Level level : levelMap.values()) {
            if(!level.isRoot()) {
                level.parent = levelMap.get(level.parentId);
                level.parent.children.add(level);
            }
        }

        // find roots
        for(Level level : levelMap.values()) {
            if(level.isRoot()) {
                roots.add(level);
            }
        }

        // breadth first search to establish presentation order
        establishPresentationOrder(roots);
    }

    public List<Level> getRoots() {
        return roots;
    }


    /**
     * @return  the Level associated with the given {@code formClassId}
     */
    public Level getLevel(Cuid formClassId) {
        return levelMap.get(formClassId);
    }

    private void establishPresentationOrder(List<Level> parents) {
        for(Level child : parents) {
            this.levels.add(child);
            establishPresentationOrder(child.children);
        }
    }

    /**
     *
     * @return a list of levels in this tree, in topologically sorted order
     */
    public List<Level> getLevels() {
        return levels;
    }

    public boolean hasLevel(Cuid classId) {
        return levelMap.containsKey(classId);
    }
}
