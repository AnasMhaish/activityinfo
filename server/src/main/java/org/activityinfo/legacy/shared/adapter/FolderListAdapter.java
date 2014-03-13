package org.activityinfo.legacy.shared.adapter;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.activityinfo.core.shared.application.FolderClass;
import org.activityinfo.core.shared.criteria.Criteria;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.legacy.shared.model.*;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.activityinfo.legacy.shared.adapter.CuidAdapter.*;

/**
 * Extracts a list of databases as a list of folders
 */
public class FolderListAdapter implements Function<SchemaDTO, List<FormInstance>> {
    private final Criteria criteria;

    public FolderListAdapter(Criteria criteria) {
        this.criteria = criteria;
    }

    @Nullable
    @Override
    public List<FormInstance> apply(SchemaDTO schemaDTO) {
        List<FormInstance> instances = Lists.newArrayList();
        for(UserDatabaseDTO db : schemaDTO.getDatabases()) {
            FormInstance dbFolder = newFolder(db);
            if(criteria.apply(dbFolder)) {
                instances.add(dbFolder);
            }

            Set<String> categories = new HashSet<>();

            for(ActivityDTO activity : db.getActivities()) {

                FormInstance activityClass = new FormInstance(activityFormClass(activity.getId()),
                        FormClass.CLASS_ID);

                if(!Strings.isNullOrEmpty(activity.getCategory())) {
                    categories.add(activity.getCategory());
                    activityClass.setParentId(activityCategoryFolderId(db, activity.getCategory()));
                } else {
                    activityClass.setParentId(databaseId(db));
                }

                activityClass.set(FormClass.LABEL_FIELD_ID, activity.getName());

                if(criteria.apply(activityClass)) {
                    instances.add(activityClass);
                }
            }

            for(String category : categories) {
                FormInstance categoryFolder = new FormInstance(
                        activityCategoryFolderId(db, category), FolderClass.CLASS_ID);
                categoryFolder.setParentId(dbFolder.getId());
                categoryFolder.set(FolderClass.LABEL_FIELD_ID, category);

                if(criteria.apply(categoryFolder)) {
                    instances.add(categoryFolder);
                }
            }
        }

        // Add LocationTypes which have been assigned to a database
        for(CountryDTO country : schemaDTO.getCountries()) {
            for(LocationTypeDTO locationType : country.getLocationTypes()) {
                if(!locationType.isAdminLevel() && locationType.getDatabaseId() != null) {
                    FormInstance instance = new FormInstance(CuidAdapter.locationFormClass(locationType.getId()),
                            FormClass.CLASS_ID);
                    instance.set(FormClass.LABEL_FIELD_ID, locationType.getName());
                    instance.setParentId(CuidAdapter.cuid(DATABASE_DOMAIN, locationType.getDatabaseId()));

                    if(criteria.apply(instance)) {
                        instances.add(instance);
                    }
                }
            }
        }

        return instances;
    }

    private FormInstance newFolder(UserDatabaseDTO db) {
        FormInstance folder = new FormInstance(cuid(DATABASE_DOMAIN, db.getId()),
                FolderClass.CLASS_ID);
        folder.set(FolderClass.LABEL_FIELD_ID, db.getName());
        folder.set(FolderClass.DESCRIPTION_FIELD_ID, db.getFullName());
        return folder;
    }

}
