package org.activityinfo.api.shared.adapter;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.Cuids;
import org.activityinfo.api2.shared.criteria.*;

import java.util.List;

/**
 * Helper functions for evaluating criteria
 */
public class CriteriaEvaluation {

    /**
     * Partially evaluates a Criteria against a {@code FormClass} id
     *
     * @return a Predicate which returns true if {@code FormInstances} of this class
     * <strong>might</strong> be included in the result.
     */
    public static Predicate<Cuid> evaluatePartiallyOnClassId(Criteria criteria) {
        ClassIdEvaluator evaluator = new ClassIdEvaluator();
        return evaluator.getPredicate();
    }

    private static class ClassIdEvaluator extends CriteriaVisitor {

        private List<Predicate<Cuid>> classPredicates = Lists.newArrayList();
        private boolean hasCriteriaIndependentOfClassId = false;

        @Override
        public void visitInstanceIdCriteria(IdCriteria criteria) {
            hasCriteriaIndependentOfClassId = true;
        }

        @Override
        public void visitFieldCriteria(FieldCriteria criteria) {
            hasCriteriaIndependentOfClassId = true;
        }

        @Override
        public void visitClassCriteria(ClassCriteria criteria) {
            if(criteria.getClassIri().getScheme().equals(Cuids.SCHEME)) {
               classPredicates.add(Predicates.equalTo(
                       new Cuid(criteria.getClassIri().getSchemeSpecificPart())));
            }
        }

        @Override
        public void visitFuzzyFieldCriteria(FuzzyFieldCriteria criteria) {
            hasCriteriaIndependentOfClassId = true;
        }

        @Override
        public void visitIntersection(CriteriaIntersection intersection) {
            ClassIdEvaluator visitor = evaluateSet(intersection);
            if(visitor.hasCriteriaIndependentOfClassId) {
                hasCriteriaIndependentOfClassId = true;
            }
            classPredicates.add(Predicates.and(visitor.classPredicates));
        }

        @Override
        public void visitUnion(CriteriaUnion union) {
            ClassIdEvaluator visitor = evaluateSet(union);
            if(visitor.hasCriteriaIndependentOfClassId) {
                hasCriteriaIndependentOfClassId = true;
            } else {
                classPredicates.add(Predicates.or(visitor.classPredicates));
            }
        }

        private ClassIdEvaluator evaluateSet(Iterable<Criteria> set) {
            ClassIdEvaluator visitor = new ClassIdEvaluator();
            for(Criteria criteria : set) {
                criteria.accept(visitor);
            }
            return visitor;
        }

        public Predicate<Cuid> getPredicate() {
            if(classPredicates.isEmpty()) {
                return Predicates.alwaysTrue();
            } else {
                Preconditions.checkState(classPredicates.size() == 1);
                return classPredicates.get(0);
            }
        }
    }
}
