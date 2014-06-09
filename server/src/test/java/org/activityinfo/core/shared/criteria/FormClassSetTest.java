package org.activityinfo.core.shared.criteria;

import org.activityinfo.datamodel.shared.Cuid;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class FormClassSetTest {

    @Test
    public void test() {
        CriteriaUnion union = new CriteriaUnion(Arrays.asList(
                new ClassCriteria(Cuid.create("a")),
                new ClassCriteria(Cuid.create("b"))));

        FormClassSet set = FormClassSet.of(union);
        assertTrue(set.isClosed());
        assertThat(set.getElements(), hasSize(2));
    }
}
