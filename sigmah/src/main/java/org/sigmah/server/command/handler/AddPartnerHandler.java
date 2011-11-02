/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.command.handler;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.sigmah.server.database.hibernate.entity.Partner;
import org.sigmah.server.database.hibernate.entity.User;
import org.sigmah.server.database.hibernate.entity.UserDatabase;
import org.sigmah.server.database.hibernate.entity.UserPermission;
import org.sigmah.shared.command.AddPartner;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.exception.CommandException;
import org.sigmah.shared.exception.DuplicateException;
import org.sigmah.shared.exception.IllegalAccessCommandException;

import com.google.inject.Inject;

/**
 * @author Alex Bertram
 * @see org.sigmah.shared.command.AddPartner
 */
public class AddPartnerHandler implements CommandHandler<AddPartner> {

    private final EntityManager em;

    @Inject
    public AddPartnerHandler(EntityManager em) {
        this.em = em;
    }

    public CommandResult execute(AddPartner cmd, User user) throws CommandException {

        UserDatabase db = em.find(UserDatabase.class, cmd.getDatabaseId());
        if (db.getOwner().getId() != user.getId()) {
            UserPermission perm = db.getPermissionByUser(user);
            if (perm == null || !perm.isAllowManageAllUsers()) {
                throw new IllegalAccessCommandException("The user does not have the manageAllUsers permission.");
            }
        }

        // first check to see if an organization by this name is already
        // a partner

        Set<Partner> dbPartners = db.getPartners();
        for (Partner partner : dbPartners) {
            if (partner.getName().equals(cmd.getPartner().getName())) {
                throw new DuplicateException();
            }
        }

        // now try to match this partner by name
        List<Partner> allPartners = em.createQuery("select p from Partner p where p.name = ?1")
                .setParameter(1, cmd.getPartner().getName())
                .getResultList();

        if (allPartners.size() != 0) {
            db.getPartners().add(allPartners.get(0));
            return new CreateResult(allPartners.get(0).getId());
        }

        // nope, have to create a new record
        Partner newPartner = new Partner();
        newPartner.setName(cmd.getPartner().getName());
        newPartner.setFullName(cmd.getPartner().getFullName());
        em.persist(newPartner);
        db.setLastSchemaUpdate(new Date());
        em.persist(db);
        db.getPartners().add(newPartner);

        return new CreateResult(newPartner.getId());
    }
}
