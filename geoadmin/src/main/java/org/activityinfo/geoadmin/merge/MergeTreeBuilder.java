package org.activityinfo.geoadmin.merge;

import java.util.List;

import org.activityinfo.geoadmin.ImportFeature;
import org.activityinfo.geoadmin.ImportSource;
import org.activityinfo.geoadmin.Join;
import org.activityinfo.geoadmin.Joiner;
import org.activityinfo.geoadmin.model.ActivityInfoClient;
import org.activityinfo.geoadmin.model.AdminEntity;
import org.activityinfo.geoadmin.model.AdminLevel;

import com.google.common.collect.Lists;

public class MergeTreeBuilder {

    private ActivityInfoClient client;
    private List<AdminLevel> parentLevels = Lists.newArrayList();
    private AdminLevel level;
    private ImportSource source;

    public MergeTreeBuilder(ActivityInfoClient client, AdminLevel level,
        ImportSource source) {
        this.client = client;
        this.level = level;
        this.source = source;
    }

    public MergeNode build() {

        findParentLevels();

        MergeNode root = new MergeNode();
        if (parentLevels.isEmpty()) {
            addLeafNodes(root, null, source.getFeatures());
        } else {
            addChildNodes(root, parentLevels.get(0), source.getFeatures());
        }

        return root;

    }

    private void addChildNodes(MergeNode parentNode, AdminLevel adminLevel,
        List<ImportFeature> features) {

        AdminLevel childLevel = nextParent(adminLevel);

        List<AdminEntity> parents = client.getAdminEntities(adminLevel);
        Joiner joiner = new Joiner(parents, features);

        List<AdminEntity> featureParents = joiner.joinParents();

        for (AdminEntity parent : parents) {
            MergeNode node = new MergeNode();
            node.setLevel(level);
            node.setEntity(parent);
            node.setParent(parentNode);
            parentNode.add(node);

            List<ImportFeature> children = Lists.newArrayList();
            for (int i = 0; i != features.size(); ++i) {
                if (featureParents.get(i) == parent) {
                    children.add(features.get(i));
                }
            }

            if (childLevel != null) {
                addChildNodes(node, childLevel, children);
            } else {
                addLeafNodes(node, parent, children);
            }
        }
    }

    private void addLeafNodes(MergeNode parentNode, AdminEntity parent,
        List<ImportFeature> features) {

        List<AdminEntity> entities = getChildEntities(level, parent);

        Joiner joiner = new Joiner(entities, features);

        List<Join> joins = joiner.joinOneToOne();
        for (Join join : joins) {
            MergeNode node = new MergeNode();
            node.setEntity(join.getEntity());
            node.setFeature(join.getFeature());
            node.setLevel(level);
            node.setParent(parentNode);

            if (join.getEntity() != null && join.getFeature() != null) {
                node.setAction(MergeAction.UPDATE);
            } else {
                node.setAction(MergeAction.IGNORE);
            }

            parentNode.add(node);
        }
    }

    private List<AdminEntity> getChildEntities(AdminLevel level,
        AdminEntity parent) {
        List<AdminEntity> entities = client.getAdminEntities(level);
        List<AdminEntity> children = Lists.newArrayList();
        for (AdminEntity child : entities) {
            if (parent == null || child.getParentId() == parent.getId()) {
                children.add(child);
            }
        }
        return children;
    }

    private AdminLevel nextParent(AdminLevel parentLevel) {
        int index = parentLevels.indexOf(parentLevel);
        if (index + 1 < parentLevels.size()) {
            return parentLevels.get(index + 1);
        } else {
            return null;
        }
    }

    private void findParentLevels() {
        Integer parentId = level.getParentId();
        while (parentId != null) {
            AdminLevel parentLevel = client.getAdminLevel(parentId);
            parentLevels.add(0, parentLevel);
            parentId = parentLevel.getParentId();
        }
    }

}
