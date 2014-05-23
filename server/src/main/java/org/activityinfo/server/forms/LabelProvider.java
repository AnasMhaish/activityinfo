package org.activityinfo.server.forms;

import com.bedatadriven.rebar.sql.client.query.SqlQuery;
import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;

import javax.persistence.EntityManager;
import java.util.*;

import static org.activityinfo.legacy.shared.adapter.CuidAdapter.*;

public class LabelProvider {

    private final EntityManager entityManager;

    @Inject
    public LabelProvider(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Map<Cuid, String> query(Set<Cuid> instanceIds) {

        Map<Cuid, String> labels = new HashMap<>();

        if(!instanceIds.isEmpty()) {

            Multimap<Character, Integer> ids = HashMultimap.create();
            for(Cuid instanceId : instanceIds) {
                ids.put(instanceId.getDomain(), getLegacyIdFromCuid(instanceId));
            }

            String query = null;

            for(Character domain : ids.keySet()) {
                String domainQuery = SqlQuery.select()
                                      .appendColumn("'" + domain + "'", "domain")
                                      .appendColumn(idColumn(domain), "id")
                                      .appendColumn("name")
                                      .from(tableName(domain))
                                      .whereTrue(idCriteria(domain, ids.get(domain)))
                                      .sql();

                if(query == null) {
                    query = domainQuery;
                } else {
                    query = query + " UNION ALL " + domainQuery;
                }
            }


            List<Object[]> results = entityManager.createNativeQuery(query)
                                               .getResultList();
            for(Object[] result : results) {
                String domain = (String) result[0];
                int id = (int) result[1];
                Cuid instanceId = CuidAdapter.cuid(domain.charAt(0), id);
                String label = (String) result[2];
                labels.put(instanceId, label);
            }
        }
        return labels;
    }

    private String idCriteria(Character domain, Collection<Integer> ids) {
        return idColumn(domain) + " in (" + Joiner.on(", ").join(ids) + ")";
    }

    private String idColumn(char domain) {
        return tableName(domain) + "Id";
    }

    private String tableName(char domain) {
        switch(domain) {
            case LOCATION_DOMAIN:
                return "location";
            case PARTNER_DOMAIN:
                return "partner";
            case PROJECT_DOMAIN:
                return "project";
            case ATTRIBUTE_DOMAIN:
                return "attribute";
        }
        throw new UnsupportedOperationException("domain:" + domain);
    }
}
