package org.activityinfo.model.table;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.resource.*;
import org.activityinfo.model.system.ApplicationProperties;
import org.activityinfo.model.type.primitive.TextType;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Selects the given root field
 */
public class FieldPathSelector implements FieldSelector {


    public static class Step implements Predicate<FormTree.Node>, IsRecord {
        private ResourceId formClass;
        private ResourceId fieldId;


        /**
         * Step which matches a specific formClass and fieldId
         */
        public Step(ResourceId formClass, ResourceId fieldId) {
            assert formClass != null;
            assert fieldId != null;

            this.formClass = formClass;
            this.fieldId = fieldId;
        }

        /**
         * Step which matches only on fieldId
         */
        public Step(ResourceId fieldId) {
            assert fieldId != null;

            this.fieldId = fieldId;
        }


        @Override
        public boolean apply(FormTree.Node input) {
            if(formClass != null) {
                if(!Objects.equals(formClass, input.getDefiningFormClass().getId())) {
                    return false;
                }
            }
            return input.getFieldId().equals(fieldId) ||
                   input.getField().getSuperProperties().contains(fieldId);
        }

        @Override
        public Record asRecord() {
            RecordBuilder record = Records.builder();
            if(formClass != null) {
                record.set("formClass", formClass.asString());
            }
            record.set("fieldId", fieldId.asString());
            return record.build();
        }
    }

    private final LinkedList<Step> steps = Lists.newLinkedList();

    private FieldPathSelector() {

    }

    /**
     * Selects the specific field of the given FormClass, wherever it appears
     * in the heirarchy
     * @param classId
     * @param fieldId
     */
    public FieldPathSelector(ResourceId classId, ResourceId fieldId) {
        steps.add(new Step(classId, fieldId));
    }

    public FieldPathSelector(Iterable<ResourceId> fieldIds) {
        for(ResourceId fieldId : fieldIds) {
            steps.add(new Step(fieldId));
        }
    }

    @Override
    public List<FormTree.Node> select(FormTree tree) {
        List<FormTree.Node> matching = Lists.newArrayList();
        collect(steps, tree.getRootFields(), matching);
        return matching;
    }

    private void collect(List<Step> steps, List<FormTree.Node> fields, List<FormTree.Node> matching) {
        Step head = steps.get(0);
        List<Step> tail = steps.subList(1, steps.size());

        if(tail.isEmpty()) {
            // last node
            if(head.fieldId.equals(ApplicationProperties.LABEL_PROPERTY)) {
                findLabelField(head, fields, matching);
            } else {
                findMatchingField(head, fields, matching);
            }
        } else {
            for(FormTree.Node field : fields) {
                if(head.apply(field)) {
                    collect(tail, field.getChildren(), matching);
                }
            }
        }

    }

    private void findLabelField(Step head, List<FormTree.Node> fields, List<FormTree.Node> matching) {
        int matchCount = findMatchingField(head, fields, matching);
        if(matchCount == 0) {
            for(FormTree.Node field : fields) {
                if(field.getType() instanceof TextType) {
                    matching.add(field);
                    break;
                }
            }
        }
    }

    private int findMatchingField(Step head, List<FormTree.Node> fields, List<FormTree.Node> matching) {
        int count = 0;
        for(FormTree.Node field : fields) {
            if(head.apply(field)) {
                if(matching.add(field)) {
                    count ++;
                }
            }
        }
        return count;
    }

    @Override
    public Record asRecord() {
        return Records.builder().set("path", Records.toRecordList(steps)).build();
    }

    public static FieldPathSelector fromRecord(Record record) {
        FieldPathSelector selector = new FieldPathSelector();
        for(Record stepRecord : record.getRecordList("path")) {
            String formClassId = stepRecord.isString("formClass");
            String fieldId = stepRecord.getString("fieldId");
            if(formClassId != null) {
                selector.steps.add(new Step(ResourceId.valueOf(formClassId), ResourceId.valueOf(fieldId)));
            } else {
                selector.steps.add(new Step(ResourceId.valueOf(fieldId)));
            }
        }
        return selector;
    }
}
