package org.activityinfo.server.forms;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.core.shared.form.FormClassSerializer;
import org.activityinfo.core.shared.form.FormField;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;
import org.activityinfo.legacy.shared.command.Month;
import org.activityinfo.legacy.shared.model.AttributeDTO;
import org.activityinfo.legacy.shared.model.IndicatorDTO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.activityinfo.legacy.shared.adapter.CuidAdapter.*;


public class SiteFormAdapter extends FormClassAdapter implements FormClassProvider, Serializable {

    private FormClass siteForm;
    private FormClass monthlyForm;
    private Map<Integer, Integer> attributeGroupMap;
    private Set<Cuid> fieldIds = Sets.newHashSet();

    private static final Logger LOGGER = Logger.getLogger(SiteFormAdapter.class.getName());

    public SiteFormAdapter(FormClass siteForm, FormClass monthlyForm, Map<Integer, Integer> attributeGroupMap) {
        Preconditions.checkNotNull(attributeGroupMap);

        this.siteForm = siteForm;
        this.monthlyForm = monthlyForm;
        this.attributeGroupMap = attributeGroupMap;

        for(FormField field : siteForm.getFields()) {
            fieldIds.add(field.getId());
        }
    }

    public Cuid getStartDateFieldId() {
        return CuidAdapter.field(siteForm.getId(), CuidAdapter.START_DATE_FIELD);
    }

    public Cuid getEndDateFieldId() {
        return CuidAdapter.field(siteForm.getId(), CuidAdapter.END_DATE_FIELD);
    }

    public Cuid getCommentFieldId() {
        return CuidAdapter.field(siteForm.getId(), CuidAdapter.COMMENT_FIELD);
    }

    private Cuid getAttributeGroupField(int groupId) {
        return attributeGroupField(getLegacyIdFromCuid(siteForm.getId()), groupId);
    }

    public FormClass getSiteForm() {
        return siteForm;
    }

    public FormClass getMonthlyForm() {
        return monthlyForm;
    }

    public void applySiteChanges(FormInstance site, Map<String, Object> propertyMap) {

        for(Map.Entry<String, Object> entry : propertyMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (key.equals("date1")) {
                site.set(getStartDateFieldId(), value);

            } else if(key.equals("date2")) {
                site.set(getStartDateFieldId(), value);

            } else if (key.equals("comments")) {
                if(value == null) {
                    site.remove(getCommentFieldId());
                } else {
                    site.set(getCommentFieldId(), value);
                }

            } else if (key.equals("locationId")) {
                site.set(field(siteForm.getId(), LOCATION_FIELD), cuid(LOCATION_DOMAIN, ((Integer) value)));

            } else if (key.equals("projectId")) {
                site.set(field(siteForm.getId(), PROJECT_FIELD), cuid(PROJECT_DOMAIN, ((Integer) value)));

            } else if(key.equals("partnerId")) {
                site.set(field(siteForm.getId(), PARTNER_FIELD), cuid(PARTNER_DOMAIN, ((Integer) value)));

            } else if(key.startsWith(IndicatorDTO.PROPERTY_PREFIX)) {
                applyIndicatorChange(site, key, value);

            } else if(key.startsWith(AttributeDTO.PROPERTY_PREFIX)) {
                int attributeId = AttributeDTO.idForPropertyName(key);
                if(attributeGroupMap.containsKey(attributeId)) {
                    int groupId = attributeGroupMap.get(attributeId);
                    Cuid fieldId = getAttributeGroupField(groupId);
                    Set<Cuid> references = Sets.newHashSet(site.getReferences(fieldId));
                    if(value == Boolean.TRUE) {
                        references.add(attributeId(attributeId));
                    } else {
                        references.remove(attributeId(attributeId));
                    }
                    site.set(fieldId, references);
                }

            } else {
                LOGGER.log(Level.WARNING, "Unrecognized property " + key);
            }
        }
    }

    public void applyMonthlyReportChanges(FormInstance monthlySnapshot, Month snapshotMonth, Map<String, Object> changeMap) {

        for(String propertyName : changeMap.keySet()) {
            if(propertyName.startsWith(IndicatorDTO.PROPERTY_PREFIX)) {
                Month month = IndicatorDTO.monthForPropertyName(propertyName);
                int indicatorId = IndicatorDTO.indicatorIdForPropertyName(propertyName);
                if(snapshotMonth.equals(month)) {
                    Double value = (Double) changeMap.get(propertyName);
                    if(value == null) {
                        monthlySnapshot.remove(indicatorField(indicatorId));
                    } else {
                        monthlySnapshot.set(indicatorField(indicatorId), value);
                    }
                }
            }
        }
    }


    private void applyIndicatorChange(FormInstance instance, String key, Object value) {
        int indicatorId = IndicatorDTO.indicatorIdForPropertyName(key);
        Cuid indicatorField = indicatorField(indicatorId);
        if(value != null) {
            instance.set(indicatorField, value);
        } else {
            instance.remove(indicatorField);
        }
    }

    @Override
    public FormClass getFormClass(Cuid id) {
        if(id.equals(siteForm.getId())) {
            return siteForm;
        } else if(id.equals(monthlyForm.getId())) {
            return monthlyForm;
        } else {
            throw new UnsupportedOperationException("id: " + id);
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        FormClassSerializer serializer = new FormClassSerializer();
        out.writeUTF(serializer.toJson(siteForm).toString());
        if(monthlyForm == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            out.writeUTF(serializer.toJson(monthlyForm).toString());
        }
        out.writeObject(attributeGroupMap);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        FormClassSerializer serializer = new FormClassSerializer();
        JsonParser parser = new JsonParser();
        siteForm = serializer.fromJson((JsonObject) parser.parse(in.readUTF()));
        boolean isMonthly = in.readBoolean();
        if(isMonthly) {
            monthlyForm = serializer.fromJson((JsonObject) parser.parse(in.readUTF()));
        }
        attributeGroupMap = (Map<Integer, Integer>) in.readObject();
    }
}
